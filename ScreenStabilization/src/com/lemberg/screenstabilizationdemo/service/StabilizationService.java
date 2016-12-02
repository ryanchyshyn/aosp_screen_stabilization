package com.lemberg.screenstabilizationdemo.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Parcel;
import android.os.ServiceManager;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import com.lemberg.screenstabilizationdemo.settings.AppSettings;
import com.lemberg.screenstabilizationdemo.Constants;
import com.lemberg.screenstabilizationdemo.utils.Utils;

public class StabilizationService extends Service
{
	private static final String TAG = StabilizationService.class.getSimpleName();

	private AppSettings settings;
	private SensorManager sensorManager;
	private Sensor accelerometer;

	private boolean accListenerRegistered = false;

	private final float[] tempAcc = new float[3];
	private final float[] acc = new float[3];
	private final float[] velocity = new float[3];
	private final float[] position = new float[3];
	private long timestamp = 0;

	private int x = 0, y = 0;

	private IBinder flinger = null;

	private final SensorEventListener sensorEventListener = new SensorEventListener()
	{
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy)
		{
		}

		@Override
		public void onSensorChanged(SensorEvent event)
		{
			if (timestamp != 0)
			{
				tempAcc[0] = Utils.rangeValue(event.values[0], -Constants.MAX_ACC, Constants.MAX_ACC);
				tempAcc[1] = Utils.rangeValue(event.values[1], -Constants.MAX_ACC, Constants.MAX_ACC);
				tempAcc[2] = Utils.rangeValue(event.values[2], -Constants.MAX_ACC, Constants.MAX_ACC);

				Utils.lowPassFilter(tempAcc, acc, settings.getLowPassAlpha());

				float dt = (event.timestamp - timestamp) * Constants.NS2S;

				for(int index = 0; index < 3; ++index)
				{
					velocity[index] += acc[index] * dt - settings.getVelocityFriction() * velocity[index];
					velocity[index] = Utils.fixNanOrInfinite(velocity[index]);

					position[index] += velocity[index] * settings.getVelocityAmpl() * dt - settings.getPositionFriction() * position[index];
					position[index] = Utils.rangeValue(position[index], -Constants.MAX_POS_SHIFT, Constants.MAX_POS_SHIFT);
				}
			}
			else
			{
				velocity[0] = velocity[1] = velocity[2] = 0f;
				position[0] = position[1] = position[2] = 0f;

				acc[0] = Utils.rangeValue(event.values[0], -Constants.MAX_ACC, Constants.MAX_ACC);
				acc[1] = Utils.rangeValue(event.values[1], -Constants.MAX_ACC, Constants.MAX_ACC);
				acc[2] = Utils.rangeValue(event.values[2], -Constants.MAX_ACC, Constants.MAX_ACC);
			}

			timestamp = event.timestamp;

			int newPosX = Math.round(position[0]);
			int newPosY = Math.round(position[1]);
			if ((newPosX != x) || (newPosY != y))
			{
				x = newPosX;
				y = newPosY;
				setSurfaceFlingerTranslate(-x, y);
			}
		}
	};

	private final BroadcastReceiver screenOnReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.d(TAG, "Screen is on");
			setSurfaceFlingerTranslate(0, 0);
			reset();
			registerAccListener();
		}
	};

	private final BroadcastReceiver screenOffReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			unregisterAccListener();
			setSurfaceFlingerTranslate(0, 0);
			Log.d(TAG, "Screen is off");
		}
	};

	public StabilizationService()
	{
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		settings = AppSettings.getAppSettings(getApplicationContext());

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

		registerReceiver(screenOnReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
		registerReceiver(screenOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

		if (Utils.isScreenOn(this)) registerAccListener();
	}

	@Override
	public void onDestroy()
	{
		unregisterReceiver(screenOnReceiver);
		unregisterReceiver(screenOffReceiver);

		unregisterAccListener();
		setSurfaceFlingerTranslate(0, 0);

		super.onDestroy();
	}

	private void reset()
	{
		position[0] = position[1] = position[2] = 0;
		velocity[0] = velocity[1] = velocity[2] = 0;
		acc[0] = acc[1] = acc[2] = 0;
		timestamp = 0;
		x = y = 0;
	}

	private void registerAccListener()
	{
		if (accListenerRegistered) return;
		accListenerRegistered = sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		if (!accListenerRegistered)
		{
			Log.wtf(TAG, "Sensor listener not registered");
		}
	}

	private void unregisterAccListener()
	{
		if (accListenerRegistered)
		{
			accListenerRegistered = false;
			sensorManager.unregisterListener(sensorEventListener);
		}
	}

	private void setSurfaceFlingerTranslate(int x, int y)
	{
		try
		{
			if (flinger == null) flinger = ServiceManager.getService("SurfaceFlinger");
			if (flinger == null)
			{
				Log.wtf(TAG, "SurfaceFlinger is null");
				return;
			}

			Parcel data = Parcel.obtain();
			data.writeInterfaceToken("android.ui.ISurfaceComposer");
			data.writeInt(x);
			data.writeInt(y);
			flinger.transact(2020, data, null, 0);
			data.recycle();
		}
		catch(Exception e)
		{
			Log.e(TAG, "SurfaceFlinger error", e);
		}
	}

}

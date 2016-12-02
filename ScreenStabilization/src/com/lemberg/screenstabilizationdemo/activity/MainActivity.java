package com.lemberg.screenstabilizationdemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.lemberg.screenstabilizationdemo.settings.AppSettings;
import com.lemberg.screenstabilizationdemo.Constants;
import com.lemberg.screenstabilizationdemo.view.LineGraphView;
import com.lemberg.screenstabilizationdemo.R;
import com.lemberg.screenstabilizationdemo.service.StabilizationService;
import com.lemberg.screenstabilizationdemo.dialog.SettingsDialog;
import com.lemberg.screenstabilizationdemo.utils.Utils;

public class MainActivity extends Activity
{
	private static final String TAG = MainActivity.class.getSimpleName();

	private View layoutSensor;

	private LineGraphView graph1;
	private LineGraphView graph2;
	private LineGraphView graph3;

	private SensorManager sensorManager;
	private Sensor accelerometer;

	private final float[] tempAcc = new float[3];
	private final float[] acc = new float[3];
	private final float[] velocity = new float[3];
	private final float[] position = new float[3];
	private long timestamp = 0;

	private AppSettings settings;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		settings = AppSettings.getAppSettings(getApplicationContext());

		initViews();

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		boolean ret = sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		if (!ret)
		{
			Log.wtf(TAG, "Sensor listener registration failed");
			Toast.makeText(this, "Sensor listener registration failed", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		sensorManager.unregisterListener(sensorEventListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);

		Switch switchSvc = (Switch) menu.findItem(R.id.action_svc_switch).getActionView().findViewById(R.id.switch_svc);
		switchSvc.setChecked(settings.isServiceEnabled());

		switchSvc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				setSvcEnabled(isChecked);
			}
		});
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_settings:
				showSettings();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initViews()
	{
		View layoutRoot = findViewById(R.id.layout_root);
		layoutRoot.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				reset();
			}
		});

		layoutSensor = findViewById(R.id.layout_sensor);

		graph1 = (LineGraphView) findViewById(R.id.graph1);
		graph2 = (LineGraphView) findViewById(R.id.graph2);
		graph3 = (LineGraphView) findViewById(R.id.graph3);

		graph1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				graph1.clear();
			}
		});

		graph2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				graph2.clear();
			}
		});

		graph3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				graph3.clear();
			}
		});
	}

	private void showSettings()
	{
		SettingsDialog dlg = SettingsDialog.newInstance();
		dlg.show(getFragmentManager(), "settings");
	}

	private void setSvcEnabled(boolean enabled)
	{
		if (enabled) startService(new Intent(this, StabilizationService.class));
		else stopService(new Intent(this, StabilizationService.class));

		settings.setServiceEnabled(enabled);
		settings.saveDeferred();
	}

	private void reset()
	{
		position[0] = position[1] = position[2] = 0;
		velocity[0] = velocity[1] = velocity[2] = 0;
		acc[0] = acc[1] = acc[2] = 0;
		timestamp = 0;

		layoutSensor.setTranslationX(0);
		layoutSensor.setTranslationY(0);
	}

	private final SensorEventListener sensorEventListener = new SensorEventListener()
	{
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}

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

			layoutSensor.setTranslationX(-position[0]);
			layoutSensor.setTranslationY(position[1]);

			graph1.setValue(acc[0]);
			graph2.setValue(acc[1]);
			graph3.setValue(acc[2]);
		}
	};
}

package com.lemberg.screenstabilizationdemo.activity;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.lemberg.screenstabilizationdemo.settings.AppSettings;
import com.lemberg.screenstabilizationdemo.Constants;
import com.lemberg.screenstabilizationdemo.view.LineGraphView;
import com.lemberg.screenstabilizationdemo.R;
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

	private void reset()
	{
		position[0] = position[1] = position[2] = 0;
		velocity[0] = velocity[1] = velocity[2] = 0;
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
				float dt = (event.timestamp - timestamp) * Constants.NS2S;

				for(int index = 0; index < 3; ++index)
				{
					velocity[index] += event.values[index] * dt;
					position[index] += velocity[index] * dt * 10000;
				}
			}
			else
			{
				velocity[0] = velocity[1] = velocity[2] = 0f;
				position[0] = position[1] = position[2] = 0f;
			}

			timestamp = event.timestamp;

			layoutSensor.setTranslationX(-position[0]);
			layoutSensor.setTranslationY(position[1]);

			graph1.setValue(event.values[0]);
			graph2.setValue(event.values[1]);
			graph3.setValue(event.values[2]);
		}
	};
}

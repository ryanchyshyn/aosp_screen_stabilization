package com.lemberg.screenstabilizationdemo.settings;

import android.content.SharedPreferences;

public class Settings
{
	private static final String SERVICE_ENABLED_KEY = "service_enabled";
	private static final String VELOCITY_FRICTION_KEY = "velocity_friction";
	private static final String POSITION_FRICTION_KEY = "position_friction";
	private static final String LOW_PASS_ALPHA_KEY = "low_pass_alpha";
	private static final String VELOCITY_AMPL_KEY = "velocity_ampl";

	private static final boolean SVC_ENABLED_DEFAULT = false;
	private static final float VELOCITY_FRICTION_DEFAULT = 0.2f;
	private static final float POSITION_FRICTION_DEFAULT = 0.1f;
	private static final float LOW_PASS_ALPHA_DEFAULT = 0.85f;
	private static final int VELOCITY_AMPL_DEFAULT = 10000;

	private boolean serviceEnabled = SVC_ENABLED_DEFAULT;
	private float velocityFriction = VELOCITY_FRICTION_DEFAULT;
	private float positionFriction = POSITION_FRICTION_DEFAULT;
	private float lowPassAlpha = LOW_PASS_ALPHA_DEFAULT;
	private int velocityAmpl = VELOCITY_AMPL_DEFAULT;

	public boolean isServiceEnabled()
	{
		return serviceEnabled;
	}

	public void setServiceEnabled(boolean serviceEnabled)
	{
		this.serviceEnabled = serviceEnabled;
	}

	public float getVelocityFriction()
	{
		return velocityFriction;
	}

	public void setVelocityFriction(float velocityFriction)
	{
		this.velocityFriction = velocityFriction;
	}

	public float getPositionFriction()
	{
		return positionFriction;
	}

	public void setPositionFriction(float positionFriction)
	{
		this.positionFriction = positionFriction;
	}

	public float getLowPassAlpha()
	{
		return lowPassAlpha;
	}

	public void setLowPassAlpha(float lowPassAlpha)
	{
		this.lowPassAlpha = lowPassAlpha;
	}

	public int getVelocityAmpl()
	{
		return velocityAmpl;
	}

	public void setVelocityAmpl(int velocityAmpl)
	{
		this.velocityAmpl = velocityAmpl;
	}

	public void load(SharedPreferences prefs)
	{
		serviceEnabled = prefs.getBoolean(SERVICE_ENABLED_KEY, SVC_ENABLED_DEFAULT);
		velocityFriction = prefs.getFloat(VELOCITY_FRICTION_KEY, VELOCITY_FRICTION_DEFAULT);
		positionFriction = prefs.getFloat(POSITION_FRICTION_KEY, POSITION_FRICTION_DEFAULT);
		lowPassAlpha = prefs.getFloat(LOW_PASS_ALPHA_KEY, LOW_PASS_ALPHA_DEFAULT);
		velocityAmpl = prefs.getInt(VELOCITY_AMPL_KEY, VELOCITY_AMPL_DEFAULT);
	}

	public void save(SharedPreferences prefs)
	{
		SharedPreferences.Editor editor = prefs.edit();
		save(editor);
		editor.commit();
	}

	public void saveDeferred(SharedPreferences prefs)
	{
		SharedPreferences.Editor editor = prefs.edit();
		save(editor);
		editor.apply();
	}

	public void save(SharedPreferences.Editor editor)
	{
		editor.putBoolean(SERVICE_ENABLED_KEY, serviceEnabled);
		editor.putFloat(VELOCITY_FRICTION_KEY, velocityFriction);
		editor.putFloat(POSITION_FRICTION_KEY, positionFriction);
		editor.putFloat(LOW_PASS_ALPHA_KEY, lowPassAlpha);
		editor.putInt(VELOCITY_AMPL_KEY, velocityAmpl);
	}
}

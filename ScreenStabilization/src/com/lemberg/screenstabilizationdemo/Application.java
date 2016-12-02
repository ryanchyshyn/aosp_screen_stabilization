package com.lemberg.screenstabilizationdemo;

import android.preference.PreferenceManager;

import com.lemberg.screenstabilizationdemo.settings.AppSettings;

public class Application extends android.app.Application
{
	private AppSettings settings;

	public AppSettings getSettings()
	{
		return settings;
	}

	@Override
	public void onCreate()
	{
		settings = new AppSettings(PreferenceManager.getDefaultSharedPreferences(this));
		settings.load();

		super.onCreate();
	}
}

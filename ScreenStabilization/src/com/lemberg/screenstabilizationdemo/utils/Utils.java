package com.lemberg.screenstabilizationdemo.utils;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.view.Display;

public class Utils
{
	public static boolean isScreenOn(Context context)
	{
		DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
		for (Display display : dm.getDisplays())
		{
			if (display.getState() != Display.STATE_OFF)
				return true;
		}
		return false;
	}

	public static void lowPassFilter(float[] input, float[] output, float alpha)
	{
		for (int i = 0; i < input.length; i++)
			output[i] = output[i] + alpha * (input[i] - output[i]);
	}

	public static float rangeValue(float value, float min, float max)
	{
		if (value > max) return max;
		if (value < min) return min;
		return value;
	}

	public static float fixNanOrInfinite(float value)
	{
		if (Float.isNaN(value) || Float.isInfinite(value)) return 0;
		return value;
	}
}

package com.lemberg.screenstabilizationdemo.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lemberg.screenstabilizationdemo.R;
import com.lemberg.screenstabilizationdemo.settings.AppSettings;

public class SettingsDialog extends DialogFragment
{
	public static SettingsDialog newInstance()
	{
		SettingsDialog ret = new SettingsDialog();
		return ret;
	}

	private static final float SCALE = 1000f;

	private AppSettings settings;

	private SeekBar seekbarVelFriction;
	private TextView txtVelFrictionSummary;

	private SeekBar seekbarPosFriction;
	private TextView txtPosFrictionSummary;

	private SeekBar seekbarLowPassAlpha;
	private TextView txtLowPassAlphaSummary;

	private SeekBar seekbarVelocityAmpl;
	private TextView txtVelocityAmplSummary;

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		settings = AppSettings.getAppSettings(activity.getApplicationContext());
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i)
				{
					settings.saveDeferred();
				}
			});

		LayoutInflater inflater = getActivity().getLayoutInflater();

		View view = inflater.inflate(R.layout.settings_dialog, null);
		initViews(view);
		fillViews();

		builder.setView(view);

		return builder.create();
	}

	private void initViews(View root)
	{
		seekbarVelFriction = (SeekBar) root.findViewById(R.id.seekbar_vel_friction);
		txtVelFrictionSummary = (TextView) root.findViewById(R.id.txt_vel_friction_summary);

		seekbarPosFriction = (SeekBar) root.findViewById(R.id.seekbar_pos_friction);
		txtPosFrictionSummary = (TextView) root.findViewById(R.id.txt_pos_friction_summary);

		seekbarLowPassAlpha = (SeekBar) root.findViewById(R.id.seekbar_low_pass_alpha);
		txtLowPassAlphaSummary = (TextView) root.findViewById(R.id.txt_low_pass_alpha_summary);

		seekbarVelocityAmpl = (SeekBar) root.findViewById(R.id.seekbar_velocity_ampl);
		txtVelocityAmplSummary = (TextView) root.findViewById(R.id.txt_velocity_ampl_summary);

		seekbarVelFriction.setOnSeekBarChangeListener(sliderChangeListener);
		seekbarVelFriction.setMax((int) SCALE);

		seekbarPosFriction.setOnSeekBarChangeListener(sliderChangeListener);
		seekbarPosFriction.setMax((int) SCALE);

		seekbarLowPassAlpha.setOnSeekBarChangeListener(sliderChangeListener);
		seekbarLowPassAlpha.setMax((int) SCALE);

		seekbarVelocityAmpl.setOnSeekBarChangeListener(sliderChangeListener);
		seekbarVelocityAmpl.setMax(10000); // 5000 .. 15000
	}

	private void fillViews()
	{
		seekbarVelFriction.setProgress((int) (settings.getVelocityFriction() * SCALE));
		seekbarPosFriction.setProgress((int) (settings.getPositionFriction() * SCALE));
		seekbarLowPassAlpha.setProgress((int) (settings.getLowPassAlpha() * SCALE));
		seekbarVelocityAmpl.setProgress(settings.getVelocityAmpl() - 5000);
	}

	private final SeekBar.OnSeekBarChangeListener sliderChangeListener = new SeekBar.OnSeekBarChangeListener()
	{
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
		{
			if (seekBar.getId() == R.id.seekbar_vel_friction)
			{
				if (fromUser) settings.setVelocityFriction(progress / SCALE);
				txtVelFrictionSummary.setText(String.format("%.2f", settings.getVelocityFriction()));
			}
			else if (seekBar.getId() == R.id.seekbar_pos_friction)
			{
				if (fromUser) settings.setPositionFriction(progress / SCALE);
				txtPosFrictionSummary.setText(String.format("%.2f", settings.getPositionFriction()));
			}
			else if (seekBar.getId() == R.id.seekbar_low_pass_alpha)
			{
				if (fromUser) settings.setLowPassAlpha(progress / SCALE);
				txtLowPassAlphaSummary.setText(String.format("%.2f", settings.getLowPassAlpha()));
			}
			else if (seekBar.getId() == R.id.seekbar_velocity_ampl)
			{
				if (fromUser) settings.setVelocityAmpl(progress + 5000);
				txtVelocityAmplSummary.setText(String.format("%d", settings.getVelocityAmpl()));
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
	};


}

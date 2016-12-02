package com.lemberg.screenstabilizationdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class LineGraphView extends View
{
	private static final int UPDATE_DELAY = 16; // 60 FPS
	private static final int TEXT_SIZE = 10;

	private final Handler handler = new Handler();

	private final Paint lp = new Paint();

	private float[] data;
	private int framePos = 0;
	private int width = 0;
	private float maxAbsValue = 0;
	private float currValue = 0;

	private int backgroundColor = 0xFF202020;
	private int borderColor = 0xFFFFFFFF;
	private int axisColor = 0xFF708090;
	private int dataColor = 0xFFFF8090;

	private boolean isAttached = false;

	private int stringHeight;
	private int stringWidth;

	private final Runnable updateRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			// shift data
			framePos++;
			if (framePos == width - 1)
			{
				framePos = 0;
				// shift data
				System.arraycopy(data, (width - 1) * 4, data, 0, data.length / 2);
				// fill X
				fillX();
			}
			else
			{
				data[(framePos + width - 1) * 4 + 1] = data[(framePos - 1 + width - 1) * 4 + 3];
				data[(framePos + width - 1) * 4 + 3] = currValue;
			}

			invalidate();
			if (isAttached) handler.postDelayed(updateRunnable, UPDATE_DELAY);
		}
	};

	public LineGraphView(Context context)
	{
		super(context);
		lp.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE, getResources().getDisplayMetrics()));
	}

	public LineGraphView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		lp.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE, getResources().getDisplayMetrics()));
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		framePos = 0;
		data = new float[(w * 2 - 1) * 4];
		clearData();
	}

	@Override
	protected void onAttachedToWindow()
	{
		super.onAttachedToWindow();
		isAttached = true;
		handler.postDelayed(updateRunnable, UPDATE_DELAY);
	}

	@Override
	protected void onDetachedFromWindow()
	{
		isAttached = false;
		handler.removeCallbacks(updateRunnable);
		super.onDetachedFromWindow();
	}

	private void clearData()
	{
		for (int x = 0; x < data.length / 4; ++x)
		{
			data[x * 4] = x;
			data[x * 4 + 1] = 0;
			data[x * 4 + 2] = x + 1;
			data[x * 4 + 3] = 0;
		}
	}

	public void setValue(float value)
	{
		currValue = value;
		setAbsValue(value);
	}

	private void setAbsValue(float value)
	{
		if (Math.abs(value) > Math.abs(maxAbsValue)) maxAbsValue = value;
	}

	public void clear()
	{
		if (data == null) return;
		clearData();
		maxAbsValue = currValue = 0;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		int width = getWidth();
		int height = getHeight();

		if (width != this.width) return;

		canvas.drawColor(backgroundColor);

		canvas.save();
		canvas.translate(0, height / 2 - 1);
		canvas.scale(1, 10);
		{
			// axis
			lp.setColor(axisColor);
			canvas.drawLine(0, 0, width - 1, 0, lp);

			// border
			//lp.setColor(borderColor);
			//canvas.drawLine(0, 0, width - 1, 0, lp);
			//canvas.drawLine(width, 0, width - 1, height - 1, lp);
			//canvas.drawLine(width, height - 1, 0, height - 1, lp);
			//canvas.drawLine(0, height - 1, 0, 0, lp);

			// data
			if (data != null)
			{
				lp.setColor(dataColor);
				canvas.translate(-framePos, 0);
				canvas.drawLines(data, framePos * 4, (width - 1) * 4, lp);
			}
		}
		canvas.restore();

		// text
		lp.setColor(axisColor);
		if (stringHeight == 0) stringHeight = Math.round(Math.abs(lp.ascent()) + lp.descent() + 0.5f);
		if (stringWidth == 0) stringWidth = Math.round(lp.measureText("88.88") + 0.5f);
		String str = String.format("%.1f", maxAbsValue);
		canvas.drawText(str, width - 2 - stringWidth, 1 + stringHeight, lp);

		str = String.format("%.1f", currValue);
		canvas.drawText(str, width - 2 - stringWidth, height - lp.descent(), lp);
	}

	private void fillX()
	{
		for (int x = 0; x < data.length / 4; ++x)
		{
			data[x * 4] = x;
			data[x * 4 + 2] = x + 1;
		}
	}
}

package com.mountainreacher.motionalarm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class TimerView extends View {

	private Context context;
	private int time;
	private boolean alarmOn;
	private int centroX, centroY;
	private Paint paint;

	public TimerView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.context = context;
		this.centroX = getWidth()/2;
		this.centroY = getHeight()/2;
		this.paint = new Paint();
	}

	public void stopTimer() {
		this.alarmOn = false;
	}

	public void setTime(int time) {
		this.alarmOn = true;
		this.time = time + 1000;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if(alarmOn) {
			int size = (1000-(time%1000))/20;
			paint.setTextSize(size*4+50);
//			paint.setColor(size);
			paint.setAlpha((200-size*4)+55);
			int clock = time/1000;
			canvas.drawText(clock+"", getWidth()/2-(clock >= 10 ? size*2 : size), getHeight()/2+size, paint);
		}
	}

}

package com.mountainreacher.motionalarm;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {

	private final int SPLASH_DISPLAY_LENGHT = 1000;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.activity_splash);

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Intent mainIntent = new Intent().setClass(SplashActivity.this,
						ControlActivity.class);
				startActivity(mainIntent);
				finish();
			}
		};

		Timer timer = new Timer();
		timer.schedule(task, SPLASH_DISPLAY_LENGHT);
	}

}

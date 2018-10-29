package com.mountainreacher.motionalarm;

import com.google.android.gms.ads.*;
import com.mountainreacher.motionalarm.R;
import com.mountainreacher.motionalarm.alarmservice.Alarm;
import com.mountainreacher.motionalarm.events.MenuEvents;
import com.mountainreacher.motionalarm.events.NotificationEvents;
import com.mountainreacher.motionalarm.motionservice.MotionSensorService;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import net.technologichron.android.control.NumberPicker;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class ControlActivity extends Activity implements OnClickListener {

	// Constantes
	private final String AD_UNIT_ID = "ca-app-pub-0360613478597349/4735746323";
	private final String AD_UNIT_ID_INTERSTITIAL = "ca-app-pub-0360613478597349/3658506320";
	private final int MIN_START_TIME = 3;
	private final int MAX_START_TIME = 15;
	private final int DEF_START_TIME = 5;

	// Notification
	private NotificationEvents notifications;

	// Time to start alarm
	private int timerAlarm;
	private TimerView timerAnimation;

	// Alarma prendida
	private boolean alarmOn;

	// Visual objects
	private Button startButton, calibrateButton, alarmSelectionButton;
	private TextView sensibilityText;
	private NumberPicker numberPicker;
	private SeekBar sensibility;
	
	// Ads
	private AdView adView;
	private InterstitialAd interstitial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.control_activity);

		MobileAds.initialize(this, "ca-app-pub-0360613478597349~3259013121");
		initBanner();
		init();
		notifications = new NotificationEvents(this);

	}

	private void initBanner() {

		// Create the adView.
		adView = new AdView(this);
		adView.setAdUnitId(AD_UNIT_ID);
		adView.setAdSize(AdSize.SMART_BANNER);

		// Lookup your LinearLayout assuming it's been given
		LinearLayout layout = (LinearLayout)findViewById(R.id.MainLayout);

		// Add the adView to it.
		layout.addView(adView, 0);

		// Initiate a generic request.
		AdRequest adRequest = new AdRequest.Builder().build();

		// Load the adView with the ad request.
		adView.loadAd(adRequest);

		// Create the interstitial
		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId(AD_UNIT_ID_INTERSTITIAL);

		// Create ad request
		AdRequest adRequest2 = new AdRequest.Builder().build();

		// Begin loading your interstitial
		interstitial.loadAd(adRequest2);

	}

	private void init() {

		// Start button
		startButton = (Button)findViewById(R.id.startButton);
		startButton.setOnClickListener(this);

		// Calibrate button
		calibrateButton = (Button)findViewById(R.id.calibrateButton);
		calibrateButton.setOnClickListener(this);

		// Alarm selection button
		alarmSelectionButton = (Button)findViewById(R.id.alarmSelectionButton);
		alarmSelectionButton.setOnClickListener(this);

		// Text views
		sensibilityText = (TextView)findViewById(R.id.sensibilityText);

		// Number picker
		numberPicker = (NumberPicker)findViewById(R.id.numberPicker);
		numberPicker.setOnClickListener(this);
		numberPicker.setMinValue(MIN_START_TIME);
		numberPicker.setMaxValue(MAX_START_TIME);
		timerAlarm = DEF_START_TIME;
		numberPicker.setValue(DEF_START_TIME);

		// Sencibility
		sensibility = (SeekBar)findViewById(R.id.sensibilityBar);
		sensibility.setOnClickListener(this);
		sensibility.setProgress(70);

		// Timer animation
		timerAnimation = (TimerView)findViewById(R.id.timerAnimation);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.control_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return MenuEvents.menuPressed(this, item);
	}

	public void startAlarmServiceIn(final int seconds) {
		new Thread(new Runnable() {
			public void run() {

				int timeToAlarm = seconds*1000;
				while(timeToAlarm > 0 && alarmOn) {
					try {Thread.sleep(100);} catch (Exception e) {}
					timeToAlarm -= 100;
					timerAnimation.setTime(timeToAlarm);
					timerAnimation.postInvalidate();
				}
				timerAnimation.stopTimer();
				timerAnimation.postInvalidate();
				if(alarmOn) {
					//					alarmSet = true;
					Intent serviceIntent = new Intent(ControlActivity.this, MotionSensorService.class);
					serviceIntent.putExtra(MotionSensorService.ACTION_COMMAND, MotionSensorService.SET_ALARM);
					startService(serviceIntent);
				}
			}
		}).start();
	}

	public void calibrateSensors() {

		Intent serviceIntent = new Intent(ControlActivity.this, MotionSensorService.class);
		serviceIntent.putExtra(MotionSensorService.ACTION_COMMAND, MotionSensorService.CALIBRATE_SENSORS);
		startService(serviceIntent);

		final ProgressDialog loadingdialog = ProgressDialog.show(ControlActivity.this,
				getResources().getString(R.string.calibrate_title), getResources().getString(R.string.calibrate_text), true);
		new Thread() {
			public void run() {
				try {sleep(500);} catch(Exception e) {}
				while(MotionSensorService.isCalibrating()) {
					try {sleep(150);} catch(Exception e) {}
				}
				try {sleep(1000 - 20*MotionSensorService.getCalibrationValue());} catch(Exception e) {}
				loadingdialog.dismiss();
			}
		}.start();

		// Escuchando cuando termine la calibrada para notificarlo al usuario
		loadingdialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				sensibilityText.setText("Sensibility: Adjusted to "+((int)Math.abs(MotionSensorService.getCalibrationValue()))+"% "+(MotionSensorService.getCalibrationValue() < 0 ? "more" : "less")+" sensitive.");
				Toast.makeText(getApplicationContext(), "Adjusted to "+((int)Math.abs(MotionSensorService.getCalibrationValue()))+"% "+(MotionSensorService.getCalibrationValue() < 0 ? "more" : "less")+" sensitive.", Toast.LENGTH_SHORT).show();
			}
		});
	}

	// **************************************** TOUCH-EVENTS ****************************************

	@Override
	public void onClick(View view) {

		switch(view.getId()) {

		case R.id.startButton:
			alarmOn = !alarmOn;
			if(alarmOn) {

				Intent serviceIntent = new Intent(this, MotionSensorService.class);
				serviceIntent.putExtra(MotionSensorService.ACTION_COMMAND, MotionSensorService.RESUME_SENSORS);
				serviceIntent.putExtra(MotionSensorService.SENSIBILITY, sensibility.getProgress());
				startService(serviceIntent);

				timerAlarm = numberPicker.getValue();
				startButton.setText(R.string.turn_off_alarm);
				Toast.makeText(getApplicationContext(), R.string.alarm_on, Toast.LENGTH_SHORT).show();
				startAlarmServiceIn(timerAlarm);
			} else {

				Intent serviceIntent = new Intent(this, MotionSensorService.class);
				serviceIntent.putExtra(MotionSensorService.ACTION_COMMAND, MotionSensorService.PAUSE_SENSORS);
				startService(serviceIntent);

				startButton.setText(R.string.start_alarm_in);
				Toast.makeText(getApplicationContext(), R.string.alarm_off, Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.calibrateButton:
			if(!alarmOn) {
				calibrateSensors();
			}
			break;

		case R.id.alarmSelectionButton:
			if(!alarmOn) {
				Intent intent = new Intent(this, SelectSoundActivity.class);
				startActivityForResult(intent, 1);
			}
			break;
		}
	}

	public void displayInterstitial() {
		if (interstitial != null && interstitial.isLoaded()) {
			interstitial.show();
		}
	}

	// **************************************** TOUCH-EVENTS ****************************************

	// ***************************************** OTHER-EVENTS ***************************************

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode != -1) {
			Intent serviceIntent = new Intent(ControlActivity.this, MotionSensorService.class);
			serviceIntent.putExtra(MotionSensorService.ACTION_COMMAND, MotionSensorService.CHANGE_ALARM);
			serviceIntent.putExtra(MotionSensorService.ALARM_CODE, resultCode);
			startService(serviceIntent);
			Toast.makeText(this, getResources().getString(R.string.selected_sound)+" "+Alarm.SOUND_NAMES[resultCode], Toast.LENGTH_SHORT).show();
		}
	}

	// ***************************************** OTHER-EVENTS ***************************************

	// **************************************** APP LIFE CYCLE ***************************************

	@Override
	public void onBackPressed() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(getResources().getString(R.string.do_u_want_exit));
		// Set an EditText view to get user input

		alert.setPositiveButton(getResources().getString(R.string.exit), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				stopAll();
				displayInterstitial();
				finish();
			}
		});

		alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {}
		});
		alert.show();
	}

	@Override
	public void onPause() {
		adView.pause();
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		adView.resume();
	}

	@Override
	public void onDestroy() {
		stopAll();
		super.onDestroy();
	}
	
	private void stopAll() {
		Intent serviceIntent = new Intent(ControlActivity.this, MotionSensorService.class);
		stopService(serviceIntent);
		adView.destroy();
		notifications.dismissAll();
	}

	// **************************************** APP LIFE CYCLE ***************************************
}

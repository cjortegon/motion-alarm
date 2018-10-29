package com.mountainreacher.motionalarm.motionservice;

import com.mountainreacher.motionalarm.alarmservice.Alarm;
import com.mountainreacher.motionalarm.motionservice.MotionSensor.MotionEvent;
import com.mountainreacher.motionalarm.motionservice.MotionSensor.OnMotionDetectionListener;

import android.app.Service;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class MotionSensorService extends Service implements OnMotionDetectionListener {

	/**
	 * Constants
	 */
	public static final int EXIT = 0;
	public static final int RESUME_SENSORS = 1;
	public static final int PAUSE_SENSORS = 2;
	public static final int SET_ALARM = 3;
	public static final int CHANGE_ALARM = 4;
	public static final int CALIBRATE_SENSORS = 5;
	public static final String ACTION_COMMAND = "ACTION_COMMAND";
	public static final String SENSIBILITY = "SENSIBILITY";
	public static final String ALARM_CODE = "ALARM_CODE";

	/**
	 * Sensor
	 */
	private static MotionSensor sensor;

	/**
	 * Alarm
	 */
	private Alarm alarm;
	private boolean alarmSet;

	/**
	 * Static parameters
	 */
	private static boolean calibrating;
	public static boolean isCalibrating() {return calibrating;}

	public void pauseAlarm() {
		alarmSet = false;
		alarm.autoPause();
	}

	private void calibrateSensors() {

		if(!calibrating) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					calibrating = true;
					Log.i("servicio", "Calibrando los sensores...");
					sensor.calibrateSensors();
					calibrating = false;
					Log.i("servicio", "Termine de calibrar los sensores...");
				}
			}).start();
		}
	}
	
	public static int getCalibrationValue() {
		if(sensor == null)
			return 0;
		else
			return sensor.getCalibrationValue();
	}

	// ************************************* SERVICE METHODS *************************************

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Bundle b = intent.getExtras();

		int command = b.getInt(ACTION_COMMAND);
		switch (command) {
		case RESUME_SENSORS:
			sensor.setSencibility(b.getInt(SENSIBILITY));
			sensor.resumeSensors(SensorManager.SENSOR_DELAY_NORMAL);
			break;

		case PAUSE_SENSORS:
			pauseAlarm();
			sensor.pauseSensors();
			break;

		case SET_ALARM:
			alarmSet = true;
			break;

		case CHANGE_ALARM:
			alarm.selectAlarm(b.getInt(ALARM_CODE));
			break;

		case CALIBRATE_SENSORS:
			calibrateSensors();
			break;
		}

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.i("servicio","onCreate()");

		// Sensor
		sensor = new MotionSensor(this);
		sensor.addOnMotionDetectionListener(this);

		// Sound service
		alarm = new Alarm(this);

		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.i("servicio","onDestroy()");

		pauseAlarm();
		sensor.destroy();
		alarm.destroy();
		alarm = null;
		sensor = null;

		super.onDestroy();
	}

	// ************************************* SERVICE METHODS *************************************

	// ************************************** MOTION EVENTS **************************************

	@Override
	public void onMotionDectection(MotionEvent sensor) {
		if(sensor.moving && alarmSet)
			alarm.playAlarm(true);
		else
			alarm.autoPause();
	}

	// ************************************** MOTION EVENTS **************************************

}

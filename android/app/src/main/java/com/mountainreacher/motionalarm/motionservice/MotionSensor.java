package com.mountainreacher.motionalarm.motionservice;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MotionSensor implements SensorEventListener {

	// Constantes
	private final float ALPHA = 0.75f;

	// Sensores
	private SensorManager sensorManager;
	private Sensor sensorAccelerometer;
	private Sensor sensorGiroscope;
	private Sensor sensorMagneticField;
	private int accuracy, calibrationValue;
	private int moveLog;

	// Values
	private double xProm, yProm, zProm;
	private float mLastGiroX, mLastGiroY, mLastGiroZ;
	private float[] valuesAccelerometer;
	public float[] valuesMagneticField;

	private List<OnMotionDetectionListener> listeners = new ArrayList<OnMotionDetectionListener>();

	public MotionSensor(Context context) {

		// Sensor initialization
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorGiroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		// Variables initialization
		valuesAccelerometer = new float[3];
		valuesMagneticField = new float[3];
	}

	public void addOnMotionDetectionListener(OnMotionDetectionListener listener) {
		this.listeners.add(listener);
	}

	public void removeOnMotionDetectionListener(OnMotionDetectionListener listener) {
		this.listeners.remove(listener);
	}

	public void resumeSensors(int sensorDelay) {
		sensorManager.registerListener(this, sensorAccelerometer, sensorDelay);
		sensorManager.registerListener(this, sensorGiroscope, sensorDelay);
		sensorManager.registerListener(this, sensorMagneticField, sensorDelay);
	}

	public void pauseSensors() {
		sensorManager.unregisterListener(this);
	}

	public void destroy() {
		pauseSensors();
		sensorManager = null;
		sensorAccelerometer = null;
		sensorGiroscope = null;
		sensorMagneticField = null;
		valuesAccelerometer = null;
		valuesMagneticField = null;
	}

	public void calibrateSensors() {
		calibrateSensors(100, 10); // 100 as default time to wait for each calibration and 10 as default start acurracy
	}

	private void calibrateSensors(final int timeToWaitForEachCalibration, final int startAccuracy) {
		resumeSensors(SensorManager.SENSOR_DELAY_FASTEST);
		if(startAccuracy == -1)
			accuracy = calibrationValue + 15;
		else
			accuracy = startAccuracy;
		while(accuracy < 70) {
			moveLog = 0;
			try {Thread.sleep(timeToWaitForEachCalibration);} catch(Exception e) {}
			if(moveLog == 0)
				break;
			else if(moveLog < 5)
				accuracy ++;
			else
				accuracy += 2;
		}
		pauseSensors();
		accuracy += 5;
		calibrationValue = accuracy - 20;
		moveLog = 0;
	}

	/**
	 * Use this method to establish how sensitive the motion sensors must be, this value is independent to the calibration value witch is automatically set.
	 * @param accuracy must be a value between 0 and 100 being 0 the less sensitive value and 100 the most sensitive value.
	 */
	public void setSencibility(int accuracy) {
		this.accuracy = 120 + calibrationValue - accuracy;
	}

	public int getCalibrationValue() {
		return calibrationValue;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {

		switch (event.sensor.getType()) {

		case Sensor.TYPE_ACCELEROMETER:
			sensorTypeAcelerometer(event.values);
			break;

		case Sensor.TYPE_GYROSCOPE:
			sensorTypeGiroscope(event.values);
			break;

		case Sensor.TYPE_MAGNETIC_FIELD:
			sensorTypeMagneticField(event.values);
			break;

		}

		moveLogHandle();
	}

	private void moveLogHandle() {

		// Limites de moveLog
		if(moveLog < 0)
			moveLog = 0;
		else if(moveLog > 10)
			moveLog = 10;

		// Calling listeners
		if(moveLog != 1)
			callListeners();
	}

	private void callListeners() {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).onMotionDectection(new MotionEvent(moveLog > 1, moveLog));
		}
	}

	private void sensorTypeAcelerometer(float values[]) {

		int xMove = (int) (Math.abs(values[0] - valuesAccelerometer[0])*100);
		int yMove = (int) (Math.abs(values[1] - valuesAccelerometer[1])*100);
		int zMove = (int) (Math.abs(values[2] - valuesAccelerometer[2])*100);

		xProm = xProm*(1-ALPHA)+xMove*ALPHA;
		yProm = yProm*(1-ALPHA)+yMove*ALPHA;
		zProm = zProm*(1-ALPHA)+zMove*ALPHA;

		if(xProm > accuracy || yProm > accuracy || zProm > accuracy) {
			if(xProm + yProm + zProm > accuracy*3)
				moveLog += 2;
			else if(moveLog > 1)
				moveLog += 2;
			else
				moveLog ++;
		} else {
			moveLog --;
		}

		valuesAccelerometer[0] = values[0];
		valuesAccelerometer[1] = values[1];
		valuesAccelerometer[2] = values[2];

	}

	private void sensorTypeGiroscope(float values[]) {

		this.mLastGiroX = mLastGiroX*(1-ALPHA) + Math.abs(values[0])*200*ALPHA;
		this.mLastGiroY = mLastGiroY*(1-ALPHA) + Math.abs(values[1])*200*ALPHA;
		this.mLastGiroZ = mLastGiroZ*(1-ALPHA) + Math.abs(values[2])*200*ALPHA;

		if(mLastGiroX > accuracy || mLastGiroY > accuracy || mLastGiroZ > accuracy) {
			moveLog += 2;
		}

	}

	private void sensorTypeMagneticField(float values[]) {

		float newX = values[0]*200;
		float newY = values[1]*200;
		float newZ = values[2]*200;

		if(Math.abs(newX - valuesMagneticField[0]) > accuracy ||
				Math.abs(newY - valuesMagneticField[1]) > accuracy ||
				Math.abs(newZ - valuesMagneticField[2]) > accuracy) {
			moveLog ++;
		}

		valuesMagneticField[0] = newX;
		valuesMagneticField[1] = newY;
		valuesMagneticField[2] = newZ;

	}

	// ************************************** EXTRA CLASSES **************************************

	public interface OnMotionDetectionListener {
		public void onMotionDectection(MotionEvent sensor);
	}

	public class MotionEvent {

		public boolean moving;
		public int movingValue;

		public MotionEvent(boolean moving, int movingValue) {
			this.moving = moving;
			this.movingValue = movingValue;
		}

	}

	// ************************************** EXTRA CLASSES **************************************

}

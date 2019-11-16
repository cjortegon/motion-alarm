package com.mountainreacher.motionalarm.alarmservice;

import com.mountainreacher.motionalarm.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Parcel;
import android.os.Parcelable;

public class Alarm implements Parcelable {

	// Nombres de sonidos
	public static final String SOUND_NAMES[] = {"Buzzer", "Siren", "Pitched", "Warp", "Car Clasic", "Car Increasing"};
	public static final int SOUND_IDS[] = {R.raw.buzzer, R.raw.siren, R.raw.pitched, R.raw.warp, R.raw.car1, R.raw.car2};
//	private static final int SOUND_DURATION[] = {1000, 1000, 1000, 1000, 400};
	private final float VOLUME = 1f;

	private SoundPool soundPool;
	private int[] soundIDs;
	private int selected;
	private long lastPlayedAlarm;
	private int numberLoadedSounds;
	private boolean playingAlarm;

	public Alarm(Context contex) {

		soundPool = new SoundPool(10, AudioManager.STREAM_ALARM, 0);
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				numberLoadedSounds ++;
			}
		});

		soundIDs = new int[SOUND_IDS.length];
		for (int i = 0; i < SOUND_IDS.length; i++) {
			soundIDs[i] = soundPool.load(contex, SOUND_IDS[i], 1);
		}

	}

	public boolean selectAlarm(int index) {
		if(index >= 0 && index < soundIDs.length) {
			selected = index;
			return true;
		} else
			return false;
	}
	
	public int getSelectedIndex() {
		return selected;
	}

	public void playAlarm(boolean repeat) {
		if (!playingAlarm && numberLoadedSounds == soundIDs.length) {
			playingAlarm = repeat;
			lastPlayedAlarm = System.currentTimeMillis();
			soundPool.play(soundIDs[selected], VOLUME, VOLUME, 1, repeat ? -1 : 0, 1f);
		}
//		if (!playingAlarm && numberLoadedSounds == soundIDs.length && (System.currentTimeMillis() - lastPlayedAlarm > SOUND_DURATION[selected])) {
//			lastPlayedAlarm = System.currentTimeMillis();
//			soundPool.play(soundIDs[selected], VOLUME, VOLUME, 1, 0, 1f);
//		}
	}

	public void autoPause() {
		soundPool.autoPause();
		playingAlarm = false;
	}
	
	public void destroy() {
		autoPause();
		soundPool.release();
		soundPool = null;
		soundIDs = null;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}

}

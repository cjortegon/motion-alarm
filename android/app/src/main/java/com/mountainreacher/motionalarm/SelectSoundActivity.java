package com.mountainreacher.motionalarm;

import com.mountainreacher.motionalarm.alarmservice.Alarm;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class SelectSoundActivity extends Activity implements OnItemClickListener, OnClickListener {

	private ArrayAdapter<String> listAdapter;
	private ListView listView;
	private Alarm alarm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_sound_activity);
		alarm = new Alarm(this);
		init();
	}

	private void init() {

		listView = (ListView)findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 0);
		
		for (int i = 0; i < Alarm.SOUND_NAMES.length; i++)
			listAdapter.add(Alarm.SOUND_NAMES[i]);
		listView.setAdapter(listAdapter);
		
		((Button) findViewById(R.id.selectSoundButton)).setOnClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int clickPos, long arg3) {
		alarm.autoPause();
		alarm.selectAlarm(clickPos);
		alarm.playAlarm(false);
	}
	
	@Override
	public void onBackPressed() {
		setResult(-1);
		super.onBackPressed();
	}

	@Override
	public void onClick(View arg0) {
		setResult(alarm.getSelectedIndex());
		finish();
	}

}

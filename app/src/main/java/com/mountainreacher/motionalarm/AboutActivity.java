package com.mountainreacher.motionalarm;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		setTitle(getResources().getString(R.string.about));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.help, menu);
		return true;
	}

}

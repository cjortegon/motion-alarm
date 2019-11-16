package com.mountainreacher.motionalarm.events;

import com.mountainreacher.motionalarm.AboutActivity;
import com.mountainreacher.motionalarm.HelpActivity;
import com.mountainreacher.motionalarm.R;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;
import android.widget.Toast;

public class MenuEvents {

	private static final String MOTION_ALARM_VERSION = "1.5.3";

	public static boolean menuPressed(Context context, MenuItem item) {

		switch (item.getItemId()) {
		case R.id.about_item:
			Intent startActivity = new Intent(context, AboutActivity.class);
			context.startActivity(startActivity);
			return true;

		case R.id.help_item:
			Intent startActivity2 = new Intent(context, HelpActivity.class);
			context.startActivity(startActivity2);
			return true;

		case R.id.contact_item:
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"mountainreacher@gmail.com"});
			i.putExtra(Intent.EXTRA_SUBJECT, "Feedback - Motion Alarm");
			i.putExtra(Intent.EXTRA_TEXT, "The version I'm using is "+MOTION_ALARM_VERSION+"\n\n(write your comments here)");
			try {
				context.startActivity(Intent.createChooser(i, "Send mail"));
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
			}
			return true;

		case R.id.rate_item:
			Intent link = new Intent(Intent.ACTION_VIEW);
			link.setData(Uri.parse("market://details?id=com.mountainreacher.motionalarm"));
			context.startActivity(link);
			return true;

		default:
			return false;
		}
	}
}

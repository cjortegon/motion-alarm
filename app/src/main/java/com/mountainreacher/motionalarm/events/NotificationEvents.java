package com.mountainreacher.motionalarm.events;

import com.mountainreacher.motionalarm.EmptyActivity;
import com.mountainreacher.motionalarm.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

public class NotificationEvents {

	private Context context;
	private Resources res;
	private NotificationManager notificationManager;
	private Notification notification;
	private PendingIntent contentIntent;
	private final static int NOTIFICATION_ID = 12345;

	public NotificationEvents(Context context) {

		this.context = context;
		this.res = context.getResources();

		try {
			notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

			int icon = R.drawable.motionalarmicon;
			CharSequence text = res.getText(R.string.starting);
			CharSequence contentTitle = context.getResources().getText(R.string.app_name);
			CharSequence contentText = res.getText(R.string.ready_to_use);
			long when = System.currentTimeMillis();

			Intent intent = new Intent(context, EmptyActivity.class);
			contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
			Notification notification = new Notification(icon,text,when);
			notificationManager.notify(NOTIFICATION_ID, notification);
		} catch(Exception e) {}
	}

	public void dismissAll() {
		notificationManager.cancel(NOTIFICATION_ID);
	}

}

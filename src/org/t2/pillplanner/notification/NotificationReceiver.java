package org.t2.pillplanner.notification;

import org.t2.pillplanner.MainActivity;
import org.t2.pillplanner.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {

	NotificationManager notifyManager;

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {

		Log.d("NotificationAlarm", "onReceive");

		notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		//Open app if user clicks on notification
		Intent notificationIntent = new Intent(context, MainActivity.class);

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		Notification notif = new Notification(R.drawable.usericon, "pillplanner Reminder", System.currentTimeMillis());

		// Play sound?
		//notif.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.jingle);

		notif.setLatestEventInfo(context, "pillplanner: Medication due", "Ibuprofen scheduled at 2:00pm today.", contentIntent);
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
		
		notifyManager.notify(1, notif);
	}
}
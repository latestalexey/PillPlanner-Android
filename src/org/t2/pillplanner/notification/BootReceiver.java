package org.t2.pillplanner.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	/**
	 * Listens for Android's BOOT_COMPLETED broadcast and then executes
	 * the onReceive() method.
	 */
	@Override
	public void onReceive(Context context, Intent arg1) {
		Log.d("Autostart", "BOOT_COMPLETED broadcast received. Executing Alarm service.");

		Intent intent = new Intent(context, AlarmService.class);
		context.startService(intent);
	}
}

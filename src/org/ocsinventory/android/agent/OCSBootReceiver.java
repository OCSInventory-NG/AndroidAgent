package org.ocsinventory.android.agent;

import java.util.Calendar;

import org.ocsinventory.android.actions.OCSLog;
import org.ocsinventory.android.actions.OCSSettings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
 
public class OCSBootReceiver extends BroadcastReceiver {
	private static final int EXEC_INTERVAL = 20 * 1000;
 
	@Override
	public void onReceive(final Context ctx, final Intent intent) {
		OCSSettings ocssetting = OCSSettings.getInstance(ctx);
		OCSLog ocslog = OCSLog.getInstance();
		ocslog.append("SchedulerSetupReceiver : "+intent.getAction());
		if  ( ocssetting == null  ) {
			ocslog.append("NULL OSSETTING");
			return;
		}
		
		if  ( ! ocssetting.isAutoMode() ) 
			return;
		int interval = ocssetting.getFreqWake();
		ocslog.append("SchedulerSetupReceiver interval : "+interval);
		
		AlarmManager alarmManager = (AlarmManager) ctx
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(ctx, OCSEventReceiver.class); 
																	
		PendingIntent intentExecuted = PendingIntent.getBroadcast(ctx, 0, i,
				PendingIntent.FLAG_CANCEL_CURRENT);
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MINUTE, interval);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				now.getTimeInMillis(), interval*60000L, intentExecuted);
	}
 
}
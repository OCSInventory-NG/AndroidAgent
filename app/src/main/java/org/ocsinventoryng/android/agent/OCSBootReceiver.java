/*
 * Copyright 2013-2016, OCSInventory-NG/AndroidAgent contributors
 *
 * This file is part of OCSInventory-NG/AndroidAgent.
 *
 * OCSInventory-NG/AndroidAgent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OCSInventory-NG/AndroidAgent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OCSInventory-NG/AndroidAgent. If not, see <http://www.gnu.org/licenses/>
 */
package org.ocsinventoryng.android.agent;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.ocsinventoryng.android.actions.OCSLog;
import org.ocsinventoryng.android.actions.OCSSettings;

import java.util.Calendar;

public class OCSBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context ctx, final Intent intent) {
        android.util.Log.d("OCSBOOT", "on Receive called");
        OCSSettings ocssetting = OCSSettings.getInstance(ctx);
        OCSLog ocslog = OCSLog.getInstance();
        ocslog.debug("OCSBootReceiver : " + intent.getAction());
        if (ocssetting == null) {
            ocslog.error("NULL OSSETTING");
            return;
        }

        if (!ocssetting.isAutoMode()) {
            return;
        }
        int interval = ocssetting.getFreqWake();
        ocslog.debug("OCSBootReceiver interval : " + interval);

        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(ctx, OCSEventReceiver.class);

        PendingIntent intentExecuted = PendingIntent.getBroadcast(ctx, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar start = Calendar.getInstance();
        start.add(Calendar.SECOND, 5);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, start.getTimeInMillis(), interval * 60000L, intentExecuted);
    }
}
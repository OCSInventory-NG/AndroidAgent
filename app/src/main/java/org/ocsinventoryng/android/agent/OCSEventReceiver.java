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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OCSEventReceiver extends BroadcastReceiver {
    private static final String LOGTAG = "OCSEventReceiver";

    @Override
    public void onReceive(final Context ctx, final Intent intent) {
        Log.d(LOGTAG, "Called");
        Intent eventService = new Intent(ctx, OCSAgentService.class);
        boolean forceUpdate = intent.getBooleanExtra(OCSAgentService.FORCE_UPDATE, false);
        eventService.putExtra(OCSAgentService.FORCE_UPDATE, forceUpdate);
        boolean saveInventory = intent.getBooleanExtra(OCSAgentService.SAVE_INVENTORY, false);
        eventService.putExtra(OCSAgentService.SAVE_INVENTORY, saveInventory);
        ctx.startService(eventService);
        Log.d(LOGTAG, "After start service");
    }
}
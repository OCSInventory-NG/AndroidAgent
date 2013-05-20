package org.ocsinventory.android.agent;

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
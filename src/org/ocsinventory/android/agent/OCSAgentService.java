package org.ocsinventory.android.agent;

import java.nio.channels.GatheringByteChannel;
import java.util.Date;

import org.ocsinventory.android.actions.Inventory;
import org.ocsinventory.android.actions.OCSFiles;
import org.ocsinventory.android.actions.OCSLog;
import org.ocsinventory.android.actions.OCSProtocol;
import org.ocsinventory.android.actions.OCSProtocolException;
import org.ocsinventory.android.actions.OCSSettings;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
 
public class OCSAgentService extends Service {
	private static final String LOGTAG = "SchedulerEventService";
 
	@Override
	public IBinder onBind(final Intent intent) {
		return null;		
	}
 
	@Override
	public int onStartCommand(final Intent intent, final int flags,
			final int startId) {
	
		OCSSettings ocssetting = OCSSettings.getInstance(getApplicationContext());
		OCSLog ocslog = OCSLog.getInstance();
		ocslog.append("ocsservice wake : " + new Date().toString());
		
		
		// Au cas ou l'option a changé depuis le lancement du service
		if ( ! ocssetting.isAutoMode() ) 
			return Service.START_NOT_STICKY;
		
		int  freq = ocssetting.getFreqMaj();
		long lastUpdt = ocssetting.getLastUpdt();
		long delta = System.currentTimeMillis() -  lastUpdt;
		ocslog.append("now         : "+System.currentTimeMillis());
		ocslog.append("last update : "+lastUpdt);
		ocslog.append("delta 		: "+delta);
		ocslog.append("freqamj 		: "+freq * 86400000L);
		if ( delta > freq * 86400000L) {
			if ( isOnline() ) {
				if ( SendInventory() == 0 ) {
					ocssetting.setLastUpdt(System.currentTimeMillis());
				}
			}
		}
		
		return Service.START_NOT_STICKY;
	}
	
	private int SendInventory() {

		Inventory inventory  = Inventory.getInstance(getApplicationContext());
		// OCSFiles.getInstance().getInventoryFileXML(inventory);				
		OCSProtocol ocsproto = new OCSProtocol();
		String rep;
		try {
			rep=ocsproto.sendPrologueMessage(inventory);
			rep=ocsproto.sendInventoryMessage(inventory);
		} catch (OCSProtocolException e) {
			return(1);
		}
		
		return 0;
	}
	
	private  boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
}

package org.ocsinventory.android.agent;

import java.util.Date;

import org.ocsinventory.android.actions.Inventory;
import org.ocsinventory.android.actions.OCSLog;
import org.ocsinventory.android.actions.OCSProtocol;
import org.ocsinventory.android.actions.OCSProtocolException;
import org.ocsinventory.android.actions.OCSSettings;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
 
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class OCSAgentService extends Service {
	
	private NotificationManager mNM;

	/*
	 * Binder juste pour verifier que le service tourne
	 */
    public class LocalBinder extends Binder {
    	OCSAgentService getService() {
            return OCSAgentService.this;
        }
    }
    
    private final IBinder mBinder = new LocalBinder();
	
	@Override
	public IBinder onBind(final Intent intent) {
		return mBinder;		
	}
 
	@Override
	public int onStartCommand(final Intent intent, final int flags,
			final int startId) {
	
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);		

		OCSSettings ocssetting = OCSSettings.getInstance(getApplicationContext());
		OCSLog ocslog = OCSLog.getInstance();
		ocslog.append("ocsservice wake : " + new Date().toString());		
		
		// Au cas ou l'option a changé depuis le lancement du service
		if ( ! sp.getBoolean("k_automode", false) ) 
			return Service.START_NOT_STICKY;
		
		// notify(R.string.not_start_service);
		
		int  freq = Integer.parseInt(sp.getString("k_freqmaj" , ""));
		long lastUpdt = sp.getLong("k_lastupdt" , 0L);
		long delta = System.currentTimeMillis()- lastUpdt;
		
		ocslog.append("now         : "+System.currentTimeMillis());
		ocslog.append("last update : "+lastUpdt);
		ocslog.append("delta laps  : "+delta);
		ocslog.append("freqmaj     : "+freq * 3600000L);
			
		if ( delta > freq * 3600000L) {
			if ( isOnline() ) {
				if ( sendInventory() == 0 ) {
					notify(R.string.nty_inventory_sent);
					Editor edt = sp.edit();
					edt.putLong("k_lastupdt", System.currentTimeMillis());
					edt.commit();
				}
			}
		}
		
		return Service.START_NOT_STICKY;
	}
	
	private int sendInventory() {

		Inventory inventory  = Inventory.getInstance(getApplicationContext());
		// OCSFiles.getInstance().getInventoryFileXML(inventory);				
		OCSProtocol ocsproto = new OCSProtocol();
		try {
			ocsproto.sendPrologueMessage(inventory);
			ocsproto.sendInventoryMessage(inventory);
		} catch (OCSProtocolException e) {
			return(1);
		}
		
		return 0;
	}
/*	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void notify(int id) {

		Notification notif;
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle("OCS notification")
        .setContentText(getText(id));

		
		Intent rIntent = new Intent(this, OCSAgentActivity.class);
		
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(OCSAgentActivity.class);
		stackBuilder.addNextIntent(rIntent);
		PendingIntent intent = PendingIntent.getActivity(this, 0, rIntent, PendingIntent.FLAG_ONE_SHOT);
		PendingIntent rpIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(rpIntent);
		
		mNM.notify(id, mBuilder.build());
	}
	*/
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void notify(int id) {

		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
        	.setSmallIcon(R.drawable.ic_notification)
        	.setContentTitle(getText(R.string.nty_title))
        	.setContentText(getText(id)) 	
        	;

		Intent rIntent = new Intent(this, OCSAgentActivity.class);
		/*
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(OCSAgentActivity.class);
		stackBuilder.addNextIntent(rIntent);
		*/
		PendingIntent rpIntent = PendingIntent.getActivity(this, 0, rIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(rpIntent);
		
		mNM.notify(id, mBuilder.build());
	}	
	
    public void onDestroy() {
    	mNM.cancelAll();
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

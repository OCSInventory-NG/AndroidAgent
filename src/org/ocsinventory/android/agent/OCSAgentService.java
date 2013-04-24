package org.ocsinventory.android.agent;

import java.util.Date;

import org.ocsinventory.android.actions.Inventory;
import org.ocsinventory.android.actions.OCSFiles;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
 
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class OCSAgentService extends Service {
	
	private NotificationManager mNM;
	private OCSSettings mOcssetting;

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

		mOcssetting = OCSSettings.getInstance(getApplicationContext());
		OCSLog ocslog = OCSLog.getInstance();
		ocslog.append("ocsservice wake : " + new Date().toString());		
		
		// Au cas ou l'option a changé depuis le lancement du service
		if ( ! mOcssetting.isAutoMode() )
			return Service.START_NOT_STICKY;
		
		// notify(R.string.not_start_service);
		
		int  freq = mOcssetting.getFreqMaj();
		long lastUpdt = mOcssetting.getLastUpdt();
		long delta = System.currentTimeMillis()- lastUpdt;
		
		ocslog.append("now         : "+System.currentTimeMillis());
		ocslog.append("last update : "+lastUpdt);
		ocslog.append("delta laps  : "+delta);
		ocslog.append("freqmaj     : "+freq * 3600000L);
			
		if ( delta > freq * 3600000L) {
			if ( isOnline() ) {
				
					AsyncCall task = new AsyncCall(this.getApplicationContext());
					task.execute(); 
			}
		}
		
		return Service.START_NOT_STICKY;
	}
	
	private int sendInventory() {

		Inventory inventory  = Inventory.getInstance(getApplicationContext());
		// OCSFiles.getInstance().getInventoryFileXML(inventory);
		OCSProtocol ocsproto = new OCSProtocol(getApplicationContext());
		try {
			ocsproto.sendPrologueMessage(inventory);
			ocsproto.sendInventoryMessage(inventory);
		} catch (OCSProtocolException e) {
			return(1);
		}
		
		return 0;
	}
	
	   private class AsyncCall extends AsyncTask<Void, Void, Void> {
		   int status;
		   Context mContext;
		   
		   AsyncCall(Context ctx) {
			   mContext = ctx;
		   }
		   
	        @Override
	        protected Void doInBackground(Void... params) {
	        	status = sendInventory();

	            return null;
	        }

	        @Override
	        protected void onPostExecute(Void result) {
	            if ( status == 0) 
	            {
	                notify(R.string.nty_inventory_sent);
	                mOcssetting.setLastUpdt(System.currentTimeMillis());
	            }
	        }

	        @Override
	        protected void onPreExecute() {
	        }

	        @Override
	        protected void onProgressUpdate(Void... values) {
	        }
	    	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	    	private void notify(int id) {

	    		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	    	
	    		
	    		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
	            	.setSmallIcon(R.drawable.ic_notification)
	            	.setContentTitle(getText(R.string.nty_title))
	            	.setContentText(getText(id)) 	
	            	;

	    		Intent rIntent = new Intent(mContext, OCSAgentActivity.class);
	    		/*
	    		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
	    		stackBuilder.addParentStack(OCSAgentActivity.class);
	    		stackBuilder.addNextIntent(rIntent);
	    		*/
	    		PendingIntent rpIntent = PendingIntent.getActivity(mContext, 0, rIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	    		mBuilder.setContentIntent(rpIntent);
	    		
	    		mNM.notify(id, mBuilder.build());
	    	}	
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

	
    public void onDestroy() {
    	NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
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

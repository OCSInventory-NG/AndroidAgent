package org.ocsinventoryng.android.agent;

import java.util.Date;

import org.ocsinventoryng.android.actions.Inventory;
import org.ocsinventoryng.android.actions.OCSFiles;
import org.ocsinventoryng.android.actions.OCSLog;
import org.ocsinventoryng.android.actions.OCSProtocol;
import org.ocsinventoryng.android.actions.OCSProtocolException;
import org.ocsinventoryng.android.actions.OCSSettings;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
 

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class OCSAgentService extends Service {
	
	public final static String FORCE_UPDATE = "force_update";
	public final static String SAVE_INVENTORY = "save_inventory";
	
	public final static int HIDE_NOTIF_NONE 		= 0;
	public final static int HIDE_NOTIF_INVENT 		= 1;
	public final static int HIDE_NOTIF_DOWNLOAD		= 2;
	public final static int HIDE_NOTIF_ALL 			= 3;
	
	private final long HOUR_IN_MILLIS 		= android.text.format.DateUtils.HOUR_IN_MILLIS;
	private final int AUTOMODE_NOROAMING 	=  0;
	private final int AUTOMODE_ANY 			=  1;
	private final int AUTOMODE_WIFI 		=  2;	



	private NotificationManager mNM;
	private OCSSettings mOcssetting;
	boolean mIsForced = false;
	boolean mSaveInventory = false;


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
		ocslog.debug("ocsservice wake : " + new Date().toString());	
		if( intent.getExtras() != null ) {
			mIsForced = intent.getExtras().getBoolean(FORCE_UPDATE);
			mSaveInventory = intent.getExtras().getBoolean(SAVE_INVENTORY);
		}
		// Au cas ou l'option a changé depuis le lancement du service
		if ( ! mOcssetting.isAutoMode() && ! mIsForced )
			return Service.START_NOT_STICKY;
		
		try {
			int vcode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
			new OCSProtocol(getApplicationContext()).verifyNewVersion(vcode);
		} catch (NameNotFoundException e) {	}
		
		// notify(R.string.not_start_service);
		
		int  freq = mOcssetting.getFreqMaj();
		long lastUpdt = mOcssetting.getLastUpdt();
		long delta = System.currentTimeMillis()- lastUpdt;
		
		ocslog.debug("now         : "+System.currentTimeMillis());
		ocslog.debug("last update : "+lastUpdt);
		ocslog.debug("delta laps  : "+delta);
		ocslog.debug("freqmaj     : "+freq * HOUR_IN_MILLIS );

		if ( (delta > freq * HOUR_IN_MILLIS && isOnline()) || mIsForced ) {
			ocslog.debug("mIsForced  : "+mIsForced);
			ocslog.debug("bool date  : "+(delta > freq * HOUR_IN_MILLIS));
			AsyncCall task = new AsyncCall(this.getApplicationContext());
			task.execute();
		}
		
		return Service.START_NOT_STICKY;
	}
	
	private int sendInventory() {
		OCSPrologReply reply;
		Inventory inventory  = Inventory.getInstance(getApplicationContext());
		// OCSFiles.getInstance().getInventoryFileXML(inventory);
		OCSProtocol ocsproto = new OCSProtocol(getApplicationContext());
		try {
			reply = ocsproto.sendPrologueMessage(inventory);
			if ( ! reply.getIdList().isEmpty() ) {
				OCSLog.getInstance().debug(getApplicationContext().getString(R.string.start_download_service));
				// Some downlowds requiered invoke download service
				Intent dldService = new Intent(getApplicationContext(), OCSDownloadService.class);
				getApplicationContext().startService(dldService);
			}

			ocsproto.sendInventoryMessage(inventory);
		} catch (OCSProtocolException e) {
			return(1);
		}
		
		return 0;
	}
	
	private int saveInventory() {
		Inventory inventory  = Inventory.getInstance(getApplicationContext());
		new OCSFiles(getApplicationContext()).copyToExternal(inventory);
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
	        	if(mSaveInventory) {
	        		saveInventory();
	        	}

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
		
		    if ( mOcssetting.getHiddenNotif()==HIDE_NOTIF_INVENT || mOcssetting.getHiddenNotif()==HIDE_NOTIF_ALL)
				return;
				
	    		OCSLog.getInstance().debug("Notify inventory");
	    		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	    	
	    		
	    		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
	            	.setSmallIcon(R.drawable.ic_notification)
	            	.setContentTitle(getText(R.string.nty_title))
	            	.setContentText(getText(id)).setAutoCancel(true)
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
	
    public void onDestroy() {
    	NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	mNM.cancelAll();
    }
 
	/*NR : Now not only check if we are online but also is the connectivity matches the preference "automodeNetwork"
	 */
	private  boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			if ( mOcssetting.getAutoModeNetwork() == AUTOMODE_NOROAMING && !netInfo.isRoaming())
				return true; // no roaming
			if ( mOcssetting.getAutoModeNetwork() == AUTOMODE_ANY )
				return true; // any network (including roaming)
			if ( mOcssetting.getAutoModeNetwork() == AUTOMODE_WIFI && netInfo.getType() == ConnectivityManager.TYPE_WIFI )
				return true; // wifi only
	    }
	    return false;
	}
}

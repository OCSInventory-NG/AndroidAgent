/*
 * Copyright 2013-2016 OCSInventory-NG/AndroidAgent contributors : mortheres, cdpointpoint,
 * Cédric Cabessa, Nicolas Ricquemaque, Anael Mobilia
 *
 * This file is part of OCSInventory-NG/AndroidAgent.
 *
 * OCSInventory-NG/AndroidAgent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * OCSInventory-NG/AndroidAgent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OCSInventory-NG/AndroidAgent. if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.ocsinventoryng.android.agent;

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
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import org.ocsinventoryng.android.actions.Inventory;
import org.ocsinventoryng.android.actions.OCSFiles;
import org.ocsinventoryng.android.actions.OCSLog;
import org.ocsinventoryng.android.actions.OCSProtocol;
import org.ocsinventoryng.android.actions.OCSProtocolException;
import org.ocsinventoryng.android.actions.OCSSettings;

import java.util.Date;

public class OCSAgentService extends Service {
    public final static String FORCE_UPDATE = "force_update";
    public final static String SAVE_INVENTORY = "save_inventory";

    public final static int HIDE_NOTIF_NONE = 0;
    public final static int HIDE_NOTIF_INVENT = 1;
    public final static int HIDE_NOTIF_DOWNLOAD = 2;
    public final static int HIDE_NOTIF_ALL = 3;

    private final long HOUR_IN_MILLIS = android.text.format.DateUtils.HOUR_IN_MILLIS;
    private final int AUTOMODE_NOROAMING = 0;
    private final int AUTOMODE_ANY = 1;
    private final int AUTOMODE_WIFI = 2;

    private NotificationManager mNM;
    private OCSSettings mOcssetting;
    private boolean mIsForced = false;
    private boolean mSaveInventory = false;

    private final IBinder mBinder = new LocalBinder();

    /*
     * Binder juste pour verifier que le service tourne
     */
    public class LocalBinder extends Binder {
        private OCSAgentService getService() {
            return OCSAgentService.this;
        }
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        mOcssetting = OCSSettings.getInstance(getApplicationContext());
        OCSLog ocslog = OCSLog.getInstance();
        ocslog.debug("ocsservice wake : " + new Date().toString());
        if (intent.getExtras() != null) {
            mIsForced = intent.getExtras().getBoolean(FORCE_UPDATE);
            mSaveInventory = intent.getExtras().getBoolean(SAVE_INVENTORY);
        }
        // Au cas ou l'option a changé depuis le lancement du service
        if (!mOcssetting.isAutoMode() && !mIsForced) {
            return Service.START_NOT_STICKY;
        }

        try {
            int vcode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            new OCSProtocol(getApplicationContext()).verifyNewVersion(vcode);
        } catch (NameNotFoundException e) {
        }

        // notify(R.string.not_start_service);

        int freq = mOcssetting.getFreqMaj();
        long lastUpdt = mOcssetting.getLastUpdt();
        long delta = System.currentTimeMillis() - lastUpdt;

        ocslog.debug("now         : " + System.currentTimeMillis());
        ocslog.debug("last update : " + lastUpdt);
        ocslog.debug("delta laps  : " + delta);
        ocslog.debug("freqmaj     : " + freq * HOUR_IN_MILLIS);

        if ((delta > freq * HOUR_IN_MILLIS && isOnline()) || mIsForced) {
            ocslog.debug("mIsForced  : " + mIsForced);
            ocslog.debug("bool date  : " + (delta > freq * HOUR_IN_MILLIS));
            AsyncCall task = new AsyncCall(this.getApplicationContext());
            task.execute();
        }

        return Service.START_NOT_STICKY;
    }

    private int sendInventory() {
        OCSPrologReply reply;
        Inventory inventory = Inventory.getInstance(getApplicationContext());
        // OCSFiles.getInstance().getInventoryFileXML(inventory);
        OCSProtocol ocsproto = new OCSProtocol(getApplicationContext());
        try {
            reply = ocsproto.sendPrologueMessage(inventory);
            if (!reply.getIdList().isEmpty()) {
                OCSLog.getInstance().debug(getApplicationContext().getString(R.string.start_download_service));
                // Some downlowds requiered invoke download service
                Intent dldService = new Intent(getApplicationContext(), OCSDownloadService.class);
                getApplicationContext().startService(dldService);
            }

            ocsproto.sendInventoryMessage(inventory);
        } catch (OCSProtocolException e) {
            return (1);
        }

        return 0;
    }

    private void saveInventory() {
        Inventory inventory = Inventory.getInstance(getApplicationContext());
        new OCSFiles(getApplicationContext()).copyToExternal(inventory);
    }

    private class AsyncCall extends AsyncTask<Void, Void, Void> {
        private int status;
        private Context mContext;

        AsyncCall(Context ctx) {
            mContext = ctx;
        }

        @Override
        protected Void doInBackground(Void... params) {
            status = sendInventory();
            if (mSaveInventory) {
                saveInventory();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (status == 0) {
                notify(R.string.nty_inventory_sent);
                mOcssetting.setLastUpdt(System.currentTimeMillis());
            }
        }

        private void notify(int id) {
            if (mOcssetting.getHiddenNotif() == HIDE_NOTIF_INVENT || mOcssetting.getHiddenNotif() == HIDE_NOTIF_ALL) {
                return;
            }

            OCSLog.getInstance().debug("Notify inventory");
            mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext).setSmallIcon(
                    R.drawable.ic_notification).setContentTitle(getText(R.string.nty_title)).setContentText(
                    getText(id)).setAutoCancel(true).setContentText(getText(id));

            Intent rIntent = new Intent(mContext, OCSAgentActivity.class);

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
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            if (mOcssetting.getAutoModeNetwork() == AUTOMODE_NOROAMING && !netInfo.isRoaming()) {
                return true; // no roaming
            }
            if (mOcssetting.getAutoModeNetwork() == AUTOMODE_ANY) {
                return true; // any network (including roaming)
            }
            if (mOcssetting.getAutoModeNetwork() == AUTOMODE_WIFI && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true; // wifi only
            }
        }
        return false;
    }
}

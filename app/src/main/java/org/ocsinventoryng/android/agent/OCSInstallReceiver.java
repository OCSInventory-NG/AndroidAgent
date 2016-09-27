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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.widget.Toast;

import org.ocsinventoryng.android.actions.OCSLog;
import org.ocsinventoryng.android.actions.OCSProtocol;
import org.ocsinventoryng.android.actions.OCSProtocolException;

import java.io.File;
import java.io.FileInputStream;

public class OCSInstallReceiver extends BroadcastReceiver {
    private OCSLog mOCSlog;
    private String mStatus;
    private String mOCSid;

    @Override
    public void onReceive(final Context ctx, final Intent intent) {
        mOCSlog = OCSLog.getInstance();
        byte buffer[] = new byte[80];

        String data = intent.getData().toString();
        mOCSlog.debug("OCSInstallReceiver : " + intent.getAction());
        Toast.makeText(ctx, "OCSInstallReceiver : " + intent.getAction(), Toast.LENGTH_SHORT).show();
        String packageName = data.split(":")[1];
        mOCSlog.debug("Package : " + packageName);
        try {
            mOCSlog.debug("Lecture " + packageName + ".inst");
            FileInputStream fis = ctx.openFileInput(packageName + ".inst");
            fis.read(buffer);
            String s = new String(buffer);
            String t[] = s.split(":");
            mOCSid = t[0];
            int version = Integer.parseInt(t[1]);
            fis.close();

            if (isPkgInstalled(ctx, packageName, version)) {
                mOCSlog.debug("Package installed return success to OCS server");
                mStatus = "SUCCESS";
            } else {
                mOCSlog.debug("Package not installed return fail to OCS server");
                mStatus = "ERR_ABORTED";
            }

            AsyncSend task = new AsyncSend(ctx);
            task.execute();

            // Clean download files
            File fapk = new File(ctx.getExternalCacheDir(), mOCSid + ".apk");
            fapk.delete();
            File finst = new File(ctx.getFilesDir(), packageName + ".inst");
            finst.delete();
            File finfo = new File(ctx.getFilesDir(), mOCSid + ".info");
            finfo.delete();
        } catch (Exception e) {
            mOCSlog.error(e.getMessage());
        }
    }

    /**
     * Check if a package is installed with a given version code
     *
     * @param pkg     Package name
     * @param version Version code
     * @return true if installed
     */
    private boolean isPkgInstalled(Context ctx, String pkg, int version) {
        PackageManager pm = ctx.getPackageManager();

        mOCSlog.debug("Check installation " + pkg + "/" + version);
        try {
            PackageInfo lpInfo = pm.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES);
            return (lpInfo.versionCode == version);
        } catch (NameNotFoundException e) {
            mOCSlog.error("Package notfound");
            return false;
        }
    }

    private class AsyncSend extends AsyncTask<Void, Void, Void> {
        Context mContext;

        AsyncSend(Context ctx) {
            mContext = ctx;
        }

        @Override
        protected Void doInBackground(Void... params) {
            OCSProtocol ocsproto = new OCSProtocol(mContext);
            try {
                ocsproto.sendRequestMessage("DOWNLOAD", mOCSid, mStatus);
            } catch (OCSProtocolException e) {
                mOCSlog.error(e.getMessage());
            }
            return null;
        }
    }
}

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
package org.ocs.android.agent.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.ocs.android.actions.OCSLog;
import org.ocs.android.agent.OCSDownloadInfos;
import org.ocs.android.agent.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * APK installation
 */
public class OCSInstallPackageActivity extends AppCompatActivity {

    private String[] mPackageNames;
    private String[] mIdOCS;
    private int[] mVersionCode;
    private OCSLog mOcslog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocs_install_package);
        mOcslog = OCSLog.getInstance();

        File[] mFiles = getExternalCacheDir().listFiles();
        mPackageNames = new String[mFiles.length];
        String[] mPackageVersions = new String[mFiles.length];
        mIdOCS = new String[mFiles.length];
        mVersionCode = new int[mFiles.length];

        StringBuilder sb = new StringBuilder("OCS installations :\n\n");
        for (int i = 0; i < mFiles.length; ++i) {
            String filename = mFiles[i].getName();
            mIdOCS[i] = filename.substring(0, filename.indexOf(".apk"));

            PackageManager pm = getPackageManager();
            PackageInfo pkgInfo = pm.getPackageArchiveInfo(mFiles[i].getPath(), PackageManager.GET_ACTIVITIES);
            mPackageNames[i] = pkgInfo.applicationInfo.packageName;
            mPackageVersions[i] = pkgInfo.versionName;
            mVersionCode[i] = pkgInfo.versionCode;
            mOcslog.debug("package : " + mPackageNames[i] + "/" + pkgInfo.versionCode);
            mOcslog.debug("package : " + mPackageNames[i] + "/" + mPackageVersions[i]);
            sb.append(filename).append(" :\n  ").append(pkgInfo.applicationInfo.packageName).append("\n").append(
                    "  version:").append(pkgInfo.versionName).append("\n\n");
            OCSDownloadInfos infos;
            try {
                infos = getInfos(mIdOCS[i]);
                if (infos != null && infos.getNotifyText() != null) {
                    sb.append("\n");
                    sb.append(infos.getNotifyText());
                }
            } catch (IOException e) {
                mOcslog.error(filename + " : " + e.getMessage());
            }

            sb.append("\n");

            File finst = mFiles[i];
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(finst), "application/vnd.android.package-archive");
            // Special case of agent itself
            if (mPackageNames[i].equals(getPackageName())) {
                mOcslog.debug(getPackageName() + " detected");
                // Save id;version code for asynchrone check later
                String idctx = mIdOCS[i] + ":" + pkgInfo.versionCode;
                try {
                    FileOutputStream fos = getApplicationContext().openFileOutput("update.flag", 0);
                    fos.write(idctx.getBytes());
                    fos.close();
                } catch (Exception e) {
                }

                startActivity(intent);
                mOcslog.debug("stop activity");
                finish();
            } else {
                startActivityForResult(intent, i + 1);
            }
        }
        TextView vMsg = (TextView) findViewById(R.id.textInstall);
        vMsg.setText(sb.toString());
        mOcslog.debug(sb.toString());
    }

    /**
     * Called at end of an Activity Set a little context file named with package name and contain OCS pakageid and version code
     * For asynchronous verification by OCSInstallReceiver.java
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int no = requestCode - 1;
        mOcslog = OCSLog.getInstance();

        String packageName = mPackageNames[no];
        if (packageName == null) {
            return;
        }
        String ctx = mIdOCS[no] + ":" + mVersionCode[no] + ":";
        // Save requestid and versioncode in a package context file for verification in OCSInstallReceiver
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(packageName + ".inst", 0);
            fos.write(ctx.getBytes());
            fos.close();
        } catch (Exception e) {
            mOcslog.error(e.getMessage());
        }
    }

    /**
     * Read informations file of OCS package
     *
     * @param id OCS id of the package
     * @return OCSDownloadInfos object builded from info file
     * @throws IOException
     */
    private OCSDownloadInfos getInfos(String id) throws IOException {
        StringBuilder sb = new StringBuilder();
        File finfos = new File(getApplicationContext().getFilesDir(), id + ".info");
        FileInputStream fis = new FileInputStream(finfos);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        String ligne;
        while ((ligne = br.readLine()) != null) {
            sb.append(ligne);
        }
        br.close();
        isr.close();
        return new OCSDownloadInfos(sb.toString());
    }
}

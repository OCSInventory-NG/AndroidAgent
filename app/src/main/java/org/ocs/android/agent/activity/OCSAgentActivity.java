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

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.ocs.android.actions.OCSProtocol;
import org.ocs.android.actions.OCSSettings;
import org.ocs.android.actions.PrefsParser;
import org.ocs.android.actions.Utils;
import org.ocs.android.agent.AboutDialog;
import org.ocs.android.agent.AsyncOperations;
import org.ocs.android.agent.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Launch activity - Start Screen
 */
public class OCSAgentActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    public OCSSettings settings = null;
    private final static String IMPORT_CONFIG = "import_config";
    protected ProgressDialog mProgressDialog;

    //ID to identify a camera permission request.
    private static final int PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int PERMISSIONS_REQUEST_EXTERNAL_STORAGE= 2;
    private static final int PERMISSIONS_REQUEST_PHONE_STATE= 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocs_agent);

        String[] permissionName = {Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE};
        int[]  permissionRequest = {PERMISSIONS_REQUEST_CAMERA,PERMISSIONS_REQUEST_EXTERNAL_STORAGE,PERMISSIONS_REQUEST_PHONE_STATE};

        for(int i=0; i<permissionName.length; i++){
            this.requestPermissions(permissionName[i], permissionRequest[i]);
        }

        // Initialize configuration.
        // If an extra "IMPORT_CONFIG" is added on launch of the activity, the configuration will be imported
        settings = OCSSettings.getInstance(this);
        settings.logSettings();
        if (getIntent().getStringExtra(IMPORT_CONFIG) != null) {
            importConfig();
            finish();
        }
        // If deviceUid is null. It's the first start. Then an import of the config is tried.
        if (settings.getDeviceUid() == null) {
            importConfig();
        }

        // Version update on title bar.
        int vcode;
        try {
            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            setTitle(getTitle() + " v." + version);
            vcode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            vcode = 0;
        }
        // Check if a new version is available
        new OCSProtocol(getApplicationContext()).verifyNewVersion(vcode);


        /**
         * Actions for buttons
         */
        // Send Inventory
        Button sendInventory = (Button) findViewById(R.id.btSendInventory);
        sendInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStatus(R.string.title_bt_launch);
                spawnTask(true);
            }
        });
        // Show Inventory
        Button showInventory = (Button) findViewById(R.id.btShowInventory);
        showInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent localIntent = new Intent(getApplicationContext(), OCSShowInventory.class);
                startActivity(localIntent);
            }
        });
        // Save Inventory
        Button saveInventory = (Button) findViewById(R.id.btSaveInventory);
        saveInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spawnTask(false);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case PERMISSIONS_REQUEST_EXTERNAL_STORAGE:{
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case PERMISSIONS_REQUEST_PHONE_STATE:{
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, OCSPrefsActivity.class));
                break;
            case R.id.menu_export:
                exportConfig();
                break;
            case R.id.menu_import:
                importConfig();
                break;
            case R.id.menu_about:
                AboutDialog about = new AboutDialog(this);
                about.show();
                break;
            default:
                break;
        }
        return true;
    }

    private void importConfig() {
        String myPackName = getApplicationContext().getPackageName();
        String filePrefsName = myPackName + "_preferences.xml";
        String oldFilePrefsName = "org.ocsinventory.android.agent_preferences.xml";
        File repPrefs = Environment.getExternalStoragePublicDirectory("ocs");
        File ficPrefs = new File(repPrefs, filePrefsName);

        if (!ficPrefs.exists()) {
            ficPrefs = new File(repPrefs, oldFilePrefsName);
            if (!ficPrefs.exists()) {
                return;
            }
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        PrefsParser pp = new PrefsParser();
        pp.parseDocument(ficPrefs, prefs);

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
        String deviceUid = "android-" + Secure.getString(getContentResolver(), Secure.ANDROID_ID) + "-" + sdf.format(now);
        settings.setDeviceUid(deviceUid);

        Toast.makeText(this, getText(R.string.msg_conf_imported), Toast.LENGTH_SHORT).show();
        setStatus(R.string.msg_conf_imported);
    }

    private void exportConfig() {
        String myPackName = getApplicationContext().getPackageName();
        String filePrefs = myPackName + "_preferences.xml";
        String pathPrefs = getApplicationInfo().dataDir + "/shared_prefs/" + filePrefs;

        String savedUid = settings.getDeviceUid();
        settings.setDeviceUid("");

        File repOut = Environment.getExternalStoragePublicDirectory("ocs");
        File ficOut = new File(repOut, filePrefs);
        File ficIn = new File(pathPrefs);
        Log.d("COPY", pathPrefs + " TO " + ficOut.getPath());
        try {
            Utils.copyFile(ficIn, ficOut);
        } catch (IOException e) {
            setStatus(e.getMessage());
        }
        Toast.makeText(this, getText(R.string.msg_conf_exported), Toast.LENGTH_SHORT).show();
        setStatus(R.string.msg_conf_exported);

        settings.setDeviceUid(savedUid);
    }

    private void spawnTask(boolean send) {
        TextView status = (TextView) findViewById(R.id.statusBar);

        setStatus(R.string.state_send_start);

        String titleProgress = (send) ? getString(R.string.title_bt_launch) : getString(R.string.title_bt_save);

        mProgressDialog = ProgressDialog.show(this, titleProgress, getString(R.string.state_build_inventory), true, false);
        new AsyncOperations(send, mProgressDialog, status, this, getApplicationContext()).execute();
    }

    private void setStatus(int id) {
        TextView status = (TextView) findViewById(R.id.statusBar);
        status.setText(id);
    }

    private void setStatus(String msg) {
        TextView status = (TextView) findViewById(R.id.statusBar);
        status.setText(msg);
    }

    private void requestPermissions(String permissionName, int permissionRequest){
        //request permission.
        if (ContextCompat.checkSelfPermission(OCSAgentActivity.this, permissionName)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(OCSAgentActivity.this,
                    new String[]{permissionName},
                    permissionRequest);
        }
    }
}

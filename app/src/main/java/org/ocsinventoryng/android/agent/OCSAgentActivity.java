package org.ocsinventoryng.android.agent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.ocsinventoryng.android.actions.OCSProtocol;
import org.ocsinventoryng.android.actions.OCSSettings;
import org.ocsinventoryng.android.actions.PrefsParser;
import org.ocsinventoryng.android.actions.Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class OCSAgentActivity extends Activity {
    public OCSSettings settings = null;
    private final static String IMPORT_CONFIG = "import_config";

    protected ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocs_agent);

        // Initialisation de la configuration. Si on ajoute une chaine extra "IMPORT_CONFIG" en lançant l'activité, la
        // configuration est réimportée de force.
        // Init. configuration. If an extra "IMPORT_CONFIG" is added on launch of the activity. The configuration import is
        // forced.
        settings = OCSSettings.getInstance(this);
        settings.logSettings();
        if (getIntent().getStringExtra(IMPORT_CONFIG) != null) {
            importConfig();
            finish();
        }
        // If deviceUid is null. It is the first start. The an import config is tried.
        if (settings.getDeviceUid() == null) {
            importConfig();
        }

        // Version update on title bar.
        String version;
        int vcode;
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            vcode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            version = "";
            vcode = 0;
        }
        new OCSProtocol(getApplicationContext()).verifyNewVersion(vcode);
        setTitle(getTitle() + " v." + version);
    }


    @Override
    protected void onStart() {
        super.onStart();
        android.util.Log.d("OCSAgentActivity", "onStart()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, OCSPrefsActivity.class));
                return true;
            case R.id.menu_export:
                exportConfig();
                return true;
            case R.id.menu_import:
                importConfig();
                return true;
            case R.id.menu_about:
                AboutDialog about = new AboutDialog(this);
                // about.setTitle("about this app");
                about.show();
        }
        return false;
    }

    public void showInventoryClicked(View view) {
        Intent localIntent = new Intent(this, OCSListActivity.class);
        startActivity(localIntent);
    }

    public void sendInventoryClicked(View view) {
        setStatus(R.string.title_bt_launch);
        this.spawnTask(true);
    }

    public void saveInventoryClicked(View view) {
        this.spawnTask(false);
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

        // 0.9.6 : suppress device id
        String savedUid = settings.getDeviceUid();
        settings.setDeviceUid("");

        File repOut = Environment.getExternalStoragePublicDirectory("ocs");
        File ficOut = new File(repOut, filePrefs);
        File ficIn = new File(pathPrefs);
        android.util.Log.d("COPY", pathPrefs + " TO " + ficOut.getPath());
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
}

package org.ocsinventory.android.agent;

import java.io.File;
import java.io.IOException;

import org.ocsinventory.android.actions.OCSFiles;
import org.ocsinventory.android.actions.OCSSettings;
import org.ocsinventory.android.actions.PrefsParser;
import org.ocsinventory.android.actions.Utils;
import org.ocsinventory.android.agent.R.color;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class OCSAgentActivity extends Activity {
	public OCSSettings settings = null;
	protected  ProgressDialog mProgressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.ocs_splash);
		setContentView(R.layout.ocs_agent);
		/*
		Typeface font = Typeface.createFromAsset(getAssets(), Constantes.OCSFONT);        
		((Button)findViewById(R.id.bt_launch)).setTypeface(font);
		((Button)findViewById(R.id.bt_save)).setTypeface(font);
		((Button) findViewById(R.id.bt_show)).setTypeface(font);
		((TextView) findViewById(R.id.statusBar)).setTypeface(font);
		*/
		/*
		View title = getWindow().findViewById(android.R.id.title);
		View titleBar = (View) title.getParent();
		titleBar.setBackgroundColor(color.violet_clair);
		// setTitleColor(color.violet_fonce);
		*/
		// Initialisation de la configuration
		settings=OCSSettings.getInstance(this);
		settings.logSettings();
		// logText.append("Init inventaire\n");
		// new AsyncJob(this).execute();
		
		// MAJ de la version dans la barre de titre
		String version;
		try {
			version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			version = "";
		}
		setTitle(getTitle()+" v."+version);
		/*
		if ( settings.getLastUpdt() > 0 ) {
			SimpleDateFormat sdf = new SimpleDateFormat();
			// (String)DateFormat.format("MM/dd/yyyy hh:mm:ss", settings.getLastUpdt());
			StringBuffer sb = new StringBuffer("Last upload ");
			sb.append(sdf.format(new Date(settings.getLastUpdt())));
			setStatus(sb.toString());;
		}
		*/
 }
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
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
		String filePrefs=myPackName+"_preferences.xml";
		File repOut=Environment.getExternalStoragePublicDirectory("ocs");
		File ficOut= new File(repOut, filePrefs);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		PrefsParser pp = new PrefsParser();
		pp.parseDocument(ficOut, prefs);
		Toast.makeText(this, getText(R.string.msg_conf_imported),Toast.LENGTH_SHORT ).show();
		setStatus(R.string.msg_conf_imported);
	}
	private void exportConfig() {
		String myPackName = getApplicationContext().getPackageName();
		String filePrefs=myPackName+"_preferences.xml";
		String pathPrefs = getApplicationInfo().dataDir+"/shared_prefs/"+filePrefs;
		
		// 0.9.6 : suppress device id
		String savedUid = settings.getDeviceUid();
		settings.setDeviceUid("");
		
		File repOut=Environment.getExternalStoragePublicDirectory("ocs");
		File ficOut= new File(repOut, filePrefs);
		File ficIn = new File(pathPrefs);
		android.util.Log.d("COPY", pathPrefs+" TO "+ficOut.getPath());
		try {
			Utils.copyFile(ficIn, ficOut);
		} catch (IOException e) {
			setStatus(e.getMessage());
		}
		Toast.makeText(this, getText(R.string.msg_conf_exported),Toast.LENGTH_SHORT ).show();
		setStatus(R.string.msg_conf_exported);
		
		settings.setDeviceUid(savedUid);
	}
	
	private void spawnTask(boolean send ) {
		TextView status = (TextView) findViewById(R.id.statusBar);

		setStatus(R.string.state_send_start);
		
		String titleProgress =  ( send  ) ? 
				getString(R.string.title_bt_launch) : 
				getString(R.string.title_bt_save);
				
		mProgressDialog = ProgressDialog.show(this,
				titleProgress,
				getString(R.string.state_build_inventory), true, false);
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

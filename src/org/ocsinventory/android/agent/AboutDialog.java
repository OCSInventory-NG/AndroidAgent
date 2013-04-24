package org.ocsinventory.android.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ocsinventory.android.actions.OCSSettings;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.util.Linkify;
import android.widget.TextView;

public class AboutDialog extends Dialog {
	private static Context mContext = null;

	public AboutDialog(Context context) {
		super(context);
		mContext = context;
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	/**
	 * Standard Android on create method that gets called when the activity
	 * initialized.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.about);

		OCSSettings ocssettings = OCSSettings.getInstance(mContext);
		
		long lastUpdt = ocssettings.getLastUpdt();
		
		StringBuffer sb = new StringBuffer("OCS Inventory NG android Agent \n");
		sb.append("Version :");
		try {
			sb.append( mContext.getPackageManager().
					getPackageInfo(mContext.getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {	}
		sb.append("\n");
		if ( lastUpdt > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat();		
			sb.append("Last upload : ");
			sb.append(sdf.format(new Date(lastUpdt)));
			sb.append("\n");
			if ( ocssettings.isAutoMode() ) {
				int  freq = ocssettings.getFreqMaj();
				long nextUpdt= lastUpdt+freq*3600000L;
				sb.append("Next upload : ");
				sb.append(sdf.format(new Date(nextUpdt)));
			} else 
				sb.append("Mode manuel");
		}
			
		TextView tv = (TextView) findViewById(R.id.test_about);
		tv.setText(sb.toString());

	}
}
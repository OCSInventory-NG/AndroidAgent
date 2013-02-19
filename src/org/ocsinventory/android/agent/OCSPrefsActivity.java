package org.ocsinventory.android.agent;

import java.util.Calendar;

import org.ocsinventory.android.actions.OCSSettings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class OCSPrefsActivity extends PreferenceActivity {
	/**
	 * Determines whether to always show the simplified settings UI, where
	 * settings are presented in a single list. When false, settings are shown
	 * as a master/detail two-pane view on tablets. When true, a single pane is
	 * shown on tablets.
	 */
	private static final boolean ALWAYS_SIMPLE_PREFS = false;
	private MyPreferenceChangeListener mPreferenceListener;
	private boolean mfreqwake_chg = false;
	private boolean mAutoMode_chg = false;
	private SharedPreferences mPrefs;	
	private static String uHour;
	private static String uMn;
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		uHour = getString(R.string.unit_hour);
		uMn = getString(R.string.unit_minutes);		
		// Add 'general' preferences.
		addPreferencesFromResource(R.xml.preferences);
		MyPreferenceChangeListener mPreferenceListener = new MyPreferenceChangeListener();
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this); 
	    mPrefs.registerOnSharedPreferenceChangeListener(mPreferenceListener);
	    
		bindPreferenceSummaryToValue(findPreference("k_serverurl"));
		bindPreferenceSummaryToValue(findPreference("k_devicetag"));
		bindPreferenceSummaryToValue(findPreference("k_freqmaj"));
		bindPreferenceSummaryToValue(findPreference("k_freqwake"));
		bindPreferenceSummaryToValue(findPreference("k_cachelen"));
		bindPreferenceSummaryToValue(findPreference("k_proxyadr"));
		bindPreferenceSummaryToValue(findPreference("k_proxyport"));
	}
	
	@Override  
	public void onDestroy() {  
	    super.onDestroy();  
	    mPrefs.unregisterOnSharedPreferenceChangeListener(mPreferenceListener);  
	}

	public void onStop() {
		super.onStop(); 
		android.util.Log.d("DEBUG", "onStop");
		Context ctx = this.getApplicationContext();
		
   	if ( mAutoMode_chg || mfreqwake_chg ) {
    		android.util.Log.d("DEBUG", "mAutoMode_chg");
    		if ( mPrefs.getBoolean("k_automode", false) ) {
    			// Setup service
    			if  ( mAutoMode_chg )
    				Toast.makeText(ctx, "Service started" , Toast.LENGTH_LONG).show();
 
       			int interval = OCSSettings.getInstance().getFreqWake();       			
       			AlarmManager alarmManager = (AlarmManager) ctx
       					.getSystemService(Context.ALARM_SERVICE);
       			Intent i = new Intent(ctx, OCSEventReceiver.class); 
       																		
       			PendingIntent intentExecuted = PendingIntent.getBroadcast(ctx, 0, i,
       					PendingIntent.FLAG_CANCEL_CURRENT);
       			Calendar start = Calendar.getInstance();
       			start.add(Calendar.SECOND, 5 );
       			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
       					start.getTimeInMillis(), interval*60000L, intentExecuted);
    		} else {
    			// Stop service
      			AlarmManager alarmManager = (AlarmManager) ctx
       					.getSystemService(Context.ALARM_SERVICE);
       			Intent i = new Intent(ctx, OCSEventReceiver.class); 
       																		
       			PendingIntent intentExecuted = PendingIntent.getBroadcast(ctx, 0, i,
       					PendingIntent.FLAG_CANCEL_CURRENT);
       			intentExecuted.cancel();
       			// alarmManager.cancel(intentExecuted)
    			Intent eventService = new Intent(ctx, OCSAgentService.class);
    			if ( ctx.stopService(eventService) )
    				Toast.makeText(ctx, "Service stopped" , Toast.LENGTH_LONG).show();
    		}
     	}	

		mfreqwake_chg=false;
		mAutoMode_chg=false;
	}
	private void cancelTimer (Context ctx) {
		
		Intent i = new Intent(ctx, OCSEventReceiver.class);
		PendingIntent intentExecuted = PendingIntent.getBroadcast(ctx, 0, i,
				PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(intentExecuted);
		
	}
	
	private class MyPreferenceChangeListener implements OnSharedPreferenceChangeListener {  
	    
	    @Override  
	    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) { 
	    	android.util.Log.v("PreferenceChange", key);
	        if ( key.equals("k_automode") ) { 
	        	mAutoMode_chg = true;
	        }
	    	
	        if ( key.equals("k_freqwake") ) {  
	            android.util.Log.v("PreferenceChange", "**** KEY test_preference_key modified ****");
	            mfreqwake_chg=true;
	        }
	        
	    }  
	}
	
	private static void bindPreferenceSummaryToValue(Preference pref) {
		// Set the listener to watch for value changes.
		pref.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(
				pref,
				PreferenceManager.getDefaultSharedPreferences(
						pref.getContext()).getString(pref.getKey(),
						""));
	}

	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
			new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();
			android.util.Log.v("onPreferenceChange : ", value.toString());
			if ( preference instanceof ListPreference ) {
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				preference
						.setSummary(index >= 0 ? listPreference.getEntries()[index]
								: null);

			}  else if ( preference instanceof EditTextPreference )	{
				if ( preference.getKey().equals("k_freqmaj") )
					stringValue=stringValue+" " + uHour;
				if ( preference.getKey().equals("k_freqwake") )
					stringValue=stringValue+" " + uMn;
					
				preference.setSummary(stringValue);			}
			return true;
		}
	};

	
}

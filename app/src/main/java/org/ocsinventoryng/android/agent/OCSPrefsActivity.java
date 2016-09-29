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
import android.util.Log;
import android.widget.Toast;

import org.ocsinventoryng.android.actions.OCSSettings;

import java.util.Calendar;

public class OCSPrefsActivity extends PreferenceActivity {
    /**
     * Determines whether to always show the simplified settings UI, where settings are presented in a single list. When false,
     * settings are shown as a master/detail two-pane view on tablets. When true, a single pane is shown on tablets.
     */
    // private static final boolean ALWAYS_SIMPLE_PREFS = false;
    private MyPreferenceChangeListener mPreferenceListener;
    private boolean mfreqwakeChg = false;
    private boolean mAutoModeChg = false;
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
        bindPreferenceSummaryToValue(findPreference("k_automodeNetwork"));
        bindPreferenceSummaryToValue(findPreference("k_hideNotif"));
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
        Log.d("DEBUG", "onStop");
        Context ctx = getApplicationContext();

        if (mAutoModeChg || mfreqwakeChg) {
            Log.d("DEBUG", "mAutoModeChg");
            if (mPrefs.getBoolean("k_automode", false)) {
                // Setup service
                if (mAutoModeChg) {
                    Toast.makeText(ctx, "Service started", Toast.LENGTH_LONG).show();
                }

                int interval = OCSSettings.getInstance().getFreqWake();
                AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
                Intent i = new Intent(ctx, OCSEventReceiver.class);

                PendingIntent intentExecuted = PendingIntent.getBroadcast(ctx, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
                Calendar start = Calendar.getInstance();
                start.add(Calendar.SECOND, 5);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, start.getTimeInMillis(), interval * 60000L, intentExecuted);
            } else {
                // Stop service
                Intent i = new Intent(ctx, OCSEventReceiver.class);

                PendingIntent intentExecuted = PendingIntent.getBroadcast(ctx, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
                intentExecuted.cancel();
                Intent eventService = new Intent(ctx, OCSAgentService.class);
                if (ctx.stopService(eventService)) {
                    Toast.makeText(ctx, "Service stopped", Toast.LENGTH_LONG).show();
                }
            }
        }

        mfreqwakeChg = false;
        mAutoModeChg = false;
    }

    private class MyPreferenceChangeListener implements OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            Log.v("PreferenceChange", key);
            if ("k_automode".equals(key)) {
                mAutoModeChg = true;
            }

            if ("k_freqwake".equals(key)) {
                Log.v("PreferenceChange", "**** KEY test_preference_key modified ****");
                mfreqwakeChg = true;
            }
        }
    }

    private static void bindPreferenceSummaryToValue(Preference pref) {
        // Set the listener to watch for value changes.
        pref.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(pref, PreferenceManager.getDefaultSharedPreferences(
                pref.getContext()).getString(pref.getKey(), ""));
    }

    /**
     * A preference value change listener that updates the preference's summary to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference
            .OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            Log.v("onPreferenceChange : ", value.toString());
            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
            } else if (preference instanceof EditTextPreference) {
                if ("k_freqmaj".equals(preference.getKey())) {
                    stringValue = stringValue + " " + uHour;
                }
                if ("k_freqwake".equals(preference.getKey())) {
                    stringValue = stringValue + " " + uMn;
                }

                preference.setSummary(stringValue);
            }
            return true;
        }
    };
}

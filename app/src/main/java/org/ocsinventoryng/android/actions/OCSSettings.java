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
package org.ocsinventoryng.android.actions;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import org.ocsinventoryng.android.agent.R;

public class OCSSettings {
    private static OCSSettings instance = null;

    private Context ctx;
    private SharedPreferences prefs;

    private final String KLASTUPDT = "k_lastupdt";
    private final String KDEVICEUID = "k_deviceUid";

    private final String KSERVERURL = "k_serverurl";
    private final String KDEVICETAG = "k_devicetag";
    private final String KAUTOMODE = "k_automode";
    private final String KAUTOMODENETWORK = "k_automodeNetwork";
    private final String KFREQMAJ = "k_freqmaj";
    private final String KFREQWAKE = "k_freqwake";
    private final String KDEBUG = "k_debug";
    private final String KGZIP = "k_gzip";
    private final String KSTRICTSSL = "k_strictssl";
    private final String KPROXY = "k_proxy";
    private final String KPROXYADR = "k_proxyadr";
    private final String KPROXYPORT = "k_proxyport";
    private final String KCACHE = "k_cache";
    private final String KCACHELEN = "k_cachelen";

    private final String KAUTH = "k_auth";
    private final String KLOGIN = "k_login";
    private final String KPASSWD = "k_passwd";
    private final String KSYSHIDE = "k_syshide";
    private final String KCOMPUA = "k_compua";
    private final String KHIDENOTIF = "k_hideNotif";

    public OCSSettings(Context ctx) {
        //prefs = act.getSharedPreferences(LOGTAG, Context.MODE_PRIVATE);
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        this.ctx = ctx;
    }

    public void logSettings() {
        OCSLog ocslog = OCSLog.getInstance();
        if (ocslog == null) {
            return;
        }
        ocslog.debug("deviceUid : " + getDeviceUid());
        ocslog.debug("debug     : " + getDebug());
        ocslog.debug("autostart : " + isAutoMode());
        ocslog.debug("serverURL : " + getServerUrl());
        ocslog.debug("gzip      : " + getGzip());
        ocslog.debug("TAG       : " + getDeviceTag());
        ocslog.debug("STRICTSSL : " + isSSLStrict());
    }

    public static OCSSettings getInstance(Context ctx) {
        if (instance == null) {
            instance = new OCSSettings(ctx);
        }
        return instance;
    }

    public static OCSSettings getInstance() {
        return instance;
    }

    public void setDeviceUid(String uid) {
        Editor e = prefs.edit();
        e.putString(KDEVICEUID, uid);
        e.apply();
    }

    public void setLastUpdt(long l) {
        Editor e = prefs.edit();
        e.putLong(KLASTUPDT, l);
        e.apply();
    }

    public void setFreqMaj(String f) {
        Editor e = prefs.edit();
        e.putString(KFREQMAJ, f);
        e.apply();
    }


    public boolean isAutoMode() {
        return prefs.getBoolean(KAUTOMODE, false);
    }

    public int getAutoModeNetwork() {
        String p = prefs.getString(KAUTOMODENETWORK, "");
        return Integer.parseInt(p);
    }

    public int getHiddenNotif() {
        String p = prefs.getString(KHIDENOTIF, "");
        return Integer.parseInt(p);
    }

    public String getDeviceUid() {
        return prefs.getString(KDEVICEUID, null);
    }

    public boolean getDebug() {
        return prefs.getBoolean(KDEBUG, false);
    }

    public String getServerUrl() {
        return prefs.getString(KSERVERURL, ctx.getString(R.string.pref_default_serverurl));
    }

    public boolean getGzip() {
        return prefs.getBoolean(KGZIP, false);
    }

    public String getDeviceTag() {

        return prefs.getString(KDEVICETAG, ctx.getString(R.string.pref_default_devicetag));
    }

    public boolean isSSLStrict() {
        return prefs.getBoolean(KSTRICTSSL, true);
    }

    public boolean isProxy() {
        return prefs.getBoolean(KPROXY, false);
    }

    public boolean isAuth() {
        return prefs.getBoolean(KAUTH, false);
    }

    public String getProxyAdr() {
        return prefs.getString(KPROXYADR, "");
    }

    public int getProxyPort() {
        String p = prefs.getString(KPROXYPORT, "");
        return Integer.parseInt(p);
    }

    public String getLogin() {
        return prefs.getString(KLOGIN, "");
    }

    public String getPasswd() {
        return prefs.getString(KPASSWD, "");
    }

    public long getLastUpdt() {
        return prefs.getLong(KLASTUPDT, 0L);
    }

    public int getCacheLen() {
        int c = 0; // Return 0 if cache not enabled
        if (prefs.getBoolean(KCACHE, true)) {
            c = Integer.parseInt(prefs.getString(KCACHELEN, ctx.getString(R.string.pref_default_cachelen)));
        }
        return c;
    }

    public int getFreqWake() {
        int r = 60;
        if (prefs.getBoolean(KAUTOMODE, true)) {
            r = Integer.parseInt(prefs.getString(KFREQWAKE, ctx.getString(R.string.pref_default_freqwake)));
        }
        return r;
    }

    public int getFreqMaj() {
        return Integer.parseInt(prefs.getString(KFREQMAJ, ctx.getString(R.string.pref_default_freqmaj)));
    }

    public boolean isSysHide() {
        return prefs.getBoolean(KSYSHIDE, true);
    }

    public boolean isCompUAEnabled() {
        return prefs.getBoolean(KCOMPUA, false);
    }
}

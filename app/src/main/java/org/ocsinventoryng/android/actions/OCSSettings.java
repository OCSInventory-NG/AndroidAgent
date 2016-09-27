package org.ocsinventoryng.android.actions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import org.ocsinventoryng.android.agent.R;

@SuppressLint("NewApi")
public class OCSSettings {
    private static OCSSettings instance = null;

    private Context ctx;
    private SharedPreferences prefs;

    final String KLASTUPDT = "k_lastupdt";
    final String KDEVICEUID = "k_deviceUid";

    final String KSERVERURL = "k_serverurl";
    final String KDEVICETAG = "k_devicetag";
    final String KAUTOMODE = "k_automode";
    final String KAUTOMODENETWORK = "k_automodeNetwork";
    final String KFREQMAJ = "k_freqmaj";
    final String KFREQWAKE = "k_freqwake";
    final String KDEBUG = "k_debug";
    final String KGZIP = "k_gzip";
    final String KSTRICTSSL = "k_strictssl";
    final String KPROXY = "k_proxy";
    final String KPROXYADR = "k_proxyadr";
    final String KPROXYPORT = "k_proxyport";
    final String KCACHE = "k_cache";
    final String KCACHELEN = "k_cachelen";

    final String KAUTH = "k_auth";
    final String KLOGIN = "k_login";
    final String KPASSWD = "k_passwd";
    final String KSYSHIDE = "k_syshide";
    final String KCOMPUA = "k_compua";
    final String KHIDENOTIF = "k_hideNotif";

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
        int i = Integer.parseInt(p);
        return i;
    }

    public int getHiddenNotif() {
        String p = prefs.getString(KHIDENOTIF, "");
        int i = Integer.parseInt(p);
        return i;
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
        int i = Integer.parseInt(p);
        return i;
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

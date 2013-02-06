package org.ocsinventory.android.actions;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

@SuppressLint("NewApi")
public class OCSSettings
{	
	private static OCSSettings instance = null;	
	SharedPreferences prefs;
	final String KLASTUPDT = "k_lastupdt";
	final String KDEVICEUID = "k_deviceUid";
	
	final String KSERVERURL = "k_serverurl";
	final String KDEVICETAG = "k_devicetag";
	final String KAUTOMODE = "k_automode";
	final String KFREQMAJ = "k_freqmaj";
	final String KFREQWAKE = "k_freqwake";
	final String KDEBUG = "k_debug";
	final String KGZIP = "k_gzip";
	final String KSTRICTSSL = "k_strictssl";	
	final String KPROXY = "k_proxyl";
	final String KPROXYADR = "k_proxyadr";
	final String KPROXYPORT = "k_proxyport";
	final String KCACHE = "k_cache";
	final String KCACHELEN = "k_cachelen";
	
	final String KAUTH = "k_auth";
	final String KLOGIN = "k_login";	
	final String KPASSWD = "k_passwd";
	
	public OCSSettings(Context ctx)
	{
		//prefs = act.getSharedPreferences(LOGTAG, Context.MODE_PRIVATE);
		prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		
	}
	public void setDefault() {
	}
	
	public void logSettings() {
		OCSLog ocslog = OCSLog.getInstance();
		if ( ocslog == null )
			return;
		ocslog.append("deviceUid : "+getDeviceUid());
		ocslog.append("debug     : "+getDebug());
		ocslog.append("autostart : "+isAutoMode());
		ocslog.append("serverURL : "+getServerUrl());
		ocslog.append("gzip      : "+getGzip());
		ocslog.append("TAG       : "+getDeviceTag());
		ocslog.append("STRICTSSL : "+isSSLStrict());
	}

	public static OCSSettings getInstance(Context ctx) {
		if (instance == null)
			instance = new OCSSettings(ctx);
		return instance;
	}
	public static OCSSettings getInstance() {
		return instance;
	}
	public void setDeviceUid(String uid) {
		Editor e = prefs.edit();
		e.putString(KDEVICEUID,uid);
		e.commit();
	}
	public void setLastUpdt(long l) {
		Editor e = prefs.edit();
		e.putLong(KLASTUPDT,l);
		e.commit();
	}
	public void setFreqMaj(String f) {
		Editor e = prefs.edit();
		e.putString(KFREQMAJ,f);
		e.commit();
	}
	

	public boolean isAutoMode() {
		return prefs.getBoolean(KAUTOMODE, false);
	}

	public String getDeviceUid() {
		return prefs.getString(KDEVICEUID, null);
	}

	public boolean getDebug() {
		return prefs.getBoolean(KDEBUG, false);
	}

	public String getServerUrl() {
		return prefs.getString(KSERVERURL,"");
	}
	public boolean getGzip() {
		return prefs.getBoolean(KGZIP, false);
	}
	public String getDeviceTag() {
		return prefs.getString(KDEVICETAG,"Mobile");
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
		return prefs.getString(KPROXYADR,"");
	}
	public int getProxyPort() {
		String p =  prefs.getString(KPROXYPORT,"");
		int i = Integer.parseInt(p);
		return i;
	}
	public String getLogin() {
		return prefs.getString(KLOGIN,"");
	}
	public String getPasswd() {
		return prefs.getString(KPASSWD,"");
	}
	public long getLastUpdt() {
		return prefs.getLong(KLASTUPDT,0L);
	}
	public int getCacheLen() {
		int c = 0 ;
		if ( prefs.getBoolean(KCACHE, true))
			c= Integer.parseInt(prefs.getString(KCACHELEN, "0"));
		return c;
	}
	public int getFreqWake() {
		int r = 60 ;
		if ( prefs.getBoolean(KAUTOMODE, true))
			r= Integer.parseInt(prefs.getString(KFREQWAKE, "60"));
		return r;
	}
	public int getFreqMaj() {
		return Integer.parseInt(prefs.getString(KFREQMAJ, "10"));
	}
}

package org.ocsinventory.android.actions;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.ocsinventory.android.sections.OCSBios;
import org.ocsinventory.android.sections.OCSDrives;
import org.ocsinventory.android.sections.OCSHardware;
import org.ocsinventory.android.sections.OCSInputs;
import org.ocsinventory.android.sections.OCSNetwork;
import org.ocsinventory.android.sections.OCSNetworks;
import org.ocsinventory.android.sections.OCSSoftwares;
import org.ocsinventory.android.sections.OCSStorages;
import org.ocsinventory.android.sections.OCSVideos;
import org.ocsinventory.android.sections.SystemInfos;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class Inventory {
	private static Inventory instance = null;
		
	private static Date lastDate;
	private static long dureeCache = 300L;
	
	
	private String deviceUid;
	
	private OCSBios bios;
	private OCSHardware hardware;
	private OCSNetworks networks;
	private OCSDrives drives;
	private OCSStorages storages;
	private OCSSoftwares softwares;
	private OCSVideos videos;
	private OCSInputs inputs;

	private OCSLog ocslog;
	
	public Inventory(Context act) {
		BuildInventory(act);
	}	
	public void BuildInventory(Context ctx) {
		
		ocslog = OCSLog.getInstance();
		ocslog.append("SystemInfos.InitSystemInfos...");
		OCSSettings settings = OCSSettings.getInstance();
		
		lastDate = new Date();
		
		dureeCache =  settings.getCacheLen();
		
		SystemInfos.InitSystemInfos();

		ocslog.append("OCSBios...");
		this.bios= new OCSBios();
		ocslog.append("hardware...");
		this.hardware= new OCSHardware();
		String sid = Secure.getString(ctx.getContentResolver(),Secure.ANDROID_ID);
		this.hardware.setName(this.hardware.getName()+"-"+sid);
		ocslog.append("OCSNetworks...");
		this.networks=new OCSNetworks(ctx);
		if ( ! networks.getNetworks().isEmpty() ) {
			int m = networks.getMain();
			OCSNetwork pn =  networks.getNetworks().get(m);
			hardware.setIpAddress(pn.getIpAdress());
		}
		
		ocslog.append("drives...");
		this.drives=new OCSDrives();
		this.storages=new OCSStorages();
		
		if ( settings.getDeviceUid() == null ) {
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			this.deviceUid = "android-"+Secure.getString(ctx.getContentResolver(),
	                Secure.ANDROID_ID)+"-"+sdf.format(now);
			settings.setDeviceUid(this.deviceUid);
		} else
			this.deviceUid = settings.getDeviceUid();

		ocslog.append("OCSVideos...");
		this.videos = new OCSVideos(ctx);
		ocslog.append("OCSSoftwares...");
		this.softwares = new OCSSoftwares(ctx);
		this.inputs=new OCSInputs(ctx.getApplicationContext());
	}

	public static Inventory getInstance(Context ctx) {
		if (instance == null)
			instance = new Inventory(ctx);
		else {
			Date now = new Date();
			long d = (now.getTime() - lastDate.getTime())/60000L;
			android.util.Log.d("OCS","Age du cache (mn) = "+d );
			if (  d  > dureeCache ) {
				android.util.Log.d("OCS","REFRESH" );
				instance = new Inventory(ctx);
			}
		}
		return instance;
	}

	public void logInventory() {
			ocslog.append("****LOG INVENTORY****");
			ocslog.append(this.toString());
	}

	public String getDeviceUid() {
		return this.deviceUid;
	}

	public void setDeviceUid(String paramString) {
		this.deviceUid = paramString;
	}

	public OCSBios getBios() {
		return bios;
	}
	public OCSHardware getHardware() {
		return hardware;
	}
	public OCSDrives getDrives() {
		return drives;
	}
	public String toXML() {

		StringBuffer strOut = new StringBuffer("<REQUEST>\n");
		Utils.xmlLine(strOut,2,"DEVICEID",this.getDeviceUid());
		// Utils.xmlLine(strOut,2,"DEVICEID",OCSSettings.getInstance().getDeviceUid());
		strOut.append("  <CONTENT>\n");
		// strOut.append("    <DOWNLOAD><HISTORY /></DOWNLOAD>\n");
		strOut.append(this.bios.toXML());
		strOut.append(this.drives.toXML());
		strOut.append(this.hardware.toXML());
		strOut.append(this.networks.toXML());
		// strOut.append("    <CONTROLLERS></CONTROLLERS>");
		// strOut.append("    <SLOTS></SLOTS>");
		// strOut.append("    <SOUNDS></SOUNDS>");
		strOut.append(this.softwares.toXML());
		strOut.append(this.storages.toXML());
		strOut.append(this.videos.toXML()); 
		strOut.append("    <ACCOUNTINFO>\n");
		strOut.append("      <KEYNAME>TAG</KEYNAME>\n");
		Utils.xmlLine(strOut,"KEYVALUE", OCSSettings.getInstance().getDeviceTag());
		strOut.append("    </ACCOUNTINFO>\n");
		strOut.append("  </CONTENT>\n");
		strOut.append("  <QUERY>INVENTORY</QUERY>\n");
		strOut.append("</REQUEST>");

		return strOut.toString();
	}
	public String toString() {

		StringBuffer strOut = new StringBuffer();
		strOut.append(this.getDeviceUid()).append('\n');;
		strOut.append(this.bios);
		strOut.append(this.drives);
		strOut.append(this.storages);
		strOut.append(this.hardware);
		strOut.append(this.networks);
		strOut.append(this.videos);
		strOut.append(this.softwares);

		return strOut.toString();
	}
}

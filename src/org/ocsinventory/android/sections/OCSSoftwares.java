package org.ocsinventory.android.sections;

import java.util.ArrayList;
import java.util.List;

import org.ocsinventory.android.actions.OCSLog;
import org.ocsinventory.android.actions.OCSSettings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.content.pm.ProviderInfo;
import android.os.Build;
import android.text.format.DateFormat;

@SuppressLint("NewApi")
public class OCSSoftwares implements OCSSectionInterface
{
	final private String sectionTag = "SOFTWARES";
	
	public ArrayList<OCSSoftware> softs;
	private OCSLog ocslog;	  
	public OCSSoftwares(Context ctx) {
		ocslog = OCSLog.getInstance();
		this.softs= new ArrayList<OCSSoftware>();
		
		PackageManager pm = ctx.getPackageManager () ;
		List<PackageInfo> pis  = ctx.getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES |
				PackageManager.GET_PROVIDERS);
		for (PackageInfo pi : pis) { 
			// Exclude systeme softwares i required
			if ( OCSSettings.getInstance(ctx).isSysHide() )
			if ( (pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1 )
				continue;
       		OCSSoftware oSoft = new OCSSoftware();
	        try {
	            PackageInfo lpInfo = pm.getPackageInfo (pi.packageName, PackageManager.GET_ACTIVITIES|
	            		PackageManager.GET_PROVIDERS);

	            ocslog.append("PKG name         "+lpInfo.packageName);
	            ocslog.append("PKG version      "+ String.valueOf(lpInfo.versionCode));
	            ocslog.append("PKG version name "+ lpInfo.versionName);
	    		oSoft.version = lpInfo.versionName;
	    		oSoft.publisher=lpInfo.packageName;
	        }
	        catch (NameNotFoundException e) {
	        	ocslog.append("Error :"+e.getMessage ()) ;
	        }
	        PackageStats stats = new PackageStats(pi.packageName);
	        ocslog.append("PKG size    "+ String.valueOf(stats.codeSize));
	        ocslog.append("PKG folder  "+ pi.applicationInfo.dataDir);
	        oSoft.filesize=String.valueOf(stats.codeSize);
	        oSoft.folder=pi.applicationInfo.dataDir;
	        
            if ( pi.applicationInfo.name != null  )
            	oSoft.name = pi.applicationInfo.name;
            else
            	if (pi.applicationInfo.className != null )
            		oSoft.name = pi.applicationInfo.className;
            	else {
            		String v[] = oSoft.publisher.split("\\.");
            		if ( v.length > 0)
            			oSoft.name = v[v.length-1];
            		else 
            			oSoft.name = oSoft.publisher;
            	}
            ocslog.append("PKG appname "+ oSoft.name);
            
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO ) {
    			String datei = (String) DateFormat.format("MM/dd/yy mm:ss", pi.firstInstallTime);
    			ocslog.append("PKG INSTALL :"+ datei);
    			oSoft.installDate=datei;
    		}
            ProviderInfo[] provsi = pi.providers;
            
            if ( provsi != null ) {
            	for ( int i = 0; i < provsi.length; i++  ) {
            		ocslog.append("PKG Provider "+ provsi[i].authority);
            		if ( provsi[i].descriptionRes != 0 )
            			ocslog.append("PKG Desc "+ String.valueOf(provsi[i].descriptionRes));
            	}
            	if ( provsi.length > 0 )
            		oSoft.publisher=provsi[0].authority;
            }
            softs.add(oSoft);
		}
	}

	public String toXML() {
		StringBuffer strOut = new StringBuffer();
		for ( OCSSoftware o : softs ) {
			strOut.append(o.toXml());
		}
		return strOut.toString();
	}
	public String toString() {
		StringBuffer strOut = new StringBuffer();
		for ( OCSSoftware o : softs ) {
			strOut.append(o.toString());
		}
		return strOut.toString();
	}
	public ArrayList<OCSSection> getSections() {
		ArrayList<OCSSection> lst = new ArrayList<OCSSection>();
		for ( OCSSoftware o : softs ) {
			lst.add(o.getSection());
		}
		return lst;
	}
	public String  getSectionTag() {
		return sectionTag;
	}
}
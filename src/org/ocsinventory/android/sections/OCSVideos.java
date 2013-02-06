package org.ocsinventory.android.sections;

import org.ocsinventory.android.actions.Utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/*
 *  
 */

public class OCSVideos {
	private String resolution;
	private String name;
	public OCSVideos(Context ctx) {
 
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		Display disp = wm.getDefaultDisplay();
		
		disp.getMetrics(localDisplayMetrics);
		this.resolution=String.valueOf(localDisplayMetrics.widthPixels)+"*"
				+String.valueOf(localDisplayMetrics.heightPixels);

	}
	public String toXML () {
		StringBuffer strOut = new StringBuffer();
		strOut.append("    <VIDEOS>\n");
		Utils.xmlLine(strOut,"NAME",this.name);
		Utils.xmlLine(strOut,"RESOLUTION",this.resolution);
		strOut.append("    </VIDEOS>\n");	
		return strOut.toString();
	}
	
	public String toString () {
		StringBuffer strOut = new StringBuffer();
		strOut.append("***VIDEO***\n");
		Utils.strLine(strOut,"RESOLUTION", resolution);
		return strOut.toString();
	}
	
}

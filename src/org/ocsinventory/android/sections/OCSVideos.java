package org.ocsinventory.android.sections;

import java.util.ArrayList;

import org.ocsinventory.android.actions.Utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/*
 *  
 */

public class OCSVideos implements OCSSectionInterface {
	
	final private String sectionTag = "VIDEOS";
	
	private String resolution;
	private String name;
	public OCSVideos(Context ctx) {
		this.name = "Embedded display";
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		Display disp = wm.getDefaultDisplay();
		disp.getMetrics(localDisplayMetrics);
		this.resolution=String.valueOf(localDisplayMetrics.widthPixels)+"*"
				+String.valueOf(localDisplayMetrics.heightPixels);

	}
	public OCSSection getSection() {
		  	OCSSection s = new OCSSection("VIDEOS");
			s.setAttr("NAME", this.name);
			s.setAttr("RESOLUTION",this.resolution);
			s.setTitle(this.name);
			return s;
	}		
	
	public String toXML() {
		return getSection().toXML();
	}
	
	public String toString() {
		return getSection().toString();
	}
	public ArrayList<OCSSection> getSections() {
		ArrayList<OCSSection> lst = new ArrayList<OCSSection>();
		lst.add(getSection());
		return lst;
	}	
	public String  getSectionTag() {
		return sectionTag;
	}
}

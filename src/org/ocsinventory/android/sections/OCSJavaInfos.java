package org.ocsinventory.android.sections;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;

import org.ocsinventory.android.actions.OCSLog;
import org.ocsinventory.android.actions.Utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.format.DateFormat;


public class OCSJavaInfos implements OCSSectionInterface {
	final private String sectionTag = "JAVAINFOS";

	private String javaname;
	private String javapathlevel;
	private String javacountry;
	private String javaclasspath;
	private String javahome;
	
	private OCSLog ocslog;  

	public OCSJavaInfos() {
 		ocslog = OCSLog.getInstance();
		Properties sp = System.getProperties();
		javaname=sp.getProperty("java.vm.name")+
				sp.getProperty("java.vm.version");
		javapathlevel="";
		javacountry=Locale.getDefault().getCountry();
		javaclasspath=sp.getProperty("java.class.path");
		javahome=sp.getProperty("java.home");
	}
	/*
	<!ELEMENT JAVAINFO (JAVANAME | JAVAPATHLEVEL | JAVACOUNTRY | JAVACLASSPATH | JAVAHOME)*>
	 * 
	 */
	public OCSSection getSection() {
		OCSSection s = new OCSSection(sectionTag);
		s.setAttr("JAVANAME", javaname);
		s.setAttr("JAVAPATHLEVEL", javapathlevel);
		s.setAttr("JAVACOUNTRY", javacountry);
		s.setAttr("JAVACLASSPATH", javaclasspath);
		s.setAttr("JAVAHOME", javahome);		
		s.setTitle(javaname);
		return s;
	}
	public ArrayList<OCSSection> getSections() {
		ArrayList<OCSSection> lst = new ArrayList<OCSSection>();
		lst.add(getSection());
		return lst;
	}
	
	public String toString() {
		return getSection().toString();
	}
	public String toXML() {
		return getSection().toXML();
	}
	public String  getSectionTag() {
		return sectionTag;
	}
}

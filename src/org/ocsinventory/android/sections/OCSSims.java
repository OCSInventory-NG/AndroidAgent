package org.ocsinventory.android.sections;

import java.util.ArrayList;

import org.ocsinventory.android.actions.OCSLog;

import android.content.Context;
import android.telephony.TelephonyManager;


public class OCSSims implements OCSSectionInterface {
	final private String sectionTag = "SIMS";
	private String simcountry;
	private String simoperator;
	private String simopname;
	private String simserial;
	
	
	private OCSLog ocslog;  

	public OCSSims(Context ctx) {
 		ocslog = OCSLog.getInstance();
 		TelephonyManager mng = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE); 
 		
 
 		simcountry  = mng.getSimCountryIso();
 		simoperator = mng.getSimOperator();
 		simopname = mng.getSimOperatorName();
 		simserial = mng.getSimSerialNumber();
	}
	/*
	 * 
	 * <!ELEMENT SIMS (NAME | MODEL | DESCRIPTION | TYPE)*>
	 */
	public OCSSection getSection() {
		OCSSection s = new OCSSection(sectionTag);
		s.setAttr("OPERATOR", simoperator);
		s.setAttr("OPNAME", simopname);
		s.setAttr("COUNTRY", simcountry);
		s.setAttr("SERIAL", simserial);		
		s.setTitle(simserial);
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

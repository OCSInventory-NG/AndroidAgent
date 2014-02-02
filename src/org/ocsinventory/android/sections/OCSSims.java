package org.ocsinventory.android.sections;

import java.util.ArrayList;

import org.ocsinventory.android.actions.OCSLog;

import android.content.Context;
import android.telephony.TelephonyManager;


public class OCSSims implements OCSSectionInterface {
	private OCSLog ocslog;
	final private String sectionTag = "SIM";
	private String simcountry;
	private String simoperator;
	private String simopname;
	private String simserial;
	private String device_id;

	public OCSSims(Context ctx) {
		ocslog = OCSLog.getInstance();
 		TelephonyManager mng = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE); 
 		ocslog.debug("Get TelephonyManager infos");
 		if ( mng == null ) {
 			ocslog.error("TelephonyManager information not found");
 		} else {
	 		device_id= mng.getDeviceId();
	 		simcountry  = mng.getSimCountryIso();
	 		simoperator = mng.getSimOperator();
	 		simopname = mng.getSimOperatorName();
	 		simserial = mng.getSimSerialNumber();
 			ocslog.debug("device_id : "+device_id);
 		}
	}
	/*
	 * 
	 * <!ELEMENT SIM (OPERATOR | OPNAME | COUNTRY | SERIALNUMBER | DEVICEID)*>
	 */
	public OCSSection getSection() {
		OCSSection s = new OCSSection(sectionTag);
		s.setAttr("OPERATOR", simoperator);
		s.setAttr("OPNAME", simopname);
		s.setAttr("COUNTRY", simcountry);
		s.setAttr("SERIALNUMBER", simserial);
		s.setAttr("DEVICEID", device_id);
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

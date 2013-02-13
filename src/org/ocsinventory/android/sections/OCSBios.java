package org.ocsinventory.android.sections;

import org.ocsinventory.android.actions.OCSLog;
import org.ocsinventory.android.actions.Utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.format.DateFormat;

/*
 * 
     <ASSETTAG>Asset-1234567890</ASSETTAG>
      <BDATE>03/26/2008</BDATE>
      <BMANUFACTURER>American Megatrends Inc.</BMANUFACTURER>
      <BVERSION>0802</BVERSION>
      <MMANUFACTURER>ASUSTeK Computer INC.</MMANUFACTURER>
      <MMODEL>P5KPL-VM</MMODEL>
      <MSN>MT7084K06409125</MSN>
      <SMANUFACTURER>System manufacturer</SMANUFACTURER>
      <SMODEL>System Product Name</SMODEL>
      <SSN>System Serial Number</SSN>
 * 
 * 
 
BIOS (BDATE | BMANUFACTURER | BVERSION | SMANUFACTURER | SMODEL | SSN | TYPE | ASSETTAG)
*/
public class OCSBios {
	private String assettag;
	private String date;
	private String manufacturer;
	private String smanufacturer;
	private String version;
	private String serial;
	private String type;
	private String smodel;
	
	private OCSLog ocslog;

	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public OCSBios() {
		type = "Mobile";
		ocslog = OCSLog.getInstance();
		assettag = Build.ID+"-0123456789";
		date = (String) DateFormat.format("MM/dd/yy", Build.TIME);
		manufacturer = Build.MANUFACTURER;
		version = Build.BOOTLOADER;
		ocslog.append("BIOS version: "+version);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO ) {
			serial = Build.SERIAL;
		} else {
			SystemInfos.getInstance().getSerial();
		}
		ocslog.append("OCSBIOS serial "+ serial);
		smodel = Build.MODEL;
		ocslog.append("OCSBIOS model: "+ smodel);
	}
	/*
	<!ELEMENT BIOS (BDATE | BMANUFACTURER | BVERSION | SMANUFACTURER | SMODEL | SSN | TYPE | ASSETTAG)*>
	*/

	public String toXML () {
		StringBuffer strOut = new StringBuffer();
		strOut.append("    <BIOS>\n");
		Utils.xmlLine(strOut,"ASSETTAG", assettag);
		Utils.xmlLine(strOut,"BDATE", date);
		Utils.xmlLine(strOut,"BMANUFACTURER", manufacturer);
		Utils.xmlLine(strOut,"BVERSION", version);		
		Utils.xmlLine(strOut,"MMANUFACTURER", manufacturer);
		Utils.xmlLine(strOut,"MSN", serial);
		Utils.xmlLine(strOut,"SMANUFACTURER", manufacturer);
		Utils.xmlLine(strOut,"SMODEL", smodel);
		Utils.xmlLine(strOut,"SSN", serial);
		Utils.xmlLine(strOut,"TYPE", type);
		strOut.append("    </BIOS>\n");	
		return strOut.toString();
	}
	
	public String toString () {
		StringBuffer strOut = new StringBuffer("***BIOS***\n");
		Utils.strLine(strOut,"ASSETTAG", assettag);
		Utils.strLine(strOut,"BDATE", date);
		Utils.strLine(strOut,"BMANUFACTURER", manufacturer);
		Utils.strLine(strOut,"BVERSION", version);		
		Utils.strLine(strOut,"MMANUFACTURER", manufacturer);
		Utils.strLine(strOut,"MSN", serial);
		Utils.strLine(strOut,"SMANUFACTURER", manufacturer);
		Utils.strLine(strOut,"SMODEL", smodel);
		Utils.strLine(strOut,"SSN", serial);			
		return strOut.toString();
	}
	
}

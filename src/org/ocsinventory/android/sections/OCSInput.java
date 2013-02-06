package org.ocsinventory.android.sections;

import java.io.File;

import org.ocsinventory.android.actions.Utils;

import android.os.StatFs;
import android.text.format.DateFormat;

public class OCSInput {
	String type;
	String manufacturer;
	String caption;
	String description;
	String interf;
	String pointtype;	 
	
	public OCSInput() {

		this.type=null;
		this.manufacturer="NA";
		this.caption="NA";
		this.description="NA";
		this.interf=null;
		this.pointtype=null;;
		
	}

/*
	<!ELEMENT INPUTS (TYPE | MANUFACTURER | CAPTION | DESCRIPTION | INTERFACE | INTERFACE)*>
*/

	public String toXml() {
		StringBuffer strOut = new StringBuffer();
		strOut.append("    <INPUTS>\n");
		Utils.xmlLine(strOut, "TYPE", type);
		Utils.xmlLine(strOut, "MANUFACTURER", manufacturer);
		Utils.xmlLine(strOut, "CAPTION", caption);
		Utils.xmlLine(strOut, "DESCRIPTION", description);
		Utils.xmlLine(strOut, "INTERFACE",interf);
		Utils.xmlLine(strOut, "POINTERTYPE", pointtype);
		strOut.append("    </INPUTS>\n");
		return strOut.toString();
	}
	public String toString() {
		StringBuffer strOut = new StringBuffer();
		strOut.append("*INPUTS*\n");
		Utils.strLine(strOut, "TYPE", type);
		Utils.strLine(strOut, "MANUFACTURER", manufacturer);
		Utils.strLine(strOut, "CAPTION", caption);
		Utils.strLine(strOut, "DESCRIPTION", description);
		Utils.strLine(strOut, "INTERFACE", interf);
		Utils.strLine(strOut, "POINTERTYPE", pointtype);
		return strOut.toString();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getInterf() {
		return interf;
	}

	public void setInterf(String interf) {
		this.interf = interf;
	}

	public String getPointtype() {
		return pointtype;
	}

	public void setPointtype(String pointtype) {
		this.pointtype = pointtype;
	}

}
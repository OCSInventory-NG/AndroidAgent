/*
 * Copyright 2013-2016 OCSInventory-NG/AndroidAgent contributors : mortheres, cdpointpoint,
 * Cédric Cabessa, Nicolas Ricquemaque, Anael Mobilia
 *
 * This file is part of OCSInventory-NG/AndroidAgent.
 *
 * OCSInventory-NG/AndroidAgent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * OCSInventory-NG/AndroidAgent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OCSInventory-NG/AndroidAgent. if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.ocs.android.sections;

import android.os.Build;
import android.text.format.DateFormat;

import org.ocs.android.actions.OCSLog;

import java.util.ArrayList;

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
public class OCSBios implements OCSSectionInterface {

    final private String sectionTag = "BIOS";

    private String assettag;
    private String date;
    private String manufacturer;
    private String version;
    private String serial;
    private String type;
    private String smodel;

    public OCSBios() {
        type = "Mobile";
        OCSLog ocslog = OCSLog.getInstance();
        assettag = Build.ID + "-0123456789";
        date = (String) DateFormat.format("MM/dd/yy", Build.TIME);
        manufacturer = Build.MANUFACTURER;
        version = Build.BOOTLOADER;
        ocslog.debug("BIOS version: " + version);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            serial = Build.SERIAL;
        } else {
            serial = SystemInfos.getInstance().getSerial();
        }
        ocslog.debug("OCSBIOS serial " + serial);
        smodel = Build.MODEL;
        ocslog.debug("OCSBIOS model: " + smodel);
    }
    /*
    <!ELEMENT BIOS (BDATE | BMANUFACTURER | BVERSION | SMANUFACTURER | SMODEL | SSN | TYPE | ASSETTAG)*>
	*/

    public OCSSection getSection() {
        OCSSection s = new OCSSection(sectionTag);
        s.setAttr("TYPE", type);
        s.setAttr("ASSETTAG", assettag);
        s.setAttr("BDATE", date);
        s.setAttr("BMANUFACTURER", manufacturer);
        s.setAttr("BVERSION", version);
        s.setAttr("MMANUFACTURER", manufacturer);
        s.setAttr("MSN", serial);
        s.setAttr("SMANUFACTURER", manufacturer);
        s.setAttr("SMODEL", smodel);
        s.setAttr("SSN", serial);
        s.setTitle(assettag);
        return s;
    }

    public ArrayList<OCSSection> getSections() {
        ArrayList<OCSSection> lst = new ArrayList<>();
        lst.add(getSection());
        return lst;
    }

    public String toString() {
        return getSection().toString();
    }

    public String toXML() {
        return getSection().toXML();
    }

    public String getSectionTag() {
        return sectionTag;
    }
}

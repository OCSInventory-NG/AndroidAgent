/*
 * Copyright 2013-2016, OCSInventory-NG/AndroidAgent contributors
 *
 * This file is part of OCSInventory-NG/AndroidAgent.
 *
 * OCSInventory-NG/AndroidAgent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OCSInventory-NG/AndroidAgent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OCSInventory-NG/AndroidAgent. If not, see <http://www.gnu.org/licenses/>
 */
package org.ocsinventoryng.android.sections;

import android.content.Context;
import android.telephony.TelephonyManager;

import org.ocsinventoryng.android.actions.OCSLog;

import java.util.ArrayList;


public class OCSSims implements OCSSectionInterface {
    final private String sectionTag = "SIM";
    private String simcountry;
    private String simoperator;
    private String simopname;
    private String simserial;
    private String device_id;

    public OCSSims(Context ctx) {
        OCSLog ocslog = OCSLog.getInstance();
        TelephonyManager mng = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        ocslog.debug("Get TelephonyManager infos");
        if (mng == null) {
            ocslog.error("TelephonyManager information not found");
        } else {
            device_id = mng.getDeviceId();
            simcountry = mng.getSimCountryIso();
            simoperator = mng.getSimOperator();
            simopname = mng.getSimOperatorName();
            simserial = mng.getSimSerialNumber();
            ocslog.debug("device_id : " + device_id);
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

    public String getSectionTag() {
        return sectionTag;
    }
}

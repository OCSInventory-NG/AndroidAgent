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

import android.content.Context;
import android.telephony.TelephonyManager;

import org.ocs.android.actions.OCSLog;

import java.util.ArrayList;

import android.text.TextUtils;

public class OCSSims implements OCSSectionInterface {
    final private String sectionTag = "SIM";
    private String simcountry;
    private String simoperator;
    private String simopname;
    private String simserial;
    private String device_id;
    private String phonenumber;

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
            simopname = TextUtils.htmlEncode(mng.getSimOperatorName().toString());
            simserial = mng.getSimSerialNumber();
            phonenumber = mng.getLine1Number();
            ocslog.debug("device_id : " + device_id);
        }
    }

    /*
     *
     * <!ELEMENT SIM (OPERATOR | OPNAME | COUNTRY | SERIALNUMBER | DEVICEID | PHONENUMBER)*>
     */
    public OCSSection getSection() {
        OCSSection s = new OCSSection(sectionTag);
        s.setAttr("OPERATOR", simoperator);
        s.setAttr("OPNAME", simopname);
        s.setAttr("COUNTRY", simcountry);
        s.setAttr("SERIALNUMBER", simserial);
        s.setAttr("DEVICEID", device_id);
        s.setAttr("PHONENUMBER", phonenumber);
        s.setTitle(simserial);
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

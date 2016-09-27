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

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Build;

import org.ocsinventoryng.android.actions.OCSLog;

import java.util.ArrayList;


public class OCSPorts implements OCSSectionInterface {
    final private String sectionTag = "PORTS";
    private ArrayList<OCSPort> ports;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public OCSPorts(Context ctx) {
        OCSLog ocslog = OCSLog.getInstance();

        this.ports = new ArrayList<OCSPort>();

        UsbManager usbMgr = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);

        usbMgr.getDeviceList();

        ocslog.debug("OCSInputs");
    }

    public String toXML() {
        StringBuilder strOut = new StringBuilder();
        for (OCSPort o : ports) {
            strOut.append(o.toXml());
        }
        return strOut.toString();
    }

    public String toString() {
        StringBuilder strOut = new StringBuilder();
        for (OCSPort o : ports) {
            strOut.append(o.toString());
        }
        return strOut.toString();
    }

    public ArrayList<OCSSection> getSections() {
        ArrayList<OCSSection> lst = new ArrayList<OCSSection>();
        for (OCSPort o : ports) {
            lst.add(o.getSection());
        }
        return lst;
    }

    public String getSectionTag() {
        return sectionTag;
    }
}
package org.ocsinventoryng.android.sections;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Build;

import org.ocsinventoryng.android.actions.OCSLog;

import java.util.ArrayList;


public class OCSPorts implements OCSSectionInterface {
    final private String sectionTag = "PORTS";
    public ArrayList<OCSPort> ports;


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
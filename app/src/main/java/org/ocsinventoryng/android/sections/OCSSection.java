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
package org.ocsinventoryng.android.sections;

import org.ocsinventoryng.android.actions.OCSLog;

import java.util.HashMap;
import java.util.Map;

public class OCSSection {
    private String name;  // Section name ie BIOS
    private String titre; // Section title for display
    private Map<String, String> attrs;
    private OCSLog ocslog = OCSLog.getInstance();

    public OCSSection(String pName) {
        name = pName;
        attrs = new HashMap<>();
    }

    public void setAttr(String k, String v) {
        attrs.put(k, v);
    }

    public String toXML() {
        StringBuffer strOut = new StringBuffer("    <");
        strOut.append(name);
        strOut.append(">\n");
        for (String k : attrs.keySet()) {
            String v = attrs.get(k);
            xmlLine(strOut, k, v);
        }
        strOut.append("    </");
        strOut.append(name);
        strOut.append(">\n");
        return strOut.toString();
    }

    public String toString() {
        StringBuilder strOut = new StringBuilder("");
        for (String k : attrs.keySet()) {
            ocslog.debug("Key : " + k);
            String v = attrs.get(k);
            ocslog.debug("Val : " + v);
            if (v != null) {
                strOut.append(k).append(": ").append(v).append("\n");
            }
        }
        return strOut.toString();
    }

    public String getTitle() {
        return titre;
    }

    public void setTitle(String t) {
        titre = t;
    }

    private void xmlLine(StringBuffer sbOut, String tag, String val) {
        xmlLine(sbOut, 6, tag, val);
    }

    private void xmlLine(StringBuffer sbOut, int n, String tag, String val) {
        for (int i = 0; i < n; i++) {
            sbOut.append(' ');
        }
        if (val == null) {
            sbOut.append('<').append(tag).append("/>\n");
        } else {
            sbOut.append('<').append(tag).append('>').append(val).append("</").append(tag).append(">\n");
        }
    }
}

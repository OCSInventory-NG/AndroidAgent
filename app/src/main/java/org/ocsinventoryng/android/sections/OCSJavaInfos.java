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

import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;


public class OCSJavaInfos implements OCSSectionInterface {
    final private String sectionTag = "JAVAINFOS";

    private String javaname;
    private String javapathlevel;
    private String javacountry;
    private String javaclasspath;
    private String javahome;


    public OCSJavaInfos() {

        Properties sp = System.getProperties();
        javaname = sp.getProperty("java.vm.name") + sp.getProperty("java.vm.version");
        javapathlevel = "";
        javacountry = Locale.getDefault().getCountry();
        javaclasspath = sp.getProperty("java.class.path");
        javahome = sp.getProperty("java.home");
    }

    /*
    <!ELEMENT JAVAINFO (JAVANAME | JAVAPATHLEVEL | JAVACOUNTRY | JAVACLASSPATH | JAVAHOME)*>
     *
     */
    public OCSSection getSection() {
        OCSSection s = new OCSSection(sectionTag);
        s.setAttr("JAVANAME", javaname);
        s.setAttr("JAVAPATHLEVEL", javapathlevel);
        s.setAttr("JAVACOUNTRY", javacountry);
        s.setAttr("JAVACLASSPATH", javaclasspath);
        s.setAttr("JAVAHOME", javahome);
        s.setTitle(javaname);
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

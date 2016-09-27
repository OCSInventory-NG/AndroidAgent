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


public class OCSSoftware {
    public String comments;
    public String filesize;
    public String folder;
    public String installDate;
    public String name;
    public String publisher;
    public String version;

    public OCSSection getSection() {
        OCSSection s = new OCSSection("SOFTWARES");
        s.setAttr("PUBLISHER", publisher);
        s.setAttr("NAME", name);
        s.setAttr("VERSION", version);
        s.setAttr("FOLDER", folder);
        s.setAttr("FILESIZE", filesize);
        s.setAttr("COMMENTS", "");
        s.setAttr("INSTALLDATE", "");
        s.setTitle(name);
        return s;
    }

    public String toXml() {
        return getSection().toXML();
    }

    public String toString() {
        return getSection().toString();
    }
}
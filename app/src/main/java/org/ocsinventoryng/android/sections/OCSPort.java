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


public class OCSPort {
    private String type;
    private String manufacturer;
    private String caption;
    private String description;
    private String interf;
    private String pointtype;

    public OCSPort() {

        this.type = null;
        this.manufacturer = "NA";
        this.caption = "NA";
        this.description = "NA";
        this.interf = "";
        this.pointtype = "";
    }

/*
    <!ELEMENT INPUTS (TYPE | MANUFACTURER | CAPTION | DESCRIPTION | INTERFACE | POINTTYPE)*>
	INPUTS (TYPE | MANUFACTURER | CAPTION | DESCRIPTION | INTERFACE | POINTTYPE
*/

    public OCSSection getSection() {
        OCSSection s = new OCSSection("INPUTS");
        s.setAttr("TYPE", type);
        s.setAttr("MANUFACTURER", manufacturer);
        s.setAttr("CAPTION", caption);
        s.setAttr("DESCRIPTION", description);
        s.setAttr("INTERFACE", interf);
        s.setAttr("POINTTYPE", pointtype);
        s.setTitle(type);
        return s;
    }

    public String toString() {
        return getSection().toString();
    }

    public String toXml() {
        return getSection().toXML();
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
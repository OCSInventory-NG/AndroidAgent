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

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.util.ArrayList;

public class OCSVideos implements OCSSectionInterface {
    final private String sectionTag = "VIDEOS";

    private String resolution;
    private String name = "Embedded display";

    public OCSVideos(Context ctx) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        disp.getMetrics(localDisplayMetrics);
        resolution = String.valueOf(localDisplayMetrics.widthPixels) + "*" + String.valueOf(
                localDisplayMetrics.heightPixels);
    }

    public OCSSection getSection() {
        OCSSection s = new OCSSection("VIDEOS");
        s.setAttr("NAME", name);
        s.setAttr("RESOLUTION", resolution);
        s.setTitle(name);
        return s;
    }

    public String toXML() {
        return getSection().toXML();
    }

    public String toString() {
        return getSection().toString();
    }

    public ArrayList<OCSSection> getSections() {
        ArrayList<OCSSection> lst = new ArrayList<OCSSection>();
        lst.add(getSection());
        return lst;
    }

    public String getSectionTag() {
        return sectionTag;
    }
}

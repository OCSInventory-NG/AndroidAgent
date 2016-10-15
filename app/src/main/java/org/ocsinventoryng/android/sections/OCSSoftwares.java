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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.content.pm.ProviderInfo;
import android.os.Build;
import android.text.format.DateFormat;

import org.ocsinventoryng.android.actions.OCSLog;
import org.ocsinventoryng.android.actions.OCSSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class OCSSoftwares implements OCSSectionInterface {
    final private String sectionTag = "SOFTWARES";
    private ArrayList<OCSSoftware> softs;

    public OCSSoftwares(Context ctx) {
        OCSLog ocslog = OCSLog.getInstance();
        softs = new ArrayList<>();

        PackageManager pm = ctx.getPackageManager();
        List<PackageInfo> pis = ctx.getPackageManager().getInstalledPackages(
                PackageManager.GET_ACTIVITIES | PackageManager.GET_PROVIDERS);
        for (PackageInfo pi : pis) {
            // Exclude systeme softwares i required
            if (OCSSettings.getInstance(ctx).isSysHide() && (pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                continue;
            }
            OCSSoftware oSoft = new OCSSoftware();
            try {
                PackageInfo lpInfo = pm.getPackageInfo(pi.packageName,
                                                       PackageManager.GET_ACTIVITIES | PackageManager.GET_PROVIDERS);

                ocslog.debug("PKG name         " + lpInfo.packageName);
                ocslog.debug("PKG version      " + String.valueOf(lpInfo.versionCode));
                ocslog.debug("PKG version name " + lpInfo.versionName);
                oSoft.setVersion(lpInfo.versionName);
                oSoft.setPublisher(lpInfo.packageName);
            } catch (NameNotFoundException e) {
                ocslog.error("Error :" + e.getMessage());
            }
            PackageStats stats = new PackageStats(pi.packageName);
            ocslog.debug("PKG size    " + String.valueOf(stats.codeSize));
            ocslog.debug("PKG folder  " + pi.applicationInfo.dataDir);
            oSoft.setFilesize(String.valueOf(stats.codeSize));
            oSoft.setFolder(pi.applicationInfo.dataDir);

            if (pi.applicationInfo.name != null) {
                oSoft.setName(pi.applicationInfo.name);
            } else if (pi.applicationInfo.className != null) {
                oSoft.setName(pi.applicationInfo.className);
            } else {
                String v[] = oSoft.getPublisher().split("\\.");
                if (v.length > 0) {
                    oSoft.setName(v[v.length - 1]);
                } else {
                    oSoft.setName(oSoft.getPublisher());
                }
            }
            ocslog.debug("PKG appname " + oSoft.getName());

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
                String datei = (String) DateFormat.format("MM/dd/yy mm:ss", pi.firstInstallTime);
                ocslog.debug("PKG INSTALL :" + datei);
                oSoft.setInstallDate(datei);
            }
            ProviderInfo[] provsi = pi.providers;

            if (provsi != null) {
                for (ProviderInfo aProvsi : provsi) {
                    ocslog.debug("PKG Provider " + aProvsi.authority);
                    if (aProvsi.descriptionRes != 0) {
                        ocslog.debug("PKG Desc " + String.valueOf(aProvsi.descriptionRes));
                    }
                }
                if (provsi.length > 0) {
                    oSoft.setPublisher(provsi[0].authority);
                }
            }
            softs.add(oSoft);
        }
        Properties sp = System.getProperties();
        OCSSoftware jsoft = new OCSSoftware();
        jsoft.setName(sp.getProperty("java.vm.name"));
        jsoft.setVersion(sp.getProperty("java.vm.version"));
        jsoft.setFolder(sp.getProperty("java.home"));
        jsoft.setPublisher(sp.getProperty("java.vm.vendor"));
        jsoft.setFilesize("n.a");
        jsoft.setInstallDate("n.a.");
        softs.add(jsoft);
    }

    public String toXML() {
        StringBuilder strOut = new StringBuilder();
        for (OCSSoftware o : softs) {
            strOut.append(o.toXml());
        }
        return strOut.toString();
    }

    public String toString() {
        StringBuilder strOut = new StringBuilder();
        for (OCSSoftware o : softs) {
            strOut.append(o.toString());
        }
        return strOut.toString();
    }

    public ArrayList<OCSSection> getSections() {
        ArrayList<OCSSection> lst = new ArrayList<>();
        for (OCSSoftware o : softs) {
            lst.add(o.getSection());
        }
        return lst;
    }

    public String getSectionTag() {
        return sectionTag;
    }
}
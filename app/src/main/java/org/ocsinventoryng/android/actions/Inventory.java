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
package org.ocsinventoryng.android.actions;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;

import org.ocsinventoryng.android.sections.OCSBios;
import org.ocsinventoryng.android.sections.OCSDrives;
import org.ocsinventoryng.android.sections.OCSHardware;
import org.ocsinventoryng.android.sections.OCSInputs;
import org.ocsinventoryng.android.sections.OCSJavaInfos;
import org.ocsinventoryng.android.sections.OCSNetwork;
import org.ocsinventoryng.android.sections.OCSNetworks;
import org.ocsinventoryng.android.sections.OCSSection;
import org.ocsinventoryng.android.sections.OCSSectionInterface;
import org.ocsinventoryng.android.sections.OCSSims;
import org.ocsinventoryng.android.sections.OCSSoftwares;
import org.ocsinventoryng.android.sections.OCSStorages;
import org.ocsinventoryng.android.sections.OCSVideos;
import org.ocsinventoryng.android.sections.SystemInfos;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class Inventory {
    private static Inventory instance = null;
    private static String sectionsFPFile = "sectionsfp.txt";
    private Context mCtx;

    private Map<String, String> lastFP;
    private Map<String, String> currentFP;

    private static Date lastDate;
    private static long dureeCache = 300L;


    private String deviceUid;

    private OCSBios bios;
    private OCSHardware hardware;
    private OCSNetworks networks;
    private OCSDrives drives;
    private OCSStorages storages;
    private OCSSoftwares softwares;
    private OCSVideos videos;
    private OCSInputs inputs;
    private OCSJavaInfos javainfos;
    private OCSSims sims;

    private OCSLog ocslog;

    public Inventory(Context act) {
        BuildInventory(act);
    }

    public void BuildInventory(Context ctx) {

        ocslog = OCSLog.getInstance();
        ocslog.debug("SystemInfos.InitSystemInfos...");
        OCSSettings settings = OCSSettings.getInstance();

        lastDate = new Date();

        dureeCache = settings.getCacheLen();

        SystemInfos.InitSystemInfos();

        ocslog.debug("OCSBios...");
        this.bios = new OCSBios();
        ocslog.debug("hardware...");
        this.hardware = new OCSHardware();
        String sid = Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);
        this.hardware.setName(this.hardware.getName() + "-" + sid);
        ocslog.debug("OCSNetworks...");
        this.networks = new OCSNetworks(ctx);
        if (!networks.getNetworks().isEmpty()) {
            int m = networks.getMain();
            OCSNetwork pn = networks.getNetworks().get(m);
            hardware.setIpAddress(pn.getIpAdress());
        }

        ocslog.debug("OCSdrives...");
        this.drives = new OCSDrives();
        ocslog.debug("OCSStorages...");
        this.storages = new OCSStorages();

        if (settings.getDeviceUid() == null) {
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
            this.deviceUid = "android-" + Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID) + "-" + sdf.format(now);
            settings.setDeviceUid(this.deviceUid);
        } else {
            this.deviceUid = settings.getDeviceUid();
        }

        ocslog.debug("OCSVideos...");
        this.videos = new OCSVideos(ctx);
        ocslog.debug("OCSSoftwares...");
        this.softwares = new OCSSoftwares(ctx);
        ocslog.debug("OCSInputs...");
        this.inputs = new OCSInputs(ctx.getApplicationContext());
        ocslog.debug("OCSJavaInfos...");
        this.javainfos = new OCSJavaInfos();
        ocslog.debug("OCSSims...");
        this.sims = new OCSSims(ctx);

	/* Mise a jour du checksum */
        mCtx = ctx;
        ocslog.debug("CHECKSUM update");
        loadSectionsFP(ctx);
        currentFP = new Hashtable<String, String>();

        long checksum = 0L;
        checksum |= getChange(this.hardware, 1L);
        checksum |= getChange(this.bios, 2L);
        checksum |= getChange(this.storages, 0x100L);
        checksum |= getChange(this.drives, 0x200L);
        checksum |= getChange(this.inputs, 0x400L);
        checksum |= getChange(this.networks, 0x1000L);
        checksum |= getChange(this.videos, 0x8000L);
        checksum |= getChange(this.softwares, 0x10000L);
        checksum |= getChange(this.sims, 0x80000L);
        checksum |= getChange(this.javainfos, 0L);
        ocslog.debug(String.format("CK %x", checksum));
        ocslog.debug("CHECKSUM " + checksum);
        hardware.setChecksum(checksum);
    }

    private long getChange(OCSSectionInterface s, long mask) {
        long ret = 0;

        String finger = Utils.md5(s.toXML());
        String tag = s.getSectionTag();
        // Hash is hold until upload confirmation and then saved
        currentFP.put(tag, finger);
        if (lastFP.get(tag) == null || !finger.equals(lastFP.get(tag))) {
            ret = mask;
        }
        return ret;
    }

    public static Inventory getInstance(Context ctx) {
        if (instance == null) {
            instance = new Inventory(ctx);
        } else {
            Date now = new Date();
            long d = (now.getTime() - lastDate.getTime());
            android.util.Log.d("OCS", "Age du cache (mn) = " + d / 60000L);
            if (d > dureeCache) {
                android.util.Log.d("OCS", "REFRESH");
                instance = new Inventory(ctx);
            }
        }
        return instance;
    }

    public void logInventory() {
        ocslog.debug("****LOG INVENTORY****");
        ocslog.debug(this.toString());
    }

    public String getDeviceUid() {
        return this.deviceUid;
    }

    public void setDeviceUid(String paramString) {
        this.deviceUid = paramString;
    }

    public OCSBios getBios() {
        return bios;
    }

    public OCSHardware getHardware() {
        return hardware;
    }

    public OCSDrives getDrives() {
        return drives;
    }

    public String toXML() {

        StringBuffer strOut = new StringBuffer("<REQUEST>\n");
        Utils.xmlLine(strOut, 2, "DEVICEID", this.getDeviceUid());
        // Utils.xmlLine(strOut,2,"DEVICEID",OCSSettings.getInstance().getDeviceUid());
        strOut.append("  <CONTENT>\n");
        // strOut.append("    <DOWNLOAD><HISTORY /></DOWNLOAD>\n");
        strOut.append(this.bios.toXML());
        strOut.append(this.drives.toXML());
        strOut.append(this.hardware.toXML());
        strOut.append(this.inputs.toXML());
        strOut.append(this.javainfos.toXML());
        strOut.append(this.sims.toXML());
        strOut.append(this.networks.toXML());
        // strOut.append("    <CONTROLLERS></CONTROLLERS>");
        // strOut.append("    <SLOTS></SLOTS>");
        // strOut.append("    <SOUNDS></SOUNDS>");
        strOut.append(this.softwares.toXML());
        strOut.append(this.storages.toXML());
        strOut.append(this.videos.toXML());
        strOut.append("    <ACCOUNTINFO>\n");
        strOut.append("      <KEYNAME>TAG</KEYNAME>\n");
        Utils.xmlLine(strOut, "KEYVALUE", OCSSettings.getInstance().getDeviceTag());
        strOut.append("    </ACCOUNTINFO>\n");
        strOut.append("  </CONTENT>\n");
        strOut.append("  <QUERY>INVENTORY</QUERY>\n");
        strOut.append("</REQUEST>");

        return strOut.toString();
    }

    public String toString() {

        String strOut = this.getDeviceUid() + '\n' +
                        this.bios +
                        this.drives +
                        this.storages +
                        this.hardware +
                        this.networks +
                        this.videos +
                        this.softwares;

        return strOut;
    }

    public List<OCSSection> getSections(String sName) {
        if (sName.equals("BIOS")) {
            return bios.getSections();
        }
        if (sName.equals("DRIVES")) {
            return drives.getSections();
        }
        if (sName.equals("HARDWARE")) {
            return hardware.getSections();
        }
        if (sName.equals("INPUTS")) {
            return inputs.getSections();
        }
        if (sName.equals("NETWORKS")) {
            return networks.getSections();
        }
        if (sName.equals("DRIVES")) {
            return drives.getSections();
        }
        if (sName.equals("SOFTWARES")) {
            return softwares.getSections();
        }
        if (sName.equals("STORAGES")) {
            return storages.getSections();
        }
        if (sName.equals("VIDEOS")) {
            return videos.getSections();
        }
        if (sName.equals("JAVAINFOS")) {
            return javainfos.getSections();
        }
        if (sName.equals("SIM")) {
            return sims.getSections();
        }

        return null;
    }

    /**
     * Load sections checksum from file
     *
     * @param ctx application context
     **/
    private void loadSectionsFP(Context ctx) {
        lastFP = new Hashtable<String, String>();

        // Chargement des empreintes des dernieres sections validées
        FileInputStream fis;
        try {
            fis = ctx.openFileInput(sectionsFPFile);
        } catch (FileNotFoundException e1) {
            return;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                String str[] = line.split("\\|");
                String k = str[0];
                String v = str[1];
                ocslog.debug(String.format("load FP %s %s", k, v));
                if (k != null && v != null) {
                    lastFP.put(k, v);
                }
            }
        } catch (IOException e) {
        }
    }

    public void saveSectionsFP() {
        StringBuilder sb = new StringBuilder();

        for (String k : currentFP.keySet()) {
            sb.append(k).append("|").append(currentFP.get(k)).append("\n");
            ocslog.debug(String.format("save FP %s %s", k, currentFP.get(k)));
        }
        FileOutputStream fos;
        try {
            fos = mCtx.openFileOutput(sectionsFPFile, 0);
            fos.write(sb.toString().getBytes());
            fos.close();
        } catch (IOException e) {
        }
    }
}

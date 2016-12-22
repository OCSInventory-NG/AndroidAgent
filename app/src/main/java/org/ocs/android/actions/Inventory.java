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
package org.ocs.android.actions;

import android.content.Context;
import android.provider.Settings.Secure;
import android.util.Log;

import org.ocs.android.sections.OCSBios;
import org.ocs.android.sections.OCSDrives;
import org.ocs.android.sections.OCSHardware;
import org.ocs.android.sections.OCSInputs;
import org.ocs.android.sections.OCSJavaInfos;
import org.ocs.android.sections.OCSNetwork;
import org.ocs.android.sections.OCSNetworks;
import org.ocs.android.sections.OCSSection;
import org.ocs.android.sections.OCSSectionInterface;
import org.ocs.android.sections.OCSSims;
import org.ocs.android.sections.OCSSoftwares;
import org.ocs.android.sections.OCSStorages;
import org.ocs.android.sections.OCSVideos;
import org.ocs.android.sections.SystemInfos;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Inventory {
    private static Inventory instance = null;
    // Context
    private Context mCtx;

    // Footprint (previous & current)
    private Map<String, String> lastFP;
    private Map<String, String> currentFP;
    // Footprint storage file
    private static String sectionsFPFile = "sectionsfp.txt";

    // Las running time
    private static Date lastDate;
    private static long dureeCache = 300L;

    // Sections of device inventory
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

    // Log
    private OCSLog ocslog;

    private void buildInventory(Context ctx) {
        // Context
        mCtx = ctx;

        ocslog = OCSLog.getInstance();
        ocslog.debug("SystemInfos.initSystemInfos...");
        OCSSettings settings = OCSSettings.getInstance();

        lastDate = new Date();
        dureeCache = settings.getCacheLen();

        SystemInfos.initSystemInfos();

        ocslog.debug("OCSBios...");
        bios = new OCSBios();
        ocslog.debug("hardware...");
        hardware = new OCSHardware();
        String sid = Secure.getString(mCtx.getContentResolver(), Secure.ANDROID_ID);
        hardware.setName(hardware.getName() + "-" + sid);
        ocslog.debug("OCSNetworks...");
        networks = new OCSNetworks(mCtx);
        if (!networks.getNetworks().isEmpty()) {
            int m = networks.getMain();
            OCSNetwork pn = networks.getNetworks().get(m);
            hardware.setIpAddress(pn.getIpAdress());
        }

        ocslog.debug("OCSdrives...");
        drives = new OCSDrives();
        ocslog.debug("OCSStorages...");
        storages = new OCSStorages();

        if (settings.getDeviceUid() == null) {
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
            deviceUid = "android-" + sid + "-" + sdf.format(now);
            settings.setDeviceUid(deviceUid);
        } else {
            deviceUid = settings.getDeviceUid();
        }

        ocslog.debug("OCSVideos...");
        videos = new OCSVideos(mCtx);
        ocslog.debug("OCSSoftwares...");
        softwares = new OCSSoftwares(mCtx);
        ocslog.debug("OCSInputs...");
        inputs = new OCSInputs(mCtx);
        ocslog.debug("OCSJavaInfos...");
        javainfos = new OCSJavaInfos();
        ocslog.debug("OCSSims...");
        sims = new OCSSims(mCtx);

        // Checksum update
        ocslog.debug("CHECKSUM update");
        loadSectionsFP(mCtx);
        currentFP = new Hashtable<>();

        long checksum = 0L;
        checksum |= getChange(hardware, 1L);
        checksum |= getChange(bios, 2L);
        checksum |= getChange(storages, 0x100L);
        checksum |= getChange(drives, 0x200L);
        checksum |= getChange(inputs, 0x400L);
        checksum |= getChange(networks, 0x1000L);
        checksum |= getChange(videos, 0x8000L);
        checksum |= getChange(softwares, 0x10000L);
        checksum |= getChange(sims, 0x80000L);
        checksum |= getChange(javainfos, 0L);
        ocslog.debug(String.format("CK %x", checksum));
        ocslog.debug("CHECKSUM " + checksum);
        hardware.setChecksum(checksum);
    }

    /**
     * Does something change from last inventory ?
     *
     * @param s    An OCS section
     * @param mask Mask to use if changes
     * @return Mask
     */
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

    /**
     * Get an instance of {@link Inventory}
     *
     * @param ctx context
     * @return Inventory instance
     */
    public static Inventory getInstance(Context ctx) {
        // No running instance
        if (instance == null) {
            instance = new Inventory();
            instance.buildInventory(ctx.getApplicationContext());
        } else {
            // An instance already exist...
            Date now = new Date();
            long d = (now.getTime() - lastDate.getTime());
            Log.d("OCS", "Age du cache (mn) = " + d / 60000L);
            // Is it obsolete ?
            if (d > dureeCache) {
                Log.d("OCS", "REFRESH");
                instance = new Inventory();
                instance.buildInventory(ctx.getApplicationContext());
            }
        }
        return instance;
    }

    /**
     * XML values of the inventory
     *
     * @return XML values of the inventory
     */
    public String toXML() {
        StringBuffer strOut = new StringBuffer("<REQUEST>\n");
        Utils.xmlLine(strOut, 2, "DEVICEID", deviceUid);
        strOut.append("  <CONTENT>\n");
        strOut.append(bios.toXML());
        strOut.append(drives.toXML());
        strOut.append(hardware.toXML());
        strOut.append(inputs.toXML());
        strOut.append(javainfos.toXML());
        strOut.append(sims.toXML());
        strOut.append(networks.toXML());
        strOut.append(softwares.toXML());
        strOut.append(storages.toXML());
        strOut.append(videos.toXML());
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
        String strOut = deviceUid + '\n' +
                        bios.toString() +
                        drives.toString() +
                        storages.toString() +
                        hardware.toString() +
                        networks.toString() +
                        videos.toString() +
                        softwares.toString();

        return strOut;
    }

    /**
     * Get ALL sections (display of the inventory)
     *
     * @return HashMap ALL Sections
     */
    public HashMap<String, List<OCSSection>> getAllSections() {
        HashMap<String, List<OCSSection>> monRetour = new HashMap<>();

        // Fill with all values....
        monRetour.put("BIOS", bios.getSections());
        monRetour.put("DRIVES", drives.getSections());
        monRetour.put("HARDWARE", hardware.getSections());
        monRetour.put("INPUTS", inputs.getSections());
        monRetour.put("NETWORKS", networks.getSections());
        monRetour.put("DRIVES", drives.getSections());
        monRetour.put("SOFTWARES", softwares.getSections());
        monRetour.put("STORAGES", storages.getSections());
        monRetour.put("VIDEOS", videos.getSections());
        monRetour.put("JAVAINFOS", javainfos.getSections());
        monRetour.put("SIM", sims.getSections());

        return monRetour;
    }

    /**
     * Load sections footprint from file
     *
     * @param ctx application context
     **/
    private void loadSectionsFP(Context ctx) {
        lastFP = new Hashtable<>();

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

    /**
     * Save sections footprint to disk
     */
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

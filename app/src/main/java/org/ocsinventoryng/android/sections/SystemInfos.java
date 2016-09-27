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

import android.util.Log;

import org.ocsinventoryng.android.actions.OCSLog;
import org.ocsinventoryng.android.actions.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemInfos {

    private final static String CPUFREQDIR = "/sys/devices/system/cpu/cpu0/cpufreq";

    private final static String CPUFREQPATH = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq";
    private final static String CPUFREQPATH2 = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";
    private final static String CPUPRESENT = "/sys/devices/system/cpu/present";

    private final static String CPUINFOPATH = "/proc/cpuinfo";
    private final static String MEMINFOPATH = "/proc/meminfo";

    private static SystemInfos instance;

    private static int processorNumber;
    private static String processorName;
    private static String processorType;
    private static int processorSpeed;

    private static String serial;

    private static int memtotal;
    private static int swaptotal;

    private static OCSLog ocslog;

    public static SystemInfos getInstance() {
        if (instance == null) {
            instance = new SystemInfos();
        }
        return instance;
    }

    public static void InitSystemInfos() {
        ocslog = OCSLog.getInstance();
        ocslog.debug("SYSTEMINFOS start");
        readCpuinfo();
        readMeminfo();
        readCpuFreq();
        ocslog.debug("SYSTEMINFOS end");
    }

    private static void readCpuinfo() {
        try {
            ocslog.debug("=>readCpuinfo");
            File f = new File(CPUINFOPATH);
            BufferedReader bReader = new BufferedReader(new FileReader(f), 8192);
            String line;
            int nbProc = 0;
            while ((line = bReader.readLine()) != null) {
                ocslog.debug(line);
                Pattern p = Pattern.compile(".*Processor.*:(.*)");
                Matcher m = p.matcher(line);
                if (m.find()) {
                    processorName = m.group(1).trim();
                }
                p = Pattern.compile(".*BogoMIPS.*:(.*)");
                m = p.matcher(line);
                if (m.find()) {
                    nbProc++;
                }
                p = Pattern.compile(".*architecture.*:(.*)\\s.*", Pattern.CASE_INSENSITIVE);
                m = p.matcher(line);
                if (m.find()) {
                    processorType = m.group(1).trim();
                }
                p = Pattern.compile(".*serial.*:(.*)\\s.*", Pattern.CASE_INSENSITIVE);
                m = p.matcher(line);
                if (m.find()) {
                    serial = m.group(1);
                }
            }
            processorNumber = nbProc;
            bReader.close();
        } catch (FileNotFoundException e) {
            ocslog.error("File notfound : " + CPUINFOPATH);
        } catch (IOException e) {
            ocslog.error("IO error reading " + CPUINFOPATH);
        }
        ocslog.debug("<=readCpuinfo");
        // Use /sys/devices/system/cpu/present to correct cpu/core number
        // contains 0 or 0-x
        try {
            String cpupresent = Utils.readShortFile(new File(CPUPRESENT));
            processorNumber = parse_cpu_present(cpupresent) + 1;
        } catch (IOException e) {
            ocslog.error("IO error reading " + CPUPRESENT);
        }
    }

    private static void readMeminfo() {
        try {
            File f = new File(MEMINFOPATH);
            BufferedReader bReader = new BufferedReader(new FileReader(f), 8192);
            String line;
            while ((line = bReader.readLine()) != null) {
                Pattern p = Pattern.compile(".*memtotal.*:(.*)\\s.*", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(line);
                if (m.find()) {
                    memtotal = Integer.parseInt(m.group(1).trim());
                }
                p = Pattern.compile(".*swaptotal.*:(.*)\\s.*", Pattern.CASE_INSENSITIVE);
                m = p.matcher(line);
                if (m.find()) {
                    swaptotal = Integer.parseInt(m.group(1).trim());
                }
            }
            bReader.close();
        } catch (FileNotFoundException e) {
            ocslog.error("File not found : " + MEMINFOPATH);
        } catch (IOException e) {
            ocslog.error("IOException : " + e.getMessage());
        }
    }

    public static void readCpuFreq() {
        ocslog.debug("=>readCpuFreq");
        processorSpeed = readCpuFreq(CPUFREQPATH);
        if (processorSpeed == 0) {
            processorSpeed = readCpuFreq(CPUFREQPATH2);
        }
        if (processorSpeed == 0) {
            debugListDir(CPUFREQDIR);
        }

        ocslog.debug("=>readCpuFreq");
    }

    public static int getProcessorNumber() {
        return processorNumber;
    }

    public static String getProcessorName() {
        return processorName;
    }

    public String getProcessorType() {
        return processorType;
    }

    public String getSerial() {
        return serial;
    }

    public static int getMemtotal() {
        return memtotal;
    }

    public static int getSwaptotal() {
        return swaptotal;
    }

    public static int getProcessorSpeed() {
        return processorSpeed;
    }

    private static void debugListDir(String path) {
        ocslog.debug("debugListDir" + path);
        File f = new File(path);
        File[] files = f.listFiles();
        if (files != null) {
            for (File inFile : files) {
                ocslog.debug(inFile.getName());
            }
        }
    }

    private static int readCpuFreq(String path) {
        ocslog.debug("=>readCpuFreq");
        int speed = 0;
        File f = new File(path);
        try {
            BufferedReader bReader = new BufferedReader(new FileReader(f), 8192);
            String line = bReader.readLine();
            ocslog.debug(line);
            speed = Integer.parseInt(line);
            bReader.close();
        } catch (FileNotFoundException e) {
            ocslog.error("File not found : " + CPUFREQPATH);
        } catch (IOException e) {
            ocslog.error("IO error reading " + CPUFREQPATH);
        }
        ocslog.debug("=>readCpuFreq");
        return speed;
    }

    private static int parse_cpu_present(String ligne) {
        String ls = System.getProperty("line.separator");
        ligne = ligne.replaceAll(ls, "");
        Log.w("SCANCPU", ligne);
        int x = ligne.indexOf('-');
        Log.w("SCANCPU", Integer.toString(x));
        String s = ligne.substring(x + 1);

        return Integer.parseInt(s);
    }
}

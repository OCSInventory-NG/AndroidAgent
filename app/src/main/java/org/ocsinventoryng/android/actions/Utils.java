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
package org.ocsinventoryng.android.actions;

import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Utils {
    private static boolean isRooted = false;
    private static long lastRootCheck = 0;


    public static void xmlLine(StringBuffer sbOut, int n, String tag, String val) {
        for (int i = 0; i < n; i++)
            sbOut.append(' ');
        if (val == null) {
            sbOut.append('<').append(tag).append("/>\n");
        } else {
            sbOut.append('<').append(tag).append('>').append(val).append("</").append(tag).append(">\n");
        }
    }

    public static void xmlLine(StringBuffer sbOut, String tag, String val) {
        xmlLine(sbOut, 6, tag, val);
    }

    private static String readSysCommand(String commande0, String arg1) {
        OCSLog localLog = OCSLog.getInstance();
        String reponse = "";
        try {
            String[] commande = new String[2];
            commande[0] = commande0;
            commande[1] = arg1;

            // Lancement de la commande
            InputStream localInputStream = new ProcessBuilder(commande).start().getInputStream();
            // byte[] arrayOfByte = new byte[1024];
            final char[] buffer = new char[1024];
            StringBuilder sb = new StringBuilder();
            InputStreamReader isr = new InputStreamReader(localInputStream);
            int i;
            while ((i = isr.read(buffer, 0, buffer.length)) != -1) {
                sb.append(buffer, 0, i);
            }

            localInputStream.close();
            reponse = new String(sb);
        } catch (IOException localIOException) {
            localLog.error("***Error during ReadCPUinfo");
            localLog.error("Message :" + localIOException.getMessage());
        }
        return reponse;
    }

    public static String bytesToHex(byte[] array) {
        if (array == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(':');
            }
            sb.append(String.format("%02x", array[i]));
        }
        return sb.toString();
    }

    public static String intToIp(int i) {
        String sb = String.valueOf(i & 0xFF) + "." + String.valueOf(((i >> 8) & 0xFF)) + "." + String.valueOf(((i >> 16) & 0xFF))
                    + "." + String.valueOf(((i >> 24) & 0xFF));
        return sb;
    }

    /*
     *
     * Simple copie de fichier
     */
    public static void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    /**
     * Simple save string in a file
     *
     * @param str  String to write
     * @param dest Destination file
     * @throws IOException
     */
    public static void strToFile(String str, File dest) throws IOException {
        OutputStream out = new FileOutputStream(dest);
        out.write(str.getBytes(), 0, str.length());
        out.close();
    }

    public static String readShortFile(File infile) throws IOException {
        StringBuilder sb = new StringBuilder();
        byte[] buf = new byte[1024];
        int len;
        FileInputStream fis = new FileInputStream(infile);
        while ((len = fis.read(buf)) != -1)
            sb.append(new String(buf, 0, len));
        fis.close();
        return sb.toString();
    }

    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                hexString.append(Integer.toHexString(0xFF & aMessageDigest));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Calculate digest containt of a File Return it in hexa or base64
     *
     * @param dataFile File to digest
     * @param algo     Algorithme to use
     * @return The digest as hexa or base64 string
     */
    public static String digestFile(File dataFile, String algo, String format) {
        String strDigest = null;
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(algo);
        } catch (NoSuchAlgorithmException e) {
            Log.e("calculateDigest", "Exception while getting Digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(dataFile);
        } catch (FileNotFoundException e) {
            Log.e("calculateDigest", "Exception while getting FileInputStream", e);
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] messageDigest = digest.digest();

            if (format.equalsIgnoreCase("hexa")) {
                // Create Hex String
                StringBuilder hexString = new StringBuilder();
                for (byte aMessageDigest : messageDigest) {
                    String b = String.format("%02x", (0xFF & aMessageDigest));
                    hexString.append(b);
                }
                strDigest = hexString.toString();
            } else {
                strDigest = Base64.encodeToString(messageDigest, Base64.NO_WRAP);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e("calculateDigest", "Exception on closing MD5 input stream", e);
            }
        }
        return strDigest;
    }


    /**
     * Try to determine hostname else return android
     *
     * @return Hostname as string
     */
    public static String getHostname() {
        String hostname = readSysCommand("/system/bin/getprop", "net.hostname").trim();
        if (hostname.length() == 0) {
            hostname = "android";
        }
        return hostname;
    }

    public static void concateFiles(File f1, File fout) throws IOException {
        FileInputStream fis = new FileInputStream(f1);
        FileOutputStream fos = new FileOutputStream(fout, true);
        byte[] buf = new byte[8192];
        int len;
        while ((len = fis.read(buf)) != -1) {
            fos.write(buf, 0, len);
        }
        fis.close();
        fos.close();
    }

    /**
     * Check if the device is rooted
     *
     * @return true if device is rooted
     */
    public static boolean isDeviceRooted() {
        if (isRooted || (lastRootCheck != 0L && lastRootCheck > System.currentTimeMillis() - 5000)) {
            return isRooted;
        }

        // get from build info
        String buildTags = android.os.Build.TAGS;
        if (buildTags != null) {
            Log.d("android.os.Build.TAGS", buildTags);
        }
        if (buildTags != null && buildTags.contains("test-keys")) {
            isRooted = true;
        }

        try {
            // check if /system/app/Superuser.apk is present
            File file = new File("/system/app/Superuser.apk");
            if (!isRooted && file.exists()) {
                isRooted = true;
            }

            // Access to secure file
            file = new File("/mnt/secure");
            if (!isRooted && file.canRead()) {
                isRooted = true;
            }
        } catch (Throwable e1) {
            // ignore
        }

        // Access to su
        if (!isRooted && checkCommande("/bin/su") || checkCommande("/xbin/su") || checkCommande("/sbin/su")) {
            isRooted = true;
        }

        // Return
        lastRootCheck = System.currentTimeMillis();
        return isRooted;
    }

    private static boolean checkCommande(String pcmde) {
        boolean ret = false;
        Process proc;

        String commande[] = pcmde.split(" ");

        File file = new File(commande[0]);
        if (file.exists()) {
            try {
                // Lancement de la commande
                ProcessBuilder pb = new ProcessBuilder(commande);
                pb.redirectErrorStream(true);
                proc = pb.start();
                proc.waitFor();
                int cr = proc.exitValue();
                ret = (cr == 0);
            } catch (Exception e) {
                //
            }
        }

        return ret;
    }

    public static boolean unZip(String zipFile, String destination) {
        InputStream is;
        ZipInputStream zis;
        try {
            is = new FileInputStream(zipFile);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;

            while ((ze = zis.getNextEntry()) != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int count;

                String filename = ze.getName();
                FileOutputStream fout = new FileOutputStream(destination + filename);

                // reading and writing
                while ((count = zis.read(buffer)) != -1) {
                    baos.write(buffer, 0, count);
                    byte[] bytes = baos.toByteArray();
                    fout.write(bytes);
                    baos.reset();
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}

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

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.ocsinventoryng.android.agent.OCSPrologReply;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.GZIPOutputStream;

public class OCSFiles {

    private Context appCtx;

    private String inventoryFileName;
    private final String gzipedFileName = "tmp.gz";
    private final String prologFileName = "prolog.xml";
    private final String prologReplyFileName = "prolog_reply.xml";
    private OCSLog ocslog;

    public OCSFiles(Context ctx) {
        ocslog = OCSLog.getInstance();
        appCtx = ctx;

        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);

        inventoryFileName = Utils.getHostname() + "-" + dt.format(new Date()) + ".ocs";
    }

    public File getGzipedFile(File inFile) {
        FileInputStream fis;
        try {
            fis = new FileInputStream(inFile);
            GZIPOutputStream gzos = new GZIPOutputStream(appCtx.openFileOutput(gzipedFileName, 0));
            byte[] buff = new byte[1024];
            int n;
            while ((n = fis.read(buff)) != -1)
                gzos.write(buff, 0, n);
            fis.close();
            gzos.close();
        } catch (Exception e) {
            ocslog.error("Erreur creating " + gzipedFileName);
        }
        return appCtx.getFileStreamPath(gzipedFileName);
    }

    public File getInventoryFileXML(Inventory pInventory) {
        StringBuilder strOut = new StringBuilder("<?xml version =\"1.0\" encoding=\"UTF-8\"?>\n");
        strOut.append(pInventory.toXML());

        FileOutputStream fOutputStream;
        try {
            fOutputStream = appCtx.openFileOutput(inventoryFileName, 0);
            BufferedOutputStream bos = new BufferedOutputStream(fOutputStream, 8192);
            byte[] arrayOfByte = strOut.toString().getBytes();
            bos.write(arrayOfByte);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            ocslog.error("Error during xml inventory file creation");
        } catch (IOException e) {
            ocslog.error("Error writing to xml inventory file ");
        }
        ocslog.debug("xml inventory file ready");
        return appCtx.getFileStreamPath(inventoryFileName);
    }

    // deprecated for getRequestFileXML
    public File getPrologFileXML() {
        String deviceId = OCSSettings.getInstance().getDeviceUid();

        StringBuilder strBuf = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        strBuf.append("<REQUEST>\n");
        strBuf.append("  <QUERY>PROLOG</QUERY>\n");
        strBuf.append("  <DEVICEID>");
        strBuf.append(deviceId);
        strBuf.append("</DEVICEID>\n");
        strBuf.append("</REQUEST>\n");

        FileOutputStream prologFileOStream;
        try {
            prologFileOStream = appCtx.openFileOutput(prologFileName, Context.MODE_PRIVATE);
            BufferedOutputStream prologFileBOS = new BufferedOutputStream(prologFileOStream);
            byte[] arrayOfByte = strBuf.toString().getBytes();
            prologFileBOS.write(arrayOfByte);
            prologFileBOS.flush();
            prologFileBOS.close();
        } catch (Exception e) {
            ocslog.error("Erreur during prolog file creation");
        }
        return appCtx.getFileStreamPath(prologFileName);
    }

    public File getRequestFileXML(String query, String id, String err) {
        String deviceId = OCSSettings.getInstance().getDeviceUid();
        String queryFileName = query + ".xml";

        StringBuilder strBuf = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        strBuf.append("<REQUEST>\n");
        strBuf.append("  <QUERY>").append(query).append("</QUERY>\n");
        strBuf.append("  <DEVICEID>");
        strBuf.append(deviceId);
        strBuf.append("</DEVICEID>\n");
        if (id != null) {
            strBuf.append("  <ID>").append(id).append("</ID>\n");
        }
        if (err != null) {
            strBuf.append("  <ERR>").append(err).append("</ERR>\n");
        }
        strBuf.append("</REQUEST>\n");

        FileOutputStream fos;
        try {
            fos = appCtx.openFileOutput(queryFileName, Context.MODE_PRIVATE);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            byte[] arrayOfByte = strBuf.toString().getBytes();
            bos.write(arrayOfByte);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            ocslog.error("Erreur during " + query + "request file creation");
        }
        return appCtx.getFileStreamPath(queryFileName);
    }

    public String copyToExternal(Inventory inventory) {
        String res = "OK";
        File wd = getSDWorkDirectory();
        if (wd != null) {
            File ficIn = getInventoryFileXML(inventory);
            File ficOut = new File(wd, inventoryFileName);
            try {
                Utils.copyFile(ficIn, ficOut);
            } catch (IOException e) {
                res = e.getMessage();
                OCSLog.getInstance().error(e.getMessage());
            }
            ficIn.delete();
        }
        return res;
    }

    /**
     * @return File objet of ocs directory on sdcard
     */
    public File getSDWorkDirectory() {
        File rep = Environment.getExternalStoragePublicDirectory("ocs");
        Log.d("OCSAgent", Environment.getExternalStorageDirectory().getPath());
        if (!rep.isDirectory()) {
            rep.delete();
        }
        if (!rep.exists()) {
            if (!rep.mkdir()) {
                Log.e("OCSAgent", "Cannot create directory : " + rep.getPath());
                return null;
            } else {
                Log.d("OCSAgent", rep.getPath() + " created");
            }
        }
        return rep;
    }

    // Prolog reply is stored on file to be read py dowload service
    // This is more simple as sending  the object on intend .
    public void savePrologReply(String str) {
        FileOutputStream fos;
        try {
            fos = appCtx.openFileOutput(prologReplyFileName, Context.MODE_PRIVATE);
            fos.write(str.getBytes());
        } catch (Exception e) {
            ocslog.error("Erreur during prolog replay file creation");
        }
    }

    public OCSPrologReply loadPrologReply() {
        OCSPrologReply reply = null;
        FileInputStream fis;
        try {
            fis = appCtx.openFileInput(prologReplyFileName);
            PrologReplyParser prp = new PrologReplyParser();
            reply = prp.parseDocument(fis);
        } catch (Exception e) {
            ocslog.error("Erreur during prolog replay file read");
        }
        return reply;
    }
}

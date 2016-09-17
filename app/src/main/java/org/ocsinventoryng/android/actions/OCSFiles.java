package org.ocsinventoryng.android.actions;

import android.content.Context;
import android.os.Environment;

import org.ocsinventoryng.android.agent.OCSPrologReply;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
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
        StringBuilder filename = new StringBuilder();

        filename.append(Utils.getHostname());
        filename.append("-");

        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);
        filename.append(dt.format(new Date()));

        filename.append(".ocs");

        inventoryFileName = filename.toString();
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

    public void getUnGzipedFile(File inFile, String fileOutName) throws IOException {
        FileInputStream fis = new FileInputStream(inFile);

        GZIPInputStream gzis = new GZIPInputStream(new BufferedInputStream(fis));
        FileOutputStream fos = appCtx.openFileOutput(fileOutName, Context.MODE_PRIVATE);
        byte[] buff = new byte[1024];
        int n;
        while ((n = fis.read(buff)) != -1)
            fos.write(buff, 0, n);
        gzis.close();
        fos.close();
    }

    public File getInventoryFileXML(Inventory pInventory) {
        StringBuffer strOut = new StringBuffer("<?xml version =\"1.0\" encoding=\"UTF-8\"?>\n");
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
        /*
         * if (!(inventoryFile).exists()) { boolean bool =
		 * (inventoryFile).delete(); inventoryFile = new
		 * File(this.inventoryFilePath); }
		 */
        ocslog.debug("xml inventory file ready");
        return appCtx.getFileStreamPath(inventoryFileName);
    }

    // deprecated for getRequestFileXML
    public File getPrologFileXML() {
        String deviceId = OCSSettings.getInstance().getDeviceUid();

        StringBuffer strBuf = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        // strBuf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<!DOCTYPE REQUEST>\r\n");
        strBuf.append("<REQUEST>\n");
        strBuf.append("  <QUERY>PROLOG</QUERY>\n");
        strBuf.append("  <DEVICEID>");
        strBuf.append(deviceId);
        strBuf.append("</DEVICEID>\n");
        strBuf.append("</REQUEST>\n");

        FileOutputStream prologFileOStream;
        try {
            prologFileOStream = appCtx.openFileOutput(this.prologFileName, Context.MODE_PRIVATE);
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

        StringBuffer strBuf = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        // strBuf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<!DOCTYPE REQUEST>\r\n");
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
        android.util.Log.d("OCSAgent", Environment.getExternalStorageDirectory().getPath());
        if (!rep.isDirectory()) {
            rep.delete();
        }
        if (!rep.exists()) {
            if (!rep.mkdir()) {
                android.util.Log.e("OCSAgent", "Cannot create directory : " + rep.getPath());
                return null;
            } else {
                android.util.Log.d("OCSAgent", rep.getPath() + " created");
            }
        }
        return rep;
    }

    // Prolog reply is stored on file to be read py dowload service
    // This is more simple as sending  the object on intend .
    void savePrologReply(String str) {
        FileOutputStream fos;
        try {
            fos = appCtx.openFileOutput(this.prologReplyFileName, Context.MODE_PRIVATE);
            fos.write(str.getBytes());
        } catch (Exception e) {
            ocslog.error("Erreur during prolog replay file creation");
        }
    }

    //
    public OCSPrologReply loadPrologReply() {
        OCSPrologReply reply = null;
        FileInputStream fis;
        try {
            fis = appCtx.openFileInput(this.prologReplyFileName);
            PrologReplyParser prp = new PrologReplyParser();
            reply = prp.parseDocument(fis);
        } catch (Exception e) {
            ocslog.error("Erreur during prolog replay file read");
        }
        return reply;
    }
}

package org.ocsinventory.android.actions;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.zip.GZIPOutputStream;

import android.content.Context;
import android.os.Environment;
import android.provider.OpenableColumns;

public class OCSFiles {
	private static OCSFiles instance = null;
	// public String BASE_FILE_NAME = "termadmin";
	// public String XML_DIR = "/XML/";
	private static Context appCtx;
	
	private String inventoryFileName;
	private String gzipedFileName		= "tmp.gz";
	private String prologFileName 		= "prolog.xml";
	
	public static OCSFiles getInstance() {
		if (instance == null)
			instance = new OCSFiles();
		return instance;
	}
	public static void initInstance(Context ctx) {
		if (instance == null) {
			appCtx = ctx;
			instance = new OCSFiles();
		}
	}

	public OCSFiles() {
		StringBuilder filename = new StringBuilder();

		filename.append(Utils.getHostname());
		filename.append("-");

		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);
		filename.append(dt.format(new Date()));

		filename.append(".ocs");

		inventoryFileName = filename.toString();
	}

	public File getGzipedFile(File inFile) {
		OCSLog ocslog = OCSLog.getInstance();
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
			ocslog.append("Erreur creating "+gzipedFileName);
		}
		return appCtx.getFileStreamPath (gzipedFileName);
	}
	
	public File getInventoryFileXML(Inventory pInventory) {
		OCSLog ocslog = OCSLog.getInstance();

		StringBuffer strOut = new StringBuffer("<?xml version =\"1.0\" encoding=\"UTF-8\"?>\n");
		strOut.append(pInventory.toXML());
		
		FileOutputStream fOutputStream;
		try {
			fOutputStream = appCtx.openFileOutput(inventoryFileName,0);
			BufferedOutputStream bos = new BufferedOutputStream(fOutputStream, 8192);
			byte[] arrayOfByte = strOut.toString().getBytes();
			bos.write(arrayOfByte);
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			android.util.Log.e("OCS", "FileNotFoundException");
			ocslog.append("Erreur de creation fichier xml inventaire");
		} catch (IOException e) {
			android.util.Log.e("OCS", "FileNotFoundException");
			ocslog.append("Erreur d'ecriture sur le fichier xml");
		}
		/*
		 * if (!(inventoryFile).exists()) { boolean bool =
		 * (inventoryFile).delete(); inventoryFile = new
		 * File(this.inventoryFilePath); }
		 */
		android.util.Log.i("OCS", "Fichier pret");
		return appCtx.getFileStreamPath (inventoryFileName);
	}
	
	public File getPrologFileXML() {
		OCSLog ocslog = OCSLog.getInstance();		
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
			prologFileOStream = appCtx.openFileOutput(this.prologFileName,0);
			BufferedOutputStream prologFileBOS = new BufferedOutputStream(
					prologFileOStream);
			byte[] arrayOfByte = strBuf.toString().getBytes();
			prologFileBOS.write(arrayOfByte);
			prologFileBOS.flush();
			prologFileBOS.close();
		} catch (Exception e) {
			ocslog.append("Erreur during prolog file creation");
		}
		return appCtx.getFileStreamPath(prologFileName);
	}
	
	public String copyToExternal(Inventory inventory) {
		String res="OK";
		File wd = getSDWorkDirectory();
		if ( wd != null ) {
			File ficIn = getInventoryFileXML(inventory);
			File ficOut= new File(wd, inventoryFileName);
				try {
					Utils.copyFile(ficIn, ficOut);
				} catch (IOException e) {
					res=e.getMessage();
					OCSLog.getInstance().append(e.getMessage());
				}
		}
		return res;
	}
	/*
	 * Retourne le repertoire de l'appli sur la sdcard
	 * 
	 */
	public File getSDWorkDirectory() { 
		File rep=Environment.getExternalStoragePublicDirectory("ocs");
		android.util.Log.d("OCSAgent", Environment.getExternalStorageDirectory().getPath());
		if ( ! rep.isDirectory() ) {
			rep.delete();
		}
		if ( ! rep.exists() ) {
			if ( ! rep.mkdir() ) {
		    	android.util.Log.e("OCSAgent", "Cannot create directory : "+rep.getPath());
		    	return null ;
			}
			else
				android.util.Log.d("OCSAgent",  rep.getPath()+ " created");
		}
		return rep;
	}

}

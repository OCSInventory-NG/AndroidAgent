package org.ocsinventory.android.actions;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import android.os.Environment;

public class OCSFiles {
	private static OCSFiles instance = null;
	// public String BASE_FILE_NAME = "termadmin";
	// public String XML_DIR = "/XML/";
	private String inventoryFileName 	= "inventory.xml";
	private String gzipedFileName		= "tmp.gz";
	private String prologFileName 		= "prolog.xml";
	
	public static OCSFiles getInstance() {
		if (instance == null)
			instance = new OCSFiles();
		return instance;
	}
	
	public File getGzipedFile(File inFile) {
		OCSLog ocslog = OCSLog.getInstance();
		File wd = getSDWorkDirectory();
		if ( wd == null )
			 return null;
		String gzipedFilePath = wd.getPath()+"/"+gzipedFileName;
		FileInputStream fis;
		try {
			fis = new FileInputStream(inFile);
			GZIPOutputStream gzos = new GZIPOutputStream(new FileOutputStream( gzipedFilePath));
			byte[] buff = new byte[1024];
			int n;
			while ((n = fis.read(buff)) != -1)
				 gzos.write(buff, 0, n);
			fis.close();
			gzos.close();
		} catch (Exception e) {
			ocslog.append("Erreur creating "+gzipedFilePath);
		}
		return new File(gzipedFilePath);
	}
	
	public File getInventoryFileXML(Inventory pInventory) {
		File inventoryFile	= null;
		OCSLog ocslog = OCSLog.getInstance();

		File wd = getSDWorkDirectory();
		if ( wd == null )
			 return null;
		 
		inventoryFile = new File(wd, this.inventoryFileName);
		if (!inventoryFile.exists()) {
			try {
				inventoryFile.createNewFile();
			} catch (IOException e) {
				android.util.Log.e("OCS", "Erreur de creation du fichier XML inventaire");
				ocslog.append("Erreur de creation du fichier xml inventaire");
				ocslog.append(e.getMessage());
			}
		}
		android.util.Log.i("OCS", "Fichier "+inventoryFile.getPath()+" created");
		StringBuffer strOut = new StringBuffer("<?xml version =\"1.0\" encoding=\"UTF-8\"?>\n");
		strOut.append(pInventory.toXML());
		
		FileOutputStream fOutputStream;
		try {
			fOutputStream = new FileOutputStream(inventoryFile);
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
		
		return inventoryFile;
	}
	
	public File getPrologFileXML() {
		OCSLog ocslog = OCSLog.getInstance();		
		File prologFile		= null;

		String deviceId = OCSSettings.getInstance().getDeviceUid();

		File wd = getSDWorkDirectory();
		if ( wd == null )
			 return null;
		
		prologFile = new File(wd, this.prologFileName);
		if (!prologFile.exists()) {
			prologFile.delete();
			prologFile = new File(wd, this.prologFileName);
		}
		
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
			prologFileOStream = new FileOutputStream(prologFile);
			BufferedOutputStream prologFileBOS = new BufferedOutputStream(
					prologFileOStream);
			byte[] arrayOfByte = strBuf.toString().getBytes();
			prologFileBOS.write(arrayOfByte);
			prologFileBOS.flush();
			prologFileBOS.close();
		} catch (Exception e) {
			ocslog.append("Erreur during prolog file creation");
		}
		return prologFile;
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

package org.ocsinventory.android.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

	public static void xmlLine(StringBuffer sbOut, int n, String tag, String val) {
		for ( int i=0; i<n;i++)			
			sbOut.append(' ');
		if ( val == null )
			sbOut.append('<').append(tag).append("/>\n");
		else
			sbOut.append('<').append(tag).append('>').append(val)
				.append("</").append(tag).append(">\n");
	}
	public static void xmlLine(StringBuffer sbOut, String tag, String val) {
		xmlLine( sbOut, 6,  tag,  val);
	}

	public static void strLine(StringBuffer sbOut, String tag,	String val) {
		sbOut.append(tag).append(':').append(val).append("\n");		
	}
	
	private static String readSysCommand(String commande0, String arg1) {
		OCSLog localLog = OCSLog.getInstance();
		String reponse = "";
		try {
			String[] commande = new String[2];
			commande[0] = commande0;
			commande[1] = arg1;

			// Lancement de la commande
			InputStream localInputStream = new ProcessBuilder(commande).start()
					.getInputStream();
			// byte[] arrayOfByte = new byte[1024];
			final char[] buffer = new char[1024];
			StringBuilder sb = new StringBuilder();
			InputStreamReader isr = new InputStreamReader(localInputStream);
			int i;
			while ((i = isr.read(buffer, 0, buffer.length)) != -1) {
				localLog.append(String.valueOf(i)
						+ "--------------------------------");
				sb.append(buffer, 0, i);
			}
			;
			localInputStream.close();
			reponse = new String(sb);
		} catch (IOException localIOException) {
			localLog.append("***Error during ReadCPUinfo");
			localLog.append("Message :" + localIOException.getMessage());
		}
		return reponse;
	}
	
	public static String bytesToHex( byte[]array ) {
		if ( array == null )
			return "";
		StringBuffer sb = new StringBuffer();
		for (int i=0;i<array.length;i++) {
			if ( i > 0 )
				sb.append(':');
			sb.append(String.format("%02x",array[i]));
		}
		return sb.toString();
	}
	public static  String intToIp(int i) {
		StringBuffer sb = new StringBuffer (String.valueOf( i & 0xFF));
			sb.append( "." ).append(String.valueOf( ((i >> 8 ) & 0xFF) ))
			.append( "." ).append(String.valueOf( ((i >> 16 ) & 0xFF) ))
			.append( "." ).append(String.valueOf( ((i >> 24 ) & 0xFF) ));
			return sb.toString();
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
	
	public static String md5(String s) {
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();
	        
	        // Create Hex String
	        StringBuffer hexString = new StringBuffer();
	        for (int i=0; i<messageDigest.length; i++)
	            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
	        return hexString.toString();
	        
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return "";
	}
	
	public static String getHostname() {
		String hostname = readSysCommand("/system/bin/getprop", "net.hostname").trim();
		if(hostname.length() == 0)
			hostname = "android";
		return hostname;
	}
	private static boolean isRooted;
	private static long lastRootCheck=0;
	
	public static boolean isDeviceRooted() {
        
	     if (isRooted || (lastRootCheck != 0L && lastRootCheck > System.currentTimeMillis() - 5000))
	     {
	         return isRooted;
	     }

	      // get from build info

	     String buildTags = android.os.Build.TAGS;
	      if ( buildTags != null )
	    	  android.util.Log.d("android.os.Build.TAGS", buildTags);
	      if (buildTags != null && buildTags.contains("test-keys")) {
	          isRooted = true;
	          lastRootCheck = System.currentTimeMillis();
	          // return true;
	      }

	      // check if /system/app/Superuser.apk is present
	      try {
	        File file = new File("/system/app/Superuser.apk");
	        if (file.exists()) {
	          isRooted = true;
	          lastRootCheck = System.currentTimeMillis();
	          return true;
	        }
	      } catch (Throwable e1) {
	        // ignore
	      }
	      // Access to secrure file
	      try {
		        File file = new File("/mnt/secure");
		        if (file.canRead()) {
		          isRooted = true;
		          lastRootCheck = System.currentTimeMillis();
		          return true;
		        }
		      } catch (Throwable e1) {
		        // ignore
		      }

	      // Access to su

		  if ( checkCommande("/bin/su") ) {
		          isRooted = true;
		          lastRootCheck = System.currentTimeMillis();
		          return true;
		  }
		  if ( checkCommande("/xbin/su") ) {
	          isRooted = true;
	          lastRootCheck = System.currentTimeMillis();
	          return true;
		  }
		  if ( checkCommande("/sbin/su") ) {
	          isRooted = true;
	          lastRootCheck = System.currentTimeMillis();
	          return true;
		  }
	      isRooted = false;
	      lastRootCheck = System.currentTimeMillis();
	      return false;
	}
	
	private static boolean checkCommande(String pcmde ) {
		String commande[]=pcmde.split(" ");

		File file = new File(commande[0]);
		if ( ! file.exists())
			return false;
		
		boolean ret;
		Process proc=null;
		try {
			// Lancement de la commande
			ProcessBuilder pb =  new ProcessBuilder(commande);
			pb.redirectErrorStream(true);
			proc = pb.start();
			proc.waitFor();
			int cr = proc.exitValue();
			ret = ( cr == 0 );
		} catch (Exception e) {
			return false;
		}
		return ret;
	}
	
}

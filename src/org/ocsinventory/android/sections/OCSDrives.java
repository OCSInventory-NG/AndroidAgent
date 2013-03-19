package org.ocsinventory.android.sections;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ocsinventory.android.actions.OCSLog;

public class OCSDrives implements OCSSectionInterface
{
	final private String sectionTag = "DRIVES";
	
	private final String DFPATH = "/system/bin/df";
	private final String MOUNTSPATH = "/proc/mounts";
	
	public ArrayList<OCSDrive> drives;
	
	public OCSDrives() {
		this.drives= new ArrayList<OCSDrive>();
		OCSLog ocslog = OCSLog.getInstance();
		// Lecture des FS a partir de la commande df
		try {

			// Lancement de la commande
			InputStream is = new ProcessBuilder(DFPATH).start()
					.getInputStream();

			BufferedReader br =new BufferedReader(new InputStreamReader(is), 8192);
			String ligne;
			while ((ligne = br.readLine()) != null ) {
				android.util.Log.i("df : ", ligne);
				// Parsing the df line
				/*
				String strPattern=null;
				int rUnit=1;
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
					strPattern="(.*):\\s*(\\d*)K.*?(\\d*)K.*?(\\d*)K.*";
				}
				else
					rUnit=1024;
					strPattern="(.*)\\s*(\\d*)M.*?(\\d*)M.*?(\\d*)M.*";
				*/
				String strPattern="^(/.*?):*\\s.*";
				Pattern p = Pattern.compile(strPattern, Pattern.CASE_INSENSITIVE);
				// Pattern p = Pattern.compile("(.*):.*", Pattern.CASE_INSENSITIVE);
				Matcher m= p.matcher(ligne);
				if ( m.find() ) {
					if ( m.group(1) != null ) {
						ocslog.append("Add drive "+m.group(1));
						OCSDrive drive = new OCSDrive(m.group(1).trim());
						drives.add(drive);
					}
				}
			}
			is.close();
		} catch (IOException localIOException) {
			android.util.Log.e("ERREUR", "Message :" + localIOException.getMessage());
		}
		/*
		* Complement avec le fichier /proc/mounts
		*/
		try {
			File f = new File(MOUNTSPATH);
			BufferedReader bReader = new BufferedReader(new FileReader(f), 8192);
			String line;
			while ( ( line =  bReader.readLine() ) != null  ) {
				ocslog.append(line);
				Pattern p = Pattern.compile("(.*?)\\s+(.*?)\\s+(.*?)\\s.*", Pattern.CASE_INSENSITIVE);
				Matcher m= p.matcher(line);
				if ( m.find() ) {
						String dev=m.group(1);
						String type=m.group(2);
						String fs=m.group(3);
						ocslog.append("Volumename :"+dev);
						ocslog.append("type       :"+type);
						ocslog.append("filesystem :"+fs);
						int i=0;
						
						for ( i=0; i < drives.size(); i++ ) {
							OCSDrive d=drives.get(i);
							if ( type.matches(d.getType()) ) {
								ocslog.append("MATCH       :"+type);
								d.setFilesystem(fs);
								d.setVolumName(dev);
								break;
							}
						}
				}

				
			}
			bReader.close();
		} catch (FileNotFoundException e) {
			//ocslog.appendLog("File not found : "+MOUNTSPATH);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	
	}
	
	public String toXML() {
		StringBuffer strOut = new StringBuffer();
		for ( OCSDrive o : drives ) {
			strOut.append(o.toXml());
		}
		return strOut.toString();
	}
	public String toString() {
		StringBuffer strOut = new StringBuffer();
		for ( OCSDrive o : drives ) {
			strOut.append(o.toString());
		}
		return strOut.toString();
	}
	public ArrayList<OCSSection> getSections() {
		ArrayList<OCSSection> lst = new ArrayList<OCSSection>();
		for ( OCSDrive o : drives ) {
			lst.add(o.getSection());
		}
		return lst;
	}
	
	private int parseInt(String s ) {
		int i;
		try {
			i=Integer.parseInt(s);
		} catch ( NumberFormatException e ) { i=0; }
		return i;
	}
	public String  getSectionTag() {
		return sectionTag;
	}
}
package org.ocsinventory.android.sections;

import java.util.ArrayList;

import android.os.Environment;

public class OCSStorages implements OCSSectionInterface
{
	final private String sectionTag = "STORAGES";
	
	public ArrayList<OCSStorage> storages;
	  
	public OCSStorages() {
		this.storages= new ArrayList<OCSStorage>();
		
		
		OCSStorage stExternal = new OCSStorage(Environment.getExternalStorageDirectory(), "External storage");
		OCSStorage stInternal = new OCSStorage(Environment.getDataDirectory(), "Internal storage");
		storages.add(stExternal);
		storages.add(stInternal);
	}

	public String toXML() {
		StringBuffer strOut = new StringBuffer();
		for ( OCSStorage o : storages ) {
			strOut.append(o.toXml());
		}
		return strOut.toString();
	}
	public String toString() {
		StringBuffer strOut = new StringBuffer();
		for ( OCSStorage o : storages ) {
			strOut.append(o.toString());
		}
		return strOut.toString();
	}
	public ArrayList<OCSSection> getSections() {
		ArrayList<OCSSection> lst = new ArrayList<OCSSection>();
		for ( OCSStorage o : storages ) {
			lst.add(o.getSection());
		}
		return lst;
	}
	public String  getSectionTag() {
		return sectionTag;
	}
}
package org.ocsinventory.android.sections;

import java.util.ArrayList;

public interface OCSSectionInterface {
	public String getSectionTag();
	// public long getSectioMask();
	public ArrayList<OCSSection> getSections();
	public String toString();
	public String toXML();
}

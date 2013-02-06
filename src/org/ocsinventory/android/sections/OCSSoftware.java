package org.ocsinventory.android.sections;

import org.ocsinventory.android.actions.Utils;

public class OCSSoftware
{
	  public String comments;
	  public String filesize;
	  public String folder;
	  public String installDate;
	  public String name;
	  public String publisher;
	  public String version;
	 
	public String toXml() {
		StringBuffer strOut = new StringBuffer();
		strOut.append("    <SOFTWARES>\n");
		Utils.xmlLine(strOut,6,"PUBLISHER", publisher);
		Utils.xmlLine(strOut,6,"NAME", name);
		Utils.xmlLine(strOut,6,"VERSION", version);
		Utils.xmlLine(strOut,6,"FOLDER", folder);
		Utils.xmlLine(strOut,6,"FILESIZE", filesize);
		Utils.xmlLine(strOut,6,"COMMENTS", "");
		Utils.xmlLine(strOut,6,"INSTALLDATE", "");		
		strOut.append("    </SOFTWARES>\n");
		return strOut.toString();
	}
	public String toString() {
		StringBuffer strOut = new StringBuffer();
		strOut.append("***SOFTWARE***\n");
		Utils.strLine(strOut,"PUBLISHER", publisher);
		Utils.strLine(strOut,"NAME", name);
		Utils.strLine(strOut,"VERSION", version);
		Utils.strLine(strOut,"FOLDER", folder);
		Utils.strLine(strOut,"FILESIZE", filesize);
		Utils.strLine(strOut,"COMMENTS", "");
		Utils.strLine(strOut,"INSTALLDATE", "");		
		return strOut.toString();
	}
}
package org.ocsinventory.android.sections;

import org.ocsinventory.android.actions.Utils;

public class OCSSoftware {
	public String comments;
	public String filesize;
	public String folder;
	public String installDate;
	public String name;
	public String publisher;
	public String version;

	public OCSSection getSection() {
		OCSSection s = new OCSSection("SOFTWARES");
		s.setAttr("PUBLISHER", publisher);
		s.setAttr("NAME", name);
		s.setAttr("VERSION", version);
		s.setAttr("FOLDER", folder);
		s.setAttr("FILESIZE", filesize);
		s.setAttr("COMMENTS", "");
		s.setAttr("INSTALLDATE", "");
		s.setTitle(name);
		return s;
	}

	public String toXml() {
		return getSection().toXML();
	}

	public String toString() {
		return getSection().toString();
	}

}
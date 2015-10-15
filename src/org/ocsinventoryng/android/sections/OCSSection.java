package org.ocsinventoryng.android.sections;
import org.ocsinventoryng.android.actions.OCSLog;

import java.util.HashMap;
import java.util.Map;

public class OCSSection  {
	String name;  // Section name ie BIOS
	String titre; // Section title for display
	Map<String, String> attrs;
	private OCSLog ocslog = OCSLog.getInstance();
	public OCSSection(String pName) {
		name = pName;
		attrs = new  HashMap<String, String>();
	}
	
	public void setAttr(String k, String v) {
		attrs.put(k,v);
	}
	public String toXML() {
		StringBuffer strOut = new StringBuffer("    <");
		strOut.append(name);
		strOut.append(">\n");
		for ( String k : attrs.keySet() ) {
			String v = attrs.get(k);
			xmlLine(strOut, k, v);			
		}
		strOut.append("    </");
		strOut.append(name);
		strOut.append(">\n");
		return strOut.toString();
	}
	public String toString() {
		StringBuffer strOut = new StringBuffer("");
		for ( String k : attrs.keySet() ) {
			ocslog.debug("Key : "+k);
			String v = attrs.get(k);
			ocslog.debug("Val : "+v);
			if ( v != null )
				strOut.append(k).append(": ").append(v).append("\n");
		}
		return strOut.toString();
	}
	public String getTitle() {
		return titre;
	}
	
	public void setTitle(String t ) {
		titre=t;
	}
	
	private void xmlLine(StringBuffer sbOut, String tag, String val) {
		xmlLine( sbOut, 6,  tag,  val);
	}
	private void xmlLine(StringBuffer sbOut, int n, String tag, String val) {
		for ( int i=0; i<n;i++)			
			sbOut.append(' ');
		if ( val == null )
			sbOut.append('<').append(tag).append("/>\n");
		else
			sbOut.append('<').append(tag).append('>').append(val)
				.append("</").append(tag).append(">\n");
	}
	

}

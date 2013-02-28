package org.ocsinventory.android.sections;
import java.util.HashMap;
import java.util.Map;

import org.ocsinventory.android.actions.Utils;

public class OCSSection  {
	String name;  // Section name ie BIOS
	String titre; // Section title for display
	Map<String, String> attrs;
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
			String v = attrs.get(k);
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

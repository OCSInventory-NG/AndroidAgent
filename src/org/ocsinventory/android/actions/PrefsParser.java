package org.ocsinventory.android.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.Preference;

public class PrefsParser extends DefaultHandler {
	private String responseText = "";
	private SharedPreferences prefs;
	private String keyName;
	private String keyValue;
	private Editor edit; 
	/*
	 * 
	 * <map> <boolean name="k_automode" value="true" /> <string
	 * name="k_freqmaj">24</string> <string name="k_freqwake">60</string>
	 * <string name="k_serverurl">http://localhost/ocsinventory</string>
	 * <boolean name="k_sslstrict" value="true" /> </map>
	 */

	public void parseDocument(File paramFile, SharedPreferences prefs) {
		android.util.Log.d("PARSE", "Start parseDocument ");
		this.prefs = prefs;
		edit = prefs.edit();

		SAXParserFactory localSAXParserFactory = SAXParserFactory.newInstance();
		try {
			SAXParser localSAXParser = localSAXParserFactory.newSAXParser();
			localSAXParser.parse(paramFile, this);
			return;
		} catch (SAXException localSAXException) {
			localSAXException.printStackTrace();
		} catch (ParserConfigurationException localParserConfigurationException) {
			localParserConfigurationException.printStackTrace();
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		}
	}

	public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
			throws SAXException {

		String str = new String(paramArrayOfChar, paramInt1, paramInt2);
		this.keyValue = str;
		android.util.Log.d("PARSE", "characters"+str);
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		android.util.Log.d("PARSE", "endElement");
		android.util.Log.d("PARSE", "uri   : "+uri);
		android.util.Log.d("PARSE", "lName : "+localName);
		android.util.Log.d("PARSE", "qName : "+qName);
		if ( qName.equals("map")) {
			edit.commit();
		} 
		else if  ( qName.equals("string") ) {
			if ( ! keyName.equals("k_deviceUid")) {
				android.util.Log.d("PARSE", keyName+"/"+keyValue);
				edit.putString(keyName, keyValue);
			}
		} 
	}

	public String getResponseText() {
		return this.responseText;
	}

	public void setResponseText(String paramString) {
		this.responseText = paramString;
	}

	public void startElement(String uri, String local,
			String qName, Attributes attributes)
			throws SAXException {

		keyName = attributes.getValue("", "name");
		keyValue= attributes.getValue("", "value");
		android.util.Log.d("PARSE", "startElement");
		android.util.Log.d("PARSE", "uri     : "+uri);
		android.util.Log.d("PARSE", "local   : "+local);
		android.util.Log.d("PARSE", "qName : "+qName);
 		if  ( qName.equalsIgnoreCase("boolean") ) {
 				android.util.Log.d("PARSE", keyName+"/"+keyValue);
 				edit.putBoolean(keyName, keyValue.equals("true"));
 		}
	}
}

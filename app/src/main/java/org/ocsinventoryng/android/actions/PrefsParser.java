/*
 * Copyright 2013-2016, OCSInventory-NG/AndroidAgent contributors
 *
 * This file is part of OCSInventory-NG/AndroidAgent.
 *
 * OCSInventory-NG/AndroidAgent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OCSInventory-NG/AndroidAgent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OCSInventory-NG/AndroidAgent. If not, see <http://www.gnu.org/licenses/>
 */
package org.ocsinventoryng.android.actions;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class PrefsParser extends DefaultHandler {
    private String responseText = "";
    private String keyName;
    private String keyValue;
    private Editor edit;

    public void parseDocument(File paramFile, SharedPreferences prefs) {
        Log.d("PARSE", "Start parseDocument ");
        edit = prefs.edit();

        SAXParserFactory localSAXParserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser localSAXParser = localSAXParserFactory.newSAXParser();
            localSAXParser.parse(paramFile, this);
        } catch (SAXException localSAXException) {
            localSAXException.printStackTrace();
        } catch (ParserConfigurationException localParserConfigurationException) {
            localParserConfigurationException.printStackTrace();
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }

    public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {

        String str = new String(paramArrayOfChar, paramInt1, paramInt2);
        this.keyValue = str;
        Log.d("PARSE", "characters" + str);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        Log.d("PARSE", "endElement");
        Log.d("PARSE", "uri   : " + uri);
        Log.d("PARSE", "lName : " + localName);
        Log.d("PARSE", "qName : " + qName);
        if ("map".equals(qName)) {
            edit.apply();
        } else if ("string".equals(qName) && !keyName.equals("k_deviceUid")) {
            Log.d("PARSE", keyName + "/" + keyValue);
            edit.putString(keyName, keyValue);
        }
    }

    public String getResponseText() {
        return this.responseText;
    }

    public void setResponseText(String paramString) {
        this.responseText = paramString;
    }

    public void startElement(String uri, String local, String qName, Attributes attributes) throws SAXException {

        keyName = attributes.getValue("", "name");
        keyValue = attributes.getValue("", "value");
        Log.d("PARSE", "startElement");
        Log.d("PARSE", "uri     : " + uri);
        Log.d("PARSE", "local   : " + local);
        Log.d("PARSE", "qName : " + qName);
        if (qName.equalsIgnoreCase("boolean")) {
            Log.d("PARSE", keyName + "/" + keyValue);
            edit.putBoolean(keyName, keyValue.equals("true"));
        }
    }
}

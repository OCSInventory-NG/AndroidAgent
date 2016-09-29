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

import android.util.Log;

import org.ocsinventoryng.android.agent.OCSDownloadIdParams;
import org.ocsinventoryng.android.agent.OCSPrologReply;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class PrologReplyParser extends DefaultHandler {
    private String currentTag;
    private OCSPrologReply reply;

    PrologReplyParser() {
        reply = new OCSPrologReply();
    }

    public OCSPrologReply parseDocument(String strReply) {

        Log.d("PrologReplyParser", strReply);
        ByteArrayInputStream bais = new ByteArrayInputStream(strReply.getBytes());

        return parseDocument(bais);
    }

    public OCSPrologReply parseDocument(InputStream is) {

        Log.d("PrologReplyParser", "");
        SAXParserFactory localSAXParserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser localSAXParser = localSAXParserFactory.newSAXParser();
            localSAXParser.parse(is, this);
        } catch (SAXException localSAXException) {
            localSAXException.printStackTrace();
        } catch (ParserConfigurationException localParserConfigurationException) {
            localParserConfigurationException.printStackTrace();
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
        return reply;
    }


    public void startElement(String uri, String local, String qName, Attributes attributes) throws SAXException {
        if ("PARAM".equals(qName)) {
            String id = attributes.getValue("", "ID");
            if (id != null) {
                OCSDownloadIdParams dip = new OCSDownloadIdParams();
                dip.setId(id);
                dip.setSchedule(attributes.getValue("", "SCHEDULE"));
                dip.setCertFile(attributes.getValue("", "CERT_FILE"));
                dip.setType(attributes.getValue("", "TYPE"));
                dip.setInfoLoc(attributes.getValue("", "INFO_LOC"));
                dip.setCertPath(attributes.getValue("", "CERT_PATH"));
                dip.setPackLoc(attributes.getValue("", "PACK_LOC"));
                dip.setForce(attributes.getValue("", "FORCE"));
                dip.setPostcmd(attributes.getValue("", "POSTCMD"));
                reply.getIdList().add(dip);
            } else {
                String frag_lat = attributes.getValue("", "FRAG_LATENCY");
                if (frag_lat != null) {
                    reply.setFragLatency(frag_lat);
                    reply.setPeriodLatency(attributes.getValue("", "PERIOD_LATENCY"));
                    reply.setOn(attributes.getValue("", "ON"));
                    reply.setType(attributes.getValue("", "TYPE"));
                    reply.setCycleLatency(attributes.getValue("", "CYCLE_LATENCY"));
                    reply.setTimeout(attributes.getValue("", "TIMEOUT"));
                    reply.setPeriodeLength(attributes.getValue("", "PERIOD_LENGTH"));
                    reply.setExecutionTimeout(attributes.getValue("", "EXECUTION_TIMEOUT"));
                }
            }
        }
        currentTag = qName;
    }

    public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {

        String str = new String(paramArrayOfChar, paramInt1, paramInt2);
        if ("RESPONSE".equals(currentTag)) {
            reply.setResponse(str);
        } else if ("PROLOG_FREQ".equals(currentTag)) {
            reply.setPrologFreq(str);
        } else if ("NAME".equals(currentTag)) {
            if (reply.getOptName() == null) {
                reply.setOptName(str);
            }
        }
    }
}

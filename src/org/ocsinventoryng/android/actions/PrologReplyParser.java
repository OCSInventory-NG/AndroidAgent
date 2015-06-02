package org.ocsinventoryng.android.actions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.ocsinventoryng.android.agent.OCSDownloadIdParams;
import org.ocsinventoryng.android.agent.OCSPrologReply;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PrologReplyParser extends DefaultHandler {
	private String curentTag;
	private OCSPrologReply reply;
	
	PrologReplyParser() {
		reply = new OCSPrologReply();
	}
	
	public OCSPrologReply parseDocument(String strReply) {
		
		android.util.Log.d("PrologReplyParser", strReply);
		ByteArrayInputStream bais = new  ByteArrayInputStream(strReply.getBytes());

		return parseDocument(bais);

	}
	public OCSPrologReply parseDocument(InputStream is) {
		
		android.util.Log.d("PrologReplyParser","");
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
	
	
	public void startElement(String uri, String local,
							String qName, Attributes attributes)
							throws SAXException {
		if ( qName.equals("PARAM")) {
			String id = attributes.getValue("", "ID");
			if ( id != null ) {
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
			}
			else {
				String frag_lat = attributes.getValue("", "FRAG_LATENCY");
				if ( frag_lat != null ) {
					reply.setFrag_latency(frag_lat);
					reply.setPeriod_latency(attributes.getValue("", "PERIOD_LATENCY"));
					reply.setOn(attributes.getValue("", "ON"));
					reply.setType(attributes.getValue("", "TYPE"));
					reply.setCycle_latency(attributes.getValue("", "CYCLE_LATENCY"));
					reply.setTimeout(attributes.getValue("", "TIMEOUT"));
					reply.setPeriode_length(attributes.getValue("", "PERIOD_LENGTH"));
					reply.setExecution_timeout(attributes.getValue("", "EXECUTION_TIMEOUT"));}
			}
		}
		curentTag=qName;
	}
	
	public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
			throws SAXException {

		String str = new String(paramArrayOfChar, paramInt1, paramInt2);
		if ( curentTag.equals("RESPONSE")) {
			reply.setResponse(str);
		} else if ( curentTag.equals("PROLOG_FREQ") ) {
			reply.setPrologFreq(str);
		} else if ( curentTag.equals("NAME") ) {
			if ( reply.getOptName() == null ) {
				reply.setOptName(str);
			}
		} 
	}
}

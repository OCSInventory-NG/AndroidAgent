package org.ocsinventory.android.sections;

import java.util.ArrayList;

import org.ocsinventory.android.actions.OCSLog;
import org.ocsinventory.android.actions.Utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.provider.Settings.Secure;
import android.text.format.DateFormat;

/*
 * 
     <HARDWARE>
	  <OSCOMMENTS>#32-Ubuntu SMP Tue Dec 11 18:52:46 UTC 2012</OSCOMMENTS>
      <OSNAME>Ubuntu 12.10</OSNAME>
      <OSVERSION>3.5.0-21-generic</OSVERSION>
      <PROCESSORN>2</PROCESSORN>
      <PROCESSORS>1596</PROCESSORS>
      <PROCESSORT>Intel(R) Core(TM)2 Duo CPU     E7200  @ 2.53GHz</PROCESSORT>
      <DESCRIPTION>i686/00-00-00 02:51:22</DESCRIPTION>
      <DATELASTLOGGEDUSER>Fri Jan  4 21:53</DATELASTLOGGEDUSER>
      <LASTLOGGEDUSER>jce</LASTLOGGEDUSER>
      <MEMORY>4039</MEMORY>
      <NAME>UBUNTU-JCE</NAME>
	   <SWAP>972</SWAP>
      <USERID>jce</USERID>
      <UUID>C01D9E89-8DFE-D511-93B8-001FC6B6A11E</UUID>
      <VMSYSTEM>Physical</VMSYSTEM>
      <WORKGROUP>WORKGROUP</WORKGROUP>
		<DEFAULTGATEWAY>192.168.0.254</DEFAULTGATEWAY>
		<DNS>127.0.1.1</DNS>
      <IPADDR>192.168.0.10</IPADDR>
 
       <CHECKSUM>262143</CHECKSUM>
    </HARDWARE>
 */

public class OCSHardware implements OCSSectionInterface  {
	final private String sectionTag = "HARDWARE";

	private long checksum;
	private String processorType;
	private String processorNumber;
	private String processorSpeed;
	private String memory;
	private String swap;
	private String name;
	private String systemName;
	private String systemVersion;
	private String osComment;
	private String description;
	private String userid;

	private String ipAddress;
	private String gateway;
	private String dns;
	private String dateLastLog ;
	private String lastUser ;
	private String uuid ;
	
	public OCSHardware() {
		logBuild();
		name = Build.MODEL;
		this.checksum = 262143; 			// TODO 262143
		this.systemVersion = Build.VERSION.RELEASE;
		this.systemName = "Android "+this.systemVersion;
		this.ipAddress = "";
		this.processorSpeed = String.valueOf(SystemInfos.getProcessorSpeed()/1000);
		this.memory=String.valueOf(SystemInfos.getMemtotal()/1024);
		this.swap=String.valueOf(SystemInfos.getSwaptotal()/1024);
		this.userid = Build.USER;
		this.lastUser = Build.USER;
		this.dateLastLog = (String) DateFormat.format("MM/dd/yy hh:mm:ss", System.currentTimeMillis());
	}
	public String getProcessorName() {
		return SystemInfos.getProcessorName();
	}
	public String getProcessorType() {
		return Build.CPU_ABI;
	}
	public String getProcessorNumber() {
		return String.valueOf(SystemInfos.getProcessorNumber());
	}
	public String getName() {
		return name;
	}
	public long getChecksum() {
		return checksum;
	}
	public String getProcessorSpeed() {
		return processorSpeed;
	}
	public String getMemory() {
		return memory;
	}
	public String getSwap() {
		return swap;
	}
	public String getSystemName() {
		return systemName;
	}
	public String getSystemVersion() {
		return systemVersion;
	}
	public String getDescription() {
		return description;
	}
	public String getUserid() {
		return userid;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public String getGateway() {
		return gateway;
	}
	public String getDns() {
		return dns;
	}
	public String getDateLastLog() {
		return dateLastLog;
	}
	public String getLastUser() {
		return lastUser;
	}
	public String getUuid() {
		return uuid;
	}
	public String getOsComment() {
		return osComment;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setChecksum(long checksum) {
		this.checksum = checksum;
	}

	public OCSSection getSection() {
		OCSSection s = new OCSSection(sectionTag);
		s.setTitle(name);
		s.setAttr("NAME", name);
		s.setAttr("WORKGROUP", "WORKGROUP");
		s.setAttr("USERDOMAIN", "");
		s.setAttr("OSNAME",this.getSystemName());
		s.setAttr("OSVERSION",this.getSystemVersion());
		s.setAttr("OSCOMMENT",this.getOsComment());
		s.setAttr("PROCESSORT",this.getProcessorType());
		s.setAttr("PROCESSORN",this.getProcessorNumber());
		s.setAttr("PROCESSORS",this.getProcessorSpeed());
		s.setAttr("MEMORY",this.getMemory());
		s.setAttr("SWAP",this.getSwap());
		s.setAttr("USERID",this.getUserid());
		s.setAttr("CHECKSUM",String.valueOf(this.getChecksum()));
		s.setAttr("IPADDR",this.getIpAddress());
		s.setAttr("DEFAULTGATEWAY",this.getGateway());		
		s.setAttr("DNS",this.getDns());
		s.setAttr("LASTLOGGEDUSER",this.getLastUser());
		s.setAttr("DATELASTLOGGEDUSER",this.getDateLastLog());
		return s;
	}
	public ArrayList<OCSSection> getSections() {
		ArrayList<OCSSection> lst = new ArrayList<OCSSection>();
		lst.add(getSection());
		return lst;
	}
	public String toXML() {
		return getSection().toXML();
	}

	public String toString() {
		return getSection().toString();
	}	
	public String  getSectionTag() {
		return sectionTag;
	}

	@SuppressLint("NewApi")
	void logBuild() {
		OCSLog ocslog = OCSLog.getInstance();
		
		ocslog.append("BOARD      : "+Build.BOARD);
		ocslog.append("BOOTLOADER : "+Build.BOOTLOADER);
		ocslog.append("BRAND      : "+Build.BRAND);
		ocslog.append("CPU_ABI    : "+Build.CPU_ABI);		
		ocslog.append("CPU_ABI2   : "+Build.CPU_ABI2);		
		ocslog.append("DEVICE     : "+Build.DEVICE);		
		ocslog.append("DISPLAY    : "+Build.DISPLAY);		
		ocslog.append("FINGERPRINT: "+Build.FINGERPRINT);
		ocslog.append("HARDWARE   : "+Build.HARDWARE);	
		ocslog.append("HOST       : "+Build.HOST);
		ocslog.append("ID         : "+Build.ID);
		ocslog.append("HARDWARE     : "+Build.HARDWARE);
		ocslog.append("MANUFACTURER : "+Build.MANUFACTURER);
		ocslog.append("PRODUCT      : "+Build.PRODUCT);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO ) {
			ocslog.append("SERIAL       : "+Build.SERIAL);
		}	
		ocslog.append("TIME         : "+Build.TIME);
		ocslog.append("TYPE         : "+Build.TYPE);
		ocslog.append("USER         : "+Build.USER);		
	}
}

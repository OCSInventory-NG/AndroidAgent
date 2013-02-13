package org.ocsinventory.android.sections;

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

public class OCSHardware {
	private String checksum;

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
		this.checksum = "1234567892"; 			// TODO
		this.systemVersion = Build.VERSION.RELEASE;
		this.systemName = "android "+this.systemVersion;
		this.ipAddress = "";
		this.processorSpeed = String.valueOf(SystemInfos.getProcessorSpeed()/1000);
		this.memory=String.valueOf(SystemInfos.getMemtotal()/1024);
		this.swap=String.valueOf(SystemInfos.getSwaptotal()/1024);
		this.userid = Build.USER;
		this.lastUser = Build.USER;
		this.dateLastLog = (String) DateFormat.format("MM/dd/yy mm:ss", System.currentTimeMillis());
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
	public String getChecksum() {
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

	public String toXML() {
		StringBuffer strOut = new StringBuffer("    <HARDWARE>\n");
		Utils.xmlLine(strOut,"NAME",this.getName());
		strOut.append("      <WORKGROUP>WORKGROUP</WORKGROUP>\n");
		strOut.append("      <USERDOMAIN />\n");
		Utils.xmlLine(strOut,"OSNAME",this.getSystemName());
		Utils.xmlLine(strOut,"OSVERSION",this.getSystemVersion());
		Utils.xmlLine(strOut,"OSCOMMENT",this.getOsComment());
		Utils.xmlLine(strOut,"PROCESSORT",this.getProcessorType());
		Utils.xmlLine(strOut,"PROCESSORN",this.getProcessorNumber());
		Utils.xmlLine(strOut,"PROCESSORS",this.getProcessorSpeed());
		Utils.xmlLine(strOut,"MEMORY",this.getMemory());
		Utils.xmlLine(strOut,"SWAP",this.getSwap());
		Utils.xmlLine(strOut,"USERID",this.getUserid());
		Utils.xmlLine(strOut,"CHECKSUM",this.getChecksum());
		Utils.xmlLine(strOut,"IPADDR",this.getIpAddress());
		Utils.xmlLine(strOut,"DEFAULTGATEWAY",this.getGateway());		
		Utils.xmlLine(strOut,"DNS",this.getDns());
		Utils.xmlLine(strOut,"LASTLOGGEDUSER",this.getLastUser());
		Utils.xmlLine(strOut,"DATELASTLOGGEDUSER",this.getDateLastLog());
		strOut.append("    </HARDWARE>\n");	
		return strOut.toString();	
	}
	public String toString() {
		StringBuffer strOut = new StringBuffer("***HARDWARE***\n");
		Utils.strLine(strOut,"NAME",this.getName());
		Utils.strLine(strOut,"OSNAME",this.getSystemName());
		Utils.strLine(strOut,"OSVERSION",this.getSystemVersion());
		Utils.strLine(strOut,"OSCOMMENT",this.getOsComment());
		Utils.strLine(strOut,"PROCESSORT",this.getProcessorType());
		Utils.strLine(strOut,"PROCESSORN",this.getProcessorNumber());
		Utils.strLine(strOut,"PROCESSORS",this.getProcessorSpeed());
		Utils.strLine(strOut,"MEMORY",this.getMemory());
		Utils.strLine(strOut,"SWAP",this.getSwap());
		Utils.strLine(strOut,"USERID",this.getUserid());
		Utils.strLine(strOut,"CHECKSUM",this.getChecksum());
		Utils.strLine(strOut,"IPADDR",this.getIpAddress());
		Utils.strLine(strOut,"DEFAULTGATEWAY",this.getGateway());		
		Utils.strLine(strOut,"DNS",this.getDns());
		Utils.strLine(strOut,"LASTLOGGEDUSER",this.getLastUser());
		Utils.strLine(strOut,"DATELASTLOGGEDUSER",this.getDateLastLog());
		return strOut.toString();	
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

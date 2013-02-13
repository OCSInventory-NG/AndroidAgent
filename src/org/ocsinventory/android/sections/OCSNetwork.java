package org.ocsinventory.android.sections;

import org.ocsinventory.android.actions.Utils;

public class OCSNetwork
{

	public String description;
	  public String driver;
	  public String ipAdress;
	  public String ipDHCP;
	  public String ipGateway;
	  public String ipMask;
	  public String ipSubnet;
	  public String macaddr;	  
	  public String status;
	  public String type;

	  public String dns1;
	  public String dns2;
	  
	  public OCSNetwork(String desc) {
		  this.description = desc;
	  }
	/*
	 * NETWORKS><DESCRIPTION>eth0</DESCRIPTION>
	 * <DRIVER>atl1</DRIVER>
	 * <IPADDRESS>192.168.0.10</IPADDRESS>
	 * <IPDHCP/>
	 * <IPGATEWAY>192.168.0.254</IPGATEWAY>
	 * <IPMASK>255.255.255.0</IPMASK>
	 * <IPSUBNET>192.168.0.0</IPSUBNET>
	 * <MACADDR>00:1f:c6:b6:a1:1e</MACADDR>
	 * <PCISLOT>0000:02:00.0</PCISLOT>
	 * <STATUS>Up</STATUS>macaddr
	 * <TYPE>Ethernet</TYPE>
	 * <VIRTUALDEV/></NETWORKS>
	 */
	public String toXml() {
		StringBuffer strOut = new StringBuffer();
		strOut.append("    <NETWORKS>\n");
		Utils.xmlLine(strOut,"DESCRIPTION", description);
		Utils.xmlLine(strOut,"DRIVER", driver);
		Utils.xmlLine(strOut,"IPADDRESS", ipAdress);
		//Utils.xmlLine(strOut,"IPDHCP", ipdhcp);
		Utils.xmlLine(strOut,"IPGATEWAY", ipGateway);
		Utils.xmlLine(strOut,"IPMASK", ipMask);
		Utils.xmlLine(strOut,"IPSUBNET", ipSubnet);
		Utils.xmlLine(strOut,"MACADDR", macaddr);
		Utils.xmlLine(strOut,"STATUS", status);
		Utils.xmlLine(strOut,"TYPE", type);
		strOut.append("    </NETWORKS>\n");
		return strOut.toString();
	}
	public String toString() {
		StringBuffer strOut = new StringBuffer("***NETWORK***\n");
		Utils.strLine(strOut,"DESCRIPTION", description);
		Utils.strLine(strOut,"DRIVER", driver);
		Utils.strLine(strOut,"IPADRESS", ipAdress);
		//Utils.xmlLine(strOut,"IPDHCP", ipdhcp);
		Utils.strLine(strOut,"IPGATEWAY", ipGateway);
		Utils.strLine(strOut,"IPMASK", ipMask);
		Utils.strLine(strOut,"IPSUBNET", ipSubnet);
		Utils.strLine(strOut,"STATUS", status);
		Utils.strLine(strOut,"MACADDR", macaddr);
		Utils.strLine(strOut,"TYPE", type);
		return strOut.toString();
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getIpAdress() {
		return ipAdress;
	}
	public void setIpAdress(String ipAdress) {
		this.ipAdress = ipAdress;
	}
	public String getIpDHCP() {
		return ipDHCP;
	}
	public void setIpDHCP(String ipDHCP) {
		this.ipDHCP = ipDHCP;
	}
	public String getIpGateway() {
		return ipGateway;
	}
	public void setIpGatewey(String ipGateway) {
		this.ipGateway = ipGateway;
	}
	public String getIpMask() {
		return ipMask;
	}
	public void setIpMask(String ipMask) {
		this.ipMask = ipMask;
	}
	public String getIpSubnet() {
		return ipSubnet;
	}
	public void setIpSubnet(String ipSubnet) {
		this.ipSubnet = ipSubnet;
	}
	public String getMacaddr() {
		return macaddr;
	}
	public void setMacaddr(String macaddr) {
		this.macaddr = macaddr;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	  public String getDns1() {
		return dns1;
	}
	public void setDns1(String dns1) {
		this.dns1 = dns1;
	}
	public String getDns2() {
		return dns2;
	}
	public void setDns2(String dns2) {
		this.dns2 = dns2;
	}
}
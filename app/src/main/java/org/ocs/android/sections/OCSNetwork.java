/*
 * Copyright 2013-2016 OCSInventory-NG/AndroidAgent contributors : mortheres, cdpointpoint,
 * Cédric Cabessa, Nicolas Ricquemaque, Anael Mobilia
 *
 * This file is part of OCSInventory-NG/AndroidAgent.
 *
 * OCSInventory-NG/AndroidAgent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * OCSInventory-NG/AndroidAgent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OCSInventory-NG/AndroidAgent. if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.ocs.android.sections;


public class OCSNetwork {
    public static String STATUS_UP = "Up";
    public static String STATUS_DOWN = "Down";

    private String description;
    private String driver;
    private String ipAdress;
    private String ipDHCP;
    private String ipGateway;
    private String ipMask;
    private String ipSubnet;
    private String macaddr;
    private String status;
    private String type;
    private String speed;
    private String dns1;
    private String dns2;

    public OCSNetwork(String desc) {
        this.description = desc;
    }

    /*
     * <NETWORKS><DESCRIPTION>eth0</DESCRIPTION> <DRIVER>atl1</DRIVER>
     * <IPADDRESS>192.168.0.10</IPADDRESS> <IPDHCP/>
     * <IPGATEWAY>192.168.0.254</IPGATEWAY> <IPMASK>255.255.255.0</IPMASK>
     * <IPSUBNET>192.168.0.0</IPSUBNET> <MACADDR>00:1f:c6:b6:a1:1e</MACADDR>
     * <PCISLOT>0000:02:00.0</PCISLOT> <STATUS>Up</STATUS>macaddr
     * <TYPE>Ethernet</TYPE> <VIRTUALDEV/></NETWORKS>
     */
    public OCSSection getSection() {
        OCSSection s = new OCSSection("NETWORKS");
        s.setAttr("DESCRIPTION", description);
        s.setAttr("DRIVER", driver);
        s.setAttr("IPADDRESS", ipAdress);
        s.setAttr("IPDHCP", ipDHCP);
        s.setAttr("IPGATEWAY", ipGateway);
        s.setAttr("IPMASK", ipMask);
        s.setAttr("IPSUBNET", ipSubnet);
        s.setAttr("MACADDR", macaddr);
        s.setAttr("STATUS", status);
        s.setAttr("TYPE", type);
        s.setAttr("SPEED", speed);
        s.setTitle(type);
        return s;
    }

    public String toXml() {
        return getSection().toXML();
    }

    public String toString() {
        return getSection().toString();
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

    public void setSpeed(String speed) {
        this.speed = speed;
    }
}
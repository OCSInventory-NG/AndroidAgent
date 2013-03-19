package org.ocsinventory.android.sections;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.ocsinventory.android.actions.OCSLog;
import org.ocsinventory.android.actions.Utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

public class OCSNetworks implements OCSSectionInterface
{
	final private String sectionTag = "NETWORKS";
	private OCSLog ocslog;
	private ArrayList<OCSNetwork> networks;
	private int main = 0;
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public OCSNetworks(Context ctx) {
		ocslog = OCSLog.getInstance();

		this.networks= new ArrayList<OCSNetwork>();

		WifiManager wifii= (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		boolean prevWifiState=false;
		if ( wifii != null ) {
			if ( wifii.getWifiState() == 4 )
				return;
			prevWifiState = wifii.isWifiEnabled(); 
			if ( ! wifii.isWifiEnabled() ) {
				wifii.setWifiEnabled(true);
			}
			if ( wifii.isWifiEnabled() ) {
				DhcpInfo d=wifii.getDhcpInfo();
			
				OCSNetwork netw = new OCSNetwork("Wifi/3G interface");
				
				if  ( wifii.getWifiState()== wifii.WIFI_STATE_ENABLED )
					netw.setStatus("Up");
				else 
					netw.setStatus("Down");
				
				netw.setIpAdress(Utils.intToIp(d.ipAddress));
				netw.setIpGatewey(Utils.intToIp(d.gateway));
				netw.setIpMask(Utils.intToIp(d.netmask));
				netw.setIpDHCP(Utils.intToIp(d.serverAddress));
	
				WifiInfo wInfos = wifii.getConnectionInfo();
				netw.setMacaddr(wInfos.getMacAddress());
				netw.setDriver("Wifi");
				netw.setType("Wifi");

				String speed = String.valueOf(wInfos. getLinkSpeed());
				netw.setSpeed(speed+" Mb/s");
			
				//String s_dns1="DNS 1: "+String.valueOf(d.dns1);
			    //String s_dns2="DNS 2: "+String.valueOf(d.dns2);
	
			    networks.add(netw);
			}
		}
		
		// Parcours des intefaces reseau
		Enumeration<NetworkInterface> listeNI;
		try {
			listeNI = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			ocslog.append("Error : during call getNetworkInterfaces()");
			ocslog.append(e.getMessage());
			return;
		}
		while (listeNI.hasMoreElements()) {
			NetworkInterface ni = (NetworkInterface) listeNI.nextElement();
			Enumeration<InetAddress> listeIPAdr = ni.getInetAddresses();
			String name = ni.getName();
			
			ocslog.append("OCSNET Name :"+ni.getName());
			// android.util.Log.d("OCSNET HAdr", ni.getHardwareAddress());
			while (listeIPAdr.hasMoreElements()) {
				InetAddress ipAdr = (InetAddress) listeIPAdr.nextElement();
				
				if ( ! ipAdr.isLoopbackAddress() ) {
					OCSNetwork netw = new OCSNetwork(name);
					String ipadr = ipAdr.getHostAddress();
					netw.setIpAdress(ipadr);
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO ) {
					 	try {
					 		netw.setMacaddr(Utils.bytesToHex(ni.getHardwareAddress()));
						} catch (SocketException se) {}
					}
					networks.add(netw);
				}
			} 
		}
		if ( wifii.isWifiEnabled() && ! prevWifiState)
				wifii.setWifiEnabled(false);

	}
	
/*
 * ex Linux
 <NETWORKS>
      <DESCRIPTION>eth0</DESCRIPTION>
      <DRIVER>atl1</DRIVER>
      <IPADDRESS>192.168.0.10</IPADDRESS>
      <IPDHCP></IPDHCP>
      <IPGATEWAY>192.168.0.254</IPGATEWAY>
      <IPMASK>255.255.255.0</IPMASK>
      <IPSUBNET>192.168.0"SIMS".0</IPSUBNET>
      <MACADDR>00:1f:c6:b6:a1:1e</MACADDR>
      <PCISLOT>0000:02:00.0</PCISLOT>
      <STATUS>Up</STATUS>
      <TYPE>Ethernet</TYPE>
      <VIRTUALDEV></VIRTUALDEV>
    </NETWORKS>
 */
	public String toXML() {
		StringBuffer strOut = new StringBuffer();
		for ( OCSNetwork o : networks ) {
			strOut.append(o.toXml());
		}
		return strOut.toString();
	}
	public String toString() {
		StringBuffer strOut = new StringBuffer();
		for ( OCSNetwork o : networks ) {
			strOut.append(o.toString());
		}
		return strOut.toString();
	}
	public int getMain() {
		return main;
	}
	public ArrayList<OCSNetwork> getNetworks() {
		return networks;
	}
	public ArrayList<OCSSection> getSections() {
		ArrayList<OCSSection> lst = new ArrayList<OCSSection>();
		for ( OCSNetwork o : networks ) {
			lst.add(o.getSection());
		}
		return lst;
	}
	public String  getSectionTag() {
		return sectionTag;
	}
}
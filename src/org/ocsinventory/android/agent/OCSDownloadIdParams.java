package org.ocsinventory.android.agent;


/*
 * <PARAM ID="1365231890" SCHEDULE="" CERT_FILE="INSTALL_PATH/cacert.pem" TYPE="PACK" INFO_LOC="localhost/download" CERT_PATH="INSTALL_PATH" PACK_LOC="localhost/download" FORCE="0" POSTCMD="" />
 * 
 */
public class OCSDownloadIdParams {
	String id;
	String schedule;
	String certFile;
	String type;
	String infoLoc;
	String certPath;
	String packLoc;
	boolean force;
	String postcmd;
	
	OCSDownloadInfos infos=null;
	int downloaded=0;


	public int getDownloaded() {
		return downloaded;
	}
	public void setDownloaded(int downloaded) {
		this.downloaded = downloaded;
	}
	public OCSDownloadInfos getInfos() {
		return infos;
	}
	public void setInfos(OCSDownloadInfos infos) {
		this.infos = infos;
	}
	public String getId() {
		return id;
	}
	public void setId(String str) {
		this.id = str;
	}
	public String getSchedule() {
		return schedule;
	}
	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}
	public String getCertFile() {
		return certFile;
	}
	public void setCertFile(String certFile) {
		this.certFile = certFile;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getInfoLoc() {
		return infoLoc;
	}
	public void setInfoLoc(String infoLoc) {
		this.infoLoc = infoLoc;
	}
	public String getCertPath() {
		return certPath;
	}
	public void setCertPath(String certPath) {
		this.certPath = certPath;
	}
	public String getPackLoc() {
		return packLoc;
	}
	public void setPackLoc(String packLoc) {
		this.packLoc = packLoc;
	}
	public boolean isForce() {
		return force;
	}
	public void setForce(boolean force) {
		this.force = force;
	}
	public void setForce(String str) {
		if ( str == null) return;
		this.force = str.equals("1");
	}

	public String getPostcmd() {
		return postcmd;
	}
	public void setPostcmd(String postcmd) {
		this.postcmd = postcmd;
	}
		
}

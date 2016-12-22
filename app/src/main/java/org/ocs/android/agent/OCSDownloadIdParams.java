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
package org.ocs.android.agent;


/*
 * <PARAM ID="1365231890" SCHEDULE="" CERT_FILE="INSTALL_PATH/cacert.pem" TYPE="PACK" INFO_LOC="localhost/download"
 * CERT_PATH="INSTALL_PATH" PACK_LOC="localhost/download" FORCE="0" POSTCMD="" />
 * 
 */
public class OCSDownloadIdParams {
    private String id;
    private String schedule;
    private String certFile;
    private String type;
    private String infoLoc;
    private String certPath;
    private String packLoc;
    private boolean force;
    private String postcmd;

    private OCSDownloadInfos infos = null;
    private int downloaded = 0;


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
        if (str == null) {
            return;
        }
        this.force = "1".equals(str);
    }

    public String getPostcmd() {
        return postcmd;
    }

    public void setPostcmd(String postcmd) {
        this.postcmd = postcmd;
    }
}

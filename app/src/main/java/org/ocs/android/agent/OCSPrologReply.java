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

import java.util.ArrayList;


/*
 * <PARAM ID="1365231890"
 *  SCHEDULE="" CERT_FILE="INSTALL_PATH/cacert.pem" TYPE="PACK" INFO_LOC="localhost/download" 
 *  CERT_PATH="INSTALL_PATH" PACK_LOC="localhost/download" FORCE="0" POSTCMD="" />
 * 
 */
public class OCSPrologReply {
    final private int DEF_PERIODE_LENGTH = 10;

    private String type;
    private String response;
    private String prologFreq;
    private String optName;

    private int periodLatency;        // Wait between 2 periods of deployment  (def 1 sec)
    private int cycleLatency;        // Wait between 2 cycles (def 60 sec )
    private int fragLatency;        // Wait between 2 fragment download	( def. 10 sec )
    private int timeout;            // Validity of a package from 1st consideration
    private int periodeLength;        // Nombre de cycle dans la periode def 10
    private int executionTimeout;
    private boolean on;
    private ArrayList<OCSDownloadIdParams> idList;

    public OCSPrologReply() {
        idList = new ArrayList<>();
        optName = null;
        response = "";
    }

    public String log() {
        StringBuilder sb = new StringBuilder();
        sb.append("OPTION: ").append(getOptName()).append("\n");
        sb.append("prologFreq: ").append(getPrologFreq()).append("\n");
        sb.append("periodLatency: ").append(getPeriodLatency()).append("\n");
        sb.append("cycleLatency: ").append(getCycleLatency()).append("\n");
        for (OCSDownloadIdParams dip : getIdList()) {
            sb.append("PARAM ID: ").append(dip.getId()).append("TYPE:").append(dip.getType()).append("\n");
        }
        return sb.toString();
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getPrologFreq() {
        return prologFreq;
    }

    public void setPrologFreq(String prologFreq) {
        this.prologFreq = prologFreq;
    }

    public String getOptName() {
        return optName;
    }

    public void setOptName(String optName) {
        this.optName = optName;
    }

    /**
     * Wait between 2 fragment download	( def. 10 sec )
     */
    public int getFragLatency() {
        return fragLatency;
    }

    public void setFragLatency(int fragLatency) {
        this.fragLatency = fragLatency;
    }

    public void setFragLatency(String str) {
        try {
            setFragLatency(Integer.parseInt(str));
        } catch (NumberFormatException e) {
        }
    }

    /**
     * Wait between 2 periods of deployment  (def 1 sec)
     */
    public int getPeriodLatency() {
        return periodLatency;
    }

    public void setPeriodLatency(int periodLatency) {
        this.periodLatency = periodLatency;
    }

    public void setPeriodLatency(String str) {
        try {
            setPeriodLatency(Integer.parseInt(str));
        } catch (NumberFormatException e) {
        }
    }

    /**
     * Wait between 2 cycles (def 60 sec )
     */
    public int getCycleLatency() {
        return cycleLatency;
    }

    public void setCycleLatency(int cycleLatency) {
        this.cycleLatency = cycleLatency;
    }

    public void setCycleLatency(String str) {
        try {
            setCycleLatency(Integer.parseInt(str));
        } catch (NumberFormatException e) {
        }
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setTimeout(String str) {
        try {
            this.timeout = Integer.parseInt(str);
        } catch (NumberFormatException e) {
        }
    }

    public int getPeriodeLength() {
        return periodeLength;
    }

    public void setPeriodeLength(int periodeLength) {
        this.periodeLength = periodeLength;
    }

    public void setPeriodeLength(String str) {
        try {
            setPeriodeLength(Integer.parseInt(str));
        } catch (NumberFormatException e) {
            setPeriodeLength(DEF_PERIODE_LENGTH);
        }
    }

    public int getExecutionTimeout() {
        return executionTimeout;
    }

    public void setExecutionTimeout(int executionTimeout) {
        this.executionTimeout = executionTimeout;
    }

    public void setExecutionTimeout(String str) {
        try {
            setExecutionTimeout(Integer.parseInt(str));
        } catch (NumberFormatException e) {
        }
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public void setOn(String str) {
        if (str == null) {
            return;
        }
        this.on = "1".equals(str);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<OCSDownloadIdParams> getIdList() {
        return idList;
    }

    public void setIdList(ArrayList<OCSDownloadIdParams> idList) {
        this.idList = idList;
    }
}

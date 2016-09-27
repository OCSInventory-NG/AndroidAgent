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
package org.ocsinventoryng.android.agent;

import java.util.ArrayList;


/*
 * <PARAM ID="1365231890"
 *  SCHEDULE="" CERT_FILE="INSTALL_PATH/cacert.pem" TYPE="PACK" INFO_LOC="localhost/download" 
 *  CERT_PATH="INSTALL_PATH" PACK_LOC="localhost/download" FORCE="0" POSTCMD="" />
 * 
 */
public class OCSPrologReply {
    final private int DEF_PERIODE_LENGTH = 10;

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
    public int getFrag_latency() {
        return frag_latency;
    }

    public void setFrag_latency(int frag_latency) {
        this.frag_latency = frag_latency;
    }

    public void setFrag_latency(String str) {
        try {
            this.frag_latency = Integer.parseInt(str);
        } catch (NumberFormatException e) {
        }
    }

    /**
     * Wait between 2 periods of deployment  (def 1 sec)
     */
    public int getPeriod_latency() {
        return period_latency;
    }

    public void setPeriod_latency(int period_latency) {
        this.period_latency = period_latency;
    }

    public void setPeriod_latency(String str) {
        try {
            this.period_latency = Integer.parseInt(str);
        } catch (NumberFormatException e) {
        }
    }

    /**
     * Wait between 2 cycles (def 60 sec )
     */
    public int getCycle_latency() {
        return cycle_latency;
    }

    public void setCycle_latency(int cycle_latency) {
        this.cycle_latency = cycle_latency;
    }

    public void setCycle_latency(String str) {
        try {
            this.cycle_latency = Integer.parseInt(str);
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

    public int getPeriode_length() {
        return periode_length;
    }

    public void setPeriode_length(int periode_length) {
        this.periode_length = periode_length;
    }

    public void setPeriode_length(String str) {
        try {
            this.periode_length = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            this.periode_length = DEF_PERIODE_LENGTH;
        }
    }

    public int getExecution_timeout() {
        return execution_timeout;
    }

    public void setExecution_timeout(int execution_timeout) {
        this.execution_timeout = execution_timeout;
    }

    public void setExecution_timeout(String str) {
        if (str == null) {
            return;
        }
        try {
            this.execution_timeout = Integer.parseInt(str);
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
        this.on = str.equals("1");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

    public ArrayList<OCSDownloadIdParams> getIdList() {
        return idList;
    }

    public void setIdList(ArrayList<OCSDownloadIdParams> idList) {
        this.idList = idList;
    }

    private String response;
    private String prologFreq;
    private String optName;

    private int period_latency;        // Wait between 2 periods of deployment  (def 1 sec)
    private int cycle_latency;        // Wait between 2 cycles (def 60 sec )
    private int frag_latency;        // Wait between 2 fragment download	( def. 10 sec )
    private int timeout;            // Validity of a package from 1st consideration
    private int periode_length;        // Nombre de cycle dans la periode def 10
    private int execution_timeout;
    private boolean on;


    ArrayList<OCSDownloadIdParams> idList;

    public OCSPrologReply() {
        idList = new ArrayList<OCSDownloadIdParams>();
        optName = null;
        response = "";
    }

    public String log() {
        StringBuilder sb = new StringBuilder();
        sb.append("OPTION: ").append(optName).append("\n");
        sb.append("prologFreq: ").append(prologFreq).append("\n");
        sb.append("period_latency: ").append(period_latency).append("\n");
        sb.append("cycle_latency: ").append(cycle_latency).append("\n");
        for (OCSDownloadIdParams dip : idList) {
            sb.append("PARAM ID: ").append(dip.getId()).append("TYPE:").append(dip.getType()).append("\n");
        }
        return sb.toString();
    }
}

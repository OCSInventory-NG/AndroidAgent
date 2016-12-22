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

import android.util.Log;

public class OCSDownloadInfos {
    private String id;
    private int pri;
    private String act;
    private String digest;
    private String proto;
    private int frags;
    private String digestAlgo;
    private String digestEncode;
    private String path;
    private String notifyText;
    private String notifyCountdown;
    private boolean notifyUser;
    private boolean notifyCanAbort;
    private boolean notifyCanDelay;
    private boolean needDoneAction;
    private String needDoneActionText;


    public OCSDownloadInfos(String strinfos) {
        id = extrAttr(strinfos, "ID");
        act = extrAttr(strinfos, "ACT");
        digest = extrAttr(strinfos, "DIGEST");
        proto = extrAttr(strinfos, "PROTO");
        digestAlgo = extrAttr(strinfos, "DIGEST_ALGO");
        digestEncode = extrAttr(strinfos, "DIGEST_ENCODE");
        path = extrAttr(strinfos, "PATH");
        notifyCountdown = extrAttr(strinfos, "NOTIFY_COUNTDOWN");
        notifyCanAbort = extrAttr(strinfos, "NOTIFY_CAN_ABORT").equals("1");
        notifyCanDelay = extrAttr(strinfos, "NOTIFY_CAN_DELAY").equals("1");
        needDoneAction = extrAttr(strinfos, "NEED_DONE_ACTION").equals("1");
        needDoneActionText = extrAttr(strinfos, "NEED_DONE_ACTION_TEXT");
        try {
            pri = Integer.parseInt(extrAttr(strinfos, "PRI"));
        } catch (NumberFormatException e) {
        }

        try {
            frags = Integer.parseInt(extrAttr(strinfos, "FRAGS"));
        } catch (NumberFormatException e) {
        }
    }

    private String extrAttr(String doc, String attrName) {
        int x = doc.indexOf(attrName);
        x = doc.indexOf("\"", x);
        int y = doc.indexOf("\"", x + 1);
        Log.i("extrattr", attrName + ":" + doc.substring(x + 1, y));
        return doc.substring(x + 1, y);
    }

    public String getId() {
        return id;
    }

    public int getPri() {
        return pri;
    }

    /**
     * @return action requested
     */
    public String getAct() {
        return act;
    }

    /**
     * @return digest value as string
     */
    public String getDigest() {
        return digest;
    }

    /**
     * @return protocol to use (HTTP/HTTPS)
     */
    public String getProto() {
        return proto;
    }

    /**
     * @return number of fragments
     */
    public int getFrags() {
        return frags;
    }

    public String getNeedDoneActionText() {
        return needDoneActionText;
    }

    public String getDigestAlgo() {
        return digestAlgo;
    }

    public String getDigestEncode() {
        return digestEncode;
    }

    public String getPath() {
        return path;
    }

    public String getNotifyText() {
        return notifyText;
    }

    public String getNotifyCountdown() {
        return notifyCountdown;
    }

    public boolean isNotifyUser() {
        return notifyUser;
    }

    public boolean isNotifyCanAbort() {
        return notifyCanAbort;
    }

    public boolean isNotifyCanDelay() {
        return notifyCanDelay;
    }

    public boolean isNeedDoneAction() {
        return needDoneAction;
    }
}

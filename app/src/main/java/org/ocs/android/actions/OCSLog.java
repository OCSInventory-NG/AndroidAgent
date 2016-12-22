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
package org.ocs.android.actions;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class OCSLog {
    private static String TAG = "OCSLOG";
    private static OCSLog instance;
    private File logFile;

    public OCSLog() {
        File rep = Environment.getExternalStoragePublicDirectory("ocs");

        Log.d(TAG, Environment.getExternalStorageDirectory().getPath());

        if (!rep.isDirectory()) {
            rep.delete();
        }
        if (!rep.exists()) {
            if (!rep.mkdir()) {
                Log.e(TAG, "Cannot create directory : " + rep.getPath());
                return;
            } else {
                Log.d(TAG, rep.getPath() + " created");
            }
        }
        logFile = new File(rep, "ocslog.txt");
        if (logFile.length() > 100000L) {
            logFile.delete();
        }
    }

    public static OCSLog getInstance() {
        if (instance == null) {
            instance = new OCSLog();
        }
        return instance;
    }

    public void debug(String paramString) {
        if (paramString != null && OCSSettings.getInstance() != null && OCSSettings.getInstance().getDebug() && logFile != null) {
            Log.d("OCSLOG", paramString);
            log(paramString);
        }
    }

    public void error(String paramString) {
        if (paramString != null && OCSSettings.getInstance() != null) {
            Log.e("OCSLOG", paramString);
            log(paramString);
        }
    }

    private void log(String paramString) {
        if (logFile == null) {
            return;
        }
        Date localDate = new Date();
        String strDate = DateFormat.getInstance().format(localDate);
        try {
            FileWriter fileWriter = new FileWriter(logFile, true);
            fileWriter.append(strDate).append(":").append(paramString).append("\n");
            fileWriter.close();
        } catch (IOException e) {
        }
    }
}
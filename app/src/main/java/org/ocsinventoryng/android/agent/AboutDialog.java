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
package org.ocsinventoryng.android.agent;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

import org.ocsinventoryng.android.actions.OCSSettings;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AboutDialog extends Dialog {
    private static Context mContext;

    public AboutDialog(Context context) {
        super(context);
        mContext = context;
    }

    /**
     * Standard Android on create method that gets called when the activity initialized.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.about);

        OCSSettings ocssettings = OCSSettings.getInstance(mContext);

        long lastUpdt = ocssettings.getLastUpdt();

        StringBuilder sb = new StringBuilder("OCS Inventory NG android Agent \n");
        sb.append("Version :");
        try {
            sb.append(mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionName);
        } catch (NameNotFoundException e) {
        }
        sb.append("\n");
        if (lastUpdt > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);
            sb.append("Last upload : ");
            sb.append(sdf.format(new Date(lastUpdt)));
            sb.append("\n");
            if (ocssettings.isAutoMode()) {
                int freq = ocssettings.getFreqMaj();
                long nextUpdt = lastUpdt + freq * 3600000L;
                sb.append("Next upload : ");
                sb.append(sdf.format(new Date(nextUpdt)));
            } else {
                sb.append("Mode manuel");
            }
        }

        TextView tv = (TextView) findViewById(R.id.test_about);
        tv.setText(sb.toString());
    }
}
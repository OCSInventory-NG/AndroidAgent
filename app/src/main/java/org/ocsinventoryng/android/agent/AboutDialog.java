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
    private static Context mContext = null;

    public AboutDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    /**
     * Standard Android on create method that gets called when the activity initialized.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.about);

        OCSSettings ocssettings = OCSSettings.getInstance(mContext);

        long lastUpdt = ocssettings.getLastUpdt();

        StringBuffer sb = new StringBuffer("OCS Inventory NG android Agent \n");
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
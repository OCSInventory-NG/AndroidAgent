package org.ocsinventoryng.android.actions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import android.os.Environment;

public class OCSLog
{
	private static String TAG = "OCSLOG";
	private static OCSLog instance = null;
	private File logFile = null;
	
	public OCSLog() {
		File rep=Environment.getExternalStoragePublicDirectory("ocs");

		android.util.Log.d(TAG, Environment.getExternalStorageDirectory().getPath());

		if ( ! rep.isDirectory() ) {
			rep.delete();
		}
		if ( ! rep.exists() ) {
			if ( ! rep.mkdir() ) {
		    	android.util.Log.e(TAG, "Cannot create directory : "+rep.getPath());
		    	return;
			}
			else
				android.util.Log.d(TAG,  rep.getPath()+ " created");
		}
		logFile = new File(rep, "ocslog.txt");
		if ( logFile.length() > 100000L )
			logFile.delete();
	}
	
	public static OCSLog getInstance()
	{
		if (instance == null)
				instance = new OCSLog();
		return instance;
	}

	public void debug(String paramString)
	{
		if ( paramString == null )
			return;
		if ( OCSSettings.getInstance() == null )
			return;
		if ( ! OCSSettings.getInstance().getDebug())
			return;
		android.util.Log.d("OCSLOG", paramString);
		if ( logFile == null ) {
			return;
		}
		log(paramString);
	}
	
	public void error(String paramString)
	{
		if ( paramString == null )
			return;
		if ( OCSSettings.getInstance() == null )
			return;
		android.util.Log.e("OCSLOG", paramString);
		log(paramString);
	}
	private void log(String paramString)
	{
		if ( logFile == null ) {
			return;
		}
		Date localDate = new Date();
		String strDate = DateFormat.getInstance().format(localDate);
		try
		{
			FileWriter fileWriter = new FileWriter(logFile, true);
			fileWriter.append(strDate+":"+paramString).append("\n");
			fileWriter.close();
		}
		catch (IOException e){ }
	}
	
}
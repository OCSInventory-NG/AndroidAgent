package org.ocsinventory.android.agent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.ocsinventory.android.actions.OCSLog;
import org.ocsinventory.android.actions.OCSProtocol;
import org.ocsinventory.android.actions.OCSProtocolException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class OCSLaunchActivity extends Activity {

	private File[] mFiles;
	private String[] mPackageNames;
	private String[] mPackageVersions;
	private int[] mVersionCode;
	private boolean[] mInstalled;
	private OCSLog mOcslog;
	private int mLaunched =0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ocs_launch);
		mOcslog = OCSLog.getInstance();
		
		File dirSofts = getExternalCacheDir();
		mFiles = dirSofts.listFiles();
		mPackageNames = new String[mFiles.length];
		mPackageVersions = new String[mFiles.length];
		mInstalled = new boolean[mFiles.length];
		mVersionCode = new int[mFiles.length];
		mLaunched=0;
		
		StringBuffer sb = new StringBuffer("Soft to be installed :\n");
		for (int i = 0; i < mFiles.length; ++i) {
	    	String filename=mFiles[i].getName();
	    	String id=filename.substring(0,filename.indexOf(".apk"));

	        PackageManager pm = getPackageManager();
	        PackageInfo pkgInfo = pm.getPackageArchiveInfo(mFiles[i].getPath(), 
	                               PackageManager.GET_ACTIVITIES);
	    	mPackageNames[i]=pkgInfo.applicationInfo.packageName;
	    	mPackageVersions[i]=pkgInfo.versionName;
	    	mVersionCode[i]=pkgInfo.versionCode;
	    	mOcslog.debug("package : "+mPackageNames[i]+"/"+pkgInfo.versionCode);
	    	mOcslog.debug("package : "+mPackageNames[i]+"/"+mPackageVersions[i]);
	    	sb.append(filename).append(" : ").append(pkgInfo.applicationInfo.packageName);
	    	
	    	OCSDownloadInfos infos = null;
			try {
				infos = getInfos(id);
				if  (infos != null )
				sb.append(infos.getNotify_text());
			} catch (IOException e) {
				mOcslog.error(filename+" : "+e.getMessage());
			}
			
			sb.append("\n");
			
			File finst=mFiles[i];
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(finst), "application/vnd.android.package-archive");
			if ( mPackageNames[i].equals("org.ocsinventory.android.agent" ) ) {
				mOcslog.debug("org.ocsinventory.android.agent detected");
				// Save id;version code for asynchrone check later
				String idctx=id+";"+pkgInfo.versionCode;
				try {
					FileOutputStream fos = getApplicationContext().openFileOutput("update.flag", 0);
					fos.write(idctx.getBytes());
					fos.close();
				} catch (Exception e) {}
				
				startActivity(intent);
				mOcslog.debug("stop activity");
				this.finish();
			} else 	
				startActivityForResult(intent, i+1);
		}
		TextView vMsg = (TextView) findViewById(R.id.textInstall);
		vMsg.setText(sb.toString());
		
		mOcslog.debug(sb.toString());
	}
	
	/**
	 * Catch the result of each installation task
	 */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	int no=requestCode-1;
    	String msg=String.valueOf(no+"/"+String.valueOf(resultCode));
    	Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
    	toast.show();
    	String filename=mFiles[no].getName();
    	String id=filename.substring(0,filename.indexOf(".apk"));
   	
    	OCSProtocol ocsproto = new OCSProtocol(getApplicationContext());
		try {
			// if ( resultCode == RESULT_OK )
			mInstalled[no]=isPkgInstalled( mPackageNames[no], mVersionCode[no] );
		   	if ( mInstalled[no])
		   	 	ocsproto.sendRequestMessage("DOWNLOAD", id, "SUCCESS");
			else
		    	ocsproto.sendRequestMessage("DOWNLOAD", id, "ERR_ABORTED");
		} catch (OCSProtocolException e) {}

		mLaunched++;
		if ( mLaunched == mFiles.length ) {
			StringBuffer sb = new StringBuffer("Installation report :\n");
			for( int i=0; i <mFiles.length; i++ ) {
					sb.append(mPackageNames[i]).append(" ").append(mPackageVersions[i]).append(" : ");
					if  ( mInstalled[i]) {
						sb.append("OK\n");
						mFiles[i].delete();
					} else {
						sb.append("KO\n");
					}
			}
			TextView vMsg = (TextView) findViewById(R.id.textInstall);
			vMsg.setText(sb.toString());	
		}
   	
     }
    /**
     * Check if a package is installed whith a given version code
     * 
     * @param pkg	Package name
     * @param version Version code
     * @return true if installed
     */
    private boolean isPkgInstalled( String pkg, int version) {
		PackageManager pm = getApplicationContext().getPackageManager () ;

		mOcslog.debug("Check installation "+pkg+"/"+version);
		try {
			PackageInfo lpInfo = pm.getPackageInfo (pkg, PackageManager.GET_ACTIVITIES);
			return ( lpInfo.versionCode == version );
		} catch (NameNotFoundException e) {
			mOcslog.error("Package notfound");
			return false;
		}
    }
    /**
     * 
     * @param id ocd id package
     * @return OCSDownloadInfos object builded from info file
     * @throws IOException
     */
	private OCSDownloadInfos getInfos(String id) throws IOException {
		StringBuilder sb = new StringBuilder();
		File finfos = new File (getApplicationContext().getFilesDir(), id+".info");
		FileInputStream fis = new FileInputStream(finfos);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br =new BufferedReader(isr);
		String ligne;
		while ((ligne = br.readLine()) != null ) 
			sb.append(ligne);
		br.close();
		isr.close();
		return new OCSDownloadInfos(sb.toString());
	}

}

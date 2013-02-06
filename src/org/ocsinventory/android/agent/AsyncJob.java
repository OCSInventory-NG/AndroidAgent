package org.ocsinventory.android.agent;

import org.ocsinventory.android.actions.Inventory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;

@SuppressLint("NewApi")
public class AsyncJob extends AsyncTask<Void , Void, String> {
	//private final View root;
	private final Activity activity;
	
	public AsyncJob ( Activity activity) {
		//this.root=root;
		this.activity=activity;
	}
	@Override protected void onPreExecute() {
		// root.setBackgroundResource(R.drawable.ocs_splash_vertical) ;
	}
	@Override protected void onPostExecute(String msg) {
		// if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN )
			//root.setBackgroundDrawable(null);
		//else
		//	root.setBackground(null);
			//android.util.Log.i("BG", msg);
		activity.setContentView(R.layout.ocs_agent);
	}
	@Override	protected String doInBackground(Void... params) {
	
		//logText.setText("Init inventaire\n");
		Inventory inventory = Inventory.getInstance(activity);
			
		return "OK";
	}

}

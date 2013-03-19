package org.ocsinventory.android.agent;

import java.io.File;

import org.ocsinventory.android.actions.Inventory;
import org.ocsinventory.android.actions.OCSFiles;
import org.ocsinventory.android.actions.OCSLog;
import org.ocsinventory.android.actions.OCSProtocol;
import org.ocsinventory.android.actions.OCSProtocolException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.provider.Contacts.ContactMethods;
import android.widget.TextView;

@SuppressLint("NewApi")
public class AsyncOperations extends AsyncTask<Void , Integer, String> {
	//private final View root;
	private ProgressDialog progressDialog;
	private TextView status;
	private String retour;
	private Context ctx;
	private final Activity activity;
	private boolean send;
	
	public AsyncOperations ( boolean send , ProgressDialog progressDialog, TextView status, Activity act, Context ctx) {
		this.send=send;
		this.progressDialog=progressDialog;
		this.status=status;
		this.ctx=ctx;
		this.activity = act;
	}
	
	@Override protected void onPreExecute() {
		status.setText(R.string.state_build_inventory);
	}
	
	@Override protected void onPostExecute(String msg) {
		OCSLog.getInstance().append("onPostExecute ["+msg+"]");
		progressDialog.dismiss();
		status.setText(msg);
	}
	
	@Override	protected String doInBackground(Void... params) {
	
		Inventory inventory  = Inventory.getInstance(activity);
	
		OCSProtocol ocsproto = new OCSProtocol();
		
		if ( ! send ) { 
			String status = OCSFiles.getInstance().copyToExternal(inventory);
			if ( status.equals("OK"))
				status=ctx.getString(R.string.state_saved);
			return status; 
		}
		publishProgress(R.string.state_send_prolog);
	
		String rep;
		try {
			rep = ocsproto.sendPrologueMessage(inventory);
		} catch (OCSProtocolException e1) {
			return(e1.getMessage());
		}
		OCSLog.getInstance().append("Retour send ["+rep+"]");
		if ( rep.equals("ERROR")) {
			return(rep);
		}
		else {
			publishProgress(R.string.state_send_inventory);
			try {
				rep=ocsproto.sendInventoryMessage(inventory);
			} catch (OCSProtocolException e) {
				return(e.getMessage());
			}
			OCSLog.getInstance().append("Retour send ["+rep+"]");
			if ( rep == null )
				return(ctx.getString(R.string.state_send_error));
			else if ( rep.length() == 0 )
				return(ctx.getString(R.string.state_sent_inventory));
			else
				return(rep);
		}
	}
	protected void onProgressUpdate(Integer... progress) {
		progressDialog.setMessage(ctx.getString(progress[0]));
    }

}

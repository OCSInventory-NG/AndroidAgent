package org.ocsinventoryng.android.agent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import org.ocsinventoryng.android.actions.Inventory;
import org.ocsinventoryng.android.actions.OCSFiles;
import org.ocsinventoryng.android.actions.OCSLog;
import org.ocsinventoryng.android.actions.OCSProtocol;
import org.ocsinventoryng.android.actions.OCSProtocolException;

@SuppressLint("NewApi")
public class AsyncOperations extends AsyncTask<Void, Integer, String> {
    //private final View root;
    private final Activity mActivity;
    private ProgressDialog mProgressDialog;
    private TextView mTvStatus;
    private Context mAppCtx;
    private boolean mSend;
    private boolean mDownload;

    public AsyncOperations(boolean send, ProgressDialog progressDialog, TextView status, Activity act, Context ctx) {
        this.mSend = send;
        this.mProgressDialog = progressDialog;
        this.mTvStatus = status;
        this.mAppCtx = ctx;
        this.mActivity = act;
        this.mDownload = false;
    }

    @Override
    protected void onPreExecute() {
        mTvStatus.setText(R.string.state_build_inventory);
    }

    @Override
    protected void onPostExecute(String msg) {
        OCSLog.getInstance().debug("onPostExecute [" + msg + "]");
        mProgressDialog.dismiss();
        if (mDownload) {
            // Toast cant be build in the thread doInBackground
            Toast toast = Toast.makeText(mAppCtx, mAppCtx.getString(R.string.start_download_service), Toast.LENGTH_LONG);
            toast.show();
        }

        mTvStatus.setText(msg);
    }

    @Override
    protected String doInBackground(Void... params) {

        Inventory inventory = Inventory.getInstance(mActivity);

        OCSProtocol ocsproto = new OCSProtocol(mAppCtx);

        if (!mSend) {
            String status = new OCSFiles(mAppCtx).copyToExternal(inventory);
            if (status.equals("OK")) {
                status = mAppCtx.getString(R.string.state_saved);
            }
            return status;
        }
        publishProgress(R.string.state_send_prolog);

        OCSPrologReply reply;
        String rep;
        try {
            reply = ocsproto.sendPrologueMessage(inventory);
        } catch (OCSProtocolException e1) {
            return (e1.getMessage());
        }
        OCSLog.getInstance().debug("Retour send prolog [" + reply.getResponse() + "]");
        OCSLog.getInstance().debug(reply.log());
        if (reply.getResponse().equals("ERROR")) {
            return (reply.getResponse());
        } else {
            publishProgress(R.string.state_send_inventory);
            try {
                rep = ocsproto.sendInventoryMessage(inventory);
            } catch (OCSProtocolException e) {
                return (e.getMessage());
            }


            OCSLog.getInstance().debug("Retour send inventory [" + rep + "]");

            if (!reply.getIdList().isEmpty()) {
                OCSLog.getInstance().debug(mAppCtx.getString(R.string.start_download_service));

                // Some downlowds requiered invoke download service
                Intent dldService = new Intent(mAppCtx, OCSDownloadService.class);
                mAppCtx.startService(dldService);
                mDownload = true; // Info for postexecute stage
            }

            if (rep == null) {
                return (mAppCtx.getString(R.string.state_send_error));
            } else if (rep.length() == 0) {
                return (mAppCtx.getString(R.string.state_sent_inventory));
            } else {
                return (rep);
            }
        }
    }

    protected void onProgressUpdate(Integer... progress) {
        mProgressDialog.setMessage(mAppCtx.getString(progress[0]));
    }
}

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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import org.ocsinventoryng.android.actions.OCSFiles;
import org.ocsinventoryng.android.actions.OCSLog;
import org.ocsinventoryng.android.actions.OCSProtocol;
import org.ocsinventoryng.android.actions.OCSProtocolException;
import org.ocsinventoryng.android.actions.OCSSettings;
import org.ocsinventoryng.android.actions.Utils;

import java.io.File;
import java.io.IOException;

public class OCSDownloadService extends Service {

    private final String ACTION_STORE = "STORE";
    //private final String ACTION_EXECUTE = "EXECUTE";
    private final String ACTION_LAUNCH = "LAUNCH";

    private final String ERR_DOWNLOAD_INFO = "ERR_DOWNLOAD_INFO";
    private final String ERR_DOWNLOAD_PACK = "ERR_DOWNLOAD_PACK";
    private final String ERR_OUT_OF_SPACE = "ERR_OUT_OF_SPACE";
    private final String ERR_UNZIP = "ERR_UNZIP";
    // private final String ERR_CLEAN 			= "ERR_CLEAN";
    // private final String ERR_TIMEOUT 		= "ERR_TIMEOUT";
    private final String ERR_BAD_DIGEST = "ERR_BAD_DIGEST";
    private final String DOWNLOAD_QUERY = "DOWNLOAD";
    private final String SUCCESS = "SUCCESS";

    private final long MILLE = 1000L;    // To change time scale on tests
    private boolean mLaunch = false;            // One package(s) downloaded for install

    private OCSSettings mOcssetting;
    private OCSLog mOcslog;
    private OCSProtocol mOcsproto;
    private OCSPrologReply mReply;

    private final IBinder mBinder = new LocalBinder();

    /*
     * Binder juste pour verifier que le service tourne
     */
    public class LocalBinder extends Binder {
        public OCSDownloadService getService() {
            return OCSDownloadService.this;
        }
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        mOcssetting = OCSSettings.getInstance(getApplicationContext());
        mOcslog = OCSLog.getInstance();
        // Read the prolog reply file describing the jobs
        OCSFiles mOcsfiles = new OCSFiles(getApplicationContext());
        mReply = mOcsfiles.loadPrologReply();

        mOcslog.debug("mFragLatency     : " + mReply.getFragLatency());
        mOcslog.debug("mPeriodLatency   : " + mReply.getPeriodLatency());
        mOcslog.debug("mCycleLatency    : " + mReply.getCycleLatency());
        mOcslog.debug("mTimeout          : " + mReply.getTimeout());
        AsyncCall task = new AsyncCall(getApplicationContext());
        task.execute();

        return Service.START_NOT_STICKY;
    }

    private void doDownloads() {
        mOcsproto = new OCSProtocol(getApplicationContext());

        // Load index files
        for (OCSDownloadIdParams dip : mReply.getIdList()) {
            StringBuilder sbUrl = new StringBuilder("https://");
            sbUrl.append(dip.getInfoLoc()).append("/").append(dip.getId()).append("/info");
            File fileInfo = new File(getApplicationContext().getFilesDir(), dip.getId() + ".info");
            try {
                mOcslog.debug("Get index : " + sbUrl);
                String strindex = mOcsproto.strwget(sbUrl.toString());
                // Save it to be read by launchActivity
                Utils.strToFile(strindex, fileInfo);
                OCSDownloadInfos di = new OCSDownloadInfos(strindex);
                mOcslog.debug(strindex);
                dip.setInfos(di);
                mOcslog.debug(di.getId() + " : " + dip.getInfos().getPri());
            } catch (Exception e) {
                mOcslog.error("Erreur : " + e.getMessage());
                notifyServer(dip.getId(), ERR_DOWNLOAD_INFO);
            }
        }
        // Begin cycles
        mOcslog.debug("Begin cycles");
        int iPeriod = 0;
        int todo = mReply.getIdList().size();
        while (todo > 0) {
            for (int iCycle = 1; iCycle <= mReply.getPeriodeLength(); iCycle++) {
                mOcslog.debug("Period " + iPeriod + " cycle " + iCycle + " todo " + todo);
                for (OCSDownloadIdParams dip : mReply.getIdList()) {
                    if (dip.getInfos() == null) {
                        todo--;
                        continue;
                    }
                    int pri = dip.getInfos().getPri();
                    int m = (pri == 0 ? 0 : iCycle % pri);
                    if (m == 0) {
                        int nofrag = dip.getDownloaded() + 1;
                        if (nofrag > dip.getInfos().getFrags()) {
                            // Download complete
                            // Build the file
                            String fileOutName = dip.getId() + "-1";
                            File fileOut = getFileStreamPath(fileOutName);
                            // Concate if more the 1 fragment
                            for (int n = 2; n <= dip.getInfos().getFrags(); n++) {
                                String fileAddName = dip.getId() + "-" + n;
                                File fileAdd = getFileStreamPath(fileAddName);
                                mOcslog.debug("concate : " + fileAddName + "," + fileOutName);
                                try {
                                    Utils.concateFiles(fileAdd, fileOut);
                                    fileAdd.delete();
                                } catch (IOException e) {
                                    mOcslog.error("Erreur : " + e.getMessage());
                                    notifyServer(dip.getId(), ERR_OUT_OF_SPACE);
                                }
                            }
                            // Now frag_1 contains all data
                            // Verify integrity
                            String dgst = Utils.digestFile(fileOut, dip.getInfos().getDigestAlgo(),
                                                           dip.getInfos().getDigestEncode());
                            if (!dgst.equalsIgnoreCase(dip.getInfos().getDigest())) {
                                mOcslog.debug("Calculated digest  : " + dgst);
                                mOcslog.debug("Package    digest  : " + dip.getInfos().getDigest());
                                mOcslog.debug("Integrity check fail");
                                notifyServer(dip.getId(), ERR_BAD_DIGEST);
                            }

                            if (dip.getInfos().getAct().equals((ACTION_STORE))) {
                                // Unzip it

                                if (Utils.unZip(fileOut.getPath(), dip.getInfos().getPath())) {
                                    notifyServer(dip.getId(), SUCCESS);
                                } else {
                                    mOcslog.error("Erreur when unzip package");
                                    notifyServer(dip.getId(), ERR_UNZIP);
                                }
                            } else if (dip.getInfos().getAct().equals((ACTION_LAUNCH))) {
                                // getFilesDir()+fileOutName+".apk"
                                File finst = new File(getExternalCacheDir(), dip.getId() + ".apk");
                                try {
                                    Utils.copyFile(fileOut, finst);
                                    mOcslog.debug("Ready to install : " + finst);
                                    mLaunch = true;
                                } catch (IOException e) {
                                    mOcslog.error("Erreur : " + e.getMessage());
                                    notifyServer(dip.getId(), ERR_OUT_OF_SPACE);
                                }
                                fileOut.delete();
                            }
                            dip.setInfos(null); // Dont take it again
                            todo--;
                        } else {
                            // Get next fragment
                            StringBuilder sbUrl = new StringBuilder("http://");
                            String fileName = dip.getId() + "-" + nofrag;
                            sbUrl.append(dip.getPackLoc()).append("/").append(dip.getId()).append("/").append(fileName);
                            try {
                                mOcslog.debug("Get fragment : " + sbUrl);
                                mOcsproto.downloadFile(sbUrl.toString(), fileName);
                                dip.setDownloaded(nofrag);
                            } catch (OCSProtocolException e) {
                                notifyServer(dip.getId(), ERR_DOWNLOAD_PACK);
                                dip.setInfos(null);
                                todo--;
                            }
                        }
                    }
                    try {
                        Thread.sleep(MILLE * mReply.getFragLatency());
                    } catch (InterruptedException e) {
                    }
                }
                try {
                    Thread.sleep(MILLE * mReply.getCycleLatency());
                } catch (InterruptedException e) {
                }
            }
            try {
                Thread.sleep(MILLE * mReply.getPeriodLatency());
            } catch (InterruptedException e) {
            }
            iPeriod++;
        }
        mOcslog.debug("End cycles");
    }

    private void notifyServer(String id, String code) {
        try {
            String rep = mOcsproto.sendRequestMessage(DOWNLOAD_QUERY, id, code);
            mOcslog.debug("Reponse : " + rep);
        } catch (OCSProtocolException e) {
            mOcslog.error("Erreur : " + e.getMessage());
        }
    }

    private class AsyncCall extends AsyncTask<Void, Void, Void> {
        private Context mContext;

        AsyncCall(Context ctx) {
            mContext = ctx;
        }

        @Override
        protected Void doInBackground(Void... params) {
            doDownloads();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mLaunch) {
                notify(R.string.nty_downloads);
            }
        }

        private void notify(int id) {
            if (mOcssetting.getHiddenNotif() == OCSAgentService.HIDE_NOTIF_DOWNLOAD
                || mOcssetting.getHiddenNotif() == OCSAgentService.HIDE_NOTIF_ALL) {
                return;
            }

            NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext).setSmallIcon(
                    R.drawable.ic_notification).setContentTitle(getText(R.string.nty_title)).setContentText(
                    getText(id)).setAutoCancel(true);

            Intent rIntent = new Intent(mContext, OCSLaunchActivity.class);

            PendingIntent rpIntent = PendingIntent.getActivity(mContext, 0, rIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(rpIntent);

            mNM.notify(id, mBuilder.build());
        }
    }

    public void onDestroy() {
        NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNM.cancelAll();
    }

	/*
SUCCESS L'agent a téléchargé avec succès le paquet et la commande associée d'exécution ou d'enregistrement des données s'est
terminée sans erreur (code retour 0).
ERR_EXIT_CODE_xxx 	L'agent a téléchargé avec succès le paquet, MAIS la commande associée d'exécution ou d'enregistrement des
données s'est terminée en erreur (code retour xxx).
ERR_ALREADY_SETUP 	Le paquet a déjà été installé avec succès sur l'ordinateur, et l'agent ne l'a pas ré-installé.
ERR_BAD_ID 	L'agent est incapable de télécharger le paquet parce qu'il ne peut trouver l'ID(entifiant) du paquet sur le serveur
de déploiement.
ERR_BAD_DIGEST 	La signature du paquet téléchargé est incorrecte, l'agent n'a pas exécuté la commande associée.
ERR_DOWNLOAD_INFO 	L'agent n'a pas pu télécharger le fichier INFO de méta-données du paquet.
ERR_DOWNLOAD_PACK 	L'agent n'a pas pu télécharger un des fragments du paquet.
ERR_BUILD 	L'agent n'a pas pu reconstruire le ZIP ou le TAR.GZ à partir des fragments de paquet.
ERR_UNZIP 	L'agent n'a pas pu décompresser le ZIP ou le TAR.GZ du paquet.
ERR_OUT_OK_SPACE 	Il n'y a pas assez d'espace disque disponible pour décompresser et exécuter le ZIP ou le TAR.GZ du paquet
(il faut au moins 3 fois la taille du ZIP ou du TAR.GZ).
ERR_BAD_PARAM 	Un paramètre du fichier INFO de méta-données du paquet est incorrect.
ERR_EXECUTE_PACK 	Aucune commande d'exERR_DOWNLOAD_INFOécution n'est indiquée dans le fichier INFO de méta-données du paquet.
ERR_EXECUTE 	L'agent n'a pas pu exécuter la commande indiquée dans le fichier INFO de méta-données du paquet.
ERR_CLEAN 	L'agent n'a pas pu nettoyer le paquet téléchargé (supprimer les fichiers temporaires), mais la commande
d'installation a été exécutée avec succès.
ERR_DONE_FAILED 	L'agent n'a pas pu récupérer le résultat d'exécution mis en cache du paquet (le cache sert à stocker le
résultat au cas où le serveur ne répondrait pas ou ne serait pas disponible au moment de fin d'exécution du paquet).
ERR_TIMEOUT 	L'agent n'a pas pu télécharger le paquet durant le nombre de jours permis.
ERR_ABORTED

LINUX AGENT
        code_success                    => 'SUCCESS',
        success_already_setup               => 'SUCCESS_ALREADY_SETUP',
        err_bad_id                  => 'ERR_BAD_ID',
        err_download_info               => 'ERR_DOWNLOAD_INFO',
        err_bad_digest                  => 'ERR_BAD_DIGEST',
        err_download_pack               => 'ERR_DOWNLOAD_PACK',
        err_build                   => 'ERR_BUILD',
        err_execute                 => 'ERR_EXECUTE',
        err_clean                   => 'ERR_CLEAN',
        err_timeout                 => 'ERR_TIMEOUT',

 <REQUEST>
        <DEVICEID>$context->{deviceid},
        <QUERY> => 'DOWNLOAD',
        <ID> => $id,
        <ERR>' => $code
	 */
}

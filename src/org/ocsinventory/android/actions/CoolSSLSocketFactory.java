package org.ocsinventory.android.actions;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;
	
public class CoolSSLSocketFactory extends SSLSocketFactory {
    SSLContext sslContext = SSLContext.getInstance("TLS");

    public CoolSSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
 
     	super(truststore);

        TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            	int n=chain.length;
            	for ( int i = 0; i< n;i++ ) {
            		android.util.Log.d("X509", chain[i].getSubjectDN().toString());
            	}
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        android.util.Log.d("X509", "CoolSSLSocketFactory");
        sslContext.init(null, new TrustManager[] { tm }, null);
        this.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }
}

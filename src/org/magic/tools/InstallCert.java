package org.magic.tools;
/**
 * Originally from:
 * http://blogs.sun.com/andreas/resource/InstallCert.java
 * Use:
 * java InstallCert hostname
 * Example:
 *% java InstallCert ecc.fedora.redhat.com
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

/**
 * Class used to add the server's certificate to the KeyStore
 * with your trusted certificates.
 */
public class InstallCert {

	static final Logger logger = MTGLogger.getLogger(InstallCert.class);

	
    public static void install(String website) throws Exception {
        String host;
        int port;
    	
        
        File  defaultF = new File(System.getProperty("java.home") + File.separatorChar+ "lib" + File.separatorChar + "security");
        char[] passphrase;
            String[] c = website.split(":");
            host = c[0];
            port = 443;
            passphrase = MTGConstants.KEYSTORE_PASS.toCharArray();
       
            File keystoreFile = new File(MTGControler.CONF_DIR,MTGConstants.KEYSTORE_NAME);
            if (keystoreFile.exists() == false) {
            	keystoreFile.createNewFile();
            	FileUtils.copyFile(new File(defaultF, "cacerts"),keystoreFile);
            }
        
        logger.debug("Loading KeyStore " + keystoreFile.getAbsolutePath() + "...");
        InputStream in = new FileInputStream(keystoreFile);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(in, passphrase);
        in.close();

        SSLContext context = SSLContext.getInstance("TLS");
        TrustManagerFactory tmf =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
        SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
        context.init(null, new TrustManager[]{tm}, null);
        SSLSocketFactory factory = context.getSocketFactory();

        logger.debug("Opening connection to " + host + ":" + port + "...");
        SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
        socket.setSoTimeout(10000);
        try {
        	logger.debug("Starting SSL handshake...");
            socket.startHandshake();
            socket.close();
            logger.debug("No errors, certificate is already trusted");
            return;
        } catch (SSLException e) {
        	logger.error(e);
        }

        X509Certificate[] chain = tm.chain;
        if (chain == null) {
           logger.error("Could not obtain server certificate chain");
            return;
        }

        logger.debug("Server sent " + chain.length + " certificate(s):");
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        MessageDigest md5 = MessageDigest.getInstance("MD5");
       
        int i=0;
      //  for (X509Certificate cert :chain) 
        X509Certificate cert =chain[0];
        {
         sha1.update(cert.getEncoded());
         md5.update(cert.getEncoded());
       
        String alias = host + "-" + (i++);
        ks.setCertificateEntry(alias, cert);

        
        OutputStream out = new FileOutputStream(new File(MTGControler.CONF_DIR,MTGConstants.KEYSTORE_NAME));
        ks.store(out, passphrase);
        out.close();

        logger.debug("Added certificate to keystore '"+new File(MTGControler.CONF_DIR,MTGConstants.KEYSTORE_NAME)+"' using alias '"+ alias + "'");
        
        }
    }

    private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

     private static class SavingTrustManager implements X509TrustManager {

        private final X509TrustManager tm;
        private X509Certificate[] chain;

        SavingTrustManager(X509TrustManager tm) {
            this.tm = tm;
        }

        public X509Certificate[] getAcceptedIssuers() {
	   
	    /** 
	     * This change has been done due to the following resolution advised for Java 1.7+
		http://infposs.blogspot.kr/2013/06/installcert-and-java-7.html
       	     **/ 
	    
	    return new X509Certificate[0];	
            //throw new UnsupportedOperationException();
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            throw new UnsupportedOperationException();
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            this.chain = chain;
            tm.checkServerTrusted(chain, authType);
        }
    }
}

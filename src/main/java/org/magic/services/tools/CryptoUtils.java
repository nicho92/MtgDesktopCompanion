package org.magic.services.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.jasypt.util.text.AES256TextEncryptor;
import org.mindrot.jbcrypt.BCrypt;

public class CryptoUtils {

	private CryptoUtils() {

	}

	public static String encrypt(String strToEncrypt, String secret) {
		var textEncryptor = new AES256TextEncryptor();
		textEncryptor.setPasswordCharArray(secret.toCharArray());
		return textEncryptor.encrypt(strToEncrypt);
	}

	public static String decrypt(String strToDecrypt, String secret) {
		var textEncryptor = new AES256TextEncryptor();
		textEncryptor.setPasswordCharArray(secret.toCharArray());
		return textEncryptor.decrypt(strToDecrypt);
	}

	public static String getMD5(byte[] input) {
		  byte[] bytes= DigestUtils.getMd5Digest().digest(input);
			 StringBuilder sb = new StringBuilder();
		      for (byte b : bytes) {
		          sb.append(String.format("%02x", b));
		      }

		      return sb.toString();
	}


	public static String generateMD5(String s)
	{
		return DigestUtils.md5Hex(s).toUpperCase();
	}
	
	
	public static boolean verifyPassword(String inputPassword, String storedHash) {
        return BCrypt.checkpw(inputPassword, storedHash);
    }
	
	public static String generatePasswordHash(String inputPassword) {
        return BCrypt.hashpw(inputPassword, BCrypt.gensalt());
    }

	


	public static String toBase64(byte[] str) {
	    return Base64.getEncoder().encodeToString(str);
	}

	public static byte[] fromBase64(String s) {
	    return Base64.getDecoder().decode(s);
	}

	public static int randomInt(int i) {
		try {
			return SecureRandom.getInstanceStrong().nextInt(i);
		} catch (NoSuchAlgorithmException _) {	
			return -1;
		}
	}

	public static Long randomLong() {
		try {
			return SecureRandom.getInstanceStrong().nextLong();
		} catch (NoSuchAlgorithmException _) {
			return -1L;
		}
	}
	
	public static List<X509Certificate> getCertificates(File keystoreFile,String password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException
	{
		var ret = new ArrayList<X509Certificate>();
	        var keystore = KeyStore.getInstance(KeyStore.getDefaultType());
	        keystore.load(new FileInputStream(keystoreFile), password.toCharArray());
	        var aliases = keystore.aliases();
	        while(aliases.hasMoreElements()){
	            String alias = aliases.nextElement();
	            if(keystore.getCertificate(alias).getType().equals("X.509")){
	                ret.add((X509Certificate) keystore.getCertificate(alias));
	            }
	        }
	        return ret;
	}
	

	public static Double randomDouble(double bound) {
		try {
			return SecureRandom.getInstanceStrong().nextDouble(bound);
		} catch (NoSuchAlgorithmException _) {
			return -1.0;
		}
	}

	public static boolean randomBoolean() {
		try {
			return SecureRandom.getInstanceStrong().nextBoolean();
		} catch (NoSuchAlgorithmException _) {
			return false;
		}
	}

	public static String randomString(Integer tokensize) {
		return RandomStringUtils.secure().next(tokensize, true, true);
	}

	public static String sha256Hex(String string) {
		return DigestUtils.sha256Hex(string);
	}

	public static String uuid() {
		return UUID.randomUUID().toString();
	}
	
}
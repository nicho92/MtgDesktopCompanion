package org.magic.tools;

import java.util.Base64;

import org.apache.commons.codec.digest.DigestUtils;
import org.jasypt.util.text.AES256TextEncryptor;

public class CryptoUtils {

	private CryptoUtils() {

	}

	public static String encrypt(String strToEncrypt, String secret) {
		AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
		textEncryptor.setPasswordCharArray(secret.toCharArray());
		return textEncryptor.encrypt(strToEncrypt);
	}

	public static String decrypt(String strToDecrypt, String secret) {

		AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
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



	public static String toBase64(byte[] img) {
	    return Base64.getEncoder().encodeToString(img);
	}

	public static byte[] fromBase64(String s) {
	    return Base64.getDecoder().decode(s);
	}



}
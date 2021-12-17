package org.magic.tools;

import org.apache.commons.codec.digest.Md5Crypt;
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
      return Md5Crypt.md5Crypt(input);
    }
	
	
}
package org.beta;

import org.jasypt.util.text.AES256TextEncryptor;


public class CryptoUtils {

	
	
	
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

}
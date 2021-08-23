package org.beta;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.magic.services.MTGLogger;

public class CryptoUtils {
	private static final String AES_ECB_PKCS5_PADDING = "AES/ECB/PKCS5Padding";
	private static Logger logger = MTGLogger.getLogger(CryptoUtils.class);
	private static SecretKeySpec secretKey;
	
	private static void setKey(String myKey) {
		MessageDigest sha = null;
		try {
			byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			logger.error(e);
		} 
	}

	public static String encrypt(String strToEncrypt, String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance(AES_ECB_PKCS5_PADDING);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception e) {
			logger.error("Error while encrypting: " + e.toString());
		}
		return null;
	}

	public static String decrypt(String strToDecrypt, String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance(AES_ECB_PKCS5_PADDING);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
			logger.error("Error while decrypting: " + e.toString());
		}
		return null;
	}

}
package org.magic.services.tools;

import java.util.Base64;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.Logger;
import org.jasypt.util.text.AES256TextEncryptor;
import org.magic.api.beans.MTGCard;
import org.magic.services.logging.MTGLogger;

public class CryptoUtils {

	static Logger logger = MTGLogger.getLogger(CryptoUtils.class);

	private CryptoUtils() {

	}
	
	public static String generateCardId(MTGCard mc) {
		
		try {
		
		var number=mc.getNumber();
		
		if(number!=null&&number.isEmpty() )
			number=null;

		var id = String.valueOf((mc.getName() + mc.getEdition() + number + mc.getMultiverseid()));
		id = DigestUtils.sha1Hex(id);

		logger.trace("Generate ID for {}|{}|{}|{}->:{}",mc.getName(),mc.getEdition(),number,mc.getMultiverseid(),id);

		return id;
		
		}catch(Exception e)
		{
			logger.error("Error generating ID for {}",mc,e);
			return "";
		}
		
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

	public static String generateSha256(String s)
	{
		return DigestUtils.sha256Hex(s).toUpperCase();
	}



	public static String toBase64(byte[] img) {
	    return Base64.getEncoder().encodeToString(img);
	}

	public static byte[] fromBase64(String s) {
	    return Base64.getDecoder().decode(s);
	}



}
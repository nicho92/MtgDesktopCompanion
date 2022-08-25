package org.magic.services;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.magic.api.beans.shop.Contact;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;


public class JWTServices {

	private Logger logger = MTGLogger.getLogger(this.getClass());
	private int expirationMinute;
	private Algorithm algo;
	private String issuer;

	public JWTServices(String secret, int expirationMinute,String issuer) {
		this.expirationMinute=expirationMinute;
		this.issuer = issuer;
		algo = Algorithm.HMAC512(secret);
	}
	
	public void setExpirationMinute(int expirationMinute) {
		this.expirationMinute = expirationMinute;
	}
		
	public void setSecret(String secret) {
		algo = Algorithm.HMAC512(secret);
	}
	
	public String generateToken(Contact c)
	{
		return JWT.create()
		   .withIssuer(issuer)
		   .withIssuedAt(new Date())
		   .withExpiresAt(DateUtils.addMinutes(new Date(),expirationMinute))
		   .withClaim("name", c.getName())
		   .withClaim("email", c.getEmail())
		   .sign(algo);
	}
	
	public boolean validateToken(String token)
	{
		try {
			
			JWT.require(algo)
				   .withIssuer(issuer)
				   .build()
				   .verify(token);
		
			return true;
		
		}
		catch(JWTVerificationException ex)
		{
			logger.warn(ex);
			return false;
		}
			
	}
	
}

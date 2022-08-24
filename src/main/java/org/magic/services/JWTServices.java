package org.magic.services;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.magic.api.beans.shop.Contact;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class JWTServices {

	private int expirationMinute;
	private Algorithm algo;

	public JWTServices(String secret, int expirationMinute) {
		this.expirationMinute=expirationMinute;
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
		   .withIssuer(MTGConstants.MTG_APP_NAME)
		   .withExpiresAt(DateUtils.addMinutes(new Date(),expirationMinute))
		   .withClaim("name", c.getName())
		   .withClaim("email", c.getEmail())
		   .sign(algo);
	}
	
	public void validateToken(String token)
	{
		
		JWT.decode(token);
	}
	
	
	
	
}

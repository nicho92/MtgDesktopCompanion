package org.magic.services;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JWTServices {

	private Logger logger = MTGLogger.getLogger(this.getClass());
	private int expirationMinute;
	private int refreshExpirationMinute;
	private String issuer;
	private Key  secret;
	private SignatureAlgorithm algo;

	public JWTServices(String secret, int expirationMinute,int refreshExpirationMinute,String issuer) {
		init(secret,expirationMinute,refreshExpirationMinute,issuer);
	}
	
	public JWTServices(String secret, String issuer) {
		init(secret,15,21600,issuer);
	}
	
	
	private void init(String secret, int expirationMinute,int refreshExpirationMinute,String issuer)
	{
		this.expirationMinute=expirationMinute;
		this.refreshExpirationMinute=refreshExpirationMinute;
		this.issuer = issuer;
		this.algo = SignatureAlgorithm.HS256;
		setSecret(secret);
	}
	
	
	public void setExpirationMinute(int expirationMinute) {
		this.expirationMinute = expirationMinute;
	}
	
	public void setRefreshExpirationMinute(int expirationMinute) {
		this.refreshExpirationMinute = expirationMinute;
	}
	
		
	public void setSecret(String secret) {
		this.secret=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}
	
	
	private String generateToken(Map<String,Object> claims, int timeout)
	{
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(null)
				.setIssuer(issuer)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(DateUtils.addMinutes(new Date(), timeout))
				.signWith(secret,algo)
				.compact();
	}
	
	public String generateRefreshToken(Map<String,Object> claims)
	{
		return generateToken(claims,refreshExpirationMinute);
	}
	
	public String generateToken(Map<String,Object> claims)
	{
		return generateToken(claims,expirationMinute);
	}
	
	public boolean validateToken(String token)
	{
		
		try {
			Jwts.parserBuilder()
				 .setSigningKey(secret)
				 .requireIssuer(issuer)
				 .build()
				 .parseClaimsJws(token);
			return true;
		}
		catch(Exception ex)
		{
			logger.warn(ex);
			return false;
		}
			
	}

	public static void main(String[] args) {
		var service = new JWTServices("MySecretKeyIsNotTooWeakForThisPowerfullApp", "MTGCompanion");
		var tok  = service.generateToken(Map.of("name","nico"));
		
		System.out.println(tok);
		
		service.validateToken(tok+"4");
		
		
		
		
	}
	
	
	
}

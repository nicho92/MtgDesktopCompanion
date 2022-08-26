package org.magic.services;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JWTServices {

	private Logger logger = MTGLogger.getLogger(this.getClass());
	private String issuer;
	private Key secret;
	private SignatureAlgorithm algo;

	private List<String> refreshedTokenRepository = new ArrayList<>();
	
	public JWTServices(String secret, String issuer) {
		this.issuer = issuer;
		this.algo = SignatureAlgorithm.HS256;
		setSecret(secret);
	}
		
	public void setSecret(String secret) {
		this.secret=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}
	
	public String generateToken(Map<String,Object> claims, int timeoutInMinutes,boolean store)
	{
		var tok= Jwts.builder()
				.setClaims(claims)
				.setSubject(null)
				.setIssuer(issuer)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(DateUtils.addMinutes(new Date(System.currentTimeMillis()), timeoutInMinutes))
				.signWith(secret,algo)
				.compact();
		
		
		if(store)
			refreshedTokenRepository.add(tok);
		
		
		return tok;
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
}

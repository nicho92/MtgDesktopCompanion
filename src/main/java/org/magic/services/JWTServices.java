package org.magic.services;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.apache.commons.lang3.time.DateUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JWTServices {

	private String issuer;
	private SecretKey key;
	private List<String> refreshedTokenRepository = new ArrayList<>();

	public JWTServices(String secret, String issuer) {
		this.issuer = issuer;
		setSecret(secret);
	}

	public String generateToken(String sub, Map<String,String> claims, int timeoutInMinutes,boolean store)
	{
		var tok=Jwts.builder()
				.claims(claims)
				.issuer(issuer)
				.subject(sub)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(DateUtils.addMinutes(new Date(System.currentTimeMillis()), timeoutInMinutes))
				.signWith(key)
				.compact();


		if(store)
			refreshedTokenRepository.add(tok);


		return tok;
	}

	public Jws<Claims> validateToken(String token) throws JwtException, IllegalArgumentException
	{
			return Jwts.parser()
				 .verifyWith(key)
				 .requireIssuer(issuer)
				 .build()
				 .parseSignedClaims(token);
	}

	public void setSecret(String secret) {
		this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public static String generateRandomSecret()
	{
		return Base64.getEncoder().encodeToString(Jwts.SIG.HS256.key().build().getEncoded());
	
	}

}

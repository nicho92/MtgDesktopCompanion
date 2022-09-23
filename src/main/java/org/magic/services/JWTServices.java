package org.magic.services;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.Logger;
import org.magic.services.logging.MTGLogger;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JWTServices {

	private Logger logger = MTGLogger.getLogger(this.getClass());
	private String issuer;
	private Key secret;
	private static SignatureAlgorithm algo = SignatureAlgorithm.HS256;
	private String aud;
	private List<String> refreshedTokenRepository = new ArrayList<>();

	public JWTServices(String secret, String issuer) {
		this.issuer = issuer;
		setSecret(secret);
	}


	public void setAudience(String aud)
	{
		this.aud=aud;
	}

	public void setSecret(String secret) {
		this.secret=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(Map<String,Object> claims, int timeoutInMinutes,boolean store)
	{
		var tok=Jwts.builder()
				.setAudience(aud)
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


	public Jws<Claims> validateToken(String token) throws ExpiredJwtException
	{
			return Jwts.parserBuilder()
				 .setSigningKey(secret)
				 .requireIssuer(issuer)
				 .build()
				 .parseClaimsJws(token);


	}

	public static String generateRandomSecret()
	{
		var sk = Keys.secretKeyFor(algo);
		return Base64.getEncoder().encodeToString(sk.getEncoded());
	}

}

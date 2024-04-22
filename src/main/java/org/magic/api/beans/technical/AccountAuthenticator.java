package org.magic.api.beans.technical;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AccountAuthenticator implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String PASSWORD = "PASSWORD";
	public static final String LOGIN = "LOGIN";
	protected transient Map<String,String> tokens;

	public AccountAuthenticator() {
		tokens = new HashMap<>();
	}

	public AccountAuthenticator(String login,String password) {
		tokens = new HashMap<>();
		tokens.put(LOGIN, login);
		tokens.put(PASSWORD, password);
	}

	public Map<String, String> getTokens() {
		return tokens;
	}

	public Properties getTokensAsProperties()
	{
		var prop = new Properties();
		getTokens().forEach(prop::put);
		return prop;

	}


	public void addToken(String k, String val)
	{
		tokens.put(k, val);
	}


	public void addLoginPassword(String login,String password)
	{
		tokens.put(LOGIN, login);
		tokens.put(PASSWORD, password);
	}

	public String get(String key)
	{
		return tokens.getOrDefault(key,"");
	}

	public String get(String key,String defaultValue)
	{
		return tokens.getOrDefault(key,defaultValue);
	}


	public String getLogin()
	{
		return get(LOGIN);
	}

	public String getPassword()
	{
		return get(PASSWORD);
	}



}

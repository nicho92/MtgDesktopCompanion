package org.magic.api.beans.technical;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AccountAuthenticator implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String PASSWORD = "PASSWORD";
	public static final String LOGIN = "LOGIN";
	private transient Map<String,String> tokens;

	public AccountAuthenticator() {
		tokens = new HashMap<>();
	}
	
	@Override
	public String toString() {
		return "Acc:"+tokens.keySet();
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


	public String get(String key)
	{
		return tokens.getOrDefault(key,"");
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

package org.magic.services.providers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.magic.api.beans.AccountAuthenticator;
import org.magic.api.interfaces.MTGAuthenticated;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.pricers.impl.MagicCardMarketPricer2;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;

public class AccountsManager {

	private static AccountsManager inst;
	
	private Map<MTGAuthenticated, AccountAuthenticator > keys;
	
	
	public static AccountsManager inst()
	{
		if(inst==null)
			inst = new AccountsManager();
		
		return inst;
	}
	
	public AccountsManager() {
		keys = new HashMap<>();
	}
	
	
	public void addAuthentication(MTGAuthenticated plug, AccountAuthenticator token)
	{
		keys.put(plug, token);
	}
	
	
	public AccountAuthenticator getToken(MTGAuthenticated plug)
	{
		return keys.get(plug);
	}
	
	public Map<MTGAuthenticated, AccountAuthenticator> getKeys() {
		return keys;
	}
	
	public List<MTGPlugin> listAvailablePlugins()
	{
		return PluginRegistry.inst().listPlugins().stream().filter(MTGAuthenticated.class::isInstance).distinct().collect(Collectors.toList());
	}
	
	
	
	public static void main(String[] args) {
		MTGControler.getInstance();
		AccountsManager.inst().addAuthentication(new MagicCardMarketPricer2(), new AccountAuthenticator("Mkm","nicà022", "test"));
		MTGControler.getInstance().saveAccounts();
		
	}
	
	
	public static List<String> generateKeysForMkm()
	{
		return List.of("APP_TOKEN","APP_SECRET","APP_ACCESS_TOKEN", "APP_ACCESS_TOKEN_SECRET");
	}

	public static List<String> generateLoginPasswordsKeys() {
		return List.of(AccountAuthenticator.LOGIN,AccountAuthenticator.PASSWORD);
	}
	
}

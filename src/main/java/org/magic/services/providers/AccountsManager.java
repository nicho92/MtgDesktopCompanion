package org.magic.services.providers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.magic.api.beans.AccountAuthenticator;
import org.magic.api.exports.impl.WooCommerceExport;
import org.magic.api.interfaces.MTGAuthenticated;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.pricers.impl.MagicCardMarketPricer2;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;

public class AccountsManager {

	private static AccountsManager inst;
	
	private Map<String, AccountAuthenticator > keys;
	
	
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
		keys.put(plug.getTiersName(), token);
	}
	
	
	public AccountAuthenticator getToken(MTGAuthenticated plug)
	{
		return getToken(plug.getTiersName());
	}
	
	public AccountAuthenticator getToken(String plugname)
	{
		return keys.get(plugname);
	}
	
	
	public Map<String, AccountAuthenticator> getKeys() {
		return keys;
	}
	
	public List<MTGPlugin> listAvailablePlugins()
	{
		return PluginRegistry.inst().listPlugins().stream().filter(MTGAuthenticated.class::isInstance).distinct().collect(Collectors.toList());
	}
	
	public void saveConfig()
	{
		if(!keys.isEmpty())
			MTGControler.getInstance().saveAccounts();
	}
	
	public void loadConfig()
	{
		keys =  MTGControler.getInstance().listAccounts();
	}
	
	
	
	public static void main(String[] args) {
		MTGControler.getInstance();
		AccountsManager.inst().loadConfig();
		
		
		AccountsManager.inst().getKeys().entrySet().forEach(System.out::println);
		
	}
	
	
	public static List<String> generateKeysForMkm()
	{
		return List.of("APP_TOKEN","APP_SECRET","APP_ACCESS_TOKEN", "APP_ACCESS_TOKEN_SECRET");
	}

	public static List<String> generateLoginPasswordsKeys() {
		return List.of(AccountAuthenticator.LOGIN,AccountAuthenticator.PASSWORD);
	}
	
}

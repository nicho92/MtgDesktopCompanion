package org.magic.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.magic.api.beans.AccountAuthenticator;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGPlugin;

import com.google.gson.JsonObject;

public class AccountsManager {

	private static AccountsManager inst;
	
	private Map<MTGPlugin, AccountAuthenticator > keys;
	
	
	public static AccountsManager inst()
	{
		if(inst==null)
			inst = new AccountsManager();
		
		return inst;
	}
	
	public AccountsManager() {
		keys = new HashMap<>();
	}
	
	
	public void addAuthentication(MTGPlugin plug, AccountAuthenticator token)
	{
		keys.put(plug, token);
	}
	
	public AccountAuthenticator getAuthenticator(MTGPlugin plug)
	{
		return keys.get(plug);
	}
	
	
	public Map<MTGPlugin, AccountAuthenticator> getKeys() {
		return keys;
	}
	

	
	public MTGPlugin loadAuthenticator(String name)
	{
		return listAvailablePlugins().stream().filter(p->name.equalsIgnoreCase(p.getName())).findFirst().orElse(null);
	}
	
	public List<MTGPlugin> listAvailablePlugins()
	{
		return PluginRegistry.inst().listPlugins().stream().filter(p->!p.listAuthenticationAttributes().isEmpty()).sorted().distinct().collect(Collectors.toList());
	}
	
	public void saveConfig()
	{
		if(!keys.isEmpty())
			MTGControler.getInstance().saveAccounts();
	}
	
	public String exportConfig() {
		return new JsonExport().toJson(AccountsManager.inst().getKeys());
	}
	
	public void loadConfig(String o) {
		
		if(!(o==null) && !o.isEmpty())
			loadConfig(new JsonExport().fromJson(o, JsonObject.class));
	}
	
	public void loadConfig(JsonObject o) {
		if(o!=null && !o.isJsonNull())
			o.keySet().forEach(name->{
				var tokens = o.get(name).getAsJsonObject().get("tokens").getAsJsonObject();
				var tok = new AccountAuthenticator();
				tokens.entrySet().forEach(e->tok.addToken(e.getKey(), e.getValue().getAsString()));
				keys.put(loadAuthenticator(name), tok);
			});
	}
	

	public static List<String> generateKeysForMkm()
	{
		return List.of("APP_TOKEN","APP_SECRET","APP_ACCESS_TOKEN", "APP_ACCESS_TOKEN_SECRET");
	}

	public static List<String> generateLoginPasswordsKeys() {
		return List.of(AccountAuthenticator.LOGIN,AccountAuthenticator.PASSWORD);
	}

	
}

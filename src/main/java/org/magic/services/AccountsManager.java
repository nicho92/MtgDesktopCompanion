package org.magic.services;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.technical.AccountAuthenticator;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.CryptoUtils;
import org.magic.services.tools.FileTools;

import com.google.gson.JsonObject;

public class AccountsManager {

	private static AccountsManager inst;
	private Map<MTGPlugin, AccountAuthenticator> keys;
	private Logger logger = MTGLogger.getLogger(this.getClass());

	public String getKey() throws IOException {

		String key=FileTools.readFile(new File(MTGConstants.DATA_DIR.getAbsolutePath(),"key"));
		if(key.isEmpty())
		{
			throw new IOException("Please create a keypass");
		}

		return key;

	}

	public void setKey(String pass) throws IOException
	{
		FileTools.saveFile(new File(MTGConstants.DATA_DIR.getAbsolutePath(),"key"), pass);
	}

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
		var auth = keys.get(plug);

		if(auth==null)
		{
			logger.warn("No Authentifcator found for {}. Please fill it in config",plug );
			auth = new AccountAuthenticator();
		}

		return auth;
	}
	
	public JsonObject toJson()
	{
		var obj = new JsonObject();
		
		keys.entrySet().forEach(e->{
			
			var plugEntry = new JsonObject();
			var tokens = new JsonObject();
			
			e.getValue().getTokens().entrySet().forEach(t->tokens.addProperty(t.getKey(), t.getValue()));
			plugEntry.add("tokens",tokens);
			
			obj.add(e.getKey().getName(), plugEntry);
		});
		return obj;
	}
	
	
	
	public Map<MTGPlugin, AccountAuthenticator> listAuthEntries() {
		return keys;
	}

	public void removeEntry(MTGPlugin selectedValue) {
		keys.remove(selectedValue);
	}

	public MTGPlugin loadAuthenticator(String name)
	{
		return listAvailablePlugins().stream().filter(p->name.equalsIgnoreCase(p.getName())).findFirst().orElse(null);
	}

	public List<MTGPlugin> listAvailablePlugins()
	{
		return PluginRegistry.inst().listPlugins().stream().filter(p->!p.listAuthenticationAttributes().isEmpty()).sorted().distinct().toList();
	}

	public void saveConfig()
	{
		MTGControler.getInstance().saveAccounts();

	}

	public String exportConfig() {
		try {
			return CryptoUtils.encrypt(toJson().toString(),getKey());
		} catch (IOException e) {
			logger.error("Error getting keypass : {}",e.getMessage());
			return "";
		}

	}

	public void loadConfig(String content) {
		if((content!=null) && !content.isEmpty())
		{
			try {
				loadConfig(new JsonExport().fromJson(CryptoUtils.decrypt(content,getKey()), JsonObject.class));
			} catch (Exception _) {
				logger.error("Error while decryptions");
			}
		}
		else
		{
			logger.warn("content ={}",content);
		}
	}

	private void loadConfig(JsonObject o) {
		if(o!=null && !o.isJsonNull())
			o.keySet().forEach(name->{
				var tokens = o.get(name).getAsJsonObject().get("tokens").getAsJsonObject();
				var tok = new AccountAuthenticator();
				tokens.entrySet().forEach(e->tok.addToken(e.getKey(), e.getValue().getAsString()));
				var plug = loadAuthenticator(name);
				if(plug!=null)
					keys.put(plug, tok);
			});
	}


	public static List<String> generateLoginPasswordsKeys() {
		return List.of(AccountAuthenticator.LOGIN,AccountAuthenticator.PASSWORD);
	}



}

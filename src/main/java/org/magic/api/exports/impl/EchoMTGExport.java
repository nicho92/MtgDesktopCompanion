package org.magic.api.exports.impl;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGControler;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.network.URLToolsClient;
import org.magic.services.network.RequestBuilder.METHOD;

import com.google.gson.JsonElement;

public class EchoMTGExport extends AbstractCardExport {

	private String authToken=null;
	public static final String BASE_URL="https://www.echomtg.com";
	private URLToolsClient client;
	
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("EMAIL","PASSWORD");
	}
	
	private void connect() throws IOException
	{
		client = URLTools.newClient();
		
		JsonElement con = RequestBuilder.build().method(METHOD.POST)
				 .url(BASE_URL+"/api/user/auth/")
				 .addContent("email", getAuthenticator().get("EMAIL"))
				 .addContent("password", getAuthenticator().get("PASS"))
				 .setClient(client)
				 .toJson();
		
		authToken=con.getAsJsonObject().get("token").getAsString();
	}

	@Override
	public String getFileExtension() {
		return "";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		if(client==null)
			connect();
		
		
		deck.getMain().entrySet().forEach(entry->{
				try {
					JsonElement list = RequestBuilder.build().method(METHOD.POST)
							 .url(BASE_URL+"/api/inventory/add/")
							 .addContent("auth", authToken)
							 .addContent("mid",entry.getKey().getCurrentSet().getMultiverseid())
							 .addContent("quantity", String.valueOf(entry.getValue()))
							 .addContent("condition", "NM")
							 .addContent("foil", MTGControler.getInstance().getDefaultStock().isFoil()?"1":"0")
							 .setClient(client)
							 .toJson();
					
					if(list.getAsJsonObject().get("status").getAsString().equalsIgnoreCase("error"))
						logger.error("error loading " + entry.getKey()+ ": " + list.getAsJsonObject().get("message").getAsString());
					else
						logger.debug("export: " + list.getAsJsonObject().get("message").getAsString());
					
					notify(entry.getKey());
					
				} catch (IOException e) {
					logger.error("error for " + entry.getKey(),e);
				}
		});
		
	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		if(client==null)
			connect();
		
		var d = new MagicDeck();
				  d.setName(name);
				  d.setDescription("import from "+getName());
				  
				  var list = RequestBuilder.build().method(METHOD.GET)
				 .url(BASE_URL+"/api/inventory/view/")
				 .addContent("auth", authToken)
				 .addContent("start", "0")
				 .addContent("limit", "100")
				 .setClient(client)
				 .toJson();
		
		
		var arr = list.getAsJsonObject().get("items").getAsJsonArray();
		
		arr.forEach(element -> {
			var ob = element.getAsJsonObject();
			MagicEdition ed =null;
			try {
				ed = getEnabledPlugin(MTGCardsProvider.class).getSetById(ob.get("set_code").getAsString());
			} catch (Exception e) {
				logger.error("error with " + ob,e);
			}
			
			
			try {
				List<MagicCard> ret = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(ob.get("name").getAsString(), ed, true);
				d.add(ret.get(0));
				notify(ret.get(0));
			} catch (IOException e) {
				logger.error(e);
			}
			

			
			
		});
		return d;
	}

	@Override
	public String getName() {
		return "EchoMTG";
	}
	

	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj ==null)
			return false;
		
		return hashCode()==obj.hashCode();
	}
	

}

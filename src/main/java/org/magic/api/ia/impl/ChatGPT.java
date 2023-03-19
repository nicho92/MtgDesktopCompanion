package org.magic.api.ia.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.http.entity.StringEntity;
import org.magic.api.beans.MTGDocumentation;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.interfaces.abstracts.AbstractIA;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class ChatGPT extends AbstractIA {

	
	private MTGHttpClient client;
	private static String TOKEN = "TOKEN";
	
	private JsonElement query( JsonObject obj,String endpoint) throws IOException
	{
		
		if(client==null)
			client = URLTools.newClient();
		
		if(getAuthenticator().get(TOKEN)==null)
			throw new IOException("Please fill TOKEN value in Account configuration");
		
		
		var headers = new HashMap<String, String>();
		headers.put("Authorization", "Bearer " + getAuthenticator().get(TOKEN));
		headers.put(URLTools.CONTENT_TYPE, URLTools.HEADER_JSON);
		var resp =  client.doPost("https://api.openai.com/"+getVersion()+endpoint, new StringEntity(obj.toString(), MTGConstants.DEFAULT_ENCODING), headers);
		return URLTools.toJson(resp.getEntity().getContent());
		
	}
	
	@Override
	public String ask(String prompt) throws IOException
	{
		logger.info("asking : {} ",prompt);
		var obj = new JsonObject();
					obj.addProperty("model", getString("MODEL"));
					obj.addProperty("prompt", prompt);
					obj.addProperty("temperature", getDouble("TEMPERATURE"));
					obj.addProperty("max_tokens", getInt("MAX_TOKEN"));

		var json = query(obj,"/completions");
		
		logger.debug(json);
		
		try {
			var ret = json.getAsJsonObject().get("choices").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString();
					logger.info("{} answer : {} ",getName(), ret);
			return ret;
		}
		catch(Exception e)
		{
			throw new IOException(e);
		}
	}
	
	public String chat(String prompt) throws IOException
	{
		logger.info("chat : {} ",prompt);
		var obj = new JsonObject();
					obj.addProperty("model", "gpt-3.5-turbo-0301");
					var arr = new JsonArray();
					var obj2 = new JsonObject();
						  obj2.addProperty("content", prompt);
						  obj2.addProperty("role", "user");
						  
						  arr.add(obj2);
					obj.add("messages", arr);
					
		var jsonReponse = query(obj,"/chat/completions");
		
		try {
			var ret = jsonReponse.getAsJsonObject().get("choices").getAsJsonArray().get(0).getAsJsonObject().get("message").getAsJsonObject().get("content").getAsString();
			logger.info("{} answer : {} ",getName(), ret);
			return ret;
		}
		catch(Exception e)
		{
			throw new IOException(e);
		}
	}
	
	

	@Override
	public String getName() {
		return "ChatGPT";
	}
	
	@Override
	public String getVersion() {
		return "v1";
	}
	
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of(TOKEN);
	}
	
	@Override
	public Map<String, String> getDefaultAttributes() {
			var map = super.getDefaultAttributes();
			map.put("MODEL", "text-davinci-003");
			map.put("TEMPERATURE", "0");
			map.put("MAX_TOKEN", "2000");
			return map;
	}
	
		
	@Override
	public MTGDocumentation getDocumentation() {
		return new MTGDocumentation("https://platform.openai.com/docs/models",FORMAT_NOTIFICATION.HTML);
	}
	
	private JsonElement read(JsonObject obj, String... atts)
	{
		
		for(String att: atts)
		{
			if(obj.get(att)!=null && !obj.get(att).isJsonNull())
				return obj.get(att);
			
		}
		
		return JsonNull.INSTANCE;
	}
	
	@Override
	public MagicCard generateRandomCard(String description) throws IOException {
		
		
		
		var ret = chat(NEW_CARD_QUERY  +( (description==null || description.isEmpty())?"": " with this description  : "+description));
			
		if(ret==null)
			return null;
		
		var obj = URLTools.toJson(ret).getAsJsonObject();
		
		var mc = new MagicCard();
			 mc.setName(read(obj,"name").getAsString());
			 mc.setText(read(obj,"text").getAsString());
			 mc.setColors(EnumColors.parseByManaCost(mc.getCost()));
			 try {
				 mc.setFlavor(read(obj,"flavor").getAsString());
			 }
			 catch(Exception e)
			 {
				 //do nothing
			 }
			 
			 
			 if(read(obj,"type").isJsonPrimitive())
			 {
				 mc.getTypes().add(read(obj,"type").getAsString());
			 }
			 else 
			 {
				 try {
					 read(obj,"types").getAsJsonArray().forEach(je->mc.getTypes().add(je.getAsString()));
				 }catch(Exception e)
				 {
					 //do nothing
				 }
				 
			 }
			 
			 try {
				 read(obj,"supertypes").getAsJsonArray().forEach(je->mc.getSupertypes().add(je.getAsString()));
			 }
			 catch(Exception e)
			 {
				 //do nothing
			 }
			 try {
			 read(obj,"subtypes").getAsJsonArray().forEach(je->mc.getSubtypes().add(je.getAsString()));
			 }
			 catch(Exception e)
			 {
				 //do nothing
			 }
			 
			 if(mc.isCreature())
			 {
				 mc.setPower(read(obj,"power").getAsString());
				 mc.setToughness(read(obj,"toughness").getAsString());
		
			 }
			 
			 if(!mc.isLand()) {
				 mc.setCost(read(obj,"manaCost","mana_cost","cost").getAsString());
			 }
			 
			 
		return mc;
	}
	
	public static void main(String[] args) throws IOException {
		MTGControler.getInstance().loadAccountsConfiguration();
		new ChatGPT().generateRandomCard(JOptionPane.showInputDialog("Description"));
		System.exit(0);
	}


}

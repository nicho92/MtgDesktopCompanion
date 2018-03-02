package org.magic.api.providers.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.magic.api.beans.Booster;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;
import org.magic.services.MTGConstants;
import org.magic.tools.InstallCert;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class DeckbrewProvider extends AbstractCardsProvider {
	private String urldeckbrewJSON = "https://api.deckbrew.com/mtg";
	private Gson gson;
	private List<MagicEdition> list;
	private String encoding="UTF-8";

	public DeckbrewProvider() {
		super();
		gson = new Gson();
		list = new ArrayList<>();
		init();
		
	}
	
	public void init() {
		try {
    	    	InstallCert.install("api.deckbrew.com");
		    	System.setProperty("javax.net.ssl.trustStore",new File(MTGConstants.CONF_DIR,MTGConstants.KEYSTORE_NAME).getAbsolutePath());
		  	} catch (Exception e1) {
				logger.error(e1);
			}
	}
	
	
	public MagicCard getCardById(String id) throws  IOException {
		String url = urldeckbrewJSON +"/cards/"+id;
		logger.info("get Card ID " + url );
		Reader reader = new InputStreamReader(new URL(url).openStream(),encoding);
		return gson.fromJson(reader, MagicCard.class);
				
	}
	
	
	
	
	public List<MagicCard> searchCardByCriteria(String att,String crit,MagicEdition me,boolean exact) throws IOException {
		
		crit=att+"="+URLEncoder.encode(crit,encoding);

		String url = urldeckbrewJSON +"/cards?"+crit;
		
		Reader reader = new InputStreamReader(new URL(url).openStream(),encoding);
		List<MagicCard> retour=new ArrayList<>();
		JsonArray root = new JsonParser().parse(reader).getAsJsonArray();
		int page=1;
		boolean gonextpage=true;
		
		
		while(gonextpage)
		{
			String pagination;
			if(crit==null)
				pagination="?";	
			else
				pagination="&";
				
			URL u =null;
			if(root.size()==100)
			{
				u = new URL(url+pagination+"page="+page++);
				gonextpage=true;
			}
			else
			{
				u = new URL(url);
				gonextpage=false;
			}
			
			logger.info("Connexion to " + u);
			
			reader = new InputStreamReader(u.openStream(),encoding);
			root = new JsonParser().parse(reader).getAsJsonArray();
			for(int i = 0;i<root.size();i++)
			{
				JsonObject e = root.get(i).getAsJsonObject();
				MagicCard mc = new MagicCard();
					
					if(e.get("name")!=null)
						mc.setName(e.get("name").getAsString());
					
					if(e.get("id")!=null)
						mc.setId(e.get("id").getAsString());
					
					if(e.get("cmc")!=null)
						mc.setCmc(e.get("cmc").getAsInt());
					
					if(e.get("cost")!=null)
						mc.setCost(e.get("cost").getAsString());
					
					if(e.get("text")!=null)
						mc.setText(e.get("text").getAsString());
					
					if(e.get("power")!=null)
						mc.setPower(e.get("power").getAsString());
					
					if(e.get("toughness")!=null)
						mc.setToughness(e.get("toughness").getAsString());
					
					if(e.get("types")!=null){
						Iterator<JsonElement> it = e.get("types").getAsJsonArray().iterator();
						while(it.hasNext())
						{
							mc.getTypes().add(it.next().getAsString());
						}
					}
					if(e.get("subtypes")!=null){
						Iterator<JsonElement> it2 = e.get("subtypes").getAsJsonArray().iterator();
						while(it2.hasNext())
						{
							mc.getSubtypes().add(it2.next().getAsString());
						}
					}
					
					if(e.get("colors")!=null){
						Iterator<JsonElement> it3 = e.get("colors").getAsJsonArray().iterator();
						while(it3.hasNext())
						{
							mc.getColors().add(it3.next().getAsString());
						}
					}
					
					JsonArray editions = e.getAsJsonArray("editions");
					for(int j=0;j<editions.size();j++)
					{
						JsonObject obj = editions.get(j).getAsJsonObject();
						MagicEdition ed = getSetById(obj.get("set_id").getAsString());
									 ed.setArtist(obj.get("artist").getAsString());
									 ed.setMultiverse_id(obj.get("multiverse_id").getAsString());
									 ed.setRarity(obj.get("rarity").getAsString());
									 ed.setNumber(obj.get("number").getAsString());
									 
									 MagicCardNames name = new MagicCardNames();
									 				name.setName(mc.getName());
									 				name.setLanguage("English");
									 				name.setGathererId(Integer.parseInt(ed.getMultiverse_id()));
									 mc.getForeignNames().add(name);
									 mc.setLayout(obj.get("layout").getAsString());
									 mc.getEditions().add(ed);
									 mc.setNumber(ed.getNumber());
									 
					}
					if(e.get("formats")!=null){
						JsonObject obj = e.get("formats").getAsJsonObject();
						for(Entry<String,JsonElement> k : obj.entrySet())
						{
								MagicFormat format = new MagicFormat();
								format.setFormat(k.getKey());
								format.setLegality(k.getValue().getAsString());
								mc.getLegalities().add(format);
						}
					}
					
					
				retour.add(mc);
			}
			
		}
		
		return retour;
	}
	
	
	
	public List<MagicEdition> loadEditions() throws IOException  {
		
		String url = urldeckbrewJSON+"/sets";
		
		JsonReader reader= new JsonReader(new InputStreamReader(new URL(url).openStream()));
		
		JsonArray root = new JsonParser().parse(reader).getAsJsonArray();
		
		if(list.isEmpty())
			for(int i = 0;i<root.size();i++)
			{
				JsonObject e = root.get(i).getAsJsonObject();
				MagicEdition ed = getSetById(e.get("id").getAsString());
				list.add(ed);
			}
		
		return list;
		
	}

	public MagicEdition getSetById(String id) throws IOException   {

		String url = urldeckbrewJSON+"/sets/"+id;
		Reader reader = new InputStreamReader(new URL(url).openStream(),encoding);
		JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
		
		MagicEdition ed = new MagicEdition();
					 ed.setId(root.get("id").getAsString());
					 ed.setBorder(root.get("border").getAsString());
					 ed.setSet(root.get("name").getAsString());
					 ed.setType(root.get("type").getAsString());
					 ed.setCardCount(0);
		return ed;
		
	}


	@Override
	public String[] getQueryableAttributs() {
		return new String[]{"name","type","subtype","supertype","oracle","set","rarity","color","multicolor","multiverseid","format","status"};
		
	}
	
	@Override
	public String getName() {
		return "DeckBrew Provider";
	}

	@Override
	public String[]  getLanguages() {
		return new String[]{"English"};
	}

	@Override
	public Booster generateBooster(MagicEdition me) {
		return null;
	}

	@Override
	public MagicCard getCardByNumber(String id, MagicEdition me) throws IOException {
		return null;
	}

	@Override
	public String getVersion() {
		return "1";
	}

	@Override
	public URL getWebSite() throws MalformedURLException {
		return new URL("https://deckbrew.com/api/");
	}

	@Override
	public STATUT getStatut() {
		return STATUT.ABANDONNED;
	}

	

}

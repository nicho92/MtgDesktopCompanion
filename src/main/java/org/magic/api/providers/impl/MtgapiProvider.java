package org.magic.api.providers.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.magic.api.beans.Booster;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;
import org.magic.services.MTGLogger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class MtgapiProvider extends AbstractCardsProvider{
	Logger logger = MTGLogger.getLogger(this.getClass());

	
	String urlJson = "http://api.mtgapi.com/v2";
	
	CloseableHttpClient httpclient;


	private boolean enable;

	@Override
	public void enable(boolean enabled) {
		this.enable=enabled;
		
	}

	@Override
	public boolean isEnable() {
		return enable;
	}
	
	
	public MtgapiProvider() {
		httpclient = HttpClients.createDefault();
		init();
	}
	
	@Override
	public void init() {
		//do nothing
	}
	
	
	@Override
	public MagicCard getCardById(String id) throws  IOException {
		
		String url =urlJson+"/cards?multiverseid="+id;
		
		
		HttpGet httpget = new HttpGet(url);
		CloseableHttpResponse response = httpclient.execute(httpget);
		
		
		Reader reader = new InputStreamReader(response.getEntity().getContent(),"UTF-8");
		JsonElement root = new JsonParser().parse(reader);
		JsonArray arr = root.getAsJsonObject().getAsJsonArray("cards");
		
		if(arr.size()<=0)
			return null;
		
		
		MagicCard mc = new MagicCard();
				  mc.setName(arr.get(0).getAsJsonObject().get("name").toString());
		
		return mc;
	}
	@Override
	public List<MagicCard> searchCardByCriteria(String att,String crit,MagicEdition ed,boolean exact) throws IOException {
		List<MagicCard> list = new ArrayList<>();
		
		crit=att+"="+crit;
		
		String url =urlJson+"/cards?"+crit;
		
		logger.debug(url);
		
		HttpGet httpget = new HttpGet(url);
		CloseableHttpResponse response = httpclient.execute(httpget);
		
		
		Reader reader = new InputStreamReader(response.getEntity().getContent(),"UTF-8");
		JsonElement root = new JsonParser().parse(reader);
		
		if(root.getAsJsonObject().get("cards").isJsonNull())
			return list;
		
		JsonArray arr = root.getAsJsonObject().getAsJsonArray("cards");
		
		
		if(arr.size()<=0)
			return list;
		
		
		
		String nextUrl="";
		
		while(nextUrl!=null)
		{
				for (int i = 0; i < arr.size(); i++)
				{
					MagicCard mc = new MagicCard();
					  mc.setName(arr.get(i).getAsJsonObject().get("name").getAsString());
					  mc.setId(arr.get(i).getAsJsonObject().get("multiverseid").getAsString());
					 
					  if(!arr.get(i).getAsJsonObject().get("cmc").isJsonNull())
						  mc.setCmc(arr.get(i).getAsJsonObject().get("cmc").getAsInt());
					  
					  if(!arr.get(i).getAsJsonObject().get("text").isJsonNull())
						  mc.setText(arr.get(i).getAsJsonObject().get("text").getAsString());
					  
					  if(!arr.get(i).getAsJsonObject().get("manaCost").isJsonNull())
						  mc.setCost(arr.get(i).getAsJsonObject().get("manaCost").getAsString());
					  else
						  mc.setCost("");
					  
					  if(!arr.get(i).getAsJsonObject().get("power").isJsonNull())
						  mc.setPower(arr.get(i).getAsJsonObject().get("power").getAsString());
					  
					  if(!arr.get(i).getAsJsonObject().get("toughness").isJsonNull())
						  mc.setToughness(arr.get(i).getAsJsonObject().get("toughness").getAsString());
					  
					  if(!arr.get(i).getAsJsonObject().get("loyalty").isJsonNull())
						  mc.setLoyalty(arr.get(i).getAsJsonObject().get("loyalty").getAsInt());
					  
					  
					 MagicEdition me = getSetById(arr.get(i).getAsJsonObject().get("set").getAsString());
					  	me.setMultiverse_id(mc.getId());
					  	me.setSet_url(arr.get(i).getAsJsonObject().get("links").getAsJsonObject().get("set").getAsString());
					  	me.setId(arr.get(i).getAsJsonObject().get("set").getAsString());
					  	me.setRarity(arr.get(i).getAsJsonObject().get("rarity").getAsString());
					  	mc.getEditions().add(me);
					  
					  
					  	
					  	if(!arr.get(i).getAsJsonObject().get("foreignNames").isJsonNull())
						{
						  	JsonArray listforeignNames = arr.get(i).getAsJsonObject().get("types").getAsJsonArray();
						  	 for (int t=0; t<listforeignNames.size(); t++) {
						  		MagicCardNames mcn = new MagicCardNames();
						  			mcn.setGathererId(listforeignNames.get(i).getAsJsonObject().get("multiverseid").getAsInt());
						  			mcn.setLanguage(listforeignNames.get(i).getAsJsonObject().get("language").getAsString());
						  			mcn.setName(listforeignNames.get(i).getAsJsonObject().get("name").getAsString());
						  		
						  			mc.getForeignNames().add(mcn);
						  	 }
 	
						}
					  	
					  
					  List<String> ltypes = new ArrayList<>();
					  
					  if(!arr.get(i).getAsJsonObject().get("types").isJsonNull())
					  {
						  JsonArray types = arr.get(i).getAsJsonObject().get("types").getAsJsonArray();
						  for (int t=0; t<types.size(); t++) {
							  ltypes.add( types.get(t).getAsString() );
						  }
						  
						  mc.setTypes(ltypes);
					  }
					  
					  if(!arr.get(i).getAsJsonObject().get("supertypes").isJsonNull())
					  {
						  List<String> stypes = new ArrayList<>();
						  JsonArray suptypes = arr.get(i).getAsJsonObject().get("supertypes").getAsJsonArray();
						  for (int t=0; t<suptypes.size(); t++) {
							  stypes.add( suptypes.get(t).getAsString() );
						  }
						  mc.setSupertypes(stypes);
					  }
					  
					  if(!arr.get(i).getAsJsonObject().get("subtypes").isJsonNull())
					  {
						  List<String> subtypes = new ArrayList<>();
						  JsonArray supAtypes = arr.get(i).getAsJsonObject().get("subtypes").getAsJsonArray();
						  for (int t=0; t<supAtypes.size(); t++) {
							  subtypes.add( supAtypes.get(t).getAsString() );
						 
						  }
						  mc.setSubtypes(subtypes);
					  }
					  
					  list.add(mc);
				}
				
				nextUrl=hasNextPage(root);
				
				if(nextUrl!=null)
				{
					httpget.releaseConnection();
					httpget = new HttpGet(nextUrl);
					response = httpclient.execute(httpget);
					reader = new InputStreamReader(response.getEntity().getContent(),"UTF-8");
					root = new JsonParser().parse(reader);
					arr = root.getAsJsonObject().getAsJsonArray("cards");
				}
			
		}
		
		
		return list;
	}

	private String hasNextPage(JsonElement root) {
		
		if(root.getAsJsonObject().get("links").getAsJsonObject().get("next").isJsonNull())
			return null;
		else
			return root.getAsJsonObject().get("links").getAsJsonObject().get("next").getAsString();
		
		
	}
	@Override
	public List<MagicEdition> loadEditions() {
		return new ArrayList<>();
	}

	public MagicEdition getSetById(String id) throws IOException {
		String url =urlJson+"/sets?code="+id;
		
		logger.debug(url);
		
		HttpGet httpget = new HttpGet(url);
		CloseableHttpResponse response = httpclient.execute(httpget);
		
		
		Reader reader = new InputStreamReader(response.getEntity().getContent(),"UTF-8");
		JsonElement root = new JsonParser().parse(reader);
		
		if(root.getAsJsonObject().getAsJsonArray("sets").isJsonNull())
			return null;
		
		JsonArray arr = root.getAsJsonObject().getAsJsonArray("sets");
		
		
		
		
		
		MagicEdition me = new MagicEdition();
					 me.setId(id);
					 me.setSet(arr.get(0).getAsJsonObject().get("name").getAsString());
					 me.setReleaseDate(arr.get(0).getAsJsonObject().get("releaseDate").getAsString());
					 
					 if(!arr.get(0).getAsJsonObject().get("block").isJsonNull())
						 me.setBlock(arr.get(0).getAsJsonObject().get("block").getAsString());
					 
					 me.setCardCount(arr.get(0).getAsJsonObject().get("cardCount").getAsInt());
					 
		
		httpget.releaseConnection();
		
		return me;
	
		
		
	}
	

	public List<String> getCardNames()
	{
		List<String> lists = new ArrayList<>();
		String url="http://api.mtgapi.com/v2/names";
		HttpGet httpget = new HttpGet(url);
		try (CloseableHttpResponse response = httpclient.execute(httpget))
		{
			Reader reader = new InputStreamReader(response.getEntity().getContent(),"UTF-8");
			JsonElement root = new JsonParser().parse(reader);
			JsonObject arr = root.getAsJsonObject().get("names").getAsJsonObject();
			
			for(Entry<String, JsonElement> e : arr.entrySet())
			{
				lists.add(e.getValue().getAsString());
			}
			 
			return lists;
		} 
		catch (Exception e) {
			logger.error("couldn't get cardnames",e);
		}
		finally {
			httpget.releaseConnection();
			
		}
		return lists;
	}
	
	
	public List<String> getListType() {
		return new ArrayList<>();
	}
	
	public List<String> getListSubTypes()  {
		return new ArrayList<>();
	}
	
	@Override
	public String[] getQueryableAttributs() {
		return new String[]{"name","artist","border","cmc","colors","flavor","foreignNames","hand","layout","legalities","life","loyalty","manaCost","multiversid","names","number","originalText","originalType","power","printings","rarity","rulings","set","subtypes","supertypes","text","toughness","type","types","variations","watermark"};
	}
	
	@Override
	public String toString() {
		return getName();
	}

	
	public String getName() {
		return "MTG API Provider";
	}

	@Override
	public String[]  getLanguages() {
		return new String[0];
	}

	@Override
	public Booster generateBooster(MagicEdition me) {
		return null;
	}

	@Override
	public MagicCard getCardByNumber(String id, MagicEdition me) throws Exception {
		return null;
	}

	@Override
	public String getVersion() {
		return "v2";
	}

	@Override
	public URL getWebSite() throws MalformedURLException {
		return new URL("https://mtgapi.com/" );
	}

	@Override
	public STATUT getStatut() {
		return STATUT.ABANDONNED;
	}

}

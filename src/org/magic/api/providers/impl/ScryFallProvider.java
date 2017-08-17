package org.magic.api.providers.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.api.mkm.tools.Tools;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.services.MTGControler;
import org.magic.tools.ColorParser;
import org.magic.tools.InstallCert;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class ScryFallProvider implements MagicCardsProvider {

	private boolean enabled;
	static final Logger logger = LogManager.getLogger(ScryFallProvider.class.getName());
	private static String baseURI ="https://api.scryfall.com";
	private Map<String , MagicEdition> cache;
	private JsonParser parser;
	
	
	public ScryFallProvider() {
		
	}
	
	@Override
	public void init() {
		cache=new TreeMap<String,MagicEdition>();
		parser = new JsonParser();
    	try {
    		InstallCert.install("api.scryfall.com");
    		System.setProperty("javax.net.ssl.trustStore",new File(MTGControler.CONF_DIR,MTGControler.KEYSTORE_NAME).getAbsolutePath());
    	} catch (Exception e1) {
			logger.error(e1);
		}

	}

	@Override
	public MagicCard getCardById(String id) throws Exception {
		return searchCardByCriteria("id", id, null).get(0);
	}

	@Override
	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition me) throws Exception {
		List<MagicCard> list = new ArrayList<MagicCard>();
		
		String url = baseURI+"/cards/";
				if(att.equals("name"))
					url+="search?q="+URLEncoder.encode("++"+crit +" include:extras","UTF-8");
				else if(att.equals("custom"))
					url+="search?q="+URLEncoder.encode(crit,"UTF-8");
				else if(att.equals("set"))
					url+="search?q=++e:"+URLEncoder.encode(crit,"UTF-8")+"%20order=set";
				else if(att.equals("id"))
					url+=URLEncoder.encode(crit,"UTF-8");
				else
					url+="search?q="+URLEncoder.encode(att+":"+crit+" include:extras","UTF-8");
				
				if(me!=null)
					url+="%20" +URLEncoder.encode("e:"+me.getId(),"UTF-8");
				
				
				
		HttpURLConnection con;
		JsonReader reader;
		boolean hasMore=true;
		while(hasMore)
		{
			
			logger.debug(url);
			con = (HttpURLConnection) getConnection(url);
			
			if(testError(con)==false)
				return list;
			
			try{
				reader= new JsonReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
				JsonElement el = parser.parse(reader);
			
			
				
			JsonArray jsonList = el.getAsJsonObject().getAsJsonArray("data");
			for(int i=0;i<jsonList.size();i++)
			{
				MagicCard mc = generateCard(jsonList.get(i).getAsJsonObject());
				list.add(mc);
			}
			hasMore=el.getAsJsonObject().get("has_more").getAsBoolean();
			
			if(hasMore)
				url=el.getAsJsonObject().get("next_page").getAsString();
			
			Thread.sleep(50);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				logger.error(e);
				hasMore=false;
			}
		}
			
		return list;
	}
	
	private boolean testError(HttpURLConnection connection) {
		try {
			return (connection.getResponseCode()>=200 && connection.getResponseCode()<300);
		} catch (IOException e) {
			return false;
		}
	}

		public static void main(String[] args) throws Exception {
		
		ScryFallProvider prov = new ScryFallProvider();
		
		prov.init();
		prov.loadEditions();
		List<MagicCard> res = prov.searchCardByCriteria("custom", "s:akh ++is:token", null);
		
		//for(MagicCard mc : res)
			System.out.println(res.size());
		
	}
	
	
	@Override
	public MagicCard getCardByNumber(String id, MagicEdition me) throws Exception {
		String url = baseURI+"/cards/"+me.getId()+"/"+id;
		URLConnection con = getConnection(url);
		JsonReader reader= new JsonReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
		JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
		return generateCard(root);
	}

	@Override
	public List<MagicEdition> loadEditions() throws Exception {
		if(cache.size()<=0)
		{
			String url = baseURI+"/sets";
			logger.info("connect to " + url);
			
			URLConnection con = getConnection(url);
			JsonReader reader= new JsonReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
			JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
			for(int i = 0;i<root.get("data").getAsJsonArray().size();i++)
			{
				
				JsonObject e = root.get("data").getAsJsonArray().get(i).getAsJsonObject();
				MagicEdition ed = generateEdition(e.getAsJsonObject());
				cache.put(ed.getId(), ed);
			}
		}
		return new ArrayList<MagicEdition>(cache.values());
	}

	@Override
	public MagicEdition getSetById(String id) throws Exception {
		if(cache.size()>0)
		{
			for(MagicEdition ed : cache.values())
				if(ed.getId().equalsIgnoreCase(id))
					return (MagicEdition)BeanUtils.cloneBean(ed);
		}
		try {
			JsonReader reader= new JsonReader(new InputStreamReader(getConnection(baseURI+"/sets/"+id.toLowerCase()).getInputStream(),"UTF-8"));
			JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
			return generateEdition(root.getAsJsonObject());
		}catch(Exception e)
		{
			MagicEdition ed = new MagicEdition();
			ed.setId(id);
			ed.setSet(id);
			return ed;
		}
		
		
	}

	@Override
	public String[]  getLanguages() {
		return new String[]{"English"};
	}

	@Override
	public String[] getQueryableAttributs() {
		return new String[]{"name","custom","type","color","oracle","mana","cmc","power","toughness","loyalty","is","rarity","cube","artist","flavor","watermark","border","frame","set"};
	}

	@Override
	public List<MagicCard> openBooster(MagicEdition me) throws Exception {
		
				List<MagicCard> ret= new ArrayList<MagicCard>();
		
		
			   List<MagicCard> commons = searchCardByCriteria("custom",  "s:"+me.getId()+" r:common -t:land", me);
			   Collections.shuffle(commons);
			   ret.addAll(commons.subList(0, 10));
			   
			   List<MagicCard> uncommons = searchCardByCriteria("rarity", "uncommon", me);
			   Collections.shuffle(uncommons);
			   ret.addAll(uncommons.subList(0, 3));
			   
			   List<MagicCard> rares = searchCardByCriteria("custom", "s:"+me.getId()+" r:rare or r:mythics", null);
			   Collections.shuffle(rares);
			   ret.addAll(rares.subList(0, 1));
			   
			   List<MagicCard> lands = searchCardByCriteria("custom", "s:"+me.getId()+" t:land", null);
			   Collections.shuffle(lands);
			   ret.addAll(lands.subList(0, 1));
			   /*
			   List<MagicCard> tokens = searchCardByCriteria("custom", "s:"+me.getId()+" ++is:token", null);
			   Collections.shuffle(tokens);
			   ret.addAll(tokens.subList(0, 1));
			   
			   */
			   
			   
		return ret;
	}

	@Override
	public String getVersion() {
		return "0.5";
	}

	@Override
	public URL getWebSite() throws MalformedURLException {
		return new URL("https://scryfall.com/");
	}

	@Override
	public void enable(boolean enabled) {
		this.enabled=enabled;

	}

	@Override
	public boolean isEnable() {
		return enabled;
	}

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	@Override
	public String getName() {
		return "Scryfall";
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	
	private URLConnection getConnection(String url)
	{
		try {
			logger.debug("get stream from " + url);
			URLConnection connection = new URL(url).openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			connection.connect();
			return connection;
		} catch (IOException e) {
			logger.error(e);
			return null;
		}	
	}
	
	private MagicCard generateCard(JsonObject obj) throws Exception
	{
		final MagicCard mc = new MagicCard();
		
		  mc.setId(obj.get("id").getAsString());
		  
		  try{mc.setMultiverseid(obj.get("multiverse_id").getAsInt());}catch(NullPointerException e) { };
		  try{mc.setText(obj.get("oracle_text").getAsString());}catch(NullPointerException e) { mc.setText(""); };
		  try{mc.getTypes().add(obj.get("type_line").getAsString());}catch(NullPointerException e) {  };
				
		  mc.setName(obj.get("name").getAsString());
		  mc.setCmc(obj.get("cmc").getAsInt());
		  mc.setCost(obj.get("mana_cost").getAsString());
		  mc.setLayout(obj.get("layout").getAsString());
		  
		  MagicCardNames n = new MagicCardNames();
						  n.setLanguage("English");
						  n.setName(mc.getName());
						  try
						  {
							  n.setGathererId(obj.get("multiverse_id").getAsInt());
						  }
						  catch(NullPointerException e) {
							  n.setGathererId(0);
						  };
		  
		  mc.getForeignNames().add(n);
		  //String uri = obj.get("uri").getAsString();
		  //uri=uri.substring(uri.lastIndexOf("/")+1);
		  mc.setNumber(obj.get("collector_number").getAsString());
		  
		  try{mc.setArtist(obj.get("artist").getAsString());}catch(NullPointerException e) { };
		  try{mc.setReserved(obj.get("reserved").getAsBoolean());}catch(NullPointerException e) { };
		  try{mc.setPower(obj.get("power").getAsString());}catch(NullPointerException e) { };
		  try{mc.setToughness(obj.get("toughness").getAsString());}catch(NullPointerException e) { };
		  try{mc.setLoyalty(obj.get("loyalty").getAsInt());}catch(Exception e) { };
		  try{mc.setWatermarks(obj.get("watermark").getAsString());}catch(NullPointerException e) { };

		   if(obj.get("colors")!=null){
				Iterator<JsonElement> it = obj.get("colors").getAsJsonArray().iterator();
				while(it.hasNext())
					mc.getColors().add(ColorParser.getNameByCode(it.next().getAsString()));
			
		   }
		   
		   if(obj.get("color_identity")!=null){
				Iterator<JsonElement> it = obj.get("color_identity").getAsJsonArray().iterator();
				while(it.hasNext())
					mc.getColorIdentity().add("{"+it.next().getAsString()+"}");
		   }
		   
		   
		   if(obj.get("legalities")!=null) {
			  JsonObject legs= obj.get("legalities").getAsJsonObject();
			  Iterator<Entry<String, JsonElement>> it = legs.entrySet().iterator();
			  while(it.hasNext())
			  {
				  Entry<String, JsonElement> ent = it.next();
				  MagicFormat format = new MagicFormat();
				  format.setFormat(ent.getKey());
				  format.setLegality(ent.getValue().getAsString());
				  mc.getLegalities().add(format);
			  }
		   }
		   
		  mc.setTranformable(mc.getLayout().equals("transform")||mc.getLayout().equals("meld"));
		  mc.setFlippable(mc.getLayout().equals("flip")); 
		   
		  
		  if(obj.get("all_parts")!=null)
		  {
			  JsonArray arr = obj.get("all_parts").getAsJsonArray();
			  
			  int index = -1;
			  for(int i=0;i<arr.size();i++)
			  {
				  if(arr.get(i).getAsJsonObject().get("name").getAsString().equals(mc.getName()))
				  {
					  index=i;
					  break;
				  }
					  
			  }
			  arr.remove(index);
			  if(arr.size()==1)
				  mc.setRotatedCardName(arr.get(0).getAsJsonObject().get("name").getAsString());
			  else
				  mc.setRotatedCardName(arr.get(1).getAsJsonObject().get("name").getAsString());
			  
			  
		  }
		
		  
		  
		  
		  MagicEdition ed = (MagicEdition)BeanUtils.cloneBean(getSetById(obj.get("set").getAsString()));
					  ed.setArtist(mc.getArtist());
					  if(mc.getMultiverseid()!=null)
						  ed.setMultiverse_id(String.valueOf(mc.getMultiverseid()));
					  
					  ed.setRarity(obj.get("rarity").getAsString());
					  ed.setOnlineOnly(obj.get("digital").getAsBoolean());
					  ed.setNumber(mc.getNumber());
		  mc.getEditions().add(ed);
		 
		  new Thread(new Runnable() {
			public void run() {
				try {
					initOtherEdition(mc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		  
		
		  
		return mc;
		
	}
	
	
	private void initOtherEdition(MagicCard mc) throws Exception {
		
		String url=baseURI+"/cards/search?q=+"
				+ URLEncoder.encode("++!'"+mc.getName()+"'","UTF-8")
				+ "%20include:extras"
				+ "%20-s:"+mc.getEditions().get(0).getId();

		HttpURLConnection con;
		
		
		JsonReader reader;
		boolean hasMore=true;
		while(hasMore)
		{
			con = (HttpURLConnection) getConnection(url);
			if(!testError(con))
				return;
			
			try{
				reader= new JsonReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
				JsonElement el = parser.parse(reader);
			
				JsonArray jsonList = el.getAsJsonObject().getAsJsonArray("data");
				for(int i=0;i<jsonList.size();i++)
				{
					JsonObject obj = jsonList.get(i).getAsJsonObject();
					MagicEdition ed = getSetById(obj.get("set").getAsString());
					
						if(obj.get("artist")!=null)
							ed.setArtist(obj.get("artist").getAsString());
						
						if(obj.get("multiverse_id")!=null)
							ed.setMultiverse_id(obj.get("multiverse_id").getAsString());
						
						if(obj.get("rarity")!=null)
							ed.setRarity(obj.get("rarity").getAsString());
						
						if(obj.get("collector_number")!=null)
							ed.setNumber(obj.get("collector_number").getAsString());
					
						mc.getEditions().add(ed);
				}
			hasMore=el.getAsJsonObject().get("has_more").getAsBoolean();
			
			if(hasMore)
				url=el.getAsJsonObject().get("next_page").getAsString();
			
			Thread.sleep(50);
			}
			catch(Exception e)
			{
				logger.error(e);
				hasMore=false;
			}
		}
	}

	private MagicEdition generateEdition(JsonObject obj)
	{
		MagicEdition ed = new MagicEdition();
			ed.setId(obj.get("code").getAsString());
			ed.setSet(obj.get("name").getAsString());
			ed.setType(obj.get("set_type").getAsString());
			
			if(obj.get("digital")!=null)
				ed.setOnlineOnly(true);
			else
				ed.setOnlineOnly(false);
			
			
			if(obj.get("border")!=null)
				ed.setBorder(obj.get("border").getAsString());
		
			ed.setCardCount(obj.get("card_count").getAsInt());
			
			if(obj.get("block")!=null)
				ed.setBlock(obj.get("block").getAsString());
			
			if(obj.get("released_at")!=null)
				ed.setReleaseDate(obj.get("released_at").getAsString());
		
		return ed;
	}

}

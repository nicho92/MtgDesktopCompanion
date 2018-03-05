package org.magic.api.providers.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
import java.util.TreeMap;

import org.apache.commons.beanutils.BeanUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.magic.api.beans.Booster;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.MagicRuling;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;
import org.magic.services.MTGConstants;
import org.magic.tools.ColorParser;
import org.magic.tools.InstallCert;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class ScryFallProvider extends AbstractCardsProvider {

	private static String baseURI ="https://api.scryfall.com";
	private Map<String , MagicEdition> cache;
	private JsonParser parser;
	private Map<String,List<MagicCard>> cachedCardEds;
	private String encoding="UTF-8";
	private String version;
	
	public ScryFallProvider() {
		super();
	}
	
	@Override
	public void init() {
		cache=new TreeMap<>();
		cachedCardEds= new HashMap<>();
		parser = new JsonParser();
    	try {
    		InstallCert.install("api.scryfall.com");
    		System.setProperty("javax.net.ssl.trustStore",new File(MTGConstants.CONF_DIR,MTGConstants.KEYSTORE_NAME).getAbsolutePath());
    	} catch (Exception e1) {
			logger.error("Erreur installation certificat",e1);
		}

	}

	@Override
	public MagicCard getCardById(String id) throws IOException {
		return searchCardByCriteria("id", id, null,true).get(0);
	}

	@Override
	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition me,boolean exact) throws IOException {
		List<MagicCard> list = new ArrayList<>();
		
		String comparator=crit;
		
		if(exact)
			comparator="!\""+crit+"\"";
		
		
		String url = baseURI+"/cards/";
				if(att.equals("name"))
					url+="search?q="+URLEncoder.encode("++"+comparator +" include:extras",encoding);
				else if(att.equals("custom"))
					url+="search?q="+URLEncoder.encode(crit,encoding);
				else if(att.equals("set"))
					url+="search?q="+URLEncoder.encode("++e:"+crit,encoding);
				else if(att.equals("id"))
					url+=URLEncoder.encode(crit,encoding);
				else
					url+="search?q="+URLEncoder.encode(att+":"+comparator+" include:extras",encoding);
				
				if(me!=null)
					url+="%20" +URLEncoder.encode("e:"+me.getId(),encoding);
				
		HttpURLConnection con;
		JsonReader reader;
		boolean hasMore=true;
		while(hasMore)
		{
			
			logger.debug(URLDecoder.decode(url,encoding));
			con = (HttpURLConnection) getConnection(url);
			
			if(!isCorrectConnection(con))
				return list;
			
			try{
				reader= new JsonReader(new InputStreamReader(con.getInputStream(),encoding));
				JsonElement el = parser.parse(reader);
			
				if(att.equals("id"))
				{
					list.add(generateCard(el.getAsJsonObject(), exact, crit));
					hasMore=false;
				}
				else
				{
					JsonArray jsonList = el.getAsJsonObject().getAsJsonArray("data");
					for(int i=0;i<jsonList.size();i++)
					{
						MagicCard mc = generateCard(jsonList.get(i).getAsJsonObject(),exact,crit);
						list.add(mc);
					}
					hasMore=el.getAsJsonObject().get("has_more").getAsBoolean();
					
					if(hasMore)
						url=el.getAsJsonObject().get("next_page").getAsString();
					
					Thread.sleep(50);
				}
			}
			catch(Exception e)
			{
				logger.error("erreur",e);
				hasMore=false;
			}
		}
		return list;
	}
	
	private boolean isCorrectConnection(HttpURLConnection connection) throws IOException {
		return (connection.getResponseCode()>=200 && connection.getResponseCode()<300);
	}
	
	@Override
	public MagicCard getCardByNumber(String id, MagicEdition me) throws IOException {
		String url = baseURI+"/cards/"+me.getId()+"/"+id;
		URLConnection con = getConnection(url);
		JsonReader reader= new JsonReader(new InputStreamReader(con.getInputStream(),encoding));
		JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
		return generateCard(root,true,null);
	}

	@Override
	public List<MagicEdition> loadEditions() throws IOException {
		if(cache.size()<=0)
		{
			String url = baseURI+"/sets";
			URLConnection con = getConnection(url);
					
			JsonReader reader= new JsonReader(new InputStreamReader(con.getInputStream(),encoding));
			JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
			for(int i = 0;i<root.get("data").getAsJsonArray().size();i++)
			{
				
				JsonObject e = root.get("data").getAsJsonArray().get(i).getAsJsonObject();
				MagicEdition ed = generateEdition(e.getAsJsonObject());
				cache.put(ed.getId(), ed);
			}
		}
		return new ArrayList<>(cache.values());
	}

	@Override
	public MagicEdition getSetById(String id) throws IOException {
		if(cache.size()>0)
		{
			for(MagicEdition ed : cache.values())
				if(ed.getId().equalsIgnoreCase(id))
					try {
						return (MagicEdition)BeanUtils.cloneBean(ed);
					} catch (Exception e) {
						throw new IOException(e);
					}
		}
		try {
			JsonReader reader= new JsonReader(new InputStreamReader(getConnection(baseURI+"/sets/"+id.toLowerCase()).getInputStream(),encoding));
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
	public Booster generateBooster(MagicEdition me) throws IOException {
		
				List<MagicCard> ret = new ArrayList<>();
				List<MagicCard> common = new ArrayList<>();
				List<MagicCard> uncommon = new ArrayList<>();
				List<MagicCard> rare= new ArrayList<>();
			
				if(cachedCardEds.get(me.getId())==null)
					cachedCardEds.put(me.getId(), searchCardByCriteria("set", me.getId(), null,true));

				for(MagicCard mc : cachedCardEds.get(me.getId()))
				{	
					if(mc.getEditions().get(0).getRarity().equalsIgnoreCase("common"))
							common.add(mc);
					
					if(mc.getEditions().get(0).getRarity().equalsIgnoreCase("uncommon"))
							uncommon.add(mc);
					
					if(mc.getEditions().get(0).getRarity().toLowerCase().contains("rare"))
						rare.add(mc);
				
				}
				
				Collections.shuffle(common);		   
				Collections.shuffle(uncommon);
				Collections.shuffle(rare);
				
				ret.addAll(common.subList(0, 10));
				ret.addAll(uncommon.subList(0, 4));
				ret.add(rare.get(0));
				
				Booster b = new Booster();
						b.setEdition(me);
						b.setCards(ret);
				
				
		return b;
	}

	@Override
	public String getVersion() {
		 try {
			
			if(version==null) 
			{ 
				Document d = Jsoup.connect("https://scryfall.com/blog/category/api").timeout(0).get();
				String date=d.select("a.muted-n").first().text();
				String href=d.select("a.muted-n").first().attr("href");
				version=href.substring(href.lastIndexOf('-')+1,href.length())+" " + date;
			}
		} catch (IOException e) {
			logger.error(e);
			version="";
		}
		return version;
	}

	@Override
	public URL getWebSite() throws MalformedURLException {
		return new URL("https://scryfall.com/");
	}

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	@Override
	public String getName() {
		return "Scryfall";
	}
	
	private URLConnection getConnection(String url) throws IOException
	{
			logger.trace("get stream from " + url);
			URLConnection connection = new URL(url).openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			connection.connect();
			return connection;
	}
	
	private MagicCard generateCard(JsonObject obj,boolean exact,String search) throws IOException
	{
		final MagicCard mc = new MagicCard();
		
		  mc.setId(obj.get("id").getAsString());
		  mc.setName(obj.get("name").getAsString());
		  mc.setCmc(obj.get("cmc").getAsInt());
		  mc.setLayout(obj.get("layout").getAsString());
		  
		  try{mc.setMultiverseid(obj.get("multiverse_ids").getAsJsonArray().get(0).getAsInt());}catch(Exception e) { logger.error("could not find multiverse_ids " + mc.getName());}
		  try{mc.setText(obj.get("oracle_text").getAsString());}catch(NullPointerException e) { mc.setText(""); }
		  try{mc.setCost(obj.get("mana_cost").getAsString());}catch(NullPointerException e) { mc.setCost(""); }
		  try{mc.setFlavor(obj.get("flavor_text").getAsString());}catch(NullPointerException e) { mc.setFlavor(""); }
		  
		  
		 
		  if(obj.get("type_line")!=null)
			  generateTypes(mc,String.valueOf(obj.get("type_line")));
			  
		  MagicCardNames n = new MagicCardNames();
						  n.setLanguage("English");
						  n.setName(mc.getName());
						  try{n.setGathererId(obj.get("multiverse_id").getAsInt());}catch(NullPointerException e) {n.setGathererId(0);}
		  
		  mc.getForeignNames().add(n);
	
		  mc.setNumber(obj.get("collector_number").getAsString());
		  
		  try{mc.setArtist(obj.get("artist").getAsString());}catch(NullPointerException e) { logger.trace("artist not found");}
		  try{mc.setReserved(obj.get("reserved").getAsBoolean());}catch(NullPointerException e) { logger.trace("reserved not found");}
		  try{mc.setPower(obj.get("power").getAsString());}catch(NullPointerException e) { logger.trace("power not found");}
		  try{mc.setToughness(obj.get("toughness").getAsString());}catch(NullPointerException e) {logger.trace("toughness not found") ;}
		  try{mc.setLoyalty(obj.get("loyalty").getAsInt());}catch(Exception e) { logger.trace("loyalty not found");}
		  try{mc.setWatermarks(obj.get("watermark").getAsString());}catch(NullPointerException e) {logger.trace("watermark not found"); }
		  try{mc.setImageName(obj.get("illustration_id").getAsString());}catch(NullPointerException e) { logger.trace("illustration_id not found");}
		  
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
		   
		  mc.setTranformable(mc.getLayout().equalsIgnoreCase("transform")||mc.getLayout().equalsIgnoreCase("meld"));
		  mc.setFlippable(mc.getLayout().equals("flip")); 
		  int idface=0;
		  
		  if(mc.getName().contains("//"))
		  {
			  String[] names = mc.getName().split(" // ");
			  if(exact)
				  if(names[0].equals(search))
				  {
					  idface=0;
				  }
				  else
				  {
					  idface=1;
				  }
			  
		  }
		  if(obj.get("card_faces")!=null)
		  {
			  mc.setText(obj.get("card_faces").getAsJsonArray().get(idface).getAsJsonObject().get("oracle_text").getAsString());
			  mc.setCost(obj.get("card_faces").getAsJsonArray().get(idface).getAsJsonObject().get("mana_cost").getAsString());
			  mc.setRotatedCardName(obj.get("card_faces").getAsJsonArray().get(1).getAsJsonObject().get("name").getAsString());
			  mc.setImageName(obj.get("card_faces").getAsJsonArray().get(idface).getAsJsonObject().get("illustration_id").getAsString());
			  
			  generateTypes(mc, obj.get("card_faces").getAsJsonArray().get(idface).getAsJsonObject().get("type_line").getAsString());
			  try{mc.setMultiverseid(obj.get("multiverse_ids").getAsJsonArray().get(idface).getAsInt());}catch(Exception e) { logger.error(e);}
  			  try{mc.setLoyalty(obj.get("card_faces").getAsJsonArray().get(idface).getAsJsonObject().get("loyalty").getAsInt());}catch(Exception e){logger.error(e); }
  			
  			  Iterator<JsonElement> it = obj.get("card_faces").getAsJsonArray().get(idface).getAsJsonObject().get("colors").getAsJsonArray().iterator();
				while(it.hasNext())
					mc.getColors().add(ColorParser.getNameByCode(it.next().getAsString()));
  			 
  			  try{
				  mc.setPower(obj.get("card_faces").getAsJsonArray().get(idface).getAsJsonObject().get("power").getAsString());
				  mc.setToughness(obj.get("card_faces").getAsJsonArray().get(idface).getAsJsonObject().get("toughness").getAsString());
			 	 }
			  	catch(Exception e){
			  		logger.error(e);
			  		
			  	}
		  }
		 
		  
		  
		  //meld
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
		  }
		  
		  MagicEdition ed;
		try {
			ed = (MagicEdition)BeanUtils.cloneBean(getSetById(obj.get("set").getAsString()));
					  ed.setArtist(mc.getArtist());
					  if(mc.getMultiverseid()!=null)
						  ed.setMultiverse_id(String.valueOf(mc.getMultiverseid()));
					  
					  ed.setRarity(obj.get("rarity").getAsString());
					  ed.setOnlineOnly(obj.get("digital").getAsBoolean());
					  ed.setNumber(mc.getNumber());
		  mc.getEditions().add(ed);
		 
		} catch (Exception e1) {
			throw new IOException(e1);
		}

		  new Thread(()->{
				try {
					initOtherEdition(mc);
					//generateRules(mc);
				} catch (Exception e) {
					logger.error("error in initOtherEdition :" + e.getMessage());
				}
		},"other editions").start();
		    
		return mc;
		
	}

	private void generateRules(MagicCard mc) throws IOException
	{
		String url = "https://api.scryfall.com/cards/"+mc.getId()+"/rulings";
		HttpURLConnection con = (HttpURLConnection) getConnection(url);
		
		
		JsonElement el = parser.parse(new JsonReader(new InputStreamReader(con.getInputStream(),encoding)));
		JsonArray arr = el.getAsJsonObject().get("data").getAsJsonArray();
		
		for(int i=0; i<arr.size();i++)
		{
			JsonObject obr = arr.get(i).getAsJsonObject();
			MagicRuling rul = new MagicRuling();
						rul.setDate(obr.get("published_at").getAsString());
						rul.setText(obr.get("comment").getAsString());
			
			mc.getRulings().add(rul);
		}
	}
	
	private void generateTypes(MagicCard mc, String line) {

		line=line.replaceAll("\"", "");
		
		for(String k:new String[]{"Legendary","Basic","Ongoing","Snow","World"})
		{
			if(line.contains(k))
			{
				mc.getSupertypes().add(k);
				line=line.replaceAll(k,"").trim();
			}
		}
		
		String sep = "\u2014";

		if(line.contains(sep))
		{
		
			for(String s : line.substring(0, line.indexOf(sep)).trim().split(" "))
				mc.getTypes().add(s.replaceAll("\"", ""));
			
			for(String s : line.substring(line.indexOf(sep)+1).trim().split(" "))
				mc.getSubtypes().add(s);
		}
		else
		{
			for(String s : line.split(" "))
				mc.getTypes().add(s.replaceAll("\"", ""));
		}
	
		
		
	}

	private void initOtherEdition(MagicCard mc) throws IOException {
		
		String url=baseURI+"/cards/search?q=+"
				+ URLEncoder.encode("++!\""+mc.getName()+"\"",encoding)
				+ "%20include:extras"
				+ "%20-s:"+mc.getEditions().get(0).getId();

		
		logger.trace("initOtherEdition " + URLDecoder.decode(url,encoding));
		HttpURLConnection con;
		
		
		JsonReader reader;
		boolean hasMore=true;
		while(hasMore)
		{
			con = (HttpURLConnection) getConnection(url);
			
			try{
				reader= new JsonReader(new InputStreamReader(con.getInputStream(),encoding));
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
				ed.setOnlineOnly(obj.get("digital").getAsBoolean());
		
			if(obj.get("border")!=null)
				ed.setBorder(obj.get("border").getAsString());
		
			ed.setCardCount(obj.get("card_count").getAsInt());
			
			
			if(obj.get("block")!=null)
				ed.setBlock(obj.get("block").getAsString());
			
			if(obj.get("released_at")!=null)
				ed.setReleaseDate(obj.get("released_at").getAsString());
		
		return ed;
	}

	@Override
	public void initDefault() {
		// TODO Auto-generated method stub
		
	}

}

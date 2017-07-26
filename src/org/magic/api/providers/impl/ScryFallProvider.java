package org.magic.api.providers.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	
	
	@Override
	public void init() {
		cache=new HashMap<String,MagicEdition>();
    	try {
    		InstallCert.install("api.scryfall.com");
    		System.setProperty("javax.net.ssl.trustStore",new File(MTGControler.CONF_DIR,MTGControler.KEYSTORE_NAME).getAbsolutePath());
    	} catch (Exception e1) {
			logger.error(e1);
		}

	}

	@Override
	public MagicCard getCardById(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition me) throws Exception {
		List<MagicCard> list = new ArrayList<MagicCard>();
		JsonParser parser = new JsonParser();
		String url = baseURI+"/cards/search";
				if(att.equals("name"))
					url+="?q="+URLEncoder.encode("++"+crit,"UTF-8");
				else if(att.equals("custom"))
					url+="?q="+URLEncoder.encode(crit,"UTF-8");
				else if(att.equals("set"))
					url+="?q=s:"+URLEncoder.encode(crit,"UTF-8");
				else
					url+="?q="+URLEncoder.encode(att+":"+crit,"UTF-8");
				
				if(me!=null)
					url+="%20" +URLEncoder.encode("e:"+me.getId(),"UTF-8");
				
				url+="%20include:extras";
				
		URLConnection con;
		JsonReader reader;
		boolean hasMore=true;
		while(hasMore)
		{
			logger.debug(url);
			con = getConnection(url);
			reader= new JsonReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
			JsonElement el = parser.parse(reader);
			
			JsonArray jsonList = el.getAsJsonObject().getAsJsonArray("data");
			for(int i=0;i<jsonList.size();i++)
			{
				list.add(generateCard(jsonList.get(i).getAsJsonObject()));
			}
			hasMore=el.getAsJsonObject().get("has_more").getAsBoolean();
			
			if(hasMore)
				url=el.getAsJsonObject().get("next_page").getAsString();
			
			Thread.sleep(50);
		}
			
		
		
		
		return list;
	}

	@Override
	public MagicCard getCardByNumber(String id, MagicEdition me) throws Exception {
		// TODO Auto-generated method stub
		return null;
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
				if(ed.getId().equals(id))
					return ed;
		}
		
		JsonReader reader= new JsonReader(new InputStreamReader(getConnection(baseURI+"/sets/"+id).getInputStream(),"UTF-8"));
		JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
		return generateEdition(root.getAsJsonObject());
		
	}

	@Override
	public String[]  getLanguages() {
		return new String[]{"English"};
	}

	@Override
	public String[] getQueryableAttributs() {
		return new String[]{"name","type","color","oracle","mana","cmc","power","toughness","loyalty","is","rarity","cube","artist","flavor","watermark","border","frame","set","custom"};
	}

	@Override
	public List<MagicCard> openBooster(MagicEdition me) throws Exception {
		// TODO Auto-generated method stub
		return null;
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
		return STATUT.DEV;
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
			e.printStackTrace();
			return null;
		}	
	}
	
	private MagicCard generateCard(JsonObject obj) throws Exception
	{
		MagicCard mc = new MagicCard();
		
		  mc.setId(obj.get("id").getAsString());
		  
		  try{mc.setMultiverseid(obj.get("multiverse_id").getAsInt());}catch(NullPointerException e) { };
		  try{mc.setText(obj.get("oracle_text").getAsString());}catch(NullPointerException e) { mc.setText(""); };
			
		  mc.setName(obj.get("name").getAsString());
		  mc.setCmc(obj.get("cmc").getAsInt());
		  mc.setCost(obj.get("mana_cost").getAsString());
		  mc.setLayout(obj.get("layout").getAsString());
		  
		  MagicCardNames n = new MagicCardNames();
						  n.setLanguage("English");
						  n.setName(mc.getName());
						  try{n.setGathererId(mc.getMultiverseid());}catch(NullPointerException e) { };
		  
		  mc.getForeignNames().add(n);
		  mc.getTypes().add(obj.get("type_line").getAsString());
		  
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
					  ed.setMultiverse_id(String.valueOf(mc.getMultiverseid()));
					  ed.setRarity(obj.get("rarity").getAsString());
					  ed.setOnlineOnly(obj.get("digital").getAsBoolean());
					  ed.setNumber(mc.getNumber());
		  mc.getEditions().add(ed);
		  
		return mc;
		
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

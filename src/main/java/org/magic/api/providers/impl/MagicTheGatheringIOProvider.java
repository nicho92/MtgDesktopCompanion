package org.magic.api.providers.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.Booster;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.MagicRuling;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class MagicTheGatheringIOProvider extends AbstractCardsProvider{

	private String jsonUrl ="https://api.magicthegathering.io/v1";
	private File fcacheCount = new File(confdir,"mtgio.cache"); 
	private Properties propsCache;
	private Map<String , MagicEdition> cache;
	private String encoding="UTF-8";
	
	
	public MagicTheGatheringIOProvider() {
		super();
		init();
		
		
	}
	
	@Override
	public void init() {

		cache=new HashMap<>();
		
		propsCache = new Properties();
		try {
			propsCache.load(new FileReader(fcacheCount));
		} catch (FileNotFoundException e) {
			try {
				FileUtils.touch(fcacheCount);
			} catch (IOException e1) {
				logger.error("couldn't create "+fcacheCount,e1);
				
			}
		} catch (IOException e) {
			logger.error(e);
			
		} 
	}

	@Override
	public MagicCard getCardById(String id) throws Exception {
		return searchCardByCriteria("id", id,null,true).get(0);
	}

	
	@Override
	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition me,boolean exact) throws Exception {
		List<MagicCard> lists= new ArrayList<>();
		URLConnection con =null;
		int page=1;
		String url = jsonUrl+"/cards?"+att+"="+URLEncoder.encode(crit,encoding);
		logger.debug(url);
		
		con = getConnection(url);
		JsonReader reader;
		
		int count = 0;
		int totalcount= con.getHeaderFieldInt("Total-Count", 0);
	
		while(count<totalcount)
		{
			url = jsonUrl+"/cards?"+att+"="+URLEncoder.encode(crit,encoding)+"&page="+page++;
			logger.debug(url);
			con = getConnection(url);
			reader= new JsonReader(new InputStreamReader(con.getInputStream(),encoding));
			JsonArray jsonList = new JsonParser().parse(reader).getAsJsonObject().getAsJsonArray("cards");
			for(int i=0;i<jsonList.size();i++)
			{
				lists.add(generateCard(jsonList.get(i).getAsJsonObject()));
			}
			count += con.getHeaderFieldInt("Count", 0);
		}
		return lists;
	}

	@Override
	public MagicCard getCardByNumber(String id, MagicEdition me) throws Exception {
		return searchCardByCriteria("number", id,me,true).get(0);
	}
	
	private MagicCard generateCard(JsonObject obj) throws Exception
	{
		MagicCard mc = new MagicCard();
			
			if(obj.get("name")!=null)
				mc.setName(obj.get("name").getAsString());
			
			if(obj.get("manaCost")!=null)
				mc.setCost(obj.get("manaCost").getAsString());
			
			if(obj.get("text")!=null)
				mc.setText(obj.get("text").getAsString());
			
			if(obj.get("originalText")!=null)
				mc.setOriginalText(obj.get("originalText").getAsString());
			
			if(obj.get("id")!=null)
				mc.setId(obj.get("id").getAsString());
			
			if(obj.get("artist")!=null)
				mc.setArtist(obj.get("artist").getAsString());
			
			if(obj.get("cmc")!=null)
				mc.setCmc(obj.get("cmc").getAsInt());
			
			if(obj.get("layout")!=null)
				mc.setLayout(obj.get("layout").getAsString());

			if(obj.get("number")!=null)
				mc.setNumber(obj.get("number").getAsString());
			
			if(obj.get("power")!=null)
				mc.setPower(obj.get("power").getAsString());
			
			if(obj.get("toughness")!=null)
				mc.setToughness(obj.get("toughness").getAsString());
			
			if(obj.get("loyalty")!=null)
				mc.setLoyalty(obj.get("loyalty").getAsInt());
	
			if(obj.get("mciNumber")!=null)
		 		 mc.setMciNumber(String.valueOf(obj.get("mciNumber")));
		 
			
			if(obj.get("colors")!=null){
				Iterator<JsonElement> it = obj.get("colors").getAsJsonArray().iterator();
				while(it.hasNext())
					mc.getColors().add(it.next().getAsString());
			}
			
			if(obj.get("types")!=null){
				Iterator<JsonElement> it = obj.get("types").getAsJsonArray().iterator();
				while(it.hasNext())
					mc.getTypes().add(it.next().getAsString());
			}
			
			if(obj.get("supertypes")!=null){
				Iterator<JsonElement> it = obj.get("supertypes").getAsJsonArray().iterator();
				while(it.hasNext())
					mc.getSupertypes().add(it.next().getAsString());
			}
			
			if(obj.get("subtypes")!=null){
				Iterator<JsonElement> it = obj.get("subtypes").getAsJsonArray().iterator();
				while(it.hasNext())
					mc.getSubtypes().add(it.next().getAsString());
			}
			
		
			if(obj.get("legalities")!=null){
				JsonArray arr = obj.get("legalities").getAsJsonArray();
				for(int i=0;i<arr.size();i++)
				{
					JsonObject k = arr.get(i).getAsJsonObject();
						MagicFormat format = new MagicFormat();
						format.setFormat(k.get("format").getAsString());
						format.setLegality(k.get("legality").getAsString());
						mc.getLegalities().add(format);
				}
			}
			
			if(obj.get("rulings")!=null){
				JsonArray arr = obj.get("rulings").getAsJsonArray();
				for(int i=0;i<arr.size();i++)
				{
					JsonObject k = arr.get(i).getAsJsonObject();
						MagicRuling rule = new MagicRuling();
						rule.setDate(k.get("date").getAsString());
						rule.setText(k.get("text").getAsString());
						mc.getRulings().add(rule);
				}
			}
			
			
			if(obj.get("names")!=null)
			{
				 JsonArray arr = obj.get("names").getAsJsonArray();
				 
				 List<String> list = new ArrayList<>();
				 for (int i = 0; i < arr.size();list.add(arr.get(i++).getAsString())) {
					 //TODO complete this function
				 }
				 
				 list.remove(mc.getName());
				 
				 String rotateName=(list.get(list.size()-1)) ;
	 			 mc.setRotatedCardName(rotateName);
	 			 
	 			 if(mc.getLayout().equals("flip"))
	 				 mc.setFlippable(true);
	 			 if(mc.getLayout().equals("double-faced") || mc.getLayout().equals("meld") )
	 				 mc.setTranformable(true);
			}
			
			
			
			
			
			
			String currentSet = obj.get("set").getAsString(); 
			MagicEdition currentEd = getSetById(currentSet);
			
			if(obj.get("multiverseid")!=null)
				currentEd.setMultiverse_id(obj.get("multiverseid").getAsString());
			
			if(obj.get("rarity")!=null)
				currentEd.setRarity(obj.get("rarity").getAsString());
			
			currentEd.setNumber(mc.getNumber());
			
			
			mc.getEditions().add(0,currentEd);
			
			if(obj.get("printings")!=null){
				JsonArray arr = obj.get("printings").getAsJsonArray();
				for(int i=0;i<arr.size();i++)
				{
						String k = arr.get(i).getAsString();
						if(!k.equals(currentSet))
						{
							MagicEdition ed = getSetById(k);
							initOtherEdVariable(mc,ed);
							mc.getEditions().add(ed);
						}
				}
			}
			
				MagicCardNames defaultMcn = new MagicCardNames();
				defaultMcn.setName(mc.getName());
				defaultMcn.setLanguage("English");
				try{
					defaultMcn.setGathererId(Integer.parseInt(currentEd.getMultiverse_id()));	
				}
				catch(Exception e)
				{
					defaultMcn.setGathererId(0);
				}
				
				
			mc.getForeignNames().add(defaultMcn);	
			
			if(obj.get("foreignNames")!=null){
				JsonArray arr = obj.get("foreignNames").getAsJsonArray();
				for(int i=0;i<arr.size();i++)
				{
					JsonObject lang = arr.get(i).getAsJsonObject();
					MagicCardNames mcn = new MagicCardNames();
								mcn.setName(lang.get("name").getAsString());
								mcn.setLanguage(lang.get("language").getAsString());
								
								if(lang.get("multiverseid")!=null)
									mcn.setGathererId(lang.get("multiverseid").getAsInt());
								
								mc.getForeignNames().add(mcn);
				}
			}
		return mc;
	}
	
	private void initOtherEdVariable(MagicCard mc, MagicEdition ed)
	{
		JsonReader reader;
		JsonObject root = null;
		JsonObject temp=null;
		try {
			reader = new JsonReader(new InputStreamReader(getConnection(jsonUrl+"/cards?set="+ed.getId()+"&name="+URLEncoder.encode(mc.getName(),encoding)).getInputStream(),encoding));
			root = new JsonParser().parse(reader).getAsJsonObject();
			
			temp = root.get("cards").getAsJsonArray().get(0).getAsJsonObject();
			
			if(temp.get("rarity")!=null)
				ed.setRarity(temp.get("rarity").getAsString());
			if(temp.get("multiverseid")!=null)
				ed.setMultiverse_id(temp.get("multiverseid").getAsString());
			if(temp.get("number")!=null)
				ed.setNumber(temp.get("number").getAsString());
			
		} catch (Exception e) {
			logger.error("ERROR on " + ed.getId() +" " + mc.getName()  + ": " + e);
		} 
		
	}
	
	private MagicEdition generateEdition(JsonObject obj) throws IOException
	{
		MagicEdition ed = new MagicEdition();
			ed.setId(obj.get("code").getAsString());
			ed.setSet(obj.get("name").getAsString());
			ed.setType(obj.get("type").getAsString());
			ed.setBorder(obj.get("border").getAsString());
			ed.setReleaseDate(obj.get("releaseDate").getAsString());
		
			if(obj.get("mkm_id")!=null){
				ed.setMkm_id(obj.get("mkm_id").getAsInt());
				ed.setMkm_name(obj.get("mkm_name").getAsString());
			}
			
			if(obj.get("magicCardsInfoCode")!=null)
				ed.setMagicCardsInfoCode(obj.get("magicCardsInfoCode").getAsString());
			
			/*
			if(obj.get("booster")!=null)
			{
				JsonArray arr = obj.get("booster").getAsJsonArray();
				for(int i = 0;i<arr.size();i++)
				{
					ed.getBooster().add(arr.get(i));//TODO correct JsonArray
				}
			}*/
			
			if(propsCache.getProperty(ed.getId())!=null)
				ed.setCardCount(Integer.parseInt(propsCache.getProperty(ed.getId())));
			else
				ed.setCardCount(getCount(ed.getId()));
			
		return ed;
	}
	
	private int getCount(String id) throws IOException 
	{
		int count = getConnection(jsonUrl+"/cards?set="+id).getHeaderFieldInt("Total-Count", 0);
		propsCache.put(id, String.valueOf(count));
		try {
			logger.info("update cache " + id );
			
			try(FileOutputStream fos = new FileOutputStream(fcacheCount))
			{
				propsCache.store(fos, new Date().toString());
			}
			
		} catch (Exception e) {
			logger.error("error in count for "+id,e);
		}
		return count;
	}

	private URLConnection getConnection(String url) throws IOException
	{
			logger.debug("get stream from " + url);
			URLConnection connection = new URL(url).openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			connection.connect();
			return connection;
		
	}

	@Override
	public List<MagicEdition> loadEditions() throws Exception {
		if(cache.size()==0)
		{
				String url = jsonUrl+"/sets";
				String rootKey="sets";
		
				logger.info("connect to " + url);
			
			URLConnection con = getConnection(url);
			
			JsonReader reader= new JsonReader(new InputStreamReader(con.getInputStream(),encoding));
			JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
			for(int i = 0;i<root.get(rootKey).getAsJsonArray().size();i++)
			{
				JsonObject e = root.get(rootKey).getAsJsonArray().get(i).getAsJsonObject();
				MagicEdition ed = generateEdition(e.getAsJsonObject());
				cache.put(ed.getId(), ed);
			}
		}
		return new ArrayList<>(cache.values());
	}

	@Override
	public MagicEdition getSetById(String id) throws Exception {
		logger.debug("get Set " + id);
		
		if(cache.get(id)!=null)
			return cache.get(id);
			
		JsonReader reader= new JsonReader(new InputStreamReader(getConnection(jsonUrl+"/sets/"+id).getInputStream(),encoding));
		JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
		return generateEdition(root.getAsJsonObject("set"));
	}


	public String[]  getLanguages() {
		return new String[]{"English","Chinese Simplified","Chinese Traditional","French","German","Italian","Japanese","Korean","Portugese","Russian","Spanish"};
	}

	@Override
	public String[] getQueryableAttributs() {
		return new String[]{"name","foreignNames","text","artist","type","rarity","flavor","cmc","set","watermark","power","toughness","layout"};
	}

	@Override
	public Booster generateBooster(MagicEdition me) throws Exception {
		return null;
	}

	@Override
	public String getVersion() {
		return "v1";
	}

	@Override
	public URL getWebSite() throws MalformedURLException {
		return new URL("http://magicthegathering.io/");
	}

	public String getName() {
		return "MTG Developpers.io";
	}
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

}

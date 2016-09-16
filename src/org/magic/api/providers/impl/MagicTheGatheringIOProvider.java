package org.magic.api.providers.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.MagicRuling;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.services.MagicFactory;
import org.magic.tools.EditionCardCount;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

public class MagicTheGatheringIOProvider implements MagicCardsProvider{

	static final Logger logger = LogManager.getLogger(MagicTheGatheringIOProvider.class.getName());

	private boolean enable;
	private String jsonUrl ="https://api.magicthegathering.io/v1";
	private File fcacheCount = new File(MagicFactory.CONF_DIR,"mtgio.cache"); 
	
	Properties propsCache;
	
	Map<String , MagicEdition> cache;
	

	
	
	public MagicTheGatheringIOProvider() {
		init();
		
		
	}
	
	@Override
	public void init() {
		Configuration.setDefaults(new Configuration.Defaults() {

		    private final JsonProvider jsonProvider = new GsonJsonProvider();
		    private final MappingProvider mappingProvider = new GsonMappingProvider();

		    
		    @Override
		    public JsonProvider jsonProvider() {
		        return jsonProvider;
		    }

		    @Override
		    public MappingProvider mappingProvider() {
		        return mappingProvider;
		    }

		    @Override
		    public Set<Option> options() {
		        return EnumSet.noneOf(Option.class);
		    }
		    
		});
		Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
		
		cache=new HashMap<String,MagicEdition>();
		
		propsCache = new Properties();
		try {
			propsCache.load(new FileReader(fcacheCount));
		} catch (FileNotFoundException e) {
			try {
				fcacheCount.createNewFile();
			} catch (IOException e1) {
				
			}
		} catch (IOException e) {
			
		} 
	}

	@Override
	public MagicCard getCardById(String id) throws Exception {
		return searchCardByCriteria("id", id,null).get(0);
	}

	
	public static void main(String[] args) throws Exception {
		MagicTheGatheringIOProvider prov = new MagicTheGatheringIOProvider();
		
		prov.searchCardByCriteria("name", "emrak", null);
	}
	
	
	@Override
	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition me) throws Exception {
		List<MagicCard> lists= new ArrayList<MagicCard>();
		URLConnection con =null;
		int page=1;
		String url = jsonUrl+"/cards?"+att+"="+URLEncoder.encode(crit,"UTF-8");
		con = getConnection(url);
		JsonReader reader= new JsonReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
		
		int count = 0;
		int totalcount= con.getHeaderFieldInt("Total-Count", 0);
	
		while(count<totalcount)
		{
			url = jsonUrl+"/cards?"+att+"="+URLEncoder.encode(crit,"UTF-8")+"&page="+page++;
			con = getConnection(url);
			reader= new JsonReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
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
		return searchCardByCriteria("number", id,me).get(0);
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
			
			if(obj.get("rarity")!=null)
				mc.setRarity(obj.get("rarity").getAsString());
			
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
				 
				 List<String> list = new ArrayList<String>();
				 for (int i = 0; i < arr.size();list.add(arr.get(i++).getAsString()));
				 
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
			
			currentEd.setRarity(mc.getRarity());
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
		try {
			reader = new JsonReader(new InputStreamReader(getConnection(jsonUrl+"/cards?set="+ed.getId()+"&name="+URLEncoder.encode(mc.getName(),"UTF-8")).getInputStream(),"UTF-8"));
			JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
			
			JsonObject temp = root.get("cards").getAsJsonArray().get(0).getAsJsonObject();
			
			if(temp.get("rarity")!=null)
				ed.setRarity(temp.get("rarity").getAsString());
			if(temp.get("multiverseid")!=null)
				ed.setMultiverse_id(temp.get("multiverseid").getAsString());
			if(temp.get("number")!=null)
				ed.setNumber(temp.get("number").getAsString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
	
	
	private MagicEdition generateEdition(JsonObject obj)
	{
		MagicEdition ed = new MagicEdition();
			ed.setId(obj.get("code").getAsString());
			ed.setSet(obj.get("name").getAsString());
			ed.setType(obj.get("type").getAsString());
			ed.setBorder(obj.get("border").getAsString());
			ed.setReleaseDate(obj.get("releaseDate").getAsString());
			
			if(obj.get("magicCardsInfoCode")!=null)
				ed.setMagicCardsInfoCode(obj.get("magicCardsInfoCode").getAsString());
			
			
			if(obj.get("booster")!=null)
			{
				JsonArray arr = obj.get("booster").getAsJsonArray();
				for(int i = 0;i<arr.size();i++)
				{
					ed.getBooster().add(arr.get(i));
				}
			}
			
			if(propsCache.getProperty(ed.getId())!=null)
				ed.setCardCount(Integer.parseInt(propsCache.getProperty(ed.getId())));
			else
				ed.setCardCount(getCount(ed.getId()));
			
		return ed;
	}
	
	
	private int getCount(String id) 
	{
		int count = getConnection(jsonUrl+"/cards?set="+id).getHeaderFieldInt("Total-Count", 0);
		propsCache.put(id, String.valueOf(count));
		try {
			logger.info("update cache " + id );
			propsCache.store(new FileOutputStream(fcacheCount), new Date().toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
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

	@Override
	public List<MagicEdition> searchSetByCriteria(String att, String crit) throws Exception {
		String url = jsonUrl+"/sets";
		String rootKey="sets";
		
		if(crit!=null)
		{
			
			if(att.equals("set"))
			{
				url = jsonUrl+"/sets/"+crit;
				rootKey="set";
			}
			else
			{
				url = jsonUrl+"/sets?"+att+"="+crit;
				rootKey="sets";
			}
			
			
		}
		logger.info("connect to " + url);
		
		URLConnection con = getConnection(url);
		
		JsonReader reader= new JsonReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
		JsonObject root = new JsonParser().parse(reader).getAsJsonObject();

		if(cache.size()==0)
			for(int i = 0;i<root.get(rootKey).getAsJsonArray().size();i++)
			{
				JsonObject e = root.get(rootKey).getAsJsonArray().get(i).getAsJsonObject();
				MagicEdition ed = generateEdition(e.getAsJsonObject());
				
				cache.put(ed.getId(), ed);
			}
		return new ArrayList<MagicEdition>(cache.values());
	}

	@Override
	public MagicEdition getSetById(String id) throws Exception {
		logger.debug("get Set " + id);
		if(cache.get(id.toString())!=null)
		{
			logger.debug("loadgin " + id + " from cache");
			return cache.get(id.toString());
		}
		
		JsonReader reader= new JsonReader(new InputStreamReader(getConnection(jsonUrl+"/sets/"+id).getInputStream(),"UTF-8"));
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
	public List<MagicCard> openBooster(MagicEdition me) throws Exception {
		// TODO Auto-generated method stub
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

	@Override
	public void enable(boolean enabled) {
		this.enable=enabled;
		
	}

	@Override
	public boolean isEnable() {
		return enable;
	}
	
	public String toString() {
		return "MTG Developpers.io";
	}
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

}

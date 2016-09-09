package org.magic.api.providers.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicCardsProvider;

import com.google.gson.JsonArray;
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
	
	List<MagicEdition> list;

	
	
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
		
		list=new ArrayList<MagicEdition>();
	}

	@Override
	public MagicCard getCardById(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition me) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MagicCard getCardByNumber(String id, MagicEdition me) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private MagicEdition generateEdition(JsonObject obj)
	{
		MagicEdition ed = new MagicEdition();
			ed.setId(obj.get("code").getAsString());
			ed.setSet(obj.get("name").getAsString());
			ed.setType(obj.get("type").getAsString());
			ed.setBorder(obj.get("border").getAsString());
			ed.setReleaseDate(obj.get("releaseDate").getAsString());
			
			List<String> list = new ArrayList<String>();
			if(obj.get("booster")!=null)
			{
				JsonArray arr = obj.get("booster").getAsJsonArray();
				for(int i = 0;i<arr.size();i++)
				{
					ed.getBooster().add(arr.get(i));
				}
			}
			
		return ed;
	}
	
	
	private InputStream getStream(String url)
	{
		try {
			URLConnection connection = new URL(url).openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			connection.connect();
			return connection.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}	
		
		
	}

	@Override
	public List<MagicEdition> searchSetByCriteria(String att, String crit) throws Exception {
		String url = jsonUrl+"/sets";
		
		if(crit!=null)
		{
			url = jsonUrl+"/sets?"+att+"="+crit;
			
			if(crit.equals("set"))
				url = jsonUrl+"/sets/"+crit;
		}
		
		JsonReader reader= new JsonReader(new InputStreamReader(getStream(url)));
		JsonObject root = new JsonParser().parse(reader).getAsJsonObject();

		if(list.size()==0)
			for(int i = 0;i<root.get("sets").getAsJsonArray().size();i++)
			{
				JsonObject e = root.get("sets").getAsJsonArray().get(i).getAsJsonObject();
				MagicEdition ed = generateEdition(e.getAsJsonObject());
				list.add(ed);
			}
		
		return list;
	}

	@Override
	public MagicEdition getSetById(String id) throws Exception {
		
		JsonReader reader= new JsonReader(new InputStreamReader(getStream(jsonUrl+"/sets/"+id)));
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
		return STATUT.DEV;
	}

}

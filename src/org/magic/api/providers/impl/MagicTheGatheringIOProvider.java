package org.magic.api.providers.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicCardsProvider;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

public class MagicTheGatheringIOProvider implements MagicCardsProvider{

	static final Logger logger = LogManager.getLogger(MagicTheGatheringIOProvider.class.getName());

	private boolean enable;

	private String url ="https://api.magicthegathering.io/v1/";
	
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

	@Override
	public List<MagicEdition> searchSetByCriteria(String att, String crit) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MagicEdition getSetById(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
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
		return "MTG Developpers.io  (dev)";
	}

}

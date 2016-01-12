package org.magic.api.pricers.impl;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MagicPricesProvider;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class ChannelFireballPricer implements MagicPricesProvider {

	
	String setvar="&setname=";
	static final Logger logger = LogManager.getLogger(ChannelFireballPricer.class.getName());

	Properties props;

	
	public ChannelFireballPricer() {
		props = new Properties();
		
		props.put("MAX", 5);
		props.put("URL", "http://magictcgprices.appspot.com/api/cfb/price.json?cardname=%CARDNAME%");
		props.put("WEBSITE", "http://store.channelfireball.com/");
		props.put("ENCODING", "UTF-8");
		props.put("KEYWORD", "");

	}

	@Override
	public List<MagicPrice> getPrice(MagicEdition me, MagicCard card) throws Exception {
	
		
		String KEYWORD=card.getName();
		String url = props.getProperty("URL").toString();
		
		
		KEYWORD=URLEncoder.encode(KEYWORD,props.getProperty("ENCODING"));
		
		props.put("KEYWORD", KEYWORD);
		
		if(me!=null)
			KEYWORD += "&setname=" + URLEncoder.encode(me.getSet(),props.getProperty("ENCODING"));
		
		
		String link=url.replaceAll("%CARDNAME%", KEYWORD);
		
		
		logger.debug(getName()+ " Looking for price " + link);
		JsonReader reader = new JsonReader(new InputStreamReader(new URL(link).openStream(), props.getProperty("ENCODING")));
		JsonElement root = new JsonParser().parse(reader);
		
		String value = root.getAsJsonArray().get(0).getAsString();
		
		MagicPrice mp = new MagicPrice();
			mp.setUrl("http://store.channelfireball.com/products/search?query="+URLEncoder.encode(card.getName(),props.getProperty("ENCODING")));
			mp.setSite(getName());
			mp.setCurrency(value.substring(0, 1));
			mp.setValue(Double.parseDouble(value.substring(1)));
			
			
		ArrayList<MagicPrice> list = new ArrayList<MagicPrice>();
							list.add(mp);
		return list;
	}

	@Override
	public String getName() {
		return "Channel Fireball";
	}
	
	public String toString()
	{
		return getName();
	}

	@Override
	public Properties getProperties() {
		return props;
	}

	@Override
	public void setProperties(String k, Object value) {
		props.put(k, value);
		
	}

	
}

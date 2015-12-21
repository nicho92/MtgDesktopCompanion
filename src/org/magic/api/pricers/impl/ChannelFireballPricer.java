package org.magic.api.pricers.impl;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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

	
	String url= "http://magictcgprices.appspot.com/api/cfb/price.json?cardname=%CARDNAME%";
	String setvar="&setname=";
	static final Logger logger = LogManager.getLogger(ChannelFireballPricer.class.getName());

	
	public static void main(String[] args) throws Exception {
		MagicCard mc = new MagicCard();
		mc.setName("tarmogoyf");
		new ChannelFireballPricer().getPrice(null, mc);
		
	}
	
	@Override
	public List<MagicPrice> getPrice(MagicEdition me, MagicCard card) throws Exception {
	
		
		String KEYWORD=card.getName();
		
		KEYWORD=URLEncoder.encode(KEYWORD,"UTF-8");
		
		if(me!=null)
			KEYWORD += "&setname=" + URLEncoder.encode(me.getSet(),"UTF-8");
		
		
		String link=url.replaceAll("%CARDNAME%", KEYWORD);
		
		
		logger.debug(getName()+ " Looking for price " + link);
		JsonReader reader = new JsonReader(new InputStreamReader(new URL(link).openStream(), "UTF-8"));
		JsonElement root = new JsonParser().parse(reader);
		
		String value = root.getAsJsonArray().get(0).getAsString();
		
		MagicPrice mp = new MagicPrice();
			mp.setUrl("http://store.channelfireball.com/products/search?query="+card.getName());
			mp.setSite(getName());
			mp.setCurrency(value.substring(0, 1));
			mp.setValue(Double.parseDouble(value.substring(1)));
			
			
		ArrayList<MagicPrice> list = new ArrayList<MagicPrice>();
							list.add(mp);
		return list;
	}
	@Override
	public void setMaxResults(int max) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getName() {
		return "Channel Fireball";
	}
}

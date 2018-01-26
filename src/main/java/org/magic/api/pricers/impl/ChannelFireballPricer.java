package org.magic.api.pricers.impl;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class ChannelFireballPricer extends AbstractMagicPricesProvider {

	
	private String setvar="&setname=";
	
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}
	
	
	public ChannelFireballPricer() {
		super();
		
		if(!new File(confdir, getName()+".conf").exists()){
		props.put("MAX", "5");
		props.put("URL", "http://magictcgprices.appspot.com/api/cfb/price.json?cardname=%CARDNAME%");
		props.put("WEBSITE", "http://store.channelfireball.com/");
		props.put("ENCODING", "UTF-8");
		props.put("KEYWORD", "");
		save();
		}

	}

	@Override
	public List<MagicPrice> getPrice(MagicEdition me, MagicCard card) throws Exception {
	
		
		String keyword=card.getName();
		String url = getProperty("URL");
		
		
		keyword=URLEncoder.encode(keyword,props.getProperty("ENCODING"));
		
		props.put("KEYWORD", keyword);
		
		if(me!=null)
			keyword += "&setname=" + URLEncoder.encode(me.getSet(),props.getProperty("ENCODING"));
		
		
		String link=url.replaceAll("%CARDNAME%", keyword);
		
		
		logger.info(getName()+ " Looking for price " + link);
		JsonReader reader = new JsonReader(new InputStreamReader(new URL(link).openStream(), props.getProperty("ENCODING")));
		JsonElement root = new JsonParser().parse(reader);
		
		String value = root.getAsJsonArray().get(0).getAsString();
		
		MagicPrice mp = new MagicPrice();
			mp.setUrl("http://store.channelfireball.com/products/search?query="+URLEncoder.encode(card.getName(),props.getProperty("ENCODING")));
			mp.setSite(getName());
			mp.setCurrency(value.substring(0, 1));
			mp.setValue(Double.parseDouble(value.substring(1).replaceAll(",", "")));
			
			
		ArrayList<MagicPrice> list = new ArrayList<MagicPrice>();
							list.add(mp);
							
							
		logger.info(getName() +" found " + list.size() +" item(s)" );
							
		return list;
	}

	@Override
	public String getName() {
		return "Channel Fireball";
	}

	@Override
	public void alertDetected(List<MagicPrice> p) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}

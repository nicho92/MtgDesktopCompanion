package org.magic.api.pricers.impl;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class MTGPricePricer extends AbstractMagicPricesProvider {

	static final Logger logger = LogManager.getLogger(MTGPricePricer.class.getName());

	
	public MTGPricePricer() {
	super();
		
		if(!new File(confdir, getName()+".conf").exists()){
		props.put("MAX", "5");
		props.put("WS_URL", "http://www.mtgprice.com/api");
		props.put("API_KEY", "");
		props.put("WEBSITE", "http://www.mtgprice.com/");
		save();
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		MagicEdition ed = new MagicEdition();
		MagicCard mc = new MagicCard();
		mc.setName("Wharf Infiltrator");
		ed.setSet("Eldritch Moon");
		
		new MTGPricePricer().getPrice(ed, mc);
	}
	
	
	@Override
	public List<MagicPrice> getPrice(MagicEdition me, MagicCard card) throws Exception {
		// http://www.mtgprice.com/api?apiKey=DavidDOTCatuheATmicrosoftDOTcom-SunMay1018-30-46UTC2015&s=Eldritch_Moon
		
		String set=me.getSet().replaceAll(" ", "_");
		
		String url = props.getProperty("WS_URL")+"?apiKey="+props.getProperty("API_KEY")+"&s="+set;
		InputStream stream = new URL(url).openConnection().getInputStream();
		List<MagicPrice> ret = new ArrayList<MagicPrice>();
		
		logger.debug(getName() +" looking for price at " + url);
		
		try{
			JsonReader reader = new JsonReader(new InputStreamReader(stream)) ;
			reader.setLenient(true);
			reader.beginObject();
			reader.nextName();
			reader.beginArray();
		
		String name="", mtgpriceID="", fairPrice="";
		
	     while (reader.hasNext()) {
	       reader.beginObject();
	       reader.nextName();
	       mtgpriceID=(reader.nextString());
	       reader.nextName();
	       name=(reader.nextString());
	       reader.nextName();
	       fairPrice=(reader.nextString());
	       reader.endObject();
	       
	       if(name.equalsIgnoreCase(card.getName()))
	       {
	    	   MagicPrice price = new MagicPrice();
	       		price.setCurrency("$");
	       		price.setSeller("MTGPrice");
	       		price.setUrl(props.getProperty("WEBSITE")+"/sets/"+set+"/"+mtgpriceID.substring(0, mtgpriceID.indexOf(set)));
	       		price.setValue(Double.parseDouble(fairPrice.replaceAll("\\$", "")));
	       		price.setQuality("NM");
	       		price.setFoil(mtgpriceID.substring(mtgpriceID.indexOf(set)+set.length()).startsWith("true"));
	       		price.setSite(getName());
	       		ret.add(price);
	       		reader.close();
	       		return ret;
	       }
	     }
	     
		}
		catch(Exception e)
		{
			return ret;	
		}
		return ret;
		
	}

	@Override
	public String getName() {
		return "MTGPrice";
	}

}

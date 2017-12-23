package org.magic.api.pricers.impl;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;

import com.google.gson.stream.JsonReader;

public class MTGPricePricer extends AbstractMagicPricesProvider {
	
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}
	
	
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
	
	
	@Override
	public List<MagicPrice> getPrice(MagicEdition me, MagicCard card) throws Exception {
		// http://www.mtgprice.com/api?apiKey=DavidDOTCatuheATmicrosoftDOTcom-SunMay1018-30-46UTC2015&s=Eldritch_Moon
		if(props.getProperty("API_KEY").equals(""))
			throw new Exception ("API_KEY must be filled");
		
		String set=me.getSet().replaceAll(" ", "_");
		
		String url = props.getProperty("WS_URL")+"?apiKey="+props.getProperty("API_KEY")+"&s="+set;
		InputStream stream = new URL(url).openConnection().getInputStream();
		List<MagicPrice> ret = new ArrayList<MagicPrice>();
		
		logger.info(getName() +" looking for price at " + url);
		
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
	       		logger.info(getName() +" found " + ret.size() +" items");
	    		
	       		return ret;
	       }
	     }
	     reader.close();
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

	@Override
	public void alertDetected(List<MagicPrice> p) {
		// TODO Auto-generated method stub
		
	}

}

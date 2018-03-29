package org.magic.api.dashboard.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.CardDominance;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;
import org.magic.services.MTGConstants;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class MTGStockDashBoard extends AbstractDashBoard {
	private boolean connected;
	private Map<String,Integer> correspondance;
	private JsonParser parser;
	
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	public static void main(String[] args) throws IOException {
		MTGStockDashBoard dash = new MTGStockDashBoard();
		MagicCard mc = new MagicCard();
		mc.setName("Jace, the Mind Sculptor");
		MagicEdition ed = new MagicEdition();
		ed.setId("A25");
		dash.getPriceVariation(mc, ed);
	}
	
	
	
	public MTGStockDashBoard() {
		super();
		connected=false;
		correspondance = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		parser = new JsonParser();
	}
	
	private void connect() throws IOException
	{
		HttpURLConnection con = getConnection("https://api.mtgstocks.com/card_sets");
		JsonArray arr = parser.parse(new JsonReader(new InputStreamReader(con.getInputStream()))).getAsJsonArray();
		for(JsonElement el : arr)
		{
			if(!el.getAsJsonObject().get("abbreviation").isJsonNull())
				correspondance.put(el.getAsJsonObject().get("abbreviation").getAsString(), el.getAsJsonObject().get("id").getAsInt());
			else
				logger.trace("no id for" + el.getAsJsonObject().get("name"));
		}
		logger.debug("init editions id done: " + correspondance.size() + " items");
		connected=true;
	}
	
	
	
	@Override
	public List<CardShake> getShakerFor(String gameFormat) throws IOException {
		if(!connected)
			connect();
		
		return new ArrayList<>();
	}

	@Override
	public List<CardShake> getShakeForEdition(MagicEdition edition) throws IOException {
		if(!connected)
			connect();
		
		List<CardShake> list = new ArrayList<>();
		
		String url = "https://api.mtgstocks.com/card_sets/"+correspondance.get(edition.getId());
		HttpURLConnection con = getConnection(url);
		JsonObject obj = parser.parse(new JsonReader(new InputStreamReader(con.getInputStream()))).getAsJsonObject();
		
		JsonArray arr = obj.get("prints").getAsJsonArray();
		
		for(JsonElement el : arr)
		{
			CardShake cs = new CardShake();
					  cs.setName(el.getAsJsonObject().get("name").getAsString());
					  cs.setEd(edition.getId());
					  cs.setPrice(el.getAsJsonObject().get("latest_price").getAsJsonObject().get("avg").getAsDouble());
					  
					  list.add(cs);
		}
		
		
		return list;
	}

	@Override
	public Map<Date, Double> getPriceVariation(MagicCard mc, MagicEdition me) throws IOException {
		if(!connected)
			connect();
		
		int setId=-1;
		
		if(me!=null)
			setId=correspondance.get(me.getId());
		else
			setId=correspondance.get(mc.getEditions().get(0).getId());
		
		String url = "https://api.mtgstocks.com/search/autocomplete/"+StringUtils.replaceAll(mc.getName(), " ", "%20");
		HttpURLConnection con = getConnection(url);
		
		JsonArray arr = parser.parse(new JsonReader(new InputStreamReader(con.getInputStream()))).getAsJsonArray();
		int id = arr.get(0).getAsJsonObject().get("id").getAsInt();
		
		logger.debug("found " + id + " for " + mc.getName());
		
		String urlC ="https://api.mtgstocks.com/prints/"+id;
		con = getConnection(urlC);
		
		id = searchId(id,setId,parser.parse(new JsonReader(new InputStreamReader(con.getInputStream()))).getAsJsonObject());
		
		//get prices
		String urlP = "https://api.mtgstocks.com/prints/"+id+"/prices";
		con = getConnection(urlP);
		
		return extractPrice(parser.parse(new JsonReader(new InputStreamReader(con.getInputStream()))).getAsJsonObject());
	}

	private Map<Date, Double> extractPrice(JsonObject obj) {
		JsonArray arr = obj.get(getString("PRICE_FROM")).getAsJsonArray();
		Map<Date, Double> prices = new TreeMap<>();
		Calendar cal = GregorianCalendar.getInstance();
		
		for(JsonElement el : arr)
		{
			long timest = el.getAsJsonArray().get(0).getAsLong();
			double value = el.getAsJsonArray().get(1).getAsDouble();
			
			cal.setTime(new Date(timest));
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			
			prices.put(cal.getTime(), value);
		}
		return prices;
	}

	private int searchId(int id, int setId, JsonObject obj) {
		
		JsonArray arr = obj.get("sets").getAsJsonArray();
		
		for(JsonElement el : arr)
		{
			if(el.getAsJsonObject().get("set_id").getAsInt()==setId)
				return el.getAsJsonObject().get("id").getAsInt();
			
		}
		return id;
	}

	@Override
	public String getName() {
		return "MTG Stocks";
	}

	@Override
	public Date getUpdatedDate() {
		return new Date();
	}

	@Override
	public List<CardDominance> getBestCards(FORMAT f,String filter) throws IOException {
		if(!connected)
			connect();
		
		
		//https://api.mtgstocks.com/analytics/mostplayed/1  1=Legacy 2=Vintage 3=Modern 4=Standard 7=Pauper
			
		return new ArrayList<>();
	}

	@Override
	public String[] getDominanceFilters() {
		return new String[] { "Legacy","Vintage", "Standard","Modern","Pauper"};
	}


	@Override
	public void initDefault() {
		setProperty("LOGIN", "login@mail.com");
		setProperty("PASS", "changeme");
		setProperty("MTGSTOCKS_BASE_URL","https://www.mtgstocks.com");
		setProperty("USER_AGENT", MTGConstants.USER_AGENT);
		setProperty("PRICE_FROM","avg"); 	//[low, avg, high, foil, market, market_foil]
	}


	@Override
	public String getVersion() {
		return "1";
	}
	
	private HttpURLConnection getConnection(String url) throws IOException
	{
			logger.debug("get stream from " + url);
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestProperty("User-Agent", MTGConstants.USER_AGENT);
			connection.connect();
			return connection;
	}

}

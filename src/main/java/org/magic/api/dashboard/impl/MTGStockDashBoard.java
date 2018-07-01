package org.magic.api.dashboard.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.CardDominance;
import org.magic.api.beans.CardPriceVariations;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;
import org.magic.tools.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class MTGStockDashBoard extends AbstractDashBoard {
	private static final String PRINT = "print";
	private static final String LEGAL = "legal";
	private static final String MTGSTOCK_API_URI = "https://api.mtgstocks.com";
	private boolean connected;
	private Map<String, Integer> correspondance;
	private JsonParser parser;
	private JsonObject interests;

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	public MTGStockDashBoard() {
		super();
		connected = false;
		correspondance = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		parser = new JsonParser();
	}

	private void connect() throws IOException {
		if (!connected) {
			initInterests();
			initEds();
			connected = true;
		}
	}

	@Override
	public List<CardShake> getShakerFor(MTGFormat f) throws IOException {
		connect();

		List<CardShake> ret = new ArrayList<>();
		String format="";
		
		if(f !=null)
			format=f.name().toLowerCase();
		
		for (String filter : getString("SHAKERS").split(","))
			for (JsonElement el : interests.get(getString("FORMAT_SHAKER")).getAsJsonObject().get(filter).getAsJsonArray()) 
			{
				if ( f==null || (el.getAsJsonObject().get(PRINT).getAsJsonObject().get(LEGAL).getAsJsonObject().get(format) != null && el.getAsJsonObject().get(PRINT).getAsJsonObject().get(LEGAL).getAsJsonObject().get(format).getAsString().equalsIgnoreCase(LEGAL))) 
					ret.add(extract(el));
				
			}
		return ret;
	}

	private CardShake extract(JsonElement el) {
		CardShake cs = new CardShake();
		cs.setName(el.getAsJsonObject().get(PRINT).getAsJsonObject().get("name").getAsString());
		cs.setPrice(el.getAsJsonObject().get("present_price").getAsDouble());
		cs.setPercentDayChange(el.getAsJsonObject().get("percentage").getAsDouble());
		cs.setPriceDayChange(el.getAsJsonObject().get("present_price").getAsDouble()- el.getAsJsonObject().get("past_price").getAsDouble());
		cs.setDateUpdate(new Date(el.getAsJsonObject().get("date").getAsLong()));
		cs.setCurrency(Currency.getInstance("USD"));
		correspondance.forEach((key, value) -> {
			if (value == el.getAsJsonObject().get(PRINT).getAsJsonObject().get("set_id").getAsInt()) {
				cs.setEd(key);
			}
		});
		return cs;
	}

	@Override
	public List<CardShake> getShakeForEdition(MagicEdition edition) throws IOException {
		connect();

		if(cacheEditions.get(edition.getId())!=null)
			return cacheEditions.get(edition.getId());
		
		List<CardShake> list = new ArrayList<>();

		String url = MTGSTOCK_API_URI + "/card_sets/" + correspondance.get(edition.getId());
		logger.debug("loading edition cardshake from " + url);
		HttpURLConnection con = URLTools.openConnection(url);
		JsonObject obj = parser.parse(new JsonReader(new InputStreamReader(con.getInputStream()))).getAsJsonObject();

		JsonArray arr = obj.get("prints").getAsJsonArray();

		for (JsonElement el : arr) {
			CardShake cs = new CardShake();
			cs.setName(el.getAsJsonObject().get("name").getAsString());
			cs.setEd(edition.getId());
			cs.setCurrency(Currency.getInstance("USD"));
			try{
				cs.setPrice(el.getAsJsonObject().get("latest_price").getAsJsonObject().get("avg").getAsDouble());
			}
			catch(Exception e)
			{
				logger.error("Error adding :" + el);
			}
			list.add(cs);
		}
		cacheEditions.put(edition.getId(), list);
		con.disconnect();
		return list;
	}

	@Override
	public CardPriceVariations getPriceVariation(MagicCard mc, MagicEdition me) throws IOException {
		
		if(mc==null)
		{
			logger.error("couldn't calculate edition only");
			return new CardPriceVariations(mc);
		}

		
		connect();
		logger.debug(mc + " " + me);
		int setId = -1;

		if (me != null)
			setId = correspondance.get(me.getId());
		else
			setId = correspondance.get(mc.getCurrentSet().getId());

		String url = MTGSTOCK_API_URI + "/search/autocomplete/" + StringUtils.replaceAll(mc.getName(), " ", "%20");
		
		logger.debug("get prices to " + url);
		
		HttpURLConnection con = URLTools.openConnection(url);

		JsonArray arr = parser.parse(new JsonReader(new InputStreamReader(con.getInputStream()))).getAsJsonArray();
		int id = arr.get(0).getAsJsonObject().get("id").getAsInt();

		logger.trace("found " + id + " for " + mc.getName());

		String urlC = MTGSTOCK_API_URI + "/prints/" + id;
		con = URLTools.openConnection(urlC);

		id = searchId(id, setId,
				parser.parse(new JsonReader(new InputStreamReader(con.getInputStream()))).getAsJsonObject());

		// get prices
		String urlP = MTGSTOCK_API_URI + "/prints/" + id + "/prices";
		con = URLTools.openConnection(urlP);

		CardPriceVariations ret = extractPrice(parser.parse(new JsonReader(new InputStreamReader(con.getInputStream()))).getAsJsonObject(), mc);
		con.disconnect();
	
		return ret;
	}

	private CardPriceVariations extractPrice(JsonObject obj,MagicCard mc) {
		JsonArray arr = obj.get(getString("CARD_PRICES_SHAKER")).getAsJsonArray();
		CardPriceVariations prices = new CardPriceVariations(mc);
		prices.setCurrency(Currency.getInstance("USD"));
		Calendar cal = GregorianCalendar.getInstance();

		for (JsonElement el : arr) {
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

		for (JsonElement el : arr) {
			if (el.getAsJsonObject().get("set_id").getAsInt() == setId)
				return el.getAsJsonObject().get("id").getAsInt();

		}
		return id;
	}

	private void initInterests() throws IOException {

		if (interests == null) {
			HttpURLConnection con = URLTools.openConnection(MTGSTOCK_API_URI + "/interests");
			interests = parser.parse(new JsonReader(new InputStreamReader(con.getInputStream()))).getAsJsonObject();
			con.disconnect();
		}
	}

	private void initEds() throws IOException {
		
		if(correspondance.isEmpty()) 
		{
			HttpURLConnection con = URLTools.openConnection(MTGSTOCK_API_URI + "/card_sets");
			JsonArray arr = parser.parse(new JsonReader(new InputStreamReader(con.getInputStream()))).getAsJsonArray();
			arr.forEach(el->{
				if (!el.getAsJsonObject().get("abbreviation").isJsonNull())
					correspondance.put(el.getAsJsonObject().get("abbreviation").getAsString(),
							el.getAsJsonObject().get("id").getAsInt());
				else
					logger.trace("no id for" + el.getAsJsonObject().get("name"));
				con.disconnect();	
			});
			logger.debug("init editions id done: " + correspondance.size() + " items");
			
		}
	}
	

	@Override
	public String getName() {
		return "MTGStocks";
	}

	@Override
	public Date getUpdatedDate() {
		if(interests==null)
			return new Date();
		else
			return new Date(interests.get("date").getAsLong());
	}

	@Override
	public List<CardDominance> getBestCards(MTGFormat f, String filter) throws IOException {
		if (!connected)
			connect();

		List<CardDominance> ret = new ArrayList<>();
		int id = 1;

		switch (f) {
		case LEGACY:
			id = 1;
			break;
		case MODERN:
			id = 3;
			break;
		case STANDARD:
			id = 4;
			break;
		case VINTAGE:
			id = 2;
			break;
		// 1=Legacy 2=Vintage 3=Modern 4=Standard 7=Pauper
		default:
			break;
		}
		String url = MTGSTOCK_API_URI + "/analytics/mostplayed/" + id;
		HttpURLConnection con =  URLTools.openConnection(url);
		JsonObject obj = parser.parse(new JsonReader(new InputStreamReader(con.getInputStream()))).getAsJsonObject();
		JsonArray arr = obj.get("mostplayed").getAsJsonArray();
		int i = 1;
		for (JsonElement el : arr) {
			CardDominance cd = new CardDominance();
			cd.setCardName(el.getAsJsonObject().get("card").getAsJsonObject().get("name").getAsString());
			cd.setPlayers(el.getAsJsonObject().get("quantity").getAsDouble());
			cd.setPosition(i++);
			ret.add(cd);
		}

		return ret;
	}

	@Override
	public String[] getDominanceFilters() {
		return new String[] { "" };
	}

	@Override
	public void initDefault() {
		setProperty("LOGIN", "login@mail.com");
		setProperty("PASS", "changeme");
		setProperty("MTGSTOCKS_BASE_URL", "https://www.mtgstocks.com");
		setProperty("CARD_PRICES_SHAKER", "market"); // [low, avg, high, foil, market, market_foil]
		setProperty("FORMAT_SHAKER", "average"); // average // market
		setProperty("SHAKERS","normal,foil");
	}
	
	@Override
	public String getVersion() {
		return "1";
	}

}

package org.magic.api.dashboard.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.RegExUtils;
import org.magic.api.beans.CardDominance;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.EditionsShakers;
import org.magic.api.beans.HistoryPrice;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.Packaging;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MTGStockDashBoard extends AbstractDashBoard {
	private static final String PRINT = "print";
	private static final String LEGAL = "legal";
	private static final String MTGSTOCK_API_URI = "https://api.mtgstocks.com";
	private boolean connected;
	private Map<String, Integer> correspondance;
	private JsonObject interests;

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	public MTGStockDashBoard() {
		super();
		connected = false;
		correspondance = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	}

	private void connect() throws IOException {
		if (!connected) {
			initInterests();
			initEds();
			connected = true; 
		}
	}

	@Override
	public List<CardShake> getOnlineShakerFor(MagicFormat.FORMATS f) throws IOException {
		connect();

		List<CardShake> ret = new ArrayList<>();
		String format="";
		
		if(f !=null)
			format=f.name().toLowerCase();
		
		for (String filter : getArray("SHAKERS"))
			for (JsonElement el : interests.get(getString("FORMAT_SHAKER")).getAsJsonObject().get(filter).getAsJsonArray()) 
			{
				if ( f==null || (el.getAsJsonObject().get(PRINT).getAsJsonObject().get(LEGAL).getAsJsonObject().get(format) != null && el.getAsJsonObject().get(PRINT).getAsJsonObject().get(LEGAL).getAsJsonObject().get(format).getAsString().equalsIgnoreCase(LEGAL))) 
					ret.add(extract(el));
				
			}
		return ret;
	}

	private CardShake extract(JsonElement el) {
		logger.trace(el);
		CardShake cs = new CardShake();
		cs.setName(el.getAsJsonObject().get(PRINT).getAsJsonObject().get("name").getAsString());
		cs.setPrice(el.getAsJsonObject().get("present_price").getAsDouble());
		cs.setPercentDayChange(el.getAsJsonObject().get("percentage").getAsDouble());
		cs.setPriceDayChange(el.getAsJsonObject().get("present_price").getAsDouble()- el.getAsJsonObject().get("past_price").getAsDouble());
		cs.setDateUpdate(new Date(el.getAsJsonObject().get("date").getAsLong()));
		cs.setCurrency(Currency.getInstance("USD"));
		cs.setProviderName(getName());
		cs.setLink("https://www.mtgstocks.com/prints/"+el.getAsJsonObject().get(PRINT).getAsJsonObject().get("id"));
		correspondance.forEach((key, value) -> {
			if (value == el.getAsJsonObject().get(PRINT).getAsJsonObject().get("set_id").getAsInt()) {
				cs.setEd(key);
			}
		});
		return cs;
	}
	
	@Override
	protected EditionsShakers getOnlineShakesForEdition(MagicEdition edition) throws IOException {
		connect();
		EditionsShakers list = new EditionsShakers();

		list.setProviderName(getName());
		list.setEdition(edition);
		list.setDate(new Date());
		
		String id = edition.getId();
		
		if(id.equals("CON_"))
			id="CON";
		
		if(correspondance.get(id)==null)
		{
			
			logger.debug(id + " is not found in " + getName());
			return list;
		}
		
		
		String url = MTGSTOCK_API_URI + "/card_sets/" + correspondance.get(id);

		JsonObject obj = RequestBuilder.build().method(METHOD.GET).setClient(URLTools.newClient()).url(url).toJson().getAsJsonObject();

		JsonArray arr = obj.get("prints").getAsJsonArray();

		for (JsonElement el : arr) {
			CardShake cs = new CardShake();
			cs.setName(el.getAsJsonObject().get("name").getAsString());
			cs.setEd(edition.getId());
			cs.setCurrency(Currency.getInstance("USD"));
			cs.setProviderName(getName());
		
			try{
				double todayPrice = el.getAsJsonObject().get("latest_price").getAsJsonObject().get("avg").getAsDouble();
				double lastDayPrice = el.getAsJsonObject().get("previous_price").getAsDouble();
				double lastWeekPrice = el.getAsJsonObject().get("last_week_price").getAsDouble();
				cs.init(todayPrice, lastDayPrice, lastWeekPrice);
			}
			catch(Exception e)
			{
				logger.error("Error adding :" + el +" :" + e);
			}
			cs.setLink("https://www.mtgstocks.com/prints/"+el.getAsJsonObject().get("id"));
			
			notify(cs);
			list.addShake(cs);
		}
		return list;
	}

	@Override
	public HistoryPrice<MagicCard> getOnlinePricesVariation(MagicCard mc, MagicEdition me) throws IOException {
		
		if(mc==null)
		{
			logger.error("couldn't calculate edition only");
			return new HistoryPrice<>(mc);
		}

		
		connect();

		Integer id = mc.getMtgstocksId();
		
		if(id==null) 
		{
			int setId = -1;
	
			if (me != null)
				setId = correspondance.get(me.getId());
			else
				setId = correspondance.get(mc.getCurrentSet().getId());
	
			String url = MTGSTOCK_API_URI + "/search/autocomplete/" + RegExUtils.replaceAll(mc.getName(), " ", "%20");
			
			logger.debug("get prices to " + url);
			
			
			
				JsonArray arr = URLTools.extractJson(url).getAsJsonArray();
				id = arr.get(0).getAsJsonObject().get("id").getAsInt();
				logger.trace("found " + id + " for " + mc.getName());
				id = searchId(id, setId,URLTools.extractJson(MTGSTOCK_API_URI + "/prints/" + id).getAsJsonObject());
		}
		

		return extractPrice(URLTools.extractJson(MTGSTOCK_API_URI + "/prints/" + id + "/prices").getAsJsonObject(), mc);
		
	}

	private HistoryPrice<MagicCard> extractPrice(JsonObject obj,MagicCard mc) {
		
		logger.trace("extract " + obj);
		
		JsonArray arr = obj.get(getString("CARD_PRICES_SHAKER")).getAsJsonArray();
		
		if(mc.getCurrentSet().isFoilOnly())
			arr = obj.get("foil").getAsJsonArray();
		
		
		HistoryPrice<MagicCard> prices = new HistoryPrice<>(mc);
		prices.setCurrency(Currency.getInstance("USD"));
		Calendar cal = Calendar.getInstance();

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
			interests = RequestBuilder.build().setClient(URLTools.newClient()).method(METHOD.GET).url(MTGSTOCK_API_URI + "/interests").toJson().getAsJsonObject();
		}
	}

	private void initEds() throws IOException {
		
		if(correspondance.isEmpty()) 
		{
			JsonArray arr = RequestBuilder.build().setClient(URLTools.newClient()).method(METHOD.GET).url(MTGSTOCK_API_URI + "/card_sets").toJson().getAsJsonArray();
			arr.forEach(el->{
				if (!el.getAsJsonObject().get("abbreviation").isJsonNull())
					correspondance.put(el.getAsJsonObject().get("abbreviation").getAsString(),el.getAsJsonObject().get("id").getAsInt());
				else
					logger.error("no id for" + el.getAsJsonObject().get("name"));
			});
			logger.debug("init editions id done: " + correspondance.size() + " items");
			
		}
		correspondance.put("FBB", 305);
		correspondance.put("FWB", 306);
		
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
	public List<CardDominance> getBestCards(MagicFormat.FORMATS f, String filter) throws IOException {
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
		default:return new ArrayList<>();
		}
		String url = MTGSTOCK_API_URI + "/analytics/mostplayed/" + id;
		JsonObject obj = URLTools.extractJson(url).getAsJsonObject();
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

	@Override
	public HistoryPrice<Packaging> getOnlinePricesVariation(Packaging packaging) throws IOException {
		connect();
		
		HistoryPrice<Packaging> ret = new HistoryPrice<>(packaging);
		
		String url = MTGSTOCK_API_URI + "/card_sets/" + correspondance.get(packaging.getEdition().getId())+"/ev";
		
		String strPrices = URLTools.extractJson(url).getAsJsonObject().get("price_hash").getAsString();
		JsonObject obj = URLTools.toJson(strPrices).getAsJsonObject().get("booster").getAsJsonObject();
		
		
		switch (packaging.getType()) 
		{
			case BOOSTER :ret.put(new Date(), obj.get("avg").getAsDouble());break;
			case BOX: ret.put(new Date(), obj.get("avg").getAsDouble() * obj.get("num").getAsDouble());break;
			default: break;
		}
		
		
		
		return ret;
	}
	
}

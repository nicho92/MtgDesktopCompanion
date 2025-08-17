package org.magic.api.dashboard.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.EditionsShakers;
import org.magic.api.beans.HistoryPrice;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDominance;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.enums.EnumCardVariation;
import org.magic.api.beans.enums.EnumExtra;
import org.magic.api.beans.enums.EnumPromoType;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;
import org.magic.services.MTGConstants;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

public class MTGoldFishDashBoard extends AbstractDashBoard {
	private static final String FORMAT = "FORMAT";
	private static final String DAILY_WEEKLY = "DAILY_WEEKLY";
	private static final String WEBSITE = "https://www.mtggoldfish.com";
	private static final String SET_EXTRA = "SET_EnumExtra";
	private static final String MOVERS_DETAILS = WEBSITE+"/movers-details/";
	private MTGHttpClient client;
	
	
	boolean isPaperparsing=true;

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	public MTGoldFishDashBoard() {
		client = URLTools.newClient();
	}
	
	
	
	@Override
	public HistoryPrice<MTGSealedProduct> getOnlinePricesVariation(MTGSealedProduct packaging) throws IOException {


		var history =  new HistoryPrice<MTGSealedProduct>(packaging);
			 history.setCurrency(getCurrency());

		if(packaging==null || packaging.getEdition()==null)
				return history;
		
		logger.debug("loading prices for {}",packaging);
		parsing(history);							  
		


		return history;
	}


	@Override
	protected HistoryPrice<MTGEdition> getOnlinePricesVariation(MTGEdition me) throws IOException {
		var historyPrice = new HistoryPrice<MTGEdition>(me);
			historyPrice.setCurrency(getCurrency());
		
			if(me==null )
				return historyPrice;
			
			parsing(historyPrice);
		
		return historyPrice;
	}

	@Override
	public HistoryPrice<MTGCard> getOnlinePricesVariation(MTGCard mc, boolean foil) throws IOException {

		var historyPrice = new HistoryPrice<MTGCard>(mc);
		 historyPrice.setCurrency(getCurrency());
		 historyPrice.setFoil(foil);
		
		if(mc==null )
			return historyPrice;
		
		parsing(historyPrice);
		
		return historyPrice;
	}
	
	
	private String token="";
	
	private String readXcrf() throws IOException
	{
		
		if(!token.isEmpty())
			return token;
		
		var meta = RequestBuilder.build().setClient(client).url(WEBSITE).get().toHtml().getElementsByAttributeValue("name", "csrf-token").first();
		
		if(meta==null)
			throw new IOException("No csrf token present");
		
		token = meta.attr("content");
		
		logger.debug("getting {} token",token);
		
		return token;
	}
/*
	private String suggestId(MTGCard c,boolean foil) throws IOException
	{
		var arr = RequestBuilder.build().url(WEBSITE+"/autocomplete").setClient(client).get()
				.addContent("term",c.getName())
				.addHeader("referer", WEBSITE)
				.addHeader("x-requested-with", "XMLHttpRequest")
				.addHeader(URLTools.ACCEPT,"application/json, text/javascrip td")
				.addHeader("priority","u=1, i")
				.addHeader("x-csrf-token", readXcrf())
				.toJson().getAsJsonArray();
	
				var q = arr.asList().stream().filter(je->je.getAsJsonObject().get("id").getAsString().contains(aliases.getReversedSetIdFor(this, c.getEdition())));
				
				if(foil)
					q = q.filter(je->je.getAsJsonObject().get("finish").getAsString().contains("foil"));
					
					
				var res = q.toList();	
				
				logger.info("return {}" , res);
				
				
				if(res.size()==1)
					return res.get(0).getAsJsonObject().get("id").getAsString();
		
		
				return "";
	}
	
	*/

	private void parsing(HistoryPrice <?> history) throws IOException {
		
			var url =WEBSITE+"/price_history_component";
			
			
			var cardid="";
			var pricetype="card";
			
			if(history.getItem() instanceof MTGCard card)
			{
				var variant = "";
				
				if(card.isTimeshifted())
					variant = "<futureshifted>";
				else if(card.isBasicLand() && !card.isExtraCard())
					variant = "<"+card.getNumber()+">";
				else if(card.isShowCase())
					variant = "<showcase>";
				else if(card.isBorderLess())
					variant = "<borderless"+(card.getPromotypes().contains(EnumPromoType.STEPANDCOMPLEAT)?" Step and Compleat Foil":"")+">";
				else if(card.isExtendedArt())
					variant = "<extended>";
				else if(card.isRetro())
					variant ="<retro>";
				else if(card.isJapanese())
					variant = "<Japanese>";
				
				if(card.getPromotypes().contains(EnumPromoType.POSTER))
					variant = "<borderless poster>";
				if(card.getPromotypes().contains(EnumPromoType.PRERELEASE))
					variant = "<prerelease>";
				if(card.getPromotypes().contains(EnumPromoType.SERIALIZED))
					variant = "<serialized>";
				if(card.getPromotypes().contains(EnumPromoType.FIRSTPLACEFOIL ))
					variant = "<first place" + (card.isShowCase()?" showcase>":">");
				if(card.getPromotypes().contains(EnumPromoType.FRACTUREFOIL))
					variant = "<"+(card.isShowCase()?"showcase - ":"") + "fracture foil>";
				if(card.getPromotypes().contains(EnumPromoType.TEXTURED))
					variant = "<textured>";	
				
				cardid=card.getName() + (variant.isEmpty()?"": " " +variant) +" ["+aliases.getReversedSetIdFor(this, card.getEdition())+"] "+(history.isFoil()?"(F)":"");
			}
			else if(history.getItem() instanceof MTGEdition set)
			{
				cardid= set.getId()+"-main_set";
				pricetype="set";
			}
			else if(history.getItem() instanceof MTGSealedProduct packaging)
			{
				var selead ="";
				if(packaging.getExtra()!=null) {
					switch (packaging.getExtra())
					{
						case COLLECTOR: selead="Collector";break;
						case DRAFT: selead="Draft";break;
						case SET: selead="Set";break;
						case PLAY: selead ="Play";break;
						case THEME: selead="Collector";break;
						case VIP: selead="VIP Edition Pack";break;
						default: selead="";break;
					}
				}

				if(packaging.getExtra()!=EnumExtra.VIP)
				{

					switch(packaging.getTypeProduct())
					{
						case SET:break;
						case BOOSTER:selead+=" Booster Pack";break;
						case BOX:selead+=" Booster Box";break;
						case FATPACK:selead+=" Fat Pack";break;
						case BUNDLE:selead+=" Bundle";break;
						case PRERELEASEPACK:selead+=" Prerelease Pack";break;
						case STARTER: break;
						default:break;

					}
				}

				cardid= aliases.getSetNameFor(this, packaging.getEdition()) +  " " + selead + " <sealed> "+ "["+aliases.getReversedSetIdFor(this, packaging.getEdition())+"]";
			}
			
			var q = RequestBuilder.build().url(url).setClient(client).get()
							.addContent("card_id",cardid)
							.addContent("selector","#tab-"+getString(FORMAT).toLowerCase())
							.addContent("type",getString(FORMAT).toLowerCase())
							.addContent("price_type",pricetype)
							.addHeader("referer", WEBSITE)
							.addHeader("x-requested-with", "XMLHttpRequest")
							.addHeader("x-csrf-token", readXcrf())
							.toHtml();
			
			var res = q.select("a span").html();
			
			try {
				res = res.substring(res.indexOf("d += "),res.indexOf("g = "));
			}
			catch(StringIndexOutOfBoundsException _ )
			{
				logger.error("can't found any data for {}",cardid);
				return ;
			}
			res = RegExUtils.replaceAll(res, "d \\+\\= ", "");
			res = RegExUtils.replaceAll(res, ";", "");
			res = RegExUtils.replaceAll(res, "\"", "");
			
			for(var l : res.split("\\\\n"))
			{
				if(!StringUtils.isEmpty(l))
				{
				var content = l.split(",");
				history.put(UITools.parseDate(content[0], "yyyy-MM-dd"), UITools.parseDouble(content[1]));
				}
			}
	}
	
	
	@Override
	public List<CardShake> getOnlineShakerFor(MTGFormat.FORMATS f) throws IOException {
		var list = new ArrayList<CardShake>();

		var gameFormat="all";
		if(f!=null)
			gameFormat=f.name();

		var urlW = MOVERS_DETAILS + getString(FORMAT) + "/" + gameFormat.toLowerCase() + "/winners/"+ getString(DAILY_WEEKLY);
		var urlL = MOVERS_DETAILS + getString(FORMAT) + "/" + gameFormat.toLowerCase() + "/losers/"+ getString(DAILY_WEEKLY);

		logger.trace("Loading Shake {} and {}",urlW,urlL);
		var doc = URLTools.extractAsHtml(urlW);
		var doc2 = URLTools.extractAsHtml(urlL);

		Element table = null;
		try {

			table = doc.select(MTGConstants.HTML_TAG_TABLE).get(0).getElementsByTag("tbody").first().appendChild(doc2
							 .select(MTGConstants.HTML_TAG_TABLE).get(0).getElementsByTag(MTGConstants.HTML_TAG_TBODY).get(0));

			for (Element e : table.getElementsByTag(MTGConstants.HTML_TAG_TR)) {
				var cs = new CardShake();
				
				cs.setProviderName(getName());
				cs.setName(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(2).select("span a").text());
				cs.setLink(WEBSITE+e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(2).getElementsByTag("a").get(0).attr("href"));
				cs.setPrice(UITools.parseDouble(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(3).text()));
				cs.setPriceDayChange(UITools.parseDouble(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(0).text()));
				cs.setPercentDayChange(UITools.parseDouble(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(4).text())/100);
				cs.setFoil(false);
				var set = e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(2).getElementsByTag("a").get(0).attr("data-card-id");
				cs.setEd(aliases.getSetIdFor(this,StringUtils.substringBetween(set, "[", "]").toUpperCase()));

				list.add(cs);

			}
		} catch (IndexOutOfBoundsException e) {
			logger.error("error parsing ",e);
		}
		return list;

	}

	@Override
	protected EditionsShakers getOnlineShakesForEdition(MTGEdition edition) throws IOException {
		var list = new EditionsShakers();
		list.setEdition(edition);
		list.setProviderName(getName());
		list.setDate(new Date());

		if(edition==null)
			return list;

		var urlEditionChecker = WEBSITE+"/sets/" + aliases.getReversedSetIdFor(this,edition)+"/All+Cards";

		urlEditionChecker = URLTools.getLocation(urlEditionChecker);


		var page = "Main+Set";

		if(getBoolean(SET_EXTRA))
			page ="All+Cards";


		if (edition.isOnlineOnly())
			urlEditionChecker += "/"+page+"#online";
		else
			urlEditionChecker += "/"+page+"#"+getString(FORMAT);

		var doc = URLTools.extractAsHtml(urlEditionChecker);
		
		var trs = URLTools.toJson(doc.select("div[data-react-class=CardsContainer]").attr("data-react-props"));

		for(var e : trs.getAsJsonObject().get("cards").getAsJsonArray())
		{
		
			var obj = e.getAsJsonObject();
			var nameExtra = obj.get("name").getAsString().toLowerCase();
			
			if(obj.get("subset_id").getAsString().equals("sealed"))
				continue;
			
					var cs = new CardShake();
						cs.setCurrency(getCurrency());
						cs.setName(obj.get("display_name").getAsString());
						cs.setLink(WEBSITE+obj.get("links").getAsJsonObject().get("default").getAsString());
						cs.setFoil(obj.get("foil").getAsBoolean());
						cs.setEtched(nameExtra.contains("etched"));
						
						
						
						if(!obj.get("card_num").isJsonNull())
							cs.setNumber(obj.get("card_num").getAsString());
						
						cs.setEd(edition.getId());
						cs.setProviderName(getName());
						try {
							
							var pobj=obj.get("prices").getAsJsonObject().get(getString("FORMAT").toLowerCase()).getAsJsonObject();
							
							cs.setPrice(pobj.get("current_price").getAsDouble());
							cs.setPriceDayChange(pobj.get("dod_delta").getAsDouble());
							cs.setPercentDayChange(pobj.get("dod_pct").getAsDouble());
							cs.setPriceWeekChange(pobj.get("wow_delta").getAsDouble());
							cs.setPercentWeekChange(pobj.get("wow_pct").getAsDouble());
						}
						catch(Exception _)
						{
							cs.setPrice(0.0);
						}
	
						if(nameExtra.contains("extended"))
							cs.setCardVariation(EnumCardVariation.EXTENDEDART);
						else if(nameExtra.contains("showcase"))
							cs.setCardVariation(EnumCardVariation.SHOWCASE);
						else if(nameExtra.contains("borderless"))
							cs.setCardVariation(EnumCardVariation.BORDERLESS);
						else if (nameExtra.contains("timeshifted"))
							cs.setCardVariation(EnumCardVariation.TIMESHIFTED);
						else if (nameExtra.contains("retro"))
							cs.setCardVariation(EnumCardVariation.RETRO);
						else if (nameExtra.contains("japanese"))
							cs.setCardVariation(EnumCardVariation.JAPANESEALT);
						else if (nameExtra.contains("serialized"))
							cs.setCardVariation(EnumCardVariation.SERIALIZED);
						
						
			list.addShake(cs);
			notify(cs);
		}

		return list;
	}

	@Override
	public List<MTGDominance> getBestCards(MTGFormat.FORMATS f, String filter) throws IOException {

		// spells, creatures, all, lands

		String u = WEBSITE + "/format-staples/" + f.name().toLowerCase() + "/full/" + filter;

		if(f == MTGFormat.FORMATS.COMMANDER)
			u=WEBSITE + "/format-staples/commander_1v1/full/" + filter;

		Document doc = URLTools.extractAsHtml(u);

		logger.debug("get best cards : {}",u);
		Elements trs = doc.select("table tr");
		trs.remove(0);
		List<MTGDominance> ret = new ArrayList<>();
		for (Element e : trs) {
			Elements tds = e.select(MTGConstants.HTML_TAG_TD);
			try {
				int correct = filter.equalsIgnoreCase("lands") ? 1 : 0;

				var d = new MTGDominance();
				d.setPosition(Integer.parseInt(tds.get(0).text()));
				d.setCardName(tds.get(1).text());
				d.setDecksPercent(UITools.parseDouble(tds.get(3 - correct).text()));
				d.setPlayers(UITools.parseDouble(tds.get(4 - correct).text()));

				ret.add(d);
			} catch (Exception ex) {
				logger.error("Error parsing {}",tds, ex);
			}

		}
		return ret;
	}

	
	@Override
	public String getName() {
		return "MTGoldFish";
	}

	@Override
	public Date getUpdatedDate() {
		try {
			return UITools.parseDate(URLTools.extractAsHtml(MOVERS_DETAILS + getString(FORMAT) + "/all/winners/"+ getString(DAILY_WEEKLY)).getElementsByClass("timeago").get(0).attr("title"), "yyyy-MM-dd'T'HH:mm:ss'Z'");

		} catch (Exception e1) {
			logger.error(e1);
		}

		return null;
	}

	@Override
	public String[] getDominanceFilters() {
		return new String[] { "all", "spells", "creatures", "lands" };
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
		
		m.put(FORMAT, new MTGProperty("paper", "paper = price of physical cards, online= price of MTGO cards","paper","online"));
		m.put(DAILY_WEEKLY, new MTGProperty("wow", "wow = shakes of the week, dod = shakes of the days","wow","dod"));
		m.put(SET_EXTRA, MTGProperty.newBooleanProperty("true", "if true, all extra cards will be loaded"));
		return m;
	}

	@Override
	public String getVersion() {
		return "3.0";
	}



}

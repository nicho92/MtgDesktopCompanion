package org.magic.api.dashboard.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.Level;
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
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;
import org.magic.api.interfaces.extra.MTGProduct;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstNode;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class MTGoldFishDashBoard extends AbstractDashBoard {
	private static final String FORMAT = "FORMAT";
	private static final String DAILY_WEEKLY = "DAILY_WEEKLY";
	private static final String WEBSITE = "https://www.mtggoldfish.com";
	private static final String SET_EXTRA = "SET_EnumExtra";
	private static final String MOVERS_DETAILS = WEBSITE+"/movers-details/";

	boolean isPaperparsing=true;

	@Override
	public STATUT getStatut() {
		return STATUT.BUGGED;
	}
	

	private String searchUrlFor(MTGCard mc,boolean foil)
	{
		var arr = RequestBuilder.build().setClient(URLTools.newClient())
				  .get()
				  .url(WEBSITE+"/autocomplete?term="+URLTools.encode(mc.getName()))
				  .addHeader("x-requested-with", "XMLHttpRequest")
				  .addHeader("accept", "application/json, text/javascript, */*; q=0.01")
				  .addHeader("referer", WEBSITE)
				  .toJson().getAsJsonArray();

			JsonObject item=null;
		
			if(arr.isEmpty())
			{
				logger.debug("No url found for {}",mc);
				return null;
			}

			if(arr.size()==1)
			{
				item = arr.get(0).getAsJsonObject();
			}
			else
			{
				var filteredArray = new JsonArray();
				var set = aliases.getReversedSetIdFor(this,mc.getEdition());
				for(var el : arr)
				{
					if(el.getAsJsonObject().get("id").getAsString().contains("["+set+"]") && el.getAsJsonObject().get("foil").getAsBoolean()==foil){
						filteredArray.add(el);
					}
				}

				logger.debug("filtered with set {} and foil = {} and extra={} : {} items",set,foil,mc.getExtra(),filteredArray.size());
				logger.debug(filteredArray);
				var variationTag = "variation";
				if(filteredArray.size()==1) {
					item = filteredArray.get(0).getAsJsonObject();
				}
				else if(filteredArray.size()>1)
				{
					for(var el : filteredArray)
					{
							if(el.getAsJsonObject().get("id").getAsString().contains(mc.getNumber())){
								item=el.getAsJsonObject();
							}
							else if(!el.getAsJsonObject().get(variationTag).isJsonNull() && mc.getExtra()!=null)
							{
								if(mc.getFlavorName()!=null && mc.getFlavorName().equalsIgnoreCase(el.getAsJsonObject().get(variationTag).getAsString())){
									item=el.getAsJsonObject();
								}
								if(mc.isShowCase() && el.getAsJsonObject().get(variationTag).getAsString().equals("Showcase")) {
									item=el.getAsJsonObject();
								}
								if(mc.isBorderLess() && el.getAsJsonObject().get(variationTag).getAsString().equals("Borderless")) {
									item=el.getAsJsonObject();
								}
								if(mc.isExtendedArt() && el.getAsJsonObject().get(variationTag).getAsString().equals("Extended")) {
									item=el.getAsJsonObject();
								}
								if(mc.isJapanese() && el.getAsJsonObject().get(variationTag).getAsString().equals("Japanese")) {
									item=el.getAsJsonObject();
								}
								if(mc.isRetro() && (el.getAsJsonObject().get(variationTag).getAsString().equals("Retro"))) {
									item=el.getAsJsonObject();
								}
								if(mc.isTimeshifted() && el.getAsJsonObject().get(variationTag).getAsString().equals("Timeshifted")) {
									item=el.getAsJsonObject();
								}
								if(mc.isBundleCard() && el.getAsJsonObject().get(variationTag).getAsString().equals("Bundle")) {
									item=el.getAsJsonObject();
								}
							}
							else if(el.getAsJsonObject().get(variationTag).isJsonNull())
							{
								item=el.getAsJsonObject();
							}
					}
				}
		}
		if(item==null)
		{
			logger.debug("item is null");
			return null;
		}


		return URLTools.getLocation(WEBSITE+"/q?utf8=%E2%9C%93&query_string="+URLTools.encode(item.get("id").getAsString()))+"#" + getString(FORMAT);
	}
	
	
	@Override
	public HistoryPrice<MTGSealedProduct> getOnlinePricesVariation(MTGSealedProduct packaging) throws IOException {


		HistoryPrice<MTGSealedProduct> history =  new HistoryPrice<>(packaging);
							  history.setCurrency(getCurrency());

		logger.debug("loading prices for {}",packaging);

		if(packaging==null || packaging.getEdition()==null)
			return history;


		var selead="";

		if(packaging.getExtra()!=null) {
			switch (packaging.getExtra())
			{
				case COLLECTOR: selead="+Collector";break;
				case DRAFT: selead="+Draft";break;
				case SET: selead="+Set";break;
				case THEME: selead="+Collector";break;
				case VIP: selead="+VIP+Edition+Pack";break;
				default: selead="";break;
			}
		}

		if(packaging.getExtra()!=EnumExtra.VIP)
		{

			switch(packaging.getTypeProduct())
			{
				case SET:break;
				case BOOSTER:selead+="+Booster+Pack";break;
				case BOX:selead+="+Booster+Box";break;
				case FATPACK:selead+="+Fat+Pack";break;
				case BUNDLE:selead+="+Bundle";break;
				case CONSTRUCTPACK:break;
				case PRERELEASEPACK:selead+="+Prerelease+Pack";break;
				case STARTER: break;
				default:break;

			}
		}


		return history;
	}


	@Override
	protected HistoryPrice<MTGEdition> getOnlinePricesVariation(MTGEdition me) throws IOException {
		String url = WEBSITE+"/sets/" + aliases.getSetIdFor(this,me) + "#" + getString(FORMAT);
		var historyPrice = new HistoryPrice<MTGEdition>(me);
		historyPrice.setCurrency(getCurrency());

		try {
			parsing(URLTools.toHtml(url),historyPrice);
			return historyPrice;

		} catch (Exception e) {
			logger.error(e);
			return historyPrice;
		}
	}

	@Override
	public HistoryPrice<MTGCard> getOnlinePricesVariation(MTGCard mc, boolean foil) throws IOException {

		var historyPrice = new HistoryPrice<MTGCard>(mc);
		 historyPrice.setCurrency(getCurrency());
		 historyPrice.setFoil(foil);
		
		if(mc==null )
			return historyPrice;

		try {
			parsing(historyPrice);
		} catch (Exception e) {
			logger.error(e);
		}
		
		return historyPrice;
	}

	
	public static void main(String[] args) throws IOException {
		
		var mc = new MTGCard();
		mc.setName("Rootwater Matriarch");
		mc.setEdition(new MTGEdition("10E", "Tenth Edition"));
		
		MTGLogger.changeLevel(Level.DEBUG);
		new MTGoldFishDashBoard().parsing(new HistoryPrice<MTGCard>(mc));
	}
	
	
	private void parsing(Document d, HistoryPrice<?> historyPrice)
	{

		Element js = null;

		for(Element j : d.getElementsByTag("script"))
		{
			if(j.toString().contains("var d = "))
			{
				js=j;
				break;
			}
		}
		if(js==null)
		{
			return;
		}



		AstNode root = new Parser().parse(js.html(), "", 1);
		isPaperparsing=true;
		root.visit(visitedNode -> {
			var stop = false;

			if (!stop && visitedNode.toSource().startsWith("d"))
			{
				String val = visitedNode.toSource();

				if(val.startsWith("document.getElementById"))
					isPaperparsing=false;

				val = RegExUtils.replaceAll(val, "d \\+\\= ", "");
				val = RegExUtils.replaceAll(val, "\\\\n", "");
				val = RegExUtils.replaceAll(val, ";", "");
				val = RegExUtils.replaceAll(val, "\"", "");
				String[] res = val.split(",");

				try {
					var date = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(res[0] + " 00:00");
					if (historyPrice.get(date) == null)
					{

						if(getString(FORMAT).equalsIgnoreCase("paper") && isPaperparsing)
							historyPrice.put(date, UITools.parseDouble(res[1]));

						if(getString(FORMAT).equalsIgnoreCase("online") && !isPaperparsing)
							historyPrice.put(date, UITools.parseDouble(res[1]));
					}

					} catch (Exception e) {
					// do nothing
					}
			}

			if (visitedNode.toSource().startsWith("g =")) {
				stop = true;
			}
			return true;
		});
	}
	
	//TODO FIX
	private void parsing(HistoryPrice <? extends MTGProduct> history) throws IOException {
			var client = URLTools.newClient();
			var url =WEBSITE+"/price_history_component";
			var token = RequestBuilder.build().setClient(client).url(WEBSITE).get().toHtml().getElementsByAttributeValue("name", "csrf-token").first().attr("content");
			
			var variant = "";
			
			if(!history.getItem().isSealed())
			{
				var card = (MTGCard)history.getItem();
				
				if(card.isShowCase())
					variant = "<showcase>";
				else if(card.isTimeshifted())
					variant = "<futureshifted>";
				else if(card.isBorderLess())
					variant = "<borderless>";
				else if(card.isExtendedArt())
					variant = "<extended>";
			}
			
			var p = history.getItem();
			var q = RequestBuilder.build().url(url).setClient(client).get()
							.addContent("card_id",p.getName() + variant +" ["+aliases.getReversedSetIdFor(this, p.getEdition())+"] "+(history.isFoil()?"(F)":""))
							.addContent("selector","#tab-paper")
							.addContent("type","paper")
							.addContent("price_type","card")
							.addHeader("referer", WEBSITE)
							.addHeader("x-requested-with", "XMLHttpRequest")
							.addHeader("x-csrf-token", token).toHtml();
			
			var res = q.select("a span").html();
			res = StringEscapeUtils.unescapeJava(res.substring(res.indexOf("d += "),res.indexOf("g = ")));
			
			res = RegExUtils.replaceAll(res, "d \\+\\= ", "");
			res = RegExUtils.replaceAll(res, ";", "");
			res = RegExUtils.replaceAll(res, "\"", "");
			
			for(var l : res.split(System.lineSeparator()))
			{
				if(!StringUtils.isEmpty(l))
				{
				var content = l.split(",");
				System.out.println(content[0]+ " " + content[1]);
				history.put(UITools.parseDate(content[0], "yyyy-MM-dd"), UITools.parseDouble(content[1]));
				}
			}
			
			
	}
	
	
	@Override
	public List<CardShake> getOnlineShakerFor(MTGFormat.FORMATS f) throws IOException {
		List<CardShake> list = new ArrayList<>();

		var gameFormat="all";
		if(f!=null)
			gameFormat=f.name();

		var urlW = MOVERS_DETAILS + getString(FORMAT) + "/" + gameFormat.toLowerCase() + "/winners/"+ getString(DAILY_WEEKLY);
		var urlL = MOVERS_DETAILS + getString(FORMAT) + "/" + gameFormat.toLowerCase() + "/losers/"+ getString(DAILY_WEEKLY);

		logger.trace("Loading Shake {} and {}",urlW,urlL);
		Document doc = URLTools.extractAsHtml(urlW);
		Document doc2 = URLTools.extractAsHtml(urlL);

		Element table = null;
		try {

			table = doc.select(MTGConstants.HTML_TAG_TABLE).get(0).getElementsByTag("tbody").first().appendChild(doc2
					.select(MTGConstants.HTML_TAG_TABLE).get(0).getElementsByTag(MTGConstants.HTML_TAG_TBODY).get(0));// combine
																														// 2
																														// results

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

		Document doc = URLTools.extractAsHtml(urlEditionChecker);





		Elements trs = doc.select(MTGConstants.HTML_TAG_TABLE+".card-container-table tbody tr");
		
		if(!trs.isEmpty())
			trs.remove(0);

		for(Element e : trs)
		{
			var nameExtra= e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(1).select("span.badge").text();


			if(nameExtra.contains("Sealed"))
				continue;

					var cs = new CardShake();
						cs.setCurrency(getCurrency());
						cs.setName(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(1).select("span.card_name a").text().trim());
						cs.setLink(WEBSITE+e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(1).select("span.card_name a").attr("href"));
						cs.setFoil(nameExtra.contains("Foil"));
						cs.setEtched(nameExtra.contains("Etched"));
						cs.setNumber(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(0).text().trim());
						
						
						if(nameExtra.toLowerCase().contains("extended"))
							cs.setCardVariation(EnumCardVariation.EXTENDEDART);
						else if(nameExtra.toLowerCase().contains("showcase"))
							cs.setCardVariation(EnumCardVariation.SHOWCASE);
						else if(nameExtra.toLowerCase().contains("borderless"))
							cs.setCardVariation(EnumCardVariation.BORDERLESS);
						else if (nameExtra.toLowerCase().contains("timeshifted"))
							cs.setCardVariation(EnumCardVariation.TIMESHIFTED);
						else if (nameExtra.toLowerCase().contains("retro"))
							cs.setCardVariation(EnumCardVariation.RETRO);
						else if (nameExtra.toLowerCase().contains("japanese"))
							cs.setCardVariation(EnumCardVariation.JAPANESEALT);
						else if (nameExtra.toLowerCase().contains("serialized"))
							cs.setCardVariation(EnumCardVariation.SERIALIZED);
			
						
						
						
						
						cs.setEd(edition.getId());
						cs.setProviderName(getName());
						cs.setPrice(UITools.parseDouble(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(4).text()));
						cs.setPriceDayChange(UITools.parseDouble(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(5).text()));
						cs.setPercentDayChange(UITools.parseDouble(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(6).text())/100);
						cs.setPriceWeekChange(UITools.parseDouble(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(7).text()));
						cs.setPercentWeekChange(UITools.parseDouble(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(8).text())/100);
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

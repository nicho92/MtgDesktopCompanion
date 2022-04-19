package org.magic.api.dashboard.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.CardDominance;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.EditionsShakers;
import org.magic.api.beans.HistoryPrice;
import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.MTGSealedProduct.EXTRA;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.enums.EnumMarketType;
import org.magic.api.beans.enums.MTGCardVariation;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;
import org.magic.services.MTGConstants;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.RequestBuilder.METHOD;
import org.magic.services.network.URLTools;
import org.magic.services.providers.PluginsAliasesProvider;
import org.magic.tools.UITools;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstNode;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MTGoldFishDashBoard extends AbstractDashBoard {
	private static final String TIMEOUT = "TIMEOUT";
	private static final String FORMAT = "FORMAT";
	private static final String DAILY_WEEKLY = "DAILY_WEEKLY";
	private static final String WEBSITE = "https://www.mtggoldfish.com";
	private static final String SET_EXTRA = "SET_EXTRA";
	private static final String MOVERS_DETAILS = WEBSITE+"/movers-details/";

	boolean isPaperparsing=true;

	
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
						
						if(getString(FORMAT).equals("paper") && isPaperparsing)
							historyPrice.put(date, Double.parseDouble(res[1]));
						
						if(getString(FORMAT).equals("online") && !isPaperparsing)
							historyPrice.put(date, Double.parseDouble(res[1]));
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
	
	
	@Override
	public EnumMarketType getMarket() {
		return EnumMarketType.US_MARKET;
	}
	
	
	@Override
	public HistoryPrice<MTGSealedProduct> getOnlinePricesVariation(MTGSealedProduct packaging) throws IOException {
		

		HistoryPrice<MTGSealedProduct> history =  new HistoryPrice<>(packaging);
							  history.setCurrency(getCurrency());

		logger.debug("loading prices for " + packaging);							  
							  
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
		
		if(packaging.getExtra()!=EXTRA.VIP)
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
							  
							  
		String url = WEBSITE +"/price/"+convert(packaging.getEdition())+"/"+convert(packaging.getEdition())+selead+ "-sealed#"+ getString(FORMAT);
		
		
		Document d = URLTools.extractAsHtml(url);
		parsing(d, history);
		
		return history;
	}
	
	
	@Override
	protected HistoryPrice<MagicEdition> getOnlinePricesVariation(MagicEdition me) throws IOException {
		String url = WEBSITE+"/sets/" + PluginsAliasesProvider.inst().getSetIdFor(this,me) + "#" + getString(FORMAT);
		HistoryPrice<MagicEdition> historyPrice = new HistoryPrice<>(me);
		historyPrice.setCurrency(getCurrency());
		
		try {
			Document d = URLTools.extractAsHtml(url);
			parsing(d,historyPrice);
			return historyPrice;

		} catch (Exception e) {
			logger.error(e);
			return historyPrice;
		}
	}
	
	
	
	private String searchUrlFor(MagicCard mc,boolean foil)
	{
		var arr = RequestBuilder.build().setClient(URLTools.newClient())
				  .method(METHOD.GET)
				  .url(WEBSITE+"/autocomplete?term="+URLTools.encode(mc.getName()))
				  .addHeader("x-requested-with", "XMLHttpRequest")
				  .addHeader("accept", "application/json, text/javascript, */*; q=0.01")
				  .addHeader("referer", WEBSITE)
				  .toJson().getAsJsonArray();
		
			JsonObject item=null;
			logger.trace(arr );
			
			if(arr.isEmpty())
			{
				logger.trace("No url found for " + mc);
				return null;
			}
			
			if(arr.size()==1)
			{
				item = arr.get(0).getAsJsonObject();
			}
			else
			{
				var filteredArray = new JsonArray();
				var set = PluginsAliasesProvider.inst().getSetIdFor(this,mc.getCurrentSet());
				for(JsonElement el : arr)
				{
					if(el.getAsJsonObject().get("id").getAsString().contains("["+set+"]") && el.getAsJsonObject().get("foil").getAsBoolean()==foil){
						filteredArray.add(el);
					}
				}
				
				logger.debug("filtered with set "+set+" and foil : " + foil+ " : " +  filteredArray);
				
				
				if(filteredArray.size()==1) {
					logger.trace("Found 1 item for " + mc + " " + mc.getCurrentSet());
					item = filteredArray.get(0).getAsJsonObject(); 
				}
				else if(filteredArray.size()>1)
				{
					for(JsonElement el : filteredArray)
					{
							if(el.getAsJsonObject().get("id").getAsString().contains(mc.getCurrentSet().getNumber())){
								item=el.getAsJsonObject();
							}
							else if(!el.getAsJsonObject().get("variation").isJsonNull() && mc.getExtra()!=null)
							{
								
								if(mc.getFlavorName()!=null && mc.getFlavorName().equalsIgnoreCase(el.getAsJsonObject().get("variation").getAsString())){
									item=el.getAsJsonObject();
								}
								
								if(mc.getExtra()==MTGCardVariation.SHOWCASE && el.getAsJsonObject().get("variation").getAsString().equals("Showcase")) {
									item=el.getAsJsonObject();
								}
								if(mc.getExtra()==MTGCardVariation.BORDERLESS && el.getAsJsonObject().get("variation").getAsString().equals("Borderless")) {
									item=el.getAsJsonObject();
								}
								if(mc.getExtra()==MTGCardVariation.EXTENDEDART && el.getAsJsonObject().get("variation").getAsString().equals("Extended")) {
									item=el.getAsJsonObject();
								}
								if(mc.getExtra()==MTGCardVariation.JAPANESEALT && el.getAsJsonObject().get("variation").getAsString().equals("Japanese")) {
									item=el.getAsJsonObject();
								}
								if(mc.getExtra()==MTGCardVariation.TIMESHIFTED && (el.getAsJsonObject().get("variation").getAsString().equals("Retro")||el.getAsJsonObject().get("variation").getAsString().equals("Timeshifted"))) {
									item=el.getAsJsonObject();
								}
							}
							else if(el.getAsJsonObject().get("variation").isJsonNull())
							{
								item=el.getAsJsonObject();
							}
					}
			}
		}
		
		logger.debug("Founded " + item);
			
		if(item==null)
		{
			logger.debug("item is null");
			return null;
		}
			
	
		return URLTools.getLocation(WEBSITE+"/q?utf8=%E2%9C%93&query_string="+URLTools.encode(item.get("id").getAsString()))+"#" + getString(FORMAT);
	}
	
	
	public HistoryPrice<MagicCard> getOnlinePricesVariation(MagicCard mc, boolean foil) throws IOException {

		var url ="";//searchUrlFor(mc,foil);
		
		HistoryPrice<MagicCard> historyPrice = new HistoryPrice<>(mc);
		historyPrice.setCurrency(getCurrency());
		historyPrice.setFoil(foil);
		
		
		if(mc==null )
			return historyPrice;
			
			String cardName = RegExUtils.replaceAll(mc.getName(), " ", "+");
			cardName = RegExUtils.replaceAll(cardName, ":", "+");
			cardName = RegExUtils.replaceAll(cardName, "'", "");
			cardName = RegExUtils.replaceAll(cardName, ",", "");

			
			if (cardName.indexOf('/') > -1)
				cardName = cardName.substring(0, cardName.indexOf('/')).trim();

			
			var pfoil="";
			
			if(mc.getCurrentSet().isFoilOnly() || foil)
				pfoil=":Foil";
			
			var extra="";
			var extend="";
			if(mc.isExtendedArt())
				extend="-extended";
			else if(mc.isShowCase())
				extend="-showcase";
			else if(mc.isBorderLess())
				extend="-borderless";
			else if(mc.isTimeshifted() && (mc.getCurrentSet().getId().equalsIgnoreCase("MH2") || mc.getCurrentSet().getId().equalsIgnoreCase("H1R")))
				extend="-retro";
			else if(mc.isTimeshifted())
				extend="-timeshifted";
			else if(mc.isJapanese())
				extend="-japanese";


			if(mc.getFlavorName()!=null)
				extend="-"+mc.getFlavorName().replace(" ", "+");
			
			
			if(mc.getCurrentSet().getId().equals("PUMA")||mc.getCurrentSet().getId().equals("STA")||mc.getCurrentSet().getId().equals("SLD"))
				extend="";
			
			url = WEBSITE + "/price/" + convert(mc.getCurrentSet()) + extra+pfoil+"/" + cardName +extend+ "#" + getString(FORMAT);

		try {
			Document d = URLTools.extractAsHtml(url);
			parsing(d,historyPrice);
			return historyPrice;

		} catch (Exception e) {
			logger.error(e);
			return historyPrice;
		}
	}
	
	
	@Override
	public List<CardShake> getOnlineShakerFor(MagicFormat.FORMATS f) throws IOException {
		List<CardShake> list = new ArrayList<>();
		
		var gameFormat="all";
		if(f!=null)
			gameFormat=f.name();
		
		String urlW = MOVERS_DETAILS + getString(FORMAT) + "/" + gameFormat.toLowerCase() + "/winners/"+ getString(DAILY_WEEKLY);
		String urlL = MOVERS_DETAILS + getString(FORMAT) + "/" + gameFormat.toLowerCase() + "/losers/"+ getString(DAILY_WEEKLY);

		logger.trace("Loading Shake " + urlW + " and " + urlL);
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
				cs.setName(StringUtils.remove(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(2).text(), "(RL)").trim());
				cs.setLink(WEBSITE+e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(2).getElementsByTag("a").get(0).attr("href"));
				cs.setPrice(UITools.parseDouble(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(3).text()));
				cs.setPriceDayChange(UITools.parseDouble(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(0).text()));
				cs.setPercentDayChange(UITools.parseDouble(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(4).text())/100);
				cs.setFoil(false);
				String set = e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(2).getElementsByTag("a").get(0).attr("data-card-id");
				cs.setEd(PluginsAliasesProvider.inst().getReversedSetIdFor(this,StringUtils.substringBetween(set, "[", "]").toUpperCase()));

				list.add(cs);

			}
		} catch (IndexOutOfBoundsException e) {
			logger.error("error parsing ",e);
		}
		return list;

	}
	
	@Override
	protected EditionsShakers getOnlineShakesForEdition(MagicEdition edition) throws IOException {
		var list = new EditionsShakers();
		list.setEdition(edition);
		list.setProviderName(getName());
		list.setDate(new Date());
		
		if(edition==null)
			return list;
		
		var urlEditionChecker = WEBSITE+"/sets/" + PluginsAliasesProvider.inst().getSetIdFor(this,edition)+"/All+Cards";
		
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
						
						if(nameExtra.contains("Extended"))
							cs.setCardVariation(MTGCardVariation.EXTENDEDART);
						else if(nameExtra.contains("Showcase"))
							cs.setCardVariation(MTGCardVariation.SHOWCASE);
						else if(nameExtra.contains("Borderless"))
							cs.setCardVariation(MTGCardVariation.BORDERLESS);
						else if (nameExtra.contains("Timeshifted")||nameExtra.contains("Retro"))
							cs.setCardVariation(MTGCardVariation.TIMESHIFTED);
						else if (nameExtra.contains("Japanese"))
							cs.setCardVariation(MTGCardVariation.JAPANESEALT);
						
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
	public List<CardDominance> getBestCards(MagicFormat.FORMATS f, String filter) throws IOException {

		// spells, creatures, all, lands
		
		String u = WEBSITE + "/format-staples/" + f.name().toLowerCase() + "/full/" + filter;
		
		if(f == MagicFormat.FORMATS.COMMANDER)
			u=WEBSITE + "/format-staples/commander_1v1/full/" + filter;
		
		Document doc = URLTools.extractAsHtml(u);

		logger.debug("get best cards : " + u);
		Elements trs = doc.select("table tr");
		trs.remove(0);
		//trs.remove(0);
		List<CardDominance> ret = new ArrayList<>();
		for (Element e : trs) {
			Elements tds = e.select(MTGConstants.HTML_TAG_TD);
			try {
				int correct = filter.equalsIgnoreCase("lands") ? 1 : 0;

				var d = new CardDominance();
				d.setPosition(Integer.parseInt(tds.get(0).text()));
				d.setCardName(tds.get(1).text());
				d.setDecksPercent(UITools.parseDouble(tds.get(3 - correct).text()));
				d.setPlayers(UITools.parseDouble(tds.get(4 - correct).text()));
				
				ret.add(d);
			} catch (Exception ex) {
				logger.error("Error parsing " + tds, ex);
			}

		}
		return ret;
	}

	private String convert(MagicEdition ed) {
		String editionName = RegExUtils.replaceAll(PluginsAliasesProvider.inst().getSetNameFor(this, ed), " ", "+");
			editionName = RegExUtils.replaceAll(editionName, "'", "");
			editionName = RegExUtils.replaceAll(editionName, ",", "");
			editionName = RegExUtils.replaceAll(editionName, ":", "");
		return editionName;

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
	public Map<String, String> getDefaultAttributes() {
		return Map.of(FORMAT, "paper",
							   TIMEOUT, "0",
							   DAILY_WEEKLY, "wow",
							   SET_EXTRA,"true"
							   );
	}

	@Override
	public String getVersion() {
		return "3.0";
	}



}

package org.magic.api.dashboard.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;

import org.apache.commons.lang3.RegExUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.CardDominance;
import org.magic.api.beans.CardPriceVariations;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;
import org.magic.services.MTGConstants;
import org.magic.tools.URLTools;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstNode;

public class MTGoldFishDashBoard extends AbstractDashBoard {
	private static final String TIMEOUT = "TIMEOUT";
	private static final String URL_MOVERS = "URL_MOVERS";
	private static final String URL_EDITIONS = "URL_EDITIONS";
	private static final String FORMAT = "FORMAT";
	private static final String DAILY_WEEKLY = "DAILY_WEEKLY";
	private static final String WEBSITE = "WEBSITE";
	private Date updateTime;
	private boolean stop;
	private Map<String, String> mapConcordance;


	public MTGoldFishDashBoard() {
		super();
		initConcordance();
	}

	public CardPriceVariations getOnlinePricesVariation(MagicCard mc, MagicEdition me) throws IOException {

		stop = false;
		String url = "";
		CardPriceVariations historyPrice = new CardPriceVariations(mc);
		historyPrice.setCurrency(getCurrency());
		int index = 0;

		if(mc==null && me==null)
			return historyPrice;
		
		
		if (me == null && mc!=null)
			me = mc.getCurrentSet();

		
		
		
		if (mc == null) {
			url = getString(URL_EDITIONS) + replace(me.getId(), false) + "#" + getString(FORMAT);
			index = 6;
		} else {
			String cardName = RegExUtils.replaceAll(mc.getName(), " ", "+");
			cardName = RegExUtils.replaceAll(cardName, "'", "");
			cardName = RegExUtils.replaceAll(cardName, ",", "");

			if (cardName.indexOf('/') > -1)
				cardName = cardName.substring(0, cardName.indexOf('/')).trim();

			String editionName = RegExUtils.replaceAll(me.toString(), " ", "+");
			editionName = RegExUtils.replaceAll(editionName, "'", "");
			editionName = RegExUtils.replaceAll(editionName, ",", "");
			editionName = RegExUtils.replaceAll(editionName, ":", "");

			url = getString(WEBSITE) + "/price/" + convert(editionName) + "/" + cardName + "#" + getString(FORMAT);
			index = 8;

		}

		try {

			logger.debug("get shakes from " + url);

			Document d = URLTools.extractHtml(url);

			Element js = d.getElementsByTag("script").get(index);

			AstNode root = new Parser().parse(js.html(), "", 1);
			root.visit(visitedNode -> {

				if (!stop && visitedNode.toSource().startsWith("d")) {
					String val = visitedNode.toSource();
					val = RegExUtils.replaceAll(val, "d \\+\\= ", "");
					val = RegExUtils.replaceAll(val, "\\\\n", "");
					val = RegExUtils.replaceAll(val, ";", "");
					val = RegExUtils.replaceAll(val, "\"", "");
					String[] res = val.split(",");

					try {
						Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(res[0] + " 00:00");
						if (historyPrice.get(date) == null)
							historyPrice.put(date, Double.parseDouble(res[1]));

					} catch (Exception e) {
						// do nothing
					}
				}

				if (visitedNode.toSource().startsWith("g =")) {
					stop = true;
				}
				return true;
			});
			
			
			return historyPrice;

		} catch (Exception e) {
			logger.error(e);
			return historyPrice;
		}
	}

	@Override
	public List<CardShake> getOnlineShakerFor(MTGFormat f) throws IOException {
		List<CardShake> list = new ArrayList<>();
		
		String gameFormat="all";
		if(f!=null)
			gameFormat=f.name();
		
		String urlW = getString(URL_MOVERS) + getString(FORMAT) + "/" + gameFormat.toLowerCase() + "/winners/"+ getString(DAILY_WEEKLY);
		String urlL = getString(URL_MOVERS) + getString(FORMAT) + "/" + gameFormat.toLowerCase() + "/losers/"+ getString(DAILY_WEEKLY);

		logger.trace("Loading Shake " + urlW + " and " + urlL);
		Document doc = URLTools.extractHtml(urlW);
		Document doc2 = URLTools.extractHtml(urlL);
		try {
			updateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
					.parse(doc.getElementsByClass("timeago").get(0).attr("title"));
		} catch (ParseException e1) {
			logger.error(e1);
		}

		Element table = null;
		try {

			table = doc.select(MTGConstants.HTML_TAG_TABLE).get(0).getElementsByTag("tbody").get(0).appendChild(doc2
					.select(MTGConstants.HTML_TAG_TABLE).get(0).getElementsByTag(MTGConstants.HTML_TAG_TBODY).get(0));// combine
																														// 2
																														// results

			for (Element e : table.getElementsByTag(MTGConstants.HTML_TAG_TR)) {
				CardShake cs = new CardShake();
				cs.setName(
						e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(3).text().replaceAll("\\(RL\\)", "").trim());
				cs.setImg(new URL(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(3).getElementsByTag("a").get(0)
						.attr("data-full-image")));
				cs.setPrice(parseDouble(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(4).text()));
				cs.setPriceDayChange(parseDouble(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(1).text()));
				cs.setPercentDayChange(parseDouble(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(5).text()));
				cs.setProviderName(getName());
				String set = e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(2).getElementsByTag("img").get(0)
						.attr("alt");
				cs.setEd(replace(set, true));

				list.add(cs);

			}
		} catch (IndexOutOfBoundsException e) {
			logger.error(e);
		}
		return list;

	}

	protected List<CardShake> getOnlineShakesForEdition(MagicEdition edition) throws IOException {

		List<CardShake> list = new ArrayList<>();

		if(edition==null)
			return list;
		
		
		String oldID = edition.getId();
		String urlEditionChecker = "";
		
		if (edition.isOnlineOnly())
			urlEditionChecker = getString(URL_EDITIONS) + replace(edition.getId().toUpperCase(), false) + "#online";
		else
			urlEditionChecker = getString(URL_EDITIONS) + replace(edition.getId().toUpperCase(), false) + "#"+ getString(FORMAT);

		logger.trace("Parsing dashboard " + urlEditionChecker);

		try {
			Document doc = URLTools.extractHtml(urlEditionChecker);
			Element table = null;
		
			table = doc.select(MTGConstants.HTML_TAG_TABLE).get(1).getElementsByTag(MTGConstants.HTML_TAG_TBODY).get(0);

			for (Element e : table.getElementsByTag(MTGConstants.HTML_TAG_TR)) {
				CardShake cs = new CardShake();

				cs.setName(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(0).text().replaceAll("\\(RL\\)", "").trim());
				cs.setImg(new URL(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(0).getElementsByTag("a").get(0).attr("data-full-image")));
				cs.setPrice(parseDouble(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(3).text()));
				cs.setPriceDayChange(parseDouble(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(4).text()));
				cs.setPercentDayChange(parseDouble(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(5).text()));
				cs.setPriceWeekChange(parseDouble(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(6).text()));
				cs.setPercentWeekChange(parseDouble(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(7).text()));
				cs.setEd(oldID);
				cs.setDateUpdate(new Date());
				cs.setProviderName(getName());
				notify(cs);
				list.add(cs);
			}
		}
		catch(FileNotFoundException ex){
			logger.error(edition + " is not found");
		}
		catch (IndexOutOfBoundsException e) {
			logger.error("error getting CardShake for " + edition,e);
		}
		
		return list;
	}

	@Override
	public List<CardDominance> getBestCards(MTGFormat f, String filter) throws IOException {

		// spells, creatures, all, lands
		String u = getString(WEBSITE) + "/format-staples/" + f.toString().toLowerCase() + "/full/" + filter;
		Document doc = URLTools.extractHtml(u);

		logger.debug("get best cards : " + u);
		Elements trs = doc.select("table tr");
		trs.remove(0);
		trs.remove(0);
		List<CardDominance> ret = new ArrayList<>();
		for (Element e : trs) {
			Elements tds = e.select(MTGConstants.HTML_TAG_TD);
			try {
				int correct = filter.equalsIgnoreCase("lands") ? 1 : 0;

				CardDominance d = new CardDominance();
				d.setPosition(Integer.parseInt(tds.get(0).text()));
				d.setCardName(tds.get(1).text());
				d.setDominance(Double.parseDouble(tds.get(3 - correct).text().replaceAll("\\%", "")));
				d.setDecksPercent(Double.parseDouble(tds.get(4 - correct).text().replaceAll("\\%", "")));
				d.setPlayers(Double.parseDouble(tds.get(5 - correct).text()));
				ret.add(d);
			} catch (Exception ex) {
				logger.error("Error parsing " + tds, ex);
			}

		}
		return ret;
	}

	private String convert(String editionName) {

		switch (editionName) {
		case "Grand+Prix":
			return "Grand+Prix+Promos";
		case "Prerelease+Events":
			return "Prerelease+Cards";
		case "Champs+and+States":
			return "Champs+Promos";
		case "Ugins+Fate+promos":
			return "Ugins+Fate+Promos";
		case "Magic+Game+Day":
			return "Game+Day+Promos";
		case "Media+Inserts":
			return "Media+Promos";
		case "Judge+Gift+Program":
			return "Judge+Promos";
		case "Friday+Night+Magic":
			return "FNM+Promos";
		case "Arena+League":
			return "Arena+Promos";
		case "Masterpiece+Series+Amonkhet+Invocations":
			return "Amonkhet+Invocations";
		case "Masterpiece+Series+Kaladesh+Inventions":
			return "Kaladesh+Inventions";
		case "You+Make+the+Cube":
			return "Treasure+Chest";
		case "Modern+Masters+2015+Edition":
			return "Modern+Masters+2015";
		case "Guru":
			return "Guru+Lands";
		case "Modern+Masters+2017":
			return "Modern+Masters+2017+Edition";
		case "Ultimate+Box+Topper":
			return "Ultimate+Box+Topper:Foil";
			
			
		default:
			return editionName;
		}
	}

	private void initConcordance() {
		mapConcordance = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		mapConcordance.put("HOP", "PC1");
		mapConcordance.put("TMP", "TE");
		mapConcordance.put("STH", "ST");
		mapConcordance.put("PCY", "PR");
		mapConcordance.put("MIR", "MI");
		mapConcordance.put("UDS", "UD");
		mapConcordance.put("NMS", "NE");
		mapConcordance.put("NEM", "NE");
		mapConcordance.put("ULG", "UL");
		mapConcordance.put("USG", "UZ");
		mapConcordance.put("WTH", "WL");
		mapConcordance.put("ODY", "OD");
		mapConcordance.put("EXO", "EX");
		mapConcordance.put("APC", "AP");
		mapConcordance.put("PLS", "PS");
		mapConcordance.put("INV", "IN");
		mapConcordance.put("MMQ", "MM");
		mapConcordance.put("VIS", "VI");
		mapConcordance.put("7ED", "7E");
		mapConcordance.put("USD", "UD");
		mapConcordance.put("MPS", "MS2");
		mapConcordance.put("P02", "PO2");
		mapConcordance.put("MPS_AKH", "MS3");
		mapConcordance.put("pGRU", "PRM-GUR");
		mapConcordance.put("pMGD", "PRM-GDP");
		mapConcordance.put("pMEI", "PRM-MED");
		mapConcordance.put("pJGP", "PRM-JUD");
		mapConcordance.put("pGPX", "PRM-GPP");
		mapConcordance.put("pFNM", "PRM-FNM");
		mapConcordance.put("pARL", "PRM-ARN");
		mapConcordance.put("PGPX", "PRM-GPP");
		mapConcordance.put("PUMA", "PRM-UMA");
		// p15A

	}

	private String replace(String id, boolean byValue) {

		if (byValue) {
			for (Entry<String, String> entry : mapConcordance.entrySet()) {
				if (Objects.equals(id, entry.getValue())) {
					return entry.getKey();
				}
			}
		} else {
			if (mapConcordance.get(id) != null)
				return mapConcordance.get(id);
		}

		return id;

	}

	private double parseDouble(String number) {
		return Double.parseDouble(number.replaceAll(",", "").replaceAll("%", ""));
	}

	@Override
	public String getName() {
		return "MTGoldFish";
	}

	@Override
	public Date getUpdatedDate() {
		return updateTime;
	}

	@Override
	public String[] getDominanceFilters() {
		return new String[] { "all", "spells", "creatures", "lands" };
	}

	@Override
	public void initDefault() {
		setProperty(URL_MOVERS, "https://www.mtggoldfish.com/movers-details/");
		setProperty(URL_EDITIONS, "https://www.mtggoldfish.com/index/");
		setProperty(WEBSITE, "https://www.mtggoldfish.com/");
		setProperty(FORMAT, "paper");
		setProperty(TIMEOUT, "0");
		setProperty(DAILY_WEEKLY, "wow");

	}

	@Override
	public String getVersion() {
		return "2.0";
	}

}

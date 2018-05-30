package org.magic.api.dashboard.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.magic.api.beans.CardDominance;
import org.magic.api.beans.CardPriceVariations;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MTGPriceDashBoard extends AbstractDashBoard {
	private Date updateTime;

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	
	@Override
	public List<CardShake> getShakerFor(MTGFormat f) throws IOException {
		List<CardShake> list = new ArrayList<>();
		String url = getString("WEBSITE") + "/taneLayout/mtg_price_tracker.jsp?period=" + getString("PERIOD");
		logger.debug("Get Shake for " + url);
		
		Document doc = Jsoup.connect(url).userAgent(MTGConstants.USER_AGENT).get();
		try {
			
			String date = doc.getElementsByClass("span6").get(1).text().replaceAll("Updated:", "")
					.replaceAll("UTC ", "").trim();
			SimpleDateFormat forma = new SimpleDateFormat("E MMMM dd hh:mm:ss yyyy", Locale.ENGLISH);
			updateTime = forma.parse(date);
		} catch (ParseException e1) {
			logger.error(e1);
		}
		String gameFormat="";
		
		if (f.name().equalsIgnoreCase("STANDARD"))
			gameFormat = "Standard";
		else if (f.name().equalsIgnoreCase("MODERN"))
			gameFormat = "Modern";
		else if (f.name().equalsIgnoreCase("VINTAGE"))
			gameFormat = "Vintage";
		else if (f.name().equalsIgnoreCase("LEGACY"))
			gameFormat = "All";

		Element table = doc.getElementById("top50" + gameFormat);
		Element table2 = doc.getElementById("bottom50" + gameFormat);

		try {

			for (Element e : table.select(MTGConstants.HTML_TAG_TR)) {
				CardShake cs = new CardShake();
				cs.setName(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(0).text().trim());
				cs.setPrice(parseDouble(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(2).text()));
				cs.setPriceDayChange(parseDouble(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(4).text()));

				String set = e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(1).text();
				set = set.replaceAll("_\\(Foil\\)", "");
				cs.setEd(getCodeForExt(set));

				list.add(cs);

			}

			for (Element e : table2.select(MTGConstants.HTML_TAG_TR)) {
				CardShake cs = new CardShake();
				cs.setName(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(0).text().trim());
				cs.setPrice(parseDouble(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(2).text()));
				cs.setPriceDayChange(parseDouble(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(4).text()));

				String set = e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(1).text();
				set = set.replaceAll("_\\(Foil\\)", "");
				cs.setEd(getCodeForExt(set));
				list.add(cs);
			}
		} catch (Exception e) {
			logger.error("error retrieve cardshake for " + gameFormat, e);
		}
		return list;
	}

	private double parseDouble(String number) {
		return Double.parseDouble(number.replaceAll("\\$", ""));
	}

	private String getCodeForExt(String name) {
		try {
			for (MagicEdition ed : MTGControler.getInstance().getEnabledCardsProviders().loadEditions())
				if (ed.getSet().toUpperCase().contains(name.toUpperCase()))
					return ed.getId();
		} catch (Exception e) {
			logger.error(e);
		}

		return name;
	}

	@Override
	public List<CardShake> getShakeForEdition(MagicEdition edition) throws IOException {

		String name = convert(edition.getSet()).replaceAll(" ", "_");

		String url = "http://www.mtgprice.com/spoiler_lists/" + name;
		logger.debug("get Prices for " + name + " " + url);

		Document doc = Jsoup.connect(url).userAgent(MTGConstants.USER_AGENT).get();

		Element table = doc.getElementsByTag("body").get(0).getElementsByTag("script").get(2);

		List<CardShake> list = new ArrayList<>();
		String data = table.html();
		data = data.substring(data.indexOf('['), data.indexOf(']') + 1);
		JsonElement root = new JsonParser().parse(data);
		JsonArray arr = root.getAsJsonArray();
		for (int i = 0; i < arr.size(); i++) {
			JsonObject card = arr.get(i).getAsJsonObject();
			CardShake shake = new CardShake();

			shake.setName(card.get("name").getAsString());
			shake.setEd(edition.getId());
			shake.setPrice(card.get("fair_price").getAsDouble());
			try {
				shake.setPercentDayChange(card.get("percentageChangeSinceYesterday").getAsDouble());
			} catch (Exception e) {
				// do nothing
			}
			try {
				shake.setPercentWeekChange(card.get("percentageChangeSinceOneWeekAgo").getAsDouble());
			} catch (Exception e) {
				// do nothing
			}
			shake.setPriceDayChange(card.get("absoluteChangeSinceYesterday").getAsDouble());
			shake.setPriceWeekChange(card.get("absoluteChangeSinceOneWeekAgo").getAsDouble());

			list.add(shake);
		}

		return list;
	}

	private String convert(String name) {
		if (name.equalsIgnoreCase("Limited Edition Alpha"))
			return "Alpha";

		return name;
	}

	@Override
	public CardPriceVariations getPriceVariation(MagicCard mc, MagicEdition me) throws IOException {

		CardPriceVariations historyPrice = new CardPriceVariations(mc);

		String name = "";

		if (mc == null)
		{
			logger.error("no magiccard defined");
			return historyPrice;
		}

		name = mc.getName().replaceAll(" ", "_");

		String edition = "";

		if (me == null)
			edition = mc.getCurrentSet().getSet();
		else
			edition = me.getSet();

		edition = edition.replaceAll(" ", "_");

		String url = "http://www.mtgprice.com/sets/" + edition + "/" + name;
		Document d = Jsoup.connect(url).userAgent(MTGConstants.USER_AGENT).get();

		logger.debug("get Prices for " + name + " " + url);

		Element js = d.getElementsByTag("body").get(0).getElementsByTag("script").get(29);

		String html = js.html();
		html = html.substring(html.indexOf("[[") + 1, html.indexOf("]]") + 1);

		Pattern p = Pattern.compile("\\[(.*?)\\]");
		Matcher m = p.matcher(html);
		while (m.find()) {

			Date date = new Date(Long.parseLong(m.group(1).split(",")[0]));
			Double val = Double.parseDouble(m.group(1).split(",")[1]);

			historyPrice.put(date, val);
		}
		return historyPrice;
	}

	@Override
	public String getName() {
		return "MTGPrice";
	}

	@Override
	public Date getUpdatedDate() {
		return updateTime;
	}

	@Override
	public List<CardDominance> getBestCards(MTGFormat f, String filter) throws IOException {
		return new ArrayList<>();
	}

	@Override
	public String[] getDominanceFilters() {
		return new String[] { "" };
	}

	@Override
	public void initDefault() {
		setProperty("PERIOD", "WEEKLY");
		setProperty("WEBSITE", "http://www.mtgprice.com");
	}

	@Override
	public String getVersion() {
		return "0.1";
	}

}

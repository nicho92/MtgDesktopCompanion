package org.magic.api.dashboard.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.EditionsShakers;
import org.magic.api.beans.HistoryPrice;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;
import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

public class MTGPriceDashBoard extends AbstractDashBoard {
	private static final String WEBSITE = "https://www.mtgprice.com";
	private Date updateTime;
	private final Pattern p;

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	public MTGPriceDashBoard() {
		p = Pattern.compile("\\[(.*?)\\]");
	}

	@Override
	public List<CardShake> getOnlineShakerFor(MTGFormat.FORMATS f) throws IOException {
		final var list = new ArrayList<CardShake>();
		final var url = WEBSITE + "/taneLayout/mtg_price_tracker.jsp?period=" + getString("PERIOD");
		final var doc = URLTools.extractAsHtml(url);
		try {

			final var date = doc.getElementsByClass("span6").get(1).text().replace("Updated:", "").replace("UTC ", "").trim();
			final var forma = new SimpleDateFormat("E MMMM dd hh:mm:ss yyyy", Locale.ENGLISH);
			updateTime = forma.parse(date);
		} catch (final ParseException e1) {
			logger.error(e1);
		}
		var gameFormat = MTGFormat.toString(f);
		if (f == MTGFormat.FORMATS.LEGACY || f == MTGFormat.FORMATS.COMMANDER) {
		    gameFormat = "All";
		}

		final var table = doc.getElementById("top50" + gameFormat);
		final var table2 = doc.getElementById("bottom50" + gameFormat);

		try {

			if (table != null) {
			    for (final Element e : table.select(MTGConstants.HTML_TAG_TR)) {
			    	final var cs = new CardShake();
			    	cs.setName(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(0).text().trim());
			    	cs.setPrice(UITools.parseDouble(
			    			e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(2).text().replaceAll("\\$", "")));
			    	cs.setPriceDayChange(UITools.parseDouble(
			    			e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(4).text().replaceAll("\\$", "")));
			    	cs.setProviderName(getName());
			    	var set = e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(1).text();
			    	cs.setFoil(set.contains("(Foil)"));
			    	set = set.replace("_\\(Foil\\)", "");
			    	cs.setEd(getCodeForExt(set));
			    	list.add(cs);
			    	notify(cs);
			    }
			}

			if (table2 != null) {
			    for (final Element e : table2.select(MTGConstants.HTML_TAG_TR)) {
			    	final var cs = new CardShake();
			    	cs.setName(e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(0).text().trim());
			    	cs.setPrice(UITools.parseDouble(
			    			e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(2).text().replaceAll("\\$", "")));
			    	cs.setPriceDayChange(UITools.parseDouble(
			    			e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(4).text().replaceAll("\\$", "")));
			    	cs.setProviderName(getName());
			    	var set = e.getElementsByTag(MTGConstants.HTML_TAG_TD).get(1).text();
			    	cs.setFoil(set.contains("(Foil)"));
			    	set = set.replace("_\\(Foil\\)", "");
			    	cs.setEd(getCodeForExt(set));
			    	list.add(cs);
			    	notify(cs);
			    }
			}
		} catch (final Exception e) {
			logger.error("error retrieve cardshake for {}", gameFormat, e);
		}
		return list;
	}

	private String getCodeForExt(String name) {
		try {
			for (final MTGEdition ed : getEnabledPlugin(MTGCardsProvider.class).listEditions()) {
			    if (ed.getSet().toUpperCase().contains(name.toUpperCase())) {
			        return ed.getId();
			    }
			}
		} catch (final Exception e) {
			logger.error(e);
		}

		return name;
	}

	@Override
	protected EditionsShakers getOnlineShakesForEdition(MTGEdition edition) throws IOException {

		final String name = convert(edition.getSet()).replace(" ", "_");

		final String url = WEBSITE + "/spoiler_lists/" + name;
		logger.debug("get Prices for {} at {}", name, url);

		final Document doc = URLTools.extractAsHtml(url);

		final Element table = doc.getElementsByTag("body").get(0).getElementsByTag("script").get(2);

		final var list = new EditionsShakers();
		list.setProviderName(getName());
		list.setEdition(edition);
		list.setDate(new Date());

		var data = table.html();
		data = StringUtils.substringBetween(data, "[", "]");
		final var root = URLTools.toJson(data);
		final var arr = root.getAsJsonArray();
		for (var i = 0; i < arr.size(); i++) {
			final var card = arr.get(i).getAsJsonObject();
			final var shake = new CardShake();
			shake.setProviderName(getName());
			shake.setName(card.get("name").getAsString());
			shake.setEd(edition.getId());
			shake.setPrice(card.get("fair_price").getAsDouble());
			try {
				shake.setPercentDayChange(card.get("percentageChangeSinceYesterday").getAsDouble());
			} catch (final Exception _) {
				// do nothing
			}
			try {
				shake.setPercentWeekChange(card.get("percentageChangeSinceOneWeekAgo").getAsDouble());
			} catch (final Exception _) {
				// do nothing
			}
			shake.setPriceDayChange(card.get("absoluteChangeSinceYesterday").getAsDouble());
			shake.setPriceWeekChange(card.get("absoluteChangeSinceOneWeekAgo").getAsDouble());
			notify(shake);
			list.addShake(shake);
		}

		return list;
	}

	private String convert(String name) {

		if (name == null) {
		    return "";
		}

		if (name.equalsIgnoreCase("Limited Edition Alpha")) {
		    return "Alpha";
		}

		return name;
	}

	@Override
	protected HistoryPrice<MTGEdition> getOnlinePricesVariation(MTGEdition ed) throws IOException {
		return new HistoryPrice<>(ed);
	}

	@Override
	public HistoryPrice<MTGCard> getOnlinePricesVariation(MTGCard mc, boolean foil) throws IOException {

		final HistoryPrice<MTGCard> historyPrice = new HistoryPrice<>(mc);
		historyPrice.setFoil(foil);

		if (mc == null) {
			logger.error("no magiccard defined");
			return historyPrice;
		}

		var name = "";
		name = mc.getName().replace(" ", "_");

		var edition = "";
		edition = mc.getEdition().getSet();
		edition = edition.replace(" ", "_");

		final String url = WEBSITE + "/sets/" + edition + "/" + name;
		final var d = URLTools.extractAsHtml(url);

		logger.debug("get Prices for {} at {}", name, url);

		final Element js = d.getElementsByTag("body").get(0).getElementsByTag("script").get(29);

		String html = js.html();
		html = StringUtils.substringBetween(html, "[[", "]]");

		final var m = p.matcher(html);
		while (m.find()) {

			final var date = new Date(Long.parseLong(m.group(1).split(",")[0]));
			final var val = Double.parseDouble(m.group(1).split(",")[1]);

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
	public Map<String, MTGProperty> getDefaultAttributes() {

		final var m = super.getDefaultAttributes();
		m.put("PERIOD", new MTGProperty("WEEKLY", "choose periode for cards update WEEKLY or DAILY"));

		return m;
	}

	@Override
	public String getVersion() {
		return "0.2";
	}

	@Override
	public HistoryPrice<MTGSealedProduct> getOnlinePricesVariation(MTGSealedProduct packaging) throws IOException {
		return new HistoryPrice<>(packaging);
	}

}

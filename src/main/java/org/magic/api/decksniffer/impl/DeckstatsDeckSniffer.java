package org.magic.api.decksniffer.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.beans.technical.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class DeckstatsDeckSniffer extends AbstractDeckSniffer {

    private static final String MAX_PAGE = "MAX_PAGE";
    private Map<Integer, String> cacheColor;
    private HashMap<String, Integer> formats;
    private static final String BASE_URL="https://deckstats.net";

    public DeckstatsDeckSniffer() {

	cacheColor = new HashMap<>();
	formats = new HashMap<>();
	initcache();
    }

    private void initcache() {
	cacheColor.put(1, "{W}");
	cacheColor.put(2, "{U}");
	cacheColor.put(3, "{W}{U}");
	cacheColor.put(4, "{B}");
	cacheColor.put(5, "{W}{B}");
	cacheColor.put(6, "{U}{B}");
	cacheColor.put(7, "{W}{U}{B}");
	cacheColor.put(8, "{R}");
	cacheColor.put(9, "{W}{R}");
	cacheColor.put(10, "{U}{R}");
	cacheColor.put(11, "{W}{U}{R}");
	cacheColor.put(12, "{B}{R}");
	cacheColor.put(13, "{W}{B}{R}");
	cacheColor.put(14, "{U}{B}{R}");
	cacheColor.put(15, "{W}{U}{B}{R}");
	cacheColor.put(16, "{G}");
	cacheColor.put(17, "{W}{G}");
	cacheColor.put(18, "{U}{G}");
	cacheColor.put(19, "{W}{U}{G}");
	cacheColor.put(20, "{B}{R}");
	cacheColor.put(21, "{W}{B}{G}");
	cacheColor.put(22, "{U}{B}{G}");
	cacheColor.put(23, "{W}{U}{B}{G}");
	cacheColor.put(24, "{R}{G}");
	cacheColor.put(25, "{W}{R}{G}");
	cacheColor.put(26, "{U}{R}{G}");
	cacheColor.put(27, "{W}{U}{R}{G}");
	cacheColor.put(28, "{B}{R}{G}");
	cacheColor.put(29, "{W}{B}{R}{G}");
	cacheColor.put(30, "{U}{B}{R}{G}");
	cacheColor.put(31, "{W}{U}{B}{R}{G}");

	formats.put("casual", 1);
	formats.put("standard", 6);
	formats.put("modern", 4);
	formats.put("EDH-Commander", 10);
	formats.put("Oathbreaker", 17);
	formats.put("Pioneer", 18);
	formats.put("Explorer", 22);
	formats.put("Historic", 19);
	formats.put("Brawl", 16);
	formats.put("Legacy", 3);
	formats.put("vintage", 2);
	formats.put("pauper", 9);
	formats.put("highlander", 7);
	formats.put("tiny-leaders", 12);
	formats.put("frontier", 13);
	formats.put("peasant", 11);
	formats.put("extended", 5);
	formats.put("cube", 8);
	formats.put("limited", 14);
	formats.put("other", 9999);

    }

    @Override
    public String[] listFilter() {
	return formats.keySet().toArray(new String[formats.keySet().size()]);

    }

    @Override
    public MTGDeck getDeck(RetrievableDeck info) throws IOException {
	var deck = info.toBaseDeck();

	logger.debug("get deck {}", info.getUrl());
	var d = URLTools.extractAsHtml(info.getUrl().toString());
	var jsonContent = URLTools.toJson(d.selectFirst("script[data-page=app]").html()).getAsJsonObject();
	var props = jsonContent.get("props").getAsJsonObject();

	for(var entry : props.get("entries").getAsJsonArray())
	{
	    var obj = entry.getAsJsonObject();
	    var zone = obj.get("zone").getAsString();
	    var card = MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(obj.get("printing").getAsJsonObject().get("scryfall_id").getAsString());
	    var qty = obj.get("amount").getAsInt();

	    notify(card);
	    switch (zone) {
	    case "commander":  deck.setCommander(card);break;
	    case "main": deck.getMain().put(card, qty);break;
	    case "sideboard": deck.getSideBoard().put(card, qty);break;
	    case "maybeboard": break;
	    default : logger.warn("no side for {}",zone);break;
	    }
	}
	return deck;
    }


    @Override
    public boolean hasCardFilter() {
	return true;
    }

    private String getCardId(MTGCard card) throws IOException
    {
	var content = URLTools.extractAsJson(BASE_URL+"/api/cards/autocomplete?q="+URLTools.encode(card.getName())).getAsJsonObject().get("results").getAsJsonArray();
	var opt = content.asList().stream().filter(je->je.getAsJsonObject().get("scryfall_id").getAsString().equals(card.getScryfallId())).findFirst();

	if(opt.isPresent())
	    return opt.get().getAsJsonObject().get("idcards").getAsString();
	else
	    return content.get(0).getAsJsonObject().get("idcards").getAsString();
    }



    @Override
    public List<RetrievableDeck> getDeckList(String filter, MTGCard mc) throws IOException {

	List<RetrievableDeck> list = new ArrayList<>();

	for (var i = 1; i <= getInt(MAX_PAGE); i++) {

	    var q = RequestBuilder.build().get().newClient().url(BASE_URL+"/api/explore")
		    .addContent("format_id", "" + formats.get(filter))
		    .addContent("sort", getString("TYPES_ORDER"))
		    .addContent("offset", "" + i)
		    .addContent("limit", "100");

	    if (mc != null)
		q.addContent("card_ids[]", getCardId(mc));

	    var d = q.toJson();

	    for (var item : d.getAsJsonObject().get("items").getAsJsonArray()) {
		var deck = new RetrievableDeck();

		var cont = item.getAsJsonObject();
		deck.setName(cont.get("name").getAsString());
		try {
		    deck.setUrl(new URI(BASE_URL+cont.get("url").getAsString()));
		} catch (URISyntaxException _) {
		    deck.setUrl(null);
		}
		deck.setAuthor(cont.get("owner_name").getAsString());
		deck.setColor(cacheColor.get(cont.get("colors").getAsInt()));
		deck.setDescription(UITools.formatDate(new Date(cont.get("updated").getAsLong()*1000)));
		list.add(deck);
	    }
	}
	return list;
    }

    @Override
    public String getName() {
	return "DeckStats";
    }

    @Override
    public Map<String, MTGProperty> getDefaultAttributes() {
	var m = super.getDefaultAttributes();
	m.put(MAX_PAGE, MTGProperty.newIntegerProperty("2", "number of page to query", 1, 10));
	m.put("TYPES_ORDER",
		new MTGProperty("updated", "How to sort search results", "updated", "likes", "views", "price", "name"));
	return m;
    }

    @Override
    public String getVersion() {
	return "4.0";
    }

}

package org.magic.api.decksniffer.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.beans.technical.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.MTG;

public class DeckstatsDeckSniffer extends AbstractDeckSniffer {

	private static final String MAX_PAGE = "MAX_PAGE";
	private Map<Integer, String> cacheColor;
	private HashMap<String,Integer>formats;


	public DeckstatsDeckSniffer() {
		super();
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
		formats.put("cube",8);
		formats.put("limited",14);
		formats.put("other",9999);
		
	}

	
	@Override
	public String[] listFilter() {
		return formats.keySet().toArray(new String[formats.keySet().size()]);
		
		
	}
	
	@Override
	public MTGDeck getDeck(RetrievableDeck info) throws IOException {
		//

		var deck = info.toBaseDeck();

		logger.debug("get deck {}",info.getUrl());
		var d = URLTools.extractAsHtml(info.getUrl().toString());

		if (!d.select("div#deck_overview_info").isEmpty())
			deck.setDescription(d.select("div#deck_overview_info").select("div.deck_text_editable_container").text());
		else
			deck.setDescription("From " + getName() +" at " + info.getUrl());

		for (Element a : d.select("a.deck_tags_list_tag"))
			deck.getTags().add(a.text());

		var e = d.select("textarea#deck_code");
		var content= e.html();

		var arr  = content.split("\n");

		arr = ArrayUtils.remove(arr, 0); //remove deck name
		arr = ArrayUtils.remove(arr, 0); //remove //main
		
		var p =Pattern.compile(aliases.getRegexFor(this, "default"));
		
		for(String s : arr)
		{
			if(s.isEmpty())
				continue;
			
			try {
					if(s.startsWith("SB: "))
					{
						s=s.replaceFirst("SB: ", "").trim();
						read(s,p,deck.getSideBoard());
					}
					else
					{
						read(s,p,deck.getMain());
					}
			}
			catch(Exception ex)
			{
				logger.error("error parsing -> {} : {}",s,ex.getMessage());
			}
		}
		return deck;
	}

	private void read(String s, Pattern regex, Map<MTGCard, Integer> map) {
			
		var m = regex.matcher(s);
		if(m.find())
		{
			var qty = Integer.parseInt(m.group(1));
			
			MTGCard mc = null;
			
			if(m.group(3)!=null)
			{
				var idSet = m.group(3).split("#")[0];
				var number = m.group(3).split("#")[1];
				try {
					mc = MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByNumber(number, idSet);
				} catch (IOException e) {
					logger.error("Error getting card by number {} {} : {}",idSet,number,e.getMessage() );
					return;
				}
			}
			else
			{
				try {
					mc = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(m.group(4), null, true).get(0);
				} catch (IOException e) {
					logger.error("Error getting card by name {} : {}",m.group(4),e.getMessage() );
					return;
				}
			}
			map.put(mc, qty);
			notify(mc);
		}
		
		
	}
	
	@Override
	public boolean hasCardFilter() {
		return true;
	}
	

	@Override
	public List<RetrievableDeck> getDeckList(String filter, MTGCard mc) throws IOException {

		List<RetrievableDeck> list = new ArrayList<>();
		
		for (var i = 1; i <= getInt(MAX_PAGE); i++) {
			
			var q = RequestBuilder.build().get().newClient()
									.url("https://deckstats.net/decks/search")
									.addContent("search_format",""+formats.get(filter))
									.addContent("lng","fr")
									.addContent("search_order",getString("TYPES_ORDER")+",desc")
									.addContent("page",""+i);
			
				if(mc!=null)
					q.addContent("search_cards[]",mc.getName());
			
			
			var d = q.toHtml();
			
			Elements e = d.select("tr.deck_row");

			for (Element cont : e) {
				var deck = new RetrievableDeck();
				var info = cont.select("a").get(0);
				var idColor = cont.select("img").get(0).attr("src");
				idColor = idColor.substring(idColor.lastIndexOf('/') + 1, idColor.lastIndexOf('.'));
				var name = info.text();
				var url = info.attr("href") + "/fr?get_code=1&code_type=bb_deck&code_extended=0&code_html_nl=off";
				var auteur = cont.select("a").get(1).text();

				deck.setName(name);
				try {
					deck.setUrl(new URI(url));
				} catch (URISyntaxException _) {
					deck.setUrl(null);
				}
				deck.setAuthor(auteur);
				deck.setColor(cacheColor.get(Integer.parseInt(idColor)));

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
		m.put("TYPES_ORDER", new MTGProperty("updated", "How to sort search results","updated","likes","views","price","name"));
		return m;
	}

	@Override
	public String getVersion() {
		return "3.0";
	}




}

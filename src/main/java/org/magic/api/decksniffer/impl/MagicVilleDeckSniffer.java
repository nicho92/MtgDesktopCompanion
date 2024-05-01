package org.magic.api.decksniffer.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.technical.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.MTG;

public class MagicVilleDeckSniffer extends AbstractDeckSniffer {


	private String baseUrl="https://www.magic-ville.com/fr/decks/";
	private HashMap<String,String> mapCodes;

	public MagicVilleDeckSniffer()
	{
		mapCodes = new HashMap<>();
		mapCodes.put("PIONEER",  "resultats?data=1&dci=TP&tour_cur=1&tour_orig=1");
		mapCodes.put("MODERN",  "resultats?data=1&dci=TM&tour_cur=1&tour_orig=1");
		mapCodes.put("STANDARD", "resultats?data=1&dci=T2&tour_cur=1&tour_orig=1");
		mapCodes.put("PEASANT",  "resultats?data=1&alt=Peasant");
		mapCodes.put("COMMANDER",  "resultats?data=1&dci=1vs1&tour_orig=1");
		mapCodes.put("LEGACY",  "resultats?data=1&dci=T15&tour_cur=1&tour_orig=1");
		mapCodes.put("VINTAGE",  "resultats?data=1&dci=T1&tour_cur=1&tour_orig=1");
		mapCodes.put("Brawl",  "resultats?data=1&alt=Brawl");
		mapCodes.put("FUN",  "resultats?data=1&fun=1");
		mapCodes.put("TINY LEADERS",  "resultats?data=1&alt=TinyLeaders");
		mapCodes.put("EDH Peasant",  "resultats?data=1&alt=EDHPeasant");
	}

	@Override
	public String[] listFilter() {
		return mapCodes.keySet().stream().toArray(String[]::new);
	}

	@Override
	public MTGDeck getDeck(RetrievableDeck info) throws IOException {
		var doc = RequestBuilder.build().setClient(URLTools.newClient()).get().url(info.getUrl()).toHtml();
		var urlimport = baseUrl+doc.select("div.lil_menu > a[href^=dl_mws]").first().attr("href");
		var content = RequestBuilder.build().setClient(URLTools.newClient()).get().url(urlimport).toContentString();
		var imp = MTG.getPlugin("MagicWorkStation", MTGCardsExport.class);
		try {
			imp.addObserver(listObservers().get(0));
		}
		catch(IndexOutOfBoundsException e)
		{
			logger.warn("error adding current observer to {}" ,imp);
		}

		content = content.replace("<br />","");
		var d = imp.importDeck(content, info.getName());
		d.setCreationDate(new Date());
		d.setDateUpdate(new Date());
		d.setDescription(getName() +" at  " + info.getUrl());

		imp.removeObservers();

		return d;
	}

	@Override
	public List<RetrievableDeck> getDeckList(String filter) throws IOException {

		List<RetrievableDeck> ret = new ArrayList<>();
		int maxPage=getInt("MAX_PAGE");


		for(var currPage=0;currPage<maxPage;currPage++)
		{
			var d = RequestBuilder.build().get().setClient(URLTools.newClient())
						.url(baseUrl + mapCodes.get(filter)+"&pointeur="+currPage)
						.toHtml();
				Elements trs = d.select("tr[height=33]");
				for(Element tr : trs)
				{
					Elements tds = tr.select("td");


					try {
						var de = new RetrievableDeck();
						de.setName(tds.get(0).text());
						de.setUrl(new URI(baseUrl+tds.get(0).select("a").attr("href")+"&decklanglocal=eng"));
						de.setAuthor(tds.get(1).text());
						var temp = new StringBuilder();
						tds.get(3).select("img").forEach(e->{
							var img = e.attr("src");
							img = img.substring(img.indexOf("png/")+4,img.indexOf(".png"));

							if(img.length()>1)
								img = img.substring(1);

							if(img.equals("W")||img.equals("U")||img.equals("B")||img.equals("G")||img.equals("R"))
								temp.append("{").append(img).append("}");
						});
						de.setColor(temp.toString());
						de.setDescription(tds.get(4).text());
						ret.add(de);
					} catch (URISyntaxException e) {
						logger.error("error for url {}",baseUrl+tds.get(0).select("a").attr("href"));
					}
				}
		}
		return ret;
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("MAX_PAGE", "1");
	}

	@Override
	public String getName() {
		return "Magic-Ville";
	}

}

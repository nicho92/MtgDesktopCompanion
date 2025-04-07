package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.jsoup.nodes.Document;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.MTGControler;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;


public class UndergroundSeaPricer extends AbstractPricesProvider {

	private static final String PROXYCARDSLIST_HTML = "/proxycardslist.html";
	private static final String BASE_URL="http://www.usmtgproxy.com/";
	private String priceToken="price = ";
	private String editionToken="cardVersion =";

	private MTGPrice mp = null;

	@Override
	public String getName() {
		return "UnderGroundSea";
	}

	private String getUrl()
	{
		var uploadPath = "wp-content/uploads/";


		var month = String.format("%02d",Calendar.getInstance().get(Calendar.MONTH)+1);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String url = BASE_URL + uploadPath +year+"/"+month+PROXYCARDSLIST_HTML;



		if(!URLTools.isCorrectConnection(url))
		{
			month=String.format("%02d",Calendar.getInstance().get(Calendar.MONTH));
			url = BASE_URL + uploadPath+year+"/"+month+PROXYCARDSLIST_HTML;
		}

		if(!URLTools.isCorrectConnection(url))
		{
			month=String.format("%02d",Calendar.getInstance().get(Calendar.MONTH)-1);
			url = BASE_URL + uploadPath+year+"/"+month+PROXYCARDSLIST_HTML;
		}

		return url;
	}


	@Override
	protected List<MTGPrice> getLocalePrice(MTGCard card) throws IOException {

		Document d = URLTools.extractAsHtml(getUrl());
		AstNode root = new Parser().parse(d.select("script").get(1).html(), "", 1);
		List<MTGPrice> ret = new ArrayList<>();

		String url = getUrl();

		root.visit(visitedNode -> {
			if(visitedNode.getType()==Token.NEW)
			{
				mp = new MTGPrice();
				mp.setCardData(card);
				mp.setSite(getName());
				mp.setUrl(url);
				mp.setQuality(EnumCondition.PROXY);
				mp.setCurrency(Currency.getInstance("USD"));
				mp.setLanguage("EN");
				mp.setCountry("China");
				mp.setCountry(Locale.CHINA.getDisplayCountry(MTGControler.getInstance().getLocale()));
			}

			if(visitedNode.getType()==Token.EXPR_RESULT)
			{
				String value = visitedNode.toSource();

				if(value.startsWith("cardslist") || value.startsWith("createTable") || value.startsWith("window.onload") || value.startsWith("confirmBtn") || value.startsWith("infos"))
					return false;

				String content = visitedNode.toSource().substring(visitedNode.toSource().indexOf('.')+1).trim();

				if(content.toLowerCase().contains(card.getName().toLowerCase()) && mp !=null)
				{
					ret.add(mp);
					mp.setFoil(content.contains("(foil)"));
				}

				if(content.startsWith(priceToken) && mp !=null)
				{
					content = content.substring(content.indexOf(priceToken)+priceToken.length()).replace(";", "").trim();
					mp.setValue(UITools.parseDouble(content));
				}

				if(content.startsWith(editionToken) && mp !=null)
				{
					content = content.substring(content.indexOf(editionToken)+editionToken.length()).replace(";", "").replace("\"", "").trim();
					mp.setSeller(content);
				}

				if(content.startsWith("cardID") && mp !=null)
				{
					content = content.substring(content.indexOf(editionToken)+editionToken.length()).replace(";", "").replace("\"", "").trim();
					mp.setShopItem(content);
				}

			}

			return true;
		});

		logger.info("{} found {} items",getName(),ret.size());

		return ret;
	}

}

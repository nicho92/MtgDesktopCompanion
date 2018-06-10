package org.magic.api.pricers.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.magic.services.MTGConstants;
import org.magic.tools.URLTools;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class ChannelFireballPricer extends AbstractMagicPricesProvider {

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}


	@Override
	public List<MagicPrice> getPrice(MagicEdition me, MagicCard card) throws IOException {

		String keyword = card.getName();
		String url = getString("URL");

		keyword = URLEncoder.encode(keyword, MTGConstants.DEFAULT_ENCODING);

		setProperty("KEYWORD", keyword);

		if (me != null)
			keyword += "&setname=" + URLEncoder.encode(me.getSet(), MTGConstants.DEFAULT_ENCODING);

		String link = url.replaceAll("%CARDNAME%", keyword);

		logger.info(getName() + " Looking for price " + link);
		JsonReader reader = new JsonReader(new InputStreamReader(URLTools.openConnection(link).getInputStream()));
		JsonElement root = new JsonParser().parse(reader);

		String value = root.getAsJsonArray().get(0).getAsString();

		MagicPrice mp = new MagicPrice();
		mp.setUrl("http://store.channelfireball.com/products/search?query="
				+ URLEncoder.encode(card.getName(), MTGConstants.DEFAULT_ENCODING));
		mp.setSite(getName());
		mp.setCurrency("USD");
		mp.setValue(Double.parseDouble(value.substring(1).replaceAll(",", "")));

		ArrayList<MagicPrice> list = new ArrayList<>();
		list.add(mp);

		logger.info(getName() + " found " + list.size() + " item(s)");

		return list;
	}

	@Override
	public String getName() {
		return "Channel Fireball";
	}

	@Override
	public void alertDetected(List<MagicPrice> p) {
		// do nothing

	}

	@Override
	public void initDefault() {
		setProperty("MAX", "5");
		setProperty("URL", "http://magictcgprices.appspot.com/api/cfb/price.json?cardname=%CARDNAME%");
		setProperty("WEBSITE", "http://store.channelfireball.com/");
		
		setProperty("KEYWORD", "");

	}


}

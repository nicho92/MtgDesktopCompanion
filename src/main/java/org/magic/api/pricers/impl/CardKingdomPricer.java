package org.magic.api.pricers.impl;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;
import static com.jayway.jsonpath.JsonPath.parse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jsoup.select.Elements;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.exports.impl.CardKingdomCardExport;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.network.URLTools;
import org.magic.services.tools.CardKingdomTools;
import org.magic.services.tools.Chrono;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.UITools;

import com.jayway.jsonpath.DocumentContext;

public class CardKingdomPricer extends AbstractPricesProvider {

	private static final String API_URI="https://api.cardkingdom.com/api/pricelist";
	private static final String WEB_URI="https://www.cardkingdom.com";
	private File jsonFile;
	private DocumentContext cont;

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	private void init() throws IOException
	{
		var c = new Chrono();
		c.start();
		cont = parse(jsonFile);
		logger.debug("Init {} dataFile in {}s",jsonFile,c.stop());
	}


	private String getUrlFor(MTGCard mc,boolean foil) throws IOException
	{
		if(!jsonFile.exists()|| FileTools.daysBetween(jsonFile)>1) {
			logger.debug("{} is not present or out of date. Downloading new one",jsonFile);
			FileTools.saveFile(jsonFile, URLTools.extractAsJson(API_URI).toString());
		}

		if(cont==null)
			init();


        var name = CardKingdomTools.getCKFormattedName(mc);
        var ed = CardKingdomTools.getCKFormattedSet(mc);
		
		var filtres =where("scryfall_id").is(mc.getScryfallId()).and("is_foil").is(String.valueOf(foil));

		if(mc.isToken())
		{
			name = name + " Token";
			ed = ed.replace(" Tokens", "");
			ed = aliases.getSetNameFor(new CardKingdomCardExport() , ed);
			filtres = where("name").is(name)
					  .and("edition").is(ed)
					  .and("is_foil").is(String.valueOf(foil));
		}


		var cardFilter = filter(filtres);
		logger.debug("Reading file {} with {} ",jsonFile,cardFilter );
		List<Map<String, Object>> arr = cont.read("$.data[?]",cardFilter);

		try {

			if(arr.size()>1) {
				logger.warn(" found multiples values for {} : {}", mc,arr);
			}
			return arr.get(0).get("url").toString();
		}
		catch(Exception _)
		{
			logger.error("No product found for {} foil={}",mc,foil) ;
		}
		return null;
	}


	public CardKingdomPricer() {
		jsonFile=new File(MTGConstants.DATA_DIR,"mtgkingdom.json");
	}

	@Override
	public List<MTGPrice> getLocalePrice(MTGCard card) throws IOException {

		var ret = getPrices(card,false);
		ret.addAll(getPrices(card, true));
		return ret;

	}



	public List<MTGPrice> getPrices(MTGCard card,boolean foil) throws IOException {

		List<MTGPrice> list = new ArrayList<>();
		var productUri =getUrlFor(card,foil);

		if(productUri==null)
			return list;

		var url = WEB_URI+ "/"+productUri;
		Elements prices = null;
		Elements qualities = null;

		try {
			var doc = URLTools.extractAsHtml(url);
			qualities = doc.select(".cardTypeList li");
			prices = doc.select(".stylePrice");

		} catch (Exception e) {
			logger.info("{} no item : {}",getName(),e.getMessage());
			return list;
		}

		var lstPrices = new ArrayList<MTGPrice>();
		for (var i = 0; i < qualities.size(); i++) {
			var mp = new MTGPrice();

			var price = prices.get(i).html();
			mp.setCardData(card);
			mp.setValue(UITools.parseDouble(price));
			mp.setCurrency("USD");
			mp.setCountry(Locale.US.getDisplayCountry(MTGControler.getInstance().getLocale()));
			mp.setSeller(getName());
			mp.setSite(getName());
			mp.setUrl(url+"?partner=Mtgdesktopcompanion&utm_source=Mtgdesktopcompanion&utm_medium=affiliate&utm_campaign=condition");
			mp.setSellerUrl(url+"?partner=Mtgdesktopcompanion&utm_source=Mtgdesktopcompanion&utm_medium=affiliate&utm_campaign=condition");
			mp.setQuality(aliases.getReversedConditionFor(this, qualities.get(i).html().trim(), EnumCondition.NEAR_MINT));
			mp.setLanguage("English");
			mp.setFoil(foil);
			if (!qualities.get(i).hasClass("disabled"))
				lstPrices.add(mp);
		}
		logger.info("{} found {} offers",getName(), lstPrices.size());
		return lstPrices;
	}

	@Override
	public String getName() {
		return "Card Kingdom";
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if(obj ==null)
			return false;

		return hashCode()==obj.hashCode();
	}

	@Override
	public boolean isPartner() {
		return true;
	}

}

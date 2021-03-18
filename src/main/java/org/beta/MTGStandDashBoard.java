package org.beta;

import java.io.IOException;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.magic.api.beans.CardDominance;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.EditionsShakers;
import org.magic.api.beans.HistoryPrice;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat.FORMATS;
import org.magic.api.beans.Packaging;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;
import org.magic.services.MTGControler;
import org.magic.tools.MTG;
import org.magic.tools.UITools;
import org.magic.tools.URLTools;

import com.google.gson.JsonElement;

public class MTGStandDashBoard extends AbstractDashBoard {

	private static final String BASE_URL="https://www.mtgstand.com";
	
	

	@Override
	protected EditionsShakers getOnlineShakesForEdition(MagicEdition ed) throws IOException {
		
		String url = BASE_URL+"/api/"+getString("TOKEN")+"/setcode/"+ed.getId()+"/"+getCurrency().getCurrencyCode();
		
		JsonElement e = URLTools.extractJson(url);
		EditionsShakers ret = new EditionsShakers();
						ret.setEdition(ed);
						ret.setProviderName(getName());
						ret.setDate(new Date());
		
		e.getAsJsonArray().forEach(je->{
			
			CardShake cs = new CardShake();
					  cs.setName(je.getAsJsonObject().get("name").getAsString());
					  cs.setEd(je.getAsJsonObject().get("setcode").getAsString().toUpperCase());
					  cs.setPrice(UITools.parseDouble(je.getAsJsonObject().get("PriceEUR").getAsString()));
					  cs.setFoil(false);
					  cs.setLink(je.getAsJsonObject().get("url").getAsString());
			ret.addShake(cs);
			logger.debug(je);
		});
		
		
		
		return ret;
	}
	
	@Override
	public Currency getCurrency() {
		return Currency.getInstance("EUR");
	}
	
	
	@Override
	public List<CardDominance> getBestCards(FORMATS f, String filter) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getUpdatedDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "MTGStand";
	}

	@Override
	protected HistoryPrice<Packaging> getOnlinePricesVariation(Packaging packaging) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<CardShake> getOnlineShakerFor(FORMATS gameFormat) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected HistoryPrice<MagicCard> getOnlinePricesVariation(MagicCard mc, MagicEdition ed, boolean foil)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void initDefault() {
		setProperty("TOKEN", "");
	}
	

	public static void main(String[] args) throws IOException {
		MTGControler.getInstance();
		MTG.getEnabledPlugin(MTGCardsProvider.class).init();
		new MTGStandDashBoard().getOnlineShakesForEdition(MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById("UMA"));
	}

}

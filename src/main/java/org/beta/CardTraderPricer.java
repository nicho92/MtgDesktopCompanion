package org.beta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.magic.services.MTGLogger;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;

public class CardTraderPricer extends AbstractMagicPricesProvider {

	private static final String URL ="https://www.cardtrader.com/api/simple/v1";
	
	public static void main(String[] args) throws IOException {
		MTGLogger.changeLevel(Level.TRACE);
		new CardTraderPricer().getLocalePrice(null,null);
	}
	
	@Override
	public String getName() {
		return "CardTrader";
	}

	@Override
	protected List<MagicPrice> getLocalePrice(MagicEdition me, MagicCard card) throws IOException {
		List<MagicPrice> prices = new ArrayList<>();
		
		String ret = RequestBuilder.build().setClient(URLTools.newClient()).url(URL).method(METHOD.GET).addContent("token", getString("TOKEN")).execute();
		
		logger.info(ret);
		
		return prices;
		
	}

	
	@Override
	public void initDefault() {
		setProperty("TOKEN", "");
	}
	
}

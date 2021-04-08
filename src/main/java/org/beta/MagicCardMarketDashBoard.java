package org.beta;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.api.mkm.exceptions.MkmException;
import org.api.mkm.tools.MkmAPIConfig;
import org.api.mkm.tools.MkmConstants;
import org.magic.api.beans.CardDominance;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.EditionsShakers;
import org.magic.api.beans.HistoryPrice;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat.FORMATS;
import org.magic.api.beans.Packaging;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;
import org.magic.api.pricers.impl.MagicCardMarketPricer2;

public class MagicCardMarketDashBoard extends AbstractDashBoard {

	private boolean init=false;

	@Override
	public List<CardDominance> getBestCards(FORMATS f, String filter) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	private void init()
	{
		try {
			MagicCardMarketPricer2 mkmPricer = new MagicCardMarketPricer2();
			MkmAPIConfig.getInstance().init(
					mkmPricer.getString("APP_ACCESS_TOKEN_SECRET"),
					mkmPricer.getString("APP_ACCESS_TOKEN"), 
					mkmPricer.getString("APP_SECRET"),
					mkmPricer.getString("APP_TOKEN"));
			init=true;
		} catch (MkmException e) {
			logger.error(e);
			init=false;
		}
	}
	
	
	@Override
	public String getVersion() {
		return MkmConstants.MKM_API_VERSION;
	}

	
	@Override
	public Date getUpdatedDate() {
		return new Date();
	}

	@Override
	public String getName() {
		return "MagicCardMarket";
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
	protected EditionsShakers getOnlineShakesForEdition(MagicEdition ed) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected HistoryPrice<MagicCard> getOnlinePricesVariation(MagicCard mc, boolean foil) throws IOException {
		return null;
		
	}
	
	@Override
	protected HistoryPrice<MagicEdition> getOnlinePricesVariation(MagicEdition ed) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

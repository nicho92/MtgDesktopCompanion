package org.beta;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;

public class CardTraderPricer extends AbstractMagicPricesProvider {
	
	@Override
	public void initDefault() {
		setProperty("TOKEN", "");
	}


	@Override
	public String getName() {
		return "CardTrader";
	}


	@Override
	protected List<MagicPrice> getLocalePrice(MagicEdition me, MagicCard card) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}

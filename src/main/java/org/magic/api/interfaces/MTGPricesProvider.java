package org.magic.api.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicPrice;
import org.magic.api.beans.enums.EnumMarketType;

public interface MTGPricesProvider extends MTGPriceSuggester {

	public List<MagicPrice> getPrice(MagicCard card) throws IOException;
	
	public List<MagicPrice> getPrice(MagicDeck d,boolean side) throws IOException;

	public Map<String, List<MagicPrice>> getPricesBySeller(List<MagicCard> cards) throws IOException;
	
	public void alertDetected(List<MagicPrice> okz);

	public EnumMarketType getMarket();
	
}

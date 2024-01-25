package org.magic.api.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGPrice;

public interface MTGPricesProvider extends MTGPriceSuggester {

	public List<MTGPrice> getPrice(MTGCard card) throws IOException;

	public MTGPrice getBestPrice(MTGCard card);

	public List<MTGPrice> getPrice(MTGDeck d,boolean side) throws IOException;

	public Map<String, List<MTGPrice>> getPricesBySeller(List<MTGCard> cards) throws IOException;

	public void alertDetected(List<MTGPrice> okz);


}

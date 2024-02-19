package org.magic.api.interfaces;

import java.io.IOException;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.magic.api.beans.CardShake;
import org.magic.api.beans.EditionsShakers;
import org.magic.api.beans.HistoryPrice;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDominance;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.MTGSealedProduct;

public interface MTGDashBoard extends MTGPriceSuggester {

	public List<CardShake> getShakerFor(MTGFormat.FORMATS gameFormat) throws IOException;

	public EditionsShakers getShakesForEdition(MTGEdition edition) throws IOException;

	public HistoryPrice<MTGCard> getPriceVariation(MTGCard mc, boolean foil) throws IOException;

	public HistoryPrice<MTGEdition> getPriceVariation(MTGEdition me) throws IOException;

	public HistoryPrice<MTGSealedProduct> getPriceVariation(MTGSealedProduct packaging) throws IOException;

	public List<MTGDominance> getBestCards(MTGFormat.FORMATS f, String filter) throws IOException;

	public Date getUpdatedDate();

	public String[] getDominanceFilters();

	public Currency getCurrency();

}

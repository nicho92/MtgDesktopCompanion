package org.magic.api.interfaces;

import java.io.IOException;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.magic.api.beans.CardDominance;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.EditionsShakers;
import org.magic.api.beans.HistoryPrice;
import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumMarketType;

public interface MTGDashBoard extends MTGPriceSuggester {

	public List<CardShake> getShakerFor(MTGFormat.FORMATS gameFormat) throws IOException;

	public EditionsShakers getShakesForEdition(MagicEdition edition) throws IOException;

	public HistoryPrice<MagicCard> getPriceVariation(MagicCard mc, boolean foil) throws IOException;

	public HistoryPrice<MagicEdition> getPriceVariation(MagicEdition me) throws IOException;

	public HistoryPrice<MTGSealedProduct> getPriceVariation(MTGSealedProduct packaging) throws IOException;

	public List<CardDominance> getBestCards(MTGFormat.FORMATS f, String filter) throws IOException;

	public Date getUpdatedDate();

	public String[] getDominanceFilters();

	public Currency getCurrency();

	public EnumMarketType getMarket();


}

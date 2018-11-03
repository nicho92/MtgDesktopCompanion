package org.magic.api.interfaces;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.swing.Icon;

import org.magic.api.beans.CardDominance;
import org.magic.api.beans.CardPriceVariations;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;

public interface MTGDashBoard extends MTGPlugin {

	public List<CardShake> getShakerFor(MTGFormat gameFormat) throws IOException;
	
	public List<CardShake> getShakesForEdition(MagicEdition edition) throws IOException;

	public CardPriceVariations getPriceVariation(MagicCard mc, MagicEdition me) throws IOException;

	public List<CardDominance> getBestCards(MTGFormat f, String filter) throws IOException;

	public Date getUpdatedDate();

	public String[] getDominanceFilters();
	
	public Icon getIcon();
	

}

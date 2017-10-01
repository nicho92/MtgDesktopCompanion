package org.magic.api.interfaces;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.magic.api.beans.CardDominance;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractDashBoard.FORMAT;

public interface DashBoard extends MTGPlugin {

	public List<CardShake> getShakerFor(String gameFormat) throws IOException;
	public List<CardShake> getShakeForEdition(MagicEdition edition) throws IOException;
	public Map<Date,Double> getPriceVariation(MagicCard mc,MagicEdition me) throws IOException;
	public List<CardDominance> getBestCards(FORMAT f,String filter) throws IOException;
	public Date getUpdatedDate();
	public String[] getDominanceFilters();
	
	
}

package org.magic.api.interfaces;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;

public interface DashBoard {

	public List<CardShake> getShakerFor(String gameFormat) throws IOException;
	public List<CardShake> getShakeForEdition(MagicEdition edition) throws IOException;
	public Map<Date,Double> getPriceVariation(MagicCard mc,MagicEdition me) throws IOException;
	
	public String getName();
	public Date getUpdatedDate();
	public Properties getProperties();
	public void save();
	public void load();
	void setProperties(String k, Object value);
	boolean isEnable();
	void enable(boolean t);
	
}

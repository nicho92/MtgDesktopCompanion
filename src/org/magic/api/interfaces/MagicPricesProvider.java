package org.magic.api.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;

public interface MagicPricesProvider {

	public List<MagicPrice> getPrice(MagicEdition me,MagicCard card) throws Exception;
	public Properties getProperties();
	public void setProperties(String k,Object value);
	public Object getProperty(String k);
	public String getName();
	public boolean isEnable();
	public void enable(boolean t);

}

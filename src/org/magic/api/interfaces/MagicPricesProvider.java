package org.magic.api.interfaces;

import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;

public interface MagicPricesProvider {

	public List<MagicPrice> getPrice(MagicEdition me,MagicCard card) throws Exception;
	
	public void setMaxResults(int max);
	public int getMaxResults();
	public String getName();
}

package org.magic.api.interfaces;

import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;

public interface MagicPricesProvider extends MTGPlugin{

	public List<MagicPrice> getPrice(MagicEdition me,MagicCard card) throws Exception;
	public void alertDetected(List<MagicPrice> okz);

}

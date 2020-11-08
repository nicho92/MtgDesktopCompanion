package org.magic.api.interfaces;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;

public interface MTGPricesProvider extends MTGPlugin {

	public List<MagicPrice> getPrice(MagicCard card) throws IOException;
	
	public List<MagicPrice> getPrice(MagicDeck d,boolean side) throws IOException;

	public void alertDetected(List<MagicPrice> okz);

}

package org.magic.api.interfaces;

import org.magic.api.beans.MTGCard;

public interface MTGPriceSuggester extends MTGPlugin{

	public Double getSuggestedPrice(MTGCard mc, boolean foil);

}

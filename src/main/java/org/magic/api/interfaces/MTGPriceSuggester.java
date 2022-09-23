package org.magic.api.interfaces;

import org.magic.api.beans.MagicCard;

public interface MTGPriceSuggester extends MTGPlugin{

	public Double getSuggestedPrice(MagicCard mc, boolean foil);

}

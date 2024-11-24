package org.magic.api.interfaces.extra;

import org.magic.api.beans.MTGCard;
import org.magic.api.interfaces.MTGPlugin;

public interface MTGPriceSuggester extends MTGPlugin{

	public Double getSuggestedPrice(MTGCard mc, boolean foil);

}

package org.magic.api.interfaces;

import java.io.IOException;
import java.util.Map;

import org.magic.api.beans.MagicCard;

public interface MTGCardsIndexer extends MTGPlugin {

	
	public Map<MagicCard,Float> similarity(MagicCard mc) throws IOException;
	public void initIndex() throws IOException;
	
	
}

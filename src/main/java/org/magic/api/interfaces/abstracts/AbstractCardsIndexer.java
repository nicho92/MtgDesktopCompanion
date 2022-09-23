package org.magic.api.interfaces.abstracts;

import org.magic.api.interfaces.MTGCardsIndexer;

public abstract class AbstractCardsIndexer extends AbstractMTGPlugin implements MTGCardsIndexer{


	@Override
	public PLUGINS getType() {
		return PLUGINS.INDEXER;
	}


}

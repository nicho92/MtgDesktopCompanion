package org.magic.api.interfaces.abstracts;

import java.io.File;

import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.services.MTGConstants;

public abstract class AbstractCardsIndexer extends AbstractMTGPlugin implements MTGCardsIndexer{

	public AbstractCardsIndexer() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "indexers");
		if (!confdir.exists())
			confdir.mkdir();
		load();

		if (!new File(confdir, getName() + ".conf").exists()) {
			initDefault();
			save();
		}
	}
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.INDEXER;
	}
	
	
}

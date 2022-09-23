package org.magic.api.interfaces.abstracts;

import org.magic.api.interfaces.MTGNewsProvider;

public abstract class AbstractMagicNewsProvider extends AbstractMTGPlugin implements MTGNewsProvider {

	public enum NEWS_TYPE {
		RSS, TWITTER, FORUM, REDDIT
	}

	@Override
	public PLUGINS getType() {
		return PLUGINS.NEWS;
	}

}

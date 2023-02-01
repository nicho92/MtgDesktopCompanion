package org.magic.api.interfaces.abstracts;

import org.magic.api.interfaces.MTGIA;

public abstract class AbstractIA extends AbstractMTGPlugin implements MTGIA {

	
	protected static final String SET_QUERY = "Tell me more about MTG set : ";
	protected static final String CARD_QUERY = "Tell me more about MTG card ";
	protected static final String DECK_QUERY = "Build a magic the gathering deck with this cards : ";

	@Override
	public PLUGINS getType() {
		return PLUGINS.IA;
	}

}

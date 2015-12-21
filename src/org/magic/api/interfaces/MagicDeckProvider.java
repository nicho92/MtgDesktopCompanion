package org.magic.api.interfaces;

import java.util.List;

import org.magic.api.beans.MagicDeck;

public interface MagicDeckProvider {

	public List<MagicDeck> getDeckByName(String n);
	
}

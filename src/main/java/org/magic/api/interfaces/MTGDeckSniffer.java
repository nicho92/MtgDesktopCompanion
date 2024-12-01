package org.magic.api.interfaces;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.technical.RetrievableDeck;

public interface MTGDeckSniffer extends MTGPlugin {

	public String[] listFilter();

	public MTGDeck getDeck(RetrievableDeck info) throws IOException;

	public List<RetrievableDeck> getDeckList(String filter,MTGCard mc) throws IOException;

	public void connect() throws IOException;
	
	public boolean hasCardFilter();
		
	
}
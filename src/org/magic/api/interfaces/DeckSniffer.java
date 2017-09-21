package org.magic.api.interfaces;

import java.util.List;
import java.util.Properties;

import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.RetrievableDeck;

public interface DeckSniffer extends MTGPlugin {

	public String[] listFilter();

	public MagicDeck getDeck(RetrievableDeck info) throws Exception;
	public List<RetrievableDeck> getDeckList() throws Exception;
	public void connect() throws Exception;
	
}
package org.magic.api.interfaces;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.RetrievableDeck;

public interface MTGDeckSniffer extends MTGPlugin {

	public String[] listFilter();

	public MagicDeck getDeck(RetrievableDeck info) throws IOException;

	public List<RetrievableDeck> getDeckList(String filter) throws IOException;

	public void connect() throws IOException;

}
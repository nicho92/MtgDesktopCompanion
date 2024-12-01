package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.technical.RetrievableDeck;
import org.magic.api.interfaces.MTGDeckSniffer;

public abstract class AbstractDeckSniffer extends AbstractMTGPlugin implements MTGDeckSniffer {

	@Override
	public PLUGINS getType() {
		return PLUGINS.DECKSNIFFER;
	}

	@Override
	public void connect() throws IOException {
		// do nothing by default

	}

	protected AbstractMap.SimpleEntry<String,Integer> parseString(String s)
	{
		Integer qte = Integer.parseInt(s.substring(0, s.indexOf(' ')));
		String cardName = s.substring(s.indexOf(' '), s.length()).trim();

		return new AbstractMap.SimpleEntry<>(cardName, qte);
	}


	@Override
	public boolean hasCardFilter() {
		return false;
	}
	
	@Override
	public List<RetrievableDeck> getDeckList(MTGCard filter) throws IOException {
		return new ArrayList<RetrievableDeck>();
	}
	
	
	
	
	
}
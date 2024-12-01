package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.util.AbstractMap;

import org.magic.api.interfaces.MTGDeckSniffer;

public abstract class AbstractDeckSniffer extends AbstractMTGPlugin implements MTGDeckSniffer {

	@Override
	public PLUGINS getType() {
		return PLUGINS.DECKSNIFFER;
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
	
	
}
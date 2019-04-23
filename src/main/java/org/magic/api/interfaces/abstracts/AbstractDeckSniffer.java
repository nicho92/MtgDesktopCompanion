package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;

import org.apache.commons.collections4.keyvalue.DefaultMapEntry;
import org.magic.api.interfaces.MTGDeckSniffer;
import org.magic.services.MTGConstants;

public abstract class AbstractDeckSniffer extends AbstractMTGPlugin implements MTGDeckSniffer {

	public AbstractDeckSniffer() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "decksniffers");
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
		return PLUGINS.DECKS;
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

	

}

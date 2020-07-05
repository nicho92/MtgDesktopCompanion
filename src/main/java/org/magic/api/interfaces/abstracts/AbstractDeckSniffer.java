package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MTGImportExportException;
import org.magic.api.interfaces.MTGDeckSniffer;

public abstract class AbstractDeckSniffer extends AbstractMTGPlugin implements MTGDeckSniffer {


	protected List<MTGImportExportException> rejects= new ArrayList<>();
	
	
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.DECKS;
	}
	
	@Override
	public void connect() throws IOException {
		// do nothing by default
		
	}
	
	
	@Override
	protected String getConfigDirectoryName() {
		return "decksniffers";
	}
	
	protected AbstractMap.SimpleEntry<String,Integer> parseString(String s)
	{
		Integer qte = Integer.parseInt(s.substring(0, s.indexOf(' ')));
		String cardName = s.substring(s.indexOf(' '), s.length()).trim();
		
		return new AbstractMap.SimpleEntry<>(cardName, qte);
	}

	@Override
	public void reject(String msg) {
		rejects.add(new MTGImportExportException(this, msg));
		
	}

	@Override
	public List<MTGImportExportException> rejects() {
		return rejects;
	}
	

}

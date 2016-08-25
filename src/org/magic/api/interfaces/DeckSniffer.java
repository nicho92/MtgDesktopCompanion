package org.magic.api.interfaces;

import java.util.List;
import java.util.Properties;

import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.RetrievableDeck;

public interface DeckSniffer {

	public String[] listFilter();

	public MagicDeck getDeck(RetrievableDeck info) throws Exception;
	public List<RetrievableDeck> getDeckList() throws Exception;
	public void connect() throws Exception;
	
	
	public Properties getProperties();
	public void setProperties(String k,Object value);
	public Object getProperty(String k);
	public String getName();
	public boolean isEnable();
	public void save();
	public void load();
	public void enable(boolean t);
}
package org.magic.db;

import java.sql.SQLException;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;

public interface MagicDAO {


	public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException;
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException;
	public MagicCard loadCard(String name, MagicCollection collection)throws SQLException;
	
	public MagicDeck getDeck(String name)throws SQLException;
	public void saveDeck(MagicDeck deck)throws SQLException;
	public void removeDeck(MagicDeck deck)throws SQLException;
	
	
	public int getCardsCount(List<MagicCollection> list)throws SQLException;
	
	public List<MagicCard> getCardsFromCollection(MagicCollection collection) throws SQLException;
	public List<MagicCard> getCardsFromCollection(MagicCollection collection,MagicEdition me) throws SQLException;
	public List<String> getEditionsFromCollection(MagicCollection collection) throws SQLException;
	
	public MagicCollection getCollection(String name)throws SQLException;
	public void saveCollection(MagicCollection c)throws SQLException;
	public void removeCollection(MagicCollection c)throws SQLException;
	public List<MagicCollection> getCollections() throws SQLException;

	public void removeEdition(MagicEdition ed, MagicCollection col)throws SQLException;
 
	public String getDBLocation();
	public long getDBSize();
	
}
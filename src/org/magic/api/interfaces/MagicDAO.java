package org.magic.api.interfaces;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;

public interface MagicDAO {

	public Properties getProperties();
	public void init() throws ClassNotFoundException, SQLException ;
	public String getName();
	public boolean isEnable();
	public void save();
	public void load();
	public void enable(boolean enabled);
	
	
	public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException;
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException;
	public MagicCard loadCard(String name, MagicCollection collection)throws SQLException;
	public List<MagicCard> listCards()throws SQLException;
	
	public int getCardsCount(MagicCollection list)throws SQLException;
	
	public List<MagicCard> getCardsFromCollection(MagicCollection collection) throws SQLException;
	public List<MagicCard> getCardsFromCollection(MagicCollection collection,MagicEdition me) throws SQLException;
	public List<String> getEditionsIDFromCollection(MagicCollection collection) throws SQLException;
	
	public MagicCollection getCollection(String name)throws SQLException;
	public void saveCollection(MagicCollection c)throws SQLException;
	public void removeCollection(MagicCollection c)throws SQLException;
	public List<MagicCollection> getCollections() throws SQLException;

	public void removeEdition(MagicEdition ed, MagicCollection col)throws SQLException;
 
	public String getDBLocation();
	public long getDBSize();
	
	
}
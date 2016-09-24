package org.magic.api.interfaces;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.ShopItem;

public interface MagicDAO {

	public Properties getProperties();
	public void init() throws ClassNotFoundException, SQLException ;
	
	public String getName();
	public void save();
	public void load();
	public void enable(boolean enabled);
	public boolean isEnable();
	
	
	public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException;
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException;
	public MagicCard loadCard(String name, MagicCollection collection)throws SQLException;
	public List<MagicCard> listCards()throws SQLException;
	
	public int getCardsCount(MagicCollection list,MagicEdition me) throws SQLException;
	public Map<String,Integer> getCardsCountGlobal(MagicCollection c) throws SQLException;
	
	public List<MagicCard> getCardsFromCollection(MagicCollection collection) throws SQLException;
	public List<MagicCard> getCardsFromCollection(MagicCollection collection,MagicEdition me) throws SQLException;
	public List<MagicCollection> getCollectionFromCards(MagicCard mc)throws SQLException;
	public List<String> getEditionsIDFromCollection(MagicCollection collection) throws SQLException;
	
	public MagicCollection getCollection(String name)throws SQLException;
	public void saveCollection(MagicCollection c)throws SQLException;
	public void removeCollection(MagicCollection c)throws SQLException;
	public List<MagicCollection> getCollections() throws SQLException;

	public void removeEdition(MagicEdition ed, MagicCollection col)throws SQLException;
	
	public List<MagicDeck> listDeck()throws SQLException;
	public void saveDeck(MagicDeck d)throws SQLException;
	public void deleteDeck(MagicDeck d)throws SQLException;
 
	public void saveShopItem(ShopItem mp, String string) throws SQLException;
	public String getSavedShopItemAnotation(ShopItem id) throws SQLException;

	public List<MagicCardStock> getStocks(MagicCard mc, MagicCollection col) throws SQLException;
	public void saveOrUpdateStock(MagicCardStock state) throws SQLException;
	public void deleteStock(MagicCardStock state) throws SQLException;
		
	
	public String getDBLocation();
	public long getDBSize();
	public void backup(File dir) throws Exception;

	
}
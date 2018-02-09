package org.magic.api.interfaces;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;

public interface MTGDao extends MTGPlugin{

	
	public void init() throws ClassNotFoundException, SQLException ;
	
	public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException;
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException;
	//public MagicCard loadCard(String name, MagicCollection collection)throws SQLException;
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
	
	public List<MagicCardStock> getStocks(MagicCard mc, MagicCollection col) throws SQLException;
	public void saveOrUpdateStock(MagicCardStock state) throws SQLException;
	public void deleteStock(List<MagicCardStock> state) throws SQLException;
	public List<MagicCardStock> getStocks() throws SQLException;
	
	public List<MagicCardAlert> getAlerts();
	public void saveAlert(MagicCardAlert alert) throws Exception;
	public void deleteAlert(MagicCardAlert alert) throws Exception;
	public boolean hasAlert(MagicCard mc);
	
	
	public String getDBLocation();
	public long getDBSize();
	public void backup(File dir) throws Exception;
	public void updateAlert(MagicCardAlert alert) throws Exception;

	
}
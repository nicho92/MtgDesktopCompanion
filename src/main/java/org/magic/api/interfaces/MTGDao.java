package org.magic.api.interfaces;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicNews;
import org.magic.api.beans.OrderEntry;

public interface MTGDao extends MTGPlugin {

	public void init() throws SQLException;

	public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException;
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException;
	public void moveCard(MagicCard mc, MagicCollection from, MagicCollection to) throws SQLException;
	public List<MagicCard> listCards() throws SQLException;

	public int getCardsCount(MagicCollection list, MagicEdition me) throws SQLException;
	public Map<String, Integer> getCardsCountGlobal(MagicCollection c) throws SQLException;
	public List<MagicCard> listCardsFromCollection(MagicCollection collection) throws SQLException;
	public List<MagicCard> listCardsFromCollection(MagicCollection collection, MagicEdition me) throws SQLException;

	public MagicCollection getCollection(String name) throws SQLException;
	public void saveCollection(MagicCollection c) throws SQLException;
	public void saveCollection(String name) throws SQLException;
	public void removeCollection(MagicCollection c) throws SQLException;

	
	public List<MagicCollection> listCollections() throws SQLException;
	public List<MagicCollection> listCollectionFromCards(MagicCard mc) throws SQLException;
	public List<String> listEditionsIDFromCollection(MagicCollection collection) throws SQLException;
	public void removeEdition(MagicEdition ed, MagicCollection col) throws SQLException;

	public List<MagicCardStock> listStocks(MagicCard mc, MagicCollection col,boolean editionStrict) throws SQLException;
	public void saveOrUpdateStock(MagicCardStock state) throws SQLException;
	public void deleteStock(List<MagicCardStock> state) throws SQLException;
	public void deleteStock(MagicCardStock state) throws SQLException;
	public List<MagicCardStock> listStocks() throws SQLException;
	
	
	public List<OrderEntry> listOrders();
	public void saveOrUpdateOrderEntry(OrderEntry state) throws SQLException;
	public void deleteOrderEntry(List<OrderEntry> state) throws SQLException;
	public void deleteOrderEntry(OrderEntry state) throws SQLException;
	public List<OrderEntry> listOrderForEdition(MagicEdition ed);
	public List<OrderEntry> listOrdersAt(Date d);
	public List<Date> listDatesOrders();

	public List<MagicCardAlert> listAlerts();
	public void saveAlert(MagicCardAlert alert) throws SQLException;
	public void deleteAlert(MagicCardAlert alert) throws SQLException;
	public boolean hasAlert(MagicCard mc);
	public void updateAlert(MagicCardAlert alert) throws SQLException;

	public List<MagicNews> listNews();
	public void deleteNews(MagicNews n) throws SQLException;
	public void saveOrUpdateNews(MagicNews n) throws SQLException;

	public String getDBLocation();
	public long getDBSize();
	public void backup(File dir) throws SQLException, IOException;
	public void duplicateTo(MTGDao dao) throws SQLException;

	public void update(MagicCard c, MagicCard newC, MagicCollection col) throws SQLException;

	

}
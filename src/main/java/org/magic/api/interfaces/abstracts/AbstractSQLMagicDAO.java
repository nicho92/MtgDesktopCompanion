package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicNews;
import org.magic.api.beans.OrderEntry;

public class AbstractSQLMagicDAO extends AbstractMagicDAO {

	@Override
	public void init() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<MagicCard> listCards() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCardsCount(MagicCollection list, MagicEdition me) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<String, Integer> getCardsCountGlobal(MagicCollection c) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MagicCard> listCardsFromCollection(MagicCollection collection) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MagicCard> listCardsFromCollection(MagicCollection collection, MagicEdition me) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MagicCollection getCollection(String name) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveCollection(MagicCollection c) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeCollection(MagicCollection c) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<MagicCollection> listCollections() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MagicCollection> listCollectionFromCards(MagicCard mc) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> listEditionsIDFromCollection(MagicCollection collection) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeEdition(MagicEdition ed, MagicCollection col) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<MagicCardStock> listStocks(MagicCard mc, MagicCollection col, boolean editionStrict)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveOrUpdateStock(MagicCardStock state) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteStock(List<MagicCardStock> state) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<MagicCardStock> listStocks() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveOrUpdateOrderEntry(OrderEntry state) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteOrderEntry(List<OrderEntry> state) throws SQLException {
		// TODO Auto-generated method stub

	}

	
	@Override
	public void saveAlert(MagicCardAlert alert) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void updateAlert(MagicCardAlert alert) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void deleteAlert(MagicCardAlert alert) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<MagicNews> listNews() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteNews(MagicNews n) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveOrUpdateNews(MagicNews n) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDBLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getDBSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void backup(File dir) throws SQLException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void initAlerts() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initOrders() {
		// TODO Auto-generated method stub

	}

}

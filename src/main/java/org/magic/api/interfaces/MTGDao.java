package org.magic.api.interfaces;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.Announce;
import org.magic.api.beans.Announce.STATUS;
import org.magic.api.beans.ConverterItem;
import org.magic.api.beans.GedEntry;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicNews;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.SealedStock;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;

public interface MTGDao extends MTGPlugin{

	public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException;
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException;
	public void moveCard(MagicCard mc, MagicCollection from, MagicCollection to) throws SQLException;
	public List<MagicCard> listCards() throws SQLException;
	
	public int getCardsCount(MagicCollection list, MagicEdition me) throws SQLException;
	public Map<String, Integer> getCardsCountGlobal(MagicCollection c) throws SQLException;
	public List<MagicCard> listCardsFromCollection(String collectionName) throws SQLException;
	public List<MagicCard> listCardsFromCollection(MagicCollection collection) throws SQLException;
	public List<MagicCard> listCardsFromCollection(MagicCollection collection, MagicEdition me) throws SQLException;
	public List<MagicCard> listCardsFromCollection(String collectionName,MagicEdition me) throws SQLException;
	
	public List<MagicDeck> listDecks() throws SQLException;
	public void deleteDeck(MagicDeck d) throws SQLException;
	public Integer saveOrUpdateDeck(MagicDeck d) throws SQLException;
	public MagicDeck getDeckById(Integer id) throws SQLException;
	
	
	public MagicCollection getCollection(String name) throws SQLException;
	public void saveCollection(MagicCollection c) throws SQLException;
	public void saveCollection(String name) throws SQLException;
	public void removeCollection(MagicCollection c) throws SQLException;
	public List<MagicCollection> listCollections() throws SQLException;
	public List<MagicCollection> listCollectionFromCards(MagicCard mc) throws SQLException;
	public List<String> listEditionsIDFromCollection(MagicCollection collection) throws SQLException;
	public void removeEdition(MagicEdition ed, MagicCollection col) throws SQLException;
	public List<MagicCard> synchronizeCollection(MagicCollection col) throws SQLException;
	

	public List<MTGStockItem> listStockItems()throws SQLException;
	public MTGStockItem getStockById(EnumItems typeStock, Long id)throws SQLException;
	public void saveOrUpdateStock(MTGStockItem stock) throws SQLException;

	
	
	public List<MagicCardStock> listStocks(MagicCard mc) throws SQLException;
	public List<MagicCardStock> listStocks(MagicCard mc, MagicCollection col,boolean editionStrict) throws SQLException;
	public List<MagicCardStock> listStocks(String cardName, List<MagicCollection> cols) throws SQLException;
	public List<MagicCardStock> listStocks(List<MagicCollection> cols) throws SQLException;
	public List<MagicCardStock> listStocks() throws SQLException;
	public MagicCardStock getStockWithTiersID(String key,String id) throws SQLException;
	public MagicCardStock getStockById(Long id) throws SQLException;
	public void saveOrUpdateCardStock(MagicCardStock state) throws SQLException;
	public void deleteStock(List<MagicCardStock> state) throws SQLException;
	public void deleteStock(MagicCardStock state) throws SQLException;
	
	
	
	public List<SealedStock> listSealedStocks() throws SQLException;
	public List<SealedStock> listSealedStocks(MagicCollection c) throws SQLException;
	public List<SealedStock> listSealedStocks(MagicCollection c, MagicEdition ed) throws SQLException;
	public void saveOrUpdateSealedStock(SealedStock state) throws SQLException;
	public void deleteStock(SealedStock state) throws SQLException;
	public SealedStock getSealedStockById(Long id) throws SQLException;
	

	
	public List<OrderEntry> listOrders();
	public void saveOrUpdateOrderEntry(OrderEntry state) throws SQLException;
	public void deleteOrderEntry(List<OrderEntry> state) throws SQLException;
	public void deleteOrderEntry(OrderEntry state) throws SQLException;
	public List<OrderEntry> listOrderForEdition(MagicEdition ed);
	public List<OrderEntry> listOrdersAt(Date d);
	
	public List<OrderEntry> listOrdersByDescription(String desc, boolean strict);

	
	public List<Transaction> listTransactions() throws SQLException;
	public List<Transaction> listTransactions(Contact c) throws SQLException;
	public int saveOrUpdateTransaction(Transaction t)throws SQLException;
	public void deleteTransaction(Transaction t)throws SQLException;
	public Transaction getTransaction(int id) throws SQLException;
	public void deleteTransaction(List<Transaction> t) throws SQLException;
	public void changePassword(Contact c, String newPassword)  throws SQLException;
	public int saveOrUpdateContact(Contact c)  throws SQLException;
	public Contact getContactById(int id)  throws SQLException;
	public List<Contact> listContacts()  throws SQLException;
	public Contact getContactByLogin(String email, String password)  throws SQLException;
	public Contact getContactByEmail(String email)  throws SQLException;
	public boolean enableContact(String token) throws SQLException;
	public void deleteContact(Contact contact) throws SQLException;

	public List<MagicCardAlert> listAlerts();
	public void saveAlert(MagicCardAlert alert) throws SQLException;
	public void updateAlert(MagicCardAlert alert) throws SQLException;
	public void deleteAlert(MagicCardAlert alert) throws SQLException;
	public MagicCardAlert hasAlert(MagicCard mc);

	
	public void saveFavorites(int idContact, int idAnnounce, String classename) throws SQLException;
	public void deleteFavorites(int idContact, int idAnnounce,String classename) throws SQLException;
	public List<Announce> listFavorites(Contact c,String classename) throws SQLException;
	
	public List<Announce> listAnnounces() throws SQLException;
	public List<Announce> listAnnounces(Contact c) throws SQLException;
	public List<Announce> listAnnounces(int max,STATUS stat) throws SQLException;
	public List<Announce> listAnnounces(String textSearch) throws SQLException;
	public List<Announce> listAnnounces(EnumItems valueOf)throws SQLException;
	
	
	public Announce getAnnounceById(int id) throws SQLException;
	public int saveOrUpdateAnnounce(Announce a) throws SQLException;
	public void deleteAnnounce(Announce alert) throws SQLException;
	
	public List<MagicNews> listNews();
	public void deleteNews(MagicNews n) throws SQLException;
	public void saveOrUpdateNews(MagicNews n) throws SQLException;

	
	public List<ConverterItem> listConversionItems() throws SQLException;
	public void deleteConversionItem(ConverterItem n) throws SQLException;
	public void saveOrUpdateConversionItem(ConverterItem n) throws SQLException;

	public <T extends MTGStorable> List<GedEntry<T>> listEntries(String classename, String fileName)  throws SQLException;
	public <T extends MTGStorable> boolean deleteEntry(GedEntry<T> gedItem) throws SQLException;
	public <T extends MTGStorable> boolean storeEntry(GedEntry<T> gedItem) throws SQLException;
	public <T extends MTGStorable> GedEntry<T> readEntry(String classe, String idInstance, String fileName) throws SQLException;
	public <T extends MTGStorable> List<GedEntry<T>> listAllEntries()throws SQLException;
	
	public void init() throws SQLException;
	public void init(MTGPool pool) throws SQLException;
	public String getDBLocation();
	public Map<String,Long> getDBSize();
	
	public void backup(File dir) throws SQLException, IOException;
	public void duplicateTo(MTGDao dao) throws SQLException;
	public void updateCard(MagicCard c, MagicCard newC, MagicCollection col) throws SQLException;
	public void executeQuery(String query)throws SQLException;
	public boolean isSQL();

	
	

}
package org.magic.api.interfaces;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGAlert;
import org.magic.api.beans.MTGAnnounce;
import org.magic.api.beans.MTGAnnounce.STATUS;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGNews;
import org.magic.api.beans.MTGSealedStock;
import org.magic.api.beans.abstracts.AbstractAuditableItem;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.GedEntry;
import org.magic.api.interfaces.extra.MTGSerializable;

public interface MTGDao extends MTGPlugin{

	
	
	public void saveCard(MTGCard mc, MTGCollection collection) throws SQLException;
	public void removeCard(MTGCard mc, MTGCollection collection) throws SQLException;
	public void moveCard(MTGCard mc, MTGCollection from, MTGCollection to) throws SQLException;
	public void moveEdition(MTGEdition ed, MTGCollection from, MTGCollection to) throws SQLException;
	public void removeEdition(MTGEdition ed, MTGCollection col) throws SQLException;


	public MTGCollection getCollection(String name) throws SQLException;
	public void saveCollection(MTGCollection c) throws SQLException;
	public void saveCollection(String name) throws SQLException;
	public void removeCollection(MTGCollection c) throws SQLException;
	
	public List<MTGCollection> listCollections() throws SQLException;
	public List<MTGCollection> listCollectionFromCards(MTGCard mc) throws SQLException;
	public List<String> listEditionsIDFromCollection(MTGCollection collection) throws SQLException;


	public List<MTGStockItem> listStockItems()throws SQLException;
	public MTGStockItem getStockById(EnumItems typeStock, Long id)throws SQLException;
	public void saveOrUpdateStock(MTGStockItem stock) throws SQLException;
	
	public Map<String, Integer> getCardsCountGlobal(MTGCollection c) throws SQLException;
	public List<MTGCard> listCardsFromCollection(String collectionName) throws SQLException;
	public List<MTGCard> listCardsFromCollection(MTGCollection collection) throws SQLException;
	public List<MTGCard> listCardsFromCollection(MTGCollection collection, MTGEdition me) throws SQLException;
	public List<MTGCard> listCardsFromCollection(String collectionName,String me) throws SQLException;
	public List<MTGCardStock> listStocks(MTGCard mc) throws SQLException;
	public List<MTGCardStock> listStocks(MTGCard mc, MTGCollection col,boolean editionStrict) throws SQLException;
	public List<MTGCardStock> listStocks(String cardName, List<MTGCollection> cols) throws SQLException;
	public List<MTGCardStock> listStocks(List<MTGCollection> cols) throws SQLException;
	public List<MTGCardStock> listStocks() throws SQLException;
	public List<MTGCardStock> listStocks(MTGCollection collection, MTGEdition me) throws SQLException;
	public List<MTGCardStock> listStocks(String collectionName,String me) throws SQLException;
	
	
	public MTGCardStock getStockWithTiersID(String key,String id) throws SQLException;
	public MTGCardStock getStockById(Long id) throws SQLException;
	public void saveOrUpdateCardStock(MTGCardStock state) throws SQLException;
	public void saveOrUpdateCardStock(MTGCard state) throws SQLException;
	public void deleteStock(List<MTGCardStock> state) throws SQLException;
	public void deleteStock(MTGCardStock state) throws SQLException;

	public List<MTGSealedStock> listSealedStocks() throws SQLException;
	public List<MTGSealedStock> listSealedStocks(MTGCollection c) throws SQLException;
	public List<MTGSealedStock> listSealedStocks(MTGCollection c, MTGEdition ed) throws SQLException;
	public void saveOrUpdateSealedStock(MTGSealedStock state) throws SQLException;
	public void deleteStock(MTGSealedStock state) throws SQLException;
	public MTGSealedStock getSealedStockById(Long id) throws SQLException;

	public List<MTGDeck> listDecks() throws SQLException;
	public void deleteDeck(MTGDeck d) throws SQLException;
	public Integer saveOrUpdateDeck(MTGDeck d) throws SQLException;
	public MTGDeck getDeckById(Integer id) throws SQLException;
	
	public List<Transaction> listTransactions() throws SQLException;
	public List<Transaction> listTransactions(Contact c) throws SQLException;
	public Long saveOrUpdateTransaction(Transaction t)throws SQLException;
	public void deleteTransaction(Transaction t)throws SQLException;
	public Transaction getTransaction(Long id) throws SQLException;
	public void deleteTransaction(List<Transaction> t) throws SQLException;
	public void changePassword(Contact c, String newPassword)  throws SQLException;
	public int saveOrUpdateContact(Contact c)  throws SQLException;
	public Contact getContactById(int id)  throws SQLException;
	
	
	public List<Contact> listContacts()  throws SQLException;
	public Contact getContactByLogin(String email, String password)  throws SQLException;
	public Contact getContactByEmail(String email)  throws SQLException;
	public boolean enableContact(String token) throws SQLException;
	public void deleteContact(Contact contact) throws SQLException;

	public List<MTGAlert> listAlerts();
	public void saveAlert(MTGAlert alert) throws SQLException;
	public void updateAlert(MTGAlert alert) throws SQLException;
	public void deleteAlert(MTGAlert alert) throws SQLException;
	public MTGAlert hasAlert(MTGCard mc);

	public List<MTGAnnounce> listAnnounces() throws SQLException;
	public List<MTGAnnounce> listAnnounces(Contact c) throws SQLException;
	public List<MTGAnnounce> listAnnounces(int max,STATUS stat) throws SQLException;
	public List<MTGAnnounce> listAnnounces(String textSearch) throws SQLException;
	public List<MTGAnnounce> listAnnounces(EnumItems valueOf)throws SQLException;


	public MTGAnnounce getAnnounceById(int id) throws SQLException;
	public int saveOrUpdateAnnounce(MTGAnnounce a) throws SQLException;
	public void deleteAnnounceById(int id) throws SQLException;
	public void deleteAnnounce(MTGAnnounce alert) throws SQLException;

	public List<MTGNews> listNews();
	public void deleteNews(MTGNews n) throws SQLException;
	public void saveOrUpdateNews(MTGNews n) throws SQLException;

	public <T extends MTGSerializable> List<GedEntry<T>> listEntries(String classename, String fileName)  throws SQLException;
	public <T extends MTGSerializable> boolean deleteEntry(GedEntry<T> gedItem) throws SQLException;
	public <T extends MTGSerializable> boolean storeEntry(GedEntry<T> gedItem) throws SQLException;
	public <T extends MTGSerializable> GedEntry<T> readEntry(String classe, String idInstance, String fileName) throws SQLException;
	public <T extends MTGSerializable> List<GedEntry<T>> listAllEntries() throws SQLException;

	public void init() throws SQLException;
	public void init(MTGPool pool) throws SQLException;
	public String getDBLocation();
	public Map<String,Long> getDBSize();

	public void duplicateTo(MTGDao dao) throws SQLException;
	public void updateCard(MTGCard c, MTGCard newC, MTGCollection col) throws SQLException;
	public boolean executeQuery(String query)throws SQLException;
	public boolean isSQL();

	
	public <T extends AbstractAuditableItem> void storeTechnicalItem(Class<T> c, List<T> list) throws SQLException;
	public <T extends AbstractAuditableItem> List<T> restoreTechnicalItem(Class<T> c, Instant start,Instant end) throws SQLException;



}
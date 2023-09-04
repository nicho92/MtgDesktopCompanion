package org.beta;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.Announce;
import org.magic.api.beans.Announce.STATUS;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicNews;
import org.magic.api.beans.SealedStock;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.ConverterItem;
import org.magic.api.beans.technical.GedEntry;
import org.magic.api.interfaces.MTGSerializable;
import org.magic.api.interfaces.abstracts.AbstractMagicDAO;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisDAO extends AbstractMagicDAO {

	RedisCommands<String, String> syncCommands;
	StatefulRedisConnection<String, String> connection;
	RedisClient redisClient;
	
	@Override
	public void init() throws SQLException {
		redisClient = RedisClient.create("redis://default:redispw@localhost:6379");
		connection = redisClient.connect();
		syncCommands = connection.sync();
	}
	
	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("LOGIN","default",
					  "PASS","redispw",
					  "SERVER","localhost",
					  "PORT","6379");
	}
	
	@Override
	public void unload() {
		try {
			connection.close();
			redisClient.shutdown();
		}
		catch(Exception e)
		{
			//do nothing
		}
	}
	
	
	
	
	public static void main(String[] args) throws SQLException, IOException {
		var dao = new RedisDAO();
		dao.init();
		
		dao.listCards();
		
		System.exit(0);
	}
	

	@Override
	public void saveCollection(MagicCollection c) throws SQLException {
		syncCommands.sadd("collections",c.getName());
	}
	
	@Override
	public List<MagicCollection> listCollections() throws SQLException {
		return syncCommands.hgetall("collections").values().stream().map(MagicCollection::new).toList();
	}
	
	
	@Override
	public void saveCard(MagicCard card, MagicCollection collection) throws SQLException {
		syncCommands.sadd("cards:"+collection.getName(), card.toJson().toString());

	}

	@Override
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveEdition(MagicEdition ed, MagicCollection from, MagicCollection to) throws SQLException {
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
	public List<MagicDeck> listDecks() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteDeck(MagicDeck d) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public Integer saveOrUpdateDeck(MagicDeck d) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MagicDeck getDeckById(Integer id) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MagicCollection getCollection(String name) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void removeCollection(MagicCollection c) throws SQLException {
		// TODO Auto-generated method stub

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
	public List<MagicCardStock> listStocks() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveOrUpdateCardStock(MagicCardStock state) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteStock(List<MagicCardStock> state) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<SealedStock> listSealedStocks() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveOrUpdateSealedStock(SealedStock state) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteStock(SealedStock state) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public SealedStock getSealedStockById(Long id) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Transaction> listTransactions() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Transaction> listTransactions(Contact c) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long saveOrUpdateTransaction(Transaction t) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteTransaction(Transaction t) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public Transaction getTransaction(Long id) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void changePassword(Contact c, String newPassword) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public int saveOrUpdateContact(Contact c) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Contact getContactById(int id) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Contact> listContacts() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Contact getContactByLogin(String email, String password) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Contact getContactByEmail(String email) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean enableContact(String token) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void deleteContact(Contact contact) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<MagicCardAlert> listAlerts() {
		// TODO Auto-generated method stub
		return null;
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
	public List<Announce> listAnnounces(int max, STATUS stat) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Announce getAnnounceById(int id) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int saveOrUpdateAnnounce(Announce a) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void deleteAnnounce(Announce alert) throws SQLException {
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
	public List<ConverterItem> listConversionItems() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteConversionItem(ConverterItem n) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveOrUpdateConversionItem(ConverterItem n) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends MTGSerializable> List<GedEntry<T>> listEntries(String classename, String fileName)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends MTGSerializable> boolean deleteEntry(GedEntry<T> gedItem) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends MTGSerializable> boolean storeEntry(GedEntry<T> gedItem) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends MTGSerializable> GedEntry<T> readEntry(String classe, String idInstance, String fileName)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends MTGSerializable> List<GedEntry<T>> listAllEntries() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

	@Override
	public String getDBLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Long> getDBSize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean executeQuery(String query) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		return "Redis";
	}

}

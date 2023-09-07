package org.beta;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import org.magic.api.interfaces.abstracts.extra.AbstractKeyValueDao;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisDAO extends AbstractKeyValueDao {

	RedisCommands<String, String> syncCommands;
	StatefulRedisConnection<String, String> connection;
	RedisClient redisClient;
	
	@Override
	public Long incr(Object o) {
		return syncCommands.incr("incr:"+o.getClass().getName());
	}
	
	
	
	@Override
	public void init() throws SQLException {
		redisClient = RedisClient.create("redis://default:redispw@localhost:49153");
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
	public String getDBLocation() {
		return "";
		//return new StringBuilder().append("redis://").append(getString("LOGIN")).append(":").append(getString("PASSWORD")).append("@").append(getString("SERVER")).append(":").append(getString("PORT")).toString();
	}

	@Override
	public Map<String, Long> getDBSize() {
		// TODO Auto-generated method stub
		return new HashMap<>();
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
	
//		MTGControler.getInstance().init();
//		
//		
//		var mcs = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName("Griffin", null, false);
//		
				
		System.out.println(dao.listEditionsIDFromCollection(new MagicCollection("Library")));
		
		dao.unload();
		
		System.exit(0);
	}

	@Override
	public List<String> listEditionsIDFromCollection(MagicCollection collection) throws SQLException {
		return syncCommands.keys(key(collection)+SEPARATOR+"*").stream().map(s->s.substring(s.lastIndexOf(SEPARATOR)+1)).toList();
	}
	
	@Override
	public void saveCollection(MagicCollection c) throws SQLException {
		syncCommands.sadd(KEY_COLLECTIONS,c.getName());
	}
	
	@Override
	public List<MagicCollection> listCollections() throws SQLException {
		return syncCommands.smembers(KEY_COLLECTIONS).stream().map(MagicCollection::new).toList();
	}
	
	@Override
	public void removeCollection(MagicCollection c) throws SQLException {
		syncCommands.srem(KEY_COLLECTIONS, c.getName());
	}
	
	@Override
	public void saveCard(MagicCard card, MagicCollection collection) throws SQLException {
		syncCommands.sadd(key(collection,card), serialiser.toJson(card));
	}
	

	@Override
	public int getCardsCount(MagicCollection c, MagicEdition me) throws SQLException {
		return syncCommands.scard(key(c,me)).intValue();
	}

	@Override
	public Map<String, Integer> getCardsCountGlobal(MagicCollection c) throws SQLException {
		
		var map = new HashMap<String,Integer>();
		
		syncCommands.keys(key(c)+SEPARATOR+"*").forEach(s->{
			var idSet = s.substring(s.lastIndexOf(SEPARATOR)+1);
			var count = syncCommands.scard(s).intValue();
			
			map.put(idSet, count);
		});
		return map;
	}




	@Override
	public List<MagicDeck> listDecks() throws SQLException {
		
		var l = new ArrayList<MagicDeck>();
		
		syncCommands.keys(KEY_DECK+SEPARATOR+"*").forEach(s->{
				l.add(serialiser.fromJson(syncCommands.get(s), MagicDeck.class));
		});
		
		return l;
	}

	@Override
	public void deleteDeck(MagicDeck d) throws SQLException {
		syncCommands.del(key(d));
	}

	@Override
	public Integer saveOrUpdateDeck(MagicDeck d) throws SQLException {
		//TODO maps are not serializzed
		if(d.getId()<0)
			d.setId(incr(d.getClass()).intValue());
		
		syncCommands.set(key(d), serialiser.toJsonElement(d).toString() );
		
		return d.getId();
	}

	@Override
	public MagicDeck getDeckById(Integer id) throws SQLException {
		return serialiser.fromJson(syncCommands.get(KEY_DECK+SEPARATOR+id),MagicDeck.class);
	}

	@Override
	public List<MagicCard> listCardsFromCollection(MagicCollection collection, MagicEdition me) throws SQLException {
		return syncCommands.smembers(key(collection,me)).stream().map(s->serialiser.fromJson(s, MagicCard.class)).collect(Collectors.toList());
	}
	
	
	@Override
	public List<MagicCard> listCardsFromCollection(MagicCollection collection) throws SQLException {
		return syncCommands.smembers(key(collection)).stream().map(s->serialiser.fromJson(s, MagicCard.class)).toList();
	}

	@Override
	public void removeEdition(MagicEdition ed, MagicCollection col) throws SQLException {
		syncCommands.del(key(col,ed));
	}

	

	@Override
	public MagicCollection getCollection(String name) throws SQLException {
		var opt = listCollections().stream().filter(c->c.getName().equals(name)).findFirst();
		
		if(opt.isPresent())
			return opt.get();
		else
			throw new SQLException("Collection " + name + " doesn't exist");
		
	}

	
	
	@Override
	public void moveEdition(MagicEdition ed, MagicCollection from, MagicCollection to) throws SQLException {
		syncCommands.rename(key(from,ed), key(to,ed));
	}

	
	@Override
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
		syncCommands.keys(key(collection,mc.getCurrentSet())+SEPARATOR+"*").forEach(s->{
			var opt = syncCommands.smembers(s).stream().map(str->serialiser.fromJson(str, MagicCard.class)).filter(c->c.equals(mc)).findFirst();
			
			if(opt.isPresent())
			{
				var ret=	syncCommands.srem(s, serialiser.toJson(opt.get()));
				
				logger.info("remove element index at {}",ret);
			}
	});
	
	}

	@Override
	public List<MagicCollection> listCollectionFromCards(MagicCard mc) throws SQLException {
		return new ArrayList<>();
	}

	@Override
	public List<MagicCardStock> listStocks(MagicCard mc, MagicCollection col, boolean editionStrict) throws SQLException {
		// TODO Auto-generated method stub
		return  new ArrayList<>();
	}

	@Override
	public List<MagicCardStock> listStocks() throws SQLException {
		// TODO Auto-generated method stub
		return  new ArrayList<>();
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
		return  new ArrayList<>();
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
		return  new ArrayList<>();
	}

	@Override
	public List<Transaction> listTransactions(Contact c) throws SQLException {
		// TODO Auto-generated method stub
		return  new ArrayList<>();
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
		return  new ArrayList<>();
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
		return  new ArrayList<>();
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
		return  new ArrayList<>();
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
		return  new ArrayList<>();
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
		return  new ArrayList<>();
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
		return  new ArrayList<>();
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
		return  new ArrayList<>();
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

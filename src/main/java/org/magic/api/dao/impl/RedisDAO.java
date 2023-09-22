package org.magic.api.dao.impl;

import java.sql.SQLException;
import java.time.Instant;
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
import org.magic.api.beans.technical.audit.DAOInfo;
import org.magic.api.interfaces.MTGSerializable;
import org.magic.api.interfaces.abstracts.extra.AbstractKeyValueDao;
import org.magic.services.PluginRegistry;
import org.magic.services.TechnicalServiceManager;
import org.magic.services.tools.CryptoUtils;
import org.magic.services.tools.IDGenerator;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.POMReader;

import com.google.gson.JsonObject;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.event.command.CommandFailedEvent;
import io.lettuce.core.event.command.CommandListener;
import io.lettuce.core.event.command.CommandStartedEvent;
import io.lettuce.core.event.command.CommandSucceededEvent;

public class RedisDAO extends AbstractKeyValueDao {

	private RedisCommands<String, String> redisCommand;
	private StatefulRedisConnection<String, String> connection;
	private RedisClient redisClient;
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	@Override
	public Long incr(Class<?> c) {
		return redisCommand.incr("incr:"+c.getSimpleName());
	}
	
	@Override
	public String getVersion() {
		return POMReader.readVersionFromPom(RedisClient.class, "/META-INF/maven/io.lettuce/lettuce-core/pom.properties");
	}
	
	
	@Override
	public void init() throws SQLException {
		redisClient = RedisClient.create(getDBLocation());
		
		var listener = new CommandListener() {
			
			DAOInfo obj;
			
			
			@Override
			public void commandStarted(CommandStartedEvent event) {
				obj = new DAOInfo();
				obj.setDaoName(getName());
				obj.setConnectionName(getDBLocation());
				obj.setQuery(event.getCommand().getType() + " " + event.getCommand().getArgs().toCommandString());
			}
			
			@Override
			public void commandFailed(CommandFailedEvent event) {
				obj.setEnd(Instant.now());
				obj.setMessage(event.getCause().getMessage());
				TechnicalServiceManager.inst().store(obj);
			}
			
			@Override
			public void commandSucceeded(CommandSucceededEvent event) {
				obj.setEnd(Instant.now());
				TechnicalServiceManager.inst().store(obj);
			}
		};
		
		redisClient.addListener(listener);
		
		redisClient.setOptions(ClientOptions.builder()
											.autoReconnect(true)
											.pingBeforeActivateConnection(true)
											.build()
											);
		
		connection = redisClient.connect();
		redisCommand = connection.sync();
		
		initDefaultData();
		
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
		return new StringBuilder().append("redis://").append(getString("LOGIN")).append(":").append(getString("PASS")).append("@").append(getString("SERVER")).append(":").append(getString("PORT")).toString();
	}

	@Override
	public Map<String, Long> getDBSize() {
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
	
	@Override
	public List<String> listEditionsIDFromCollection(MagicCollection collection) throws SQLException {
		return redisCommand.keys(key(collection)+SEPARATOR+"*").stream().map(s->s.substring(s.lastIndexOf(SEPARATOR)+1)).toList();
	}
	
	@Override
	public void saveCollection(MagicCollection c) throws SQLException {
		redisCommand.sadd(KEY_COLLECTIONS,c.getName());
	}
	
	@Override
	public List<MagicCollection> listCollections() throws SQLException {
		return redisCommand.smembers(KEY_COLLECTIONS).stream().map(MagicCollection::new).toList();
	}
	
	@Override
	public void removeCollection(MagicCollection c) throws SQLException {
		redisCommand.srem(KEY_COLLECTIONS, c.getName());
	}
	
	@Override
	public void saveCard(MagicCard card, MagicCollection collection) throws SQLException {
		redisCommand.sadd(key(collection,card), serialiser.toJson(card));
	}
	

	@Override
	public int getCardsCount(MagicCollection c, MagicEdition me) throws SQLException {
		return redisCommand.scard(key(c,me)).intValue();
	}

	@Override
	public Map<String, Integer> getCardsCountGlobal(MagicCollection c) throws SQLException {
		
		var map = new HashMap<String,Integer>();
		
		redisCommand.keys(key(c)+SEPARATOR+"*").forEach(s->{
			var idSet = s.substring(s.lastIndexOf(SEPARATOR)+1);
			var count = redisCommand.scard(s).intValue();
			
			map.put(idSet, count);
		});
		return map;
	}




	@Override
	public List<MagicDeck> listDecks() throws SQLException {
		
		var ret = new ArrayList<MagicDeck>();
		
		redisCommand.keys(KEY_DECK+SEPARATOR+"*").forEach(s->{
			var d=  serialiser.fromJson(redisCommand.get(s), MagicDeck.class);
			ret.add(d);
			notify(d);
		});
		
		return ret;
	}
	
	@Override
	public void deleteDeck(MagicDeck d) throws SQLException {
		redisCommand.del(key(d));
	}

	
	@Override
	public Integer saveOrUpdateDeck(MagicDeck d) throws SQLException {
		if(d.getId()<0)
			d.setId(incr(MagicDeck.class).intValue());
		
		redisCommand.set(key(d), serialiser.toJson(d));
		return d.getId();
	}

	@Override
	public MagicDeck getDeckById(Integer id) throws SQLException {
		return serialiser.fromJson(redisCommand.get(KEY_DECK+SEPARATOR+id),MagicDeck.class);
	}

	@Override
	public List<MagicCard> listCardsFromCollection(MagicCollection collection, MagicEdition me) throws SQLException {
		return redisCommand.smembers(key(collection,me)).stream().map(s->serialiser.fromJson(s, MagicCard.class)).collect(Collectors.toList());
	}
	
	
	@Override
	public List<MagicCard> listCardsFromCollection(MagicCollection collection) throws SQLException {
		return redisCommand.smembers(key(collection)).stream().map(s->serialiser.fromJson(s, MagicCard.class)).toList();
	}

	@Override
	public void removeEdition(MagicEdition ed, MagicCollection col) throws SQLException {
		redisCommand.del(key(col,ed));
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
		redisCommand.rename(key(from,ed), key(to,ed));
	}

	
	@Override
	public void saveOrUpdateCardStock(MagicCardStock mcs) throws SQLException {
		if(mcs.getId()<0)
			mcs.setId(incr(MagicCardStock.class).intValue());
		
		mcs.setUpdated(false);
		redisCommand.set(key(mcs), serialiser.toJson(mcs));
	}

	@Override
	public void deleteStock(List<MagicCardStock> state) throws SQLException {
			for(var d : state)
				redisCommand.del(key(d));

	}

	@Override
	public List<MagicCardStock> listStocks() throws SQLException {
		
		var ret = new ArrayList<MagicCardStock>();
		
		redisCommand.keys(KEY_STOCKS+SEPARATOR+"*").forEach(s->{
			var d=  serialiser.fromJson(redisCommand.get(s), MagicCardStock.class);
			ret.add(d);
			notify(d);
		});
		
		return ret;
	}


	@Override
	public List<SealedStock> listSealedStocks() throws SQLException {
		var ret = new ArrayList<SealedStock>();
		
		redisCommand.keys(KEY_SEALED+SEPARATOR+"*").forEach(s->{
			var d=  serialiser.fromJson(redisCommand.get(s), SealedStock.class);
			ret.add(d);
			notify(d);
		});
		
		return ret;
	}

	@Override
	public void saveOrUpdateSealedStock(SealedStock mcs) throws SQLException {
		if(mcs.getId()<0)
			mcs.setId(incr(SealedStock.class).intValue());
		
		mcs.setUpdated(false);
		redisCommand.set(key(mcs), serialiser.toJson(mcs));

	}

	@Override
	public void deleteStock(SealedStock state) throws SQLException {
		redisCommand.del(key(state));
	}

	@Override
	public SealedStock getSealedStockById(Long id) throws SQLException {
			return serialiser.fromJson(redisCommand.get(KEY_SEALED+SEPARATOR+id), SealedStock.class);
	}


	@Override
	public List<Transaction> listTransactions() throws SQLException {
		var ret = new ArrayList<Transaction>();
		
		redisCommand.keys(KEY_TRANSACTIONS+SEPARATOR+"*").forEach(s->{
			var d=  serialiser.fromJson(redisCommand.get(s), Transaction.class);
			ret.add(d);
			notify(d);
		});
		
		return ret;
	}

	@Override
	public Long saveOrUpdateTransaction(Transaction t) throws SQLException {
		if(t.getId()<0)
			t.setId(incr(Transaction.class).intValue());
		
		redisCommand.set(key(t), serialiser.toJson(t));
		
		return t.getId();
	}

	@Override
	public void deleteTransaction(Transaction t) throws SQLException {
		redisCommand.del(key(t));
	}
	


	@Override
	public Transaction getTransaction(Long id) throws SQLException {
		return serialiser.fromJson(redisCommand.get(KEY_TRANSACTIONS+SEPARATOR+id), Transaction.class);
	}


	@Override
	public int saveOrUpdateContact(Contact c) throws SQLException {
		if(c.getId()<0)
			c.setId(incr(Contact.class).intValue());
		
		redisCommand.set(key(c), serialiser.toJson(c));
		
		return c.getId();
	}
	

	@Override
	public void deleteContact(Contact contact) throws SQLException {
		redisCommand.del(key(contact));

	}


	@Override
	public Contact getContactById(int id) throws SQLException {
		return serialiser.fromJson(redisCommand.get(KEY_CONTACTS+SEPARATOR+id), Contact.class);
	}

	@Override
	public List<Contact> listContacts() throws SQLException {
		var ret = new ArrayList<Contact>();
		
		redisCommand.keys(KEY_CONTACTS+SEPARATOR+"*").forEach(s->{
			var d=  serialiser.fromJson(redisCommand.get(s), Contact.class);
			ret.add(d);
			notify(d);
		});
		
		return ret;
	}

	
	@Override
	public List<MagicCardAlert> listAlerts() {
		var ret = new ArrayList<MagicCardAlert>();
		redisCommand.keys(KEY_ALERTS+SEPARATOR+"*").forEach(s->{
			var d=  serialiser.fromJson(redisCommand.get(s), MagicCardAlert.class);
			ret.add(d);
			notify(d);
		});
		
		return ret;
	}

	@Override
	public void saveAlert(MagicCardAlert alert) throws SQLException {
		redisCommand.set(key(alert), serialiser.toJson(alert));

	}

	@Override
	public void updateAlert(MagicCardAlert alert) throws SQLException {
		saveAlert(alert);

	}

	@Override
	public void deleteAlert(MagicCardAlert alert) throws SQLException {
		redisCommand.del(key(alert));
	}
	

	@Override
	public List<MagicNews> listNews() {
		var ret = new ArrayList<MagicNews>();
		redisCommand.keys(KEY_NEWS+SEPARATOR+"*").forEach(s->{
			var d=  serialiser.fromJson(redisCommand.get(s), MagicNews.class);
			ret.add(d);
			notify(d);
		});
		
		return ret;
	}

	@Override
	public void deleteNews(MagicNews n) throws SQLException {
		redisCommand.del(key(n));

	}

	@Override
	public void saveOrUpdateNews(MagicNews a) throws SQLException {
		if(a.getId()<0)
			a.setId(incr(Announce.class).intValue());
		
		redisCommand.set(key(a), serialiser.toJson(a));
	}

	

	@Override
	public Announce getAnnounceById(int id) throws SQLException {
		return serialiser.fromJson(redisCommand.get(KEY_ANNOUNCES+SEPARATOR+id), Announce.class);
	}

	@Override
	public int saveOrUpdateAnnounce(Announce a) throws SQLException {
		if(a.getId()<0)
			a.setId(incr(Announce.class).intValue());
		
		redisCommand.set(key(a), serialiser.toJson(a));
		
		return a.getId();
	}

	@Override
	public void deleteAnnounce(Announce a) throws SQLException {
		redisCommand.del(key(a));

	}

	@Override
	public void changePassword(Contact c, String newPassword) throws SQLException {
		c.setPassword(CryptoUtils.generateSha256(newPassword));
		saveOrUpdateContact(c);
	}

	//TODO found how to remove element from sets
	@Override
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
			var k = key(collection,mc.getCurrentSet());
			redisCommand.srem(k, serialiser.toJson(mc));
	}


	
	
	@Override
	public List<Transaction> listTransactions(Contact c) throws SQLException {
		 return listTransactions().stream().filter(t->t.getContact().getId()==c.getId()).toList();
	}


	
	@Override
	public List<MagicCollection> listCollectionFromCards(MagicCard mc) throws SQLException {
		var c = new ArrayList<MagicCollection>();
		
			for(var collection : listCollections())
			{
				if(listCardsFromCollection(collection).stream().anyMatch(card->IDGenerator.generate(card).equals(IDGenerator.generate(mc))))
					c.add(collection);
			}
			return c;
			
	}

	@Override
	public List<MagicCardStock> listStocks(MagicCard mc, MagicCollection col, boolean editionStrict) throws SQLException {

		if(editionStrict)
			return listStocks().stream().filter(mcs->mcs.getMagicCollection().getName().equals(col.getName())).filter(mcs->mcs.getProduct().getId().equals(mc.getId())).toList();
		else
			return listStocks().stream().filter(mcs->mcs.getMagicCollection().getName().equals(col.getName())).filter(mcs->mcs.getProduct().getName().equals(mc.getName())).toList();
	}

	@Override
	public Contact getContactByLogin(String email, String password) throws SQLException {
		var opt = listContacts().stream().filter(c->c.getEmail().equals(email) && c.getPassword().equals(CryptoUtils.generateSha256(password))).findFirst();
		
		if(opt.isPresent())
			return opt.get();
		
		throw new SQLException("No result Found");
		
	}

	@Override
	public Contact getContactByEmail(String email) throws SQLException {
		 var opt = listContacts().stream().filter(c->c.getEmail().equals(email)).findFirst();
		 
		 if(opt.isPresent())
			 return opt.get();
		 
		 return null;
	}

	@Override
	public boolean enableContact(String token) throws SQLException {
		var opt = listContacts().stream().filter(c->c.getTemporaryToken().equals(token)).findFirst();
		 
		 if(opt.isPresent())
		 {
			 var c = opt.get();
			 c.setTemporaryToken(null);
			 c.setActive(true);
			 saveOrUpdateContact(c);
			 return true;
		 }
		 return false;
	}



	@Override
	public List<Announce> listAnnounces(int max, STATUS stat) throws SQLException {
		return new ArrayList<>();
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
	public <T extends MTGSerializable> List<GedEntry<T>> listEntries(String classename, String id) throws SQLException {
		var arr = new ArrayList<GedEntry<T>>();
		
		redisCommand.smembers(KEY_GED+SEPARATOR+classename+SEPARATOR+id).forEach(s->{
			try {
				arr.add(readEntry(classename,id,null));
			} catch (SQLException e) {
				logger.error(e);
			}
		});
		return arr;
	}

	@Override
	public <T extends MTGSerializable> boolean deleteEntry(GedEntry<T> gedItem) throws SQLException {
		return false;
	}

	@Override
	public <T extends MTGSerializable> boolean storeEntry(GedEntry<T> gedItem) throws SQLException {
		var ret = redisCommand.sadd(key(gedItem), gedItem.toJson().toString());
		
		return ret>0;
		
	}


	@Override
	public <T extends MTGSerializable> GedEntry<T> readEntry(String classe, String idInstance, String fileName) throws SQLException {
		
		var ged = new GedEntry<T>();
		
		var sets = redisCommand.smembers(KEY_GED+SEPARATOR+classe+SEPARATOR+idInstance);
		
		for(var s : sets ) {
			var jo = serialiser.fromJson(s,JsonObject.class);
			ged.setId(idInstance);
			ged.setName(jo.get("name").getAsString());
			ged.setContent(CryptoUtils.fromBase64(jo.get("data").getAsString()));
			
			if(jo.get("md5")!=null && !CryptoUtils.getMD5(ged.getContent()).equals(jo.get("md5").getAsString()))
				throw new SQLException("MD5 Error for " + fileName +" : " + CryptoUtils.getMD5(ged.getContent()) + " " + jo.get("md5").getAsString());

			
			ged.setIsImage(ImageTools.isImage(ged.getContent()));
			
			try {
				ged.setClasse(PluginRegistry.inst().loadClass(classe));
			} catch (ClassNotFoundException e) {
				logger.error(e);
			}
	
		}
		
		
		
		return ged;
	}

	@Override
	public <T extends MTGSerializable> List<GedEntry<T>> listAllEntries() throws SQLException {
		return new ArrayList<>();
	}
	
	@Override
	public boolean executeQuery(String query) throws SQLException {
		return false;
	}

	@Override
	public String getName() {
		return "Redis";
	}

}

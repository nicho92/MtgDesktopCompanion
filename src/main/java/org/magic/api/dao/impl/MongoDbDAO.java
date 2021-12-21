package org.magic.api.dao.impl;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.magic.tools.MTG.getPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.swing.ImageIcon;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.bson.Document;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.magic.api.beans.Announce;
import org.magic.api.beans.ConverterItem;
import org.magic.api.beans.DAOInfo;
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
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGNewsProvider;
import org.magic.api.interfaces.MTGStorable;
import org.magic.api.interfaces.abstracts.AbstractMagicDAO;
import org.magic.services.MTGConstants;
import org.magic.services.PluginRegistry;
import org.magic.tools.Chrono;
import org.magic.tools.CryptoUtils;
import org.magic.tools.IDGenerator;
import org.magic.tools.ImageTools;

import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.event.CommandFailedEvent;
import com.mongodb.event.CommandListener;
import com.mongodb.event.CommandStartedEvent;
import com.mongodb.event.CommandSucceededEvent;

public class MongoDbDAO extends AbstractMagicDAO {

	private static final String EMAIL = "email";
	private static final String PASSWORD = "password";
	private MongoDatabase db;
	private String colCards = "cards";
	private String colCollects = "collects";
	private String colStocks = "stocks";
	private String colAlerts = "alerts";
	private String colNews = "news";
	private String colOrders = "orders";
	private String colSealed = "sealed";
	private String colContacts = "contacts";
	private String colTransactions = "transactions";
	private String colConversionItem = "conversionsItems";
	private String colDecks = "decks";
	private String colAnnounces="announces";
	private String colGed="ged";
	private String colFavorites="favorites";
	
	private String dbIDField = "db_id";
	private String dbAlertField = "alertItem";
	private String dbNewsField = "newsItem";
	private String dbStockField = "stockItem";
	private String dbColIDField = "collection.name";
	private String dbTypeNewsField = "typeNews";
	private MongoClient client;
	private JsonWriterSettings setts;
	
	
	@Override
	public Map<String, String> getDefaultAttributes() {
	return Map.of(
			SERVERNAME, "localhost",
			SERVERPORT, "27017",
			DB_NAME, "mtgdesktopcompanion",
			LOGIN, "login",
			PASS, "",
			DRIVER, "mongodb://",
			PARAMETERS, ""
			);
	}
			
	private <T> T deserialize(Object o, Class<T> classe) {
		
		if(o==null)
			return null;
	
		return serialiser.fromJson(String.valueOf(o.toString()), classe);

	}
	
	private MagicDeck deserializeDeck(BasicDBObject o) {
		if(o==null)
			return null;
		
		return serialiser.importDeck(o.toJson(setts),o.getString("name"));
	}

	private String serialize(Object o) {
		return serialiser.toJson(o);
	}

	
	@Override
	public String getVersion() {
		return "3.12.10";
	
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	@Override
	public void unload() {
		if(client!=null)
			client.close();
	}
	
	
	public void init() throws SQLException {

		var pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),fromProviders(PojoCodecProvider.builder().automatic(true).build()));
			
		setts = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
		

		
		var temp = new StringBuilder();
					  temp.append(getString(DRIVER)).append(getString(LOGIN)).append(":").append(getString(PASS));
					  temp.append("@").append(getString(SERVERNAME));
					  if(!getString(SERVERPORT).isEmpty())
						  temp.append(":").append(getString(SERVERPORT));
					  
					  temp.append("/").append(getString(DB_NAME));
					  
					  if(!getString(PARAMETERS).isEmpty())
						  temp.append("?").append(getString(PARAMETERS));
					  
					  
			logger.debug(getName() + " connected to " + temp);		  
				
			MongoClientSettings settings = MongoClientSettings.builder()
			                .applyConnectionString(new ConnectionString(temp.toString()))
			                .addCommandListener(new CommandListener() {
								
			                	
			                	DAOInfo e;
			                	
								@Override
								public void commandSucceeded(CommandSucceededEvent event) {
										e.setDuration(event.getElapsedTime(TimeUnit.MILLISECONDS));
										e.setEndDate(Instant.now());
										e.setClasseName(event.getClass().getCanonicalName());
									listdao.add(e);
								}
								
								@Override
								public void commandStarted(CommandStartedEvent event) {
									e = new DAOInfo();
									e.setQuery(event.getCommand().getFirstKey() + " " + event.getCommand().get(event.getCommand().getFirstKey()));
									e.setConnectionName(event.getConnectionDescription().toString());
								}
								
								@Override
								public void commandFailed(CommandFailedEvent event) {
									e.setMessage(event.getThrowable().getMessage());
									e.setDuration(event.getElapsedTime(TimeUnit.MILLISECONDS));
									e.setEndDate(Instant.now());
									listdao.add(e);
								}
							})
			                .build();
			
			client = MongoClients.create(settings);
		
			db = client.getDatabase(getString(DB_NAME)).withCodecRegistry(pojoCodecRegistry);
			
			createDB();
			logger.info("init " + getName() + " done");
			
		
	}

	public boolean createDB() {
		
			var populateCollections=true;
			for(String s : new String[] {colCards,colCollects,colStocks,colAlerts,colNews,colOrders,colSealed,colTransactions,colContacts,colDecks,colConversionItem,colAnnounces,colGed,colFavorites})
			{
				try {
					db.createCollection(s);
				} catch (Exception e1) {
					logger.trace(s +" already exist");
					populateCollections=false;
				}
			}
			
			if(populateCollections)
				for(String s:  MTGConstants.getDefaultCollectionsNames())
					try {
						saveCollection(new MagicCollection(s));
					} catch (SQLException e) {
						logger.error(e);
					}
			
			
		return populateCollections;
	}
	
	

	@Override
	public <T extends MTGStorable> boolean storeEntry(GedEntry<T> gedItem) {
		
		var obj = BasicDBObject.parse(gedItem.toJson().toString());
			  obj.put("md5", CryptoUtils.getMD5(gedItem.getContent()));
		
		db.getCollection(colGed, BasicDBObject.class).insertOne(obj);
		notify(true);
		return true;
	}
	
	
	@Override
	public <T extends MTGStorable> GedEntry<T> readEntry(String classe, String idInstance, String fileName) throws SQLException {
		try{ 
			return (GedEntry<T>) list(classe,idInstance,fileName).get(0);
		}
		catch(IndexOutOfBoundsException ioobe)
		{
			throw new SQLException(ioobe);
		}
	}
	
	@Override
	public <T extends MTGStorable> List<GedEntry<T>> listAllEntries() throws SQLException {
		return list(null,null,null);
	}
	
	@Override
	public <T extends MTGStorable> List<GedEntry<T>> listEntries(String classename, String id) {
		return list(classename,id,null);
	}
	
	
	private <T extends MTGStorable> List<GedEntry<T>> list(String classename, String id, String fileName) {
		
		var arr = new ArrayList<GedEntry<T>>();
		FindIterable<BasicDBObject> it = null; 
		
		Bson f = null;		
		
		if(classename!=null && id!=null  && fileName==null)
			f = Filters.and(Filters.eq("classe", classename),Filters.eq("id", id));
		
		if(classename!=null && id!=null && fileName!=null)
			f= Filters.and(Filters.eq("classe", classename),Filters.eq("id", id),Filters.eq("name", fileName));
		
		
		logger.debug("reading entries with f="+f);
		
		if(f==null)
			it = db.getCollection(colGed, BasicDBObject.class).find();
		else
			it = db.getCollection(colGed, BasicDBObject.class).find(f);
		
		it.forEach((Consumer<BasicDBObject>) result -> { 
			var entry = new GedEntry<T>();
				entry.setId(result.get("id").toString());
				entry.setName(result.get("name").toString());
				entry.setContent(CryptoUtils.fromBase64(result.getString("data")));
				entry.setIsImage(ImageTools.isImage(entry.getContent()));
				
				try {
					entry.setClasse(PluginRegistry.inst().loadClass(result.getString("classe")));
				} catch (ClassNotFoundException e) {
					logger.debug("can't load "+classename,e);
				}
				
				
				if(CryptoUtils.getMD5(entry.getContent()).equals(result.get("md5")))
					arr.add(entry);
				else
					logger.error("MD5 error for " + entry.getName());
				
			}
		);
		return arr;
	}
	
	
	
	
	@Override
	public <T extends MTGStorable> boolean deleteEntry(GedEntry<T> gedItem) {
		db.getCollection(colGed).deleteOne(Filters.and(Filters.eq("name", gedItem.getName()),Filters.eq("classe", gedItem.getClasse().getCanonicalName()),Filters.eq("id", gedItem.getId())));
		notify(gedItem);
		return true;
	}
	
	
	
	@Override
	public void deleteStock(SealedStock state) throws SQLException {
		db.getCollection(colSealed).deleteOne(Filters.eq("id", state.getId()));
		notify(state);
		
	}
	
	@Override
	public void deleteConversionItem(ConverterItem n) throws SQLException {
		db.getCollection(colConversionItem).deleteOne(Filters.eq("id", n.getId()));
		notify(n);
		
	}
	

	@Override
	public void deleteContact(Contact contact)throws SQLException {
		
		if(!listTransactions(contact).isEmpty())
			throw new SQLException(contact + " has transactions and can't be removed ");
		
		db.getCollection(colContacts).deleteOne(Filters.eq("id", contact.getId()));
		notify(contact);
		
	}
	
		
	@Override
	public List<MagicDeck> listDecks() throws SQLException {
		List<MagicDeck> stocks = new ArrayList<>();
		db.getCollection(colDecks, BasicDBObject.class).find().forEach((Consumer<BasicDBObject>) result -> { 
				MagicDeck d = deserializeDeck(result);
				stocks.add(d);
				notify(d);
			}
		);
		return stocks;
	}
	
	@Override
	public List<ConverterItem> listConversionItems() throws SQLException {
		List<ConverterItem> stocks = new ArrayList<>();
		db.getCollection(colConversionItem, BasicDBObject.class).find().forEach((Consumer<BasicDBObject>) result -> stocks.add(deserialize(result, ConverterItem.class)));
		return stocks;
	}
	
	
	@Override
	public MagicDeck getDeckById(Integer id) throws SQLException {
		return deserializeDeck(db.getCollection(colDecks,BasicDBObject.class)
								 .find(Filters.eq("id", id))
								 .first());
		
	}
	
	

	@Override
	public Integer saveOrUpdateDeck(MagicDeck state) throws SQLException {
		logger.debug("saving deck " + state);
		if (state.getId() == -1) {
			state.setId(Integer.parseInt(getNextSequence().toString()));
			db.getCollection(colDecks, BasicDBObject.class).insertOne(BasicDBObject.parse(serialiser.toJsonDeck(state).toString()));

		} else {
			state.setDateUpdate(new Date());
			UpdateResult res = db.getCollection(colDecks, BasicDBObject.class).replaceOne(Filters.eq("id", state.getId()),BasicDBObject.parse(serialiser.toJsonDeck(state).toString()));
			logger.trace(res);
		}
		
		notify(state);
		return state.getId();
	}
	
	@Override
	public void deleteDeck(MagicDeck d) throws SQLException {
		logger.debug("Deleting " + d + " " + d.getId());
		DeleteResult dr = db.getCollection(colDecks, BasicDBObject.class).deleteOne(Filters.eq("id",d.getId()));
		logger.debug(dr.toString());
	}
	
	
	
	@Override
	public List<SealedStock> listSealedStocks() throws SQLException {
		List<SealedStock> stocks = new ArrayList<>();
		db.getCollection(colSealed, BasicDBObject.class).find().forEach((Consumer<BasicDBObject>) result -> stocks.add(deserialize(result, SealedStock.class)));
		return stocks;
	}
	
	@Override
	public void saveOrUpdateConversionItem(ConverterItem state) throws SQLException {
		logger.debug("saving conversion " + state);
		if (state.getId() == -1) {
			state.setId(Integer.parseInt(getNextSequence().toString()));
			db.getCollection(colConversionItem, BasicDBObject.class).insertOne(BasicDBObject.parse(serialize(state)));
			state.setUpdated(false);
		} else {
			
			UpdateResult res = db.getCollection(colConversionItem, BasicDBObject.class).replaceOne(Filters.eq("id", state.getId()),BasicDBObject.parse(serialize(state)));
			logger.trace(res);
			state.setUpdated(false);
		}
		
		notify(state);
		
	}
	
	
	@Override
	public void saveOrUpdateSealedStock(SealedStock state) throws SQLException {
		logger.debug("saving stock " + state);
		if (state.getId() == -1) {
			state.setId(Integer.parseInt(getNextSequence().toString()));
			db.getCollection(colSealed, BasicDBObject.class).insertOne(BasicDBObject.parse(serialize(state)));

		} else {
			
			UpdateResult res = db.getCollection(colSealed, BasicDBObject.class).replaceOne(Filters.eq("id", state.getId()),BasicDBObject.parse(serialize(state)));
			logger.trace(res);
		}
		
		notify(state);
		
	}

	@Override
	public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException {
		logger.debug("saveCard " + mc + " in " + collection);
		
		var obj = new BasicDBObject();
		obj.put(dbIDField, IDGenerator.generate(mc));
		obj.put("card", mc);
		obj.put("collection", collection);
		obj.put("dateUpdate", new Date().getTime());
		obj.put("set", mc.getCurrentSet().getId().toUpperCase());
		String json = serialize(obj);
		
		db.getCollection(colCards, BasicDBObject.class).insertOne(BasicDBObject.parse(json));
	}

	@Override
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
		logger.debug("removeCard " + mc + " from " + collection);

		DeleteResult dr = db.getCollection(colCards, BasicDBObject.class).deleteMany(Filters.and(Filters.eq(dbIDField,IDGenerator.generate(mc)),Filters.eq(dbColIDField,collection.getName())));
		logger.debug(dr.toString());

	}

	@Override
	public List<MagicCard> listCards() throws SQLException {
		logger.debug("list all cards");
		List<MagicCard> list = new ArrayList<>();
		db.getCollection(colCards, BasicDBObject.class).find().forEach((Consumer<BasicDBObject>) result -> list
				.add(deserialize(result.get("card").toString(), MagicCard.class)));
		return list;
	}
	
	@Override
	public Map<String, Integer> getCardsCountGlobal(MagicCollection c) throws SQLException {
		Map<String, Integer> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		List<Bson> aggr =  	Arrays.asList(
										Aggregates.match(Filters.eq(dbColIDField, c.getName())),
										Aggregates.group("$set", Accumulators.sum("count", 1))
									   );
		db.getCollection(colCards, BasicDBObject.class).aggregate(aggr).forEach((Consumer<BasicDBObject>) document -> map.put(document.getString("_id"), document.getInt("count")));
		return map;
	}


	
	@Override
	public List<MagicCard> listCardsFromCollection(MagicCollection collection) throws SQLException {
		return listCardsFromCollection(collection, null);
	}

	@Override
	public int getCardsCount(MagicCollection cols, MagicEdition me) throws SQLException {
		logger.debug("getCardsCount " + cols + " me:" + me);
		if (me != null) {
			return (int) db.getCollection(colCards, BasicDBObject.class).countDocuments(Filters.and(Filters.eq(dbColIDField,cols.getName()),Filters.eq("card.editions.0.id",me.getId().toUpperCase())));
		} else {
			return (int) db.getCollection(colCards, BasicDBObject.class).countDocuments(new BasicDBObject(dbColIDField, cols.getName()));
		}
	}
	
	
	@Override
	public List<MagicCard> listCardsFromCollection(MagicCollection collection, MagicEdition me) throws SQLException {
	
		List<MagicCard> ret = new ArrayList<>();
		
		var b = Filters.eq(dbColIDField,collection.getName());
		
		if (me != null) {
			b = Filters.and(b,Filters.eq("card.editions.0.id",me.getId().toUpperCase()));
		}
		var c = new Chrono();
		c.start();
		db.getCollection(colCards, BasicDBObject.class).find(b).forEach((Consumer<BasicDBObject>) result -> ret.add(deserialize(result.get("card"), MagicCard.class)));
		logger.debug("list cards from " + collection + "/" + me + " :" + b+": done in "+c.stop()+"s.");
		
		return ret;
	}

	@Override
	public List<String> listEditionsIDFromCollection(MagicCollection collection) throws SQLException {
		List<String> ret = new ArrayList<>();
		db.getCollection(colCards, BasicDBObject.class).distinct("card.editions.0.id", Filters.eq(dbColIDField, collection.getName()), String.class).into(ret);
		return ret;
	}

	@Override
	public MagicCollection getCollection(String name) throws SQLException {
		return db.getCollection(colCollects, MagicCollection.class).find(Filters.eq("name", name)).first();
	}

	@Override
	public void saveCollection(MagicCollection c) throws SQLException {
		if(getCollection(c.getName())==null)
			db.getCollection(colCollects, MagicCollection.class).insertOne(c);
		else
			throw new SQLException(c.getName() + " already exists");
	}

	@Override
	public void removeCollection(MagicCollection c) throws SQLException {
		var query = new BasicDBObject();
		query.put(dbColIDField, c.getName());
		db.getCollection(colCollects, MagicCollection.class).deleteOne(Filters.eq("name", c.getName()));
	}

	@Override
	public List<MagicCollection> listCollections() throws SQLException {
		MongoCollection<MagicCollection> collection = db.getCollection(colCollects, MagicCollection.class);
		List<MagicCollection> cols = new ArrayList<>();
		collection.find().into(cols);
		return cols;
	}

	@Override
	public void removeEdition(MagicEdition me, MagicCollection col) throws SQLException {
		DeleteResult dr = db.getCollection(colCards).deleteMany(Filters.and(Filters.eq(dbColIDField, col.getName()),Filters.eq("card.editions.0.id", me.getId().toUpperCase())));
		logger.debug(dr);
	}

	@Override
	public String getDBLocation() {
		return getString(SERVERNAME)+":"+getInt(SERVERPORT);
	}

	@Override
	public Map<String,Long> getDBSize() {
		
		return new HashMap<>();
		
		//return db.runCommand(new BasicDBObject("dbstats", 1)).getLong("dataSize");
	}

	@Override
	public String getName() {
		return "MongoDB";
	}

	@Override
	public List<MagicCollection> listCollectionFromCards(MagicCard mc) throws SQLException {

		List<MagicCollection> ret = new ArrayList<>();
		db.getCollection(colCards, BasicDBObject.class).distinct(dbColIDField, Filters.eq(dbIDField, IDGenerator.generate(mc)), String.class)
				.forEach((Consumer<String>) result -> {
					try {
						logger.trace("found " + mc + " in " + result);
						ret.add(getCollection(result));
					} catch (SQLException e) {
						logger.error("Error", e);
					}

				});

		return ret;
	}

	@Override
	public List<MagicCardStock> listStocks(MagicCard mc, MagicCollection col,boolean editionStrict) throws SQLException {
		ArrayList<MagicCardStock> ret = new ArrayList<>();
		db.getCollection(colStocks, BasicDBObject.class)
				.find(Filters.and(Filters.eq(dbIDField,IDGenerator.generate(mc)),Filters.eq(dbStockField+".magicCollection.name",col.getName())))
				.forEach((Consumer<BasicDBObject>) result -> ret
				.add(deserialize(result.get(dbStockField).toString(), MagicCardStock.class)));

		return ret;
	}

	public List<MagicCardStock> listStocks() throws SQLException {
		List<MagicCardStock> stocks = new ArrayList<>();
		db.getCollection(colStocks, BasicDBObject.class).find().forEach((Consumer<BasicDBObject>) result -> stocks
				.add(deserialize(result.get(dbStockField).toString(), MagicCardStock.class)));
		return stocks;
	}

	@Override
	public void saveOrUpdateCardStock(MagicCardStock state) throws SQLException {
		logger.debug("saving stock " + state);
		state.setUpdated(false);
		if (state.getId() == -1) {
			state.setId(Integer.parseInt(getNextSequence().toString()));
			var obj = new BasicDBObject();
			obj.put(dbStockField, state);
			obj.put(dbIDField, IDGenerator.generate(state.getProduct()));
			db.getCollection(colStocks, BasicDBObject.class).insertOne(BasicDBObject.parse(serialize(obj)));

		} else {
			
			var obj = new BasicDBObject();
			obj.put(dbStockField, state);
			obj.put(dbIDField, IDGenerator.generate(state.getProduct()));
			UpdateResult res = db.getCollection(colStocks, BasicDBObject.class).replaceOne(Filters.eq("stockItem.idstock", state.getId()),BasicDBObject.parse(serialize(obj)));
			logger.debug(res);
		}
		notify(state);
	}

	@Override
	public void saveAlert(MagicCardAlert alert) throws SQLException {
		logger.debug("saving alert " + alert);

		alert.setId(getNextSequence().toString());
		var obj = new BasicDBObject();
		obj.put(dbAlertField, alert);
		obj.put(dbIDField, IDGenerator.generate(alert.getCard()));
		db.getCollection(colAlerts, BasicDBObject.class).insertOne(BasicDBObject.parse(serialize(obj)));
		listAlerts.put(alert.getId(),alert);
	}

	@Override
	public void initAlerts() {
		db.getCollection(colAlerts, BasicDBObject.class).find().forEach((Consumer<BasicDBObject>) result ->{ 
				
				MagicCardAlert al = deserialize(result.get(dbAlertField).toString(), MagicCardAlert.class);
				listAlerts.put(al.getId(),al);
				});
	}


	@Override
	public void updateAlert(MagicCardAlert alert) throws SQLException {
		var obj = new BasicDBObject();
		obj.put(dbAlertField, alert);
		obj.put(dbIDField, IDGenerator.generate(alert.getCard()));
		UpdateResult res = db.getCollection(colAlerts, BasicDBObject.class).replaceOne(Filters.eq("alertItem.id", alert.getId()),BasicDBObject.parse(serialize(obj)));
		logger.debug(res);

	}

	@Override
	public void deleteAlert(MagicCardAlert alert) throws SQLException {
		logger.debug("delete alert " + alert);
		Bson filter = new Document("alertItem.id", alert.getId());
		DeleteResult res = db.getCollection(colAlerts).deleteOne(filter);
		listAlerts.remove(alert.getId());
		logger.debug(res);
	}

	@Override
	public void deleteStock(List<MagicCardStock> state) throws SQLException {
		logger.debug("remove stocks : " + state.size() + " items");
		for (MagicCardStock s : state) {
			Bson filter = new Document("stockItem.idstock", s.getId());
			db.getCollection(colStocks).deleteOne(filter);
			notify(s);
		}
	}

	@Override
	public void backup(File f) throws IOException {
		throw new NotImplementedException("Not yet implemented");
	}

	private void createCountersCollection(MongoCollection<Document> countersCollection) {
		var document = new Document();
		document.append("_id", "stock_increment");
		document.append("seq", 1);
		countersCollection.insertOne(document);
	}

	private Object getNextSequence() {
		MongoCollection<Document> countersCollection = db.getCollection("idSequences");
		if (countersCollection.countDocuments() == 0) {
			createCountersCollection(countersCollection);
		}
		var searchQuery = new Document("_id", "stock_increment");
		var increase = new Document("seq", 1);
		var updateQuery = new Document("$inc", increase);
		Document result = countersCollection.findOneAndUpdate(searchQuery, updateQuery);
		return result.get("seq");
	}

	@Override
	public List<MagicNews> listNews() {
		List<MagicNews> news = new ArrayList<>();
		db.getCollection(colNews, BasicDBObject.class).find().forEach((Consumer<BasicDBObject>) result ->{ 
			MagicNews mn = deserialize(result.get(dbNewsField).toString(), MagicNews.class);
			try{
				mn.setProvider(getPlugin(result.get(dbTypeNewsField).toString(),MTGNewsProvider.class));
			}catch(Exception e)
			{
				logger.error("error get typeNews provider "+result,e);
			}
			news.add(mn);
		});
			return news;
	}

	@Override
	public void deleteNews(MagicNews n) {
		Bson filter = new Document("newsItem.id", n.getId());
		db.getCollection(colNews).deleteOne(filter);
	}

	@Override
	public void saveOrUpdateNews(MagicNews state) {
	
		if (state.getId() == -1) {
			logger.debug("saving " + state);
			state.setId(Integer.parseInt(getNextSequence().toString()));
			var obj = new BasicDBObject();
			obj.put(dbNewsField, state);
			obj.put(dbTypeNewsField, state.getProvider().getName());
			db.getCollection(colNews, BasicDBObject.class).insertOne(BasicDBObject.parse(serialize(obj)));

		} else {
			logger.debug("update " + state);

			Bson filter = new Document("newsItem.id", state.getId());
			var obj = new BasicDBObject();
			obj.put(dbNewsField, state);
			obj.put(dbTypeNewsField, state.getProvider().getName());
			logger.debug(filter);
			UpdateResult res = db.getCollection(colNews, BasicDBObject.class).replaceOne(filter,BasicDBObject.parse(serialize(obj)));
			logger.debug(res);
		}

	}



	@Override
	public void saveOrUpdateOrderEntry(OrderEntry state) throws SQLException {
		logger.debug("saving " + state);
		state.setUpdated(false);
		if (state.getId() == -1) {
			state.setId(Integer.parseInt(getNextSequence().toString()));
			db.getCollection(colOrders, BasicDBObject.class).insertOne(BasicDBObject.parse(serialize(state)));

		} else {
			UpdateResult res = db.getCollection(colOrders, BasicDBObject.class).replaceOne(Filters.eq("id", state.getId()),BasicDBObject.parse(serialize(state)));
			logger.debug(res);
		}
		
	}

	@Override
	public void deleteOrderEntry(List<OrderEntry> state) throws SQLException {
		logger.debug("remove " + state + " item(s) in orders");
		for (OrderEntry s : state) {
			DeleteResult res = db.getCollection(colOrders).deleteOne(Filters.eq("id", s.getId()));
			logger.debug(res.getDeletedCount() + " item deleted");
		}
	}

	@Override
	protected void initOrders() {
		db.getCollection(colOrders, BasicDBObject.class).find().forEach((Consumer<BasicDBObject>) result ->{
				OrderEntry o = deserialize(result, OrderEntry.class);
				listOrders.put(o.getId(),o);
			});
		
	}
	
	@Override
	public void executeQuery(String query) throws SQLException {
		throw new SQLException("Not implemented");
		
	}
	

	@Override
	public boolean equals(Object obj) {
		
		if(obj ==null)
			return false;
		
		return hashCode()==obj.hashCode();
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public List<Transaction> listTransactions(Contact c) throws SQLException {
		List<Transaction> trans = new ArrayList<>();
		db.getCollection(colTransactions,BasicDBObject.class).find(Filters.eq("contact.id", c.getId())).forEach((Consumer<BasicDBObject>) result ->{
			Transaction o = deserialize(result.toString(), Transaction.class);
			trans.add(o);
		});
		
		return trans;
	}

	

	@Override
	public Announce getAnnounceById(int id) throws SQLException {
		return deserialize(db.getCollection(colAnnounces,BasicDBObject.class)
								 .find(Filters.eq("id", id))
								 .first(),Announce.class);
		
	}
	
	
	
	@Override
	public List<Announce> listAnnounces(int max,boolean all) {
		List<Announce> trans = new ArrayList<>();
		
		var it = db.getCollection(colAnnounces, BasicDBObject.class).find().sort(Sorts.descending("id"));
			
		if(max>0) 
			it = it.limit(max);
		
		it.forEach((Consumer<BasicDBObject>) result ->{
			Announce o = deserialize(result.toString(), Announce.class);
			trans.add(o);
		});
		
		
		return trans;
	}

	@Override
	public int saveOrUpdateAnnounce(Announce t) throws SQLException {
		if (t.getId() == -1) {
			t.setId(Integer.parseInt(getNextSequence().toString()));
			db.getCollection(colAnnounces, BasicDBObject.class).insertOne(BasicDBObject.parse(serialize(t)));

		} else {
			UpdateResult res = db.getCollection(colAnnounces, BasicDBObject.class).replaceOne(Filters.eq("id",t.getId()),BasicDBObject.parse(serialize(t)));
			logger.trace(res);
		}
		return t.getId();
		
	}

	@Override
	public void deleteAnnounce(Announce a) throws SQLException {
		DeleteResult rs = db.getCollection(colAnnounces).deleteOne(Filters.eq("id",a.getId()));
		logger.debug(rs);
		
	}

	
	
	@Override
	public List<Transaction> listTransactions() throws SQLException {

		List<Transaction> trans = new ArrayList<>();
		db.getCollection(colTransactions, BasicDBObject.class).find().forEach((Consumer<BasicDBObject>) result ->{
			Transaction o = deserialize(result.toString(), Transaction.class);
			trans.add(o);
		});
		
		
		return trans;
	}


	@Override
	public List<Contact> listContacts() throws SQLException {
		List<Contact> trans = new ArrayList<>();
		db.getCollection(colContacts, BasicDBObject.class).find().forEach((Consumer<BasicDBObject>) result ->{
			Contact o = deserialize(result.toString(), Contact.class);
			trans.add(o);
		});
		
		
		return trans;
	}
	
	@Override
	public int saveOrUpdateTransaction(Transaction t) throws SQLException {
		if (t.getId() == -1) {
			t.setId(Integer.parseInt(getNextSequence().toString()));
			db.getCollection(colTransactions, BasicDBObject.class).insertOne(BasicDBObject.parse(serialize(t)));

		} else {
			UpdateResult res = db.getCollection(colTransactions, BasicDBObject.class).replaceOne(Filters.eq("id",t.getId()),BasicDBObject.parse(serialize(t)));
			logger.trace(res);
		}
		return t.getId();
	}



	@Override
	public void deleteTransaction(Transaction t) throws SQLException {
		DeleteResult rs = db.getCollection(colTransactions).deleteOne(Filters.eq("id",t.getId()));
		logger.debug(rs);
	}


	@Override
	public boolean enableContact(String token) throws SQLException {
		
		Contact doc = deserialize(db.getCollection(colContacts, BasicDBObject.class).find(Filters.eq("temporaryToken",token)).first(),Contact.class);
		
		if(doc!=null)
		{
			doc.setTemporaryToken(null);
			doc.setActive(true);
			saveOrUpdateContact(doc);
			return true;
		}
		
		return false;
			
	}
	
	
	@Override
	public void changePassword(Contact c, String newPassword) throws SQLException {
		var passBson = new BasicDBObject();
		passBson.put(PASSWORD, IDGenerator.generateSha256(newPassword)); 

		var updateBson = new BasicDBObject();
		updateBson.put("$set", passBson); 
		
		
		var res = db.getCollection(colContacts, BasicDBObject.class).updateOne(Filters.eq("id",c.getId()),updateBson);
		logger.debug(res);
	}
	
	
	@Override
	public int saveOrUpdateContact(Contact c) throws SQLException {
		if (c.getId() == -1) 
		{
			c.setPassword(IDGenerator.generateSha256(c.getPassword()));
			if(db.getCollection(colContacts, BasicDBObject.class).find(Filters.eq(EMAIL,c.getEmail())).first()!=null)
				throw new SQLException("Please choose another email");
		
			c.setId(Integer.parseInt(getNextSequence().toString()));
			
			db.getCollection(colContacts, BasicDBObject.class).insertOne(BasicDBObject.parse(serialize(c)));

		} 
		else {
			var contactUpdateData = new BasicDBObject();
			try {
				BeanUtils.describe(c).keySet().forEach(k->{
					try {
						if(!k.equalsIgnoreCase(PASSWORD))
							contactUpdateData.put(k, PropertyUtils.getProperty(c,k));
						
					}  catch (Exception e) {
						logger.error(e);
					}
					
				});
			} catch (Exception e) {
				logger.error(e);
			}
			
		
			var update = new BasicDBObject();
						  update.put("$set", contactUpdateData);
		
			
			var res = db.getCollection(colContacts, BasicDBObject.class).updateOne(Filters.eq("id",c.getId()),update);
			logger.debug(res);
		}
		return c.getId();
	}


	
	@Override
	public Transaction getTransaction(int id) throws SQLException {
		return deserialize(db.getCollection(colTransactions,BasicDBObject.class).find(Filters.eq("id", id)).first(),Transaction.class);
	}
	
	@Override
	public Contact getContactById(int id) throws SQLException {
		return deserialize(db.getCollection(colContacts).find(Filters.eq("id", id),BasicDBObject.class).first(),Contact.class);
	}

	@Override
	public SealedStock getSealedStockById(int id) throws SQLException {
		return deserialize(db.getCollection(colSealed).find(Filters.eq("id", id),BasicDBObject.class).first(),SealedStock.class);
	}


	@Override
	public Contact getContactByLogin(String email, String password) throws SQLException {
		return deserialize(db.getCollection(colContacts,BasicDBObject.class)
							 .find(Filters.and(Filters.and(Filters.eq(EMAIL, email),Filters.eq(PASSWORD, IDGenerator.generateSha256(password))),Filters.eq("active",true)))
							 .first()
							 ,Contact.class);
	}

	@Override
	public Contact getContactByEmail(String email) throws SQLException {
		return deserialize(db.getCollection(colContacts,BasicDBObject.class)
							 .find(Filters.and(Filters.eq(EMAIL, email),Filters.eq("active",true)))
							 .first()
							 ,Contact.class);
	}

	@Override
	public void saveFavorites(int idContact, int idAnnounce, String classeName) throws SQLException {
		
		var obj = new BasicDBObject();
			obj.put("idContact", idContact);
			obj.put("idAnnounce", idAnnounce);
			obj.put("classeName", classeName);
			
		db.getCollection(colFavorites, BasicDBObject.class).insertOne(obj);
		
	}

	@Override
	public void deleteFavorites(int idContact, int idAnnounce, String classeName) throws SQLException {
		DeleteResult rs = db.getCollection(colFavorites).deleteOne(Filters.and(Filters.eq("idContact", idContact),Filters.eq("idAnnounce",idAnnounce),Filters.eq("classeName",classeName)));
		logger.debug(rs);
		
	}

	@Override
	public List<Announce> listFavorites(Contact c, String classeName) throws SQLException {
		List<Announce> trans = new ArrayList<>();
		db.getCollection(colFavorites, BasicDBObject.class).find(Filters.and(Filters.eq("idContact", c.getId()),Filters.eq("classeName",classeName))).forEach((Consumer<BasicDBObject>) result ->{
			
			try {
				var announce = getAnnounceById(Integer.parseInt(result.get("idAnnounce").toString()));
				trans.add(announce);
			}catch (Exception e) {
				logger.error(e);
			}
			
		});
		return trans;
	}
}
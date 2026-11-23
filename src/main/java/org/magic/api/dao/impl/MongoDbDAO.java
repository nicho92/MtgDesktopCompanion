package org.magic.api.dao.impl;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.magic.services.tools.MTG.getPlugin;

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

import org.apache.commons.beanutils.PropertyUtils;
import org.bson.Document;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
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
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.GedEntry;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.beans.technical.audit.DAOInfo;
import org.magic.api.interfaces.MTGNewsProvider;
import org.magic.api.interfaces.abstracts.AbstractMagicDAO;
import org.magic.api.interfaces.abstracts.AbstractTechnicalServiceManager;
import org.magic.api.interfaces.extra.MTGSerializable;
import org.magic.services.MTGConstants;
import org.magic.services.PluginRegistry;
import org.magic.services.tools.BeanTools;
import org.magic.services.tools.CryptoUtils;
import org.magic.services.tools.ImageTools;

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
import com.mongodb.client.model.Updates;
import com.mongodb.event.CommandFailedEvent;
import com.mongodb.event.CommandListener;
import com.mongodb.event.CommandStartedEvent;
import com.mongodb.event.CommandSucceededEvent;

public class MongoDbDAO extends AbstractMagicDAO {

	private static final String ID_KEY = "id";
	private static final String CLASSE_KEY = "classe";
	private static final String INIT_DEFAULT_COLS = "INIT_DEFAULT_COLS";
	private static final String EMAIL = "email";
	private static final String PASSWORD = "password";

	private MongoDatabase db;
	private final String colCollects = "collects";
	private final String colStocks = "stocks";
	private final String colAlerts = "alerts";
	private final String colNews = "news";
	private final String colSealed = "sealed";
	private final String colContacts = "contacts";
	private final String colTransactions = "transactions";
	private final String colDecks = "decks";
	private final String colAnnounces = "announces";
	private final String colGed = "ged";
	private final String colTechnical = "technical";
	private final String colCustomsCards = "customsCards";
	private final String colCustomsSets = "customsSets";

	private final String dbIDField = "db_id";
	private final String dbAlertField = "alertItem";
	private final String dbNewsField = "newsItem";
	private final String dbStockField = "stockItem";
	private final String dbTypeNewsField = "typeNews";
	private final String dbStockColField = dbStockField + ".magicCollection.name";
	private final String dbStockSetField = dbStockField + ".edition.id";

	private MongoClient client;
	private JsonWriterSettings setts;
	private final String[] collectionsNames = new String[]{colCollects, colStocks, colAlerts, colNews, colSealed,
			colTransactions, colContacts, colDecks, colAnnounces, colGed};

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {

		final var m = new HashMap<String, MTGProperty>();

		m.put(SERVERNAME, new MTGProperty("localhost", "servername or ip of the server"));
		m.put(SERVERPORT, MTGProperty.newIntegerProperty("27017", "listening port of the database", 1024, 65535));
		m.put(DB_NAME, new MTGProperty("mtgdesktopcompanion", "database name"));
		m.put(LOGIN, new MTGProperty("login", "user allowed to connect to the database"));
		m.put(PASS, new MTGProperty("", "password of the connected user"));
		m.put(PARAMETERS, new MTGProperty("", "MongoDB parameters append to the url"));
		m.put(DRIVER,
				new MTGProperty("mongodb://",
						"Mongo server protocol. Use mongodb:// for standard network. Use mongodb+srv:// for atlas",
						"mongodb://", "mongodb+srv://"));
		m.put(INIT_DEFAULT_COLS, MTGProperty.newBooleanProperty("true",
				"create defaults collection at startup. Will be set to false after startup"));
		return m;
	}

	private <T> T deserialize(Object o, Class<T> classe) {

		if (o == null) {
		    return null;
		}

		return serialiser.fromJson(o.toString(), classe);

	}

	private MTGDeck deserializeDeck(BasicDBObject o) {
		if (o == null) {
		    return null;
		}

		return serialiser.importDeck(o.toJson(setts), o.getString("name"));
	}

	private String serialize(Object o) {
		return serialiser.toJson(o);
	}

	@Override
	public String getVersion() {
		return "3.12.14";

	}

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	@Override
	public void unload() {
		if (client != null) {
		    client.close();
		}
	}

	@Override
	public void init() throws SQLException {

		final var prov = PojoCodecProvider.builder().automatic(true).build();

		final var pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), fromProviders(prov));

		setts = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();

		final var temp = new StringBuilder();
		temp.append(getString(DRIVER)).append(getString(LOGIN)).append(":").append(getString(PASS));
		temp.append("@").append(getString(SERVERNAME));
		if (!getString(SERVERPORT).isEmpty()) {
		    temp.append(":").append(getString(SERVERPORT));
		}

		temp.append("/").append(getString(DB_NAME));

		if (!getString(PARAMETERS).isEmpty()) {
		    temp.append("?").append(getString(PARAMETERS));
		}

		final var settings = MongoClientSettings.builder().applyConnectionString(new ConnectionString(temp.toString()))
				.addCommandListener(new CommandListener() {

					DAOInfo e;

					@Override
					public void commandSucceeded(CommandSucceededEvent event) {
						e.setDuration(event.getElapsedTime(TimeUnit.MILLISECONDS));
						e.setEnd(Instant.now());
						e.setClasseName(event.getClass().getCanonicalName());
						AbstractTechnicalServiceManager.inst().store(e);
					}

					@Override
					public void commandStarted(CommandStartedEvent event) {
						e = new DAOInfo();
						e.setDaoName(getName());
						e.setQuery(event.getCommand().getFirstKey() + " "
								+ event.getCommand().get(event.getCommand().getFirstKey()));
						e.setConnectionName(event.getConnectionDescription().toString());
					}

					@Override
					public void commandFailed(CommandFailedEvent event) {
						e.setMessage(event.getThrowable().getMessage());
						e.setDuration(event.getElapsedTime(TimeUnit.MILLISECONDS));
						e.setEnd(Instant.now());
						AbstractTechnicalServiceManager.inst().store(e);
					}
				}).build();

		client = MongoClients.create(settings);

		db = client.getDatabase(getString(DB_NAME)).withCodecRegistry(pojoCodecRegistry);

		createDB();
		logger.info("init {} done", getName());

	}

	public void createDB() {
		var populateCollections = getBoolean(INIT_DEFAULT_COLS);
		for (final String s : collectionsNames) {
			try {
				db.createCollection(s);
			} catch (final Exception _) {
				logger.trace("{} already exist", s);
				populateCollections = false;
			}
		}

		if (populateCollections) {
		    for (final var s : MTGConstants.defaultCollectionsNames()) {
		        try {
		        	saveCollection(new MTGCollection(s));
		        	setProperty(INIT_DEFAULT_COLS, "false");
		        } catch (final SQLException e) {
		        	logger.error(e);
		        }
		    }
		}
	}

	@Override
	public <T extends MTGSerializable> boolean storeEntry(GedEntry<T> gedItem) {

		final var obj = BasicDBObject.parse(gedItem.toJson().toString());
		obj.put("md5", CryptoUtils.getMD5(gedItem.getContent()));

		db.getCollection(colGed, BasicDBObject.class).insertOne(obj);
		notify(true);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends MTGSerializable> GedEntry<T> readEntry(String classe, String idInstance, String fileName)
			throws SQLException {
		try {
			return (GedEntry<T>) list(classe, idInstance, fileName).get(0);
		} catch (final IndexOutOfBoundsException ioobe) {
			throw new SQLException(ioobe);
		}
	}

	@Override
	public <T extends MTGSerializable> List<GedEntry<T>> listAllEntries() throws SQLException {
		return list(null, null, null);
	}

	@Override
	public <T extends MTGSerializable> List<GedEntry<T>> listEntries(String classename, String id) {
		return list(classename, id, null);
	}

	@SuppressWarnings("unchecked")
	private <T extends MTGSerializable> List<GedEntry<T>> list(String classename, String id, String fileName) {

		final var arr = new ArrayList<GedEntry<T>>();
		FindIterable<BasicDBObject> it = null;

		Bson f = null;

		if (classename != null && id != null && fileName == null) {
		    f = Filters.and(Filters.eq(CLASSE_KEY, classename), Filters.eq(ID_KEY, id));
		}

		if (classename != null && id != null && fileName != null) {
		    f = Filters.and(Filters.eq(CLASSE_KEY, classename), Filters.eq(ID_KEY, id), Filters.eq("name", fileName));
		}

		logger.debug("reading entries with f={}", f);

		if (f == null) {
		    it = db.getCollection(colGed, BasicDBObject.class).find();
		} else {
		    it = db.getCollection(colGed, BasicDBObject.class).find(f);
		}

		it.forEach((Consumer<BasicDBObject>) result -> {
			final var entry = new GedEntry<T>();
			entry.setId(result.get(ID_KEY).toString());
			entry.setName(result.get("name").toString());
			entry.setContent(CryptoUtils.fromBase64(result.getString("data")));
			entry.setIsImage(ImageTools.isImage(entry.getContent()));

			try {
				entry.setClasse(PluginRegistry.inst().loadClass(result.getString(CLASSE_KEY)));
			} catch (final ClassNotFoundException e) {
				logger.debug("can't load {}", classename, e);
			}

			if (CryptoUtils.getMD5(entry.getContent()).equals(result.get("md5"))) {
			    arr.add(entry);
			} else {
			    logger.error("MD5 error for {}", entry.getName());
			}

		});
		return arr;
	}

	@Override
	public <T extends MTGSerializable> boolean deleteEntry(GedEntry<T> gedItem) {
		db.getCollection(colGed).deleteOne(Filters.and(Filters.eq("name", gedItem.getName()),
				Filters.eq(CLASSE_KEY, gedItem.getClasse().getCanonicalName()), Filters.eq(ID_KEY, gedItem.getId())));
		notify(gedItem);
		return true;
	}

	@Override
	public void deleteStock(MTGSealedStock state) throws SQLException {
		db.getCollection(colSealed).deleteOne(Filters.eq(ID_KEY, state.getId()));
		notify(state);

	}

	@Override
	public void deleteContact(Contact contact) throws SQLException {

		if (!listTransactions(contact).isEmpty()) {
		    throw new SQLException(contact + " has transactions and can't be removed ");
		}

		db.getCollection(colContacts).deleteOne(Filters.eq(ID_KEY, contact.getId()));
		notify(contact);

	}

	@Override
	public List<MTGDeck> listDecks() throws SQLException {
		final List<MTGDeck> stocks = new ArrayList<>();
		db.getCollection(colDecks, BasicDBObject.class).find().forEach((Consumer<BasicDBObject>) result -> {
			final var d = deserializeDeck(result);
			stocks.add(d);
			notify(d);
		});
		return stocks;
	}

	@Override
	public MTGDeck getDeckById(Integer id) throws SQLException {
		return deserializeDeck(db.getCollection(colDecks, BasicDBObject.class).find(Filters.eq(ID_KEY, id)).first());

	}

	@Override
	public Integer saveOrUpdateDeck(MTGDeck state) throws SQLException {
		logger.debug("saving deck {}", state);
		if (state.getId() == -1) {
			state.setId(Integer.parseInt(getNextSequence().toString()));
			db.getCollection(colDecks, BasicDBObject.class).insertOne(BasicDBObject.parse(serialiser.toJson(state)));

		} else {
			state.setDateUpdate(new Date());
			final var res = db.getCollection(colDecks, BasicDBObject.class).replaceOne(Filters.eq(ID_KEY, state.getId()),
					BasicDBObject.parse(serialiser.toJson(state)));
			logger.trace(res);
		}

		notify(state);
		return state.getId();
	}

	@Override
	public void deleteDeck(MTGDeck d) throws SQLException {
		logger.debug("Deleting {} : {}", d, d.getId());
		final var dr = db.getCollection(colDecks, BasicDBObject.class).deleteOne(Filters.eq(ID_KEY, d.getId()));
		logger.debug("result={}", dr);
	}

	@Override
	public List<MTGSealedStock> listSealedStocks() throws SQLException {
		final List<MTGSealedStock> stocks = new ArrayList<>();
		db.getCollection(colSealed, BasicDBObject.class).find()
				.forEach((Consumer<BasicDBObject>) result -> stocks.add(deserialize(result, MTGSealedStock.class)));
		return stocks;
	}

	@Override
	public void saveOrUpdateSealedStock(MTGSealedStock state) throws SQLException {
		logger.debug("saving stock {}", state);
		state.setUpdated(false);
		state.setDateUpdate(new Date());

		if (state.getId() == -1) {
			state.setId(Integer.parseInt(getNextSequence().toString()));
			db.getCollection(colSealed, BasicDBObject.class).insertOne(BasicDBObject.parse(serialize(state)));

		} else {
			final var res = db.getCollection(colSealed, BasicDBObject.class).replaceOne(Filters.eq(ID_KEY, state.getId()),
					BasicDBObject.parse(serialize(state)));
			logger.trace(res);
		}

		notify(state);

	}

	@Override
	public void removeCard(MTGCard mc, MTGCollection collection) throws SQLException {
		logger.debug("removeCard {} from {}", mc, collection);

		final var dr = db.getCollection(colStocks, BasicDBObject.class).deleteMany(Filters
				.and(Filters.eq(dbIDField, mc.getScryfallId()), Filters.eq(dbStockColField, collection.getName())));
		logger.debug("result : {}", dr);

	}

	@Override
	public Map<String, Integer> getCardsCountGlobal(MTGCollection c) throws SQLException {
		final Map<String, Integer> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		final List<Bson> aggr = Arrays.asList(Aggregates.match(Filters.eq(dbStockColField, c.getName())),
				Aggregates.group("$stockItem.edition.id", Accumulators.sum("count", 1)));

		db.getCollection(colStocks, BasicDBObject.class).aggregate(aggr).forEach(
				(Consumer<BasicDBObject>) document -> map.put(document.getString("_id"), document.getInt("count")));
		return map;
	}

	@Override
	public List<MTGCardStock> listStocks(MTGCollection collection, MTGEdition me) throws SQLException {
		final var ret = new ArrayList<MTGCardStock>();

		var b = Filters.eq(dbStockColField, collection.getName());

		if (me != null) {
			b = Filters.and(b, Filters.eq(dbStockSetField, me.getId().toUpperCase()));
		}
		db.getCollection(colStocks, BasicDBObject.class).find(b).forEach(
				(Consumer<BasicDBObject>) result -> ret.add(deserialize(result.get("stockItem"), MTGCardStock.class)));
		return ret;
	}

	@Override
	public List<String> listEditionsIDFromCollection(MTGCollection collection) throws SQLException {
		final var ret = new ArrayList<String>();
		db.getCollection(colStocks, BasicDBObject.class)
				.distinct(dbStockSetField, Filters.eq(dbStockColField, collection.getName()), String.class).into(ret);
		return ret;
	}

	@Override
	public MTGCollection getCollection(String name) throws SQLException {
		return db.getCollection(colCollects, MTGCollection.class).find(Filters.eq("name", name)).first();
	}

	@Override
	public void saveCollection(MTGCollection c) throws SQLException {
		if (getCollection(c.getName()) == null) {
		    db.getCollection(colCollects, MTGCollection.class).insertOne(c);
		} else {
		    throw new SQLException(c.getName() + " already exists");
		}
	}

	@Override
	public void removeCollection(MTGCollection c) throws SQLException {
		final var query = new BasicDBObject();
		query.put(dbStockColField, c.getName());
		db.getCollection(colCollects, MTGCollection.class).deleteOne(Filters.eq("name", c.getName()));

		final var dr = db.getCollection(colStocks).deleteMany(Filters.eq(dbStockColField, c.getName()));
		logger.debug(dr);

	}

	@Override
	public List<MTGCollection> listCollections() throws SQLException {
		final var collection = db.getCollection(colCollects, MTGCollection.class);
		return collection.find().into(new ArrayList<MTGCollection>());
	}

	@Override
	public void removeEdition(MTGEdition me, MTGCollection col) throws SQLException {
		final var dr = db.getCollection(colStocks).deleteMany(Filters.and(Filters.eq(dbStockColField, col.getName()),
				Filters.eq(dbStockSetField, me.getId().toUpperCase())));
		logger.debug(dr);
	}

	@Override
	public void moveEdition(MTGEdition ed, MTGCollection from, MTGCollection to) throws SQLException {
		final var filter = Filters.and(Filters.eq(dbStockColField, from.getName()),
				Filters.eq(dbStockSetField, ed.getId().toUpperCase()));
		final var upds = Updates.set(dbStockColField, to.getName());
		final var res = db.getCollection(colStocks).updateMany(filter, upds);
		logger.debug(res);
	}

	@Override
	public String getDBLocation() {
		return getString(SERVERNAME) + ":" + getInt(SERVERPORT);
	}

	@Override
	public Map<String, Long> getDBSize() {

		final var map = new HashMap<String, Long>();

		for (final String col : collectionsNames) {
		    map.put(col, db.runCommand(new BasicDBObject("collStats", col)).getInteger("size").longValue());
		}

		return map;
	}

	@Override
	public String getName() {
		return "MongoDB";
	}

	@Override
	public List<MTGCollection> listCollectionFromCards(MTGCard mc) throws SQLException {

		final var ret = new ArrayList<MTGCollection>();

		db.getCollection(colStocks, BasicDBObject.class)
				.find(Filters.and(Filters.eq(dbStockField + ".product.scryfallId", mc.getScryfallId())))
				.forEach((Consumer<BasicDBObject>) result -> {
					final var mcs = deserialize(result.get(dbStockField).toString(), MTGCardStock.class);
					ret.add(mcs.getMagicCollection());
				});

		return ret;
	}

	@Override
	public MTGCardStock getStockById(Long id) throws SQLException {

		return deserialize(db.getCollection(colStocks, BasicDBObject.class).find(Filters.eq(dbStockField + ".id", id))
				.first().get(dbStockField).toString(), MTGCardStock.class);
	}

	@Override
	public List<MTGCardStock> listStocks(MTGCard mc, MTGCollection col, boolean editionStrict) throws SQLException {
		final var ret = new ArrayList<MTGCardStock>();
		db.getCollection(colStocks, BasicDBObject.class)
				.find(Filters.and(Filters.eq(dbStockField + ".product.scryfallId", mc.getScryfallId()),
						Filters.eq(dbStockField + ".magicCollection.name", col.getName())))
				.forEach((Consumer<BasicDBObject>) result -> ret
						.add(deserialize(result.get(dbStockField).toString(), MTGCardStock.class)));

		return ret;
	}

	@Override
	public List<MTGCardStock> listStocks() throws SQLException {
		final var stocks = new ArrayList<MTGCardStock>();
		db.getCollection(colStocks, BasicDBObject.class).find().forEach((Consumer<BasicDBObject>) result -> stocks
				.add(deserialize(result.get(dbStockField).toString(), MTGCardStock.class)));
		return stocks;
	}

	@Override
	public void saveOrUpdateCardStock(MTGCardStock state) throws SQLException {
		logger.debug("saving stock {}", state);
		state.setUpdated(false);
		if (state.getId() == -1) {
			state.setId(Integer.parseInt(getNextSequence().toString()));
			final var obj = new BasicDBObject();
			obj.put(dbStockField, state);
			obj.put(dbIDField, state.getId());
			db.getCollection(colStocks, BasicDBObject.class).insertOne(BasicDBObject.parse(serialize(obj)));

		} else {

			final var obj = new BasicDBObject();
			obj.put(dbStockField, state);
			obj.put(dbIDField, state.getProduct().getScryfallId());
			final var res = db.getCollection(colStocks, BasicDBObject.class)
					.replaceOne(Filters.eq("stockItem.id", state.getId()), BasicDBObject.parse(serialize(obj)));
			logger.debug(res);
		}
		notify(state);
	}

	@Override
	public void saveAlert(MTGAlert alert) throws SQLException {
		logger.debug("saving alert {}", alert);
		final var obj = new BasicDBObject();
		obj.put(dbAlertField, alert);
		obj.put(dbIDField, alert.getId());
		db.getCollection(colAlerts, BasicDBObject.class).insertOne(BasicDBObject.parse(serialize(obj)));

	}

	@Override
	public void loadAlerts() {
		db.getCollection(colAlerts, BasicDBObject.class).find().forEach((Consumer<BasicDBObject>) result -> {
			final var a = deserialize(result.get(dbAlertField).toString(), MTGAlert.class);
			alerts.put(a.getId(), a);
		});
	}

	@Override
	public void updateAlert(MTGAlert alert) throws SQLException {
		final var obj = new BasicDBObject();
		obj.put(dbAlertField, alert);
		obj.put(dbIDField, alert.getId());
		final var res = db.getCollection(colAlerts, BasicDBObject.class).replaceOne(Filters.eq("alertItem.id", alert.getId()),
				BasicDBObject.parse(serialize(obj)));
		alerts.put(alert.getId(), alert);
		logger.debug(res);

	}

	@Override
	public void deleteAlert(MTGAlert alert) throws SQLException {
		logger.debug("delete alert {}", alert);
		final Bson filter = new Document("alertItem.id", alert.getId());
		final var res = db.getCollection(colAlerts).deleteOne(filter);
		alerts.remove(alert.getId());
		logger.debug(res);
	}

	@Override
	public void deleteStock(List<MTGCardStock> state) throws SQLException {
		logger.debug("remove stocks : {} items ", state.size());
		for (final MTGCardStock s : state) {
			final var filter = new Document("stockItem.id", s.getId());
			final var res = db.getCollection(colStocks).deleteOne(filter);
			logger.debug("result delete : {}", res);

			notify(s);
		}
	}

	private void createCountersCollection(MongoCollection<Document> countersCollection) {
		final var document = new Document();
		document.append("_id", "stock_increment");
		document.append("seq", 1);
		countersCollection.insertOne(document);
	}

	private Object getNextSequence() {
		final MongoCollection<Document> countersCollection = db.getCollection("idSequences");
		if (countersCollection.countDocuments() == 0) {
			createCountersCollection(countersCollection);
		}
		final var searchQuery = new Document("_id", "stock_increment");
		final var increase = new Document("seq", 1);
		final var updateQuery = new Document("$inc", increase);
		final var result = countersCollection.findOneAndUpdate(searchQuery, updateQuery);
		return result.get("seq");
	}

	@Override
	public List<MTGNews> listNews() {
		final List<MTGNews> news = new ArrayList<>();
		db.getCollection(colNews, BasicDBObject.class).find().forEach((Consumer<BasicDBObject>) result -> {
			final var mn = deserialize(result.get(dbNewsField).toString(), MTGNews.class);
			try {
				mn.setProvider(getPlugin(result.get(dbTypeNewsField).toString(), MTGNewsProvider.class));
			} catch (final Exception e) {
				logger.error("error get typeNews provider {} ", result, e);
			}
			news.add(mn);
		});
		return news;
	}

	@Override
	public void deleteNews(MTGNews n) {
		final Bson filter = new Document("newsItem.id", n.getId());
		db.getCollection(colNews).deleteOne(filter);
	}

	@Override
	public void saveOrUpdateNews(MTGNews state) {

		if (state.getId() == -1) {
			logger.debug("saving {}", state);
			state.setId(Integer.parseInt(getNextSequence().toString()));
			final var obj = new BasicDBObject();
			obj.put(dbNewsField, state);
			obj.put(dbTypeNewsField, state.getProvider().getName());
			db.getCollection(colNews, BasicDBObject.class).insertOne(BasicDBObject.parse(serialize(obj)));

		} else {
			logger.debug("update {}", state);

			final Bson filter = new Document("newsItem.id", state.getId());
			final var obj = new BasicDBObject();
			obj.put(dbNewsField, state);
			obj.put(dbTypeNewsField, state.getProvider().getName());
			logger.debug(filter);
			final var res = db.getCollection(colNews, BasicDBObject.class).replaceOne(filter,
					BasicDBObject.parse(serialize(obj)));
			logger.debug(res);
		}

	}

	@Override
	public boolean executeQuery(String query) throws SQLException {
		throw new SQLException("Not implemented");

	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
		    return false;
		}

		return hashCode() == obj.hashCode();
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public List<Transaction> listTransactions(Contact c) throws SQLException {
		final List<Transaction> trans = new ArrayList<>();
		db.getCollection(colTransactions, BasicDBObject.class).find(Filters.eq("contact.id", c.getId()))
				.forEach((Consumer<BasicDBObject>) result -> {
					final var o = deserialize(result.toString(), Transaction.class);
					trans.add(o);
				});

		return trans;
	}

	@Override
	public MTGAnnounce getAnnounceById(int id) throws SQLException {
		return deserialize(db.getCollection(colAnnounces, BasicDBObject.class).find(Filters.eq(ID_KEY, id)).first(),
				MTGAnnounce.class);

	}

	@Override
	public List<MTGAnnounce> listAnnounces(int max, STATUS stat) {
		final List<MTGAnnounce> trans = new ArrayList<>();

		var it = db.getCollection(colAnnounces, BasicDBObject.class).find().sort(Sorts.descending(ID_KEY));

		if (max > 0) {
		    it = it.limit(max);
		}

		// TODO add filter for status

		it.forEach((Consumer<BasicDBObject>) result -> {
			final MTGAnnounce o = deserialize(result.toString(), MTGAnnounce.class);
			trans.add(o);
		});

		return trans;
	}

	@Override
	public int saveOrUpdateAnnounce(MTGAnnounce t) throws SQLException {
		if (t.getId() == -1) {
			t.setId(Integer.parseInt(getNextSequence().toString()));
			db.getCollection(colAnnounces, BasicDBObject.class).insertOne(BasicDBObject.parse(serialize(t)));

		} else {
			final var res = db.getCollection(colAnnounces, BasicDBObject.class).replaceOne(Filters.eq(ID_KEY, t.getId()),
					BasicDBObject.parse(serialize(t)));
			logger.trace(res);
		}
		return t.getId();

	}

	@Override
	public void deleteAnnounceById(int a) throws SQLException {
		final var rs = db.getCollection(colAnnounces).deleteOne(Filters.eq(ID_KEY, a));
		logger.debug(rs);
	}

	@Override
	public List<Transaction> listTransactions() throws SQLException {

		final List<Transaction> trans = new ArrayList<>();
		db.getCollection(colTransactions, BasicDBObject.class).find().forEach((Consumer<BasicDBObject>) result -> {
			final Transaction o = deserialize(result.toString(), Transaction.class);
			trans.add(o);
		});

		return trans;
	}

	@Override
	public List<Contact> listContacts() throws SQLException {
		final List<Contact> trans = new ArrayList<>();
		db.getCollection(colContacts, BasicDBObject.class).find().forEach((Consumer<BasicDBObject>) result -> {
			final Contact o = deserialize(result.toString(), Contact.class);
			trans.add(o);
		});

		return trans;
	}

	@Override
	public Long saveOrUpdateTransaction(Transaction t) throws SQLException {
		if (t.getId() == -1) {
			t.setId(Integer.parseInt(getNextSequence().toString()));
			db.getCollection(colTransactions, BasicDBObject.class).insertOne(BasicDBObject.parse(serialize(t)));

		} else {
			final var res = db.getCollection(colTransactions, BasicDBObject.class).replaceOne(Filters.eq(ID_KEY, t.getId()),
					BasicDBObject.parse(serialize(t)));
			logger.trace(res);
		}
		return t.getId();
	}

	@Override
	public void deleteTransaction(Transaction t) throws SQLException {
		final var rs = db.getCollection(colTransactions).deleteOne(Filters.eq(ID_KEY, t.getId()));
		logger.debug(rs);
	}

	@Override
	public boolean enableContact(String token) throws SQLException {

		final Contact doc = deserialize(
				db.getCollection(colContacts, BasicDBObject.class).find(Filters.eq("temporaryToken", token)).first(),
				Contact.class);

		if (doc != null) {
			doc.setTemporaryToken(null);
			doc.setActive(true);
			saveOrUpdateContact(doc);
			return true;
		}

		return false;

	}

	@Override
	public void changePassword(Contact c, String newPassword) throws SQLException {
		final var passBson = new BasicDBObject();
		passBson.put(PASSWORD, CryptoUtils.generatePasswordHash(newPassword));

		final var updateBson = new BasicDBObject();
		updateBson.put("$set", passBson);

		final var res = db.getCollection(colContacts, BasicDBObject.class).updateOne(Filters.eq(ID_KEY, c.getId()), updateBson);
		logger.debug(res);
	}

	@Override
	public int saveOrUpdateContact(Contact c) throws SQLException {
		if (c.getId() == -1) {
			if (db.getCollection(colContacts, BasicDBObject.class).find(Filters.eq(EMAIL, c.getEmail()))
					.first() != null) {
			    throw new SQLException("Please choose another email");
			}

			c.setId(Integer.parseInt(getNextSequence().toString()));

			final var obj = BasicDBObject.parse(serialize(c));
			obj.put(PASSWORD, CryptoUtils.generatePasswordHash(c.getPassword()));

			db.getCollection(colContacts, BasicDBObject.class).insertOne(obj);

		} else {
			final var contactUpdateData = new BasicDBObject();
			try {
				BeanTools.describe(c).keySet().forEach(k -> {
					try {
						if (!k.equalsIgnoreCase(PASSWORD)) {
						    contactUpdateData.put(k, PropertyUtils.getProperty(c, k));
						}

					} catch (final Exception e) {
						logger.error(e);
					}

				});
			} catch (final Exception e) {
				logger.error(e);
			}

			contactUpdateData.put(PASSWORD, CryptoUtils.generatePasswordHash(c.getPassword()));

			final var update = new BasicDBObject();
			update.put("$set", contactUpdateData);

			final var res = db.getCollection(colContacts, BasicDBObject.class).updateOne(Filters.eq(ID_KEY, c.getId()), update);
			logger.debug(res);
		}
		return c.getId();
	}

	@Override
	public Transaction getTransaction(Long id) throws SQLException {
		return deserialize(db.getCollection(colTransactions, BasicDBObject.class).find(Filters.eq(ID_KEY, id)).first(),
				Transaction.class);
	}

	@Override
	public Contact getContactById(int id) throws SQLException {
		return deserialize(db.getCollection(colContacts).find(Filters.eq(ID_KEY, id), BasicDBObject.class).first(),
				Contact.class);
	}

	@Override
	public MTGSealedStock getSealedStockById(Long id) throws SQLException {
		return deserialize(db.getCollection(colSealed).find(Filters.eq(ID_KEY, id), BasicDBObject.class).first(),
				MTGSealedStock.class);
	}

	@Override
	public Contact getContactByLogin(String email, String password) throws SQLException {
		final var b = db.getCollection(colContacts, BasicDBObject.class)
				.find(Filters.and(Filters.eq(EMAIL, email), Filters.eq("active", true))).first();

		if (!CryptoUtils.verifyPassword(password, b.get(PASSWORD).toString())) {
		    throw new SQLException("Password doesn't match");
		}

		return deserialize(
				db.getCollection(colContacts, BasicDBObject.class)
						.find(Filters.and(Filters.eq(EMAIL, email), Filters.eq("active", true))).first(),
				Contact.class);
	}

	@Override
	public Contact getContactByEmail(String email) throws SQLException {
		return deserialize(
				db.getCollection(colContacts, BasicDBObject.class)
						.find(Filters.and(Filters.eq(EMAIL, email), Filters.eq("active", true))).first(),
				Contact.class);
	}

	@Override
	public <T extends AbstractAuditableItem> void storeTechnicalItem(Class<T> c, List<T> list) throws SQLException {
		db.getCollection(colTechnical, BasicDBObject.class).insertMany(list.stream().map(o -> {
			final var obj = BasicDBObject.parse(serialize(o));
			obj.put("classeName", c.getSimpleName());
			return obj;
		}).toList());

	}

	@Override
	public <T extends AbstractAuditableItem> List<T> restoreTechnicalItem(Class<T> c, Instant start, Instant end)
			throws SQLException {
		final List<T> trans = new ArrayList<>();

		var f = Filters.eq("classeName", c.getSimpleName());

		if (start != null) {
		    f = Filters.and(f, Filters.gte("start", start));
		}

		if (end != null) {
		    f = Filters.and(f, Filters.gte("end", end));
		}

		db.getCollection(colTechnical, BasicDBObject.class).find(f).forEach((Consumer<BasicDBObject>) result -> {
			final var o = deserialize(result.toString(), c);
			trans.add(o);
		});
		return trans;
	}

	@Override
	public List<MTGEdition> listCustomSets() throws SQLException {
		final var trans = new ArrayList<MTGEdition>();
		db.getCollection(colCustomsSets, BasicDBObject.class).find().forEach((Consumer<BasicDBObject>) result -> {
			final var o = deserialize(result.toString(), MTGEdition.class);
			trans.add(o);
		});

		return trans;
	}

	@Override
	public void saveCustomSet(MTGEdition ed) throws SQLException {

		final var obj = db.getCollection(colCustomsSets, BasicDBObject.class).find(Filters.eq(ID_KEY, ed.getId())).first();

		if (obj == null) {
		    db.getCollection(colCustomsSets, BasicDBObject.class).insertOne(BasicDBObject.parse(serialiser.toJson(ed)));
		} else {
		    db.getCollection(colCustomsSets, BasicDBObject.class).replaceOne(Filters.eq(ID_KEY, ed.getId()),
		    		BasicDBObject.parse(serialiser.toJson(ed)));
		}
	}

	@Override
	public void deleteCustomSet(MTGEdition ed) throws SQLException {
		db.getCollection(colCustomsSets).deleteOne(Filters.eq(ID_KEY, ed.getId()));
		db.getCollection(colCustomsCards).deleteMany(Filters.eq("edition.id", ed.getId()));
	}

	@Override
	public void saveCustomCard(MTGCard card) throws SQLException {

		final var obj = db.getCollection(colCustomsCards, BasicDBObject.class).find(Filters.eq(ID_KEY, card.getId())).first();
		if (obj == null) {
		    db.getCollection(colCustomsCards, BasicDBObject.class)
		    		.insertOne(BasicDBObject.parse(serialiser.toJson(card)));
		} else {
		    db.getCollection(colCustomsCards, BasicDBObject.class).replaceOne(Filters.eq(ID_KEY, card.getId()),
		    		BasicDBObject.parse(serialiser.toJson(card)));
		}

	}

	@Override
	public List<MTGCard> listCustomCards(MTGEdition ed) throws SQLException {
		final var trans = new ArrayList<MTGCard>();
		db.getCollection(colCustomsCards, BasicDBObject.class).find(Filters.eq("edition.id", ed.getId()))
				.forEach((Consumer<BasicDBObject>) result -> {
					final var o = deserialize(result.toString(), MTGCard.class);
					trans.add(o);
				});

		return trans;
	}

	@Override
	public void deleteCustomCard(MTGCard card) throws SQLException {
		db.getCollection(colCustomsCards).deleteOne(Filters.eq(ID_KEY, card.getId()));
	}

	@Override
	public MTGEdition getCustomSetById(String id) throws SQLException {
		return deserialize(db.getCollection(colCustomsSets, BasicDBObject.class).find(Filters.eq(ID_KEY, id)).first(),
				MTGEdition.class);
	}

}
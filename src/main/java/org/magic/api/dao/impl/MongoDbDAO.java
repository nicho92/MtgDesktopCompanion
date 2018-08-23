package org.magic.api.dao.impl;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import org.apache.commons.lang3.NotImplementedException;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicNews;
import org.magic.api.interfaces.abstracts.AbstractMagicDAO;
import org.magic.services.MTGControler;
import org.magic.tools.IDGenerator;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class MongoDbDAO extends AbstractMagicDAO {

	private MongoClient client;
	private MongoDatabase db;
	private String colCards = "cards";
	private String colShops = "shops";
	private String colCollects = "collects";
	private String colStocks = "stocks";
	private String colAlerts = "alerts";
	private String colDecks = "decks";
	private String colNews = "news";
	private String dbIDField = "db_id";
	private String dbCardIDField = "card_id";
	private String dbEditionField = "edition";
	private String dbAlertField = "alertItem";
	private String dbNewsField = "newsItem";
	private String dbStockField = "stockItem";
	private String dbColIDField = "collection.name";
	private String dbTypeNewsField = "typeNews";


	private <T> T deserialize(Object o, Class<T> classe) {
		return serialiser.fromJson(o.toString(), classe);

	}

	private String serialize(Object o) {
		return serialiser.toJson(o);
	}

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	
	public void init() throws SQLException, ClassNotFoundException {

		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		client = new MongoClient(new ServerAddress(getString("SERVERNAME"), getInt("SERVERPORT")),
				MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build());
		db = client.getDatabase(getString("DB_NAME")).withCodecRegistry(pojoCodecRegistry);

		createDB();

		logger.info("init " + getName() + " done");

	}

	public boolean createDB() {
		try {
			db.createCollection(colCards);
			db.createCollection(colShops);
			db.createCollection(colCollects);
			db.createCollection(colStocks);
			db.createCollection(colAlerts);
			db.createCollection(colDecks);
			db.createCollection(colNews);

			for (String s : new String[] { "Library", "Needed", "For Sell", "Favorites" })
				db.getCollection(colCollects, MagicCollection.class).insertOne(new MagicCollection(s));

			return true;
		} catch (Exception e) {
			logger.debug(e);
			return false;
		}

	}

	@Override
	public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException {
		logger.debug("saveCard " + mc + " in " + collection);

		BasicDBObject obj = new BasicDBObject();
		obj.put(dbIDField, IDGenerator.generate(mc));
		obj.put("card", mc);
		obj.put(dbEditionField, mc.getCurrentSet().getId().toUpperCase());
		obj.put("collection", collection);
		String json = serialize(obj);

		db.getCollection(colCards, BasicDBObject.class).insertOne(BasicDBObject.parse(json));
	}

	@Override
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
		logger.debug("removeCard " + mc + " from " + collection);

		BasicDBObject andQuery = new BasicDBObject();
		List<BasicDBObject> obj = new ArrayList<>();
		obj.add(new BasicDBObject(dbIDField, IDGenerator.generate(mc)));
		obj.add(new BasicDBObject(dbColIDField, collection.getName()));
		andQuery.put("$and", obj);
		DeleteResult dr = db.getCollection(colCards, BasicDBObject.class).deleteMany(andQuery);
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
		List<Bson> aggr = Arrays.asList(Aggregates.match(Filters.eq(dbColIDField, c.getName())),
				Aggregates.group("$edition", Accumulators.sum("count", 1)));

		logger.trace(aggr.toString());

		db.getCollection(colCards, BasicDBObject.class).aggregate(aggr).forEach(
				(Consumer<BasicDBObject>) document -> map.put(document.getString("_id"), document.getInt("count")));
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
			BasicDBObject andQuery = new BasicDBObject();
			List<BasicDBObject> obj = new ArrayList<>();
			obj.add(new BasicDBObject(dbColIDField, cols.getName()));
			obj.add(new BasicDBObject(dbEditionField, me.getId().toUpperCase()));
			andQuery.put("$and", obj);
			return (int) db.getCollection(colCards, BasicDBObject.class).countDocuments(andQuery);
		} else {
			return (int) db.getCollection(colCards, BasicDBObject.class)
					.countDocuments(new BasicDBObject(dbColIDField, cols.getName()));
		}

	}

	@Override
	public List<MagicCard> listCardsFromCollection(MagicCollection collection, MagicEdition me) throws SQLException {
		logger.debug("getCardsFromCollection " + collection + " " + me);

		BasicDBObject query = new BasicDBObject();
		List<MagicCard> ret = new ArrayList<>();

		List<BasicDBObject> obj = new ArrayList<>();
		obj.add(new BasicDBObject(dbColIDField, collection.getName()));

		if (me != null) {
			obj.add(new BasicDBObject(dbEditionField, me.getId().toUpperCase()));
			query.put("$and", obj);
		}

		db.getCollection(colCards, BasicDBObject.class).find(query).forEach((Consumer<BasicDBObject>) result -> ret
				.add(deserialize(result.get("card").toString(), MagicCard.class)));
		return ret;
	}

	@Override
	public List<String> getEditionsIDFromCollection(MagicCollection collection) throws SQLException {
		List<String> ret = new ArrayList<>();
		BasicDBObject query = new BasicDBObject();
		query.put(dbColIDField, collection.getName());
		db.getCollection(colCards, BasicDBObject.class).distinct(dbEditionField, query, String.class).into(ret);
		return ret;
	}

	@Override
	public MagicCollection getCollection(String name) throws SQLException {
		MongoCollection<MagicCollection> collection = db.getCollection(colCollects, MagicCollection.class);
		return collection.find(Filters.eq("name", name)).first();
	}

	@Override
	public void saveCollection(MagicCollection c) throws SQLException {
		db.getCollection(colCollects, MagicCollection.class).insertOne(c);
	}

	@Override
	public void removeCollection(MagicCollection c) throws SQLException {
		logger.debug("remove collection " + c);
		BasicDBObject query = new BasicDBObject();
		query.put(dbColIDField, c.getName());
		DeleteResult dr = db.getCollection(colCards).deleteMany(query);
		logger.trace(dr);
		MongoCollection<MagicCollection> collection = db.getCollection(colCollects, MagicCollection.class);
		collection.deleteOne(Filters.eq("name", c.getName()));

	}

	@Override
	public List<MagicCollection> getCollections() throws SQLException {
		MongoCollection<MagicCollection> collection = db.getCollection(colCollects, MagicCollection.class);
		List<MagicCollection> cols = new ArrayList<>();
		collection.find().into(cols);
		return cols;
	}

	@Override
	public void removeEdition(MagicEdition me, MagicCollection col) throws SQLException {
		logger.debug("delete " + me + " from " + col);

		BasicDBObject query = new BasicDBObject();
		query.put(dbColIDField, col.getName());
		query.put(dbEditionField, me.getId().toUpperCase());

		DeleteResult dr = db.getCollection(colCards).deleteMany(query);
		logger.debug(dr);
	}

	@Override
	public String getDBLocation() {
		return client.getConnectPoint();
	}

	@Override
	public long getDBSize() {
		return db.runCommand(new BasicDBObject("dbstats", 1)).getDouble("dataSize").longValue();
	}

	@Override
	public String getName() {
		return "MongoDB";
	}

	@Override
	public List<MagicCollection> listCollectionFromCards(MagicCard mc) throws SQLException {

		List<MagicCollection> ret = new ArrayList<>();
		BasicDBObject query = new BasicDBObject();
		query.put(dbIDField, IDGenerator.generate(mc));
		db.getCollection(colCards, BasicDBObject.class).distinct(dbColIDField, query, String.class)
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
	public List<MagicCardStock> listStocks(MagicCard mc, MagicCollection col) throws SQLException {
		ArrayList<MagicCardStock> ret = new ArrayList<>();

		BasicDBObject filter = new BasicDBObject(dbCardIDField, IDGenerator.generate(mc));
		filter.put("stockItem.magicCollection.name", col.getName());
		logger.debug(filter);
		db.getCollection(colStocks, BasicDBObject.class).find(filter).forEach((Consumer<BasicDBObject>) result -> ret
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
	public void saveOrUpdateStock(MagicCardStock state) throws SQLException {
		logger.debug("saving " + state);

		if (state.getIdstock() == -1) {
			state.setIdstock(Integer.parseInt(getNextSequence().toString()));
			BasicDBObject obj = new BasicDBObject();
			obj.put(dbStockField, state);
			obj.put(dbCardIDField, IDGenerator.generate(state.getMagicCard()));
			db.getCollection(colStocks, BasicDBObject.class).insertOne(BasicDBObject.parse(serialize(obj)));

		} else {
			Bson filter = new Document("stockItem.idstock", state.getIdstock());
			BasicDBObject obj = new BasicDBObject();
			obj.put(dbStockField, state);
			obj.put(dbCardIDField, IDGenerator.generate(state.getMagicCard()));
			logger.debug(filter);
			UpdateResult res = db.getCollection(colStocks, BasicDBObject.class).replaceOne(filter,
					BasicDBObject.parse(serialize(obj)));
			logger.debug(res);
		}
	}

	@Override
	public void saveAlert(MagicCardAlert alert) throws SQLException {
		logger.debug("saving alert " + alert);

		alert.setId(getNextSequence().toString());
		BasicDBObject obj = new BasicDBObject();
		obj.put(dbAlertField, alert);
		obj.put(dbIDField, IDGenerator.generate(alert.getCard()));
		db.getCollection(colAlerts, BasicDBObject.class).insertOne(BasicDBObject.parse(serialize(obj)));
	}

	@Override
	public List<MagicCardAlert> listAlerts() {
		ArrayList<MagicCardAlert> ret = new ArrayList<>();
		db.getCollection(colAlerts, BasicDBObject.class).find().forEach((Consumer<BasicDBObject>) result -> ret
				.add(deserialize(result.get(dbAlertField).toString(), MagicCardAlert.class)));
		return ret;
	}

	@Override
	public boolean hasAlert(MagicCard mc) {
		return db.getCollection(colAlerts, BasicDBObject.class)
				.find(new BasicDBObject(dbIDField, IDGenerator.generate(mc))).limit(1).iterator().hasNext();
	}

	@Override
	public void updateAlert(MagicCardAlert alert) throws SQLException {
		Bson filter = new Document("alertItem.id", alert.getId());
		BasicDBObject obj = new BasicDBObject();
		obj.put(dbAlertField, alert);
		obj.put(dbIDField, IDGenerator.generate(alert.getCard()));

		UpdateResult res = db.getCollection(colAlerts, BasicDBObject.class).replaceOne(filter,
				BasicDBObject.parse(serialize(obj)));
		logger.debug(res);

	}

	@Override
	public void deleteAlert(MagicCardAlert alert) throws SQLException {
		logger.debug("delete alert " + alert);
		Bson filter = new Document("alertItem.id", alert.getId());
		DeleteResult res = db.getCollection(colAlerts).deleteOne(filter);
		logger.debug(res);
	}

	@Override
	public void deleteStock(List<MagicCardStock> state) throws SQLException {
		logger.debug("remove " + state.size() + " items in stock");
		for (MagicCardStock s : state) {
			Bson filter = new Document("stockItem.idstock", s.getIdstock());
			db.getCollection(colStocks).deleteOne(filter);
		}
	}

	@Override
	public void backup(File f) throws IOException {
		throw new NotImplementedException("Not yet implemented");
	}

	private void createCountersCollection(MongoCollection<Document> countersCollection) {
		Document document = new Document();
		document.append("_id", "stock_increment");
		document.append("seq", 1);
		countersCollection.insertOne(document);
	}

	private Object getNextSequence() {
		MongoCollection<Document> countersCollection = db.getCollection("idSequences");
		if (countersCollection.countDocuments() == 0) {
			createCountersCollection(countersCollection);
		}
		Document searchQuery = new Document("_id", "stock_increment");
		Document increase = new Document("seq", 1);
		Document updateQuery = new Document("$inc", increase);
		Document result = countersCollection.findOneAndUpdate(searchQuery, updateQuery);
		return result.get("seq");
	}

	@Override
	public List<MagicNews> listNews() {
		List<MagicNews> news = new ArrayList<>();
		db.getCollection(colNews, BasicDBObject.class).find().forEach((Consumer<BasicDBObject>) result ->{ 
			MagicNews mn = deserialize(result.get(dbNewsField).toString(), MagicNews.class);
			try{
				mn.setProvider(MTGControler.getInstance().getNewsProvider(result.get(dbTypeNewsField).toString()));
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
		logger.debug("remove " + n);
		Bson filter = new Document("newsItem.id", n.getId());
		db.getCollection(colNews).deleteOne(filter);
	}

	@Override
	public void saveOrUpdateNews(MagicNews state) {
	
		if (state.getId() == -1) {
			logger.debug("saving " + state);
			state.setId(Integer.parseInt(getNextSequence().toString()));
			BasicDBObject obj = new BasicDBObject();
			obj.put(dbNewsField, state);
			obj.put(dbTypeNewsField, state.getProvider().getName());
			db.getCollection(colNews, BasicDBObject.class).insertOne(BasicDBObject.parse(serialize(obj)));

		} else {
			logger.debug("update " + state);

			Bson filter = new Document("newsItem.id", state.getId());
			BasicDBObject obj = new BasicDBObject();
			obj.put(dbNewsField, state);
			obj.put(dbTypeNewsField, state.getProvider().getName());
			logger.debug(filter);
			UpdateResult res = db.getCollection(colNews, BasicDBObject.class).replaceOne(filter,BasicDBObject.parse(serialize(obj)));
			logger.debug(res);
		}

	}

	@Override
	public void initDefault() {
		setProperty("SERVERNAME", "localhost");
		setProperty("SERVERPORT", "27017");
		setProperty("DB_NAME", "mtgdesktopcompanion");
		setProperty("LOGIN", "login");
		setProperty("PASS", "");

	}

	@Override
	public String getVersion() {
		return "1";
	}

}

package org.magic.api.dao.impl;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicDAO;
import org.magic.sorters.MagicCardComparator;
import org.magic.tools.IDGenerator;

import com.google.gson.Gson;
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

public class MongoDbDAO extends AbstractMagicDAO{

    
	MongoClient client;
	MongoDatabase db;
	CodecRegistry pojoCodecRegistry;
	Gson gson;
	
	
	private <T> T deserialize(Object o ,Class<T> classe)
	{
		return gson.fromJson(o.toString(), classe);
		
	}
	
	private String serialize(Object o)
	{
		return gson.toJson(o);
	}
	

	
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}
	
   public MongoDbDAO()  {
	    super();	
	}
	
	public void init() throws SQLException, ClassNotFoundException {
		
		gson=new Gson();
		
		pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		
		client = new MongoClient(
								new ServerAddress(getString("SERVERNAME"), Integer.parseInt(getString("SERVERPORT"))),
								MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build()	
								);
		db = client.getDatabase(getString("DB_NAME")).withCodecRegistry(pojoCodecRegistry);
		
		createDB();
	
		logger.info("init " + getName() +" done");
		 
	}
	
	 public boolean createDB()
	 {
		 try{
		 	db.createCollection("cards");
		 	db.createCollection("shops");
		 	db.createCollection("collects");
		 	db.createCollection("stocks");
		 	db.createCollection("alerts");
		 	db.createCollection("decks");
		 	db.createCollection("news");
		 	
		 	String[] cols = {"Library","Needed","For Sell","Favorites"};
		 	
		 	for(String s : cols)
		 		db.getCollection("collects",MagicCollection.class).insertOne(new MagicCollection(s));
		 	
		 	return true;
		 }
		 catch(Exception e)
		 {
			 logger.debug(e);
			 return false;
		 }
		 
	 }
	
	
	@Override
	public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException {
		logger.debug("saveCard " + mc +" in " + collection);
		
		BasicDBObject obj = new BasicDBObject();
		obj.put("db_id", IDGenerator.generate(mc));
		obj.put("card", mc);
		obj.put("edition",mc.getEditions().get(0).getId().toUpperCase());
		obj.put("collection", collection);
		String json = serialize(obj);
		db.getCollection("cards",BasicDBObject.class).insertOne(BasicDBObject.parse(json));
	}

	@Override
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
		logger.debug("removeCard " + mc + " from " + collection);
		
		BasicDBObject andQuery = new BasicDBObject();
		List<BasicDBObject> obj = new ArrayList<>();
							obj.add(new BasicDBObject("db_id",IDGenerator.generate(mc)));
							obj.add(new BasicDBObject("collection.name", collection.getName()));
		andQuery.put("$and", obj);
		DeleteResult dr = db.getCollection("cards",BasicDBObject.class).deleteMany(andQuery);
		logger.debug(dr.toString());
		
	}

	@Override
	public List<MagicCard> listCards() throws SQLException {
		logger.debug("list all cards");
		List<MagicCard> list = new ArrayList<>();
		db.getCollection("cards",BasicDBObject.class).find().forEach((Consumer<BasicDBObject>)result->list.add(deserialize(result.get("card").toString(),MagicCard.class)));
		return list;
	}

	@Override
	public Map<String, Integer> getCardsCountGlobal(MagicCollection c) throws SQLException {
		Map<String, Integer> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		 List<Bson> aggr = Arrays.asList(
	              Aggregates.match(Filters.eq("collection.name", c.getName())),
	              Aggregates.group("$edition", Accumulators.sum("count", 1))
	      );
		
		 
		 logger.trace(aggr.toString());
			
		    
		db.getCollection("cards",BasicDBObject.class)
		  .aggregate(aggr)
		  .forEach( (Consumer<BasicDBObject>)document->
				            map.put(document.getString("_id"), document.getInt("count"))
				        	);
		return map;
	}
	
	@Override
	public List<MagicCard> listCardsFromCollection(MagicCollection collection) throws SQLException {
		return listCardsFromCollection(collection,null);
	}

	@Override
	public int getCardsCount(MagicCollection cols,MagicEdition me) throws SQLException {
		
		logger.debug("getCardsCount " + cols + " me:"+me);
		if(me!=null)
		{
			BasicDBObject andQuery = new BasicDBObject();
			List<BasicDBObject> obj = new ArrayList<>();
								obj.add(new BasicDBObject("collection.name", cols.getName()));
								obj.add(new BasicDBObject("edition",me.getId().toUpperCase()));
			andQuery.put("$and", obj);
			return (int) db.getCollection("cards",BasicDBObject.class).count(andQuery);
		}
		else
		{
			return (int) db.getCollection("cards",BasicDBObject.class).count(new BasicDBObject("collection.name", cols.getName()));
		}
							
		
	}

	
	@Override
	public List<MagicCard> listCardsFromCollection(MagicCollection collection, MagicEdition me) throws SQLException {
		logger.debug("getCardsFromCollection " + collection + " " + me);
		
		BasicDBObject query = new BasicDBObject();
		List<MagicCard> ret = new ArrayList<>();

		List<BasicDBObject> obj = new ArrayList<>();
							obj.add(new BasicDBObject("collection.name", collection.getName()));
		
		if(me!=null)
		{
			obj.add(new BasicDBObject("edition", me.getId().toUpperCase()));
			query.put("$and", obj);
		}
							
		db.getCollection("cards", BasicDBObject.class).find(query).forEach((Consumer<BasicDBObject>)result->ret.add(deserialize(result.get("card").toString(),MagicCard.class)));
		
		Collections.sort(ret,new MagicCardComparator());
		return ret;
	}

	
	@Override
	public List<String> getEditionsIDFromCollection(MagicCollection collection) throws SQLException {
		List<String> ret = new ArrayList<>();
		BasicDBObject query = new BasicDBObject();
		query.put("collection.name", collection.getName());
		db.getCollection("cards", BasicDBObject.class).distinct("edition",query,String.class).into(ret);
		return ret;
	}

	
	@Override
	public MagicCollection getCollection(String name) throws SQLException {
		MongoCollection<MagicCollection> collection = db.getCollection("collects", MagicCollection.class);
		return collection.find(Filters.eq("name",name)).first();
	}

	@Override
	public void saveCollection(MagicCollection c) throws SQLException {
		db.getCollection("collects",MagicCollection.class).insertOne(c);
	}

	@Override
	public void removeCollection(MagicCollection c) throws SQLException {
		logger.debug("remove collection " + c);
		BasicDBObject query = new BasicDBObject();
				query.put("collection.name", c.getName());
		DeleteResult dr = db.getCollection("cards").deleteMany(query);
		logger.trace(dr);
		MongoCollection<MagicCollection> collection = db.getCollection("collects", MagicCollection.class);
		collection.deleteOne(Filters.eq("name",c.getName()));
		
		
	}

	@Override
	public List<MagicCollection> getCollections() throws SQLException {
		MongoCollection<MagicCollection> collection = db.getCollection("collects", MagicCollection.class);
		List<MagicCollection> cols = new ArrayList<>();
		collection.find().into(cols);
		return cols;
	}

	@Override
	public void removeEdition(MagicEdition me, MagicCollection col) throws SQLException {
		logger.debug("remove " + me + " from " + col);
		
		BasicDBObject query = new BasicDBObject();
					query.put("collection.name", col.getName());
					query.put("edition",me.getId().toUpperCase());
		
		DeleteResult dr = db.getCollection("cards").deleteMany(query);
		logger.debug(dr);
	}

	@Override
	public String getDBLocation() {
		return client.getConnectPoint();
	}

	@Override
	public long getDBSize() {
		return db.runCommand(new BasicDBObject("dbstats",1)).getDouble("dataSize").longValue();
	}

	@Override
	public String getName() {
		return "MongoDB";
	}

	@Override
	public List<MagicCollection> listCollectionFromCards(MagicCard mc) throws SQLException{
		
		List<MagicCollection> ret = new ArrayList<>();
		BasicDBObject query = new BasicDBObject();
		query.put("db_id",IDGenerator.generate(mc));
		db.getCollection("cards", BasicDBObject.class).distinct("collection.name",query,String.class).forEach((Consumer<String>)result ->{
				try {
					logger.trace("found " + mc + " in " + result);
					ret.add(getCollection(result));
				} catch (SQLException e) {
					logger.error("Error",e);
				}
			
		});
		
		
		return ret;
	}
	

	
	
	@Override
	public List<MagicCardStock> listStocks(MagicCard mc, MagicCollection col) throws SQLException {
		ArrayList<MagicCardStock> ret = new ArrayList<>();
		
		BasicDBObject filter = new BasicDBObject("card_id", IDGenerator.generate(mc));
					  filter.put("stockItem.magicCollection.name", col.getName());
		logger.debug(filter);
		db.getCollection("stocks",BasicDBObject.class).find(filter).forEach((Consumer<BasicDBObject>)result->ret.add(deserialize(result.get("stockItem").toString(), MagicCardStock.class)));
		
		return ret;
	}
	
	public List<MagicCardStock> listStocks() throws SQLException {
		List<MagicCardStock> stocks= new ArrayList<>();
		db.getCollection("stocks",BasicDBObject.class).find().forEach((Consumer<BasicDBObject>)result->stocks.add(deserialize(result.get("stockItem").toString(),MagicCardStock.class)));
		return stocks;
	}
    
	
	@Override
	public void saveOrUpdateStock(MagicCardStock state) throws SQLException {
		logger.debug("saving " + state);
		
		if(state.getIdstock()==-1)
		{
			state.setIdstock(Integer.parseInt(getNextSequence().toString()));
			BasicDBObject obj = new BasicDBObject();
						  obj.put("stockItem", state);
						  obj.put("card_id", IDGenerator.generate(state.getMagicCard()));
			db.getCollection("stocks",BasicDBObject.class).insertOne(BasicDBObject.parse(serialize(obj)));
			
		}
		else 
		{ 
			Bson filter = new Document("stockItem.idstock", state.getIdstock());
			BasicDBObject obj = new BasicDBObject();
						  obj.put("stockItem",  state);
						  obj.put("card_id", IDGenerator.generate(state.getMagicCard()));
			logger.debug(filter);
			UpdateResult res = db.getCollection("stocks",BasicDBObject.class).replaceOne(filter,BasicDBObject.parse(serialize(obj)));
			logger.debug(res);
		}
	}

	@Override
	public void saveAlert(MagicCardAlert alert) throws SQLException {
		logger.debug("saving alert " + alert);
		
		alert.setId(getNextSequence().toString());
		BasicDBObject obj = new BasicDBObject();
					  obj.put("alertItem",  alert);
					  obj.put("db_id",IDGenerator.generate(alert.getCard()));
		db.getCollection("alerts",BasicDBObject.class).insertOne(BasicDBObject.parse(serialize(obj)));
	}
	
	@Override
	public List<MagicCardAlert> listAlerts() {
		ArrayList<MagicCardAlert> ret= new ArrayList<>();
		db.getCollection("alerts",BasicDBObject.class).find().forEach((Consumer<BasicDBObject>)result->
					ret.add(deserialize(result.get("alertItem").toString(),MagicCardAlert.class))
					);
      return ret;
	}

	@Override
	public boolean hasAlert(MagicCard mc) {
		return db.getCollection("alerts",BasicDBObject.class).find(new BasicDBObject("db_id",IDGenerator.generate(mc))).limit(1).iterator().hasNext();
	}
	

	@Override 
	public void updateAlert(MagicCardAlert alert) throws SQLException {
		Bson filter = new Document("alertItem.id", alert.getId());
		BasicDBObject obj = new BasicDBObject();
					  obj.put("alertItem",  alert);
					  obj.put("db_id",IDGenerator.generate(alert.getCard()));
					
		UpdateResult res = db.getCollection("alerts",BasicDBObject.class).replaceOne(filter,BasicDBObject.parse(serialize(obj)));
		logger.debug(res);
		
	}

	
	
	@Override
	public void deleteAlert(MagicCardAlert alert) throws SQLException {
		logger.debug("delete alert "  + alert);
		Bson filter = new Document("alertItem.id", alert.getId());
		DeleteResult res = db.getCollection("alerts").deleteOne(filter);
		logger.debug(res);
	}
	
	@Override
	public void deleteStock(List<MagicCardStock> state) throws SQLException {
		logger.debug("remove " + state.size()  + " items in stock");
		for(MagicCardStock s : state)
		{
			Bson filter = new Document("stockItem.idstock", s.getIdstock());
			db.getCollection("stocks").deleteOne(filter);
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
        if (countersCollection.count() == 0) {
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
		List<MagicNews> stocks= new ArrayList<>();
		db.getCollection("news",BasicDBObject.class).find().forEach((Consumer<BasicDBObject>)result->stocks.add(deserialize(result.get("newsItem").toString(),MagicNews.class)));
		return stocks;
	}

	@Override
	public void deleteNews(MagicNews n) {
		logger.debug("remove " + n);
		Bson filter = new Document("newsItem.id", n.getId());
		db.getCollection("news").deleteOne(filter);
	}

	@Override
	public void saveOrUpdateNews(MagicNews state) {
		logger.debug("saving " + state);
		
		if(state.getId()==-1)
		{
			state.setId(Integer.parseInt(getNextSequence().toString()));
			BasicDBObject obj = new BasicDBObject();
						  obj.put("newsItem", state);
			db.getCollection("news",BasicDBObject.class).insertOne(BasicDBObject.parse(serialize(obj)));
			
		}
		else 
		{ 
			Bson filter = new Document("newsItem.id", state.getId());
			BasicDBObject obj = new BasicDBObject();
						  obj.put("newsItem",  state);
			logger.debug(filter);
			UpdateResult res = db.getCollection("news",BasicDBObject.class).replaceOne(filter,BasicDBObject.parse(serialize(obj)));
			logger.debug(res);
		}
		
	}

	@Override
	public void initDefault() {
		 setProperty("SERVERNAME","localhost");
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

package org.magic.api.dao.impl;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicDAO;
import org.magic.tools.IDGenerator;
import org.magic.tools.MagicCardComparator;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
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
		return STATUT.DEV;
	}
	
    @Override
    public String toString() {
    	return getName();
    }
    
	public MongoDbDAO() throws ClassNotFoundException, SQLException {
	    super();	
		if(!new File(confdir, getName()+".conf").exists()){
			 props.put("SERVERNAME","localhost");
			 props.put("SERVERPORT", "27017");
			 props.put("DB_NAME", "mtgdesktopcompanion");
			 props.put("LOGIN", "login");
			 props.put("PASSWORD", "password");
		save();
		}
		
		
	}
	
	public void init() throws SQLException, ClassNotFoundException {
		
		gson=new Gson();
		
		//pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),fromProviders(PojoCodecProvider.builder().register("org.magic.beans").automatic(true).build()));
		pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		
		client = new MongoClient(
								new ServerAddress(getProperty("SERVERNAME").toString(), Integer.parseInt(getProperty("SERVERPORT").toString())),
								MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build()	
								);
		db = client.getDatabase(getProperty("DB_NAME").toString()).withCodecRegistry(pojoCodecRegistry);
		
		createDB();
	
		logger.info("init " + getName() +" done");
		 
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		MongoDbDAO dao = new MongoDbDAO();
		dao.init();
		
		MagicCardAlert a =  dao.getAlerts().get(0);
		System.out.println("modification " + a + " " + a.getId() + " " + a.getPrice());
		a.setPrice(100.0);
		dao.updateAlert(a);
		a=dao.getAlerts().get(0);
		System.out.println("modification " + a + " " + a.getId() + " " + a.getPrice());
			
		
		
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
		logger.debug("saving " + mc +" in " + collection);
		
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
		logger.debug("remove " + mc + " from " + collection);
		
		BasicDBObject andQuery = new BasicDBObject();
		List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
							obj.add(new BasicDBObject("db_id",IDGenerator.generate(mc)));
							obj.add(new BasicDBObject("collection.name", collection.getName()));
		andQuery.put("$and", obj);
		DeleteResult dr = db.getCollection("cards",BasicDBObject.class).deleteMany(andQuery);
		logger.debug(dr.toString());
		
	}

	@Override
	public List<MagicCard> listCards() throws SQLException {
		logger.debug("list all cards");
		List<MagicCard> list = new ArrayList<MagicCard>();
		
		db.getCollection("cards",BasicDBObject.class).find().forEach(new Block<BasicDBObject>() {
				        @Override
				        public void apply(final BasicDBObject result) {
				        	list.add(deserialize(result.get("card").toString(),MagicCard.class));
				        	}
				    	}
				  );
		return list;
	}

	@Override
	public Map<String, Integer> getCardsCountGlobal(MagicCollection c) throws SQLException {
		Map<String, Integer> map = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
		 List<Bson> aggr = Arrays.asList(
	              Aggregates.match(Filters.eq("collection.name", c.getName())),
	              Aggregates.group("$edition", Accumulators.sum("count", 1))
	      );
		
		 
		 logger.trace(aggr.toString());
			
		    
		db.getCollection("cards",BasicDBObject.class)
		  .aggregate(aggr)
		  .forEach( new Block<BasicDBObject>() {
				        @Override
				        public void apply(final BasicDBObject document) {
				            map.put(document.getString("_id"), document.getInt("count"));
				        	}
				    	}
				  );
		return map;
	}
	
	@Override
	public List<MagicCard> getCardsFromCollection(MagicCollection collection) throws SQLException {
		return getCardsFromCollection(collection,null);
	}

	@Override
	public int getCardsCount(MagicCollection cols,MagicEdition me) throws SQLException {
		
		logger.debug("getCardsCount " + cols + " me:"+me);
		if(me!=null)
		{
			BasicDBObject andQuery = new BasicDBObject();
			List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
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
	public List<MagicCard> getCardsFromCollection(MagicCollection collection, MagicEdition me) throws SQLException {
		logger.debug("getCardsFromCollection " + collection + " " + me);
		
		BasicDBObject query = new BasicDBObject();
		List<MagicCard> ret = new ArrayList<MagicCard>();

		List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
							obj.add(new BasicDBObject("collection.name", collection.getName()));
		
		if(me!=null)
		{
			obj.add(new BasicDBObject("edition", me.getId().toUpperCase()));
			query.put("$and", obj);
		}
							
		db.getCollection("cards", BasicDBObject.class).find(query).forEach(new Block<BasicDBObject>() {
				        @Override
				        public void apply(final BasicDBObject result) {
				        	ret.add(deserialize(result.get("card").toString(),MagicCard.class));
				        	}
				    	}
				  );
		
		Collections.sort(ret,new MagicCardComparator());
		return ret;
	}

	
	@Override
	public List<String> getEditionsIDFromCollection(MagicCollection collection) throws SQLException {
		List<String> ret = new ArrayList<String>();
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
		List<MagicCollection> cols = new ArrayList<MagicCollection>();
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
		return -1;
	}

	@Override
	public String getName() {
		return "MongoDB";
	}

	@Override
	public List<MagicCollection> getCollectionFromCards(MagicCard mc) throws SQLException{
		
		List<MagicCollection> ret = new ArrayList<MagicCollection>();
		BasicDBObject query = new BasicDBObject();
		query.put("db_id",IDGenerator.generate(mc));
		db.getCollection("cards", BasicDBObject.class).distinct("collection.name",query,String.class).forEach(new Block<String>() {
			public void apply(String arg) {
				try {
					logger.trace("found " + mc + " in " + arg);
					ret.add(getCollection(arg));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			};
		});
		
		
		return ret;
	}
	

	
	
	@Override
	public List<MagicCardStock> getStocks(MagicCard mc, MagicCollection col) throws SQLException {
		return new ArrayList<MagicCardStock>();
	}
	
	public List<MagicCardStock> getStocks() throws SQLException {
		List<MagicCardStock> stocks= new ArrayList<MagicCardStock>();
		db.getCollection("stocks",BasicDBObject.class).find().forEach(new Block<BasicDBObject>() {
	        @Override
	        public void apply(final BasicDBObject result) {
	        	stocks.add(deserialize(result.get("stockItem").toString(),MagicCardStock.class));
	        	}
	    	}
	  );
		
		return stocks;
	}
    
	
	@Override
	public void saveOrUpdateStock(MagicCardStock state) throws SQLException {
		logger.debug("saving " + state);
		
		if(state.getIdstock()==-1)
		{
			state.setIdstock(Integer.parseInt(getNextSequence().toString()));
			BasicDBObject obj = new BasicDBObject();
						  obj.put("stockItem", serialize(state));
		db.getCollection("stocks",BasicDBObject.class).insertOne(obj);
			
		}
		else //TODO : BSON filter doesn't work...
		{ 
			Bson filter = new Document("stockItem.idstock", state.getIdstock());
			BasicDBObject obj = new BasicDBObject();
						  obj.put("stockItem",  serialize(state));
			logger.debug(filter);
			UpdateResult res = db.getCollection("stocks",BasicDBObject.class).replaceOne(filter,obj);
			logger.debug(res);
		}
	}

	@Override
	public void saveAlert(MagicCardAlert alert) throws SQLException {
		logger.debug("saving alert " + alert);
		
		alert.setId(getNextSequence().toString());
		BasicDBObject obj = new BasicDBObject();
					  obj.put("alertItem",  serialize(alert));
					  obj.put("db_id",IDGenerator.generate(alert.getCard()));
		db.getCollection("alerts",BasicDBObject.class).insertOne(obj);
	}
	
	@Override
	public List<MagicCardAlert> getAlerts() {
		ArrayList<MagicCardAlert> ret= new ArrayList<MagicCardAlert>();
		db.getCollection("alerts",BasicDBObject.class).find().forEach(new Block<BasicDBObject>() {
	        @Override
	        public void apply(final BasicDBObject result) {
	        	ret.add(deserialize(result.get("alertItem").toString(),MagicCardAlert.class));
	        	}
	    	}
	  );
      return ret;
	}

	@Override
	public boolean hasAlert(MagicCard mc) {
		return db.getCollection("alerts",BasicDBObject.class).find(new BasicDBObject("db_id",IDGenerator.generate(mc))).limit(1).iterator().hasNext();
	}
	

	@Override //TODO : BSON filter doesn't work...
	public void updateAlert(MagicCardAlert alert) throws SQLException {
		Bson filter = new Document("alertItem.id", alert.getId());
		BasicDBObject obj = new BasicDBObject();
					  obj.put("alertItem",  serialize(alert));
					  obj.put("db_id",IDGenerator.generate(alert.getCard()));
					
		UpdateResult res = db.getCollection("alerts",BasicDBObject.class).replaceOne(filter,obj);
		logger.debug(res);
		
	}

	
	
	@Override
	public void deleteAlert(MagicCardAlert alert) throws SQLException {
		logger.debug("delete alert "  + alert);
		
	}
	
	@Override
	public void deleteStock(List<MagicCardStock> state) throws SQLException {
		logger.debug("remove " + state.size()  + " items in stock");
	}

	
	@Override
	public void backup(File f) throws Exception {
			
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
	
}

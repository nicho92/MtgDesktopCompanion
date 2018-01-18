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
import org.magic.tools.MagicCardComparator;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;

public class MongoDbDAO extends AbstractMagicDAO{

    
	MongoClient client;
	MongoDatabase db;
	CodecRegistry pojoCodecRegistry;
	Gson gson;
	
	
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
		MagicEdition ed = new MagicEdition();
					ed.setId("e02");
					ed.setSet("Explorer of ixalan");
		
		System.out.println(dao.getCardsCountGlobal(new MagicCollection("Library")));
		
		
		
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
		obj.put("card", mc);
		obj.put("edition",mc.getEditions().get(0));
		obj.put("collection", collection);
		String json = gson.toJson(obj);
		db.getCollection("cards",BasicDBObject.class).insertOne(BasicDBObject.parse(json));
	}

	@Override
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
		logger.debug("remove " + mc + " from " + collection);
		
		BasicDBObject andQuery = new BasicDBObject();
		List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
							obj.add(new BasicDBObject("card.id",mc.getId()));
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
				        	list.add(gson.fromJson(result.get("card").toString(),MagicCard.class));
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
	              Aggregates.group("$edition.id", Accumulators.sum("count", 1))
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
		BasicDBObject andQuery = new BasicDBObject();
		List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
							obj.add(new BasicDBObject("edition.id",me.getId()));
							obj.add(new BasicDBObject("collection.name", cols.getName()));
		andQuery.put("$and", obj);
		
		logger.trace(andQuery.toJson());
		
		return (int) db.getCollection("cards",BasicDBObject.class).count(andQuery);
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
			obj.add(new BasicDBObject("edition.id", me.getId()));
			query.put("$and", obj);
		}
							
		db.getCollection("cards", BasicDBObject.class).find(query).forEach(new Block<BasicDBObject>() {
				        @Override
				        public void apply(final BasicDBObject result) {
				        	ret.add(gson.fromJson(result.get("card").toString(),MagicCard.class));
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
		db.getCollection("cards", BasicDBObject.class).distinct("edition.id",query,String.class).into(ret);
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
	public List<MagicCollection> getCollectionFromCards(MagicCard mc)throws SQLException{
		
		return new ArrayList<MagicCollection>();
	}

	@Override
	public void deleteStock(List<MagicCardStock> state) throws SQLException {
		logger.debug("remove " + state.size()  + " items in stock");
	}

	
	@Override
	public List<MagicCardStock> getStocks(MagicCard mc, MagicCollection col) throws SQLException {
		return new ArrayList<MagicCardStock>();
	}
	
	public List<MagicCardStock> getStocks() throws SQLException {
		return new ArrayList<MagicCardStock>();
	}
	

	@Override
	public void saveOrUpdateStock(MagicCardStock state) throws SQLException {
		
	}
	
	@Override
	public void backup(File f) throws Exception {
			
	}

	@Override
	public List<MagicCardAlert> getAlerts() {
		
		return new ArrayList<MagicCardAlert>();
	}

	@Override
	public boolean hasAlert(MagicCard mc) {
		return false;
		
	}
	
	@Override
	public void saveAlert(MagicCardAlert alert) throws SQLException {
		
		
	}
	
	@Override
	public void updateAlert(MagicCardAlert alert) throws SQLException {
		
		
	}

	@Override
	public void deleteAlert(MagicCardAlert alert) throws SQLException {
		logger.debug("delete alert "  + alert);
		
	}
}

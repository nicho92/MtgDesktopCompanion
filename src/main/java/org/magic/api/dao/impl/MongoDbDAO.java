package org.magic.api.dao.impl;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicDAO;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;


public class MongoDbDAO extends AbstractMagicDAO{

    
	MongoClient client;
	MongoDatabase db;
	
	
	
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
		
		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		client = new MongoClient( getProperty("SERVERNAME").toString() , 
								  Integer.parseInt(getProperty("SERVERPORT").toString()) );
	
		
		db = client.getDatabase(getProperty("DB_NAME").toString());
		createDB();
		logger.info("init " + getName() +" done");
		 
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		MongoDbDAO dao = new MongoDbDAO();
		dao.init();
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
		 	{ 
		 		Document col1 = new Document(s, new MagicCollection(s));
		 		db.getCollection("collects").insertOne(col1);
		 	}
		 	
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
		
	}

	@Override
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
		logger.debug("remove " + mc + " from " + collection);
	}

	@Override
	public List<MagicCard> listCards() throws SQLException {
		logger.debug("list all cards");
	
		return null;
	}

	@Override
	public Map<String, Integer> getCardsCountGlobal(MagicCollection c) throws SQLException {
		return null;
	}

	@Override
	public int getCardsCount(MagicCollection cols,MagicEdition me) throws SQLException {
		return -1;
	}

	@Override
	public List<MagicCard> getCardsFromCollection(MagicCollection collection) throws SQLException {
		return null;
	}

	@Override
	public List<MagicCard> getCardsFromCollection(MagicCollection collection, MagicEdition me) throws SQLException {
		
		logger.debug("getCardsFromCollection " + collection + " " + me);
		return null;
	}

	@Override
	public List<String> getEditionsIDFromCollection(MagicCollection collection) throws SQLException {
		return null;
	}

	@Override
	public MagicCollection getCollection(String name) throws SQLException {
		return null;
	}

	@Override
	public void saveCollection(MagicCollection c) throws SQLException {

	}

	@Override
	public void removeCollection(MagicCollection c) throws SQLException {

	}

	@Override
	public List<MagicCollection> getCollections() throws SQLException {
		return null;
	}

	@Override
	public void removeEdition(MagicEdition me, MagicCollection col) throws SQLException {
		logger.debug("remove " + me + " from " + col);

	}

	@Override
	public String getDBLocation() {
		return props.getProperty("URL")+"/"+props.getProperty("DB_NAME");
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
		
		return null;
	}

	@Override
	public void deleteStock(List<MagicCardStock> state) throws SQLException {
		logger.debug("remove " + state.size()  + " items in stock");
	}

	
	@Override
	public List<MagicCardStock> getStocks(MagicCard mc, MagicCollection col) throws SQLException {
		return null;
	}
	
	public List<MagicCardStock> getStocks() throws SQLException {
		return null;
	}
	

	@Override
	public void saveOrUpdateStock(MagicCardStock state) throws SQLException {
		
	}
	
	@Override
	public void backup(File f) throws Exception {
			
	}

	@Override
	public List<MagicCardAlert> getAlerts() {
		
		return null;
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
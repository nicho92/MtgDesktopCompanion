package org.magic.api.dao.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.AbstractMagicDAO;

public class MysqlDAO extends AbstractMagicDAO{

	static final Logger logger = LogManager.getLogger(MysqlDAO.class.getName());
    Connection con;
    QueryRunner run;

    ResultSetHandler<MagicCollection> collectionsHandler = new BeanHandler<MagicCollection>(MagicCollection.class);
    ResultSetHandler<List<MagicCollection>> collectionsListHandler = new BeanListHandler<MagicCollection>(MagicCollection.class);
    
    ResultSetHandler<List<MagicCard>> cardsListHandler = new BeanListHandler<MagicCard>(MagicCard.class);
    ResultSetHandler<MagicCard> cardsHandler = new BeanHandler<MagicCard>(MagicCard.class);
    
	public MysqlDAO() throws ClassNotFoundException, SQLException {
	    super();	
		if(!new File(confdir, getName()+".conf").exists()){
			 props.put("DRIVER", "com.mysql.jdbc.Driver");
			 props.put("URL","jdbc:mysql://localhost:3306");
			 props.put("DB_NAME", "mtgdesktopclient");
			 props.put("LOGIN", "mtgdesktopclient");
			 props.put("PASSWORD", "mtgdesktopclient");
		save();
		}
	}
	

	public void init() throws SQLException, ClassNotFoundException {
		
		 Class.forName(props.getProperty("DRIVER"));
		 con=DriverManager.getConnection(props.getProperty("URL")+"/"+props.getProperty("DB_NAME"),props.getProperty("LOGIN"),props.getProperty("PASSWORD"));
		 run = new QueryRunner(); 
		 createDB();
	}

	 public boolean createDB()
	 {
		 try{
		 	logger.debug("Create table Cards");
		 	con.createStatement().executeUpdate("CREATE TABLE cards ( edition VARCHAR(250), collection VARCHAR(250), multiverseid INTEGER ,originalType VARCHAR (250),artist VARCHAR (250),store_url VARCHAR (250),number VARCHAR (250),id VARCHAR (250) PRIMARY KEY,power VARCHAR (250),text VARCHAR (250),toughness VARCHAR (250),cost VARCHAR (250),loyalty INTEGER ,watermarks VARCHAR (250),url VARCHAR (250),flavor VARCHAR (250),layout VARCHAR (250),originalText VARCHAR (250),cmc INTEGER ,name VARCHAR (250),fullType VARCHAR (250))");
		 	logger.debug("Create table collections");
		 	con.createStatement().executeUpdate("CREATE TABLE collections ( name VARCHAR(250))");
			con.createStatement().executeUpdate("insert into collections values ('Library')");
		 	con.createStatement().executeUpdate("insert into collections values ('Needed')");
		 	con.createStatement().executeUpdate("insert into collections values ('For sell')");
		 	logger.debug("populate collections");
		 	return true;
		 }
		 catch(SQLException e)
		 {
			 logger.debug(e);
			 return false;
		 }
		 
	 }
	
	
	@Override
	public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException {
		String sql = "INSERT INTO  `cards` (  `edition` ,  `collection` ,  `multiverseid` ,  `originalType` ,  `artist` ,  `store_url` ,  `number` ,  `id` ,  `power` ,  `text` ,  `toughness` ,  `cost` ,  `loyalty` ,  `watermarks` ,  `url` ,  `flavor` , `layout` ,  `originalText` ,  `cmc` ,  `name` ,  `fullType` )"+ 
					 "VALUES (?,  ?,  ?,  ?,  '?', ? ,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?, ? , ? , ? , ? ,  ?,  ?, ?);";
		
		PreparedStatement stat = con.prepareStatement(sql);
		stat.setString(1, mc.getEditions().get(0).getId());
		stat.setString(2, collection.getName());
		stat.setInt(3, mc.getMultiverseid());
		stat.setString(4, mc.getOriginalType());
		stat.setString(5, mc.getArtist());
		stat.setString(6, mc.getStore_url());
		stat.setString(7, mc.getNumber());
		stat.setString(8,mc.getId());
		stat.setString(8, mc.getPower());
		stat.setString(9, mc.getText());
		stat.setString(10, mc.getToughness());
		stat.setString(11, mc.getCost());
		stat.setInt(12, mc.getLoyalty());
		stat.executeQuery();
	}

	@Override
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public MagicCard loadCard(String name, MagicCollection collection) throws SQLException {
		 return run.query(con, "SELECT * FROM cards WHERE name=? and collection = ?", cardsHandler, name,collection.getName());
	}

	@Override
	public List<MagicCard> listCards() throws SQLException {
		return run.query(con, "SELECT * FROM cards", cardsListHandler);
	}

	@Override
	public int getCardsCount(MagicCollection list) throws SQLException {
		return 0;
	}

	@Override
	public List<MagicCard> getCardsFromCollection(MagicCollection collection) throws SQLException {
		 return run.query(con, "SELECT * FROM cards WHERE collection = ?", cardsListHandler, collection.getName());
	}

	@Override
	public List<MagicCard> getCardsFromCollection(MagicCollection collection, MagicEdition me) throws SQLException {
		 return run.query(con, "SELECT * FROM cards WHERE collection = ? and edition= ?", cardsListHandler, collection.getName(), me.getId());
	}

	@Override
	public List<String> getEditionsIDFromCollection(MagicCollection collection) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MagicCollection getCollection(String name) throws SQLException {
		 return run.query(con, "SELECT * FROM collections WHERE name = ?", collectionsHandler, name);
	}

	@Override
	public void saveCollection(MagicCollection c) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeCollection(MagicCollection c) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<MagicCollection> getCollections() throws SQLException {
		return run.query(con,"select * from collections",collectionsListHandler);
	}

	@Override
	public void removeEdition(MagicEdition ed, MagicCollection col) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDBLocation() {
		return props.getProperty("URL")+"/"+props.getProperty("DB_NAME");
	}

	@Override
	public long getDBSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		return "MySQL";
	}


	


}

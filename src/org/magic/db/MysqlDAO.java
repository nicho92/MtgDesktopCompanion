package org.magic.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicDAO;

public class MysqlDAO implements MagicDAO {

	static final Logger logger = LogManager.getLogger(MysqlDAO.class.getName());
    Connection con;
    String location;
	
	 public MysqlDAO() throws ClassNotFoundException, SQLException {
			init();
	}
	
	
	private void init() throws SQLException, ClassNotFoundException {
		 Class.forName("com.mysql.jdbc.Driver");
	      location = "jdbc:mysql://synology:3306/";
		  con=DriverManager.getConnection(location+"mtgdesktopclient","mtgdesktopclient","mtgdesktopclient");
		  
		  createDB();
		
	}

	 public boolean createDB()
	 {
		 try{
		 	logger.debug("Create table Cards");
		 	con.createStatement().executeUpdate("CREATE TABLE cards ( collection VARCHAR(250), multiverseid INTEGER ,originalType VARCHAR (250),artist VARCHAR (250),store_url VARCHAR (250),number VARCHAR (250),id VARCHAR (250),power VARCHAR (250),text VARCHAR (250),toughness VARCHAR (250),cost VARCHAR (250),loyalty INTEGER ,watermarks VARCHAR (250),url VARCHAR (250),flavor VARCHAR (250),layout VARCHAR (250),originalText VARCHAR (250),cmc INTEGER ,name VARCHAR (250),fullType VARCHAR (250))");
		 	logger.debug("Create table collections");
		 	con.createStatement().executeUpdate("insert into collections values ('Library')");
		 	con.createStatement().executeUpdate("insert into collections values ('Needed')");
		 	con.createStatement().executeUpdate("insert into collections values ('For sell')");
		 	logger.debug("populate collections");
		 	
		 	
		 	return true;
		 }catch(SQLException e)
		 {
			 logger.debug(e);
			 return false;
		 }
		 
	 }
	
	
	@Override
	public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public MagicCard loadCard(String name, MagicCollection collection) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MagicCard> listCards() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCardsCount(MagicCollection list) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<MagicCard> getCardsFromCollection(MagicCollection collection) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MagicCard> getCardsFromCollection(MagicCollection collection, MagicEdition me) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getEditionsIDFromCollection(MagicCollection collection) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MagicCollection getCollection(String name) throws SQLException {
		// TODO Auto-generated method stub
		return null;
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
		PreparedStatement pst=con.prepareStatement("select * from collections");	
		ResultSet rs = pst.executeQuery();
		List<MagicCollection> colls = new ArrayList<MagicCollection>();
		while(rs.next())
		{
			MagicCollection mc = new MagicCollection();
			mc.setName(rs.getString(1));
			colls.add(mc);
		}
		return colls;
	}

	@Override
	public void removeEdition(MagicEdition ed, MagicCollection col) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDBLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getDBSize() {
		// TODO Auto-generated method stub
		return 0;
	}

}

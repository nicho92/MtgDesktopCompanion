package org.magic.api.dao.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.ShopItem;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractMagicDAO;
import org.magic.api.providers.impl.MtgjsonProvider;
import org.magic.services.MagicFactory;

public class HsqlDAO extends AbstractMagicDAO{

	static final Logger logger = LogManager.getLogger(HsqlDAO.class.getName());
    Connection con;
    List<MagicCard> listNeeded ;
    
    public HsqlDAO() throws ClassNotFoundException, SQLException {
    	 super();	
 		if(!new File(confdir, getName()+".conf").exists()){
 			props.put("DRIVER", "org.hsqldb.jdbc.JDBCDriver");
 			props.put("URL", System.getProperty("user.home")+"/magicDeskCompanion/db");
 			props.put("DBNAME", "magicDB");
 			props.put("LOGIN", "SA");
 			props.put("PASS", "");
 		save();
 		}
	}
    
    
	public void init() throws ClassNotFoundException, SQLException {
	      logger.debug("init HsqlDB");
		  Class.forName(props.getProperty("DRIVER"));
	      con=DriverManager.getConnection("jdbc:hsqldb:"+props.getProperty("URL")+"/"+props.getProperty("DBNAME"),props.getProperty("LOGIN"),props.getProperty("PASS"));
		  
		  createDB();
		
	 }
	 
	 public boolean createDB()
	 {
		 try{
		 	con.createStatement().executeUpdate("create table cards (ID varchar(250), name varchar(250), mcard OBJECT, edition varchar(20), cardprovider varchar(50),collection varchar(250))");
		 	logger.debug("Create table Cards");
		 	con.createStatement().executeUpdate("create table decks (name varchar(45),mcard OBJECT)");
		 	logger.debug("Create table decks");
		 	con.createStatement().executeUpdate("create table collections (name varchar(250) PRIMARY KEY)");
		 	logger.debug("Create table collections");
		 	con.createStatement().executeUpdate("create table shop (id varchar(250), statut varchar(250))");
		 	logger.debug("Create table shopp");
		 	con.createStatement().executeUpdate("insert into collections values ('Library')");
		 	con.createStatement().executeUpdate("insert into collections values ('Needed')");
		 	con.createStatement().executeUpdate("insert into collections values ('For sell')");
		 	con.createStatement().executeUpdate("insert into collections values ('Favorites')");
		 	logger.debug("populate collections");
		 	
		 	
		 	return true;
		 }catch(SQLException e)
		 {
			 logger.debug(getName()+ ": Base already exist");
			 return false;
		 }
		 
	 }

	@Override
	public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException {
		
		logger.info("saving " + mc +" in " + collection);
		
		PreparedStatement pst = con.prepareStatement("insert into cards values (?,?,?,?,?,?)");
		 pst.setString(1, mc.getName());
		 pst.setObject(2, mc);
		 pst.setString(3, mc.getEditions().get(0).getId());
		 pst.setString(4, "");
		 pst.setString(5, collection.getName());
		 pst.setString(6, mc.getId());
		 
		 pst.executeUpdate();
		
	}

	@Override
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
		logger.info("remove " + mc + " from " + collection);
		
		String sql = "delete from cards where name=? and edition=? ";
		if(collection !=null)
			sql+=" and collection=?";
		
		
		PreparedStatement pst = con.prepareStatement(sql);
		 pst.setString(1, mc.getName());
		 pst.setString(2, mc.getEditions().get(0).getId());
		 if(collection !=null)
			 pst.setString(3, collection.getName());
		 
		 pst.executeUpdate();
	}

	public List<MagicCard> getCardsFromCollection(MagicCollection collection,MagicEdition me) throws SQLException
	{
		
		String sql ="select * from cards where collection= ? and edition = ?";
		
		if(me==null)
			sql ="select * from cards where collection= ?";
		
		if(collection==null)
			sql="select * from cards";
		
		
		PreparedStatement pst=con.prepareStatement(sql);	
		pst.setString(1, collection.getName());
		
		if(me!=null)
			pst.setString(2, me.getId());
		
		ResultSet rs = pst.executeQuery();
		List<MagicCard> list = new ArrayList<MagicCard>();
		while(rs.next())
		{
			try{
				list.add((MagicCard) rs.getObject("mcard"));
			}
			catch(Exception e)
			{
				throw new SQLException("Erreur " + rs.getString("name") + " " + rs.getString("edition") + " " + rs.getString("collection") + ": " + e.getMessage());
			}
		}
		
	
	return list;
	}
	
	
	@Override
	public List<MagicCard> getCardsFromCollection(MagicCollection collection) throws SQLException {
		return getCardsFromCollection(collection,null);
	}

	@Override
	public MagicCollection getCollection(String name) throws SQLException {
		PreparedStatement pst=con.prepareStatement("select * from collections where name= ?");	
			pst.setString(1, name);
		
		ResultSet rs = pst.executeQuery();
		
		if(rs.next())
		{
			MagicCollection mc = new MagicCollection();
			mc.setName(rs.getString("name"));
			
			return mc;
		}
		
		return null;
	}

	 
	
	@Override
	public void saveCollection(MagicCollection c) throws SQLException {

		PreparedStatement pst = con.prepareStatement("insert into collections values (?)");
		 pst.setString(1, c.getName());
		 
		 pst.executeUpdate();
	}

	@Override
	public void removeCollection(MagicCollection c) throws SQLException {
		
		if(c.getName().equals("Library"))
			throw new SQLException(c.getName() + " can not be deleted");
		
		PreparedStatement pst = con.prepareStatement("delete from collections where name = ?");
		 pst.setString(1, c.getName());
		 pst.executeUpdate();
		 
		 
		 pst = con.prepareStatement("delete from cards where collection = ?");
		 pst.setString(1, c.getName());
		 pst.executeUpdate();
		
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
	public void removeEdition(MagicEdition me, MagicCollection col) throws SQLException {
		logger.info("remove " + me + " from " + col);
		PreparedStatement pst = con.prepareStatement("delete from cards where edition=? and collection=?");
		 pst.setString(1, me.getId());
		 pst.setString(2, col.getName());
		 pst.executeUpdate();
		
	}

	@Override
	public int getCardsCount(MagicCollection cols) throws SQLException {
		
		String sql = "select count(*) from cards ";
			
		if(cols!=null)
			sql+=" where collection = '" + cols.getName()+"'";
		
		
		Statement st = con.createStatement();
		logger.debug(sql);
		
		
		ResultSet rs = st.executeQuery(sql);
		rs.next();
		return rs.getInt(1);
	}

	@Override
	public String getDBLocation() {
		return props.getProperty("URL");
	}

	@Override
	public long getDBSize() {
		return FileUtils.sizeOfDirectory(new File(props.getProperty("URL")));
	}
	
	
	

	@Override
	public MagicCard loadCard(String name, MagicCollection collection) throws SQLException {
		PreparedStatement pst=con.prepareStatement("select * from cards where collection= ? and name= ?");	
		pst.setString(1, collection.getName());
		pst.setString(2, name);
		ResultSet rs = pst.executeQuery();
		return (MagicCard) rs.getObject("mcard");
	}

	@Override
	public List<String> getEditionsIDFromCollection(MagicCollection collection) throws SQLException {
		String sql ="select distinct(edition) from cards where collection=?";
		
		PreparedStatement pst=con.prepareStatement(sql);	
		pst.setString(1, collection.getName());
		ResultSet rs = pst.executeQuery();
		List<String> list = new ArrayList<String>();
		while(rs.next())
		{
			list.add(rs.getString("edition"));
		}
	
	return list;
	}

	@Override
	public List<MagicCard> listCards() throws SQLException {
		String sql ="select * from cards";
		
		PreparedStatement pst=con.prepareStatement(sql);	
		
		ResultSet rs = pst.executeQuery();
		List<MagicCard> list = new ArrayList<MagicCard>();
		while(rs.next())
		{
			try{
				list.add((MagicCard) rs.getObject("mcard"));
			}catch(Exception e)
			{
				throw new SQLException("Erreur " + rs.getString("name") + " " + rs.getString("edition") + " " + rs.getString("collection") + ": " + e.getMessage());
			}
		}
	
	return list;
	}

	public String getName() {
		return "hSQLdb";
	}


	@Override
	public List<MagicDeck> listDeck() throws SQLException {
		String sql ="select * from decks";
		
		PreparedStatement pst=con.prepareStatement(sql);	
		
		ResultSet rs = pst.executeQuery();
		List<MagicDeck> list = new ArrayList<MagicDeck>();
		while(rs.next())
		{
			list.add((MagicDeck) rs.getObject("mcard"));
		}
	return list;
	}


	@Override
	public void saveDeck(MagicDeck d) throws SQLException {

		logger.debug("saving " + d);
		PreparedStatement pst = con.prepareStatement("insert into decks values (?,?)");
		 pst.setString(1, d.getName());
		 pst.setObject(2, d);
		 //pst.setString(3,d.getColors());
		 pst.executeUpdate();
		
	}


	@Override
	public void deleteDeck(MagicDeck d) throws SQLException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public List<MagicCollection> getCollectionFromCards(MagicCard mc) throws SQLException {
		
		if(mc.getEditions().size()==0)
			throw new SQLException("No edition defined");
		
		PreparedStatement pst = con.prepareStatement("SELECT COLLECTION FROM CARDS WHERE name=? and edition=?");
		 pst.setString(1, mc.getName());
		 pst.setString(2, mc.getEditions().get(0).getId());
		 logger.debug("SELECT COLLECTION FROM CARDS WHERE name='"+mc.getName()+"' and edition='"+mc.getEditions().get(0).getId()+"'");
		 ResultSet rs = pst.executeQuery();
		 List<MagicCollection> cols = new ArrayList<MagicCollection>();
		 while(rs.next())
		 {
			 MagicCollection col = new MagicCollection();
			 col.setName(rs.getString("COLLECTION"));
			 cols.add(col);
		 }
		 
		 return cols;
	}


	@Override
	public void saveShopItem(ShopItem mp, String string) throws SQLException {
			logger.debug("trying to update " + mp);
			PreparedStatement pst = con.prepareStatement("update shop set statut=? where id=?");
			pst.setString(1, string);
			pst.setString(2, mp.getId());
			if(pst.executeUpdate()==0)
			{
				logger.debug("trying to insert " + mp);
				pst = con.prepareStatement("insert into shop values (?,?)");
				pst.setString(1, mp.getId());
				pst.setString(2, string);
				pst.executeUpdate();
			}
	}


	@Override
	public String getSavedShopItemAnotation(ShopItem it) throws SQLException {
		PreparedStatement pst = con.prepareStatement("SELECT statut from shop where id=?");
		pst.setString(1, it.getId());
		//logger.debug("looking for shopItem : SELECT statut from shop where id=" + it.getId());
		ResultSet rs = pst.executeQuery(); 
		if(rs.next())
			return rs.getString("statut");
		else
			return "";
		
	}
	
	public ResultSet executeQuery(String query) throws SQLException
	{
		Statement pst = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = pst.executeQuery(query);
		
		return rs;
	}
	
	public int executeUpdate(String query) throws SQLException
	{
		PreparedStatement pst = con.prepareStatement(query);
		return pst.executeUpdate();
		
	}
	
//	public int updateSerializedCard(MagicCard mc,String editionCode,String collection) 
//	{
//		try{
//		String sql ="update cards set mcard=? where  name=? and edition=? and collection=? ";
//		PreparedStatement pst = con.prepareStatement(sql);
//		pst.setObject(1, mc);
//		pst.setString(2, mc.getName());
//		pst.setString(3, editionCode);
//		pst.setString(4, collection);
//		return pst.executeUpdate();
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			return -1;
//		}
//		
//	}
	
	


}



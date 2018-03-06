package org.magic.api.dao.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicNews;
import org.magic.api.beans.MagicNews.NEWS_TYPE;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicDAO;
import org.magic.tools.IDGenerator;

public class HsqlDAO extends AbstractMagicDAO{

	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}
	
	private String cardField="mcard";
	private Connection con;
   
  
	public void init() throws ClassNotFoundException, SQLException {
	      logger.info("init HsqlDB");
		  Class.forName(getString("DRIVER"));
	      con=DriverManager.getConnection("jdbc:hsqldb:"+getString("URL")+"/"+getString("DBNAME"),getString("LOGIN"),getString("PASS"));
		  
		  createDB();
		  
		  list=new ArrayList<>();
		
	 }
	 
	 public boolean createDB()
	 {
		 try(Statement stat=con.createStatement())
		 {
			stat.executeUpdate("create table cards (ID varchar(250), name varchar(250), mcard OBJECT, edition varchar(20), cardprovider varchar(50),collection varchar(250))");
		 	logger.debug("Create table Cards");
		 	stat.executeUpdate("create table decks (name varchar(45),mcard OBJECT)");
		 	logger.debug("Create table decks");
		 	stat.executeUpdate("create table collections (name varchar(250) PRIMARY KEY)");
		 	logger.debug("Create table collections");
		 	stat.executeUpdate("create table shop (id varchar(250), statut varchar(250))");
		 	logger.debug("Create table shop");
		 	stat.executeUpdate("create table stocks (idstock integer PRIMARY KEY IDENTITY, idmc varchar(250), collection varchar(250),comments varchar(250), conditions varchar(50),foil boolean, signedcard boolean, langage varchar(50), qte integer,mcard OBJECT,altered boolean,price double)");
		 	logger.debug("Create table stocks");
		 	stat.executeUpdate("create table alerts (id varchar(250),mcard OBJECT, amount DECIMAL)");
		 	logger.debug("Create table Alerts");
		 	stat.executeUpdate("CREATE TABLE news (id integer PRIMARY KEY IDENTITY, name VARCHAR(100), url VARCHAR(256), categorie VARCHAR(100),typeNews VARCHAR(100))");
		 	logger.debug("Create table News");
			
		 	
		 	stat.executeUpdate("insert into collections values ('Library')");
		 	stat.executeUpdate("insert into collections values ('Needed')");
		 	stat.executeUpdate("insert into collections values ('For sell')");
		 	stat.executeUpdate("insert into collections values ('Favorites')");
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
		
		try(PreparedStatement pst = con.prepareStatement("insert into cards values (?,?,?,?,?,?)"))
		{
			 pst.setString(1, IDGenerator.generate(mc)); 
			 pst.setString(2, mc.getName());
			 pst.setObject(3, mc);
			 pst.setString(4, mc.getEditions().get(0).getId());
			 pst.setString(5, "");
			 pst.setString(6, collection.getName());
			 pst.executeUpdate();
		}
	}

	@Override
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
		logger.trace("delete " + mc + " from " + collection);
		
		String sql = "delete from cards where name=? and edition=?";
		if(collection !=null)
			sql="delete from cards where name=? and edition=? and collection=?";
		
		
		try (PreparedStatement pst = con.prepareStatement(sql))
		{
			 pst.setString(1, mc.getName());
			 pst.setString(2, mc.getEditions().get(0).getId());
			 if(collection !=null)
				 pst.setString(3, collection.getName());
			 
			 pst.executeUpdate();
		}
	}

	public List<MagicCard> listCardsFromCollection(MagicCollection collection,MagicEdition me) throws SQLException
	{
		logger.trace("cards for " + collection + " " + me);
		
		String sql ="select mcard from cards where collection= ?";
		if(me!=null)
			sql ="select mcard from cards where collection= ? and edition = ?";
		
		if(collection==null)
			throw new SQLException("collection must not be null");
		
		
		try(PreparedStatement pst=con.prepareStatement(sql))	
		{	
			pst.setString(1, collection.getName());
			
			if(me!=null)
				pst.setString(2, me.getId());
			
			try(ResultSet rs = pst.executeQuery())
			{	List<MagicCard> retour = new ArrayList<>();
				while(rs.next())
				{
					try{
						retour.add((MagicCard) rs.getObject(cardField));
					}
					catch(Exception e)
					{
						throw new SQLException("ERROR ",e);
					}
				}
				return retour;
			}
		}
	}
	
	
	@Override
	public List<MagicCard> listCardsFromCollection(MagicCollection collection) throws SQLException {
		return listCardsFromCollection(collection,null);
	}

	@Override
	public MagicCollection getCollection(String name) throws SQLException {
		try(PreparedStatement pst=con.prepareStatement("select * from collections where name= ?"))
		{
			pst.setString(1, name);
			try(ResultSet rs = pst.executeQuery())
			{
				if(rs.next())
				{
					MagicCollection mc = new MagicCollection();
					mc.setName(rs.getString("name"));
					
					return mc;
				}
				return null;
			}
		
		}
	}

	 
	
	@Override
	public void saveCollection(MagicCollection c) throws SQLException {

		try(PreparedStatement pst = con.prepareStatement("insert into collections values (?)"))
		{
			pst.setString(1, c.getName());
			pst.executeUpdate();
		}
	}

	@Override
	public void removeCollection(MagicCollection c) throws SQLException {
		
		if(c.getName().equals("Library"))
			throw new SQLException(c.getName() + " can not be deleted");
		
		try(PreparedStatement pst = con.prepareStatement("delete from collections where name = ?"))
		{
			 pst.setString(1, c.getName());
			 pst.executeUpdate();
		}
		 
		 
		try( PreparedStatement pst2 = con.prepareStatement("delete from cards where collection = ?"))
		{
			 pst2.setString(1, c.getName());
			 pst2.executeUpdate();
		}
		
	}

	@Override
	public List<MagicCollection> getCollections() throws SQLException {
		try(PreparedStatement pst=con.prepareStatement("select * from collections"))
		{
			try(ResultSet rs = pst.executeQuery())
			{
				List<MagicCollection> colls = new ArrayList<>();
				while(rs.next())
				{
					MagicCollection mc = new MagicCollection();
					mc.setName(rs.getString(1));
					colls.add(mc);
				}
				return colls;
			}
		
		}
		
	}

	@Override
	public void removeEdition(MagicEdition me, MagicCollection col) throws SQLException {
		logger.info("remove " + me + " from " + col);
		try(PreparedStatement pst = con.prepareStatement("delete from cards where edition=? and collection=?"))
		{
		 pst.setString(1, me.getId());
		 pst.setString(2, col.getName());
		 pst.executeUpdate();
		}
	}
	
	@Override
	public Map<String, Integer> getCardsCountGlobal(MagicCollection c) throws SQLException {
		String sql = "select edition, count(name) from cards where collection=? group by edition";
		try(PreparedStatement pst=con.prepareStatement(sql))
		{
			pst.setString(1, c.getName());
			try(ResultSet rs = pst.executeQuery())
			{
				Map<String,Integer> map= new HashMap<>();
				while(rs.next())
				{
					map.put(rs.getString(1), rs.getInt(2));
				}
				return map;
			}
		}
	}

	
	@Override
	public int getCardsCount(MagicCollection cols,MagicEdition me) throws SQLException {
		
		String sql = "select count(name) from cards ";
			
		if(cols!=null)
			sql+=" where collection = '" + cols.getName()+"'";
		
		if(me!=null)
			sql+=" and edition = '" + me.getId()+"'";
	
		logger.trace(sql);
		
		try(Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql))
		{
					rs.next();
					return rs.getInt(1);
		}
	}

	@Override
	public String getDBLocation() {
		return getString("URL");
	}

	@Override
	public long getDBSize() {
		return FileUtils.sizeOfDirectory(new File(getString("URL")));
	}
	
	@Override
	public List<String> getEditionsIDFromCollection(MagicCollection collection) throws SQLException {
		String sql ="select distinct(edition) from cards where collection=?";
		
		try (PreparedStatement pst=con.prepareStatement(sql))
		{
			pst.setString(1, collection.getName());
			try(ResultSet rs = pst.executeQuery())
			{	List<String> ret = new ArrayList<>();
				while(rs.next())
				{
					ret.add(rs.getString("edition"));
				}
				return ret;
			}
		}
		
	}

	@Override
	public List<MagicCard> listCards() throws SQLException {
		logger.trace("list all cards");
		
		String sql ="select mcard from cards";
		
		try(PreparedStatement pst=con.prepareStatement(sql))
		{
			try(ResultSet rs = pst.executeQuery())
			{
				List<MagicCard> ret = new ArrayList<>();
				while(rs.next())
				{
					try{
						ret.add((MagicCard) rs.getObject(cardField));
					}catch(Exception e)
					{
						throw new SQLException("ERROR",e);
					}
				}
			
				return ret;
			}
		}
	}

	public String getName() {
		return "hSQLdb";
	}
	@Override
	public List<MagicCollection> listCollectionFromCards(MagicCard mc) throws SQLException {
		
		if(mc.getEditions().isEmpty())
			throw new SQLException("No edition defined");
		
		 try(PreparedStatement pst = con.prepareStatement("SELECT COLLECTION FROM CARDS WHERE name=? and edition=?"))
		 {
			 pst.setString(1, mc.getName());
			 pst.setString(2, mc.getEditions().get(0).getId());
			 logger.trace("SELECT COLLECTION FROM CARDS WHERE name='"+mc.getName()+"' and edition='"+mc.getEditions().get(0).getId()+"'");
			 try(ResultSet rs = pst.executeQuery())
			 {
				 List<MagicCollection> cols = new ArrayList<>();
				 while(rs.next())
				 {
					 MagicCollection col = new MagicCollection();
					 col.setName(rs.getString("COLLECTION"));
					 cols.add(col);
				 }
				 
				 return cols;
			 }
		 }
	}


	@Override
	public void backup(File dir) throws IOException {
		File base = new File(getString("URL"));
		try(ZipOutputStream out = new ZipOutputStream(new FileOutputStream(new File(dir,"backup.zip"))))
		{
			for(File doc :base.listFiles())
			{
				if(!doc.getName().endsWith(".tmp"))
				{
					try(FileInputStream in = new FileInputStream(doc))
					{	
						out.putNextEntry(new ZipEntry(doc.getName()));
						int len;
						while ((len = in.read(new byte[4096])) > 0) {
							out.write(new byte[4096], 0, len);
						}
						out.closeEntry();
					}
				}
			}
		}
	}


	@Override
	public List<MagicCardStock> listStocks(MagicCard mc, MagicCollection col) throws SQLException {
		logger.debug("load stock for " + mc + " in " + col);
		try(PreparedStatement pst=con.prepareStatement("select * from stocks where idmc=? and collection=?"))
		{
			pst.setString(1, IDGenerator.generate(mc));
			pst.setString(2, col.getName());
			try(ResultSet rs = pst.executeQuery())
			{
				List<MagicCardStock> colls = new ArrayList<>();
				while(rs.next())
				{
					MagicCardStock state = new MagicCardStock();
					
						state.setComment(rs.getString("comments"));
						state.setIdstock(rs.getInt("idstock"));
						state.setMagicCard(mc);
						state.setMagicCollection(col);
						state.setCondition( EnumCondition.valueOf(rs.getString("conditions")) );
						state.setFoil(rs.getBoolean("foil"));
						state.setSigned(rs.getBoolean("signedcard"));
						state.setLanguage(rs.getString("langage"));
						state.setQte(rs.getInt("qte"));
						state.setPrice(rs.getDouble("price"));
						
						colls.add(state);
				}
				logger.debug("load " + colls.size() +" item from stock for " + mc );

				return colls;
			}
			
		}
		
	}

	@Override
	public void saveOrUpdateStock(MagicCardStock state) throws SQLException {
		if(state.getIdstock()<0)
		{
			
			logger.debug("save "  + state);
			try(PreparedStatement pst=con.prepareStatement("insert into stocks  ( conditions,foil,signedcard,langage,qte,comments,idmc,collection,altered,price,mcard) values (?,?,?,?,?,?,?,?,?,?,?)"))
			{
				pst.setString(1, state.getCondition().toString());
				pst.setBoolean(2,state.isFoil());
				pst.setBoolean(3, state.isSigned());
				pst.setString(4, state.getLanguage());
				pst.setInt(5, state.getQte());
				pst.setString(6, state.getComment());
				pst.setString(7, IDGenerator.generate(state.getMagicCard()));
				pst.setString(8, state.getMagicCollection().getName());
				pst.setBoolean(9, state.isAltered());
				pst.setDouble(10, state.getPrice());
				pst.setObject(11, state.getMagicCard());
				state.setIdstock(pst.executeUpdate());
			}
		}
		else
		{
			logger.debug("update "  + state);
			try(PreparedStatement pst=con.prepareStatement("update stocks set comments=?, conditions=?, foil=?,signedcard=?,langage=?, qte=?,altered=?,price=? where idstock=?"))
			{
				pst.setString(1,state.getComment());
				pst.setString(2, state.getCondition().toString());
				pst.setBoolean(3,state.isFoil());
				pst.setBoolean(4, state.isSigned());
				pst.setString(5, state.getLanguage());
				pst.setInt(6, state.getQte());
				pst.setBoolean(7, state.isAltered());
				pst.setInt(9, state.getIdstock());
				pst.setDouble(8, state.getPrice());
				pst.executeUpdate();
			}
		}
	}

	
	List<MagicCardAlert> list;
	@Override
	public List<MagicCardAlert> listAlerts() {
		
		try{
			
				try(PreparedStatement pst=con.prepareStatement("select * from alerts");ResultSet rs = pst.executeQuery();)
				{
					list = new ArrayList<>();
					
					while(rs.next())
					{
						MagicCardAlert alert = new MagicCardAlert();
									   alert.setCard((MagicCard)rs.getObject(cardField));
									   alert.setId(rs.getString("id"));
									   alert.setPrice(rs.getDouble("amount"));
						   list.add(alert);
					}
					return list;
				}
		}
		catch(Exception e)
		{
			return new ArrayList<>();
		}
		
	}

	@Override
	public void saveAlert(MagicCardAlert alert) throws SQLException {
		logger.debug("save "  + alert);
		try(PreparedStatement pst=con.prepareStatement("insert into alerts  ( id,mcard,amount) values (?,?,?)"))
		{
			pst.setString(1, IDGenerator.generate(alert.getCard()));
			pst.setObject(2,alert.getCard());
			pst.setDouble(3, alert.getPrice());
			list.add(alert);
			pst.executeUpdate();
		}
	}

	@Override
	public void deleteAlert(MagicCardAlert alert) throws SQLException {
		logger.debug("delete "  + alert);
		try(PreparedStatement pst=con.prepareStatement("delete from alerts where id=?"))
		{
			pst.setString(1, IDGenerator.generate(alert.getCard()));
			list.remove(alert);
			pst.executeUpdate();
			
		}
	}

	@Override
	public void updateAlert(MagicCardAlert alert) throws SQLException {
		try(PreparedStatement pst=con.prepareStatement("update alerts set amount=? where id=?"))
		{
			pst.setDouble(1, alert.getPrice());
			pst.setString(2, alert.getId());
			pst.executeUpdate();
			list.remove(alert);
			list.add(alert);
		}
	}


	@Override
	public boolean hasAlert(MagicCard mc) {
		
		try(PreparedStatement pst=con.prepareStatement("select * from alerts where id=?"))
		{
			pst.setString(1, IDGenerator.generate(mc));
			try(ResultSet rs = pst.executeQuery())
			{
				return rs.next();
			}
		}
		catch(Exception e)
		{
			logger.error(e);
			return false;
		}
	}


	public List<MagicCardStock> listStocks() throws SQLException {
		try(PreparedStatement pst=con.prepareStatement("select * from stocks");ResultSet rs = pst.executeQuery())
		{
			List<MagicCardStock> colls = new ArrayList<>();
			while(rs.next())
			{
				MagicCardStock state = new MagicCardStock();
				
					state.setComment(rs.getString("comments"));
					state.setIdstock(rs.getInt("idstock"));
					state.setMagicCard((MagicCard)rs.getObject(cardField));
					state.setMagicCollection(new MagicCollection(rs.getString("collection")));
					state.setCondition( EnumCondition.valueOf(rs.getString("conditions")) );
					state.setFoil(rs.getBoolean("foil"));
					state.setSigned(rs.getBoolean("signedcard"));
					state.setLanguage(rs.getString("langage"));
					state.setQte(rs.getInt("qte"));
					state.setPrice(rs.getDouble("price"));
					colls.add(state);
			}
			logger.debug("load " + colls.size() +" item(s) from stock");
			return colls;
		}
		
	}

	@Override
	public void deleteStock(List<MagicCardStock> state) throws SQLException {
		logger.debug("remove " + state.size()  + " items in stock");
		StringBuilder st = new StringBuilder();
		st.append("delete from stocks where idstock IN (");
			for(MagicCardStock sto : state)
			{
				st.append(sto.getIdstock()).append(",");
			}
		st.append(")");
		String sql = st.toString().replace(",)", ")");
		try(Statement pst = con.createStatement())
		{
			pst.executeUpdate(sql);	
		}
		
		
	}

	@Override
	public List<MagicNews> listNews() {
		try(PreparedStatement pst=con.prepareStatement("select * from news"))
		{
				List<MagicNews> news = new ArrayList<>();
				try(ResultSet rs = pst.executeQuery())
				{	
					while(rs.next())
					{
						MagicNews n = new MagicNews();
								n.setCategorie(rs.getString("categorie"));
								n.setName(rs.getString("name"));
								n.setUrl(rs.getString("url"));
								n.setType(NEWS_TYPE.valueOf(rs.getString("typeNews")));
								n.setId(rs.getInt("id"));
								news.add(n);
					}
					return news;
				}
		}catch(Exception e)
		{
			logger.error(e);
			return new ArrayList<>();
		}
	}

	@Override
	public void deleteNews(MagicNews n) throws SQLException {
		logger.debug("delete news "  + n);
		try(PreparedStatement pst=con.prepareStatement("delete from news where id=?"))
		{
			pst.setInt(1,n.getId());
			pst.executeUpdate();
		}
	}


	@Override
	public void saveOrUpdateNews(MagicNews n)throws SQLException {
		if(n.getId()<0)
		{
			
			logger.debug("save "  + n);
			try(PreparedStatement pst=con.prepareStatement("insert into news  ( name,categorie,url,typeNews) values (?,?,?,?)"))
			{
				pst.setString(1, n.getName());
				pst.setString(2,n.getCategorie());
				pst.setString(3,n.getUrl());
				pst.setString(4, n.getType().toString());
				n.setId(pst.executeUpdate());
			}
			
		}
		else
		{
			logger.debug("update "  + n);
			try(PreparedStatement pst=con.prepareStatement("update news set name=?, categorie=?, url=?,typeNews=? where id=?"))
			{
				pst.setString(1,n.getName());
				pst.setString(2, n.getCategorie());
				pst.setString(3,n.getUrl());
				pst.setString(4, n.getType().toString());
				pst.setInt(5, n.getId());
				pst.executeUpdate();
			}
			catch(Exception e)
			{
				logger.error(e);
			}
		}
		
	}


	@Override
	public void initDefault() {
			setProperty("DRIVER", "org.hsqldb.jdbc.JDBCDriver");
			setProperty("URL", confdir.getAbsolutePath()+"/hsqldao");
			setProperty("DBNAME", "magicDB");
			setProperty("LOGIN", "SA");
			setProperty("PASS", "");
		
	}
}



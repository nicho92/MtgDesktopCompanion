package org.magic.api.dao.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
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
import org.magic.services.MTGControler;
import org.magic.tools.IDGenerator;


public class PostgresqlDAO extends AbstractMagicDAO {

	private Connection con;
	private List<MagicCardAlert> list;
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	public PostgresqlDAO() {
		super();	
		if(!new File(confdir, getName()+".conf").exists()){
			 setProperty("DRIVER", "org.postgresql.Driver");
			 setProperty("SERVERNAME","localhost");
			 setProperty("SERVERPORT", "5432");
			 setProperty("DB_NAME", "mtgdesktopcompanion");
			 setProperty("LOGIN", "postgres");
			 setProperty("PASS", "postgres");
			 setProperty("URL_PGDUMP", "C:/Program Files (x86)/PostgreSQL/9.5/bin");
		
		save();
		}
		list=new ArrayList<>();
	}
   
	@Override
	public void init() throws ClassNotFoundException, SQLException {
		 logger.info("init " + getName());
		 Class.forName(getProperty("DRIVER"));
		 String url = "jdbc:postgresql://"+getProperty("SERVERNAME")+":"+getProperty("SERVERPORT");
		 con=DriverManager.getConnection(url+"/"+getProperty("DB_NAME"),getProperty("LOGIN"),getProperty("PASS"));
		 createDB();
	}

	 public boolean createDB()
	 {
		 try(Statement stat=con.createStatement())
		 {
		 	logger.debug("Create table Cards");
		 	stat.executeUpdate("create table cards (ID varchar(250),name varchar(250), mcard bytea, edition varchar(20), cardprovider varchar(50),collection varchar(250))");
		 	logger.debug("Create table Shop");
		 	stat.executeUpdate("create table shop (id varchar(250), statut varchar(250))");
		 	logger.debug("Create table collections");
		 	stat.executeUpdate("CREATE TABLE collections ( name VARCHAR(250))");
		 	logger.debug("Create table stocks");
		 	stat.executeUpdate("create table stocks (idstock SERIAL PRIMARY KEY , idmc varchar(250), collection varchar(250),comments varchar(250), conditions varchar(50),foil boolean, signedcard boolean, langage varchar(50), qte integer,mcard bytea,altered boolean,price decimal)");
		 	logger.debug("Create table Alerts");
		 	stat.executeUpdate("create table alerts (id varchar(250), mcard bytea, amount decimal)");
		 	logger.debug("Create table News");
			stat.executeUpdate("CREATE TABLE news (id SERIAL PRIMARY KEY, name VARCHAR(100), url VARCHAR(256), categorie VARCHAR(100),typeNews VARCHAR(100))");
		 	
		 	
		 	logger.debug("populate collections");
		 	stat.executeUpdate("insert into collections values ('Library')");
		 	stat.executeUpdate("insert into collections values ('Needed')");
		 	stat.executeUpdate("insert into collections values ('For sell')");
		 	stat.executeUpdate("insert into collections values ('Favorites')");
		 	return true;
		 }
		 catch(SQLException e)
		 {
			 logger.debug(e);
			 return false;
		 }
		 
	 }

	 ObjectInputStream oin;
	 private <T> T readObject(Class <T> classe, InputStream o )
	 {
		try {
			logger.trace("loading " + classe);
			oin = new ObjectInputStream(o);
			return (T)oin.readObject();
		} catch (Exception e) {
			return null;
		}
		 
	 }
	 
	 private ByteArrayInputStream convertObject(Object c)
	 {
		 try(ByteArrayOutputStream baos = new ByteArrayOutputStream();ObjectOutputStream oos = new ObjectOutputStream(baos);)
		 {
			oos.writeObject(c);
			return new ByteArrayInputStream(baos.toByteArray());
		 } catch (IOException e) {
			logger.error(e);
			return null;
		}
	 }
		
		@Override
		public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException {
			logger.debug("saving " + mc +" in " + collection);
			try( PreparedStatement pst = con.prepareStatement("insert into cards values (?,?,?,?,?,?)");)
			{
			 pst.setString(1, IDGenerator.generate(mc)); 
			 pst.setString(2, mc.getName());
			 pst.setBinaryStream(3, convertObject(mc));
			 pst.setString(4, mc.getEditions().get(0).getId());
			 pst.setString(5, MTGControler.getInstance().getEnabledProviders().toString());
			 pst.setString(6, collection.getName());
			 pst.executeUpdate();
			}
		}

		@Override
		public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
			logger.debug("delete " + mc + " from " + collection);
			try(PreparedStatement pst = con.prepareStatement("delete from cards where name=? and edition=? and collection=?"))
			{
				 pst.setString(1, mc.getName());
				 pst.setString(2, mc.getEditions().get(0).getId());
				 pst.setString(3, collection.getName());
				 pst.executeUpdate();
			}
		}

		@Override
		public List<MagicCard> listCards() throws SQLException {
			logger.debug("list all cards");
			
			String sql ="select * from cards";
			
			try(PreparedStatement pst=con.prepareStatement(sql);ResultSet rs = pst.executeQuery();)
			{
				List<MagicCard> ret = new ArrayList<>();
				while(rs.next())
				{
					ret.add(readObject(MagicCard.class, rs.getBinaryStream("mcard")));
				}
				return ret;
				
			}
		}

		@Override
		public Map<String, Integer> getCardsCountGlobal(MagicCollection c) throws SQLException {
			String sql = "select edition, count(name) from cards where collection=? group by edition";
			try(PreparedStatement pst=con.prepareStatement(sql))
			{
				pst.setString(1, c.getName());
				try(ResultSet rs = pst.executeQuery();)
				{
					Map<String,Integer> map= new HashMap<>();
					while(rs.next())
						map.put(rs.getString(1), rs.getInt(2));
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
			
			logger.debug(sql);
			
			try(Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql);)
			{
				rs.next();
				return rs.getInt(1);
			}
		}

		@Override
		public List<MagicCard> listCardsFromCollection(MagicCollection collection) throws SQLException {
			return listCardsFromCollection(collection,null);
		}

		@Override
		public List<MagicCard> listCardsFromCollection(MagicCollection collection, MagicEdition me) throws SQLException {
			logger.debug("getCardsFromCollection " + collection + " " + me);
			String sql ="select * from cards where collection= ?";
			if(me!=null)
				sql ="select * from cards where collection= ? and edition = ?";
				
			
			try(PreparedStatement pst=con.prepareStatement(sql))
			{
				pst.setString(1, collection.getName());
				
				if(me!=null)
					pst.setString(2, me.getId());
				
				try(ResultSet rs = pst.executeQuery())
				{
					List<MagicCard> ret = new ArrayList<>();
					while(rs.next())
					{
						ret.add(readObject(MagicCard.class, rs.getBinaryStream("mcard")));
					}
				
				return ret;
					
				}
			}
			
		}

		@Override
		public List<String> getEditionsIDFromCollection(MagicCollection collection) throws SQLException {
			String sql ="select distinct(edition) from cards where collection=?";
			
			try(PreparedStatement pst=con.prepareStatement(sql))
			{
				pst.setString(1, collection.getName());
				try(ResultSet rs = pst.executeQuery())
				{
					List<String> ret = new ArrayList<>();
					while(rs.next())
					{
						ret.add(rs.getString("edition"));
					}
				
				return ret;
				}
			}
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
			
			if(c.getName().equals(MTGControler.getInstance().get("default-library")))
				throw new SQLException(c.getName() + " can not be deleted");
			
			try(PreparedStatement pst = con.prepareStatement("delete from collections where name = ?"))
			{
				pst.setString(1, c.getName());
				pst.executeUpdate();
			}
			try(PreparedStatement pst = con.prepareStatement("delete from cards where collection = ?"))
			{
				 pst.setString(1, c.getName());
				 pst.executeUpdate();
			}

		}

		@Override
		public List<MagicCollection> getCollections() throws SQLException {
			try(PreparedStatement pst=con.prepareStatement("select * from collections");ResultSet rs = pst.executeQuery())
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

		@Override
		public void removeEdition(MagicEdition me, MagicCollection col) throws SQLException {
			logger.debug("remove " + me + " from " + col);
			 try(PreparedStatement pst = con.prepareStatement("delete from cards where edition=? and collection=?"))
			 {
				 pst.setString(1, me.getId());
				 pst.setString(2, col.getName());
				 pst.executeUpdate();
				 
			 }
		}

		@Override
		public String getDBLocation() {
			return getProperty("SERVERNAME")+"/"+getProperty("DB_NAME");
		}

		@Override
		public long getDBSize() {
			return 0;
		
		}

		@Override
		public String getName() {
			return "PostGreSQL";
		}


		@Override
		public List<MagicCollection> listCollectionFromCards(MagicCard mc)throws SQLException{
			
			if(mc.getEditions().isEmpty())
				throw new SQLException("No edition defined");
			
			 try(PreparedStatement pst = con.prepareStatement("SELECT collection FROM cards WHERE name=? and edition=?"))
			 {
				 pst.setString(1, mc.getName());
				 pst.setString(2, mc.getEditions().get(0).getId());
				try( ResultSet rs = pst.executeQuery())
				{
					 List<MagicCollection> cols = new ArrayList<>();
					 while(rs.next())
					 {
						 MagicCollection col = new MagicCollection();
						 col.setName(rs.getString("collection"));
						 cols.add(col);
					 }
					 return cols;
				}
			 }
			 
		}


		@Override
		public void backup(File f) throws IOException {
			
			if(getProperty("URL_PGDUMP").length()<=0)
			{
				throw new NullPointerException("Please fill URL_PGDUMP var");
			}
			
			String dumpCommand = getProperty("URL_PGDUMP")+"/pg_dump"+
						" -d" + getProperty("DB_NAME") + 
						" -h" + getProperty("SERVERNAME") + 
						" -U" + getProperty("LOGIN") +
						" -p" + getProperty("SERVERPORT");
			
			
			Runtime rt = Runtime.getRuntime();
			logger.info("begin Backup :" + dumpCommand);
			
			Process child = rt.exec(dumpCommand);
			try(PrintStream ps=new PrintStream(f))
			{
				InputStream in = child.getInputStream();
				int ch;
				while ((ch = in.read()) != -1) 
				{
					ps.write(ch);
				}
				logger.info("Backup " + getProperty("DB_NAME") + " done");
			}		
			
		}

		
		
		@Override
		public List<MagicCardStock> listStocks(MagicCard mc, MagicCollection col) throws SQLException {
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
							state.setAltered(rs.getBoolean("altered"));
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
		public List<MagicCardStock> listStocks() throws SQLException {
			try(PreparedStatement pst=con.prepareStatement("select * from stocks"); ResultSet rs = pst.executeQuery();)
			{
				List<MagicCardStock> colls = new ArrayList<>();
				while(rs.next())
				{
					MagicCardStock state = new MagicCardStock();
					
						state.setComment(rs.getString("comments"));
						state.setIdstock(rs.getInt("idstock"));
						state.setMagicCard(readObject(MagicCard.class, rs.getBinaryStream("mcard")));
						state.setMagicCollection(new MagicCollection(rs.getString("collection")));
						state.setCondition( EnumCondition.valueOf(rs.getString("conditions")) );
						state.setFoil(rs.getBoolean("foil"));
						state.setSigned(rs.getBoolean("signedcard"));
						state.setLanguage(rs.getString("langage"));
						state.setQte(rs.getInt("qte"));
						state.setPrice(rs.getDouble("price"));
						colls.add(state);
				}
				return colls;
			}
			
		}



		@Override
		public void saveOrUpdateStock(MagicCardStock state) throws SQLException {
			if(state.getIdstock()<0)
			{
				logger.debug("save "  + state);
				try(PreparedStatement pst=con.prepareStatement("insert into stocks  ( mcard,conditions,foil,signedcard,langage,qte,comments,idmc,collection,altered,price) values (?,?,?,?,?,?,?,?,?,?,?)"))
				{
					pst.setBinaryStream(1, convertObject(state.getMagicCard()));
					pst.setString(2, state.getCondition().toString());
					pst.setBoolean(3,state.isFoil());
					pst.setBoolean(4, state.isSigned());
					pst.setString(5, state.getLanguage());
					pst.setInt(6, state.getQte());
					pst.setString(7, state.getComment());
					pst.setString(8, IDGenerator.generate(state.getMagicCard()));
					pst.setString(9, state.getMagicCollection().getName());
					pst.setBoolean(10, state.isAltered());
					pst.setDouble(11, state.getPrice());
					state.setIdstock(pst.executeUpdate());	
				}
			}
			else
			{
				logger.debug("update "  + state);
				try (PreparedStatement pst=con.prepareStatement("update stocks set comments=?, conditions=?, foil=?,signedcard=?,langage=?, qte=? ,altered=? , price=?where idstock=?"))
				{
					pst.setString(1,state.getComment());
					pst.setString(2, state.getCondition().toString());
					pst.setBoolean(3,state.isFoil());
					pst.setBoolean(4, state.isSigned());
					pst.setString(5, state.getLanguage());
					pst.setInt(6, state.getQte());
					pst.setBoolean(7, state.isAltered());
					pst.setInt(9, state.getIdstock());
					pst.setDouble(8,state.getPrice());
					pst.executeUpdate();
				}
				
			}
		}
		
		@Override
		public List<MagicCardAlert> listAlerts()  {
		
			try(PreparedStatement pst=con.prepareStatement("select * from alerts");	ResultSet rs = pst.executeQuery();)
			{
				
				if(!list.isEmpty())
					return list;
				
			while(rs.next())
			{
			
					MagicCardAlert alert = new MagicCardAlert();
								   alert.setCard(readObject(MagicCard.class, rs.getBinaryStream("mcard")));
								   alert.setId(rs.getString("id"));
								   alert.setPrice(rs.getDouble("amount"));
								   
								   list.add(alert);
			}
			return list;
			
			}catch(Exception e)
			{
				logger.error(e);
				return new ArrayList<>();
			}
		}
		
		@Override
		public void updateAlert(MagicCardAlert alert) throws SQLException {
			try(PreparedStatement pst=con.prepareStatement("update alerts set amount=? where id=?"))
			{
				pst.setDouble(1, alert.getPrice());
				pst.setString(2, IDGenerator.generate(alert.getCard()));
				pst.executeUpdate();
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
			}catch(Exception e)
			{
				return false;
			}
			
		}

		@Override
		public void saveAlert(MagicCardAlert alert) throws SQLException {
			logger.debug("save "  + alert);
			try(PreparedStatement pst=con.prepareStatement("insert into alerts  ( id,mcard,amount) values (?,?,?)"))
			{
				pst.setString(1, IDGenerator.generate(alert.getCard()));
				pst.setBinaryStream(2,convertObject(alert.getCard()));
				pst.setDouble(3, alert.getPrice());
				pst.executeUpdate();
				list.add(alert);
			}
		}

		@Override
		public void deleteAlert(MagicCardAlert alert) throws SQLException {
			logger.debug("delete "  + alert);
			try(PreparedStatement pst=con.prepareStatement("delete from alerts where id=?"))
			{
			pst.setString(1, IDGenerator.generate(alert.getCard()));
			pst.executeUpdate();
			list.remove(alert);
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
				try(PreparedStatement pst=con.prepareStatement("update news set name=?, categorie=?, url=?, typeNews=? where id=?"))
				{
					pst.setString(1,n.getName());
					pst.setString(2, n.getCategorie());
					pst.setString(3,n.getUrl());
					pst.setString(4, n.getType().toString());
					pst.setInt(5, n.getId());
					pst.executeUpdate();
				}
			}
			
		}



}

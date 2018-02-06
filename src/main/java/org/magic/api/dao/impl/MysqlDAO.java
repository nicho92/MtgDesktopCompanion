package org.magic.api.dao.impl;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicDAO;
import org.magic.services.MTGControler;
import org.magic.tools.IDGenerator;

public class MysqlDAO extends AbstractMagicDAO{

    Connection con;
    private String defaultStore="BLOB";
    
	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}
	
   public MysqlDAO() throws ClassNotFoundException, SQLException {
	    super();	
		if(!new File(confdir, getName()+".conf").exists()){
			 props.put("DRIVER", "com.mysql.jdbc.Driver");
			 props.put("SERVERNAME","localhost");
			 props.put("SERVERPORT", "3306");
			 props.put("DB_NAME", "mtgdesktopclient");
			 props.put("LOGIN", "login");
			 props.put("PASS", "");
			 props.put("CARD_STORE", "BLOB"); 
			 props.put("PARAMS", "?autoDeserialize=true&autoReconnect=true");
			 props.put("MYSQL_DUMP_PATH", "C:\\Program Files (x86)\\Mysql\\bin");
		save();
		}
		
		
	}
	
	public void init() throws SQLException, ClassNotFoundException {
		 logger.info("init " + getName());
		 Class.forName(props.getProperty("DRIVER"));
		 String url = "jdbc:mysql://"+getProperty("SERVERNAME")+":"+getProperty("SERVERPORT");
		 con=DriverManager.getConnection(url+"/"+getProperty("DB_NAME")+props.getProperty("PARAMS"),props.getProperty("LOGIN"),props.getProperty("PASS"));
		 createDB();
		 logger.info("init " + getName() +" done");
		 
	}

	 public boolean createDB()
	 {
		 try (Statement stat=con.createStatement())
		 {
		 	logger.debug("Create table Cards");
		 	stat.executeUpdate("create table cards (ID varchar(250),name varchar(250), mcard "+getProperty("CARD_STORE",defaultStore)+", edition varchar(20), cardprovider varchar(50),collection varchar(250))");
		 	logger.debug("Create table Shop");
		 	stat.executeUpdate("create table shop (id varchar(250), statut varchar(250))");
		 	logger.debug("Create table collections");
		 	stat.executeUpdate("CREATE TABLE collections ( name VARCHAR(250))");
		 	logger.debug("Create table stocks");
		 	stat.executeUpdate("create table stocks (idstock integer PRIMARY KEY AUTO_INCREMENT, idmc varchar(250), mcard "+getProperty("CARD_STORE",defaultStore)+", collection varchar(250),comments varchar(250), conditions varchar(50),foil boolean, signedcard boolean, langage varchar(50), qte integer,altered boolean,price double)");
			logger.debug("Create table Alerts");
			stat.executeUpdate("create table alerts (id varchar(250), mcard "+getProperty("CARD_STORE",defaultStore)+", amount DECIMAL)");
		 	logger.debug("Create table Decks");
		 	stat.executeUpdate("CREATE TABLE decks (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100), `file` "+getProperty("CARD_STORE",defaultStore)+", categorie VARCHAR(100))");
		 	
		 	
		 	logger.debug("populate collections");
		 	stat.executeUpdate("insert into collections values ('Library')");
		 	stat.executeUpdate("insert into collections values ('Needed')");
		 	stat.executeUpdate("insert into collections values ('For sell')");
		 	stat.executeUpdate("insert into collections values ('Favorites')");
			
		 	
		 	return true;
		 }
		 catch(SQLException e)
		 {
			 logger.error(e);
			 return false;
		 }
		 
	 }
	
	
	@Override
	public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException {
		logger.debug("saving " + mc +" in " + collection);
		
		 try(PreparedStatement pst = con.prepareStatement("insert into cards values (?,?,?,?,?,?)"))
		 {	 pst.setString(1, IDGenerator.generate(mc)); 
			 pst.setString(2, mc.getName());
			 pst.setObject(3, mc);
			 pst.setString(4, mc.getEditions().get(0).getId());
			 pst.setString(5, MTGControler.getInstance().getEnabledProviders().toString());
			 pst.setString(6, collection.getName());
			 pst.executeUpdate();
		 }
	}

	@Override
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
		 logger.debug("delete " + mc + " from " + collection);
		 try(PreparedStatement pst = con.prepareStatement("delete from cards where id=? and edition=? and collection=?"))
		 {
			 pst.setString(1, IDGenerator.generate(mc));
			 pst.setString(2, mc.getEditions().get(0).getId());
			 pst.setString(3, collection.getName());
			 pst.executeUpdate();
		 }
	}

	@Override
	public List<MagicCard> listCards() throws SQLException {
		logger.debug("list all cards");
		
		String sql ="select mcard from cards";
		
		try(PreparedStatement pst=con.prepareStatement(sql);ResultSet rs = pst.executeQuery();)
		{
			List<MagicCard> listCards = new ArrayList<>();
			while(rs.next())
			{
				listCards.add((MagicCard) rs.getObject("mcard"));
			}
			return listCards;
		}
	}

	@Override
	public Map<String, Integer> getCardsCountGlobal(MagicCollection c) throws SQLException {
		String sql = "select edition, count(ID) from cards where collection=? group by edition";
		try(PreparedStatement pst=con.prepareStatement(sql);)
		{
			pst.setString(1, c.getName());
			try(ResultSet rs = pst.executeQuery())
			{
				Map<String,Integer> map= new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
				while(rs.next())
					map.put(rs.getString(1), rs.getInt(2));
				return map;
			}
		}
	}

	@Override
	public int getCardsCount(MagicCollection cols,MagicEdition me) throws SQLException {
		
		
		String sql = "select count(ID) from cards ";
		
		if(cols!=null)
			sql+=" where collection = '" + cols.getName()+"'";
		
		if(me!=null)
			sql+=" and LOWER('edition') = '" + me.getId().toLowerCase()+"'";
		
		logger.debug(sql);
		
		try(Statement st = con.createStatement();
		ResultSet rs = st.executeQuery(sql);)
		{
			rs.next();
			return rs.getInt(1);
		}
	}

	@Override
	public List<MagicCard> getCardsFromCollection(MagicCollection collection) throws SQLException {
		return getCardsFromCollection(collection,null);
	}

	@Override
	public List<MagicCard> getCardsFromCollection(MagicCollection collection, MagicEdition me) throws SQLException {
		logger.debug("getCardsFromCollection " + collection + " " + me);
		
		String sql ="select * from cards where collection= ?";
		
		if(me!=null)
			 sql="select * from cards where collection= ? and edition = ?";
		
		try (PreparedStatement pst=con.prepareStatement(sql))	
		{	
			pst.setString(1, collection.getName());
			if(me!=null)
				pst.setString(2, me.getId());
			
			try(ResultSet rs = pst.executeQuery())
			{	
				List<MagicCard> ret = new ArrayList<>();
				while(rs.next())
				{
					ret.add((MagicCard) rs.getObject("mcard"));
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
				List<String> retour = new ArrayList<>();
				while(rs.next())
				{
					retour.add(rs.getString("edition"));
				}
				return retour;
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
		 
		try(PreparedStatement pst2 = con.prepareStatement("delete from cards where collection = ?"))
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
		String sql = "SELECT Round(Sum(data_length + index_length), 1) FROM   information_schema.tables WHERE  table_schema = 'mtgdesktopclient'";
		try (
			PreparedStatement pst = con.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();)
			{
			rs.first();
			return (long)rs.getDouble(1);
			} catch (SQLException e) {
				logger.error(e);
				return 0;
			}	
	
	}

	@Override
	public String getName() {
		return "MySQL";
	}


	@Override
	public List<MagicCollection> getCollectionFromCards(MagicCard mc)throws SQLException{
		
		if(mc.getEditions().isEmpty())
			throw new SQLException("No edition defined");
		
		try(PreparedStatement pst = con.prepareStatement("SELECT collection FROM cards WHERE id=? and edition=?"))
		{
			String id = IDGenerator.generate(mc);
			pst.setString(1, id);
			pst.setString(2, mc.getEditions().get(0).getId());
			logger.trace("SELECT collection FROM cards WHERE id="+id+" and edition="+mc.getEditions().get(0).getId());
		 
			try(ResultSet rs = pst.executeQuery())
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
	public List<MagicCardStock> getStocks(MagicCard mc, MagicCollection col) throws SQLException {
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
	
	public List<MagicCardStock> getStocks() throws SQLException {
		try(
				PreparedStatement pst=con.prepareStatement("select * from stocks");
				ResultSet rs = pst.executeQuery();
			)
		{
			List<MagicCardStock> colls = new ArrayList<>();
			while(rs.next())
			{
				MagicCardStock state = new MagicCardStock();
				
					state.setComment(rs.getString("comments"));
					state.setIdstock(rs.getInt("idstock"));
					state.setMagicCard((MagicCard)rs.getObject("mcard"));
					state.setMagicCollection(new MagicCollection(rs.getString("collection")));
					try{
						state.setCondition(EnumCondition.valueOf(rs.getString("conditions")));
					}catch(Exception e)
					{
						state.setCondition(null);
					}
					state.setFoil(rs.getBoolean("foil"));
					state.setSigned(rs.getBoolean("signedcard"));
					state.setLanguage(rs.getString("langage"));
					state.setQte(rs.getInt("qte"));
					state.setAltered(rs.getBoolean("altered"));
					state.setPrice(rs.getDouble("price"));
					colls.add(state);
			}
			logger.debug("load " + colls.size() +" item(s) from stock");
			return colls;
		}
	}
	
	private int getGeneratedKey(PreparedStatement pst)
	{
		
		try (ResultSet rs = pst.getGeneratedKeys())
		{
			rs.next();
			return rs.getInt(1);
		} catch (SQLException e) {
			logger.error("couldn't retrieve id",e);
		}
		return -1;
		
	}
	
	

	@Override
	public void saveOrUpdateStock(MagicCardStock state) throws SQLException {
		
		if(state.getIdstock()<0)
		{
			
			logger.debug("save "  + state);
			try(PreparedStatement pst=con.prepareStatement("insert into stocks  ( conditions,foil,signedcard,langage,qte,comments,idmc,collection,mcard,altered,price) values (?,?,?,?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS))
			{
				pst.setString(1, String.valueOf(state.getCondition()));
				pst.setBoolean(2,state.isFoil());
				pst.setBoolean(3, state.isSigned());
				pst.setString(4, state.getLanguage());
				pst.setInt(5, state.getQte());
				pst.setString(6, state.getComment());
				pst.setString(7, IDGenerator.generate(state.getMagicCard()));
				pst.setString(8, String.valueOf(state.getMagicCollection()));
				pst.setObject(9, state.getMagicCard());
				pst.setBoolean(10, state.isAltered());
				pst.setDouble(11, state.getPrice());
				pst.executeUpdate();
				state.setIdstock( getGeneratedKey(pst));
			}
			catch(Exception e)
			{
				logger.error(e);
			}
		}
		else
		{
			logger.debug("update "  + state);
			try(PreparedStatement pst=con.prepareStatement("update stocks set comments=?, conditions=?, foil=?,signedcard=?,langage=?, qte=? ,altered=?,price=?,idmc=? where idstock=?"))
			{
				pst.setString(1,state.getComment());
				pst.setString(2, state.getCondition().toString());
				pst.setBoolean(3,state.isFoil());
				pst.setBoolean(4, state.isSigned());
				pst.setString(5, state.getLanguage());
				pst.setInt(6, state.getQte());
				pst.setBoolean(7, state.isAltered());
				pst.setDouble(8, state.getPrice());
				pst.setString(9, IDGenerator.generate(state.getMagicCard()));
				pst.setInt(10, state.getIdstock());
				pst.executeUpdate();
			}
			catch(Exception e)
			{
				logger.error(e);
			}
		}
	}
	
	@Override
	public void backup(File f) throws Exception {
		
		
		if(props.getProperty("MYSQL_DUMP_PATH").length()<=0)
		{
			throw new NullPointerException("Please fill MYSQL_DUMP_PATH var");
		}
		
		String dumpCommand = props.getProperty("MYSQL_DUMP_PATH")+"/mysqldump " + props.getProperty("DB_NAME") + " -h " + props.getProperty("SERVERNAME") + " -u " + props.getProperty("LOGIN") +" -p" + props.getProperty("PASS")+" --port " + props.getProperty("SERVERPORT");
		Runtime rt = Runtime.getRuntime();
		logger.info("begin Backup " + props.getProperty("DB_NAME"));
		Process child = rt.exec(dumpCommand);
		try(PrintStream ps=new PrintStream(f))
		{ 
			InputStream in = child.getInputStream();
			int ch;
			while ((ch = in.read()) != -1) 
			{
				ps.write(ch);
			}
			logger.info("Backup " + props.getProperty("DB_NAME") + " done");
		}
		
		
		
	}


	@Override
	public List<MagicCardAlert> getAlerts() {
	
		
		try(PreparedStatement pst=con.prepareStatement("select * from alerts"))
		{
			List<MagicCardAlert> list = new ArrayList<>();
				try(ResultSet rs = pst.executeQuery())
				{	
					while(rs.next())
					{
						MagicCardAlert alert = new MagicCardAlert();
									   alert.setCard((MagicCard)rs.getObject("mcard"));
									   alert.setId(rs.getString("id"));
									   alert.setPrice(rs.getDouble("amount"));
									   
									   list.add(alert);
					}
					return list;
				}
		}catch(Exception e)
		{
			logger.error(e);
			return new ArrayList<>();
		}
	}

	@Override
	public boolean hasAlert(MagicCard mc) {
		try (PreparedStatement pst=con.prepareStatement("select * from alerts where id=?"))
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
		try(PreparedStatement pst=con.prepareStatement("insert into alerts  ( id,mcard,amount) values (?,?,?)"))
		{
			pst.setString(1, IDGenerator.generate(alert.getCard()));
			pst.setObject(2,alert.getCard());
			pst.setDouble(3, alert.getPrice());
			pst.executeUpdate();
		}
	}
	
	@Override
	public void updateAlert(MagicCardAlert alert) throws SQLException {
		try (PreparedStatement pst=con.prepareStatement("update alerts set amount=? where id=?"))
		{		pst.setDouble(1, alert.getPrice());
				pst.setString(2, alert.getId());
				pst.executeUpdate();
		}
		
	}

	@Override
	public void deleteAlert(MagicCardAlert alert) throws SQLException {
		logger.debug("delete alert "  + alert);
		try(PreparedStatement pst=con.prepareStatement("delete from alerts where id=?"))
		{
			pst.setString(1, IDGenerator.generate(alert.getCard()));
			pst.executeUpdate();
		}
		
	}
	
}
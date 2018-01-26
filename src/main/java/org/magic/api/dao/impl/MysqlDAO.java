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
	
    @Override
    public String toString() {
    	return getName();
    }
    
	public MysqlDAO() throws ClassNotFoundException, SQLException {
	    super();	
		if(!new File(confdir, getName()+".conf").exists()){
			 props.put("DRIVER", "com.mysql.jdbc.Driver");
			 props.put("SERVERNAME","localhost");
			 props.put("SERVERPORT", "3306");
			 props.put("DB_NAME", "mtgdesktopclient");
			 props.put("LOGIN", "login");
			 props.put("PASSWORD", "password");
			 props.put("CARD_STORE", "BLOB"); //TODO : BLOB, JSON,TEXT
			 props.put("PARAMS", "?autoDeserialize=true&autoReconnect=true");
			 props.put("MYSQL_DUMP_PATH", "C:\\Program Files (x86)\\Mysql\\bin");
		save();
		}
		
		
	}
	
	public void init() throws SQLException, ClassNotFoundException {
		 logger.info("init " + getName());
		 Class.forName(props.getProperty("DRIVER"));
		 String url = "jdbc:mysql://"+props.getProperty("SERVERNAME")+":"+props.getProperty("SERVERPORT");
		 con=DriverManager.getConnection(url+"/"+props.getProperty("DB_NAME")+props.getProperty("PARAMS"),props.getProperty("LOGIN"),props.getProperty("PASSWORD"));
		 createDB();
		 logger.info("init " + getName() +" done");
		 
	}

	 public boolean createDB()
	 {
		 try{
		 	logger.debug("Create table Cards");
		 	con.createStatement().executeUpdate("create table cards (ID varchar(250),name varchar(250), mcard "+getProperty("CARD_STORE",defaultStore)+", edition varchar(20), cardprovider varchar(50),collection varchar(250))");
		 	logger.debug("Create table Shop");
		 	con.createStatement().executeUpdate("create table shop (id varchar(250), statut varchar(250))");
		 	logger.debug("Create table collections");
		 	con.createStatement().executeUpdate("CREATE TABLE collections ( name VARCHAR(250))");
		 	logger.debug("Create table stocks");
		 	con.createStatement().executeUpdate("create table stocks (idstock integer PRIMARY KEY AUTO_INCREMENT, idmc varchar(250), mcard "+getProperty("CARD_STORE",defaultStore)+", collection varchar(250),comments varchar(250), conditions varchar(50),foil boolean, signedcard boolean, langage varchar(50), qte integer,altered boolean,price double)");
			logger.debug("Create table Alerts");
		 	con.createStatement().executeUpdate("create table alerts (id varchar(250), mcard "+getProperty("CARD_STORE",defaultStore)+", amount DECIMAL)");
		 	logger.debug("Create table Decks");
		 	con.createStatement().executeUpdate("CREATE TABLE decks (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100), `file` "+getProperty("CARD_STORE",defaultStore)+", categorie VARCHAR(100))");
		 	
		 	
		 	logger.debug("populate collections");
			con.createStatement().executeUpdate("insert into collections values ('Library')");
		 	con.createStatement().executeUpdate("insert into collections values ('Needed')");
		 	con.createStatement().executeUpdate("insert into collections values ('For sell')");
		 	con.createStatement().executeUpdate("insert into collections values ('Favorites')");
			
		 	
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
		logger.debug("saving " + mc +" in " + collection);
		
		PreparedStatement pst = con.prepareStatement("insert into cards values (?,?,?,?,?,?)");
		 pst.setString(1, IDGenerator.generate(mc)); 
		 pst.setString(2, mc.getName());
		 pst.setObject(3, mc);
		 pst.setString(4, mc.getEditions().get(0).getId());
		 pst.setString(5, MTGControler.getInstance().getEnabledProviders().toString());
		 pst.setString(6, collection.getName());
		 
		 pst.executeUpdate();
	}

	@Override
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
		logger.debug("remove " + mc + " from " + collection);
		PreparedStatement pst = con.prepareStatement("delete from cards where id=? and edition=? and collection=?");
		 pst.setString(1, IDGenerator.generate(mc));
		 pst.setString(2, mc.getEditions().get(0).getId());
		 pst.setString(3, collection.getName());
		 pst.executeUpdate();

	}

	@Override
	public List<MagicCard> listCards() throws SQLException {
		logger.debug("list all cards");
		
		String sql ="select mcard from cards";
		
		PreparedStatement pst=con.prepareStatement(sql);	
		
		ResultSet rs = pst.executeQuery();
		List<MagicCard> list = new ArrayList<MagicCard>();
		while(rs.next())
		{
			list.add((MagicCard) rs.getObject("mcard"));
		}
	
	return list;
	}

	@Override
	public Map<String, Integer> getCardsCountGlobal(MagicCollection c) throws SQLException {
		String sql = "select edition, count(ID) from cards where collection=? group by edition";
		PreparedStatement pst=con.prepareStatement(sql);	
		pst.setString(1, c.getName());
		ResultSet rs = pst.executeQuery();
		
		Map<String,Integer> map= new TreeMap<String,Integer>(String.CASE_INSENSITIVE_ORDER);
		
		while(rs.next())
		{
			map.put(rs.getString(1), rs.getInt(2));
		}
		
		return map;
	}

	@Override
	public int getCardsCount(MagicCollection cols,MagicEdition me) throws SQLException {
		
		
		String sql = "select count(ID) from cards ";
		
		if(cols!=null)
			sql+=" where collection = '" + cols.getName()+"'";
		
		if(me!=null)
			sql+=" and LOWER('edition') = '" + me.getId().toLowerCase()+"'";
		
		logger.debug(sql);
		
		Statement st = con.createStatement();
		
		ResultSet rs = st.executeQuery(sql);
		rs.next();
		return rs.getInt(1);
	}

	@Override
	public List<MagicCard> getCardsFromCollection(MagicCollection collection) throws SQLException {
		return getCardsFromCollection(collection,null);
	}

	@Override
	public List<MagicCard> getCardsFromCollection(MagicCollection collection, MagicEdition me) throws SQLException {
		
		logger.debug("getCardsFromCollection " + collection + " " + me);
		
		String sql ="select * from cards where collection= ? and edition = ?";
		
		if(me==null)
			sql ="select * from cards where collection= ?";
		
		PreparedStatement pst=con.prepareStatement(sql);	
		pst.setString(1, collection.getName());
		
		if(me!=null)
			pst.setString(2, me.getId());
		
		ResultSet rs = pst.executeQuery();
		List<MagicCard> list = new ArrayList<MagicCard>();
		while(rs.next())
		{
			list.add((MagicCard) rs.getObject("mcard"));
		}
	return list;
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
		
		if(c.getName().equals(MTGControler.getInstance().get("default-library")))
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
		logger.debug("remove " + me + " from " + col);
		PreparedStatement pst = con.prepareStatement("delete from cards where edition=? and collection=?");
		 pst.setString(1, me.getId());
		 pst.setString(2, col.getName());
		 pst.executeUpdate();

	}

	@Override
	public String getDBLocation() {
		return props.getProperty("URL")+"/"+props.getProperty("DB_NAME");
	}

	@Override
	public long getDBSize() {
		String sql = "SELECT Round(Sum(data_length + index_length), 1) FROM   information_schema.tables WHERE  table_schema = 'mtgdesktopclient'";
		PreparedStatement pst;
		try {
			pst = con.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			return (long)rs.getDouble(1);
		} catch (SQLException e) {
			return 0;
		}	
	
	}

	@Override
	public String getName() {
		return "MySQL";
	}


	@Override
	public List<MagicCollection> getCollectionFromCards(MagicCard mc)throws SQLException{
		
		if(mc.getEditions().size()==0)
			throw new SQLException("No edition defined");
		
		PreparedStatement pst = con.prepareStatement("SELECT collection FROM cards WHERE id=? and edition=?");
		String id = IDGenerator.generate(mc);
		 pst.setString(1, id);
		 pst.setString(2, mc.getEditions().get(0).getId());
		 
		 logger.trace("SELECT collection FROM cards WHERE id="+id+" and edition="+mc.getEditions().get(0).getId());
		 
		 ResultSet rs = pst.executeQuery();
		 List<MagicCollection> cols = new ArrayList<MagicCollection>();
		 while(rs.next())
		 {
			 MagicCollection col = new MagicCollection();
			 col.setName(rs.getString("collection"));
			 cols.add(col);
		 }
		 
		 return cols;
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
		Statement pst = con.createStatement();
		pst.executeUpdate(sql);
	}

	
	@Override
	public List<MagicCardStock> getStocks(MagicCard mc, MagicCollection col) throws SQLException {
		PreparedStatement pst=con.prepareStatement("select * from stocks where idmc=? and collection=?");	
		pst.setString(1, IDGenerator.generate(mc));
		pst.setString(2, col.getName());
		ResultSet rs = pst.executeQuery();
		List<MagicCardStock> colls = new ArrayList<MagicCardStock>();
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
	
	public List<MagicCardStock> getStocks() throws SQLException {
		PreparedStatement pst=con.prepareStatement("select * from stocks");	
		ResultSet rs = pst.executeQuery();
		List<MagicCardStock> colls = new ArrayList<MagicCardStock>();
		
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
	

	@Override
	public void saveOrUpdateStock(MagicCardStock state) throws SQLException {
		PreparedStatement pst;
		if(state.getIdstock()<0)
		{
			
			logger.debug("save "  + state);
			pst=con.prepareStatement("insert into stocks  ( conditions,foil,signedcard,langage,qte,comments,idmc,collection,mcard,altered,price) values (?,?,?,?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, String.valueOf(state.getCondition()));
			pst.setBoolean(2,state.isFoil());
			pst.setBoolean(3, state.isSigned());
			pst.setString(4, state.getLanguage());
			pst.setInt(5, state.getQte());
			pst.setString(6, state.getComment());
			pst.setString(7, state.getMagicCard().getId());
			pst.setString(8, String.valueOf(state.getMagicCollection()));
			pst.setObject(9, state.getMagicCard());
			pst.setBoolean(10, state.isAltered());
			pst.setDouble(11, state.getPrice());
			pst.executeUpdate();
			ResultSet rs = pst.getGeneratedKeys();
			rs.next();
			state.setIdstock( rs.getInt(1));
			
		}
		else
		{
			logger.debug("update "  + state);
			pst=con.prepareStatement("update stocks set comments=?, conditions=?, foil=?,signedcard=?,langage=?, qte=? ,altered=?,price=?,idmc=? where idstock=?");
			pst.setString(1,state.getComment());
			pst.setString(2, state.getCondition().toString());
			pst.setBoolean(3,state.isFoil());
			pst.setBoolean(4, state.isSigned());
			pst.setString(5, state.getLanguage());
			pst.setInt(6, state.getQte());
			pst.setBoolean(7, state.isAltered());
			pst.setDouble(8, state.getPrice());
			pst.setString(9, state.getMagicCard().getId());
			pst.setInt(10, state.getIdstock());
			
			pst.executeUpdate();
		}
	}
	
	@Override
	public void backup(File f) throws Exception {
		
		
		if(props.getProperty("MYSQL_DUMP_PATH").length()<=0)
		{
			throw new Exception("Please fill MYSQL_DUMP_PATH var");
		}
		
		String dumpCommand = props.getProperty("MYSQL_DUMP_PATH")+"/mysqldump " + props.getProperty("DB_NAME") + " -h " + props.getProperty("SERVERNAME") + " -u " + props.getProperty("LOGIN") +" -p" + props.getProperty("PASSWORD")+" --port " + props.getProperty("SERVERPORT");
		Runtime rt = Runtime.getRuntime();
		PrintStream ps;
		logger.info("begin Backup " + props.getProperty("DB_NAME"));
		
		Process child = rt.exec(dumpCommand);
		ps=new PrintStream(f);
		InputStream in = child.getInputStream();
		int ch;
		while ((ch = in.read()) != -1) 
		{
			ps.write(ch);
		}
		ps.close();
		logger.info("Backup " + props.getProperty("DB_NAME") + " done");
		
		
	}

	List<MagicCardAlert> list;
	@Override
	public List<MagicCardAlert> getAlerts() {
		
		if(list!=null)
			return list;
		try
		{
				PreparedStatement pst=con.prepareStatement("select * from alerts");	
				list = new ArrayList<MagicCardAlert>();
				ResultSet rs = pst.executeQuery();
				while(rs.next())
				{
					MagicCardAlert alert = new MagicCardAlert();
								   alert.setCard((MagicCard)rs.getObject("mcard"));
								   alert.setId(rs.getString("id"));
								   alert.setPrice(rs.getDouble("amount"));
								   
								   list.add(alert);
				}
				return list;
		}catch(Exception e)
		{
			return null;
		}
	}

	@Override
	public boolean hasAlert(MagicCard mc) {
		try
		{
				PreparedStatement pst=con.prepareStatement("select * from alerts where id=?");
				pst.setString(1, IDGenerator.generate(mc));
				ResultSet rs = pst.executeQuery();
				return rs.next();
		}catch(Exception e)
		{
			return false;
		}
		
	}
	
	@Override
	public void saveAlert(MagicCardAlert alert) throws SQLException {
		PreparedStatement pst;
				pst=con.prepareStatement("insert into alerts  ( id,mcard,amount) values (?,?,?)");
				pst.setString(1, IDGenerator.generate(alert.getCard()));
				pst.setObject(2,alert.getCard());
				pst.setDouble(3, alert.getPrice());
				pst.executeUpdate();
		list.add(alert);
		
	}
	
	@Override
	public void updateAlert(MagicCardAlert alert) throws SQLException {
		PreparedStatement pst;
				pst=con.prepareStatement("update alerts set amount=? where id=?");
				pst.setDouble(1, alert.getPrice());
				pst.setString(2, alert.getId());
				pst.executeUpdate();
		list.remove(alert);
		list.add(alert);
		
	}

	@Override
	public void deleteAlert(MagicCardAlert alert) throws SQLException {
		logger.debug("delete alert "  + alert);
		PreparedStatement pst;
		pst=con.prepareStatement("delete from alerts where id=?");
		pst.setString(1, IDGenerator.generate(alert.getCard()));
		pst.executeUpdate();
		list.remove(alert);
		
	}
	
}
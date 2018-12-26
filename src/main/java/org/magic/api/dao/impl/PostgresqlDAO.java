package org.magic.api.dao.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicNews;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.OrderEntry.TYPE_ITEM;
import org.magic.api.beans.OrderEntry.TYPE_TRANSACTION;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGNewsProvider;
import org.magic.api.interfaces.abstracts.AbstractMagicDAO;
import org.magic.services.MTGControler;
import org.magic.tools.IDGenerator;
import org.postgresql.util.PGobject;

public class PostgresqlDAO extends AbstractMagicDAO {

	private Connection con;
	private String mcardField = "mcard";

	private enum KEYS {
		DRIVER, SERVERNAME, SERVERPORT, DB_NAME, LOGIN, PASS, URL_PGDUMP
	}

	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public void init() throws ClassNotFoundException, SQLException {
		logger.info("init " + getName());
		Class.forName(getString(KEYS.DRIVER.name()));
		String url = "jdbc:postgresql://" + getString(KEYS.SERVERNAME.name()) + ":" + getString(KEYS.SERVERPORT.name());
		con = DriverManager.getConnection(url + "/" + getString(KEYS.DB_NAME.name()), getString(KEYS.LOGIN.name()),
				getString(KEYS.PASS.name()));
		createDB();
	}

	public boolean createDB() {
		try (Statement stat = con.createStatement()) {
			logger.debug("Create table Orders");
			stat.executeUpdate("CREATE TABLE orders (id SERIAL PRIMARY KEY , idTransaction VARCHAR(250), description VARCHAR(250),edition VARCHAR(10),itemPrice DECIMAL(10,3),shippingPrice  DECIMAL(10,3), currency VARCHAR(4), transactionDate DATE,typeItem VARCHAR(50),typeTransaction VARCHAR(50),sources VARCHAR(250),seller VARCHAR(250))");
			logger.debug("Create table Cards");
			stat.executeUpdate("create table cards (ID varchar(250),name varchar(250), mcard json, edition varchar(20), cardprovider varchar(50),collection varchar(250), PRIMARY KEY(ID, edition,collection))");
			logger.debug("Create table collections");
			stat.executeUpdate("CREATE TABLE collections ( name VARCHAR(250)  PRIMARY KEY)");
			logger.debug("Create table stocks");
			stat.executeUpdate("create table stocks (idstock SERIAL PRIMARY KEY , idmc varchar(250), collection varchar(250),comments varchar(250), conditions varchar(50),foil boolean, signedcard boolean, langage varchar(50), qte integer,mcard json,altered boolean,price decimal)");
			logger.debug("Create table Alerts");
			stat.executeUpdate("create table alerts (id varchar(250), mcard json, amount decimal)");
			logger.debug("Create table News");
			stat.executeUpdate("CREATE TABLE news (id SERIAL PRIMARY KEY, name VARCHAR(100), url VARCHAR(256), categorie VARCHAR(100),typeNews VARCHAR(100))");

			logger.debug("populate collections");
			stat.executeUpdate("insert into collections values ('Library')");
			stat.executeUpdate("insert into collections values ('Needed')");
			stat.executeUpdate("insert into collections values ('For sell')");
			stat.executeUpdate("insert into collections values ('Favorites')");
			return true;
		} catch (SQLException e) {
			logger.debug(e);
			return false;
		}

	}

	

	private <T> T readObject(Class<T> class1, PGobject object) {
		try {
			return serialiser.fromJson(object.getValue(), class1);
		} catch (Exception e) {
			logger.error("error reading " + object,e);
			return null;
		}

	}

	private PGobject convertObject(Object c) {
		
		PGobject jsonObject = new PGobject();
		jsonObject.setType("json");
		try {
			jsonObject.setValue(serialiser.toJsonElement(c).toString());
		} catch (SQLException e) {
			logger.error("error convert " + c,e);
		}
		
		return jsonObject;
		
	}

	@Override
	public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException {
		logger.debug("saving " + mc + " in " + collection);
		try (PreparedStatement pst = con.prepareStatement("insert into cards values (?,?,?,?,?,?)");) {
			pst.setString(1, IDGenerator.generate(mc));
			pst.setString(2, mc.getName());
			pst.setObject(3, convertObject(mc));
			pst.setString(4, mc.getCurrentSet().getId());
			pst.setString(5, MTGControler.getInstance().getEnabled(MTGCardsProvider.class).toString());
			pst.setString(6, collection.getName());
			pst.executeUpdate();
		}
	}

	@Override
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
		logger.debug("delete " + mc + " from " + collection);
		try (PreparedStatement pst = con
				.prepareStatement("delete from cards where name=? and edition=? and collection=?")) {
			pst.setString(1, mc.getName());
			pst.setString(2, mc.getCurrentSet().getId());
			pst.setString(3, collection.getName());
			pst.executeUpdate();
		}
	}

	@Override
	public List<MagicCard> listCards() throws SQLException {
		logger.debug("list all cards");

		String sql = "select * from cards";

		try (PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery();) {
			List<MagicCard> ret = new ArrayList<>();
			while (rs.next()) {
				ret.add(readObject(MagicCard.class, (PGobject)rs.getObject(mcardField)));
			}
			return ret;

		}
	}

	@Override
	public Map<String, Integer> getCardsCountGlobal(MagicCollection c) throws SQLException {
		String sql = "select edition, count(name) from cards where collection=? group by edition";
		try (PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setString(1, c.getName());
			try (ResultSet rs = pst.executeQuery();) {
				Map<String, Integer> map = new HashMap<>();
				while (rs.next())
					map.put(rs.getString(1), rs.getInt(2));
				return map;
			}
		}
	}

	@Override
	public int getCardsCount(MagicCollection cols, MagicEdition me) throws SQLException {

		String sql = "select count(name) from cards ";

		if (cols != null)
			sql += " where collection = '" + cols.getName() + "'";

		if (me != null)
			sql += " and edition = '" + me.getId() + "'";

		logger.debug(sql);

		try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql);) {
			rs.next();
			return rs.getInt(1);
		}
	}

	@Override
	public List<MagicCard> listCardsFromCollection(MagicCollection collection) throws SQLException {
		return listCardsFromCollection(collection, null);
	}

	@Override
	public List<MagicCard> listCardsFromCollection(MagicCollection collection, MagicEdition me) throws SQLException {
		logger.debug("getCardsFromCollection " + collection + " " + me);
		String sql = "select * from cards where collection= ?";
		if (me != null)
			sql = "select * from cards where collection= ? and edition = ?";

		try (PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setString(1, collection.getName());

			if (me != null)
				pst.setString(2, me.getId());

			try (ResultSet rs = pst.executeQuery()) {
				List<MagicCard> ret = new ArrayList<>();
				while (rs.next()) {
					ret.add(readObject(MagicCard.class,(PGobject) rs.getObject(mcardField)));
				}

				return ret;

			}
		}

	}

	@Override
	public List<String> listEditionsIDFromCollection(MagicCollection collection) throws SQLException {
		String sql = "select distinct(edition) from cards where collection=?";

		try (PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setString(1, collection.getName());
			try (ResultSet rs = pst.executeQuery()) {
				List<String> ret = new ArrayList<>();
				while (rs.next()) {
					ret.add(rs.getString("edition"));
				}

				return ret;
			}
		}
	}

	@Override
	public MagicCollection getCollection(String name) throws SQLException {
		try (PreparedStatement pst = con.prepareStatement("select * from collections where name= ?")) {
			pst.setString(1, name);
			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
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

		try (PreparedStatement pst = con.prepareStatement("insert into collections values (?)")) {
			pst.setString(1, c.getName());
			pst.executeUpdate();
		}
	}

	@Override
	public void removeCollection(MagicCollection c) throws SQLException {

		if (c.getName().equals(MTGControler.getInstance().get("default-library")))
			throw new SQLException(c.getName() + " can not be deleted");

		try (PreparedStatement pst = con.prepareStatement("delete from collections where name = ?")) {
			pst.setString(1, c.getName());
			pst.executeUpdate();
		}
		try (PreparedStatement pst = con.prepareStatement("delete from cards where collection = ?")) {
			pst.setString(1, c.getName());
			pst.executeUpdate();
		}

	}

	@Override
	public List<MagicCollection> listCollections() throws SQLException {
		try (PreparedStatement pst = con.prepareStatement("select * from collections");
				ResultSet rs = pst.executeQuery()) {
			List<MagicCollection> colls = new ArrayList<>();
			while (rs.next()) {
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
		try (PreparedStatement pst = con.prepareStatement("delete from cards where edition=? and collection=?")) {
			pst.setString(1, me.getId());
			pst.setString(2, col.getName());
			pst.executeUpdate();

		}
	}

	@Override
	public String getDBLocation() {
		return getString(KEYS.SERVERNAME.name()) + "/" + getString(KEYS.DB_NAME.name());
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
	public List<MagicCollection> listCollectionFromCards(MagicCard mc) throws SQLException {

		if (mc.getEditions().isEmpty())
			throw new SQLException("No edition defined");

		try (PreparedStatement pst = con.prepareStatement("SELECT collection FROM cards WHERE name=? and edition=?")) {
			pst.setString(1, mc.getName());
			pst.setString(2, mc.getCurrentSet().getId());
			try (ResultSet rs = pst.executeQuery()) {
				List<MagicCollection> cols = new ArrayList<>();
				while (rs.next()) {
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

		if (getString(KEYS.URL_PGDUMP.name()).length() <= 0) {
			throw new NullPointerException("Please fill URL_PGDUMP var");
		}

		String dumpCommand = getString(KEYS.URL_PGDUMP.name()) + "/pg_dump" + " -d" + getString(KEYS.DB_NAME.name())
				+ " -h" + getString(KEYS.SERVERNAME.name()) + " -U" + getString("LOGIN") + " -p"
				+ getString("SERVERPORT");

		Runtime rt = Runtime.getRuntime();
		logger.info("begin Backup :" + dumpCommand);

		Process child = rt.exec(dumpCommand);
		try (PrintStream ps = new PrintStream(f)) {
			InputStream in = child.getInputStream();
			int ch;
			while ((ch = in.read()) != -1) {
				ps.write(ch);
			}
			logger.info("Backup " + getString(KEYS.DB_NAME.name()) + " done");
		}

	}

	@Override
	public List<MagicCardStock> listStocks(MagicCard mc, MagicCollection col,boolean editionStrict) throws SQLException {
		
		String sql = "SELECT * FROM  stocks WHERE mcard->>'name' = ? and collection = ?";
		
		if(editionStrict)
			sql="select * from stocks where idmc=? and collection=?";
		
		try (PreparedStatement pst = con.prepareStatement(sql)) {
			if(editionStrict)
				pst.setString(1, IDGenerator.generate(mc));
			else
				pst.setString(1, mc.getName());
			
			pst.setString(2, col.getName());
			try (ResultSet rs = pst.executeQuery()) {
				List<MagicCardStock> colls = new ArrayList<>();
				while (rs.next()) {
					MagicCardStock state = new MagicCardStock();

					state.setComment(rs.getString("comments"));
					state.setIdstock(rs.getInt("idstock"));
					state.setMagicCard(mc);
					state.setMagicCollection(col);
					state.setCondition(EnumCondition.valueOf(rs.getString("conditions")));
					state.setFoil(rs.getBoolean("foil"));
					state.setSigned(rs.getBoolean("signedcard"));
					state.setLanguage(rs.getString("langage"));
					state.setAltered(rs.getBoolean("altered"));
					state.setQte(rs.getInt("qte"));
					state.setPrice(rs.getDouble("price"));
					colls.add(state);
				}
				logger.debug("load " + colls.size() + " item from stock for " + mc);
				return colls;
			}

		}

	}

	@Override
	public List<MagicCardStock> listStocks() throws SQLException {
		try (PreparedStatement pst = con.prepareStatement("select * from stocks"); ResultSet rs = pst.executeQuery();) {
			List<MagicCardStock> colls = new ArrayList<>();
			while (rs.next()) {
				MagicCardStock state = new MagicCardStock();

				state.setComment(rs.getString("comments"));
				state.setIdstock(rs.getInt("idstock"));
				state.setMagicCard(readObject(MagicCard.class, (PGobject)rs.getObject(mcardField)));
				state.setMagicCollection(new MagicCollection(rs.getString("collection")));
				state.setCondition(EnumCondition.valueOf(rs.getString("conditions")));
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
		if (state.getIdstock() < 0) {
			logger.debug("insert " + state);
			try (PreparedStatement pst = con.prepareStatement(
					"insert into stocks  ( mcard,conditions,foil,signedcard,langage,qte,comments,idmc,collection,altered,price) values (?,?,?,?,?,?,?,?,?,?,?)")) {
				pst.setObject(1, convertObject(state.getMagicCard()));
				pst.setString(2, state.getCondition().toString());
				pst.setBoolean(3, state.isFoil());
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
		} else {
			logger.debug("update " + state);
			try (PreparedStatement pst = con.prepareStatement(
					"update stocks set comments=?, conditions=?, foil=?,signedcard=?,langage=?, qte=? ,altered=? , price=?where idstock=?")) {
				pst.setString(1, state.getComment());
				pst.setString(2, state.getCondition().toString());
				pst.setBoolean(3, state.isFoil());
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

	@Override
	public void initAlerts() {

		try (PreparedStatement pst = con.prepareStatement("select * from alerts"); ResultSet rs = pst.executeQuery();) {
			while (rs.next()) {

				MagicCardAlert alert = new MagicCardAlert();
				alert.setCard(readObject(MagicCard.class, (PGobject)rs.getObject(mcardField)));
				alert.setId(rs.getString("id"));
				alert.setPrice(rs.getDouble("amount"));

				listAlerts.add(alert);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public void saveAlert(MagicCardAlert alert) throws SQLException {
			logger.debug("save " + alert);
			try (PreparedStatement pst = con.prepareStatement("insert into alerts  ( id,mcard,amount) values (?,?,?)")) {
				pst.setString(1, IDGenerator.generate(alert.getCard()));
				pst.setObject(2, convertObject(alert.getCard()));
				pst.setDouble(3, alert.getPrice());
				pst.executeUpdate();
				listAlerts.add(alert);
			}
	}
	
	@Override
	public void updateAlert(MagicCardAlert alert) throws SQLException {
		logger.debug("update " + alert);
		try (PreparedStatement pst = con.prepareStatement("update alerts set amount=? where id=?")) {
			pst.setDouble(1, alert.getPrice());
			pst.setString(2, IDGenerator.generate(alert.getCard()));
			pst.executeUpdate();
		}
		
	}
	

	@Override
	public void deleteAlert(MagicCardAlert alert) throws SQLException {
		logger.debug("delete " + alert);
		try (PreparedStatement pst = con.prepareStatement("delete from alerts where id=?")) {
			pst.setString(1, IDGenerator.generate(alert.getCard()));
			pst.executeUpdate();
			listAlerts.remove(alert);
		}
	}

	@Override
	public void deleteStock(List<MagicCardStock> state) throws SQLException {
		logger.debug("remove " + state.size() + " items in stock");
		StringBuilder st = new StringBuilder();
		st.append("delete from stocks where idstock IN (");
		for (MagicCardStock sto : state) {
			st.append(sto.getIdstock()).append(",");
		}
		st.append(")");
		String sql = st.toString().replace(",)", ")");
		try (Statement pst = con.createStatement()) {
			pst.executeUpdate(sql);
		}

	}

	@Override
	public List<MagicNews> listNews() {
		try (PreparedStatement pst = con.prepareStatement("select * from news")) {
			List<MagicNews> news = new ArrayList<>();
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					MagicNews n = new MagicNews();
					n.setCategorie(rs.getString("categorie"));
					n.setName(rs.getString("name"));
					n.setUrl(rs.getString("url"));
					n.setProvider(MTGControler.getInstance().getPlugin(rs.getString("typeNews"),MTGNewsProvider.class));
					n.setId(rs.getInt("id"));
					news.add(n);
				}
				return news;
			}
		} catch (Exception e) {
			logger.error(e);
			return new ArrayList<>();
		}
	}

	@Override
	public void deleteNews(MagicNews n) throws SQLException {
		logger.debug("delete news " + n);
		try (PreparedStatement pst = con.prepareStatement("delete from news where id=?")) {
			pst.setInt(1, n.getId());
			pst.executeUpdate();
		}
	}

	@Override
	public void saveOrUpdateNews(MagicNews n) throws SQLException {
		if (n.getId() < 0) {

			logger.debug("save " + n);
			try (PreparedStatement pst = con
					.prepareStatement("insert into news  ( name,categorie,url,typeNews) values (?,?,?,?)")) {
				pst.setString(1, n.getName());
				pst.setString(2, n.getCategorie());
				pst.setString(3, n.getUrl());
				pst.setString(4, n.getProvider().getName());
				n.setId(pst.executeUpdate());
			}

		} else {
			logger.debug("update " + n);
			try (PreparedStatement pst = con
					.prepareStatement("update news set name=?, categorie=?, url=?, typeNews=? where id=?")) {
				pst.setString(1, n.getName());
				pst.setString(2, n.getCategorie());
				pst.setString(3, n.getUrl());
				pst.setString(4, n.getProvider().getName());
				pst.setInt(5, n.getId());
				pst.executeUpdate();
			}
		}

	}

	@Override
	public void initDefault() {
		setProperty(KEYS.DRIVER.name(), "org.postgresql.Driver");
		setProperty(KEYS.SERVERNAME.name(), "localhost");
		setProperty(KEYS.SERVERPORT.name(), "5432");
		setProperty(KEYS.DB_NAME.name(), "mtgdesktopcompanion");
		setProperty(KEYS.LOGIN.name(), "postgres");
		setProperty(KEYS.PASS.name(), "postgres");
		setProperty(KEYS.URL_PGDUMP.name(), "C:/Program Files (x86)/PostgreSQL/9.5/bin");

	}

	@Override
	public String getVersion() {
		
		Driver d = new org.postgresql.Driver();
		return d.getMajorVersion()+"."+d.getMinorVersion();
		
	}


	@Override
	public void saveOrUpdateOrderEntry(OrderEntry state) throws SQLException {

		if (state.getId() < 0) {
			logger.debug("save " + state);
			try (PreparedStatement pst = con.prepareStatement(
					"INSERT INTO orders (idTransaction, description, edition, itemPrice, shippingPrice, currency, transactionDate, typeItem, typeTransaction, sources, seller)"
				  + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS)) {
				
				pst.setString(1, state.getIdTransation());
				pst.setString(2, state.getDescription());
				
				if(state.getEdition()!=null)
					pst.setString(3, state.getEdition().getId());
				else
					pst.setString(3, null);
				
				pst.setDouble(4, state.getItemPrice());
				pst.setDouble(5,state.getShippingPrice());
				pst.setString(6,state.getCurrency().getCurrencyCode());
				pst.setDate(7, new Date(state.getTransationDate().getTime()));
				pst.setString(8,state.getType().name());
				pst.setString(9,state.getTypeTransaction().name());
				pst.setString(10, state.getSource());
				pst.setString(11, state.getSeller());
				state.setId(pst.executeUpdate());
				listOrders.add(state);
			} catch (Exception e) {
				logger.error("error insert " + state.getDescription() , e);
			}
		} else {
			logger.debug("update Order " + state);
			
			
			try (PreparedStatement pst = con.prepareStatement(
					"UPDATE orders SET "
					+ "idTransaction= ?, description=?, edition=?,itemPrice=?,shippingPrice=?,currency=?,transactionDate=?,typeItem=?,typeTransaction=?,sources=?,seller=? "
					+ "WHERE id = ?")) {
				
				pst.setString(1, state.getIdTransation());
				pst.setString(2, state.getDescription());
				
				if(state.getEdition()!=null)
					pst.setString(3, state.getEdition().getId());
				else
					pst.setString(3, null);
				
				pst.setDouble(4, state.getItemPrice());
				pst.setDouble(5,state.getShippingPrice());
				pst.setString(6,state.getCurrency().getCurrencyCode());
				pst.setDate(7, new Date(state.getTransationDate().getTime()));
				pst.setString(8,state.getType().name());
				pst.setString(9,state.getTypeTransaction().name());
				pst.setString(10, state.getSource());
				pst.setString(11, state.getSeller());
				pst.setInt(12, state.getId());
				pst.executeUpdate();
			}
		}
		
	}

	@Override
	public void deleteOrderEntry(List<OrderEntry> state) throws SQLException {
		logger.debug("remove " + state.size() + " items in orders");
		StringBuilder st = new StringBuilder();
		st.append("delete from orders where id IN (");
		for (OrderEntry sto : state) {
			st.append(sto.getId()).append(",");
		}
		st.append(")");
		String sql = st.toString().replace(",)", ")");
		try (Statement pst = con.createStatement()) {
			pst.executeUpdate(sql);
		}
		

		if (listOrders != null)
		{
			boolean res = listOrders.removeAll(state);
			logger.debug("delete orders from list " + res);
		}
		
		
	}

	@Override
	protected void initOrders() {
		try (PreparedStatement pst = con.prepareStatement("select * from orders"); ResultSet rs = pst.executeQuery();) {
			while (rs.next()) {
				OrderEntry state = new OrderEntry();
				
				state.setId(rs.getInt("id"));
				state.setIdTransation(rs.getString("idTransaction"));
				state.setDescription(rs.getString("description"));
				try {
					state.setEdition(MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetById(rs.getString("edition")));
				} catch (Exception e) {
					state.setEdition(null);
				}
				state.setCurrency(Currency.getInstance(rs.getString("currency")));
				state.setTransationDate(rs.getDate("transactionDate"));
				state.setItemPrice(rs.getDouble("itemPrice"));
				state.setShippingPrice(rs.getDouble("shippingPrice"));
				state.setType(TYPE_ITEM.valueOf(rs.getString("typeItem")));
				state.setTypeTransaction(TYPE_TRANSACTION.valueOf(rs.getString("typeTransaction")));
				state.setSource(rs.getString("sources"));
				state.setSeller(rs.getString("seller"));
				state.setUpdated(false);
				listOrders.add(state);
			}
			logger.debug("load " + listOrders.size() + " item(s) from orders");
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		
	}

}

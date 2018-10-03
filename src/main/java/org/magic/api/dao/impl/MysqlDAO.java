package org.magic.api.dao.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.Driver;
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
import org.magic.api.beans.MagicNews;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGNewsProvider;
import org.magic.api.interfaces.abstracts.AbstractMagicDAO;
import org.magic.services.MTGControler;
import org.magic.tools.IDGenerator;

public class MysqlDAO extends AbstractMagicDAO {

	private static final String MCARD = "mcard";
	private static final String MYSQL_DUMP_PATH = "MYSQL_DUMP_PATH";
	private static final String LOGIN = "LOGIN";
	private static final String PASS = "PASS";
	private static final String DB_NAME = "DB_NAME";
	private static final String PARAMS = "PARAMS";
	private static final String SERVERPORT = "SERVERPORT";
	private static final String SERVERNAME = "SERVERNAME";
	private static final String DRIVER = "DRIVER";
	
	
	private Connection con;
	private List<MagicCardAlert> list;

	public MysqlDAO() throws ClassNotFoundException, SQLException {
		super();
		list = new ArrayList<>();
	}

	public void init() throws SQLException, ClassNotFoundException {
		logger.info("init " + getName());
		String url = "jdbc:mysql://" + getString(SERVERNAME) + ":" + getString(SERVERPORT);
		logger.trace("Connexion to " + url + "/" + getString(DB_NAME) + getString(PARAMS));
		con = DriverManager.getConnection(url + "/" + getString(DB_NAME) + getString(PARAMS),getString(LOGIN), getString(PASS));
		createDB();
	}

	public boolean createDB() {
		try (Statement stat = con.createStatement()) {
			logger.debug("Create table Cards");
			stat.executeUpdate("create table cards (ID varchar(250),mcard TEXT, edition varchar(20), cardprovider varchar(50),collection varchar(250))");
			logger.debug("Create table collections");
			stat.executeUpdate("CREATE TABLE collections ( name VARCHAR(250))");
			logger.debug("Create table stocks");
			stat.executeUpdate("create table stocks (idstock integer PRIMARY KEY AUTO_INCREMENT, idmc varchar(250), mcard TEXT, collection varchar(250),comments varchar(250), conditions varchar(50),foil boolean, signedcard boolean, langage varchar(50), qte integer,altered boolean,price double)");
			logger.debug("Create table Alerts");
			stat.executeUpdate("create table alerts (id varchar(250) PRIMARY KEY, mcard TEXT, amount DECIMAL)");
			logger.debug("Create table News");
			stat.executeUpdate("CREATE TABLE news (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100), url VARCHAR(256), categorie VARCHAR(100))");

			logger.debug("populate collections");
			stat.executeUpdate("insert into collections values ('Library')");
			stat.executeUpdate("insert into collections values ('Needed')");
			stat.executeUpdate("insert into collections values ('For sell')");
			stat.executeUpdate("insert into collections values ('Favorites')");

			stat.executeUpdate("ALTER TABLE cards ADD INDEX(ID);");
			stat.executeUpdate("ALTER TABLE cards ADD INDEX(edition);");
			stat.executeUpdate("ALTER TABLE cards ADD INDEX(collection);");
			stat.executeUpdate("ALTER TABLE cards ADD PRIMARY KEY (ID,edition,collection);");
			return true;
		} catch (SQLException e) {
			logger.error(e);
			return false;
		}

	}

	@Override
	public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException {
		logger.debug("saving " + mc + " in " + collection);

		try (PreparedStatement pst = con.prepareStatement("insert into cards values (?,?,?,?,?)")) {
			pst.setString(1, IDGenerator.generate(mc));
			pst.setString(2, serialiser.toJsonTree(mc).toString());
			pst.setString(3, mc.getCurrentSet().getId());
			pst.setString(4, MTGControler.getInstance().getEnabled(MTGCardsProvider.class).toString());
			pst.setString(5, collection.getName());
			pst.executeUpdate();
		}
	}

	@Override
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
		logger.debug("delete " + mc + " in " + collection);
		try (PreparedStatement pst = con
				.prepareStatement("delete from cards where id=? and edition=? and collection=?")) {
			pst.setString(1, IDGenerator.generate(mc));
			pst.setString(2, mc.getCurrentSet().getId());
			pst.setString(3, collection.getName());
			pst.executeUpdate();
		}
	}
	
	@Override
	public void moveCard(MagicCard mc, MagicCollection from, MagicCollection to) throws SQLException {
		logger.debug("move " + mc + " from " + from + " to " + to);
		
		try (PreparedStatement pst = con.prepareStatement("update cards set collection= ? where id=? and collection=?")) 
		{
			pst.setString(1, to.getName());
			pst.setString(2, IDGenerator.generate(mc));
			pst.setString(3, from.getName());
			pst.executeUpdate();
		}
		
		listStocks(mc, from).forEach(cs->{
			
			try {
				cs.setMagicCollection(to);
				saveOrUpdateStock(cs);
			} catch (SQLException e) {
				logger.error("Error saving stock for" + mc + " from " + from + " to " + to);
			}
		});
		
	}
	

	@Override
	public List<MagicCard> listCards() throws SQLException {
		logger.debug("list all cards");

		String sql = "select mcard from cards";

		try (PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery();) {
			List<MagicCard> listCards = new ArrayList<>();
			while (rs.next()) {
				listCards.add(serialiser.fromJson(rs.getString(MCARD), MagicCard.class) );
			}
			return listCards;
		}
	}

	@Override
	public Map<String, Integer> getCardsCountGlobal(MagicCollection c) throws SQLException {
		String sql = "select edition, count(ID) from cards where collection=? group by edition";
		try (PreparedStatement pst = con.prepareStatement(sql);) {
			pst.setString(1, c.getName());
			try (ResultSet rs = pst.executeQuery()) {
				Map<String, Integer> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
				while (rs.next())
					map.put(rs.getString(1), rs.getInt(2));
				return map;
			}
		}
	}

	@Override
	public int getCardsCount(MagicCollection cols, MagicEdition me) throws SQLException {

		String sql = "select count(ID) from cards ";

		if (cols != null)
			sql += " where collection = '" + cols.getName() + "'";

		if (me != null)
			sql += " and LOWER('edition') = '" + me.getId().toLowerCase() + "'";

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
					ret.add(serialiser.fromJson(rs.getString(MCARD),MagicCard.class));
				}
				return ret;
			}
		}
	}

	@Override
	public List<String> getEditionsIDFromCollection(MagicCollection collection) throws SQLException {
		String sql = "select distinct(edition) from cards where collection=?";

		try (PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setString(1, collection.getName());
			try (ResultSet rs = pst.executeQuery()) {
				List<String> retour = new ArrayList<>();
				while (rs.next()) {
					retour.add(rs.getString("edition"));
				}
				return retour;
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

		try (PreparedStatement pst2 = con.prepareStatement("delete from cards where collection = ?")) {
			pst2.setString(1, c.getName());
			pst2.executeUpdate();
		}
	}

	@Override
	public List<MagicCollection> getCollections() throws SQLException {
		try (PreparedStatement pst = con.prepareStatement("select * from collections")) {
			try (ResultSet rs = pst.executeQuery()) {
				List<MagicCollection> colls = new ArrayList<>();
				while (rs.next()) {
					MagicCollection mc = new MagicCollection(rs.getString(1));
					colls.add(mc);
				}
				return colls;
			}
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
		return getString(SERVERNAME) + "/" + getString(DB_NAME);
	}

	@Override
	public long getDBSize() {
		String sql = "SELECT Round(Sum(data_length + index_length), 1) FROM   information_schema.tables WHERE  table_schema = 'mtgdesktopclient'";
		try (PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery();) {
			rs.first();
			return (long) rs.getDouble(1);
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
	public List<MagicCollection> listCollectionFromCards(MagicCard mc) throws SQLException {

		if (mc.getEditions().isEmpty())
			throw new SQLException("No edition defined");
		try (PreparedStatement pst = con.prepareStatement("SELECT collection FROM cards WHERE id=? and edition=?")) {
			String id = IDGenerator.generate(mc);
			
			logger.trace("SELECT collection FROM cards WHERE id="+id+" and edition='"+mc.getCurrentSet().getId()+"'");
			
			
			pst.setString(1, id);
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
	public List<MagicCardStock> listStocks(MagicCard mc, MagicCollection col) throws SQLException {
		try (PreparedStatement pst = con.prepareStatement("select * from stocks where idmc=? and collection=?")) {
			pst.setString(1, IDGenerator.generate(mc));
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

	public List<MagicCardStock> listStocks() throws SQLException {
		try (PreparedStatement pst = con.prepareStatement("select * from stocks"); ResultSet rs = pst.executeQuery();) {
			List<MagicCardStock> colls = new ArrayList<>();
			while (rs.next()) {
				MagicCardStock state = new MagicCardStock();

				state.setComment(rs.getString("comments"));
				state.setIdstock(rs.getInt("idstock"));
				state.setMagicCard(serialiser.fromJson(rs.getString(MCARD),MagicCard.class));
				state.setMagicCollection(new MagicCollection(rs.getString("collection")));
				try {
					state.setCondition(EnumCondition.valueOf(rs.getString("conditions")));
				} catch (Exception e) {
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
			logger.debug("load " + colls.size() + " item(s) from stock");
			return colls;
		}
	}

	private int getGeneratedKey(PreparedStatement pst) {

		try (ResultSet rs = pst.getGeneratedKeys()) {
			rs.next();
			return rs.getInt(1);
		} catch (SQLException e) {
			logger.error("couldn't retrieve id", e);
		}
		return -1;

	}

	@Override
	public void saveOrUpdateStock(MagicCardStock state) throws SQLException {

		if (state.getIdstock() < 0) {

			logger.debug("save " + state);
			try (PreparedStatement pst = con.prepareStatement(
					"insert into stocks  ( conditions,foil,signedcard,langage,qte,comments,idmc,collection,mcard,altered,price) values (?,?,?,?,?,?,?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS)) {
				pst.setString(1, String.valueOf(state.getCondition()));
				pst.setBoolean(2, state.isFoil());
				pst.setBoolean(3, state.isSigned());
				pst.setString(4, state.getLanguage());
				pst.setInt(5, state.getQte());
				pst.setString(6, state.getComment());
				pst.setString(7, IDGenerator.generate(state.getMagicCard()));
				pst.setString(8, String.valueOf(state.getMagicCollection()));
				pst.setString(9, serialiser.toJsonTree(state.getMagicCard()).toString());
				pst.setBoolean(10, state.isAltered());
				pst.setDouble(11, state.getPrice());
				pst.executeUpdate();
				state.setIdstock(getGeneratedKey(pst));
			} catch (Exception e) {
				logger.error(e);
			}
		} else {
			logger.debug("update Stock " + state);
			try (PreparedStatement pst = con.prepareStatement(
					"update stocks set comments=?, conditions=?, foil=?,signedcard=?,langage=?, qte=? ,altered=?,price=?,idmc=?,collection=? where idstock=?")) {
				pst.setString(1, state.getComment());
				pst.setString(2, state.getCondition().toString());
				pst.setBoolean(3, state.isFoil());
				pst.setBoolean(4, state.isSigned());
				pst.setString(5, state.getLanguage());
				pst.setInt(6, state.getQte());
				pst.setBoolean(7, state.isAltered());
				pst.setDouble(8, state.getPrice());
				pst.setString(9, IDGenerator.generate(state.getMagicCard()));
				pst.setString(10, state.getMagicCollection().getName());
				pst.setInt(11, state.getIdstock());
				pst.executeUpdate();
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	@Override
	public void backup(File f) throws SQLException, IOException {

		if (getString(MYSQL_DUMP_PATH).length() <= 0)
			throw new NullPointerException("Please fill MYSQL_DUMP_PATH var");

		if (!new File(getString(MYSQL_DUMP_PATH)).exists())
			throw new IOException(getString(MYSQL_DUMP_PATH) + " doesn't exist");

		StringBuilder dumpCommand = new StringBuilder();
		dumpCommand.append(getString(MYSQL_DUMP_PATH)).append("/mysqldump ").append(getString(DB_NAME))
				   .append(" -h ").append(getString(SERVERNAME))
				   .append(" -u ").append(getString(LOGIN))
				   .append(" -p").append(getString(PASS))
				   .append(" --port ").append(getString(SERVERPORT));
		
		Runtime rt = Runtime.getRuntime();
		logger.info("begin Backup " + getString(DB_NAME));
		Process child;

		child = rt.exec(dumpCommand.toString());
		try (PrintStream ps = new PrintStream(f)) {
			InputStream in = child.getInputStream();
			int ch;
			while ((ch = in.read()) != -1) {
				ps.write(ch);
			}
			logger.info("Backup " + getString(DB_NAME) + " done");
		}

	}

	@Override
	public List<MagicCardAlert> listAlerts() {

		if (!list.isEmpty())
			return list;

		try (PreparedStatement pst = con.prepareStatement("select * from alerts")) {
			list = new ArrayList<>();
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					MagicCardAlert alert = new MagicCardAlert();
					alert.setCard(serialiser.fromJson(rs.getString(MCARD),MagicCard.class));
					alert.setId(rs.getString("id"));
					alert.setPrice(rs.getDouble("amount"));

					list.add(alert);
				}
				return list;
			}
		} catch (Exception e) {
			logger.error("error get alert",e);
			return new ArrayList<>();
		}
	}

	@Override
	public boolean hasAlert(MagicCard mc) {
		try (PreparedStatement pst = con.prepareStatement("select * from alerts where id=?")) {
			pst.setString(1, IDGenerator.generate(mc));
			try (ResultSet rs = pst.executeQuery()) {
				return rs.next();
			}
		} catch (Exception e) {
			return false;
		}

	}

	@Override
	public void saveAlert(MagicCardAlert alert) throws SQLException {

		try (PreparedStatement pst = con.prepareStatement("insert into alerts  ( id,mcard,amount) values (?,?,?)")) {
			
			alert.setId(IDGenerator.generate(alert.getCard()));
			
			pst.setString(1, alert.getId());
			pst.setString(2, serialiser.toJsonTree(alert.getCard()).toString());
			pst.setDouble(3, alert.getPrice());
			pst.executeUpdate();
			logger.debug("save alert for " + alert.getCard()+ " ("+alert.getCard().getCurrentSet()+")");
			list.add(alert);
		}
	}

	@Override
	public void updateAlert(MagicCardAlert alert) throws SQLException {
		logger.debug("update " + alert);
		try (PreparedStatement pst = con.prepareStatement("update alerts set amount=?,mcard=? where id=?")) {
			pst.setDouble(1, alert.getPrice());
			pst.setString(2, serialiser.toJsonTree(alert.getCard()).toString());
			pst.setString(3, alert.getId());
			pst.executeUpdate();
		}

	}

	@Override
	public void deleteAlert(MagicCardAlert alert) throws SQLException 
	{
		try (PreparedStatement pst = con.prepareStatement("delete from alerts where id=?")) {
			logger.debug("delete from alerts where id="+alert.getId());
			pst.setString(1, alert.getId());
			int res = pst.executeUpdate();
			logger.debug("delete alert " + alert + " ("+alert.getCard().getCurrentSet()+")="+res);
		}

		if (list != null)
		{
			boolean res = list.remove(alert);
			logger.debug("delete alert from list " + res);
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
					n.setId(rs.getInt("id"));
					n.setProvider(MTGControler.getInstance().getPlugin(rs.getString("typeNews"),MTGNewsProvider.class));
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
			try (PreparedStatement pst = con.prepareStatement(
					"insert into news  ( name,categorie,url,typeNews) values (?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS)) {
				pst.setString(1, n.getName());
				pst.setString(2, n.getCategorie());
				pst.setString(3, n.getUrl());
				pst.setString(4, n.getProvider().getName());
				pst.executeUpdate();
				n.setId(getGeneratedKey(pst));
			}

		} else {
			logger.debug("update " + n);
			try (PreparedStatement pst = con
					.prepareStatement("update news set name=?, categorie=?, url=?,typeNews=? where id=?")) {
				pst.setString(1, n.getName());
				pst.setString(2, n.getCategorie());
				pst.setString(3, n.getUrl());
				pst.setString(4, n.getProvider().getName());
				pst.setInt(5, n.getId());
				pst.executeUpdate();
			} catch (Exception e) {
				logger.error(e);
			}
		}

	}

	@Override
	public void initDefault() {
		setProperty(DRIVER, "com.mysql.jdbc.Driver");
		setProperty(SERVERNAME, "localhost");
		setProperty(SERVERPORT, "3306");
		setProperty(DB_NAME, "mtgdesktopclient");
		setProperty(LOGIN, "login");
		setProperty(PASS, "");
		setProperty(PARAMS, "?autoDeserialize=true&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&autoReconnect=true");
		setProperty(MYSQL_DUMP_PATH, "C:\\Program Files (x86)\\Mysql\\bin");

	}

	@Override
	public String getVersion() {
		
		try {
			Driver d = new com.mysql.cj.jdbc.Driver();
			return d.getMajorVersion()+"."+d.getMinorVersion();
		} catch (SQLException e) {
			return "1.0";
		}
		
	}

}
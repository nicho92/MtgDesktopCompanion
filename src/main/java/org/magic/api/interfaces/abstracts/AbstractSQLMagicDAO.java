package org.magic.api.interfaces.abstracts;

import java.io.IOException;
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
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.OrderEntry.TYPE_ITEM;
import org.magic.api.beans.OrderEntry.TYPE_TRANSACTION;
import org.magic.api.beans.Packaging;
import org.magic.api.beans.SealedStock;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGNewsProvider;
import org.magic.api.interfaces.MTGPool;
import org.magic.api.pool.impl.NoPool;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.IDGenerator;

public abstract class AbstractSQLMagicDAO extends AbstractMagicDAO {

	private static final String EDITION = "edition";
	protected MTGPool pool;
	protected abstract String getAutoIncrementKeyWord();
	protected abstract String getjdbcnamedb();
	protected abstract String cardStorage();
	protected abstract void storeCard(PreparedStatement pst, int position,MagicCard mc) throws SQLException;
	protected abstract MagicCard readCard(ResultSet rs) throws SQLException;
	protected abstract String createListStockSQL(MagicCard mc);
	protected abstract String getdbSizeQuery();
	
	protected static final int COLLECTION_COLUMN_SIZE=30;
	protected static final int CARD_ID_SIZE=50;

	protected boolean enablePooling()
	{
		return true;
	}

	
	public void createIndex(Statement stat) throws SQLException {
		stat.executeUpdate("CREATE INDEX idx_id ON cards (ID);");
		stat.executeUpdate("CREATE INDEX idx_ed ON cards (edition);");
		stat.executeUpdate("CREATE INDEX idx_col ON cards (collection);");
		stat.executeUpdate("CREATE INDEX idx_cprov ON cards (cardprovider);");
		
		
		stat.executeUpdate("CREATE INDEX idx_stk_idmc ON stocks (idmc);");
		stat.executeUpdate("CREATE INDEX idx_stk_col ON stocks (collection);");
		stat.executeUpdate("CREATE INDEX idx_stk_com ON stocks (comments);");
		stat.executeUpdate("CREATE INDEX idx_stk_con ON stocks (conditions);");
		stat.executeUpdate("CREATE INDEX idx_stk_lang ON stocks (langage);");
		stat.executeUpdate("CREATE INDEX idx_stk_gradeName ON stocks (gradeName);");
		
		
		stat.executeUpdate("CREATE INDEX idx_ord_idt ON orders (idTransaction);");
		stat.executeUpdate("CREATE INDEX idx_ord_des ON orders (description);");
		stat.executeUpdate("CREATE INDEX idx_ord_ed ON orders (edition);");
		stat.executeUpdate("CREATE INDEX idx_ord_cur ON orders (currency);");
		stat.executeUpdate("CREATE INDEX idx_ord_ite ON orders (typeItem);");
		stat.executeUpdate("CREATE INDEX idx_ord_tra ON orders (typeTransaction);");
		stat.executeUpdate("CREATE INDEX idx_ord_src ON orders (sources);");
		stat.executeUpdate("CREATE INDEX idx_ord_sel ON orders (seller);");
		
		
		stat.executeUpdate("CREATE INDEX idx_news_nam ON news (name);");
		stat.executeUpdate("CREATE INDEX idx_news_url ON news (url);");
		stat.executeUpdate("CREATE INDEX idx_news_ctg ON news (categorie);");
		stat.executeUpdate("CREATE INDEX idx_news_typ ON news (typeNews);");
		
		stat.executeUpdate("CREATE INDEX idx_sld_edition ON sealed (edition);");
		stat.executeUpdate("CREATE INDEX idx_sld_comment ON sealed (comment);");
		stat.executeUpdate("CREATE INDEX idx_sld_lang ON sealed (lang);");
		stat.executeUpdate("CREATE INDEX idx_sld_type ON sealed (typeProduct);");
		stat.executeUpdate("CREATE INDEX idx_sld_cdt ON sealed (conditionProduct);");
		
		stat.executeUpdate("CREATE INDEX idx_alrt_ida ON alerts (id);");
		
		stat.executeUpdate("ALTER TABLE cards ADD PRIMARY KEY (ID,edition,collection);");

	}
	
	@Override
	public void unload() {
		super.unload();
		if(pool!=null)
			try {
				pool.close();
			} catch (SQLException e) {
				logger.error(e);
			}
	}
	
	@Override
	public void initDefault() {
		setProperty(SERVERNAME, "localhost");
		setProperty(SERVERPORT, "");
		setProperty(DB_NAME, "mtgdesktopclient");
		setProperty(LOGIN, "login");
		setProperty(PASS, "pass");
		setProperty(PARAMS, "");
	}
	
	public String getDBLocation() {
		return getString(SERVERNAME) + "/" + getString(DB_NAME);
	}
	

	@Override
	public String getVersion() {
		try {
			Driver d = DriverManager.getDriver(getjdbcUrl());
			return d.getMajorVersion()+"."+d.getMinorVersion();
		} catch (SQLException e) {
			return "1.0";
		}
	}

	@Override
	public long getDBSize() {
		String sql = getdbSizeQuery();
		
		if(sql==null)
			return 0;
		
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(sql); ResultSet rs = pst.executeQuery();) {
			rs.first();
			return (long) rs.getDouble(1);
		} catch (SQLException e) {
			logger.error(e);
			return 0;
		}

	}

	
	protected String getjdbcUrl()
	{
		StringBuilder url = new StringBuilder();
					  url.append("jdbc:").append(getjdbcnamedb()).append("://").append(getString(SERVERNAME));

		if(!getString(SERVERPORT).isEmpty())
			url.append(":").append(getString(SERVERPORT));
	
		if(!getString(DB_NAME).isEmpty())
			url.append("/").append(getString(DB_NAME));
			
		if(!getString(PARAMS).isEmpty())	
			url.append(getString(PARAMS));
		
		return url.toString();
	}
	

	
	@Override
	public void init(MTGPool p) throws SQLException {
		pool = p;
		if(pool==null)
		{
			pool=new NoPool();
			logger.error("error loading selected pool. Use default");
		}
	
		pool.init(getjdbcUrl(),getString(LOGIN), getString(PASS),enablePooling());
		createDB();
	}

	
	 
	public void init() throws SQLException {
		logger.info("init " + getName());
		init(MTGControler.getInstance().getEnabled(MTGPool.class));
	}

	public boolean createDB() {
		try (Connection cont =  pool.getConnection();Statement stat = cont.createStatement()) {
			stat.executeUpdate("CREATE TABLE IF NOT EXISTS orders (id "+getAutoIncrementKeyWord()+" PRIMARY KEY, idTransaction VARCHAR(50), description VARCHAR(250),edition VARCHAR(5),itemPrice DECIMAL(10,3),shippingPrice  DECIMAL(10,3), currency VARCHAR(4), transactionDate DATE,typeItem VARCHAR(50),typeTransaction VARCHAR(50),sources VARCHAR(50),seller VARCHAR(50))");
			logger.debug("Create table Orders");
			
			stat.executeUpdate("create TABLE IF NOT EXISTS cards (ID varchar("+CARD_ID_SIZE+"),mcard "+cardStorage()+", edition VARCHAR(5), cardprovider VARCHAR(20),collection VARCHAR("+COLLECTION_COLUMN_SIZE+"))");
			logger.debug("Create table cards");
			
			stat.executeUpdate("CREATE TABLE IF NOT EXISTS collections ( name VARCHAR("+COLLECTION_COLUMN_SIZE+") PRIMARY KEY)");
			logger.debug("Create table collections");
			
			stat.executeUpdate("create table IF NOT EXISTS stocks (idstock "+getAutoIncrementKeyWord()+" PRIMARY KEY , idmc varchar("+CARD_ID_SIZE+"), mcard "+cardStorage()+", collection VARCHAR("+COLLECTION_COLUMN_SIZE+"),comments VARCHAR(250), conditions VARCHAR(30),foil boolean, signedcard boolean, langage VARCHAR(20), qte integer,altered boolean,price DECIMAL, graded BOOLEAN, gradeName VARCHAR(50), gradeNote DECIMAL(5,2))");
			logger.debug("Create table stocks");
			
			stat.executeUpdate("create table IF NOT EXISTS alerts (id varchar("+CARD_ID_SIZE+") PRIMARY KEY, mcard "+cardStorage()+", amount DECIMAL)");
			logger.debug("Create table alerts");
			
			stat.executeUpdate("CREATE TABLE IF NOT EXISTS news (id "+getAutoIncrementKeyWord()+" PRIMARY KEY, name VARCHAR(100), url VARCHAR(255), categorie VARCHAR(50),typeNews VARCHAR(50))");
			logger.debug("Create table news");
			
			stat.executeUpdate("CREATE TABLE IF NOT EXISTS sealed (id "+getAutoIncrementKeyWord()+" PRIMARY KEY, edition VARCHAR(5), qte integer, comment VARCHAR(250),lang VARCHAR(50),typeProduct VARCHAR(25),conditionProduct VARCHAR(25))");
			logger.debug("Create table selead");

	
			logger.debug("populate collections");
			
			for(String s : MTGConstants.getDefaultCollectionsNames())
				stat.executeUpdate("insert into collections values ('"+s+"')");
			
			createIndex(stat);
			
			
			return true;
		} catch (SQLException e) {
			logger.error(e);
			return false;
		}
	}
	
	@Override
	public void deleteStock(SealedStock state) throws SQLException {
		logger.debug("del " + state.getId() + " in sealed stock");
		String sql = "DELETE FROM sealed WHERE id=?";
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(sql)) 
		{
			pst.setInt(1, state.getId());
			pst.executeUpdate();
		}
		
	}
	
	@Override
	public List<SealedStock> listSeleadStocks() throws SQLException {
		List<SealedStock> colls = new ArrayList<>();
		
		try (Connection c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT * from sealed");ResultSet rs = pst.executeQuery()) 
		{
				while (rs.next()) {
					SealedStock state = new SealedStock();
					
					state.setComment(rs.getString("comment"));
					state.setId(rs.getInt("id"));
					state.setQte(rs.getInt("qte"));
					Packaging p = new Packaging();
					 		  p.setLang(rs.getString("lang"));
							  p.setType(Packaging.TYPE.valueOf(rs.getString("typeProduct")));
							  try 
							  {
								p.setEdition(MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetById(rs.getString(EDITION)));
							  } 
							  catch (IOException e) 
							  {
								logger.error(e);
								throw new SQLException(e);
							  }
					state.setProduct(p);
					colls.add(state);
				}
				logger.trace("loading " + colls.size() + " item FROM sealed");
		}
		return colls;
	}
	
	@Override
	public void saveOrUpdateStock(SealedStock state) throws SQLException {

		if (state.getId() < 0) {

			logger.debug("save stock " + state);
			try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(
					"INSERT INTO sealed (edition, qte, comment, lang, typeProduct, conditionProduct) VALUES (?, ?, ?, ?, ?, ?)",Statement.RETURN_GENERATED_KEYS)) {
				pst.setString(1, String.valueOf(state.getProduct().getEdition().getId()));
				pst.setInt(2, state.getQte());
				pst.setString(3, state.getComment());
				pst.setString(4, state.getProduct().getLang());
				pst.setString(5, state.getProduct().getType().name());
				pst.setString(6, state.getCondition().name());
				pst.executeUpdate();
				state.setId(getGeneratedKey(pst));
			} catch (Exception e) {
				logger.error("error insert", e);
			}
		} else {
			logger.debug("update Stock " + state);
			try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(
					"update sealed set edition=?, qte=?, comment=?, lang=?, typeProduct=?, conditionProduct=? where id=?")) {
				pst.setString(1, String.valueOf(state.getProduct().getEdition().getId()));
				pst.setInt(2, state.getQte());
				pst.setString(3, state.getComment());
				pst.setString(4, state.getProduct().getLang());
				pst.setString(5, state.getProduct().getType().name());
				pst.setString(6, state.getCondition().name());
				pst.setInt(7, state.getId());
				pst.executeUpdate();
			} catch (Exception e) {
				logger.error(e);
			}
		}
		notify(state);
		
	}
	
	

	@Override
	public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException {
		logger.debug("saving " + mc + " in " + collection);

		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("insert into cards values (?,?,?,?,?)")) {
			pst.setString(1, IDGenerator.generate(mc));
			storeCard(pst, 2, mc);
			pst.setString(3, mc.getCurrentSet().getId());
			pst.setString(4, MTGControler.getInstance().getEnabled(MTGCardsProvider.class).toString());
			pst.setString(5, collection.getName());
			pst.executeUpdate();
		}
	}

	@Override
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
		logger.debug("delete " + mc + " in " + collection);
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("DELETE FROM cards where id=? and edition=? and collection=?")) {
			pst.setString(1, IDGenerator.generate(mc));
			pst.setString(2, mc.getCurrentSet().getId());
			pst.setString(3, collection.getName());
			pst.executeUpdate();
		}
	}
	
	@Override
	public void moveCard(MagicCard mc, MagicCollection from, MagicCollection to) throws SQLException {
		logger.debug("move " + mc+ " s=" + from + " d=" + to);
		
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("update cards set collection= ? where id=? and collection=?")) 
		{
			pst.setString(1, to.getName());
			pst.setString(2, IDGenerator.generate(mc));
			pst.setString(3, from.getName());
			int res = pst.executeUpdate();
			
			logger.debug("moving " + IDGenerator.generate(mc) + "=" + res);
		}
		
		listStocks(mc, from,true).forEach(cs->{
			
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
		List<MagicCard> listCards = new ArrayList<>();
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT mcard FROM cards"); ResultSet rs = pst.executeQuery();) {
			while (rs.next()) {
				listCards.add(readCard(rs));
			}
		}
		return listCards;
	}

	@Override
	public Map<String, Integer> getCardsCountGlobal(MagicCollection col) throws SQLException {
		Map<String, Integer> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT edition, count(1) FROM cards where collection=? group by edition");) {
			pst.setString(1, col.getName());
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next())
					map.put(rs.getString(1), rs.getInt(2));
			}
		}
		return map;
	}

	@Override
	public int getCardsCount(MagicCollection cols, MagicEdition me) throws SQLException {

		String sql = "SELECT count(ID) FROM cards ";

		if (cols != null)
			sql += " where collection = '" + cols.getName() + "'";

		if (me != null)
			sql += " and LOWER('edition') = '" + me.getId().toLowerCase() + "'";

		logger.trace(sql);

		try (Connection c = pool.getConnection();Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql);) {
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
		
		List<MagicCard> ret = new ArrayList<>();
		String sql = "SELECT mcard FROM cards where collection= ?";
		
		if (me != null)
			sql = "SELECT mcard FROM cards where collection= ? and edition = ?";

		logger.trace(sql +" " + collection +" " + me);

		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(sql)) {
			pst.setString(1, collection.getName());
			if (me != null)
				pst.setString(2, me.getId());
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					MagicCard mc = readCard(rs);
					ret.add(mc);
				}
			}
		}
		return ret;
	}

	@Override
	public List<String> listEditionsIDFromCollection(MagicCollection collection) throws SQLException {
		String sql = "SELECT distinct(edition) FROM cards where collection=?";
		List<String> retour = new ArrayList<>();
		logger.trace(sql);
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(sql)) {
			pst.setString(1, collection.getName());
			try (ResultSet rs = pst.executeQuery()) {
				
				while (rs.next()) {
					retour.add(rs.getString(EDITION));
				}
			}
		}
		return retour;
	}

	@Override
	public MagicCollection getCollection(String name) throws SQLException {
		
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM collections where name= ?")) {
			pst.setString(1, name);
			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					return new MagicCollection(rs.getString("name"));
				}
				return null;
			}
		}
	}

	@Override
	public void saveCollection(MagicCollection col) throws SQLException {
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("insert into collections values (?)")) {
			pst.setString(1, col.getName().replace("'", "\'"));
			pst.executeUpdate();
		}
	}

	@Override
	public void removeCollection(MagicCollection col) throws SQLException {

		if (col.getName().equals(MTGControler.getInstance().get("default-library")))
			throw new SQLException(col.getName() + " can not be deleted");

		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("DELETE FROM collections where name = ?")) {
			pst.setString(1, col.getName());
			pst.executeUpdate();

		}

		try (Connection c = pool.getConnection(); PreparedStatement pst2 = c.prepareStatement("DELETE FROM cards where collection = ?")) {
			pst2.setString(1, col.getName());
			pst2.executeUpdate();
		}
	}

	@Override
	public List<MagicCollection> listCollections() throws SQLException {
		List<MagicCollection> colls = new ArrayList<>();
		try (Connection cont =  pool.getConnection();PreparedStatement pst = cont.prepareStatement("SELECT * FROM collections")) 
		{
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					MagicCollection mc = new MagicCollection(rs.getString(1));
					colls.add(mc);
				}
			}
		}
		return colls;
	}

	@Override
	public void removeEdition(MagicEdition me, MagicCollection col) throws SQLException {
		logger.debug("delete " + me + " from " + col);
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("DELETE FROM cards where edition=? and collection=?")) {
			pst.setString(1, me.getId());
			pst.setString(2, col.getName());
			pst.executeUpdate();
		}
	}

	@Override
	public List<MagicCollection> listCollectionFromCards(MagicCard mc) throws SQLException {
		List<MagicCollection> cols = new ArrayList<>();
		if (mc.getEditions().isEmpty())
			throw new SQLException("No edition defined");
		
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT collection FROM cards WHERE id=? and edition=?")) {
			String id = IDGenerator.generate(mc);
			
			logger.trace("SELECT collection FROM cards WHERE id="+id+" and edition='"+mc.getCurrentSet().getId()+"'");
			
			
			pst.setString(1, id);
			pst.setString(2, mc.getCurrentSet().getId());
			try (ResultSet rs = pst.executeQuery()) {
				
				while (rs.next()) {
					MagicCollection col = new MagicCollection();
					col.setName(rs.getString("collection"));
					cols.add(col);
				}
			}
		}
		return cols;
	}

	@Override
	public void deleteStock(List<MagicCardStock> state) throws SQLException {
		logger.debug("remove " + state.size() + " items in stock");
		StringBuilder st = new StringBuilder();
		st.append("DELETE FROM stocks where idstock IN (");
		for (MagicCardStock sto : state) {
			st.append(sto.getIdstock()).append(",");
			notify(sto);
		}
		st.append(")");
		String sql = st.toString().replace(",)", ")");
		try (Connection c = pool.getConnection();Statement pst = c.createStatement()) {
			pst.executeUpdate(sql);
		}
	}

	@Override
	public List<MagicCardStock> listStocks(MagicCard mc, MagicCollection col,boolean editionStrict) throws SQLException {
		
		String sql = createListStockSQL(mc);
		
		if(editionStrict)
			sql ="SELECT * FROM stocks where collection=? and idmc=?";
		
		logger.trace("sql="+sql);
		
		List<MagicCardStock> colls = new ArrayList<>();
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(sql)) {
			pst.setString(1, col.getName());
			
			if(editionStrict)
				pst.setString(2, IDGenerator.generate(mc));
			else
				pst.setString(2, mc.getName());
			
		
			try (ResultSet rs = pst.executeQuery()) {
				
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
					state.setGrade(rs.getBoolean("graded"));
					state.setGradeName(rs.getString("gradeName"));
					state.setGradeNote(rs.getDouble("gradeNote"));
					colls.add(state);
				}
				logger.trace("loading " + colls.size() + " item FROM stock for " + mc);
				
			}

		}
		return colls;

	}

	public List<MagicCardStock> listStocks() throws SQLException {
		List<MagicCardStock> colls = new ArrayList<>();
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM stocks"); ResultSet rs = pst.executeQuery();) {
			while (rs.next()) {
				MagicCardStock state = new MagicCardStock();

				state.setComment(rs.getString("comments"));
				state.setIdstock(rs.getInt("idstock"));
				state.setMagicCard(readCard(rs));
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
				state.setGrade(rs.getBoolean("graded"));
				state.setGradeName(rs.getString("gradeName"));
				state.setGradeNote(rs.getDouble("gradeNote"));
				colls.add(state);
			}
			logger.debug("load " + colls.size() + " item(s) from stock");
		}
		return colls;
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
			logger.debug("save stock " + state);
			try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(
					"insert into stocks  ( conditions,foil,signedcard,langage,qte,comments,idmc,collection,mcard,altered,price,graded,gradeName,gradeNote) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS)) {
				pst.setString(1, String.valueOf(state.getCondition()));
				pst.setBoolean(2, state.isFoil());
				pst.setBoolean(3, state.isSigned());
				pst.setString(4, state.getLanguage());
				pst.setInt(5, state.getQte());
				pst.setString(6, state.getComment());
				pst.setString(7, IDGenerator.generate(state.getMagicCard()));
				pst.setString(8, String.valueOf(state.getMagicCollection()));
				storeCard(pst, 9, state.getMagicCard());
				pst.setBoolean(10, state.isAltered());
				pst.setDouble(11, state.getPrice());
				pst.setBoolean(12, state.isGrade());
				pst.setString(13, state.getGradeName());
				pst.setDouble(14, state.getGradeNote());
				pst.executeUpdate();
				state.setIdstock(getGeneratedKey(pst));
			} catch (Exception e) {
				logger.error(e);
			}
		} else {
			logger.debug("update Stock " + state);
			try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(
					"update stocks set comments=?, conditions=?, foil=?,signedcard=?,langage=?, qte=? ,altered=?,price=?,idmc=?,collection=?,graded=?,gradeName=?,gradeNote=? where idstock=?")) {
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
				pst.setBoolean(11, state.isGrade());
				pst.setString(12, state.getGradeName());
				pst.setDouble(13, state.getGradeNote());
				pst.setInt(14, state.getIdstock());
				pst.executeUpdate();
			} catch (Exception e) {
				logger.error(e);
			}
		}
		notify(state);
	}


	@Override
	public void initAlerts() {

		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM alerts")) {
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					MagicCardAlert alert = new MagicCardAlert();
					alert.setCard(readCard(rs));
					alert.setId(rs.getString("id"));
					alert.setPrice(rs.getDouble("amount"));

					listAlerts.put(alert.getId(),alert);
				}
			}
		} catch (Exception e) {
			logger.error("error get alert",e);
		}
	}


	@Override
	public void saveAlert(MagicCardAlert alert) throws SQLException {

		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("insert into alerts  ( id,mcard,amount) values (?,?,?)")) {
			
			alert.setId(IDGenerator.generate(alert.getCard()));
			
			pst.setString(1, alert.getId());
			storeCard(pst, 2, alert.getCard());
			pst.setDouble(3, alert.getPrice());
			pst.executeUpdate();
			logger.debug("save alert for " + alert.getCard()+ " ("+alert.getCard().getCurrentSet()+")");
			listAlerts.put(alert.getId(),alert);
		}
	}

	@Override
	public void updateAlert(MagicCardAlert alert) throws SQLException {
		logger.debug("update alert " + alert);
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("update alerts set amount=?,mcard=? where id=?")) {
			pst.setDouble(1, alert.getPrice());
			storeCard(pst, 2, alert.getCard());
			pst.setString(3, alert.getId());
			pst.executeUpdate();
		}

	}

	@Override
	public void deleteAlert(MagicCardAlert alert) throws SQLException 
	{
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("DELETE FROM alerts where id=?")) {
			pst.setString(1, alert.getId());
			int res = pst.executeUpdate();
			logger.debug("delete alert " + alert + " ("+alert.getCard().getCurrentSet()+")="+res);
		}

		if (listAlerts != null)
			listAlerts.remove(alert.getId());
	}

	@Override
	public List<MagicNews> listNews() {
		List<MagicNews> news = new ArrayList<>();
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM news")) {
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
			}
		} catch (Exception e) {
			logger.error(e);
			return new ArrayList<>();
		}
		return news;
	}

	@Override
	public void deleteNews(MagicNews n) throws SQLException {
		logger.debug("delete news " + n);
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("DELETE FROM news where id=?")) {
			pst.setInt(1, n.getId());
			pst.executeUpdate();
		}
	}

	@Override
	public void saveOrUpdateNews(MagicNews n) throws SQLException {
		if (n.getId() < 0) {

			logger.debug("save " + n);
			try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(
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
			try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("update news set name=?, categorie=?, url=?,typeNews=? where id=?")) {
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
	public void initOrders() {
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM orders"); ResultSet rs = pst.executeQuery();) 
		{
			while (rs.next()) {
				OrderEntry state = new OrderEntry();
				
				state.setId(rs.getInt("id"));
				state.setIdTransation(rs.getString("idTransaction"));
				state.setDescription(rs.getString("description"));
				
				setEdition(state,rs);
				state.setCurrency(Currency.getInstance(rs.getString("currency")));
				state.setTransactionDate(rs.getDate("transactionDate"));
				state.setItemPrice(rs.getDouble("itemPrice"));
				state.setShippingPrice(rs.getDouble("shippingPrice"));
				state.setType(TYPE_ITEM.valueOf(rs.getString("typeItem")));
				state.setTypeTransaction(TYPE_TRANSACTION.valueOf(rs.getString("typeTransaction")));
				state.setSource(rs.getString("sources"));
				state.setSeller(rs.getString("seller"));
				state.setUpdated(false);
				listOrders.put(state.getId(),state);
			}
			logger.debug("load " + listOrders.size() + " item(s) from orders");
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	


	private void setEdition(OrderEntry state, ResultSet rs) {
		try {
			state.setEdition(MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetById(rs.getString(EDITION)));
		} catch (Exception e) {
			state.setEdition(null);
		}
		
	}
	@Override
	public void deleteOrderEntry(List<OrderEntry> state) throws SQLException {
		logger.debug("remove " + state.size() + " items in orders");
		StringBuilder st = new StringBuilder();
		st.append("DELETE FROM orders where id IN (");
		for (OrderEntry sto : state) {
			st.append(sto.getId()).append(",");
		}
		st.append(")");
		String sql = st.toString().replace(",)", ")");
		try (Connection c = pool.getConnection();Statement pst = c.createStatement()) {
			pst.executeUpdate(sql);
		}
		

		if (listOrders != null)
		{
			state.forEach(d->listOrders.remove(String.valueOf(d.getId())));
		}
		
	}
	
	@Override
	public void saveOrUpdateOrderEntry(OrderEntry state) throws SQLException {

		if (state.getId() < 0) {
			logger.debug("save order " + state);
			try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(
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
				pst.setDate(7, new Date(state.getTransactionDate().getTime()));
				pst.setString(8,state.getType().name());
				pst.setString(9,state.getTypeTransaction().name());
				pst.setString(10, state.getSource());
				pst.setString(11, state.getSeller());
				pst.executeUpdate();
				state.setId(getGeneratedKey(pst));
				listOrders.put(state.getId(),state);
			} catch (Exception e) {
				logger.error("error insert " + state.getDescription() , e);
			}
		} else {
			logger.debug("update order " + state);
			try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(
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
				pst.setDate(7, new Date(state.getTransactionDate().getTime()));
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
	public void updateCard(MagicCard card,MagicCard newC, MagicCollection col) throws SQLException {
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("UPDATE cards SET mcard= ? WHERE id = ? and collection = ?"))
		{
			
			storeCard(pst, 1, newC);
			pst.setString(2, IDGenerator.generate(card));
			pst.setString(3, col.getName());
			pst.executeUpdate();
		}
		
	}
	

	@Override
	public void executeQuery(String query) throws SQLException {
		try (Connection c = pool.getConnection(); Statement pst = c.createStatement())
		{
			pst.execute(query);
		}
		
	}

	@Override
	public boolean isSQL() {
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj ==null)
			return false;
		
		return hashCode()==obj.hashCode();
	}
	
	@Override
	public int hashCode() {
		return (getType()+getName()).hashCode();
	}



}
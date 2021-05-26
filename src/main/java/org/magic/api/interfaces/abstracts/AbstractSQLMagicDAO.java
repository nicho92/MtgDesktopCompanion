package org.magic.api.interfaces.abstracts;

import static org.magic.tools.MTG.getEnabledPlugin;
import static org.magic.tools.MTG.getPlugin;

import java.io.IOException;
import java.sql.Date;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.magic.api.beans.Contact;
import org.magic.api.beans.Grading;
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
import org.magic.api.beans.Transaction;
import org.magic.api.beans.Transaction.STAT;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumStock;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGNewsProvider;
import org.magic.api.interfaces.MTGPool;
import org.magic.api.pool.impl.NoPool;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.providers.PackagesProvider;
import org.magic.tools.Chrono;
import org.magic.tools.IDGenerator;

public abstract class AbstractSQLMagicDAO extends AbstractMagicDAO {

	private static final String EDITION = "edition";
	protected MTGPool pool;
	protected abstract String getAutoIncrementKeyWord();
	protected abstract String getjdbcnamedb();
	protected abstract String beanStorage();
	protected abstract void storeCard(PreparedStatement pst, int position,MagicCard mc) throws SQLException;
	protected abstract void storeGrade(PreparedStatement pst, int position,Grading grd) throws SQLException;
	protected abstract void storeTransactionItems(PreparedStatement pst, int position,List<MagicCardStock> grd) throws SQLException;
	
	protected abstract List<MagicCardStock> readTransactionItems(ResultSet rs) throws SQLException;
	protected abstract Grading readGrading(ResultSet rs) throws SQLException;
	protected abstract MagicCard readCard(ResultSet rs) throws SQLException;
	protected abstract String createListStockSQL();
	protected abstract String getdbSizeQuery();
	protected abstract Map<String, String> readTiersApps(ResultSet rs) throws SQLException;
	protected abstract void storeTiersApps(PreparedStatement pst, int i, Map<String, String> tiersAppIds) throws SQLException;

	protected static final int COLLECTION_COLUMN_SIZE=30;
	protected static final int CARD_ID_SIZE=50;

	protected boolean enablePooling()
	{
		return true;
	}

	
	protected boolean isJsonCompatible()
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
		
		stat.executeUpdate("CREATE INDEX idx_trx_statut ON transactions (statut);");
		
		stat.executeUpdate("CREATE INDEX idx_ctc_name ON contacts (contact_name);");
		stat.executeUpdate("CREATE INDEX idx_ctc_lname ON contacts (contact_lastname);");
		stat.executeUpdate("CREATE INDEX idx_ctc_country ON contacts (contact_country);");
		stat.executeUpdate("CREATE INDEX idx_ctc_address ON contacts (contact_address);");
		stat.executeUpdate("CREATE INDEX idx_ctc_site ON contacts (contact_website);");
		stat.executeUpdate("CREATE INDEX idx_ctc_mail ON contacts (contact_email);");
		
		
		
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
		
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(sql); ResultSet rs = pst.executeQuery();) {
			rs.next();
			return (long) rs.getDouble(1);
		} catch (SQLException e) {
			logger.error(e);
			return 0;
		}

	}

	
	protected String getjdbcUrl()
	{
		var url = new StringBuilder();
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
		logger.debug("Loading SQL connection to : " + getjdbcUrl());
		pool.init(getjdbcUrl(),getString(LOGIN), getString(PASS),enablePooling());
		createDB();
	}

	
	 
	public void init() throws SQLException {
		logger.info("init " + getName());
		init(getEnabledPlugin(MTGPool.class));
	}

	public boolean createDB() {
		try (var cont =  pool.getConnection();Statement stat = cont.createStatement()) {
			
			stat.executeUpdate("CREATE TABLE IF NOT EXISTS transactions (id "+getAutoIncrementKeyWord()+" PRIMARY KEY, dateTransaction TIMESTAMP, message VARCHAR(250), stocksItem "+beanStorage()+", statut VARCHAR(15), transporter VARCHAR(50), shippingPrice DECIMAL, transporterShippingCode VARCHAR(50),currency VARCHAR(5),fk_idcontact INTEGER)");
			logger.debug("Create table transactions");
			
			stat.executeUpdate("CREATE TABLE IF NOT EXISTS contacts (id " + getAutoIncrementKeyWord() + " PRIMARY KEY, contact_name VARCHAR(250), contact_lastname VARCHAR(250), contact_password VARCHAR(250),contact_telephone VARCHAR(250), contact_country VARCHAR(250), contact_zipcode VARCHAR(10), contact_city VARCHAR(50), contact_address VARCHAR(250), contact_website VARCHAR(250),contact_email VARCHAR(100) UNIQUE, emailAccept boolean)");
			logger.debug("Create table contacts");
	
			stat.executeUpdate("CREATE TABLE IF NOT EXISTS orders (id "+getAutoIncrementKeyWord()+" PRIMARY KEY, idTransaction VARCHAR(50), description VARCHAR(250),edition VARCHAR(5),itemPrice DECIMAL(10,3),shippingPrice  DECIMAL(10,3), currency VARCHAR(4), transactionDate DATE,typeItem VARCHAR(50),typeTransaction VARCHAR(50),sources VARCHAR(50),seller VARCHAR(50))");
			logger.debug("Create table orders");
			
			stat.executeUpdate("create TABLE IF NOT EXISTS cards (ID varchar("+CARD_ID_SIZE+"),mcard "+beanStorage()+", edition VARCHAR(5), cardprovider VARCHAR(20),collection VARCHAR("+COLLECTION_COLUMN_SIZE+"))");
			logger.debug("Create table cards");
			
			stat.executeUpdate("CREATE TABLE IF NOT EXISTS collections ( name VARCHAR("+COLLECTION_COLUMN_SIZE+") PRIMARY KEY)");
			logger.debug("Create table collections");
			
			stat.executeUpdate("create table IF NOT EXISTS stocks (idstock "+getAutoIncrementKeyWord()+" PRIMARY KEY , idmc varchar("+CARD_ID_SIZE+"), mcard "+beanStorage()+", collection VARCHAR("+COLLECTION_COLUMN_SIZE+"),comments VARCHAR(250), conditions VARCHAR(30),foil boolean, signedcard boolean, langage VARCHAR(20), qte integer,altered boolean,price DECIMAL, grading "+beanStorage()+", tiersAppIds "+beanStorage()+",etched boolean)");
			logger.debug("Create table stocks");
			
			stat.executeUpdate("create table IF NOT EXISTS alerts (id varchar("+CARD_ID_SIZE+") PRIMARY KEY, mcard "+beanStorage()+", amount DECIMAL, foil boolean,qte integer)");
			logger.debug("Create table alerts");
			
			stat.executeUpdate("CREATE TABLE IF NOT EXISTS news (id "+getAutoIncrementKeyWord()+" PRIMARY KEY, name VARCHAR(100), url VARCHAR(255), categorie VARCHAR(50),typeNews VARCHAR(50))");
			logger.debug("Create table news");
			
			stat.executeUpdate("CREATE TABLE IF NOT EXISTS sealed (id "+getAutoIncrementKeyWord()+" PRIMARY KEY, edition VARCHAR(5), qte integer, comment VARCHAR(250),lang VARCHAR(50),typeProduct VARCHAR(25),conditionProduct VARCHAR(25),statut VARCHAR(10))");
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
	public Contact getContactByLogin(String email, String password) throws SQLException {
		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT * from contacts where contact_email=? and contact_password=?")) 
		{
				pst.setString(1, email);
				pst.setString(2, IDGenerator.generateSha256(password));
				
				ResultSet rs = pst.executeQuery();
				rs.next();
				
				return readContact(rs);
		}
		catch(SQLDataException sqlde)
		{
			return null;
		}
		
	}
	
	
	@Override
	public Contact getContactById(int id) throws SQLException {
		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT * from contacts where id=?")) 
		{
				pst.setInt(1, id);
				ResultSet rsC = pst.executeQuery();
				rsC.next();
				return readContact(rsC);
		}
	}
	
	@Override
	public List<Contact> listContacts() throws SQLException {
		List<Contact> colls = new ArrayList<>();
		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT * from contacts")) 
		{
				ResultSet rs = pst.executeQuery();
			
				while(rs.next())
					colls.add(readContact(rs));
				
				
		}
		
		return colls;
		
	}
	
	
	
	@Override
	public Transaction getTransaction(int id) throws SQLException {
		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT * from transactions where id=?")) 
		{
				pst.setInt(1, id);
				ResultSet rs = pst.executeQuery();
			
				rs.next();
				return readTransaction(rs);
				
				
		}
	
	}
	
	@Override
	public List<Transaction> listTransactions(Contact idct)  throws SQLException {
		List<Transaction> colls = new ArrayList<>();
		
		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT * from transactions, contacts where fk_idcontact=? and contacts.id=transactions.fk_idcontact and contacts.contact_name=?")) 
		{
			pst.setInt(1, idct.getId());
			pst.setString(2, idct.getName());
			ResultSet rs = pst.executeQuery();
			
				while (rs.next()) {
					colls.add(readTransaction(rs));
				}
				logger.trace( colls.size() + " transactions");
		}
		return colls;
	}
	
	
	@Override
	public List<Transaction> listTransactions()  throws SQLException {
		List<Transaction> colls = new ArrayList<>();
		
		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT * from transactions");ResultSet rs = pst.executeQuery()) 
		{
				while (rs.next()) {
					colls.add(readTransaction(rs));
				}
				logger.trace( colls.size() + " transactions");
		}
		return colls;
	}
	
	
	private Contact readContact(ResultSet rs) throws SQLException
	{
		
		var contact = new Contact();
		contact.setId(rs.getInt("id"));
		contact.setName(rs.getString("contact_name"));
		contact.setLastName(rs.getString("contact_lastname"));
		contact.setTelephone(rs.getString("contact_telephone"));
		contact.setEmail(rs.getString("contact_email"));
		contact.setCountry(rs.getString("contact_country"));
		contact.setAddress(rs.getString("contact_address"));
		contact.setWebsite(rs.getString("contact_website"));
		contact.setPassword(rs.getString("contact_password"));
		contact.setZipCode(rs.getString("contact_zipcode"));
		contact.setCity(rs.getString("contact_city"));
		
		contact.setEmailAccept(rs.getBoolean("emailAccept"));
		return contact;
		
	}
	
	
	private Transaction readTransaction(ResultSet rs) throws SQLException {
		var state = new Transaction();
		
		state.setDateProposition(rs.getTimestamp("dateTransaction"));
		state.setId(rs.getInt("id"));
		state.setMessage(rs.getString("message"));
		
		state.setItems(readTransactionItems(rs));
		state.setStatut(STAT.valueOf(rs.getString("statut")));
		state.setTransporter(rs.getString("transporter"));
		state.setShippingPrice(rs.getDouble("shippingPrice"));
		state.setCurrency(rs.getString("currency"));
		state.setContact(getContactById(rs.getInt("fk_idcontact")));
		state.setTransporterShippingCode(rs.getString("transporterShippingCode"));
		
		return state;	
	}
	
	@Override
	public int saveOrUpdateContact(Contact ct) throws SQLException {
		
		
		if (ct.getId() < 0) 
		{
				try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("INSERT INTO contacts (contact_name, contact_lastname, contact_password, contact_telephone, contact_country, contact_address, contact_zipcode, contact_city, contact_website,contact_email, emailAccept) VALUES (?, ?, ?, ?, ?, ?,?,?, ?, ?,?);",Statement.RETURN_GENERATED_KEYS)) 
				{
					pst.setString(1, ct.getName());
					pst.setString(2, ct.getLastName());
					pst.setString(3, IDGenerator.generateSha256(ct.getPassword()));
					pst.setString(4, ct.getTelephone());
					pst.setString(5, ct.getCountry());
					pst.setString(6, ct.getAddress());
					pst.setString(7, ct.getZipCode());
					pst.setString(8, ct.getCity());
					pst.setString(9, ct.getWebsite());
					pst.setString(10, ct.getEmail());
					pst.setBoolean(11,ct.isEmailAccept());
					pst.executeUpdate();
					ct.setId(getGeneratedKey(pst));
					logger.debug("save Contact with id="+ct.getId());
					return ct.getId();
				}	
				
		}
		else
		{

			logger.debug("update Contact " + ct.getId());
			
			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("UPDATE contacts SET contact_name = ?, contact_lastname = ?, contact_telephone = ?, contact_country = ?, contact_address = ?, contact_zipcode=?, contact_city=?, contact_website = ?,contact_email=?,emailAccept=? WHERE contacts.id = ?;",Statement.RETURN_GENERATED_KEYS)) {
				pst.setString(1, ct.getName());
				pst.setString(2, ct.getLastName());
				pst.setString(3, ct.getTelephone());
				pst.setString(4, ct.getCountry());
				pst.setString(5, ct.getAddress());
				pst.setString(6, ct.getZipCode());
				pst.setString(7, ct.getCity());
				pst.setString(8, ct.getWebsite());
				pst.setString(9, ct.getEmail());
				pst.setBoolean(10, ct.isEmailAccept());
				pst.setInt(11, ct.getId());
				
				pst.executeUpdate();
				return ct.getId();
			}
		}
		
	}
	
	
	@Override
	public int saveOrUpdateTransaction(Transaction t) {
		if (t.getId() < 0) 
		{
		
				logger.debug("save transaction ");
				
				try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("INSERT INTO transactions (dateTransaction, message, stocksItem, statut,transporter,shippingPrice,transporterShippingCode, currency, fk_idcontact) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);",Statement.RETURN_GENERATED_KEYS)) {
					pst.setTimestamp(1, new Timestamp(t.getDateProposition().getTime()));
					pst.setString(2, t.getMessage());
					storeTransactionItems(pst,3, t.getItems());			
					pst.setString(4, t.getStatut().name());
					pst.setString(5, t.getTransporter());
					pst.setDouble(6, t.getShippingPrice());
					pst.setString(7, t.getTransporterShippingCode());
					pst.setString(8, t.getConfig().getCurrencyCode());
					pst.setInt(9, t.getContact().getId());
					pst.executeUpdate();
					t.setId(getGeneratedKey(pst));
					
					return t.getId();
					
				} catch (Exception e) {
					logger.error("error insert", e);
					return -1;
				}
		
		}
		else
		{

			logger.debug("update transaction " + t.getId());
			
			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("UPDATE transactions SET statut = ?, transporter=?, shippingPrice=?, transporterShippingCode=?,stocksItem=? WHERE id = ?;",Statement.RETURN_GENERATED_KEYS)) {
				pst.setString(1, t.getStatut().name());
				pst.setString(2, t.getTransporter());
				pst.setDouble(3, t.getShippingPrice());
				pst.setString(4, t.getTransporterShippingCode());
				storeTransactionItems(pst,5, t.getItems());			
				pst.setInt(6, t.getId());
				pst.executeUpdate();
				return t.getId();
				
			} catch (Exception e) {
				logger.error("error update", e);
				return -1;
			}
		}
	}
	
	
	
	@Override
	public void deleteTransaction(Transaction t) throws SQLException {
		logger.debug("delete " + t );
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("DELETE FROM transactions where id=?")) {
			pst.setInt(1, t.getId());
			pst.executeUpdate();
		}
		
	}
	
	
	@Override
	public void deleteStock(SealedStock state) throws SQLException {
		logger.debug("del " + state.getId() + " in sealed stock");
		var sql = "DELETE FROM sealed WHERE id=?";
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(sql)) 
		{
			pst.setInt(1, state.getId());
			pst.executeUpdate();
		}
		
	}
	
	@Override
	public List<SealedStock> listSeleadStocks() throws SQLException {
		List<SealedStock> colls = new ArrayList<>();
		
		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT * from sealed");ResultSet rs = pst.executeQuery()) 
		{
				while (rs.next()) {
					var state = new SealedStock();
					
					state.setComment(rs.getString("comment"));
					state.setId(rs.getInt("id"));
					state.setQte(rs.getInt("qte"));
					state.setCondition(EnumStock.valueOf(rs.getString("conditionProduct")));
					var p = new Packaging();
					 		  p.setLang(rs.getString("lang"));
							  p.setType(Packaging.TYPE.valueOf(rs.getString("typeProduct")));
							  try 
							  {
								p.setEdition(getEnabledPlugin(MTGCardsProvider.class).getSetById(rs.getString(EDITION)));
							  } 
							  catch (IOException e) 
							  {
								logger.error(e);
								throw new SQLException(e);
							  }
							
							  p.setUrl(PackagesProvider.inst().getURLFor(p));
							  
							  
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
			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(
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
			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(
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
				logger.error("error update",e);
			}
		}
		notify(state);
		
	}
	
	@Override
	public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException {
		logger.debug("saving " + mc + " in " + collection);

		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("insert into cards values (?,?,?,?,?)")) {
			pst.setString(1, IDGenerator.generate(mc));
			storeCard(pst, 2, mc);
			pst.setString(3, mc.getCurrentSet().getId());
			pst.setString(4, getEnabledPlugin(MTGCardsProvider.class).toString());
			pst.setString(5, collection.getName());
			pst.executeUpdate();
		}
	}

	@Override
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
		logger.debug("delete " + mc + " in " + collection);
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("DELETE FROM cards where id=? and edition=? and collection=?")) {
			pst.setString(1, IDGenerator.generate(mc));
			pst.setString(2, mc.getCurrentSet().getId());
			pst.setString(3, collection.getName());
			pst.executeUpdate();
		}
	}
	
	@Override
	public void moveCard(MagicCard mc, MagicCollection from, MagicCollection to) throws SQLException {
		logger.debug("move " + mc+ " s=" + from + " d=" + to);
		
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("update cards set collection= ? where id=? and collection=?")) 
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
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT mcard FROM cards"); ResultSet rs = pst.executeQuery();) {
			while (rs.next()) {
				listCards.add(readCard(rs));
			}
		}
		return listCards;
	}

	@Override
	public Map<String, Integer> getCardsCountGlobal(MagicCollection col) throws SQLException {
		Map<String, Integer> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		Chrono ch = new Chrono();
		ch.start();
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT edition, count(1) FROM cards where collection=? group by edition");) {
			pst.setString(1, col.getName());
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next())
					map.put(rs.getString(1), rs.getInt(2));
			}
		}
		logger.debug("getCardsCountGlobal(\""+col+"\") calcuation done in " + ch.stopInMillisecond()/1000+"s");
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

		try (var c = pool.getConnection();Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql);) {
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

		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(sql)) {
			pst.setString(1, collection.getName());
			if (me != null)
				pst.setString(2, me.getId());
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					MagicCard mc = readCard(rs);
					ret.add(mc);
					notify(mc);
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
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(sql)) {
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
		
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM collections where name= ?")) {
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
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("insert into collections values (?)")) {
			pst.setString(1, col.getName().replace("'", "\'"));
			pst.executeUpdate();
		}
	}

	@Override
	public void removeCollection(MagicCollection col) throws SQLException {

		if (col.getName().equals(MTGControler.getInstance().get("default-library")))
			throw new SQLException(col.getName() + " can not be deleted");

		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("DELETE FROM collections where name = ?")) {
			pst.setString(1, col.getName());
			pst.executeUpdate();
		}

		try (var c = pool.getConnection(); PreparedStatement pst2 = c.prepareStatement("DELETE FROM cards where collection = ?")) {
			pst2.setString(1, col.getName());
			pst2.executeUpdate();
		}
	}

	@Override
	public List<MagicCollection> listCollections() throws SQLException {
		List<MagicCollection> colls = new ArrayList<>();
		try (var cont =  pool.getConnection();PreparedStatement pst = cont.prepareStatement("SELECT * FROM collections")) 
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
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("DELETE FROM cards where edition=? and collection=?")) {
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
		
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT collection FROM cards WHERE id=? and edition=?")) {
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
		try (var c = pool.getConnection();Statement pst = c.createStatement()) {
			pst.executeUpdate(sql);
		}
	}
	
	
	

	@Override
	public List<MagicCardStock> listStocks(MagicCard mc, MagicCollection col,boolean editionStrict) throws SQLException {
		
		String sql = createListStockSQL();
		
		if(editionStrict)
			sql ="SELECT * FROM stocks where collection=? and idmc=?";
		
		logger.trace("sql="+sql);
		
		List<MagicCardStock> colls = new ArrayList<>();
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(sql)) {
			pst.setString(1, col.getName());
			
			if(editionStrict)
				pst.setString(2, IDGenerator.generate(mc));
			else if (!isJsonCompatible())
				pst.setString(2, "%"+mc.getName()+"%");
			else
				pst.setString(2, mc.getName());
			
			try (ResultSet rs = pst.executeQuery()) {
				
				while (rs.next()) {
					var state = readStock(rs);
					colls.add(state);
				}
				logger.trace("loading " + colls.size() + " item FROM stock for " + mc);
				
			}

		}
		return colls;

	}


	public List<MagicCardStock> listStocks(List<MagicCollection> cols) throws SQLException {
		List<MagicCardStock> colls = new ArrayList<>();
		
		var stmt = String.format("SELECT * FROM stocks where collection in  (%s)",cols.stream().map(c->"'"+c.getName()+"'").collect(Collectors.joining(", ")));
		logger.trace("loading stock with SQL=" + stmt);
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(stmt); ResultSet rs = pst.executeQuery();) {
			while (rs.next()) {
				var state = readStock(rs);
				colls.add(state);
			}
			logger.debug("load " + colls.size() + " item(s) from stock for " + cols);
		}
		return colls;
	}
	
	
	@Override
	public MagicCardStock getStockById(Integer id) throws SQLException {
		
		
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM stocks where idstock=?")) {
			pst.setInt(1, id);
			var rs = pst.executeQuery();
			
			rs.next();
			
			return readStock(rs);
		}
	}
	
	private MagicCardStock readStock(ResultSet rs) throws SQLException
	{
		var state = new MagicCardStock();
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
			state.setGrade(readGrading(rs));
			state.setTiersAppIds(readTiersApps(rs));
			state.setEtched(rs.getBoolean("etched"));
			
			if(state.getTiersAppIds()==null)
				state.setTiersAppIds(new HashMap<>());

		
		
		return state;

	}
	
	
	public List<MagicCardStock> listStocks() throws SQLException {
		List<MagicCardStock> colls = new ArrayList<>();
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM stocks"); ResultSet rs = pst.executeQuery();) {
			while (rs.next()) {
				var state = readStock(rs);
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
			
		} catch (Exception e) {
			logger.error("couldn't retrieve id", e);
		}
		return -1;

	}

	@Override
	public void saveOrUpdateStock(MagicCardStock state) throws SQLException {

		if (state.getIdstock() < 0) {
			logger.debug("save stock " + state);
			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(
					"insert into stocks  ( conditions,foil,signedcard,langage,qte,comments,idmc,collection,mcard,altered,price,grading,tiersAppIds,etched) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
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
				storeGrade(pst,12, state.getGrade());
				storeTiersApps(pst,13, state.getTiersAppIds());
				pst.setBoolean(14, state.isEtched());
				pst.executeUpdate();
				state.setIdstock(getGeneratedKey(pst));
			} catch (Exception e) {
				logger.error(e);
			}
		} else {
			logger.debug("update Stock " + state);
			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(
					"update stocks set comments=?, conditions=?, foil=?,signedcard=?,langage=?, qte=? ,altered=?,price=?,idmc=?,collection=?,grading=?,tiersAppIds=?,etched=? where idstock=?")) {
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
				storeGrade(pst, 11,state.getGrade());
				storeTiersApps(pst, 12,state.getTiersAppIds());
				pst.setBoolean(13, state.isEtched());
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

		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM alerts")) {
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					MagicCardAlert alert = new MagicCardAlert();
					alert.setCard(readCard(rs));
					alert.setId(rs.getString("id"));
					alert.setQty(rs.getInt("qte"));
					alert.setPrice(rs.getDouble("amount"));
					alert.setFoil(rs.getBoolean("foil"));
					listAlerts.put(alert.getId(),alert);
				}
			}
		} catch (Exception e) {
			logger.error("error get alert",e);
		}
	}


	@Override
	public void saveAlert(MagicCardAlert alert) throws SQLException {

		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("insert into alerts ( id,mcard,amount,qte) values (?,?,?,?)")) {
			
			alert.setId(IDGenerator.generate(alert.getCard()));
			
			pst.setString(1, alert.getId());
			storeCard(pst, 2, alert.getCard());
			pst.setDouble(3, alert.getPrice());
			pst.setInt(4, alert.getQty());
			pst.executeUpdate();
			logger.debug("save alert for " + alert.getCard()+ " ("+alert.getCard().getCurrentSet()+")");
			listAlerts.put(alert.getId(),alert);
		}
	}

	@Override
	public void updateAlert(MagicCardAlert alert) throws SQLException {
		logger.debug("update alert " + alert);
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("update alerts set amount=?,mcard=?,foil=?, qte=? where id=?")) {
			pst.setDouble(1, alert.getPrice());
			storeCard(pst, 2, alert.getCard());
			pst.setBoolean(3, alert.isFoil());
			pst.setInt(4, alert.getQty());
			pst.setString(5, alert.getId());
			pst.executeUpdate();
		}

	}

	@Override
	public void deleteAlert(MagicCardAlert alert) throws SQLException 
	{
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("DELETE FROM alerts where id=?")) {
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
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM news")) {
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					MagicNews n = new MagicNews();
					n.setCategorie(rs.getString("categorie"));
					n.setName(rs.getString("name"));
					n.setUrl(rs.getString("url"));
					n.setId(rs.getInt("id"));
					n.setProvider(getPlugin(rs.getString("typeNews"),MTGNewsProvider.class));
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
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("DELETE FROM news where id=?")) {
			pst.setInt(1, n.getId());
			pst.executeUpdate();
		}
	}

	@Override
	public void saveOrUpdateNews(MagicNews n) throws SQLException {
		if (n.getId() < 0) {

			logger.debug("save " + n);
			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(
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
			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("update news set name=?, categorie=?, url=?,typeNews=? where id=?")) {
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
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM orders"); ResultSet rs = pst.executeQuery();) 
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
			if(rs.getString(EDITION)==null)
			{
				state.setEdition(new MagicEdition("pMEI",""));
				return;
			}
			
			
			state.setEdition(getEnabledPlugin(MTGCardsProvider.class).getSetById(rs.getString(EDITION)));
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
		try (var c = pool.getConnection();Statement pst = c.createStatement()) {
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
			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(
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
			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(
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
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("UPDATE cards SET mcard= ? WHERE id = ? and collection = ?"))
		{
			
			storeCard(pst, 1, newC);
			pst.setString(2, IDGenerator.generate(card));
			pst.setString(3, col.getName());
			pst.executeUpdate();
		}
		
	}
	

	@Override
	public void executeQuery(String query) throws SQLException {
		try (var c = pool.getConnection(); Statement pst = c.createStatement())
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
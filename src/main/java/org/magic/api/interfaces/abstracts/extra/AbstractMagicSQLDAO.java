package org.magic.api.interfaces.abstracts.extra;

import static org.magic.services.tools.MTG.getEnabledPlugin;
import static org.magic.services.tools.MTG.getPlugin;

import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.jooq.SQLDialect;
import org.magic.api.beans.MTGAlert;
import org.magic.api.beans.MTGAnnounce;
import org.magic.api.beans.MTGAnnounce.STATUS;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGGrading;
import org.magic.api.beans.MTGNews;
import org.magic.api.beans.MTGSealedStock;
import org.magic.api.beans.abstracts.AbstractAuditableItem;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExtra;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.EnumPaymentProvider;
import org.magic.api.beans.enums.EnumTransactionDirection;
import org.magic.api.beans.enums.EnumTransactionStatus;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.GedEntry;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.beans.technical.audit.DAOInfo;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGNewsProvider;
import org.magic.api.interfaces.MTGPool;
import org.magic.api.interfaces.MTGSealedProvider;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractMagicDAO;
import org.magic.api.interfaces.abstracts.AbstractTechnicalServiceManager;
import org.magic.api.interfaces.extra.MTGSerializable;
import org.magic.api.pool.impl.NoPool;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;
import org.magic.services.tools.CryptoUtils;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.SQLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public abstract class AbstractMagicSQLDAO extends AbstractMagicDAO {

	protected int generatedKey = Statement.RETURN_GENERATED_KEYS;
	private static final String UPDATED = "{} updated";
	private static final String EXTRATYPE = "extra";
	private static final String COLLECTION = "collection";
	protected static final String MCARD = "mcard";
	private static final String DEFAULT_LIBRARY = "default-library";
	private static final String EDITION = "edition";
	protected MTGPool pool;

	protected abstract String getjdbcnamedb();
	protected abstract String getdbSizeQuery();

	protected abstract SQLDialect getDialect();
	
	private SQLTools hlper;
	
	
	protected AbstractMagicSQLDAO() {
		super();
		 hlper = new SQLTools(getDialect());
	}
	
	
	private List<MTGStockItem> readStockItemFrom(ResultSet rs,String field) throws SQLException {
		return serialiser.fromJsonList(rs.getString(field), MTGStockItem.class);
	}

	protected void storeTransactionItems(PreparedStatement pst, int position, List<MTGStockItem> grd) throws SQLException {
		pst.setString(position, serialiser.toJsonElement(grd).toString());

	}

	protected MTGGrading readGrading(ResultSet rs) throws SQLException {
		return serialiser.fromJson(rs.getString("grading"), MTGGrading.class);
	}


	protected void storeGrade(PreparedStatement pst, int position, MTGGrading grd) throws SQLException {
		pst.setString(position, serialiser.toJsonElement(grd).toString());
	}

	protected Map<MTGCard, Integer> readDeckBoard(ResultSet rs, String field) throws SQLException {

		Map<MTGCard, Integer> ret = new HashMap<>();
		serialiser.fromJson(rs.getString(field), JsonArray.class).forEach(je->{
			var mc = serialiser.fromJson(je.getAsJsonObject().get("card").toString(), MTGCard.class);
			var qte = je.getAsJsonObject().get("qty").getAsInt();
			ret.put(mc, qte);
		});

		return ret;
	}

	protected void storeDeckBoard(PreparedStatement pst, int i, Map<MTGCard, Integer> board) throws SQLException {

		var arr = new JsonArray();

		board.entrySet().forEach(e->{

			var obj = new JsonObject();
			obj.addProperty("qty", e.getValue());
			obj.add("card", serialiser.toJsonElement(e.getKey()));
			arr.add(obj);

		});

		pst.setString(i, arr.toString());
	}

	@SuppressWarnings("unchecked")
	protected Map<String, String> readTiersApps(ResultSet rs) throws SQLException {
		return serialiser.fromJson(rs.getString("tiersAppIds"), Map.class);
	}

	protected void storeTiersApps(PreparedStatement pst, int i, Map<String, String> tiersAppIds) throws SQLException {
		pst.setString(i, serialiser.toJsonElement(tiersAppIds).toString());
	}

	protected void storeCard(PreparedStatement pst, int position, MTGCard mc) throws SQLException {
		pst.setString(position, serialiser.toJsonElement(mc).toString());
	}

	protected MTGCard readCard(ResultSet rs,String field) throws SQLException {
		
		try{
			return serialiser.fromJson( rs.getString(field), MTGCard.class);
		}
		catch(NullPointerException e)
		{
			return null;
		}
	}


	protected boolean enablePooling()
	{
		return true;
	}


	private boolean createDB() throws SQLException {
		
		try (var cont =  pool.getConnection();Statement stat = cont.createStatement()) {
			
			
			stat.executeUpdate(hlper.createTableCollections());
			logger.debug("Create table collections");

			stat.executeUpdate(hlper.createTableStocks());
			logger.debug("Create table stocks");
			
			stat.executeUpdate(hlper.createTableGed());
			logger.debug("Create table ged");

			stat.executeUpdate(hlper.createTableAnnounces());
			logger.debug("Create table announces");

			stat.executeUpdate(hlper.createTableTransactions());
			logger.debug("Create table transactions");

			stat.executeUpdate(hlper.createTableContacts());
			logger.debug("Create table contacts");
				
			stat.executeUpdate(hlper.createTableAlerts());
			logger.debug("Create table alerts");

			stat.executeUpdate(hlper.createTableNews());
			logger.debug("Create table news");

			stat.executeUpdate(hlper.createTableSealed());
			logger.debug("Create table selead");

			stat.executeUpdate(hlper.createTableDecks());
			logger.debug("Create table decks");

			stat.executeUpdate(hlper.createTableTechnicalAudit());
			logger.debug("Create table technicalAuditLog");
			
			
			postCreation(stat);

		

			createIndex(stat);


			return true;
		} catch (SQLIntegrityConstraintViolationException e) {
			logger.debug("database already created");
			return false;
		}
		catch (Exception e) {
			logger.trace("Error in createDB : {}", e.getMessage());
			return false;
		}
	}

	@Override
	public <T extends MTGSerializable> boolean storeEntry(GedEntry<T> gedItem) throws SQLException {
		try (var c = pool.getConnection(); var pst = c.prepareStatement("INSERT INTO ged (creationDate, className, idInstance, fileContent,fileName, md5) VALUES (?, ?, ?, ?,?,?)"))
		{
				pst.setTimestamp(1, new Timestamp(Instant.now().toEpochMilli()));
				pst.setString(2, gedItem.getClasse().getCanonicalName());
				pst.setString(3, gedItem.getId());
				pst.setString(4, CryptoUtils.toBase64(gedItem.getContent()));
				pst.setString(5, gedItem.getName());
				pst.setString(6, CryptoUtils.getMD5(gedItem.getContent()));
				executeUpdate(pst,false);
				return true;

		}
	}

	@Override
	public <T extends MTGSerializable> List<GedEntry<T>> listAllEntries() throws SQLException {
		try (var c = pool.getConnection(); var pst = c.prepareStatement("SELECT className,idInstance,fileName from ged"))
		{
				var rs = executeQuery(pst);

				var arr = new ArrayList<GedEntry<T>>();
					while(rs.next())
					{
						arr.add(readEntry(rs.getString("className"),rs.getString("idInstance"),rs.getString("fileName")));
					}
				return arr;
		}
	}



	@Override
	public <T extends MTGSerializable> List<GedEntry<T>> listEntries(String classename, String id) throws SQLException {
		try (var c = pool.getConnection();var pst = c.prepareStatement("SELECT fileName from ged where className = ? and IdInstance = ?"))
		{
				pst.setString(1, classename);
				pst.setString(2,id);
				var rs = executeQuery(pst);

				var arr = new ArrayList<GedEntry<T>>();

					while(rs.next())
					{
						var entry = new GedEntry<T>();
						entry.setId(id);
						entry.setClasse(PluginRegistry.inst().loadClass(classename));
						entry.setName(rs.getString("fileName"));
						arr.add(entry);
					}
			return arr;
		} catch (ClassNotFoundException e) {
			logger.error(e);
			throw new SQLException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends MTGSerializable> GedEntry<T> readEntry(String classename, String idInstance, String fileName) throws SQLException {
		var ged = new GedEntry<T>();
		try (var c = pool.getConnection();var pst = c.prepareStatement("SELECT fileContent, md5 from ged where className = ? and IdInstance = ? and fileName= ?"))
		{
				pst.setString(1, classename);
				pst.setString(2,idInstance);
				pst.setString(3,fileName);
				var rs = executeQuery(pst);
				rs.next();
				ged.setId(idInstance);
				ged.setName(fileName);
				ged.setContent(CryptoUtils.fromBase64(rs.getString("fileContent")));

				if(rs.getString("md5")!=null && !CryptoUtils.getMD5(ged.getContent()).equals(rs.getString("md5")))
					throw new SQLException("MD5 Error for " + fileName +" : " + CryptoUtils.getMD5(ged.getContent()) + " " + rs.getString("md5"));

				ged.setIsImage(ImageTools.isImage(ged.getContent()));
		}

		try {
				ged.setClasse(PluginRegistry.inst().loadClass(classename));
			} catch (ClassNotFoundException e) {
				logger.error(e);
			}

			notify(ged);

			return ged;

	}




	@Override
	public <T extends MTGSerializable> boolean deleteEntry(GedEntry<T> gedItem) throws SQLException {
		try (var c = pool.getConnection();var pst = c.prepareStatement("DELETE FROM ged where className = ? and IdInstance = ? and fileName= ?"))
		{
				pst.setString(1, gedItem.getClasse().getCanonicalName());
				pst.setString(2,gedItem.getId());
				pst.setString(3,gedItem.getName());
				executeUpdate(pst,false);
				return true;
		}
	}





	@Override
	public MTGAnnounce getAnnounceById(int id) throws SQLException {

		try (var c = pool.getConnection();var pst = c.prepareStatement("SELECT * from announces where id=?"))
		{
				pst.setInt(1, id);
				var rs = executeQuery(pst);
				rs.next();
				return readAnnounce(rs);

		}
	}

	@Override
	public List<MTGAnnounce> listAnnounces(int max,STATUS stat) throws SQLException {
		var colls = new ArrayList<MTGAnnounce>();

		var sql = "SELECT * from announces where statusAnnounce=?  ORDER BY id DESC";

		if(stat==null)
			sql = "SELECT * from announces ORDER BY id DESC";

		try (var c = pool.getConnection();var pst = c.prepareStatement(sql))
		{

			if(stat!=null)
				pst.setString(1, stat.name());


				if(max>0)
					pst.setMaxRows(max);

				var rs = executeQuery(pst);

				while (rs.next()) {
					MTGAnnounce d = readAnnounce(rs);
					colls.add(d);
					notify(d);
				}
		}
		return colls;
	}

	@Override
	public int saveOrUpdateAnnounce(MTGAnnounce n) throws SQLException {
		if (n.getId() < 0)
		{
				try (var c = pool.getConnection(); var pst = c.prepareStatement("INSERT INTO announces (creationDate, startDate, endDate, title, description, total, currency, stocksItem, typeAnnounce, fk_idcontact,category,percentReduction,conditions,statusAnnounce) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",generatedKey))
				{
					pst.setTimestamp(1,new Timestamp(Instant.now().toEpochMilli()));
					pst.setTimestamp(2,new Timestamp(n.getStartDate().getTime()));
					pst.setTimestamp(3,new Timestamp(n.getEndDate().getTime()));
					pst.setString(4, n.getTitle());
					pst.setString(5, n.getDescription());
					pst.setDouble(6, n.getTotalPrice());
					pst.setString(7, n.getCurrency().getCurrencyCode());
					storeTransactionItems(pst,8, n.getItems());
					pst.setString(9, n.getType().name());
					pst.setInt(10, n.getContact().getId());
					pst.setString(11, n.getCategorie().name());
					pst.setDouble(12, n.getPercentReduction());
					pst.setString(13, n.getCondition().name());
					pst.setString(14, n.getStatus().name());
					executeUpdate(pst,false);
					n.setId(getGeneratedKey(pst));
					logger.debug("{} created",n);

				}
		}
		else
		{
				try (var c = pool.getConnection(); var pst = c.prepareStatement("UPDATE announces SET startDate = ?, endDate =?,title=?,  description = ?, total = ?, currency = ?, stocksItem = ?, typeAnnounce = ?, fk_idcontact = ?, category=? , percentReduction=?, conditions=?, statusAnnounce=? WHERE id = ?;"))
				{
					pst.setTimestamp(1, new Timestamp(n.getStartDate().getTime()));
					pst.setTimestamp(2,new Timestamp(n.getEndDate().getTime()));
					pst.setString(3, n.getTitle());
					pst.setString(4, n.getDescription());
					pst.setDouble(5, n.getTotalPrice());
					pst.setString(6, n.getCurrency().getCurrencyCode());
					storeTransactionItems(pst,7, n.getItems());
					pst.setString(8, n.getType().name());
					pst.setInt(9, n.getContact().getId());
					pst.setString(10, n.getCategorie().name());
					pst.setDouble(11, n.getPercentReduction());
					pst.setString(12, n.getCondition().name());
					pst.setString(13, n.getStatus().name());
					pst.setInt(14, n.getId());
					executeUpdate(pst,false);
					logger.debug(UPDATED,n);
				}

		}
		n.setUpdated(false);
		return n.getId();


	}
	
	@Override
	public void deleteAnnounceById(int id) throws SQLException {
		try (var c = pool.getConnection(); var pst = c.prepareStatement("DELETE FROM announces where id=?")) {
			pst.setInt(1, id);
			executeUpdate(pst,false);
		}
	}
	
	protected void postCreation(Statement stat) throws SQLException
	{
		logger.debug("populate default datas");
		stat.executeUpdate(hlper.insertDefaultCollections());	
		stat.executeUpdate(hlper.insertMainContact());
	}


	private void createIndex(Statement stat) throws SQLException {
		
		for(var c : new String[] {"idmc","idMe","collection","comments","conditions","langage","name"})
			stat.executeUpdate(hlper.createIndex("stocks",c));
		
		for(var c : new String[] {"name","url","categorie","typeNews"})
			stat.executeUpdate(hlper.createIndex("news",c));

		for(var c : new String[] {"edition","comment","lang","typeProduct","conditionProduct","extra"})
			stat.executeUpdate(hlper.createIndex("sealed",c));

		for(var c : new String[] {"statut","message","transporter"})
			stat.executeUpdate(hlper.createIndex("transactions",c));
		
		for(var c : new String[] {"contact_name","contact_lastname","contact_country","contact_address","contact_zipcode","contact_city","contact_website","contact_email"})
			stat.executeUpdate(hlper.createIndex("contacts",c));

		for(var c : new String[] {"name","tags"})
			stat.executeUpdate(hlper.createIndex("decks",c));

		for(var c : new String[] {"creationDate","startDate","endDate","title","currency","typeAnnounce","category","conditions","statusAnnounce"})
			stat.executeUpdate(hlper.createIndex("announces",c));

		
		stat.executeUpdate(hlper.createIndex("alerts","id"));
		
		stat.executeUpdate(hlper.createIndex("technicalauditlog","classname"));
				
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
	public Map<String, MTGProperty> getDefaultAttributes() {
		var map = new HashMap<String,MTGProperty>();

		map.put(SERVERNAME,   new MTGProperty("localhost","server name or ip where database is stored"));
		map.put(SERVERPORT, MTGProperty.newIntegerProperty("", "listening port of the database",1024,65535));
		map.put(DB_NAME, new MTGProperty("mtgdesktopclient","database name"));
		map.put(LOGIN, new MTGProperty("login","user allowed to connect to the database"));
		map.put(PASS, new MTGProperty("pass","password of the connected user"));
		map.put(PARAMS, new MTGProperty("","JDBC parameters append to the url"));
		return map;
	}

	@Override
	public String getDBLocation() {
		return getString(SERVERNAME) + "/" + getString(DB_NAME);
	}


	@Override
	public String getVersion() {
		try {
			var d = DriverManager.getDriver(getjdbcUrl());
			return d.getMajorVersion()+"."+d.getMinorVersion();
		} catch (SQLException e) {
			return "1.0";
		}
	}

	@Override
	public Map<String,Long> getDBSize() {
		String sql = getdbSizeQuery();
		var map = new HashMap<String,Long>();

		if(sql==null)
			return map;


		try (var c = pool.getConnection(); var pst = c.prepareStatement(sql); var rs = executeQuery(pst);) {

			while(rs.next())
			{
				map.put(rs.getString(1), rs.getLong(2));
			}
			return map;
		} catch (SQLException e) {
			logger.error(e);
			return map;
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
		if(pool==null || !enablePooling())
		{
			pool=new NoPool();
			logger.error("Use default pool : {}",pool);
		}
		logger.info("Loading SQL connection to : {} with pool : {}",getjdbcUrl(),pool);
		pool.init(getjdbcUrl(),getString(LOGIN), getString(PASS));
		createDB();
	}



	@Override
	public void init() throws SQLException {
		logger.info("init {}",getName());
		init(getEnabledPlugin(MTGPool.class));
	}


	@Override
	public List<MTGDeck> listDecks() throws SQLException {
		var colls = new ArrayList<MTGDeck>();

		try (var c = pool.getConnection();var pst = c.prepareStatement("SELECT * from decks order by ID DESC"))
		{
				var rs = executeQuery(pst);

				while (rs.next()) {

						MTGDeck d = readDeck(rs);
						colls.add(d);
						notify(d);

				}
		}
		return colls;
	}

	@Override
	public MTGDeck getDeckById(Integer id) throws SQLException {
		try (var c = pool.getConnection();var pst = c.prepareStatement("SELECT * from decks where id=?"))
		{
				pst.setInt(1, id);
				ResultSet rs = executeQuery(pst);

				rs.next();
				return readDeck(rs);

		}
	}


	@Override
	public Integer saveOrUpdateDeck(MTGDeck d) throws SQLException {
		if (d.getId() < 0)
		{
				try (var c = pool.getConnection(); var pst = c.prepareStatement("INSERT INTO decks (description, name, dateCreation, dateUpdate, tags, commander, main, sideboard, averagePrice) VALUES (?,?,?,?,?,?,?,?,?)",generatedKey))
				{
					pst.setString(1, d.getDescription());
					pst.setString(2, d.getName());
					pst.setDate(3,  new Date(System.currentTimeMillis()));
					pst.setDate(4, new Date(System.currentTimeMillis()));
					pst.setString(5, d.getTags().stream().collect(Collectors.joining("|")));
					storeCard(pst,6,d.getCommander());
					storeDeckBoard(pst,7,d.getMain());
					storeDeckBoard(pst,8,d.getSideBoard());
					pst.setDouble(9, d.getAveragePrice());
					executeUpdate(pst,false);
					d.setId(getGeneratedKey(pst));
				}
				logger.debug("{} saved with id={}",d.getName(),d.getId());

		}
		else
		{
			try (var c = pool.getConnection(); var pst = c.prepareStatement("UPDATE decks SET description = ?, name = ?, dateUpdate=?, tags= ?, commander= ?, main= ?, sideboard= ?, averagePrice= ? WHERE id= ?"))
			{
				pst.setString(1, d.getDescription());
				pst.setString(2, d.getName());
				pst.setDate(3,  new Date(System.currentTimeMillis()));
				pst.setString(4, d.getTags().stream().collect(Collectors.joining("|")));
				storeCard(pst,5,d.getCommander());
				storeDeckBoard(pst,6,d.getMain());
				storeDeckBoard(pst,7,d.getSideBoard());
				pst.setDouble(8, d.getAveragePrice());
				pst.setInt(9,d.getId());
				executeUpdate(pst,false);
			}
			logger.debug(UPDATED,d.getName());
		}

		return d.getId();
	}

	@Override
	public void deleteDeck(MTGDeck d) throws SQLException {
		try (var c = pool.getConnection(); var pst = c.prepareStatement("DELETE FROM decks where id=?")) {
			pst.setInt(1, d.getId());
			executeUpdate(pst,false);
		}
		logger.debug("Deck {} deleted",d.getName());
	}




	@Override
	public Contact getContactByLogin(String email, String password) throws SQLException {
		try (var c = pool.getConnection();var pst = c.prepareStatement("SELECT * from contacts where contact_email=? and contact_active=?"))
		{
				pst.setString(1, email);
				pst.setBoolean(2, true);
				ResultSet rs = executeQuery(pst);
				var res = rs.next();

				if(!res)
					throw new SQLException("No result Found");

				
				if(CryptoUtils.verifyPassword(password, rs.getString("contact_password")))
						return readContact(rs);
				else
						throw new SQLException("Password doesn't match");
		}
	}

	@Override
	public Contact getContactByEmail(String email) throws SQLException {
		try (var c = pool.getConnection();var pst = c.prepareStatement("SELECT * from contacts where contact_email=? and contact_active=?"))
		{
				pst.setString(1, email);
				pst.setBoolean(2, true);
				ResultSet rs = executeQuery(pst);
				rs.next();

				return readContact(rs);
		}
		catch(SQLException sqlde)
		{
			return null;
		}

	}



	@Override
	public Contact getContactById(int id) throws SQLException {

		try {
			return listContacts.get(String.valueOf(id), new Callable<Contact>() {

				@Override
				public Contact call() throws Exception {
					try (var c = pool.getConnection();var pst = c.prepareStatement("SELECT * from contacts where contact_id=?"))
					{
							pst.setInt(1, id);
							ResultSet rsC = executeQuery(pst);
							rsC.next();
							return readContact(rsC);
					}
				}
			});
		} catch (ExecutionException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public List<Contact> listContacts() throws SQLException {
		List<Contact> cts = new ArrayList<>();
		try (var c = pool.getConnection();var pst = c.prepareStatement(hlper.selectAll("contacts")))
		{
				var rs = executeQuery(pst);

				while(rs.next())
				{
					cts.add(readContact(rs));
				}
		}

		return cts;

	}



	@Override
	public Transaction getTransaction(Long id) throws SQLException {
		try (var c = pool.getConnection();var pst = c.prepareStatement("SELECT * from transactions,contacts where id=? and transactions.fk_idcontact = contacts.contact_id"))
		{
				pst.setLong(1, id);
				var rs = executeQuery(pst);

				rs.next();
				return readTransaction(rs);


		}

	}

	@Override
	public List<Transaction> listTransactions(Contact idct)  throws SQLException {
		List<Transaction> ctx = new ArrayList<>();

		try (var c = pool.getConnection();var pst = c.prepareStatement("SELECT * from transactions,contacts where fk_idcontact=?  and transactions.fk_idcontact = contacts.contact_id"))
		{
			pst.setInt(1, idct.getId());
			var rs = executeQuery(pst);

				while (rs.next()) {
					ctx.add(readTransaction(rs));
				}
		}
		return ctx;
	}


	@Override
	public List<Transaction> listTransactions()  throws SQLException {
		List<Transaction> ctx  = new ArrayList<>();

		try (var c = pool.getConnection();var pst = c.prepareStatement("SELECT * from transactions, contacts where fk_idcontact=contacts.contact_id");ResultSet rs = executeQuery(pst))
		{
				while (rs.next()) {
					ctx.add(readTransaction(rs));
				}
		}
		return ctx;
	}


	private Contact readContact(ResultSet rs) throws SQLException
	{

		var contact = new Contact();
		contact.setId(rs.getInt("contact_id"));
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
		contact.setActive(rs.getBoolean("contact_active"));
		contact.setEmailAccept(rs.getBoolean("emailAccept"));
		notify(contact);
		return contact;

	}


	private MTGDeck readDeck(ResultSet rs) throws SQLException{

		var deck = new MTGDeck();

		deck.setId(rs.getInt("id"));
		deck.setName(rs.getString("name"));
		deck.setAveragePrice(rs.getDouble("averagePrice"));
		deck.setCommander(readCard(rs,"commander"));
		deck.setCreationDate(rs.getDate("dateCreation"));
		deck.setDateUpdate(rs.getDate("dateUpdate"));
		deck.setDescription(rs.getString("description"));

		deck.setMain(readDeckBoard(rs, "main"));
		deck.setSideBoard(readDeckBoard(rs, "sideboard"));

		if(rs.getString("tags")!=null && !rs.getString("tags").isEmpty())
			deck.setTags(Arrays.asList(rs.getString("tags").split("\\|")));

		return deck;

	}

	private Transaction readTransaction(ResultSet rs) throws SQLException {
		var state = new Transaction();

		state.setDateCreation(rs.getTimestamp("dateTransaction"));
		state.setId(rs.getInt("id"));
		state.setMessage(rs.getString("message"));
		state.setSourceShopName(rs.getString("sourceShopName"));
		state.setSourceShopId(rs.getString("sourceShopId"));
		state.setStatut(EnumTransactionStatus.valueOf(rs.getString("statut")));
		state.setItems(readStockItemFrom(rs,"stocksItem"));
		state.setTransporter(rs.getString("transporter"));
		state.setShippingPrice(rs.getDouble("shippingPrice"));
		state.setCurrency(rs.getString("currency"));
		state.setContact(readContact(rs));
		state.setTransporterShippingCode(rs.getString("transporterShippingCode"));
		state.setDatePayment(rs.getTimestamp("datePayment"));
		state.setDateSend(rs.getTimestamp("dateSend"));
		state.setReduction(rs.getDouble("reduction"));
		
		if(rs.getString("typeTransaction")!=null)
			state.setTypeTransaction(EnumTransactionDirection.valueOf(rs.getString("typeTransaction")));
		
		var pp = rs.getString("paymentProvider");
		if(pp!=null)
			state.setPaymentProvider(EnumPaymentProvider.valueOf(pp));



		return state;
	}


	@Override
	public boolean enableContact(String token) throws SQLException {
		try (var c = pool.getConnection();var pst = c.prepareStatement("SELECT * from contacts where temporaryToken=? and contact_active=?"))
		{
				pst.setString(1, token);
				pst.setBoolean(2, false);
				ResultSet rs = executeQuery(pst);
				rs.next();
				var ct= readContact(rs);

				ct.setActive(true);
				ct.setTemporaryToken(null);

				saveOrUpdateContact(ct);

				return true;

		}
		catch(Exception sqlde)
		{
			logger.error(sqlde);
			return false;
		}
	}



	@Override
	public void deleteContact(Contact t) throws SQLException {
		logger.debug("delete Contact {}",t );

		if(listTransactions(t).isEmpty()) {
			try (var c = pool.getConnection(); var pst = c.prepareStatement("DELETE FROM contacts where contact_id=?")) {
				pst.setInt(1, t.getId());
				executeUpdate(pst,false);
			}
		}
		else
		{
			throw new SQLException(t + "{} has transactions and can't be removed ");
		}

	}


	@Override
	public int saveOrUpdateContact(Contact ct) throws SQLException {
		if (ct.getId() < 0)
		{
				try (var c = pool.getConnection(); var pst = c.prepareStatement("INSERT INTO contacts (contact_name, contact_lastname, contact_password, contact_telephone, contact_country, contact_address, contact_zipcode, contact_city, contact_website,contact_email, emailAccept, contact_active,temporaryToken ) VALUES (?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?)",generatedKey))
				{
					pst.setString(1, ct.getName());
					pst.setString(2, ct.getLastName());
					pst.setString(3, CryptoUtils.generatePasswordHash(ct.getPassword()));
					pst.setString(4, ct.getTelephone());
					pst.setString(5, ct.getCountry());
					pst.setString(6, ct.getAddress());
					pst.setString(7, ct.getZipCode());
					pst.setString(8, ct.getCity());
					pst.setString(9, ct.getWebsite());
					pst.setString(10, ct.getEmail());
					pst.setBoolean(11,ct.isEmailAccept());
					pst.setBoolean(12,ct.isActive());
					pst.setString(13,ct.getTemporaryToken());
					executeUpdate(pst,false);
					ct.setId(getGeneratedKey(pst));
					logger.debug("save Contact with id={}",ct.getId());
					return ct.getId();
				}
		}
		else
		{
			logger.debug("update Contact {}",ct.getId());

			try (var c = pool.getConnection(); var pst = c.prepareStatement("UPDATE contacts SET contact_name = ?, contact_lastname = ?, contact_telephone = ?, contact_country = ?, contact_address = ?, contact_zipcode=?, contact_city=?, contact_website = ?,contact_email=?,emailAccept=?, contact_active=?, temporaryToken=? WHERE contacts.contact_id = ?",generatedKey)) {
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
				pst.setBoolean(11, ct.isActive());
				pst.setString(12, ct.getTemporaryToken());
				pst.setInt(13, ct.getId());

				executeUpdate(pst,false);
				return ct.getId();
			}
		}

	}

	

	@Override
	public void changePassword(Contact ct, String newPassword) throws SQLException {
		try (var c = pool.getConnection(); var pst = c.prepareStatement("UPDATE contacts SET contact_password = ? WHERE contacts.contact_id = ?;")) {
			logger.debug("Changing password for {}",ct);
			pst.setString(1, CryptoUtils.generatePasswordHash(newPassword));
			pst.setInt(2, ct.getId());
			executeUpdate(pst,false);
		}

	}



	@Override
	public Long saveOrUpdateTransaction(Transaction t) {
		if (t.getId() < 0)
		{

				logger.debug("saving transaction ");

				try (var c = pool.getConnection(); var pst = c.prepareStatement("INSERT INTO transactions (dateTransaction, message, stocksItem, statut,transporter,shippingPrice,transporterShippingCode, currency,datePayment,dateSend,paymentProvider, fk_idcontact,sourceShopId, sourceShopName,typeTransaction,reduction) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",generatedKey)) {
					pst.setTimestamp(1, new Timestamp(t.getDateCreation().getTime()));
					pst.setString(2, t.getMessage());
					storeTransactionItems(pst,3, t.getItems());
					pst.setString(4, t.getStatut().name());
					pst.setString(5, t.getTransporter());
					pst.setDouble(6, t.getShippingPrice());
					pst.setString(7, t.getTransporterShippingCode());
					pst.setString(8, t.getCurrency().getCurrencyCode());

					if(t.getDatePayment()!=null)
						pst.setTimestamp(9, new Timestamp(t.getDatePayment().getTime()));
					else
						pst.setTimestamp(9, null);

					if(t.getDateSend()!=null)
						pst.setTimestamp(10, new Timestamp(t.getDateSend().getTime()));
					else
						pst.setTimestamp(10, null);

					if(t.getPaymentProvider()!=null)
						pst.setString(11, t.getPaymentProvider().name());
					else
						pst.setString(11, null);

					pst.setInt(12, t.getContact().getId());
					pst.setString(13, t.getSourceShopId());
					pst.setString(14, t.getSourceShopName());
					pst.setString(15, t.getTypeTransaction().name());
					pst.setDouble(16, t.getReduction());
					executeUpdate(pst,false);
					t.setId(getGeneratedKey(pst));

					return t.getId();

				} catch (Exception e) {
					logger.error("error insert", e);
					return -1L;
				}

		}
		else
		{

			logger.debug("update transaction {}",t.getId());

			try (var c = pool.getConnection(); var pst = c.prepareStatement("UPDATE transactions SET statut = ?, transporter=?, shippingPrice=?, transporterShippingCode=?,stocksItem=?,datePayment=?,dateSend=?,paymentProvider=?, sourceShopId=?, sourceShopName=?, reduction=?, typeTransaction=? ,fk_idcontact=?,dateTransaction=? WHERE id = ?",generatedKey)) {
				pst.setString(1, t.getStatut().name());
				pst.setString(2, t.getTransporter());
				pst.setDouble(3, t.getShippingPrice());
				pst.setString(4, t.getTransporterShippingCode());
				storeTransactionItems(pst,5, t.getItems());

				if(t.getDatePayment()!=null)
					pst.setTimestamp(6,  new Timestamp(t.getDatePayment().getTime()));
				else
					pst.setTimestamp(6, null);

				if(t.getDateSend()!=null)
					pst.setTimestamp(7, new Timestamp(t.getDateSend().getTime()));
				else
					pst.setTimestamp(7, null);


				if(t.getPaymentProvider()!=null)
					pst.setString(8, t.getPaymentProvider().name());
				else
					pst.setString(8, null);

				pst.setString(9, t.getSourceShopId());
				pst.setString(10, t.getSourceShopName());
				pst.setDouble(11, t.getReduction());
				
				pst.setString(12,t.getTypeTransaction().name());
				
				pst.setInt(13, t.getContact().getId());
				
				
				if(t.getDateCreation()!=null)
					pst.setTimestamp(14, new Timestamp(t.getDateCreation().getTime()));
				else
					pst.setTimestamp(14, null);
			
				pst.setLong(15, t.getId());
				
				
				
				executeUpdate(pst,false);
				return t.getId();

			} catch (Exception e) {
				logger.error("error update", e);
				return -1L;
			}
		}
	}



	@Override
	public void deleteTransaction(Transaction t) throws SQLException {
		logger.debug("delete Transaction {}",t );
		try (var c = pool.getConnection(); var pst = c.prepareStatement("DELETE FROM transactions where id=?")) {
			pst.setLong(1, t.getId());
			executeUpdate(pst,false);
		}

	}


	@Override
	public void deleteTransaction(List<Transaction> state) throws SQLException {
		logger.debug("remove transactions : {} items ",state.size());
		try (var c = pool.getConnection();var pst = c.prepareStatement("DELETE FROM transactions where id = ?")) {
			for (Transaction sto : state) {
				pst.setLong(1, sto.getId());
				pst.addBatch();
				notify(sto);
			}
			executeUpdate(pst, true);
		}
	}



	@Override
	public void deleteStock(MTGSealedStock state) throws SQLException {
		logger.debug("delete {} in sealed stock",state.getId());
		var sql = "DELETE FROM sealed WHERE id=?";
		try (var c = pool.getConnection(); var pst = c.prepareStatement(sql))
		{
			pst.setLong(1, state.getId());
			executeUpdate(pst,false);
			notify(state);
		}

	}

	@Override
	public MTGSealedStock getSealedStockById(Long id) throws SQLException {
		try (var c = pool.getConnection();var pst = c.prepareStatement("SELECT * from sealed where id=?"))
		{
				pst.setLong(1, id);
				ResultSet rsC = executeQuery(pst);
				rsC.next();
				return readSealed(rsC);
		}
	}


	@Override
	public List<MTGSealedStock> listSealedStocks() throws SQLException {
		List<MTGSealedStock> colls = new ArrayList<>();

		try (var c = pool.getConnection();var pst = c.prepareStatement(hlper.selectAll("sealed"));ResultSet rs = executeQuery(pst))
		{
				while (rs.next()) {
					var state = readSealed(rs);
					colls.add(state);
				}
		}
		return colls;
	}

	@Override
	public void saveOrUpdateSealedStock(MTGSealedStock state) throws SQLException {

		if (state.getId() < 0) {

			logger.debug("save sealed {}",state);
			try (var c = pool.getConnection(); var pst = c.prepareStatement(
					"INSERT INTO sealed (edition, qte, comment, lang, typeProduct, conditionProduct,extra,collection,price,tiersAppIds,numversion ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",generatedKey)) {
				pst.setString(1, String.valueOf(state.getProduct().getEdition().getId()));
				pst.setInt(2, state.getQte());
				pst.setString(3, state.getComment());
				pst.setString(4, state.getLanguage());
				pst.setString(5, state.getProduct().getTypeProduct().name());
				pst.setString(6, state.getCondition().name());

				if(state.getProduct().getExtra()!=null)
					pst.setString(7, state.getProduct().getExtra().name());
				else
					pst.setString(7, null);

				pst.setString(8, (state.getMagicCollection()==null)?MTGControler.getInstance().get(DEFAULT_LIBRARY):state.getMagicCollection().getName());
				pst.setDouble(9, state.getValue().doubleValue());
				storeTiersApps(pst,10,state.getTiersAppIds());
				pst.setInt(11, state.getProduct().getNum());

				executeUpdate(pst,false);
				state.setId(getGeneratedKey(pst));
			} catch (Exception e) {
				logger.error("error insert", e);
			}
		} else {
			logger.debug("update Sealed {}",state);
			try (var c = pool.getConnection(); var pst = c.prepareStatement(
					"update sealed set edition=?, qte=?, comment=?, lang=?, typeProduct=?, conditionProduct=?, collection=?, price=?, tiersAppIds=? where id=?")) {
				pst.setString(1, String.valueOf(state.getProduct().getEdition().getId()));
				pst.setInt(2, state.getQte());
				pst.setString(3, state.getComment());
				pst.setString(4, state.getLanguage());
				pst.setString(5, state.getProduct().getTypeProduct().name());
				pst.setString(6, state.getCondition().name());
				pst.setString(7, (state.getMagicCollection()==null)?MTGControler.getInstance().get(DEFAULT_LIBRARY):state.getMagicCollection().getName());
				pst.setDouble(8, state.getValue().doubleValue());
				storeTiersApps(pst,9,state.getTiersAppIds());
				pst.setLong(10, state.getId());
				executeUpdate(pst,false);

				state.setUpdated(false);
			} catch (Exception e) {
				logger.error("error update",e);
			}
		}
		notify(state);

	}


	@Override
	public void removeCard(MTGCard mc, MTGCollection collection) throws SQLException {
		try (var c = pool.getConnection(); var pst = c.prepareStatement("DELETE FROM stocks where idmc=? and idMe=? and collection=?")) {
			pst.setString(1, mc.getScryfallId());
			pst.setString(2, mc.getEdition().getId());
			pst.setString(3, collection.getName());
			executeUpdate(pst,false);
		}
	}

	@Override
	public void moveCard(MTGCard mc, MTGCollection from, MTGCollection to) throws SQLException {
		logger.debug("move {} from {} to {}",mc,from,to);

		try (var c = pool.getConnection(); var pst = c.prepareStatement("update stocks set collection= ? where idmc=? and collection=?"))
		{
			pst.setString(1, to.getName());
			pst.setString(2, mc.getScryfallId());
			pst.setString(3, from.getName());
			int res = executeUpdate(pst,false);

			logger.debug("move result:{}={}",mc, res);
		}
	}

	@Override
	public void moveEdition(MTGEdition ed, MTGCollection from, MTGCollection to) throws SQLException {
		logger.debug("move {} from {} to {}",ed,from,to);

		try (var c = pool.getConnection(); var pst = c.prepareStatement("update stocks set collection= ? where idMe=? and collection=?"))
		{
			pst.setString(1, to.getName());
			pst.setString(2, ed.getId());
			pst.setString(3, from.getName());
			int res = executeUpdate(pst,false);

			logger.debug("move result:{}={}",ed, res);
		}


	}
	
	@Override
	public Map<String, Integer> getCardsCountGlobal(MTGCollection col) throws SQLException {
		Map<String, Integer> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		try (var c = pool.getConnection(); var pst = c.prepareStatement("SELECT idMe, count(distinct(idmc)) FROM stocks where collection=? and qte>0 group by idMe");) 
		{
			pst.setString(1, col.getName());
			try (ResultSet rs = executeQuery(pst)) {
				while (rs.next())
					map.put(rs.getString(1), rs.getInt(2));
			}
		}
		return map;
	}

	@Override
	public List<MTGCardStock> listStocks(MTGCollection collection, MTGEdition me) throws SQLException {
		var ret = new ArrayList<MTGCardStock>();
		var sql = "SELECT * FROM stocks WHERE collection= ? and qte > 0";

		if (me != null)
			sql = "SELECT * FROM stocks WHERE qte > 0 AND collection= ? and idMe = ?";

		try (var c = pool.getConnection(); var pst = c.prepareStatement(sql)) {
			pst.setString(1, collection.getName());
			if (me != null)
				pst.setString(2, me.getId());
			try (ResultSet rs = executeQuery(pst)) {
				while (rs.next()) {
					var mc = readStock(rs);
					ret.add(mc);
					notify(mc.getProduct());
				}
			}
		}
		return ret;
	}
	
	
	@Override
	public List<String> listEditionsIDFromCollection(MTGCollection collection) throws SQLException {
		var sql = "SELECT distinct(idMe) FROM stocks WHERE qte > 0 AND collection=?";
		var retour = new ArrayList<String>();
		logger.trace(sql);
		try (var c = pool.getConnection(); var pst = c.prepareStatement(sql)) {
			pst.setString(1, collection.getName());
			try (ResultSet rs = executeQuery(pst)) {

				while (rs.next()) {
					retour.add(rs.getString("idMe"));
				}
			}
		}
		return retour;
	}

	@Override
	public MTGCollection getCollection(String name) throws SQLException {

		try (var c = pool.getConnection(); var pst = c.prepareStatement("SELECT * FROM collections where name= ?")) {
			pst.setString(1, name);
			try (ResultSet rs = executeQuery(pst)) {
				if (rs.next()) {
					return new MTGCollection(rs.getString("name"));
				}
				return null;
			}
		}
	}

	@Override
	public void saveCollection(MTGCollection col) throws SQLException {
		listCollections.put(col.getName(),col);
		try (var c = pool.getConnection(); var pst = c.prepareStatement("insert into collections values (?)")) {
			pst.setString(1, col.getName().replace("'", "\'"));
			executeUpdate(pst,false);
		}
	}

	@Override
	public void removeCollection(MTGCollection col) throws SQLException {

		listCollections.remove(col.getName());

		if (col.getName().equals(MTGControler.getInstance().get(DEFAULT_LIBRARY)))
			throw new SQLException(col.getName() + " can not be deleted");

		try (var c = pool.getConnection(); var pst = c.prepareStatement("DELETE FROM collections where name = ?")) {
			pst.setString(1, col.getName());
			executeUpdate(pst,false);
		}

		try (var c = pool.getConnection(); var pst2 = c.prepareStatement("DELETE FROM stocks where collection = ?")) {
			pst2.setString(1, col.getName());
			executeUpdate(pst2,false);
		}
	}
	
	
	

	
	@Override
	public void loadAlerts() {
		
		try (var c = pool.getConnection(); var pst = c.prepareStatement(hlper.selectAll("alerts"))) {
			try (var rs = executeQuery(pst)) {
				while (rs.next()) {
					var alert = new MTGAlert();
					alert.setCard(readCard(rs,MCARD));
					alert.setQty(rs.getInt("qte"));
					alert.setPrice(rs.getDouble("amount"));
					alert.setFoil(rs.getBoolean("foil"));
					
					alerts.put(alert.getId(),alert);
				}
			}
		} catch (Exception e) {
			logger.error("error get alert",e);
		}
	}

	@Override
	public List<MTGCollection> listCollections() throws SQLException {

		if(!listCollections.isEmpty())
			return listCollections.values();


		try (var c =  pool.getConnection();var pst = c.prepareStatement(hlper.selectAll("collections")))
		{
			try (ResultSet rs = executeQuery(pst)) {
				while (rs.next()) {
					listCollections.put(rs.getString(1),new MTGCollection(rs.getString(1)));
				}
			}
		}
		return listCollections.values();
	}

	@Override
	public void removeEdition(MTGEdition me, MTGCollection col) throws SQLException {
		logger.debug("delete {} from {}",me,col);
		try (var c = pool.getConnection(); var pst = c.prepareStatement("DELETE FROM stocks where idMe=? and collection=?")) {
			pst.setString(1, me.getId());
			pst.setString(2, col.getName());
			executeUpdate(pst,false);
		}
	}

	@Override
	public List<MTGCollection> listCollectionFromCards(MTGCard mc) throws SQLException {
		var cols = new ArrayList<MTGCollection>();

		if (mc.getEditions().isEmpty())
			throw new SQLException("No edition defined");

		try (var c = pool.getConnection(); var pst = c.prepareStatement("SELECT DISTINCT(collection) FROM stocks WHERE qte > 0 AND idmc=? and idMe=?")) {
			pst.setString(1, mc.getScryfallId());
			pst.setString(2, mc.getEdition().getId());
			try (ResultSet rs = executeQuery(pst)) {

				while (rs.next()) {
					cols.add(new MTGCollection(rs.getString(COLLECTION)));
				}
			}
		}
		return cols;
	}

	@Override
	public void deleteStock(List<MTGCardStock> state) throws SQLException {
		logger.debug("remove {} items in stock",state.size());
		var st = new StringBuilder();
		st.append("DELETE FROM stocks where idstock IN (");
		for (MTGCardStock sto : state) {
			st.append(sto.getId()).append(",");
			notify(sto);
		}
		st.append(")");
		String sql = st.toString().replace(",)", ")");
		try (var c = pool.getConnection();var pst = c.prepareStatement(sql)) {
			executeUpdate(pst,false);
		}
	}



	@Override
	public List<MTGCardStock> listStocks(MTGCard mc) throws SQLException {
		var sql ="SELECT * FROM stocks where idmc=?";

		List<MTGCardStock> colls = new ArrayList<>();
		try (var c = pool.getConnection(); var pst = c.prepareStatement(sql)) {
			pst.setString(1, mc.getScryfallId());

			try (ResultSet rs = executeQuery(pst)) {
				while (rs.next()) {
					var state = readStock(rs);
					colls.add(state);
				}
				logger.trace("loading {} items FROM stock for {}",colls.size(),mc);

			}

		}
		return colls;

	}
	
	

	@Override
	public List<MTGCardStock> listStocks(MTGCard mc, MTGCollection col,boolean editionStrict) throws SQLException {
		String sql ="SELECT * FROM stocks where collection=? and name like ?";

		if(editionStrict)
			sql ="SELECT * FROM stocks where collection=? and idmc=?";

		logger.trace("sql={}",sql);

		List<MTGCardStock> colls = new ArrayList<>();
		try (var c = pool.getConnection(); var pst = c.prepareStatement(sql)) {
			pst.setString(1, col.getName());

			if(editionStrict)
				pst.setString(2, mc.getScryfallId());
			else 
				pst.setString(2, mc.getName());

			try (ResultSet rs = executeQuery(pst)) {

				while (rs.next()) {
					var state = readStock(rs);
					colls.add(state);
				}
				logger.trace("loading {} items FROM stock for {}",colls.size(),mc);

			}

		}
		return colls;

	}

	@Override
	public List<MTGCardStock> listStocks(List<MTGCollection> cols) throws SQLException {
		List<MTGCardStock> colls = new ArrayList<>();

		var stmt = String.format("SELECT * FROM stocks where collection in  (%s)",cols.stream().map(c->"'"+c.getName()+"'").collect(Collectors.joining(", ")));
		logger.trace("loading stock with SQL={}",stmt);
		try (var c = pool.getConnection(); var pst = c.prepareStatement(stmt); ResultSet rs = executeQuery(pst);) {
			while (rs.next()) {
				var state = readStock(rs);
				colls.add(state);
			}
		}
		return colls;
	}


	@Override
	public MTGCardStock getStockById(Long id) throws SQLException {


		try (var c = pool.getConnection(); var pst = c.prepareStatement("SELECT * FROM stocks where idstock=?")) {
			pst.setLong(1, id);
			var rs = executeQuery(pst);

			rs.next();

			return readStock(rs);
		}
	}




	@Override
	public List<MTGCardStock> listStocks() throws SQLException {
		List<MTGCardStock> colls = new ArrayList<>();
		try (var c = pool.getConnection(); var pst = c.prepareStatement(hlper.selectAll("stocks")); ResultSet rs = executeQuery(pst);) {
			while (rs.next()) {
				var state = readStock(rs);
				colls.add(state);

			}
			logger.debug("load {} item(s) from stock",colls.size());
		}
		return colls;
	}

	protected int getGeneratedKey(PreparedStatement pst) {

		try (ResultSet rs = pst.getGeneratedKeys()) {
			rs.next();
				return rs.getInt(1);

		} catch (Exception e) {
			logger.error("couldn't retrieve id : {}",e.getMessage());
		}
		return -1;

	}

	@Override
	public void saveOrUpdateCardStock(MTGCardStock state) throws SQLException {

		if (state.getId() < 0) {
			logger.debug("save stock {}",state);
			try (var c = pool.getConnection(); var pst = c.prepareStatement( "insert into stocks  ( conditions,foil,signedcard,langage,qte,comments,idmc,collection,mcard,altered,price,grading,tiersAppIds,etched,idMe,dateUpdate,name,digital) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", generatedKey)) {
				pst.setString(1, state.getCondition().name());
				pst.setBoolean(2, state.isFoil());
				pst.setBoolean(3, state.isSigned());
				pst.setString(4, state.getLanguage());
				pst.setInt(5, state.getQte());
				pst.setString(6, state.getComment());
				pst.setString(7, state.getProduct().getScryfallId());
				pst.setString(8, String.valueOf(state.getMagicCollection()));
				storeCard(pst, 9, state.getProduct());
				pst.setBoolean(10, state.isAltered());
				pst.setDouble(11, state.getValue().doubleValue());
				storeGrade(pst,12, state.getGrade());
				storeTiersApps(pst,13, state.getTiersAppIds());
				pst.setBoolean(14, state.isEtched());
				pst.setString(15, state.getProduct().getEdition().getId());
				pst.setTimestamp(16, new Timestamp(new java.util.Date().getTime()));
				pst.setString(17, state.getProduct().getName());
				pst.setBoolean(18, state.isDigital());
				executeUpdate(pst,false);
				state.setId(getGeneratedKey(pst));
				state.setDateUpdate(new java.util.Date());
			} catch (Exception e) {
				logger.error(e);
			}
		} else {
			logger.debug("update Stock {}",state);
			try (var c = pool.getConnection(); var pst = c.prepareStatement(
					"update stocks set comments=?, conditions=?, foil=?,signedcard=?,langage=?, qte=? ,altered=?,price=?,idmc=?,collection=?,grading=?,tiersAppIds=?,etched=?, mcard=?, idMe=?, dateUpdate=?, name=?, digital=? where idstock=?")) {
				pst.setString(1, state.getComment());
				pst.setString(2, state.getCondition().name());
				pst.setBoolean(3, state.isFoil());
				pst.setBoolean(4, state.isSigned());
				pst.setString(5, state.getLanguage());
				pst.setInt(6, state.getQte());
				pst.setBoolean(7, state.isAltered());
				pst.setDouble(8, state.getValue().doubleValue());
				pst.setString(9, state.getProduct().getScryfallId());
				pst.setString(10, state.getMagicCollection().getName());
				storeGrade(pst, 11,state.getGrade());
				storeTiersApps(pst, 12,state.getTiersAppIds());
				pst.setBoolean(13, state.isEtched());
				storeCard(pst, 14, state.getProduct());
				pst.setString(15, state.getProduct().getEdition().getId());
				pst.setTimestamp(16, new Timestamp(new java.util.Date().getTime()));
				pst.setString(17, state.getProduct().getName());
				pst.setBoolean(18, state.isDigital());
				pst.setLong(19, state.getId());
				executeUpdate(pst,false);
				state.setDateUpdate(new java.util.Date());
			} catch (Exception e) {
				logger.error(e);
			}
		}
		notify(state);
	}



	@Override
	public void saveAlert(MTGAlert alert) throws SQLException {

		try (var c = pool.getConnection(); var pst = c.prepareStatement("insert into alerts ( id,mcard,amount,qte) values (?,?,?,?)")) {
			pst.setString(1, alert.getId());
			storeCard(pst, 2, alert.getCard());
			pst.setDouble(3, alert.getPrice());
			pst.setInt(4, alert.getQty());
			executeUpdate(pst,false);
			logger.debug("save alert for {} ({})",alert.getCard(),alert.getCard().getEdition());
			alerts.put(alert.getId(), alert);
		}
	}

	@Override
	public void updateAlert(MTGAlert alert) throws SQLException {
		logger.debug("update alert {}",alert);
		try (var c = pool.getConnection(); var pst = c.prepareStatement("update alerts set amount=?,mcard=?,foil=?, qte=? where id=?")) {
			pst.setDouble(1, alert.getPrice());
			storeCard(pst, 2, alert.getCard());
			pst.setBoolean(3, alert.isFoil());
			pst.setInt(4, alert.getQty());
			pst.setString(5, alert.getId());
			executeUpdate(pst,false);
			alerts.put(alert.getId(), alert);
		}

	}

	@Override
	public void deleteAlert(MTGAlert alert) throws SQLException
	{
		try (var c = pool.getConnection(); var pst = c.prepareStatement("DELETE FROM alerts where id=?")) {
			pst.setString(1, alert.getId());
			int res = executeUpdate(pst,false);
			logger.debug("delete alert {} = {}", alert,res);
		}
	}

	@Override
	public List<MTGNews> listNews() {
		List<MTGNews> news = new ArrayList<>();
		try (var c = pool.getConnection(); var pst = c.prepareStatement(hlper.selectAll("news"))) {
			try (ResultSet rs = executeQuery(pst)) {
				while (rs.next()) {
					var n = new MTGNews();
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
	public void deleteNews(MTGNews n) throws SQLException {
		logger.debug("delete news {}",n);
		try (var c = pool.getConnection(); var pst = c.prepareStatement("DELETE FROM news where id=?")) {
			pst.setInt(1, n.getId());
			executeUpdate(pst,false);
		}
	}

	@Override
	public void saveOrUpdateNews(MTGNews n) throws SQLException {
		if (n.getId() < 0) {

			logger.debug("save {}",n);
			try (var c = pool.getConnection(); var pst = c.prepareStatement(
					"insert into news  ( name,categorie,url,typeNews) values (?,?,?,?)",
					generatedKey)) {
				pst.setString(1, n.getName());
				pst.setString(2, n.getCategorie());
				pst.setString(3, n.getUrl());
				pst.setString(4, n.getProvider().getName());
				executeUpdate(pst,false);
				n.setId(getGeneratedKey(pst));
			}

		} else {
			logger.debug("update {}",n);
			try (var c = pool.getConnection(); var pst = c.prepareStatement("update news set name=?, categorie=?, url=?,typeNews=? where id=?")) {
				pst.setString(1, n.getName());
				pst.setString(2, n.getCategorie());
				pst.setString(3, n.getUrl());
				pst.setString(4, n.getProvider().getName());
				pst.setInt(5, n.getId());
				executeUpdate(pst,false);
			} catch (Exception e) {
				logger.error(e);
			}
		}

	}
	
	
	
	@Override
	public void updateCard(MTGCard card,MTGCard newC, MTGCollection col) throws SQLException {
		try (var c = pool.getConnection(); var pst = c.prepareStatement("UPDATE stocks SET mcard= ?, dateUpdate=? WHERE idmc = ? and collection = ?"))
		{
			
			storeCard(pst, 1, newC);
			pst.setString(3, card.getScryfallId());
			pst.setString(4, col.getName());
			pst.setDate(2,  new Date(System.currentTimeMillis()));
			executeUpdate(pst,false);
		}

	}

	


	@Override
	public <T extends AbstractAuditableItem> void storeTechnicalItem(Class<T> classe, List<T> list) throws SQLException {
		try (var c = pool.getConnection(); var pst = c.prepareStatement("insert into technicalauditlog  (classname ,techObject,startTime) values (?,?,?)")) {
			
			for(var it : list)
			{
				pst.setString(1, classe.getSimpleName());
				pst.setString(2, serialiser.toJsonElement(it).toString());
				pst.setTime(3, new java.sql.Time(it.getStart().toEpochMilli()));
				pst.addBatch();
			}
			executeUpdate(pst, true);
			
		}
		
	}
	
	@Override
	public <T extends AbstractAuditableItem> List<T> restoreTechnicalItem(Class<T> classe,Instant start,Instant end) throws SQLException {
			List<T> trans = new ArrayList<>();
			//TODO add instants filters
			try (var c = pool.getConnection(); var pst = c.prepareStatement("SELECT techobject FROM technicalauditlog where classname=?")) 
			{
				pst.setString(1, classe.getSimpleName());
				try (ResultSet rs = executeQuery(pst)) {
					while (rs.next()) {
						trans.add(readTechnical(rs,classe));
					}
				}
			}
			return trans;
	}
	
	
	


	protected <T extends AbstractAuditableItem> T readTechnical(ResultSet rs, Class<T> classe) throws SQLException
	{
		return serialiser.fromJson(rs.getString("techobject"), classe);
	}
	
	
	
	protected DAOInfo buildInfo(Statement pst)
	{
		var start=Instant.now();
		var daoInfo = new DAOInfo();

		AbstractTechnicalServiceManager.inst().store(daoInfo);

		daoInfo.setStart(start);
		try {
			daoInfo.setConnectionName(pst.getConnection().toString());
		} catch (SQLException e) {
			logger.error(e);
		}


		var index = 0;

		if(pst.toString().toUpperCase().indexOf("SELECT")>-1)
			index=pst.toString().toUpperCase().indexOf("SELECT");

		if(pst.toString().toUpperCase().indexOf("UPDATE")>-1)
			index=pst.toString().toUpperCase().indexOf("UPDATE");

		if(pst.toString().toUpperCase().indexOf("INSERT")>-1)
			index=pst.toString().toUpperCase().indexOf("INSERT");

		if(pst.toString().toUpperCase().indexOf("DELETE")>-1)
			index=pst.toString().toUpperCase().indexOf("DELETE");

		daoInfo.setQuery(pst.toString().substring(index));
		daoInfo.setClasseName(pst.getClass().getCanonicalName());
		daoInfo.setDaoName(this.getName());
	
		return daoInfo;
	}

	private int executeUpdate(PreparedStatement pst, boolean batch) throws SQLException {
		var daoInfo=buildInfo(pst);

		try {
			
			int results=0;
			
			if(batch)
			{
				results = pst.executeBatch().length;
			}
			else
			{
				results = pst.executeUpdate();
			}
				
			
			daoInfo.setEnd(Instant.now());
			
			if(results<=0)
				logger.warn("query has update {} line", results);
			
			return results;
		} catch (SQLException e) {
			daoInfo.setMessage(e.getMessage());
			daoInfo.setEnd(Instant.now());
			throw e;
		}
	}

	protected ResultSet executeQuery(PreparedStatement pst) throws SQLException {
		var daoInfo=buildInfo(pst);
		logger.trace("Executing {}",daoInfo);
		try {
			ResultSet rs = pst.executeQuery();
			daoInfo.setEnd(Instant.now());

			return rs;
		} catch (SQLException e) {
			daoInfo.setEnd(Instant.now());
			daoInfo.setMessage(e.getMessage());
			throw e;
		}



	}

	@Override
	public boolean executeQuery(String query) throws SQLException {
		try (var c = pool.getConnection(); Statement pst = c.createStatement())
		{
			var daoInfo=buildInfo(pst);
			var ret =  pst.execute(query);
			daoInfo.setEnd(Instant.now());
			
			return ret;
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

	private MTGAnnounce readAnnounce(ResultSet rs) throws SQLException {
		var a = new MTGAnnounce();
			  a.setId(rs.getInt("id"));
			  a.setStartDate(rs.getTimestamp("startDate"));
			  a.setEndDate(rs.getTimestamp("endDate"));
			  a.setTitle(rs.getString("title"));
			  a.setDescription(rs.getString("description"));
			  a.setTotalPrice(rs.getDouble("total"));
			  a.setCurrency(Currency.getInstance(rs.getString("currency")));
			  a.setContact(getContactById(rs.getInt("fk_idcontact")));
			  a.setCreationDate(rs.getTimestamp("creationDate"));
			  a.setType(EnumTransactionDirection.valueOf(rs.getString("typeAnnounce")));
			  a.setCategorie(EnumItems.valueOf(rs.getString("category")));
			  a.setPercentReduction(rs.getDouble("percentReduction"));
			  a.setCondition(EnumCondition.valueOf(rs.getString("conditions")));
			  a.setStatus(STATUS.valueOf(rs.getString("statusAnnounce")));
			  if(rs.getObject("stocksItem")!=null)
				  a.setItems(readStockItemFrom(rs,"stocksItem"));

		return a;
	}

	private MTGSealedStock readSealed(ResultSet rs) throws SQLException {
		int ref = rs.getInt("numversion");
		  try
		  {
			var list = MTG.getEnabledPlugin(MTGSealedProvider.class).get(getEnabledPlugin(MTGCardsProvider.class).getSetById(rs.getString(EDITION)),EnumItems.valueOf(rs.getString("typeProduct")),(rs.getString(EXTRATYPE)==null) ? null : EnumExtra.valueOf(rs.getString(EXTRATYPE)));
			var product = list.stream().filter(p->p.getNum()==ref).findFirst().orElse(list.get(0));
			var state =  new MTGSealedStock(product);
			 state.setComment(rs.getString("comment"));
			 state.setId(rs.getInt("id"));
			 state.setQte(rs.getInt("qte"));
			 state.setCondition(EnumCondition.valueOf(rs.getString("conditionProduct")));
			 state.setMagicCollection(new MTGCollection(rs.getString(COLLECTION)));
			 state.setPrice(rs.getDouble("price"));
			 state.setTiersAppIds(readTiersApps(rs));
			 state.setLanguage(rs.getString("lang"));
			 return state;
		  }
		  catch (Exception e)
		  {
			logger.error("Error loading Packaging for {} {} {} : {}",rs.getString("typeProduct"),rs.getString(EXTRATYPE),rs.getString(EDITION),e);
			return null;
		  }
		
		 
	}

	private MTGCardStock readStock(ResultSet rs) throws SQLException
	{
		var state = new MTGCardStock(readCard(rs,MCARD));
			state.setComment(rs.getString("comments"));
			state.setId(rs.getInt("idstock"));
			state.setMagicCollection(new MTGCollection(rs.getString(COLLECTION)));
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
			state.setDigital(rs.getBoolean("digital"));
			if(state.getTiersAppIds()==null)
				state.setTiersAppIds(new HashMap<>());
			
			try {
				state.setDateUpdate(rs.getDate("dateUpdate"));
			} catch (Exception e) {
				//do nothing
			}


		return state;

	}


}

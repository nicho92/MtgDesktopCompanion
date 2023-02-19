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

import org.magic.api.beans.Announce;
import org.magic.api.beans.Announce.STATUS;
import org.magic.api.beans.Grading;
import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicNews;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.SealedStock;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.api.beans.enums.TransactionPayementProvider;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.ConverterItem;
import org.magic.api.beans.technical.GedEntry;
import org.magic.api.beans.technical.audit.DAOInfo;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGNewsProvider;
import org.magic.api.interfaces.MTGPool;
import org.magic.api.interfaces.MTGSealedProvider;
import org.magic.api.interfaces.MTGSerializable;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractMagicDAO;
import org.magic.api.pool.impl.NoPool;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;
import org.magic.services.TechnicalServiceManager;
import org.magic.services.TransactionService;
import org.magic.services.tools.CryptoUtils;
import org.magic.services.tools.IDGenerator;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.MTG;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public abstract class AbstractMagicSQLDAO extends AbstractMagicDAO {

	private static final String UPDATED = "{} updated";
	private static final String BOOLEAN = "BOOLEAN";
	private static final String EXTRA_TYPE = "extra";
	private static final String COLLECTION = "collection";
	protected static final String MCARD = "mcard";
	private static final String DEFAULT_LIBRARY = "default-library";
	private static final String IF_NOT_EXISTS = "IF NOT EXISTS ";
	private static final String CREATE_TABLE = "CREATE TABLE ";
	private static final String EDITION = "edition";
	protected MTGPool pool;

	protected abstract String getAutoIncrementKeyWord();
	protected abstract String getjdbcnamedb();
	protected abstract String beanStorage();
	protected abstract String longTextStorage();
	protected abstract String createListStockSQL();
	protected abstract String getdbSizeQuery();

	protected static final int COLLECTION_COLUMN_SIZE=30;
	protected static final int CARD_ID_SIZE=50;


	protected List<MTGStockItem> readStockItemFrom(ResultSet rs,String field) throws SQLException {
		return serialiser.fromJsonList(rs.getObject(field).toString(), MTGStockItem.class);
	}

	protected void storeTransactionItems(PreparedStatement pst, int position, List<MTGStockItem> grd) throws SQLException {
		pst.setString(position, serialiser.toJsonElement(grd).toString());

	}

	protected Grading readGrading(ResultSet rs) throws SQLException {
		return serialiser.fromJson(rs.getString("grading"), Grading.class);
	}


	protected void storeGrade(PreparedStatement pst, int position, Grading grd) throws SQLException {
		pst.setString(position, serialiser.toJsonElement(grd).toString());
	}

	protected Map<MagicCard, Integer> readDeckBoard(ResultSet rs, String field) throws SQLException {

		Map<MagicCard, Integer> ret = new HashMap<>();
		serialiser.fromJson(rs.getString(field), JsonArray.class).forEach(je->{

			MagicCard mc = serialiser.fromJson(je.getAsJsonObject().get("card").toString(), MagicCard.class);
			Integer qte = je.getAsJsonObject().get("qty").getAsInt();

			ret.put(mc, qte);


		});

		return ret;
	}

	protected void storeDeckBoard(PreparedStatement pst, int i, Map<MagicCard, Integer> board) throws SQLException {

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

	protected void storeCard(PreparedStatement pst, int position, MagicCard mc) throws SQLException {
		pst.setString(position, serialiser.toJsonElement(mc).toString());
	}

	protected MagicCard readCard(ResultSet rs,String field) throws SQLException {
		MagicCard mc=null;
		try{
			mc = serialiser.fromJson( rs.getObject(field).toString(), MagicCard.class);
		}
		catch(NullPointerException e)
		{
			return null;
		}

		if(mc==null)
			return null;

		try {
			mc.setDateUpdated(rs.getDate("dateUpdate"));
		} catch (Exception e) {
			//do nothing
		}
		return mc;

	}


	protected boolean enablePooling()
	{
		return true;
	}


	protected boolean isJsonCompatible()
	{
		return true;
	}

	public boolean createDB() {
		try (var cont =  pool.getConnection();Statement stat = cont.createStatement()) {
			
			stat.executeUpdate(CREATE_TABLE+notExistSyntaxt()+" favorites (id_contact INTEGER, id_announce INTEGER, classeName VARCHAR(30) )");
			logger.debug("Create table favorites");

			stat.executeUpdate(CREATE_TABLE+notExistSyntaxt()+" ged (id "+getAutoIncrementKeyWord()+" PRIMARY KEY,creationDate TIMESTAMP, className VARCHAR(250), idInstance VARCHAR(250), fileName VARCHAR(250), fileContent " + longTextStorage() + ", md5 VARCHAR(35) )");
			logger.debug("Create table ged");

			stat.executeUpdate(CREATE_TABLE+notExistSyntaxt()+" announces (id "+getAutoIncrementKeyWord()+" PRIMARY KEY,creationDate TIMESTAMP , startDate TIMESTAMP ,endDate TIMESTAMP, title VARCHAR(150), description " + longTextStorage() + ", total DECIMAL(10,2), currency VARCHAR(5), stocksItem "+beanStorage() + ",typeAnnounce VARCHAR(10), fk_idcontact INTEGER, category VARCHAR(50), percentReduction DECIMAL(10,2), conditions VARCHAR(50), statusAnnounce VARCHAR(25))");
			logger.debug("Create table announces");

			stat.executeUpdate(CREATE_TABLE+notExistSyntaxt()+" transactions (id "+getAutoIncrementKeyWord()+" PRIMARY KEY, dateTransaction TIMESTAMP, message VARCHAR(250), stocksItem "+beanStorage()+", statut VARCHAR(15), transporter VARCHAR(50), shippingPrice DECIMAL(10,3), transporterShippingCode VARCHAR(50),currency VARCHAR(5),datePayment TIMESTAMP NULL ,dateSend TIMESTAMP NULL , paymentProvider VARCHAR(50),fk_idcontact INTEGER, sourceShopId VARCHAR(250),sourceShopName VARCHAR(250),typeTransaction VARCHAR(15))");
			logger.debug("Create table transactions");

			stat.executeUpdate(CREATE_TABLE+notExistSyntaxt()+" contacts (id " + getAutoIncrementKeyWord() + " PRIMARY KEY, contact_name VARCHAR(250), contact_lastname VARCHAR(250), contact_password VARCHAR(250),contact_telephone VARCHAR(250), contact_country VARCHAR(250), contact_zipcode VARCHAR(10), contact_city VARCHAR(50), contact_address VARCHAR(250), contact_website VARCHAR(250),contact_email VARCHAR(100) UNIQUE, emailAccept "+getBoolean()+", contact_active "+getBoolean()+", temporaryToken VARCHAR("+TransactionService.TOKENSIZE+"))");
			logger.debug("Create table contacts");

			stat.executeUpdate(CREATE_TABLE+notExistSyntaxt()+" orders (id "+getAutoIncrementKeyWord()+" PRIMARY KEY, idTransaction VARCHAR(50), description VARCHAR(250),edition VARCHAR(5),itemPrice DECIMAL(10,3),shippingPrice  DECIMAL(10,3), currency VARCHAR(4), transactionDate DATE,typeItem VARCHAR(50),typeTransaction VARCHAR(50),sources VARCHAR(50),seller VARCHAR(50))");
			logger.debug("Create table orders");

			stat.executeUpdate(CREATE_TABLE+notExistSyntaxt()+" cards (ID varchar("+CARD_ID_SIZE+"),mcard "+beanStorage()+", edition VARCHAR(5), cardprovider VARCHAR(20), collection VARCHAR("+COLLECTION_COLUMN_SIZE+"), dateUpdate TIMESTAMP)");
			logger.debug("Create table cards");

			stat.executeUpdate(CREATE_TABLE+notExistSyntaxt()+" collections ( name VARCHAR("+COLLECTION_COLUMN_SIZE+") PRIMARY KEY)");
			logger.debug("Create table collections");

			stat.executeUpdate(CREATE_TABLE+notExistSyntaxt()+" stocks (idstock "+getAutoIncrementKeyWord()+" PRIMARY KEY , idmc varchar("+CARD_ID_SIZE+"), mcard "+beanStorage()+", collection VARCHAR("+COLLECTION_COLUMN_SIZE+"),comments "+longTextStorage()+", conditions VARCHAR(30),foil "+getBoolean()+", signedcard "+getBoolean()+", langage VARCHAR(20), qte integer,altered "+getBoolean()+",price DECIMAL, grading "+beanStorage()+", tiersAppIds "+beanStorage()+",etched "+getBoolean()+")");
			logger.debug("Create table stocks");

			stat.executeUpdate(CREATE_TABLE+notExistSyntaxt()+" alerts (id varchar("+CARD_ID_SIZE+") PRIMARY KEY, mcard "+beanStorage()+", amount DECIMAL, foil "+getBoolean()+",qte integer)");
			logger.debug("Create table alerts");

			stat.executeUpdate(CREATE_TABLE+notExistSyntaxt()+" news (id "+getAutoIncrementKeyWord()+" PRIMARY KEY, name VARCHAR(100), url VARCHAR(255), categorie VARCHAR(50),typeNews VARCHAR(50))");
			logger.debug("Create table news");

			stat.executeUpdate(CREATE_TABLE+notExistSyntaxt()+" sealed (id "+getAutoIncrementKeyWord()+" PRIMARY KEY, edition VARCHAR(5), qte integer, comment "+longTextStorage()+",lang VARCHAR(50),typeProduct VARCHAR(25),conditionProduct VARCHAR(25),statut VARCHAR(10), extra VARCHAR(10),collection VARCHAR("+COLLECTION_COLUMN_SIZE+"),price DECIMAL, tiersAppIds "+beanStorage()+", numversion integer)");
			logger.debug("Create table selead");

			stat.executeUpdate(CREATE_TABLE+notExistSyntaxt()+" decks (id "+getAutoIncrementKeyWord()+" PRIMARY KEY, description "+longTextStorage()+", name VARCHAR(250), dateCreation DATE, dateUpdate DATE, tags VARCHAR(250), commander " +beanStorage()+", main " +beanStorage()+", sideboard " +beanStorage()+", averagePrice DECIMAL)");
			logger.debug("Create table decks");

			stat.executeUpdate(CREATE_TABLE+notExistSyntaxt()+" conversionsItems (id "+getAutoIncrementKeyWord()+" PRIMARY KEY, name VARCHAR(255),lang VARCHAR(25), source VARCHAR(25),inputId BIGINT,destination VARCHAR(25),outputId BIGINT)");
			logger.debug("Create table conversionsItems");

			postCreation(stat);

			logger.debug("populate collections");

			for(String s : MTGConstants.getDefaultCollectionsNames())
				stat.executeUpdate("insert into collections values ('"+s+"')");


			stat.executeUpdate("INSERT INTO contacts (id, contact_name, contact_lastname, contact_password, contact_telephone, contact_country, contact_zipcode, contact_city, contact_address, contact_website, contact_email, emailAccept, temporaryToken, contact_active) VALUES (1, 'MTG', 'Companion', NULL, '123456789', 'FR', '123456', 'Somewhere', 'In the middle of nowhere', 'https://www.mtgcompanion.org', 'mtgdesktopcompanion@gmail.com', '1', NULL, '1');");



			createIndex(stat);


			return true;
		} catch (SQLIntegrityConstraintViolationException e) {
			logger.debug("database already created");
			return false;
		}
		catch (SQLException e) {
			logger.error("SQLException : {}",e.getMessage());
			logger.trace("error",e);
			return false;
		}
	}


	@Override
	public List<Announce> listFavorites(Contact contact, String classename) throws SQLException {

		var ret = new ArrayList<Announce>();
		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT id_announce from favorites where id_contact= ? and classename=?"))
		{
			pst.setInt(1, contact.getId());
			pst.setString(2, classename);
			var rs = executeQuery(pst);
			while(rs.next())
			{
				try {
					ret.add(getAnnounceById(rs.getInt("id_announce")));
				}
				catch(Exception e)
				{
					logger.error(e);
				}

			}
		}

		return ret;
	}


	@Override
	public void deleteFavorites(int idContact, int idAnnounce, String classename) throws SQLException {
		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("DELETE FROM favorites WHERE id_contact= ? and id_announce=? and classename=?"))
		{
				pst.setInt(1, idContact);
				pst.setInt(2, idAnnounce);
				pst.setString(3, classename);
				executeUpdate(pst);
		}

	}

	@Override
	public void saveFavorites(int idContact, int idAnnounce,String classename) throws SQLException {
		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("INSERT INTO favorites (id_contact,id_announce,classeName) VALUES (?,?,?)"))
		{
				pst.setInt(1, idContact);
				pst.setInt(2, idAnnounce);
				pst.setString(3, classename);
				executeUpdate(pst);
		}
	}

	@Override
	public <T extends MTGSerializable> boolean storeEntry(GedEntry<T> gedItem) throws SQLException {
		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("INSERT INTO ged (creationDate, className, idInstance, fileContent,fileName, md5) VALUES (?, ?, ?, ?,?,?)"))
		{
				pst.setTimestamp(1, new Timestamp(Instant.now().toEpochMilli()));
				pst.setString(2, gedItem.getClasse().getCanonicalName());
				pst.setString(3, gedItem.getId());
				pst.setString(4, CryptoUtils.toBase64(gedItem.getContent()));
				pst.setString(5, gedItem.getName());
				pst.setString(6, CryptoUtils.getMD5(gedItem.getContent()));
				executeUpdate(pst);
				return true;

		}
	}

	@Override
	public <T extends MTGSerializable> List<GedEntry<T>> listAllEntries() throws SQLException {
		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT className,idInstance,fileName from ged"))
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
		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT fileName from ged where className = ? and IdInstance = ?"))
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
				logger.debug("return {}",arr);
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


		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT fileContent, md5 from ged where className = ? and IdInstance = ? and fileName= ?"))
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
		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("DELETE FROM ged where className = ? and IdInstance = ? and fileName= ?"))
		{
				pst.setString(1, gedItem.getClasse().getCanonicalName());
				pst.setString(2,gedItem.getId());
				pst.setString(3,gedItem.getName());
				executeUpdate(pst);
				return true;
		}
	}





	@Override
	public Announce getAnnounceById(int id) throws SQLException {

		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT * from announces where id=?"))
		{
				pst.setInt(1, id);
				var rs = executeQuery(pst);
				rs.next();
				return readAnnounce(rs);

		}
	}

	@Override
	public List<Announce> listAnnounces(int max,STATUS stat) throws SQLException {
		List<Announce> colls = new ArrayList<>();

		var sql = "SELECT * from announces where statusAnnounce=?  ORDER BY id DESC";

		if(stat==null)
			sql = "SELECT * from announces ORDER BY id DESC";

		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement(sql))
		{

			if(stat!=null)
				pst.setString(1, stat.name());


				if(max>0)
					pst.setMaxRows(max);

				var rs = executeQuery(pst);

				while (rs.next()) {
					Announce d = readAnnounce(rs);
					colls.add(d);
					notify(d);
				}
		}
		return colls;
	}

	@Override
	public int saveOrUpdateAnnounce(Announce n) throws SQLException {
		if (n.getId() < 0)
		{
				try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("INSERT INTO announces (creationDate, startDate, endDate, title, description, total, currency, stocksItem, typeAnnounce, fk_idcontact,category,percentReduction,conditions,statusAnnounce) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",Statement.RETURN_GENERATED_KEYS))
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
					executeUpdate(pst);
					n.setId(getGeneratedKey(pst));
					logger.debug("{} created",n);

				}
		}
		else
		{
				try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("UPDATE announces SET startDate = ?, endDate =?,title=?,  description = ?, total = ?, currency = ?, stocksItem = ?, typeAnnounce = ?, fk_idcontact = ?, category=? , percentReduction=?, conditions=?, statusAnnounce=? WHERE id = ?;"))
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
					executeUpdate(pst);
					logger.debug(UPDATED,n);
				}

		}
		n.setUpdated(false);
		return n.getId();


	}
	@Override
	public void deleteAnnounce(Announce a) throws SQLException {
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("DELETE FROM announces where id=?")) {
			pst.setInt(1, a.getId());
			executeUpdate(pst);
		}
		logger.debug("{} is deleted",a);

	}
	protected void postCreation(Statement stat) throws SQLException
	{
		//do nothing
	}


	protected String notExistSyntaxt()
	{
		return IF_NOT_EXISTS;
	}



	public void createIndex(Statement stat) throws SQLException {
		stat.executeUpdate("CREATE INDEX idx_id ON cards (ID);");
		stat.executeUpdate("CREATE INDEX idx_ed ON cards (edition);");
		stat.executeUpdate("CREATE INDEX idx_col ON cards (collection);");
		stat.executeUpdate("CREATE INDEX idx_cprov ON cards (cardprovider);");
		stat.executeUpdate("CREATE INDEX idx_dateUpdt ON cards (dateUpdate);");

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
		stat.executeUpdate("CREATE INDEX idx_sld_ext ON sealed (extra);");

		stat.executeUpdate("CREATE INDEX idx_trx_statut ON transactions (statut);");
		stat.executeUpdate("CREATE INDEX idx_trx_msg ON transactions (message);");
		stat.executeUpdate("CREATE INDEX idx_trx_transpter ON transactions (transporter);");

		stat.executeUpdate("CREATE INDEX idx_ctc_name ON contacts (contact_name);");
		stat.executeUpdate("CREATE INDEX idx_ctc_lname ON contacts (contact_lastname);");
		stat.executeUpdate("CREATE INDEX idx_ctc_country ON contacts (contact_country);");
		stat.executeUpdate("CREATE INDEX idx_ctc_address ON contacts (contact_address);");
		stat.executeUpdate("CREATE INDEX idx_ctc_zip ON contacts (contact_zipcode);");
		stat.executeUpdate("CREATE INDEX idx_ctc_city ON contacts (contact_city);");
		stat.executeUpdate("CREATE INDEX idx_ctc_site ON contacts (contact_website);");
		stat.executeUpdate("CREATE INDEX idx_ctc_mail ON contacts (contact_email);");

		stat.executeUpdate("CREATE INDEX idx_dck_name ON decks (name);");
		stat.executeUpdate("CREATE INDEX idx_dck_tags ON decks (tags);");

		stat.executeUpdate("CREATE INDEX idx_alrt_ida ON alerts (id);");


		stat.executeUpdate("CREATE INDEX idx_conv_id ON conversionsItems (id);");
		stat.executeUpdate("CREATE INDEX idx_conv_name ON conversionsItems (name);");
		stat.executeUpdate("CREATE INDEX idx_conv_lang ON conversionsItems (lang);");
		stat.executeUpdate("CREATE INDEX idx_conv_source ON conversionsItems (source);");
		stat.executeUpdate("CREATE INDEX idx_conv_dest ON conversionsItems (destination);");

		stat.executeUpdate("CREATE INDEX idx_creationDate ON announces (creationDate);");
		stat.executeUpdate("CREATE INDEX idx_startDate ON announces (startDate);");
		stat.executeUpdate("CREATE INDEX idx_endDate ON announces (endDate);");
		stat.executeUpdate("CREATE INDEX idx_title ON announces (title);");
		stat.executeUpdate("CREATE INDEX idx_currency ON announces (currency);");
		stat.executeUpdate("CREATE INDEX idx_typeAnnounce ON announces (typeAnnounce);");
		stat.executeUpdate("CREATE INDEX idx_category ON announces (category);");
		stat.executeUpdate("CREATE INDEX idx_conditions ON announces (conditions);");
		stat.executeUpdate("CREATE INDEX idx_statusAnnounce ON announces (statusAnnounce);");




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
	public Map<String, String> getDefaultAttributes() {
		var map = new HashMap<String,String>();

		map.put(SERVERNAME, "localhost");
		map.put(SERVERPORT, "");
		map.put(DB_NAME, "mtgdesktopclient");
		map.put(LOGIN, "login");
		map.put(PASS, "pass");
		map.put(PARAMS, "");
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

	protected String getBoolean()
	{
		return BOOLEAN;
	}

	@Override
	public Map<String,Long> getDBSize() {
		String sql = getdbSizeQuery();
		var map = new HashMap<String,Long>();

		if(sql==null)
			return map;


		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(sql); ResultSet rs = executeQuery(pst);) {

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
		if(pool==null)
		{
			pool=new NoPool();
			logger.error("error loading selected pool. Use default : {}",pool.getName());
		}
		logger.info("Loading SQL connection to : {}",getjdbcUrl());
		pool.init(getjdbcUrl(),getString(LOGIN), getString(PASS),enablePooling());
		createDB();
	}



	@Override
	public void init() throws SQLException {
		logger.info("init {}",getName());
		init(getEnabledPlugin(MTGPool.class));
	}


	@Override
	public List<ConverterItem> listConversionItems() throws SQLException {
		List<ConverterItem> colls = new ArrayList<>();

		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT * from conversionsItems"))
		{
				var rs = executeQuery(pst);

				while (rs.next()) {
					var d = readConversionItem(rs);
					colls.add(d);
					notify(d);
				}
		}
		return colls;
	}

	@Override
	public void saveOrUpdateConversionItem(ConverterItem n) throws SQLException {
		if (n.getId() < 0)
		{
				try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("INSERT INTO conversionsItems (name, source, inputId, destination, outputId) VALUES (?, ?, ?, ?, ?)"))
				{
					pst.setString(1,n.getName());
					pst.setString(2, n.getSource());
					pst.setLong(3, n.getInputId());
					pst.setString(4, n.getDestination());
					pst.setLong(5, n.getOutputId());
					executeUpdate(pst);
					logger.debug("{} created",n.getName());
					n.setUpdated(false);
				}

		}
		else if(n.isUpdated())
		{
			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("UPDATE conversionsItems SET name = ?, source = ?, inputId = ?, destination = ?, outputId = ? WHERE id = ?"))
			{
				pst.setString(1,n.getName());
				pst.setString(2, n.getSource());
				pst.setLong(3, n.getInputId());
				pst.setString(4, n.getDestination());
				pst.setLong(5, n.getOutputId());
				pst.setLong(6, n.getId());
				executeUpdate(pst);
				logger.debug(UPDATED,n.getName());
				n.setUpdated(false);
			}

		}


	}

	@Override
	public void deleteConversionItem(ConverterItem n) throws SQLException {
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("DELETE FROM conversionsItems where id=?")) {
			pst.setLong(1, n.getId());
			executeUpdate(pst);
		}
		logger.debug("{} deleted",n);

	}



	@Override
	public List<MagicDeck> listDecks() throws SQLException {
		List<MagicDeck> colls = new ArrayList<>();

		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT * from decks"))
		{
				ResultSet rs = executeQuery(pst);

				while (rs.next()) {

						MagicDeck d = readDeck(rs);
						colls.add(d);
						notify(d);

				}
		}
		return colls;
	}

	@Override
	public MagicDeck getDeckById(Integer id) throws SQLException {
		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT * from decks where id=?"))
		{
				pst.setInt(1, id);
				ResultSet rs = executeQuery(pst);

				rs.next();
				return readDeck(rs);

		}
	}


	@Override
	public Integer saveOrUpdateDeck(MagicDeck d) throws SQLException {
		if (d.getId() < 0)
		{
				try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("INSERT INTO decks (description, name, dateCreation, dateUpdate, tags, commander, main, sideboard, averagePrice) VALUES (?,?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS))
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
					executeUpdate(pst);
					d.setId(getGeneratedKey(pst));
				}
				logger.debug("{} saved with id={}",d.getName(),d.getId());

		}
		else
		{
			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("UPDATE decks SET description = ?, name = ?, dateUpdate=?, tags= ?, commander= ?, main= ?, sideboard= ?, averagePrice= ? WHERE id= ?"))
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
				executeUpdate(pst);
			}
			logger.debug(UPDATED,d.getName());
		}

		return d.getId();
	}

	@Override
	public void deleteDeck(MagicDeck d) throws SQLException {
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("DELETE FROM decks where id=?")) {
			pst.setInt(1, d.getId());
			executeUpdate(pst);
		}
		logger.debug("Deck {} deleted",d.getName());
	}




	@Override
	public Contact getContactByLogin(String email, String password) throws SQLException {
		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT * from contacts where contact_email=? and contact_password=? and contact_active=?"))
		{
				pst.setString(1, email);
				pst.setString(2, IDGenerator.generateSha256(password));
				pst.setBoolean(3, true);
				ResultSet rs = executeQuery(pst);
				var res = rs.next();

				if(!res)
					throw new SQLException("No result Found");


				return readContact(rs);
		}
	}

	@Override
	public Contact getContactByEmail(String email) throws SQLException {
		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT * from contacts where contact_email=? and contact_active=?"))
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
					try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT * from contacts where id=?"))
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
		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT * from contacts"))
		{
				ResultSet rs = executeQuery(pst);

				while(rs.next())
					cts.add(readContact(rs));


		}

		return cts;

	}



	@Override
	public Transaction getTransaction(Long id) throws SQLException {
		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT * from transactions where id=?"))
		{
				pst.setLong(1, id);
				ResultSet rs = executeQuery(pst);

				rs.next();
				return readTransaction(rs);


		}

	}

	@Override
	public List<Transaction> listTransactions(Contact idct)  throws SQLException {
		List<Transaction> ctx = new ArrayList<>();

		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT * from transactions, contacts where fk_idcontact=? and contacts.id=transactions.fk_idcontact and contacts.contact_name=?"))
		{
			pst.setInt(1, idct.getId());
			pst.setString(2, idct.getName());
			ResultSet rs = executeQuery(pst);

				while (rs.next()) {
					ctx.add(readTransaction(rs));
				}
		}
		return ctx;
	}


	@Override
	public List<Transaction> listTransactions()  throws SQLException {
		List<Transaction> ctx  = new ArrayList<>();

		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT * from transactions");ResultSet rs = executeQuery(pst))
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
		contact.setActive(rs.getBoolean("contact_active"));
		contact.setEmailAccept(rs.getBoolean("emailAccept"));
		notify(contact);
		return contact;

	}


	private ConverterItem readConversionItem(ResultSet rs) throws SQLException {
		ConverterItem it = new ConverterItem();

			it.setId(rs.getInt("id"));
			it.setName(rs.getString("name"));
			it.setSource(rs.getString("source"));
			it.setInputId(rs.getLong("inputId"));
			it.setDestination(rs.getString("destination"));
			it.setOutputId(rs.getLong("outputId"));
		return it;
	}



	private MagicDeck readDeck(ResultSet rs) throws SQLException{

		var deck = new MagicDeck();

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
		state.setStatut(TransactionStatus.valueOf(rs.getString("statut")));
		state.setItems(readStockItemFrom(rs,"stocksItem"));
		state.setTransporter(rs.getString("transporter"));
		state.setShippingPrice(rs.getDouble("shippingPrice"));
		state.setCurrency(rs.getString("currency"));
		state.setContact(getContactById(rs.getInt("fk_idcontact")));
		state.setTransporterShippingCode(rs.getString("transporterShippingCode"));
		state.setDatePayment(rs.getTimestamp("datePayment"));
		state.setDateSend(rs.getTimestamp("dateSend"));

		if(rs.getString("typeTransaction")!=null)
			state.setTypeTransaction(TransactionDirection.valueOf(rs.getString("typeTransaction")));
		
		var pp = rs.getString("paymentProvider");
		if(pp!=null)
			state.setPaymentProvider(TransactionPayementProvider.valueOf(pp));



		return state;
	}


	@Override
	public boolean enableContact(String token) throws SQLException {
		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT * from contacts where temporaryToken=? and contact_active=false"))
		{
				pst.setString(1, token);
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
	public void changePassword(Contact ct, String newPassword) throws SQLException {
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("UPDATE contacts SET contact_password = ? WHERE contacts.id = ?;")) {
			logger.debug("Changing password for {}",ct);
			pst.setString(1, IDGenerator.generateSha256(newPassword));
			pst.setInt(2, ct.getId());
			executeUpdate(pst);
		}

	}


	@Override
	public void deleteContact(Contact t) throws SQLException {
		logger.debug("delete Contact {}",t );

		if(listTransactions(t).isEmpty()) {
			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("DELETE FROM contacts where id=?")) {
				pst.setInt(1, t.getId());
				executeUpdate(pst);
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
				try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("INSERT INTO contacts (contact_name, contact_lastname, contact_password, contact_telephone, contact_country, contact_address, contact_zipcode, contact_city, contact_website,contact_email, emailAccept, contact_active,temporaryToken ) VALUES (?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?);",Statement.RETURN_GENERATED_KEYS))
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
					pst.setBoolean(12,ct.isActive());
					pst.setString(13,ct.getTemporaryToken());
					executeUpdate(pst);
					ct.setId(getGeneratedKey(pst));
					logger.debug("save Contact with id={}",ct.getId());
					return ct.getId();
				}
		}
		else
		{
			logger.debug("update Contact {}",ct.getId());

			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("UPDATE contacts SET contact_name = ?, contact_lastname = ?, contact_telephone = ?, contact_country = ?, contact_address = ?, contact_zipcode=?, contact_city=?, contact_website = ?,contact_email=?,emailAccept=?, contact_active=?, temporaryToken=? WHERE contacts.id = ?;",Statement.RETURN_GENERATED_KEYS)) {
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

				executeUpdate(pst);
				return ct.getId();
			}
		}

	}


	@Override
	public Long saveOrUpdateTransaction(Transaction t) {
		if (t.getId() < 0)
		{

				logger.debug("save transaction ");

				try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("INSERT INTO transactions (dateTransaction, message, stocksItem, statut,transporter,shippingPrice,transporterShippingCode, currency,datePayment,dateSend,paymentProvider, fk_idcontact,sourceShopId, sourceShopName,typeTransaction) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",Statement.RETURN_GENERATED_KEYS)) {
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
					
					executeUpdate(pst);
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

			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("UPDATE transactions SET statut = ?, transporter=?, shippingPrice=?, transporterShippingCode=?,stocksItem=?,datePayment=?,dateSend=?,paymentProvider=?, sourceShopId=?, sourceShopName=? WHERE id = ?",Statement.RETURN_GENERATED_KEYS)) {
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
				pst.setLong(11, t.getId());
				
				
				
				executeUpdate(pst);
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
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("DELETE FROM transactions where id=?")) {
			pst.setLong(1, t.getId());
			executeUpdate(pst);
		}

	}


	@Override
	public void deleteTransaction(List<Transaction> state) throws SQLException {
		logger.debug("remove transactions : {} items ",state.size());
		var st = new StringBuilder();
		st.append("DELETE FROM transactions where id IN (");
		for (Transaction sto : state) {
			st.append(sto.getId()).append(",");
			notify(sto);
		}
		st.append(")");
		String sql = st.toString().replace(",)", ")");
		try (var c = pool.getConnection();Statement pst = c.createStatement()) {
			pst.executeUpdate(sql);
		}
	}



	@Override
	public void deleteStock(SealedStock state) throws SQLException {
		logger.debug("delete {} in sealed stock",state.getId());
		var sql = "DELETE FROM sealed WHERE id=?";
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(sql))
		{
			pst.setLong(1, state.getId());
			executeUpdate(pst);
			notify(state);
		}

	}

	@Override
	public SealedStock getSealedStockById(Long id) throws SQLException {
		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT * from sealed where id=?"))
		{
				pst.setLong(1, id);
				ResultSet rsC = executeQuery(pst);
				rsC.next();
				return readSealed(rsC);
		}
	}


	@Override
	public List<SealedStock> listSealedStocks() throws SQLException {
		List<SealedStock> colls = new ArrayList<>();

		try (var c = pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT * from sealed");ResultSet rs = executeQuery(pst))
		{
				while (rs.next()) {
					var state = readSealed(rs);
					colls.add(state);
				}
		}
		return colls;
	}

	@Override
	public void saveOrUpdateSealedStock(SealedStock state) throws SQLException {

		if (state.getId() < 0) {

			logger.debug("save sealed {}",state);
			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(
					"INSERT INTO sealed (edition, qte, comment, lang, typeProduct, conditionProduct,extra,collection,price,tiersAppIds,numversion ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",Statement.RETURN_GENERATED_KEYS)) {
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
				pst.setDouble(9, state.getPrice());
				storeTiersApps(pst,10,state.getTiersAppIds());
				pst.setInt(11, state.getProduct().getNum());

				executeUpdate(pst);
				state.setId(getGeneratedKey(pst));
			} catch (Exception e) {
				logger.error("error insert", e);
			}
		} else {
			logger.debug("update Sealed {}",state);
			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(
					"update sealed set edition=?, qte=?, comment=?, lang=?, typeProduct=?, conditionProduct=?, collection=?, price=?, tiersAppIds=?where id=?")) {
				pst.setString(1, String.valueOf(state.getProduct().getEdition().getId()));
				pst.setInt(2, state.getQte());
				pst.setString(3, state.getComment());
				pst.setString(4, state.getLanguage());
				pst.setString(5, state.getProduct().getTypeProduct().name());
				pst.setString(6, state.getCondition().name());
				pst.setString(7, (state.getMagicCollection()==null)?MTGControler.getInstance().get(DEFAULT_LIBRARY):state.getMagicCollection().getName());
				pst.setDouble(8, state.getPrice());
				storeTiersApps(pst,9,state.getTiersAppIds());
				pst.setLong(10, state.getId());
				executeUpdate(pst);

				state.setUpdated(false);
			} catch (Exception e) {
				logger.error("error update",e);
			}
		}
		notify(state);

	}

	@Override
	public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException {
		logger.debug("saving {} in {}",mc,collection);

		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("insert into cards values (?,?,?,?,?,?)")) {
			pst.setString(1, IDGenerator.generate(mc));
			storeCard(pst, 2, mc);
			pst.setString(3, mc.getCurrentSet().getId());
			pst.setString(4, getEnabledPlugin(MTGCardsProvider.class).toString());
			pst.setString(5, collection.getName());
			pst.setTimestamp(6, new Timestamp(new java.util.Date().getTime()));
			executeUpdate(pst);
		}
	}

	@Override
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
		logger.debug("delete {} in {}",mc,collection);
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("DELETE FROM cards where id=? and edition=? and collection=?")) {
			pst.setString(1, IDGenerator.generate(mc));
			pst.setString(2, mc.getCurrentSet().getId());
			pst.setString(3, collection.getName());
			executeUpdate(pst);
		}
	}

	@Override
	public void moveCard(MagicCard mc, MagicCollection from, MagicCollection to) throws SQLException {
		logger.debug("move {} from {} to {}",mc,from,to);

		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("update cards set collection= ? where id=? and collection=?"))
		{
			pst.setString(1, to.getName());
			pst.setString(2, IDGenerator.generate(mc));
			pst.setString(3, from.getName());
			int res = executeUpdate(pst);

			logger.debug("move result:{}={}",mc, res);
		}

		listStocks(mc, from,true).forEach(cs->{

			try {
				cs.setMagicCollection(to);
				saveOrUpdateCardStock(cs);
			} catch (SQLException e) {
				logger.error("Error saving stock for {} from {} to {}",mc,from,to);
			}
		});

	}

	@Override
	public void moveEdition(MagicEdition ed, MagicCollection from, MagicCollection to) throws SQLException {
		logger.debug("move {} from {} to {}",ed,from,to);

		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("update cards set collection= ? where edition=? and collection=?"))
		{
			pst.setString(1, to.getName());
			pst.setString(2, ed.getId());
			pst.setString(3, from.getName());
			int res = executeUpdate(pst);

			logger.debug("move result:{}={}",ed, res);
		}


	}
	

	@Override
	public List<MagicCard> listCards() throws SQLException {
		List<MagicCard> listCards = new ArrayList<>();
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT mcard,dateUpdate FROM cards"); ResultSet rs = executeQuery(pst);) {
			while (rs.next()) {
				listCards.add(readCard(rs,MCARD));
			}
		}
		return listCards;
	}

	@Override
	public Map<String, Integer> getCardsCountGlobal(MagicCollection col) throws SQLException {
		Map<String, Integer> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT edition, count(1) FROM cards where collection=? group by edition");) {
			pst.setString(1, col.getName());
			try (ResultSet rs = executeQuery(pst)) {
				while (rs.next())
					map.put(rs.getString(1), rs.getInt(2));
			}
		}
		return map;
	}

	@Override
	public int getCardsCount(MagicCollection cols, MagicEdition me) throws SQLException {

		var sql = "SELECT count(ID) FROM cards ";

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

		var ret = new ArrayList<MagicCard>();
		var sql = "SELECT mcard,dateUpdate FROM cards where collection= ?";

		if (me != null)
			sql = "SELECT mcard,dateUpdate FROM cards where collection= ? and edition = ?";

		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(sql)) {
			pst.setString(1, collection.getName());
			if (me != null)
				pst.setString(2, me.getId());
			try (ResultSet rs = executeQuery(pst)) {
				while (rs.next()) {
					var mc = readCard(rs,MCARD);
					ret.add(mc);
					notify(mc);
				}
			}
		}
		return ret;
	}

	@Override
	public List<String> listEditionsIDFromCollection(MagicCollection collection) throws SQLException {
		var sql = "SELECT distinct(edition) FROM cards where collection=?";
		var retour = new ArrayList<String>();
		logger.trace(sql);
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(sql)) {
			pst.setString(1, collection.getName());
			try (ResultSet rs = executeQuery(pst)) {

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
			try (ResultSet rs = executeQuery(pst)) {
				if (rs.next()) {
					return new MagicCollection(rs.getString("name"));
				}
				return null;
			}
		}
	}

	@Override
	public void saveCollection(MagicCollection col) throws SQLException {
		listCollections.put(col.getName(),col);
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("insert into collections values (?)")) {
			pst.setString(1, col.getName().replace("'", "\'"));
			executeUpdate(pst);
		}
	}

	@Override
	public void removeCollection(MagicCollection col) throws SQLException {

		listCollections.remove(col.getName());

		if (col.getName().equals(MTGControler.getInstance().get(DEFAULT_LIBRARY)))
			throw new SQLException(col.getName() + " can not be deleted");

		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("DELETE FROM collections where name = ?")) {
			pst.setString(1, col.getName());
			executeUpdate(pst);
		}

		try (var c = pool.getConnection(); PreparedStatement pst2 = c.prepareStatement("DELETE FROM cards where collection = ?")) {
			pst2.setString(1, col.getName());
			executeUpdate(pst2);
		}
	}
	
	
	
	@Override
	public List<MagicCardAlert> listAlerts() {
		
		if(!listAlerts.isEmpty())
			return listAlerts.values();
		
		
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM alerts")) {
			try (ResultSet rs = executeQuery(pst)) {
				while (rs.next()) {
					var alert = new MagicCardAlert();
					alert.setCard(readCard(rs,MCARD));
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
		return listAlerts.values();
		
		
	}

	@Override
	public List<MagicCollection> listCollections() throws SQLException {

		if(!listCollections.isEmpty())
			return listCollections.values();


		try (var c =  pool.getConnection();PreparedStatement pst = c.prepareStatement("SELECT * FROM collections"))
		{
			try (ResultSet rs = executeQuery(pst)) {
				while (rs.next()) {
					listCollections.put(rs.getString(1),new MagicCollection(rs.getString(1)));
				}
			}
		}
		return listCollections.values();
	}

	@Override
	public void removeEdition(MagicEdition me, MagicCollection col) throws SQLException {
		logger.debug("delete {} from {}",me,col);
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("DELETE FROM cards where edition=? and collection=?")) {
			pst.setString(1, me.getId());
			pst.setString(2, col.getName());
			executeUpdate(pst);
		}
	}

	@Override
	public List<MagicCollection> listCollectionFromCards(MagicCard mc) throws SQLException {
		var cols = new ArrayList<MagicCollection>();

		if (mc.getEditions().isEmpty())
			throw new SQLException("No edition defined");

		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT collection FROM cards WHERE id=? and edition=?")) {
			String id = IDGenerator.generate(mc);

			logger.trace("SELECT collection FROM cards WHERE id={} and edition='{}'",id,mc.getCurrentSet().getId());


			pst.setString(1, id);
			pst.setString(2, mc.getCurrentSet().getId());
			try (ResultSet rs = executeQuery(pst)) {

				while (rs.next()) {
					cols.add(new MagicCollection(rs.getString(COLLECTION)));
				}
			}
		}
		return cols;
	}

	@Override
	public void deleteStock(List<MagicCardStock> state) throws SQLException {
		logger.debug("remove {} items in stock",state.size());
		var st = new StringBuilder();
		st.append("DELETE FROM stocks where idstock IN (");
		for (MagicCardStock sto : state) {
			st.append(sto.getId()).append(",");
			notify(sto);
		}
		st.append(")");
		String sql = st.toString().replace(",)", ")");
		try (var c = pool.getConnection();var pst = c.prepareStatement(sql)) {
			executeUpdate(pst);
		}
	}




	@Override
	public List<MagicCardStock> listStocks(MagicCard mc, MagicCollection col,boolean editionStrict) throws SQLException {

		String sql = createListStockSQL();

		if(editionStrict)
			sql ="SELECT * FROM stocks where collection=? and idmc=?";

		logger.trace("sql={}",sql);

		List<MagicCardStock> colls = new ArrayList<>();
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(sql)) {
			pst.setString(1, col.getName());

			if(editionStrict)
				pst.setString(2, IDGenerator.generate(mc));
			else if (!isJsonCompatible())
				pst.setString(2, "%"+mc.getName()+"%");
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
	public List<MagicCardStock> listStocks(List<MagicCollection> cols) throws SQLException {
		List<MagicCardStock> colls = new ArrayList<>();

		var stmt = String.format("SELECT * FROM stocks where collection in  (%s)",cols.stream().map(c->"'"+c.getName()+"'").collect(Collectors.joining(", ")));
		logger.trace("loading stock with SQL={}",stmt);
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(stmt); ResultSet rs = executeQuery(pst);) {
			while (rs.next()) {
				var state = readStock(rs);
				colls.add(state);
			}
			logger.debug("loading {} item(s) from stock for {}",colls.size(),cols);
		}
		return colls;
	}


	@Override
	public MagicCardStock getStockById(Long id) throws SQLException {


		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM stocks where idstock=?")) {
			pst.setLong(1, id);
			var rs = executeQuery(pst);

			rs.next();

			return readStock(rs);
		}
	}




	@Override
	public List<MagicCardStock> listStocks() throws SQLException {
		List<MagicCardStock> colls = new ArrayList<>();
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM stocks"); ResultSet rs = executeQuery(pst);) {
			while (rs.next()) {
				var state = readStock(rs);
				colls.add(state);

			}
			logger.debug("load {} item(s) from stock",colls.size());
		}
		return colls;
	}

	private int getGeneratedKey(PreparedStatement pst) {

		try (ResultSet rs = pst.getGeneratedKeys()) {
			rs.next();
				return rs.getInt(1);

		} catch (Exception e) {
			logger.error("couldn't retrieve id : {}",e.getMessage());
		}
		return -1;

	}

	@Override
	public void saveOrUpdateCardStock(MagicCardStock state) throws SQLException {

		if (state.getId() < 0) {
			logger.debug("save stock {}",state);
			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(
					"insert into stocks  ( conditions,foil,signedcard,langage,qte,comments,idmc,collection,mcard,altered,price,grading,tiersAppIds,etched) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS)) {
				pst.setString(1, String.valueOf(state.getCondition()));
				pst.setBoolean(2, state.isFoil());
				pst.setBoolean(3, state.isSigned());
				pst.setString(4, state.getLanguage());
				pst.setInt(5, state.getQte());
				pst.setString(6, state.getComment());
				pst.setString(7, IDGenerator.generate(state.getProduct()));
				pst.setString(8, String.valueOf(state.getMagicCollection()));
				storeCard(pst, 9, state.getProduct());
				pst.setBoolean(10, state.isAltered());
				pst.setDouble(11, state.getPrice());
				storeGrade(pst,12, state.getGrade());
				storeTiersApps(pst,13, state.getTiersAppIds());
				pst.setBoolean(14, state.isEtched());
				executeUpdate(pst);
				state.setId(getGeneratedKey(pst));
			} catch (Exception e) {
				logger.error(e);
			}
		} else {
			logger.debug("update Stock {}",state);
			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(
					"update stocks set comments=?, conditions=?, foil=?,signedcard=?,langage=?, qte=? ,altered=?,price=?,idmc=?,collection=?,grading=?,tiersAppIds=?,etched=?, mcard=? where idstock=?")) {
				pst.setString(1, state.getComment());
				pst.setString(2, state.getCondition().toString());
				pst.setBoolean(3, state.isFoil());
				pst.setBoolean(4, state.isSigned());
				pst.setString(5, state.getLanguage());
				pst.setInt(6, state.getQte());
				pst.setBoolean(7, state.isAltered());
				pst.setDouble(8, state.getPrice());
				pst.setString(9, IDGenerator.generate(state.getProduct()));
				pst.setString(10, state.getMagicCollection().getName());
				storeGrade(pst, 11,state.getGrade());
				storeTiersApps(pst, 12,state.getTiersAppIds());
				pst.setBoolean(13, state.isEtched());
				storeCard(pst, 14, state.getProduct());
				pst.setLong(15, state.getId());
				executeUpdate(pst);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		notify(state);
	}



	@Override
	public void saveAlert(MagicCardAlert alert) throws SQLException {

		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("insert into alerts ( id,mcard,amount,qte) values (?,?,?,?)")) {

			alert.setId(IDGenerator.generate(alert.getCard()));

			pst.setString(1, alert.getId());
			storeCard(pst, 2, alert.getCard());
			pst.setDouble(3, alert.getPrice());
			pst.setInt(4, alert.getQty());
			executeUpdate(pst);
			logger.debug("save alert for {} ({})",alert.getCard(),alert.getCard().getCurrentSet());
			listAlerts.put(alert.getId(),alert);
		}
	}

	@Override
	public void updateAlert(MagicCardAlert alert) throws SQLException {
		logger.debug("update alert {}",alert);
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("update alerts set amount=?,mcard=?,foil=?, qte=? where id=?")) {
			pst.setDouble(1, alert.getPrice());
			storeCard(pst, 2, alert.getCard());
			pst.setBoolean(3, alert.isFoil());
			pst.setInt(4, alert.getQty());
			pst.setString(5, alert.getId());
			executeUpdate(pst);
		}

	}

	@Override
	public void deleteAlert(MagicCardAlert alert) throws SQLException
	{
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("DELETE FROM alerts where id=?")) {
			pst.setString(1, alert.getId());
			int res = executeUpdate(pst);
			logger.debug("delete alert {} = {}", alert,res);
		}

		if (listAlerts != null)
			listAlerts.remove(alert.getId());
	}

	@Override
	public List<MagicNews> listNews() {
		List<MagicNews> news = new ArrayList<>();
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM news")) {
			try (ResultSet rs = executeQuery(pst)) {
				while (rs.next()) {
					var n = new MagicNews();
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
		logger.debug("delete news {}",n);
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("DELETE FROM news where id=?")) {
			pst.setInt(1, n.getId());
			executeUpdate(pst);
		}
	}

	@Override
	public void saveOrUpdateNews(MagicNews n) throws SQLException {
		if (n.getId() < 0) {

			logger.debug("save {}",n);
			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(
					"insert into news  ( name,categorie,url,typeNews) values (?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS)) {
				pst.setString(1, n.getName());
				pst.setString(2, n.getCategorie());
				pst.setString(3, n.getUrl());
				pst.setString(4, n.getProvider().getName());
				executeUpdate(pst);
				n.setId(getGeneratedKey(pst));
			}

		} else {
			logger.debug("update {}",n);
			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("update news set name=?, categorie=?, url=?,typeNews=? where id=?")) {
				pst.setString(1, n.getName());
				pst.setString(2, n.getCategorie());
				pst.setString(3, n.getUrl());
				pst.setString(4, n.getProvider().getName());
				pst.setInt(5, n.getId());
				executeUpdate(pst);
			} catch (Exception e) {
				logger.error(e);
			}
		}

	}
	
	
	@Override
	public List<OrderEntry> listOrders() {
		if(!listOrders.isEmpty())
			return listOrders.values();
		
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM orders"); ResultSet rs = executeQuery(pst);)
		{
			while (rs.next()) {
				var state = new OrderEntry();

				state.setId(rs.getInt("id"));
				state.setIdTransation(rs.getString("idTransaction"));
				state.setDescription(rs.getString("description"));

				setEdition(state,rs);
				state.setCurrency(Currency.getInstance(rs.getString("currency")));
				state.setTransactionDate(rs.getDate("transactionDate"));
				state.setItemPrice(rs.getDouble("itemPrice"));
				state.setShippingPrice(rs.getDouble("shippingPrice"));
				state.setType(EnumItems.valueOf(rs.getString("typeItem")));
				state.setTypeTransaction(TransactionDirection.valueOf(rs.getString("typeTransaction")));
				state.setSource(rs.getString("sources"));
				state.setSeller(rs.getString("seller"));
				state.setUpdated(false);
				listOrders.put(state.getId(),state);
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		
		return listOrders.values();
		
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
		logger.debug("remove {} items in orders",state.size());
		var st = new StringBuilder();
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
			logger.debug("save order {}",state);
			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("INSERT INTO orders (idTransaction, description, edition, itemPrice, shippingPrice, currency, transactionDate, typeItem, typeTransaction, sources, seller) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",Statement.RETURN_GENERATED_KEYS)) {

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
				executeUpdate(pst);
				state.setId(getGeneratedKey(pst));
				listOrders.put(state.getId(),state);
			} catch (Exception e) {
				logger.error("error insert {} : {}",state.getDescription() , e);
			}
		} else {
			logger.debug("update order {}",state);
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
				executeUpdate(pst);
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
			executeUpdate(pst);
		}

	}



	private DAOInfo buildInfo(Statement pst)
	{
		var start=Instant.now();
		var daoInfo = new DAOInfo();

		TechnicalServiceManager.inst().store(daoInfo);

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


	private int executeUpdate(PreparedStatement pst) throws SQLException {
		var daoInfo=buildInfo(pst);

		try {
			int rs = pst.executeUpdate();
			daoInfo.setEnd(Instant.now());
			return rs;
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

	private Announce readAnnounce(ResultSet rs) throws SQLException {
		var a = new Announce();
			  a.setId(rs.getInt("id"));
			  a.setStartDate(rs.getTimestamp("startDate"));
			  a.setEndDate(rs.getTimestamp("endDate"));
			  a.setTitle(rs.getString("title"));
			  a.setDescription(rs.getString("description"));
			  a.setTotalPrice(rs.getDouble("total"));
			  a.setCurrency(Currency.getInstance(rs.getString("currency")));
			  a.setContact(getContactById(rs.getInt("fk_idcontact")));
			  a.setCreationDate(rs.getTimestamp("creationDate"));
			  a.setType(TransactionDirection.valueOf(rs.getString("typeAnnounce")));
			  a.setCategorie(EnumItems.valueOf(rs.getString("category")));
			  a.setPercentReduction(rs.getDouble("percentReduction"));
			  a.setCondition(EnumCondition.valueOf(rs.getString("conditions")));
			  a.setStatus(STATUS.valueOf(rs.getString("statusAnnounce")));
			  if(rs.getObject("stocksItem")!=null)
				  a.setItems(readStockItemFrom(rs,"stocksItem"));

		return a;
	}

	private SealedStock readSealed(ResultSet rs) throws SQLException {
		var state = new SealedStock();
		state.setComment(rs.getString("comment"));
		state.setId(rs.getInt("id"));
		state.setQte(rs.getInt("qte"));

		int ref = rs.getInt("numversion");

		  try
		  {
			var list = MTG.getEnabledPlugin(MTGSealedProvider.class).get(getEnabledPlugin(MTGCardsProvider.class).getSetById(rs.getString(EDITION)),EnumItems.valueOf(rs.getString("typeProduct")),(rs.getString(EXTRA_TYPE)==null) ? null : MTGSealedProduct.EXTRA.valueOf(rs.getString(EXTRA_TYPE)));
			MTGSealedProduct product = list.stream().filter(p->p.getNum()==ref).findFirst().orElse(list.get(0));
			state.setProduct(product);
		  }
		  catch (Exception e)
		  {
			logger.error("Error loading Packaging for {} {} {} : {}",rs.getString("typeProduct"),rs.getString(EXTRA_TYPE),rs.getString(EDITION),e);
		  }

		  state.setCondition(EnumCondition.valueOf(rs.getString("conditionProduct")));
		  state.setMagicCollection(new MagicCollection(rs.getString(COLLECTION)));
		  state.setPrice(rs.getDouble("price"));
		  state.setTiersAppIds(readTiersApps(rs));
		  state.setLanguage(rs.getString("lang"));

		 return state;
	}

	private MagicCardStock readStock(ResultSet rs) throws SQLException
	{
		var state = new MagicCardStock(readCard(rs,MCARD));
			state.setComment(rs.getString("comments"));
			state.setId(rs.getInt("idstock"));
			state.setMagicCollection(new MagicCollection(rs.getString(COLLECTION)));
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


}

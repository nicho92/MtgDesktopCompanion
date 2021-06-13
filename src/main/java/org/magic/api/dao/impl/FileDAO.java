package org.magic.api.dao.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.magic.api.beans.Contact;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicNews;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.SealedStock;
import org.magic.api.beans.Transaction;
import org.magic.api.interfaces.abstracts.AbstractMagicDAO;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.FileTools;
import org.magic.tools.IDGenerator;

public class FileDAO extends AbstractMagicDAO{

	private File directory;
	private static final String CARDSDIR = "cards";
	private static final String STOCKDIR = "stocks";
	private static final String ALERTSDIR = "alerts";
	private static final String NEWSDIR = "news";
	private static final String ORDERSDIR = "orders";
	private static final String PACKAGESSDIR = "sealed";
	private static final String TRANSACTIONSDIR = "transactions";
	private static final String CONTACTSSDIR = "contacts";
	

	@Override
	public void deleteStock(SealedStock s) throws SQLException {
		var f = Paths.get(directory.getAbsolutePath(), PACKAGESSDIR,String.valueOf(s.getId())).toFile();
			try {
				FileTools.deleteFile(f);
				notify(s);
			} catch (IOException e) {
				throw new SQLException(e);
			}
		
	}
	
	@Override
	public List<SealedStock> listSeleadStocks() throws SQLException {
		List<SealedStock> st = new ArrayList<>();
		var f = new File(directory, PACKAGESSDIR);
		for (File fstock : f.listFiles()) {
			try {
				SealedStock s = read(SealedStock.class, fstock);
				st.add(s);

			} catch (Exception e) {
				throw new SQLException(e);
			}
		}

		return st;
	}
	
	@Override
	public void saveOrUpdateStock(SealedStock state) throws SQLException {
		var f = new File(directory, PACKAGESSDIR);

		if (state.getId() == -1)
			state.setId(f.listFiles().length + 1);

		f = new File(f, String.valueOf(state.getId()));
		try {
			save(state, f);
			notify(state);
		} catch (Exception e) {
			throw new SQLException(e);
		}
		
	}
	
	
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEPRECATED;
	}
	

	public <T> T read(Class<T> c, File f) throws IOException {
		return serialiser.fromJson(FileTools.readFile(f), c);
	}

	public void save(Object o, File f) throws IOException {
		FileTools.saveFile(f, serialiser.toJson(o));
	}
	
	@Override
	public void init() {
		directory = getFile("URL");

		if (!directory.exists())
			directory.mkdir();

		new File(directory, CARDSDIR).mkdir();
		new File(directory, ALERTSDIR).mkdir();
		new File(directory, STOCKDIR).mkdir();
		new File(directory, NEWSDIR).mkdir();
		new File(directory, ORDERSDIR).mkdir();
		new File(directory, PACKAGESSDIR).mkdir();
		
		new File(new File(directory, CARDSDIR), MTGControler.getInstance().get("default-library")).mkdir();
		logger.debug("File DAO init");
	}

	@Override
	public String getName() {
		return "File";
	}

	private String removeCon(String a) {
		if (a.equalsIgnoreCase("con"))
			return a + "_set";

		return a;
	}

	@Override
	public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException {
		var f = new File(new File(directory, CARDSDIR), collection.getName());

		if (!f.exists())
			f.mkdir();

		f = new File(f, removeCon(mc.getCurrentSet().getId()));
		if (!f.exists())
			f.mkdir();

		f = new File(f, IDGenerator.generate(mc));

		try {
			save(mc, f);
		} catch (Exception e) {
			throw new SQLException(e);
		}

	}

	@Override
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
		var f = Paths.get(directory.getAbsolutePath(), CARDSDIR, collection.getName(),
				removeCon(mc.getCurrentSet().getId()), IDGenerator.generate(mc)).toFile();
		var parent = f.getParentFile();

		if (f.exists())
			FileUtils.deleteQuietly(f);

		if (parent.listFiles().length == 0)
			FileUtils.deleteQuietly(parent);

	}

	@Override
	public List<MagicCard> listCards() throws SQLException {
		return new ArrayList<>();
	}

	@Override
	public int getCardsCount(MagicCollection list, MagicEdition me) throws SQLException {
		var f = new File(new File(directory, CARDSDIR), list.getName());

		if (me != null)
			f = new File(f, removeCon(me.getId()));

		return FileUtils.listFiles(f, null, true).size();
	}

	@Override
	public Map<String, Integer> getCardsCountGlobal(MagicCollection c) throws SQLException {
		Map<String, Integer> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		var eds = new File(new File(directory, CARDSDIR), c.getName());
		for (File ed : eds.listFiles())
			map.put(removeCon(ed.getName()), ed.listFiles().length);

		return map;
	}

	@Override
	public List<MagicCard> listCardsFromCollection(MagicCollection c) throws SQLException {
		return listCardsFromCollection(c, null);
	}

	@Override
	public List<MagicCard> listCardsFromCollection(MagicCollection c, MagicEdition me) throws SQLException {
		var col = new File(new File(directory, CARDSDIR), c.getName());

		if (me != null)
			col = new File(col, removeCon(me.getId()));

		logger.debug("Load " + col);

		List<MagicCard> ret = new ArrayList<>();

		for (var f : FileUtils.listFilesAndDirs(col, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
			try {
				if (!f.isDirectory())
					ret.add(read(MagicCard.class, f));
			} catch (Exception e) {
				throw new SQLException(e);
			}
		}

		return ret;
	}

	@Override
	public List<MagicCollection> listCollectionFromCards(MagicCard mc) throws SQLException {

		String id = IDGenerator.generate(mc);
		var f = new File(directory, CARDSDIR);
		List<MagicCollection> ret = new ArrayList<>();
		Collection<File> res = FileUtils.listFiles(f, new NameFileFilter(id), TrueFileFilter.INSTANCE);

		for (File result : res)
			ret.add(new MagicCollection(result.getParentFile().getParentFile().getName()));

		return ret;
	}

	@Override
	public List<String> listEditionsIDFromCollection(MagicCollection c) throws SQLException {
		var col = new File(new File(directory, CARDSDIR), c.getName());
		List<String> ret = new ArrayList<>();

		for (File f : col.listFiles())
			ret.add(f.getName());

		return ret;
	}

	@Override
	public MagicCollection getCollection(String name) throws SQLException {
		return new MagicCollection(name);
	}

	@Override
	public void saveCollection(MagicCollection c) throws SQLException {
		var f = new File(new File(directory, CARDSDIR), c.getName());

		if (!f.exists())
			f.mkdir();

	}

	@Override
	public void removeCollection(MagicCollection c) throws SQLException {
		var f = new File(new File(directory, CARDSDIR), c.getName());

		if (f.exists())
			try {
				FileUtils.deleteDirectory(f);
			} catch (IOException e) {
				throw new SQLException(e);
			}

	}

	@Override
	public List<MagicCollection> listCollections() throws SQLException {

		ArrayList<MagicCollection> ret = new ArrayList<>();

		for (File f : new File(directory, CARDSDIR).listFiles())
			ret.add(new MagicCollection(f.getName()));

		return ret;
	}

	@Override
	public void removeEdition(MagicEdition ed, MagicCollection col) throws SQLException {

		var f = Paths.get(directory.getAbsolutePath(), CARDSDIR, col.getName(), removeCon(ed.getId())).toFile();

		if (f.exists())
			try {
				FileUtils.deleteDirectory(f);
			} catch (IOException e) {
				throw new SQLException(e);
			}

	}

	@Override
	public List<MagicCardStock> listStocks(MagicCard mc, MagicCollection col,boolean editionStrict) throws SQLException {
		List<MagicCardStock> st = new ArrayList<>();
		var f = new File(directory, STOCKDIR);
		for (File fstock : FileUtils.listFiles(f, new WildcardFileFilter("*" + IDGenerator.generate(mc)),TrueFileFilter.INSTANCE)) {
			try {
				MagicCardStock s = read(MagicCardStock.class, fstock);
				if (s.getMagicCollection().getName().equals(col.getName()))
					st.add(s);

			} catch (Exception e) {
				throw new SQLException(e);
			}
		}

		return st;
	}

	@Override
	public void saveOrUpdateStock(MagicCardStock state) throws SQLException {

		var f = new File(directory, STOCKDIR);

		if (state.getIdstock() == -1)
			state.setIdstock(f.listFiles().length + 1);

		f = new File(f, state.getIdstock() + "-" + IDGenerator.generate(state.getMagicCard()));
		try {
			save(state, f);
			notify(state);
		} catch (Exception e) {
			throw new SQLException(e);
		}

	}

	@Override
	public void deleteStock(List<MagicCardStock> state) throws SQLException {

		for (MagicCardStock s : state) {
			var f = Paths.get(directory.getAbsolutePath(), STOCKDIR,
					s.getIdstock() + "-" + IDGenerator.generate(s.getMagicCard())).toFile();
			logger.debug("Delete " + f);
			try {
				FileTools.deleteFile(f);
				notify(s);
			} catch (IOException e) {
				throw new SQLException(e);
			}
			
		}
	}

	@Override
	public List<MagicCardStock> listStocks() throws SQLException {
		List<MagicCardStock> ret = new ArrayList<>();

		for (File f : FileUtils.listFiles(new File(directory, STOCKDIR), null, false)) {
			try {
				ret.add(read(MagicCardStock.class, f));
			} catch (Exception e) {
				logger.error("Error reading stocks", e);
			}
		}
		return ret;
	}
	

	@Override
	public void initOrders(){
		for (File f : FileUtils.listFiles(new File(directory, ORDERSDIR), null, false)) {
			try {
				OrderEntry o = read(OrderEntry.class, f);
				listOrders.put(o.getId(),o);
			} catch (Exception e) {
				logger.error("Error reading OrderEntry", e);
			}
		}
	}

	@Override
	public void saveOrUpdateOrderEntry(OrderEntry state) throws SQLException {
		var f = new File(directory, ORDERSDIR);

		if (state.getId() == -1 || state.getId()==null)
			state.setId(f.listFiles().length + 1);

		f = new File(f, String.valueOf(state.getId()));
		try {
			save(state, f);
		} catch (Exception e) {
			throw new SQLException(e);
		}
		
	}

	@Override
	public void deleteOrderEntry(List<OrderEntry> state) throws SQLException {
		for (OrderEntry s : state) {
			var f = Paths.get(directory.getAbsolutePath(), ORDERSDIR,String.valueOf(s.getId())).toFile();
			logger.debug("Delete " + f);
			try {
				FileTools.deleteFile(f);
			} catch (IOException e) {
				throw new SQLException(e);
			}
		}
		
	}

	
	@Override
	public void initAlerts() {
		for (var f : FileUtils.listFiles(new File(directory, ALERTSDIR), null, false)) {
			try {
				MagicCardAlert a = read(MagicCardAlert.class, f);
				
				listAlerts.put(a.getId(),a);
			} catch (Exception e) {
				logger.error("Error reading alert", e);
			}
		}
	}

	@Override
	public void saveAlert(MagicCardAlert alert) throws SQLException {
		try {
			save(alert, Paths.get(directory.getAbsolutePath(), ALERTSDIR,IDGenerator.generate(alert.getCard())).toFile());
		} catch (IOException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void deleteAlert(MagicCardAlert alert) throws SQLException {
		var f = Paths.get(directory.getAbsolutePath(), ALERTSDIR,IDGenerator.generate(alert.getCard())).toFile();
		if (f.exists())
			FileUtils.deleteQuietly(f);
	}

	@Override
	public void updateAlert(MagicCardAlert alert) throws SQLException {
		saveAlert(alert);

	}

	@Override
	public boolean hasAlert(MagicCard mc) {
		return !FileUtils.listFiles(new File(directory, ALERTSDIR), new NameFileFilter(IDGenerator.generate(mc)),
				TrueFileFilter.INSTANCE).isEmpty();
	}

	@Override
	public String getDBLocation() {
		return directory.getAbsolutePath();
	}

	@Override
	public long getDBSize() {
		return FileUtils.sizeOf(directory);
	}

	@Override
	public void backup(File dir) throws IOException {
		FileUtils.copyDirectory(directory, dir);
	}

	@Override
	public List<MagicNews> listNews() {
		List<MagicNews> ret = new ArrayList<>();

		for (var f : FileUtils.listFiles(new File(directory, NEWSDIR), null, false)) {
			try {
				ret.add(read(MagicNews.class, f));
			} catch (Exception e) {
				logger.error("Error reading news", e);
			}
		}
		return ret;
	}

	@Override
	public void deleteNews(MagicNews n) {
		var dir = new File(new File(directory, NEWSDIR), n.getCategorie());
		var f = new File(dir, n.getId() + "-" + n.getName());
		try {
			FileTools.deleteFile(f);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@Override
	public void saveOrUpdateNews(MagicNews n) throws SQLException {
		var dir = new File(new File(directory, NEWSDIR), n.getCategorie());
		if (!dir.exists())
			dir.mkdir();

		if (n.getId() == -1)
			n.setId(dir.listFiles().length + 1);

		var f = new File(dir, n.getId() + "-" + n.getName());
		try {
			save(n, f);
		} catch (Exception e) {
			throw new SQLException(e);
		}

	}

	@Override
	public void initDefault() {
		setProperty("URL", Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(),"filesDB").toString());
		setProperty("SERIALIZER", "json");

	}

	@Override
	public String getVersion() {
		return "1";
	}

	@Override
	public void executeQuery(String query) throws SQLException {
		throw new SQLException("Execute query is not possible");
		
	}

	@Override
	public List<Transaction> listTransactions() throws SQLException {
		List<Transaction> ret = new ArrayList<>();

		for (File f : FileUtils.listFiles(new File(directory, TRANSACTIONSDIR), null, false)) {
			try {
				ret.add(read(Transaction.class, f));
			} catch (Exception e) {
				logger.error("Error reading transactions", e);
			}
		}
		return ret;
	}

	@Override
	public int saveOrUpdateTransaction(Transaction t) throws SQLException {
		return 0;
	}

	@Override
	public void deleteTransaction(Transaction t) throws SQLException {
	
		if (t.getId() == -1)
			t.setId(Paths.get(directory.getAbsolutePath(), TRANSACTIONSDIR).toFile().list().length + 1);
		
		try {
			save(t, Paths.get(directory.getAbsolutePath(), TRANSACTIONSDIR,String.valueOf(t.getId())).toFile());
		} catch (IOException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public Transaction getTransaction(int id) throws SQLException {
		return null;
	}

	@Override
	public int saveOrUpdateContact(Contact c) throws SQLException {
		if (c.getId() == -1)
			c.setId(Paths.get(directory.getAbsolutePath(), CONTACTSSDIR).toFile().list().length + 1);
		
		try {
			save(c, Paths.get(directory.getAbsolutePath(), CONTACTSSDIR,String.valueOf(c.getId())).toFile());
		} catch (IOException e) {
			throw new SQLException(e);
		}
		
		
		return 0;
	}

	@Override
	public Contact getContactById(int id) throws SQLException {
		return null;
	}

	@Override
	public Contact getContactByLogin(String email, String password) throws SQLException {
		return null;
	}
	@Override
	public List<Transaction> listTransactions(Contact c) throws SQLException {
		return new ArrayList<>();
	}

	@Override
	public List<Contact> listContacts() throws SQLException {
		return new ArrayList<>();
	}

	@Override
	public boolean enableContact(String token) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void changePassword(Contact c, String newPassword) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	
}

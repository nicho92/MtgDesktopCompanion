package org.magic.api.dao.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicNews;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicDAO;
import org.magic.services.MTGControler;
import org.magic.tools.IDGenerator;

import com.google.gson.Gson;

public class FileDAO extends AbstractMagicDAO {

	private Gson export;
	private File directory;

	private static final String CARDSDIR = "cards";
	private static final String STOCKDIR = "stocks";
	private static final String ALERTSDIR = "alerts";
	private static final String NEWSDIR = "news";

	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}

	public <T> T read(Class<T> c, File f) throws IOException {
		return export.fromJson(FileUtils.readFileToString(f, Charset.defaultCharset()), c);
	}

	public void save(Object o, File f) throws IOException {
		FileUtils.write(f, export.toJson(o), Charset.defaultCharset());
	}

	@Override
	public void init() {
		export = new Gson();
		directory = new File(getString("URL"));

		if (!directory.exists())
			directory.mkdir();

		new File(directory, CARDSDIR).mkdir();
		new File(directory, ALERTSDIR).mkdir();
		new File(directory, STOCKDIR).mkdir();
		new File(directory, NEWSDIR).mkdir();
		new File(new File(directory, CARDSDIR), MTGControler.getInstance().get("default-library")).mkdir();
		logger.debug("File DAO init");
	}

	@Override
	public String getName() {
		return "FileDB";
	}

	private String removeCon(String a) {
		if (a.equalsIgnoreCase("con"))
			return a + "_set";

		return a;
	}

	@Override
	public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException {
		File f = new File(new File(directory, CARDSDIR), collection.getName());

		if (!f.exists())
			f.mkdir();

		f = new File(f, removeCon(mc.getEditions().get(0).getId()));
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
		File f = Paths.get(directory.getAbsolutePath(), CARDSDIR, collection.getName(),
				removeCon(mc.getEditions().get(0).getId()), IDGenerator.generate(mc)).toFile();
		File parent = f.getParentFile();

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
		File f = new File(new File(directory, CARDSDIR), list.getName());

		if (me != null)
			f = new File(f, removeCon(me.getId()));

		return FileUtils.listFiles(f, null, true).size();
	}

	@Override
	public Map<String, Integer> getCardsCountGlobal(MagicCollection c) throws SQLException {
		Map<String, Integer> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		File eds = new File(new File(directory, CARDSDIR), c.getName());
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
		File col = new File(new File(directory, CARDSDIR), c.getName());

		if (me != null)
			col = new File(col, removeCon(me.getId()));

		logger.debug("Load " + col);

		List<MagicCard> ret = new ArrayList<>();

		for (File f : FileUtils.listFilesAndDirs(col, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
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
		File f = new File(directory, CARDSDIR);
		List<MagicCollection> ret = new ArrayList<>();
		Collection<File> res = FileUtils.listFiles(f, new NameFileFilter(id), TrueFileFilter.INSTANCE);

		for (File result : res)
			ret.add(new MagicCollection(result.getParentFile().getParentFile().getName()));

		return ret;
	}

	@Override
	public List<String> getEditionsIDFromCollection(MagicCollection c) throws SQLException {
		File col = new File(new File(directory, CARDSDIR), c.getName());
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
		File f = new File(new File(directory, CARDSDIR), c.getName());

		if (!f.exists())
			f.mkdir();

	}

	@Override
	public void removeCollection(MagicCollection c) throws SQLException {
		File f = new File(new File(directory, CARDSDIR), c.getName());

		if (f.exists())
			try {
				FileUtils.deleteDirectory(f);
			} catch (IOException e) {
				throw new SQLException(e);
			}

	}

	@Override
	public List<MagicCollection> getCollections() throws SQLException {

		ArrayList<MagicCollection> ret = new ArrayList<>();

		for (File f : new File(directory, CARDSDIR).listFiles())
			ret.add(new MagicCollection(f.getName()));

		return ret;
	}

	@Override
	public void removeEdition(MagicEdition ed, MagicCollection col) throws SQLException {

		File f = Paths.get(directory.getAbsolutePath(), CARDSDIR, col.getName(), removeCon(ed.getId())).toFile();

		if (f.exists())
			try {
				FileUtils.deleteDirectory(f);
			} catch (IOException e) {
				throw new SQLException(e);
			}

	}

	@Override
	public List<MagicCardStock> listStocks(MagicCard mc, MagicCollection col) throws SQLException {
		List<MagicCardStock> st = new ArrayList<>();
		File f = new File(directory, STOCKDIR);
		for (File fstock : FileUtils.listFiles(f, new WildcardFileFilter("*" + IDGenerator.generate(mc)),
				TrueFileFilter.INSTANCE)) {
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

		File f = new File(directory, STOCKDIR);

		if (state.getIdstock() == -1)
			state.setIdstock(f.listFiles().length + 1);

		f = new File(f, state.getIdstock() + "-" + IDGenerator.generate(state.getMagicCard()));
		try {
			save(state, f);
		} catch (Exception e) {
			throw new SQLException(e);
		}

	}

	@Override
	public void deleteStock(List<MagicCardStock> state) throws SQLException {

		for (MagicCardStock s : state) {
			File f = Paths.get(directory.getAbsolutePath(), STOCKDIR,
					s.getIdstock() + "-" + IDGenerator.generate(s.getMagicCard())).toFile();
			logger.debug("Delete " + f);
			FileUtils.deleteQuietly(f);
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
	public List<MagicCardAlert> listAlerts() {
		List<MagicCardAlert> ret = new ArrayList<>();

		for (File f : FileUtils.listFiles(new File(directory, ALERTSDIR), null, false)) {
			try {
				ret.add(read(MagicCardAlert.class, f));
			} catch (Exception e) {
				logger.error("Error reading alert", e);
			}
		}
		return ret;
	}

	@Override
	public void saveAlert(MagicCardAlert alert) throws SQLException {
		try {
			save(alert, new File(directory, "/alerts/" + IDGenerator.generate(alert.getCard())));
		} catch (IOException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void deleteAlert(MagicCardAlert alert) throws SQLException {
		File f = new File(directory, "/alerts/" + IDGenerator.generate(alert.getCard()));
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

		for (File f : FileUtils.listFiles(new File(directory, NEWSDIR), null, false)) {
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
		File dir = new File(new File(directory, ALERTSDIR), n.getCategorie());
		File f = new File(dir, n.getId() + "-" + n.getName());
		FileUtils.deleteQuietly(f);
	}

	@Override
	public void saveOrUpdateNews(MagicNews n) throws SQLException {
		File dir = new File(new File(directory, ALERTSDIR), n.getCategorie());
		if (!dir.exists())
			dir.mkdir();

		if (n.getId() == -1)
			n.setId(dir.listFiles().length + 1);

		File f = new File(dir, n.getId() + "-" + n.getName());
		try {
			save(n, f);
		} catch (Exception e) {
			throw new SQLException(e);
		}

	}

	@Override
	public void initDefault() {
		setProperty("URL", confdir.getAbsolutePath() + "/filesDB");
		setProperty("SERIALIZER", "json");

	}

	@Override
	public String getVersion() {
		return "1";
	}

}

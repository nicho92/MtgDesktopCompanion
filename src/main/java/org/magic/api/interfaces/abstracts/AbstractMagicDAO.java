package org.magic.api.interfaces.abstracts;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MTGAlert;
import org.magic.api.beans.MTGAnnounce;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGNews;
import org.magic.api.beans.MTGSealedStock;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGPool;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.services.MTGControler;
import org.magic.services.tools.TCache;


public abstract class AbstractMagicDAO extends AbstractMTGPlugin implements MTGDao{

	protected static final String LOGIN = "LOGIN";
	protected static final String PASS = "PASS";
	protected static final String DB_NAME = "DB_NAME";
	protected static final String PARAMS = "PARAMS";
	protected static final String SERVERPORT = "SERVERPORT";
	protected static final String SERVERNAME = "SERVERNAME";
	protected static final String DRIVER ="DRIVER";
	protected static final String PARAMETERS = "PARAMETERS";

	protected JsonExport serialiser;

	protected TCache<Contact> listContacts;
	protected TCache<MTGCollection> listCollections;

	@Override
	public boolean isSQL() {
		return false;
	}

	@Override
	public PLUGINS getType() {
		return PLUGINS.DAO;
	}


	@Override
	public void init(MTGPool pool) throws SQLException {
		logger.debug("Pool isn't necessary");
		init();
	}

	protected AbstractMagicDAO() {
		listContacts = new TCache<>("contacts");
		listCollections = new TCache<>("collections");
		serialiser=new JsonExport();
		serialiser.removePrettyString();
	}

	@Override
	public void moveCard(MTGCard mc, MTGCollection from, MTGCollection to) throws SQLException {
		removeCard(mc, from);
		saveCard(mc, to);
	}

	@Override
	public void saveCard(MTGCard mc, MTGCollection collection) throws SQLException {
		var mcs = MTGControler.getInstance().getDefaultStock(collection);
		mcs.setProduct(mc);
		saveOrUpdateCardStock(mcs);
		
	}
	
	
	@Override
	public void saveOrUpdateCardStock(MTGCard mc) throws SQLException {
		var st = MTGControler.getInstance().getDefaultStock();
		st.setProduct(mc);
		saveOrUpdateCardStock(st);
	}
	

	@Override
	public List<MTGAnnounce> listAnnounces() throws SQLException {
		return listAnnounces(-1,null);
	}

	@Override
	public List<MTGAnnounce> listAnnounces(Contact contact) throws SQLException {
		return listAnnounces().stream().filter(a->a.getContact().getId()==contact.getId()).toList();
	}

	@Override
	public List<MTGAnnounce> listAnnounces(String textSearch) throws SQLException {
		return listAnnounces().stream().filter(a->a.getTitle().toLowerCase().contains(textSearch)||a.getDescription().toLowerCase().contains(textSearch)).toList();
	}

	@Override
	public List<MTGAnnounce> listAnnounces(EnumItems type) throws SQLException {
		return listAnnounces().stream().filter(a->a.getCategorie()==type).toList();
	}

	@Override
	public List<MTGCardStock> listStocks(List<MTGCollection> cols) throws SQLException {
		return listStocks().stream().filter(st->cols.contains(st.getMagicCollection())).toList();
	}

	@Override
	public List<MTGCardStock> listStocks(String cardName, List<MTGCollection> cols) throws SQLException {
		return listStocks(cols).stream().filter(st->st.getProduct().getName().equalsIgnoreCase(cardName)).toList();
	}

	@Override
	public List<MTGStockItem> listStockItems() throws SQLException {
		List<MTGStockItem> ret = new ArrayList<>();

		ret.addAll(listStocks());
		ret.addAll(listSealedStocks());

		return ret;
	}

	@Override
	public MTGStockItem getStockById(EnumItems typeStock, Long id) throws SQLException {
		if(typeStock==EnumItems.CARD)
			return getStockById(id);

		return getSealedStockById(id);
	}

	@Override
	public void saveOrUpdateStock(MTGStockItem stock) throws SQLException {
		if(stock.getProduct().getTypeProduct()==EnumItems.CARD)
			saveOrUpdateCardStock((MTGCardStock)stock);
		else
			saveOrUpdateSealedStock((MTGSealedStock)stock);
	}

	@Override
	public List<MTGSealedStock> listSealedStocks(MTGCollection c) throws SQLException {
		return listSealedStocks().stream().filter(ss->ss.getMagicCollection().getName().equalsIgnoreCase(c.getName())).toList();
	}

	@Override
	public List<MTGSealedStock> listSealedStocks(MTGCollection c, MTGEdition ed) throws SQLException {
		return listSealedStocks().stream().filter(ss->ss.getMagicCollection().getName().equalsIgnoreCase(c.getName())&& ss.getProduct().getEdition().getId().equalsIgnoreCase(ed.getId())).toList();
	}

	@Override
	public void saveCollection(String name) throws SQLException {
		saveCollection(new MTGCollection(name));
	}

	@Override
	public List<MTGCard> listCardsFromCollection(String collectionName) throws SQLException {
		return listCardsFromCollection(new MTGCollection(collectionName));
	}

	@Override
	public List<MTGCard> listCardsFromCollection(String collectionName, String me) throws SQLException {
		return listCardsFromCollection(new MTGCollection(collectionName), new MTGEdition(me,me));
	}


	@Override
	public void deleteTransaction(List<Transaction> t) throws SQLException {
		for(Transaction transaction : t)
			deleteTransaction(transaction);

	}


	@Override
	public List<MTGCardStock> listStocks(MTGCard mc) throws SQLException {
		return listStocks().stream().filter(st->st.getProduct().getName().equals(mc.getName())).toList();
	}

	@Override
	public void deleteStock(MTGCardStock state) throws SQLException
	{
		ArrayList<MTGCardStock> stock = new ArrayList<>();
		stock.add(state);
		deleteStock(stock);
	}

	@Override
	public MTGAlert hasAlert(MTGCard mc) {
		return listAlerts().stream().filter(a->a.getId().equals(mc.getScryfallId())).findFirst().orElse(null);
	}

	

	@Override
	public MTGCardStock getStockWithTiersID(String key, String id) throws SQLException {

		if(key==null)
			return null;

		return listStocks().stream().filter(st->id.equals(st.getTiersAppIds(key))).findAny().orElse(null);
	}
	

	@Override
	public void duplicateTo(MTGDao dao) throws SQLException {

		dao.init(null);
		logger.debug("duplicate collection");
		for (MTGCollection col : listCollections())
		{
			try {
			dao.saveCollection(col);
			}
			catch(Exception e)
			{
				logger.error(e);
			}
		}

		logger.debug("duplicate stock");
		for(MTGCardStock stock : listStocks())
		{
			stock.setId(-1);
			dao.saveOrUpdateCardStock(stock);
		}

		logger.debug("duplicate alerts");
		for(MTGAlert alert : listAlerts())
			dao.saveAlert(alert);

		logger.debug("duplicate news");
		for(MTGNews news : listNews())
		{
			news.setId(-1);
			dao.saveOrUpdateNews(news);
		}

		logger.debug("duplicate sealed");
		for(MTGSealedStock oe : listSealedStocks())
		{
			oe.setId(-1);
			dao.saveOrUpdateSealedStock(oe);
		}

		logger.debug("duplicate decks");
		for(MTGDeck oe : listDecks())
		{
			oe.setId(-1);
			dao.saveOrUpdateDeck(oe);
		}
		
		logger.debug("duplicate contact");
		for(Contact oe : listContacts())
		{
			oe.setId(-1);
			dao.saveOrUpdateContact(oe);
		}

		logger.debug("duplicate transactions");
		for(Transaction oe : listTransactions())
		{
			oe.setId(-1);
			oe.setContact(dao.getContactByEmail(oe.getContact().getEmail()));			
			dao.saveOrUpdateTransaction(oe);
		}

	

		logger.debug("duplicate announces");
		for(MTGAnnounce oe : listAnnounces())
		{
			oe.setId(-1);
			dao.saveOrUpdateAnnounce(oe);
		}

	}


	@Override
	public void updateCard(MTGCard c, MTGCard newC, MTGCollection col) throws SQLException {
		saveCard(newC,col);
	}

	@Override
	public boolean equals(Object obj) {

		if(obj ==null)
			return false;

		return hashCode()==obj.hashCode();
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}


}

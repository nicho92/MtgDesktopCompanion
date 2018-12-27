package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicNews;
import org.magic.api.beans.OrderEntry;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGConstants;


public abstract class AbstractMagicDAO extends AbstractMTGPlugin implements MTGDao {

	protected JsonExport serialiser;
	
	protected List<MagicCardAlert> listAlerts;
	protected List<OrderEntry> listOrders;

	protected abstract void initAlerts();
	protected abstract void initOrders();
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.DAO;
	}

	public AbstractMagicDAO() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "dao");
		if (!confdir.exists())
			confdir.mkdir();
		load();

		if (!new File(confdir, getName() + ".conf").exists()) {
			initDefault();
			save();
		}
		listAlerts = new ArrayList<>();
		listOrders = new ArrayList<>();
		serialiser=new JsonExport();
	
	}
	
	@Override
	public void moveCard(MagicCard mc, MagicCollection from, MagicCollection to) throws SQLException {
		removeCard(mc, from);
		saveCard(mc, to);
		
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
	public void saveCollection(String name) throws SQLException {
		saveCollection(new MagicCollection(name));
		
	}

	@Override
	public void deleteOrderEntry(OrderEntry state) throws SQLException
	{
		ArrayList<OrderEntry> orders = new ArrayList<>();
		orders.add(state);
		deleteOrderEntry(orders);
	}
	
	@Override
	public void deleteStock(MagicCardStock state) throws SQLException
	{
		ArrayList<MagicCardStock> stock = new ArrayList<>();
		stock.add(state);
		deleteStock(stock);
	}
	
	@Override
	public boolean hasAlert(MagicCard mc) {
		return listAlerts().stream().anyMatch(a->a.getCard().equals(mc));
	}
	
	@Override
	public List<MagicCardAlert> listAlerts() {
		if (listAlerts.isEmpty())
			initAlerts();
		
		return listAlerts;
	}
	
	@Override
	public List<OrderEntry> listOrders() {
		if (listOrders.isEmpty())
			initOrders();
		
		return listOrders;
	}
	
	
	

	@Override
	public void duplicateTo(MTGDao dao) throws SQLException {
		
		
		logger.debug("duplicate collection");
		for (MagicCollection col : listCollections())
		{
			try {
				dao.saveCollection(col);
			}catch(Exception e)
			{
				logger.error(col +" already exist");
			}
			
			for (MagicCard mc : listCardsFromCollection(col)) {
				try {
					dao.saveCard(mc, col);
				}catch(Exception e)
				{
					logger.error("error saving " + mc + " in "+ col + " :",e);
				}
			}
		}
		
		logger.debug("duplicate stock");
		for(MagicCardStock stock : listStocks())
		{
			stock.setIdstock(-1);
			dao.saveOrUpdateStock(stock);
		}
			
		logger.debug("duplicate alerts");
		for(MagicCardAlert alert : listAlerts())
			dao.saveAlert(alert);
		
		logger.debug("duplicate news");
		for(MagicNews news : listNews())
		{
			news.setId(-1);
			dao.saveOrUpdateNews(news);
		}
		
		logger.debug("duplicate orders");
		for(OrderEntry oe : listOrders())
		{
			oe.setId(-1);
			dao.saveOrUpdateOrderEntry(oe);
		}
		
	}
	
	
	public List<OrderEntry> listOrderForEdition(MagicEdition ed) 
	{
		return listOrders().stream().filter(o->o.getEdition()!=null && o.getEdition().equals(ed)).collect(Collectors.toList());
	}
	
	@Override
	public List<OrderEntry> listOrdersAt(Date d) {
		return listOrders().stream().filter(o->o.getTransationDate().equals(d)).collect(Collectors.toList());
		
	}
	
	@Override
	public List<Date> listDatesOrders() {
		Set<Date> d = new HashSet<>();
			listOrders().forEach(o->d.add(o.getTransationDate()));
	
			return new ArrayList<>(d);
	}
}

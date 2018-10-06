package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGConstants;

import com.google.gson.Gson;

public abstract class AbstractMagicDAO extends AbstractMTGPlugin implements MTGDao {

	protected Gson serialiser;
	
	
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
		
		serialiser=new Gson();
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
	public void deleteStock(MagicCardStock state) throws SQLException
	{
		ArrayList<MagicCardStock> stock = new ArrayList<>();
		stock.add(state);
		deleteStock(stock);
	}
	
	

	
}

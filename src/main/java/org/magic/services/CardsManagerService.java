package org.magic.services;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.logging.MTGLogger;
import org.utils.patterns.observer.Observer;

public class CardsManagerService {

	private static Logger logger = MTGLogger.getLogger(CardsManagerService.class);


	private CardsManagerService()
	{

	}


	public static MagicCard switchEditions(MagicCard mc, MagicEdition ed)
	{
		try {

			if(mc.isDoubleFaced())
				return getEnabledPlugin(MTGCardsProvider.class).searchCardByCriteria("faceName",mc.getName(), ed, false).get(0);
			else
				return getEnabledPlugin(MTGCardsProvider.class).searchCardByName(mc.getName(), ed, true).get(0);
		} catch (IOException e) {
			logger.error("{} is not found in {}",mc,ed);
			return mc;
		}
	}

	public static void removeCard(MagicCard mc , MagicCollection collection) throws SQLException
	{

		getEnabledPlugin(MTGDao.class).removeCard(mc, collection);


			try{
				if(mc.getRotatedCard()!=null)
					getEnabledPlugin(MTGDao.class).removeCard(mc.getRotatedCard(), collection);
				}
				catch(Exception e)
				{
						logger.error("Can't remove returned card {}",mc.getRotatedCard(),e);
				}

		if(MTGControler.getInstance().get("collections/stockAutoDelete").equals("true"))
		{
			getEnabledPlugin(MTGDao.class).listStocks(mc, collection,true).forEach(st->{
				try{
					getEnabledPlugin(MTGDao.class).deleteStock(st);
				}
				catch(Exception e)
				{
					logger.error(e);
				}
			});
		}

	}

	public static void moveCard(MagicCard mc, MagicCollection from, MagicCollection to,Observer o) throws SQLException
	{
		if(o!=null)
			getEnabledPlugin(MTGDao.class).addObserver(o);


		getEnabledPlugin(MTGDao.class).moveCard(mc, from,to);

		try {
			if(mc.getRotatedCard()!=null)
				getEnabledPlugin(MTGDao.class).moveCard(mc.getRotatedCard(), from,to);
		}
		catch(Exception e)
		{
				logger.error("Can't move returned card {}",mc.getRotatedCard(),e);
		}

	}

	public static void moveCard(MagicEdition ed, MagicCollection from, MagicCollection to,Observer o) throws SQLException
	{
		if(o!=null)
			getEnabledPlugin(MTGDao.class).addObserver(o);


		getEnabledPlugin(MTGDao.class).moveEdition(ed, from,to);

	}

	public static void saveCard(MagicCard mc , MagicCollection collection,Observer o) throws SQLException
	{

		if(o!=null)
			getEnabledPlugin(MTGDao.class).addObserver(o);

		getEnabledPlugin(MTGDao.class).saveCard(mc, collection);

		try {
			if(mc.getRotatedCard()!=null)
				{
					getEnabledPlugin(MTGDao.class).saveCard(mc.getRotatedCard(), collection);
				}
		}
		catch(Exception e)
		{
				logger.error("Can't save returned card {}",mc.getRotatedCard(),e);
		}



		if(MTGControler.getInstance().get("collections/stockAutoAdd").equals("true"))
		{
			var st = MTGControler.getInstance().getDefaultStock();
			st.setProduct(mc);
			st.setMagicCollection(collection);
			getEnabledPlugin(MTGDao.class).saveOrUpdateCardStock(st);
		}

		if(o!=null)
			getEnabledPlugin(MTGDao.class).removeObserver(o);
	}



}

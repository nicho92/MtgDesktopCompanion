package org.magic.api.customs.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.abstracts.extra.AbstractCustomCardsManager;
import org.magic.services.tools.MTG;

public class DAOCustomManager extends AbstractCustomCardsManager {
	

	@Override
	public List<MTGCard> listCustomsCards(MTGEdition me) throws IOException {
		try {
			return MTG.getEnabledPlugin(MTGDao.class).listCustomCards(me);
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void saveCustomCard(MTGCard mc) throws IOException {
		try {
			MTG.getEnabledPlugin(MTGDao.class).saveCustomCard(mc);
			notify(mc);
		} catch (SQLException e) {
			throw new IOException(e);
		}
		
	}

	@Override
	public boolean deleteCustomCard(MTGCard mc) throws IOException {
		try {
			MTG.getEnabledPlugin(MTGDao.class).deleteCustomCard(mc);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public List<MTGEdition> listCustomSets() throws IOException {
		try {
			return MTG.getEnabledPlugin(MTGDao.class).listCustomSets();
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void saveCustomSet(MTGEdition me) throws IOException {
		try {
			MTG.getEnabledPlugin(MTGDao.class).saveCustomSet(me);
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public void saveCustomSet(MTGEdition ed, List<MTGCard> cards) {
		
		cards.forEach(mc->{
			try {
				saveCustomCard(ed, mc);
				
			} catch (IOException e) {
				logger.error(e);
			}
		});
	}
	

	@Override
	public void deleteCustomSet(MTGEdition me) throws IOException {
		try {
			MTG.getEnabledPlugin(MTGDao.class).deleteCustomSet(me);
		} catch (SQLException e) {
			throw new IOException(e);
		}
		
	}

	@Override
	public MTGEdition getCustomSet(String id) {
		try {
			return MTG.getEnabledPlugin(MTGDao.class).getCustomSetById(id);
		} catch (SQLException e) {
			return null;
		}
	}

	@Override
	public String getName() {
		return "Dao";
	}
	
}

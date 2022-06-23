package org.magic.api.network.actions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.interfaces.MTGDao;
import org.magic.tools.MTG;

public class SearchAction  extends AbstractNetworkAction{

	private static final long serialVersionUID = 1L;

	
	public SearchAction()
	{
		setAct(ACTIONS.SEARCH);
	}
	
	
	public List<MagicCollection> search(MagicCard mc)
	{
		try {
			return MTG.getEnabledPlugin(MTGDao.class).listCollectionFromCards(mc);
		} catch (SQLException e) {
			return new ArrayList<>();
		}
	}
	
	
}

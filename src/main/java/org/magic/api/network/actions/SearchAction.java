package org.magic.api.network.actions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.interfaces.MTGDao;
import org.magic.game.model.Player;
import org.magic.services.tools.MTG;

public class SearchAction  extends AbstractNetworkAction{

	private static final long serialVersionUID = 1L;

	private MagicCard mc;


	public SearchAction(Player p,MagicCard mc)
	{
		super(p);
		setAct(ACTIONS.SEARCH);
		this.mc=mc;
	}

	@Override
	public String toString() {
		return "SearchAction : " + mc;
	}


	public List<MagicCollection> getResponse()
	{
		try {
			return MTG.getEnabledPlugin(MTGDao.class).listCollectionFromCards(mc);
		} catch (SQLException e) {
			return new ArrayList<>();
		}
	}


}

package org.magic.api.network.actions;

import java.util.List;

import org.magic.game.model.Player;

public class ListPlayersAction extends AbstractNetworkAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Player> list;

	public ListPlayersAction(List<Player> p) {
		setAct(ACTIONS.LIST_PLAYER);
		this.list = p;
	}

	public List<Player> getList() {
		return list;
	}

	public void setList(List<Player> list) {
		this.list = list;
	}

}

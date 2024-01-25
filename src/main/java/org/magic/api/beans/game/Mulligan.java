package org.magic.api.beans.game;

import org.magic.api.beans.MTGCard;

public abstract class Mulligan {

	protected Player player;
	protected int currentHandSize;
	protected int mulliganCount;

	protected Mulligan(Player p)
	{
		this.player = p;
		currentHandSize = p.getHand().size();
		mulliganCount=0;
	}

	public int getMulliganCount() {
		return mulliganCount;
	}

	public Player getPlayer() {
		return player;
	}

	public int getCurrentHandSize() {
		return currentHandSize;
	}

	public abstract void mulligan();
	public abstract MTGCard scry(int num);

}

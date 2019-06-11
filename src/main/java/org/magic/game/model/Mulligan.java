package org.magic.game.model;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.game.model.mulligans.LondonMulligan;
import org.magic.game.model.mulligans.ParisMulligan;
import org.magic.services.MTGDeckManager;

public abstract class Mulligan {

	protected Player player;
	protected int currentHandSize;
	protected int mulliganCount;
	
	public Mulligan(Player p)
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
	public abstract MagicCard scry(int num);
	
	
	public static void main(String[] args) {
		MTGDeckManager manager = new MTGDeckManager();
		MagicDeck d = manager.listDecks().get(0);
		Player p = new Player(d);
		
		p.drawHand();
		Mulligan mulligan = new ParisMulligan(p);
		
		
		System.out.println(p.getHand().getCards());
		mulligan.mulligan();
		System.out.println(p.getHand().getCards());
		mulligan.mulligan();
		System.out.println(p.getHand().getCards());
		mulligan.mulligan();
		
		
		
		
	}
	
}

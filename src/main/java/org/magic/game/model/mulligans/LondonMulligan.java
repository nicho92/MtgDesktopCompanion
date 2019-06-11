package org.magic.game.model.mulligans;

import org.magic.api.beans.MagicCard;
import org.magic.game.model.Mulligan;
import org.magic.game.model.Player;

public class LondonMulligan extends Mulligan {

	public LondonMulligan(Player p) {
		super(p);
	}

	@Override
	public void mulligan() {
		mulliganCount++;
		player.mixHandAndLibrary();
		player.drawCard(7);
		
		
		for(int i=0;i<getMulliganCount();i++)
			player.putCardInLibraryFromHand(player.getHand().getCards().get(0),false);
		
		
	}

	@Override
	public MagicCard scry(int num) {
		return null;
	}

	
	@Override
	public String toString() {
		return "London Mulligan";
	}
	
}

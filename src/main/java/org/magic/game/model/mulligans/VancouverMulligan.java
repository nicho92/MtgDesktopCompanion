package org.magic.game.model.mulligans;

import org.magic.api.beans.MagicCard;
import org.magic.game.model.Mulligan;
import org.magic.game.model.Player;

public class VancouverMulligan extends Mulligan {

	public VancouverMulligan(Player p) {
		super(p);
	}

	@Override
	public void mulligan() {
		if(currentHandSize==0)
			return;
		
		mulliganCount++;
		player.mixHandAndLibrary();
		player.shuffleLibrary();
		currentHandSize--;
		player.drawCard(currentHandSize);
		
	}

	@Override
	public MagicCard scry(int num) {
		return player.scry(num).get(0);
	}


	@Override
	public String toString() {
		return "Vancouver Mulligan";
	}
}

package org.magic.gui.game;

import org.magic.api.beans.MagicCard;
import org.magic.game.GameManager;
import org.magic.game.PositionEnum;

public class LibraryPanel extends ThumbnailPanel {

	
	@Override
	public void moveCard(MagicCard mc, PositionEnum to) {
		switch (to) {
		case BATTLEFIELD:GameManager.getInstance().getPlayer().playCardFromLibrary(mc);break;
		case EXIL:GameManager.getInstance().getPlayer().exileCardFromLibrary(mc);break;
		case HAND:GameManager.getInstance().getPlayer().searchCardFromLibrary(mc);break;
		default:break;
	}
		
		
	}
	
	
	@Override
	public PositionEnum getOrigine() {
		return PositionEnum.LIBRARY;
	}
	
}

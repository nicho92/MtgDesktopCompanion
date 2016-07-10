package org.magic.gui.game;

import org.magic.api.beans.MagicCard;
import org.magic.game.PositionEnum;

public class LibraryPanel extends ThumbnailPanel {

	
	public LibraryPanel() {
		super();
	}
	
	@Override
	public void moveCard(MagicCard mc, PositionEnum to) {
		
		switch (to) {
			case BATTLEFIELD:player.playCardFromLibrary(mc);break;
			case EXIL:player.exileCardFromLibrary(mc);break;
			case HAND:player.searchCardFromLibrary(mc);break;
		default:break;
	}
		
		
	}
	
	
	@Override
	public PositionEnum getOrigine() {
		return PositionEnum.LIBRARY;
	}
	
}

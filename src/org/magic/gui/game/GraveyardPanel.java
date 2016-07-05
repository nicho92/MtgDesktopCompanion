package org.magic.gui.game;

import org.magic.api.beans.MagicCard;
import org.magic.services.games.GameManager;
import org.magic.services.games.PositionEnum;

public class GraveyardPanel extends DraggablePanel {

	public GraveyardPanel() {
		super();
	}
	
	@Override
	public PositionEnum getOrigine() {
		return PositionEnum.GRAVEYARD;
	}

	@Override
	public void addComponent(DisplayableCard i) {
		add(i);
		
		
	}


	@Override
	public void moveCard(MagicCard mc, PositionEnum to) {
		switch (to) {
			case BATTLEFIELD:GameManager.getInstance().getPlayer().returnCardFromGraveyard(mc);break;
			case EXIL:GameManager.getInstance().getPlayer().exileCardFromGraveyard(mc);break;
			case HAND:GameManager.getInstance().getPlayer().returnCardFromGraveyard(mc);break;
			default:break;
		}
		
	}
	

}

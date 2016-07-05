package org.magic.gui.game;

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

	
	

}

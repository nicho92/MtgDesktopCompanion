package org.magic.game.gui.components;

import org.magic.api.beans.MagicCard;
import org.magic.game.model.PositionEnum;

public interface Draggable {

	void moveCard(MagicCard mc, PositionEnum to);

	void addComponent(DisplayableCard i);

	PositionEnum getOrigine();
	
	void update();
	
	
	
}
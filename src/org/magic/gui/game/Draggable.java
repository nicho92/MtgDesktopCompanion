package org.magic.gui.game;

import org.magic.api.beans.MagicCard;
import org.magic.game.PositionEnum;

public interface Draggable {

	void moveCard(MagicCard mc, PositionEnum to);

	void addComponent(DisplayableCard i);

	PositionEnum getOrigine();

}
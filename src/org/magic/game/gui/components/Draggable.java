package org.magic.game.gui.components;

import org.magic.game.model.PositionEnum;

public interface Draggable {

	public void moveCard(DisplayableCard mc, PositionEnum to);
	public void addComponent(DisplayableCard i);
	public PositionEnum getOrigine();
	public void updatePanel();
	public void postTreatment();
	
}
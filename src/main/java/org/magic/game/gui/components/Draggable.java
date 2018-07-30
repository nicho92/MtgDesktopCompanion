package org.magic.game.gui.components;

import org.magic.game.model.ZoneEnum;

public interface Draggable {

	public void moveCard(DisplayableCard mc, ZoneEnum to);

	public void addComponent(DisplayableCard i);

	public ZoneEnum getOrigine();

	public void updatePanel();

	public void postTreatment(DisplayableCard c);

}
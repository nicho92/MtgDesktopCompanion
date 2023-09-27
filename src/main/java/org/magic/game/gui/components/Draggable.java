package org.magic.game.gui.components;

import org.magic.api.beans.game.ZoneEnum;

public interface Draggable {

	public void moveCard(DisplayableCard mc, ZoneEnum to);

	public void addComponent(DisplayableCard i);

	public ZoneEnum getOrigine();

	public void updatePanel();

	public void postTreatment(DisplayableCard c);

}
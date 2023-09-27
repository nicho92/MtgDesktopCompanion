package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;

import org.magic.api.beans.game.ZoneEnum;
import org.magic.game.actions.abbstract.AbstractCardAction;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.counters.AbstractCounter;

public class RemoveCounterActions extends AbstractCardAction {

	private static final long serialVersionUID = 1L;
	private AbstractCounter counter;

	public RemoveCounterActions(DisplayableCard displayableCard, AbstractCounter counter) {
		super(displayableCard,"remove a " + counter.describe());
		this.counter = counter;
		putValue(SHORT_DESCRIPTION, "remove a " + counter.describe());
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		card.removeCounter(counter);
		card.initActions();
		card.revalidate();
		card.repaint();
		GamePanelGUI.getInstance().getPlayer().logAction("remove a " + counter);

	}

	@Override
	public ZoneEnum playableFrom() {
		return ZoneEnum.BATTLEFIELD;
	}
	
}

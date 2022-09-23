package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.counters.AbstractCounter;

public class RemoveCounterActions extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private DisplayableCard card;
	private AbstractCounter counter;

	public RemoveCounterActions(DisplayableCard displayableCard, AbstractCounter counter) {
		String label = "remove a " + counter.describe();
		this.card = displayableCard;
		this.counter = counter;
		putValue(NAME, label);
		putValue(SHORT_DESCRIPTION, label);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		card.removeCounter(counter);
		card.initActions();
		card.revalidate();
		card.repaint();
		GamePanelGUI.getInstance().getPlayer().logAction("remove a " + counter);

	}

}

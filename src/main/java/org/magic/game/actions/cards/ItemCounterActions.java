package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;

import org.magic.api.beans.game.ZoneEnum;
import org.magic.game.actions.abbstract.AbstractCardAction;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.counters.ItemCounter;

public class ItemCounterActions extends AbstractCardAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private transient ItemCounter itemCounter;

	public ItemCounterActions(DisplayableCard displayableCard, ItemCounter itemCounter) {
		super(displayableCard,"put a " + itemCounter.describe());
		this.itemCounter = itemCounter;
		putValue(SHORT_DESCRIPTION, "put a " + itemCounter.describe());
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		itemCounter.setName(itemCounter.getName());

		card.addCounter(itemCounter);
		card.initActions();
		card.revalidate();
		card.repaint();
		GamePanelGUI.getInstance().getPlayer()
				.logAction("add a " + itemCounter.describe() + " on " + card.getMagicCard().getName());

	}

	@Override
	public ZoneEnum playableFrom() {
		return ZoneEnum.BATTLEFIELD;
	}

}

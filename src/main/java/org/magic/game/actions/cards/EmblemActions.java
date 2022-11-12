package org.magic.game.actions.cards;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.magic.api.interfaces.MTGTokensProvider;
import org.magic.game.actions.abbstract.AbstractCardAction;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.ZoneEnum;
import org.magic.services.MTGControler;

public class EmblemActions extends AbstractCardAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	public EmblemActions(DisplayableCard card) {
		super("Generate a emblem");
		putValue(SHORT_DESCRIPTION, "Generate a emblem");
		putValue(MNEMONIC_KEY, KeyEvent.VK_E);
		this.card = card;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			var tok = getEnabledPlugin(MTGTokensProvider.class).generateEmblemFor(card.getMagicCard());
			var dc = new DisplayableCard(tok, MTGControler.getInstance().getCardsGameDimension(), true);
			dc.setMagicCard(tok);
			GamePanelGUI.getInstance().getPanelBattleField().addComponent(dc);
			GamePanelGUI.getInstance().getPanelBattleField().revalidate();
			GamePanelGUI.getInstance().getPanelBattleField().repaint();
			GamePanelGUI.getInstance().getPlayer().logAction("generate " + tok + " emblem");
		} catch (Exception ex) {
			logger.error("error",ex);
		}

	}

	@Override
	public ZoneEnum playableFrom() {
		return ZoneEnum.BATTLEFIELD;
	}
}

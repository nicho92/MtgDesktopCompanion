package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class CreateActions extends AbstractAction {

	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private DisplayableCard card;

	public CreateActions(DisplayableCard card) {
		super("Create a token");
		putValue(SHORT_DESCRIPTION, "Generate a token");
		putValue(MNEMONIC_KEY, KeyEvent.VK_T);
		this.card = card;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			MagicCard tok = GamePanelGUI.getInstance().getTokenGenerator().generateTokenFor(card.getMagicCard());
			DisplayableCard dc = new DisplayableCard(tok, MTGControler.getInstance().getCardsDimension(), true);
			dc.setMagicCard(tok);
			GamePanelGUI.getInstance().getPanelBattleField().addComponent(dc);
			GamePanelGUI.getInstance().getPanelBattleField().revalidate();
			GamePanelGUI.getInstance().getPanelBattleField().repaint();
			GamePanelGUI.getInstance().getPlayer().playToken(tok);
		} catch (Exception ex) {
			logger.error(ex);
		}

	}
}

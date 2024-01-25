package org.magic.game.actions.cards;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.game.ZoneEnum;
import org.magic.api.interfaces.MTGTokensProvider;
import org.magic.game.actions.abbstract.AbstractCardAction;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.services.MTGControler;

public class CreateActions extends AbstractCardAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public CreateActions(DisplayableCard card) {
		super(card,"Create a token");
		putValue(SHORT_DESCRIPTION, "Generate a token");
		putValue(MNEMONIC_KEY, KeyEvent.VK_T);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			MTGCard tok = getEnabledPlugin(MTGTokensProvider.class).generateTokenFor(card.getMagicCard());
			
			if(tok==null)
			{
				MTGControler.getInstance().notify(new Exception("Can't generate token for " + card.getMagicCard()));
				return;
			}
			
			logger.info("Generating token for {} = {}",card.getMagicCard(),tok);
			var dc = new DisplayableCard(tok, MTGControler.getInstance().getCardsGameDimension(), true);
			dc.setMagicCard(tok);
			GamePanelGUI.getInstance().getPanelBattleField().addComponent(dc);
			GamePanelGUI.getInstance().getPanelBattleField().revalidate();
			GamePanelGUI.getInstance().getPanelBattleField().repaint();
			GamePanelGUI.getInstance().getPlayer().playToken(tok);
		} catch (Exception ex) {
			logger.error("error creating action", ex);
		}

	}

	@Override
	public ZoneEnum playableFrom() {
		return ZoneEnum.BATTLEFIELD;
	}
}

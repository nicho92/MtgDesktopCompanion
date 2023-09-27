package org.magic.game.actions.cards;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.magic.api.beans.game.ZoneEnum;
import org.magic.api.interfaces.MTGTokensProvider;
import org.magic.game.actions.abbstract.AbstractCardAction;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.counters.ItemCounter;
import org.magic.services.MTGControler;

public class EmbalmActions extends AbstractCardAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	

	public EmbalmActions(DisplayableCard card) {
		super(card,"Embalm");
		putValue(SHORT_DESCRIPTION, "Create a embalmed copy");
		putValue(MNEMONIC_KEY, KeyEvent.VK_E);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			var tok = getEnabledPlugin(MTGTokensProvider.class).generateTokenFor(card.getMagicCard());

			if(tok==null)
			{
				MTGControler.getInstance().notify(new Exception("Can't generate token for " + card.getMagicCard()));
				return;
			}
			
			var dc = new DisplayableCard(tok, MTGControler.getInstance().getCardsGameDimension(), true);
			dc.addCounter(new ItemCounter("Embalm"));

			dc.setMagicCard(tok);
			GamePanelGUI.getInstance().getPlayer().exileCardFromGraveyard(card.getMagicCard());
			GamePanelGUI.getInstance().getPanelGrave().remove(card);
			GamePanelGUI.getInstance().getPanelBattleField().addComponent(dc);
			GamePanelGUI.getInstance().getPanelBattleField().revalidate();
			GamePanelGUI.getInstance().getPanelBattleField().repaint();
			GamePanelGUI.getInstance().getPanelGrave().postTreatment(card);
			GamePanelGUI.getInstance().getPlayer().playToken(tok);
			GamePanelGUI.getInstance().getPlayer().logAction("Embalm " + card);
		} catch (Exception ex) {
			logger.error(ex);
		}

	}

	@Override
	public ZoneEnum playableFrom() {
		return ZoneEnum.GRAVEYARD;
	}
}

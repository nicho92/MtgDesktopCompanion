package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.regex.Pattern;

import org.magic.game.actions.abbstract.AbstractCardAction;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.beans.ZoneEnum;

public class FlashbackActions extends AbstractCardAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static String k = "Flashback";

	public FlashbackActions(DisplayableCard card) {
		super(card, k);
		putValue(SHORT_DESCRIPTION, k);
		putValue(MNEMONIC_KEY, KeyEvent.VK_F);
	}

	private String parse() {
		String cost;
		try {
			final String regex = "/*" + k + " \\{(.*?)\\ ";
			final String text = card.getMagicCard().getText();
			final var p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
			final var m = p.matcher(text);

			if (m.find()) {
			    cost = m.group().replaceAll(k, "").trim();
			} else {
			    cost = text.substring(text.indexOf(k + "\u2014") + k.length(), text.indexOf('('));
			}

		} catch (final Exception _) {
			cost = "";
		}
		return cost;
	}

	@Override
	public ZoneEnum playableFrom() {
		return ZoneEnum.GRAVEYARD;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		GamePanelGUI.getInstance().getPlayer().playCardFromGraveyard(card.getMagicCard());
		GamePanelGUI.getInstance().getPlayer().logAction("Flashback " + card + " for " + parse());
		GamePanelGUI.getInstance().getPanelGrave().remove(card);
		GamePanelGUI.getInstance().getPanelGrave().postTreatment(card);
		GamePanelGUI.getInstance().getPanelBattleField().addComponent(card);
		GamePanelGUI.getInstance().getPanelBattleField().revalidate();
		GamePanelGUI.getInstance().getPanelBattleField().repaint();

	}

}

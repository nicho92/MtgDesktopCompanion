package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;

public class FlashbackActions extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private DisplayableCard card;
	private static String k = "Flashback";

	public FlashbackActions(DisplayableCard card) {
		super(k);
		putValue(SHORT_DESCRIPTION, k);
		putValue(MNEMONIC_KEY, KeyEvent.VK_F);
		this.card = card;
	}

	private String parse() {
		String cost;
		try {
			String regex = "/*" + k + " \\{(.*?)\\ ";
			String text = card.getMagicCard().getText();
			var p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
			var m = p.matcher(text);

			if (m.find())
				cost = m.group().replaceAll(k, "").trim();
			else
				cost = text.substring(text.indexOf(k + "\u2014") + k.length(), text.indexOf('('));

		} catch (Exception e) {
			cost = "";
		}
		return cost;
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

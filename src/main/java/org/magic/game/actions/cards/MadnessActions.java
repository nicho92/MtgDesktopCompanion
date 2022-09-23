package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;

public class MadnessActions extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private DisplayableCard card;

	private String k = "Madness";

	public MadnessActions(DisplayableCard card) {
		super("Madness");
		putValue(SHORT_DESCRIPTION, k);
		putValue(MNEMONIC_KEY, KeyEvent.VK_M);
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

		GamePanelGUI.getInstance().getPlayer().exileCardFromHand(card.getMagicCard());
		GamePanelGUI.getInstance().getPlayer().logAction("Play madness capacity of " + card + " for " + parse());
		GamePanelGUI.getInstance().getHandPanel().remove(card);
		GamePanelGUI.getInstance().getHandPanel().postTreatment(card);
		GamePanelGUI.getInstance().getExilPanel().addComponent(card);
		GamePanelGUI.getInstance().getExilPanel().postTreatment(card);
		GamePanelGUI.getInstance().getHandPanel().updatePanel();
	}

}

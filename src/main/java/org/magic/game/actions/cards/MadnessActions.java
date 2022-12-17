package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.regex.Pattern;

import org.magic.game.actions.abbstract.AbstractCardAction;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.ZoneEnum;

public class MadnessActions extends AbstractCardAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String k = "Madness";

	public MadnessActions(DisplayableCard card) {
		super(card,"Madness");
		putValue(SHORT_DESCRIPTION, k);
		putValue(MNEMONIC_KEY, KeyEvent.VK_M);
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

	@Override
	public ZoneEnum playableFrom() {
		return ZoneEnum.HAND;
	}

}

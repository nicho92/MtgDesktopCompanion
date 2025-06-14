package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

import org.magic.api.beans.game.ZoneEnum;
import org.magic.game.actions.abbstract.AbstractCardAction;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.gui.components.dialog.SearchCardFrame;

public class ScryActions extends AbstractCardAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String k = "scry";
	private String value;

	public ScryActions(DisplayableCard c) {
		
		super(c,"Scry  cards");
		
		
		putValue(SHORT_DESCRIPTION, "Scry " + parse() + " cards");
		putValue(MNEMONIC_KEY, KeyEvent.VK_C);
	}

	private String parse() {
		try {
			value = card.getMagicCard().getText()
					.substring(card.getMagicCard().getText().toLowerCase().indexOf(k) + k.length(),
							card.getMagicCard().getText().toLowerCase().indexOf(k) + k.length() + 2)
					.trim();
		} catch (Exception _) {
			value = "X";
		}
		return value;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (value.equals("X"))
			value = JOptionPane.showInputDialog("How many scry cards ?");

		if (value != null) {
			new SearchCardFrame(GamePanelGUI.getInstance().getPlayer(),
					GamePanelGUI.getInstance().getPlayer().scry(Integer.parseInt(value.trim())), ZoneEnum.LIBRARY)
							.setVisible(true);
		}

	}

	@Override
	public ZoneEnum playableFrom() {
		return ZoneEnum.BATTLEFIELD;
	}
}
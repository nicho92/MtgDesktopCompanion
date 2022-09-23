package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.gui.components.dialog.SearchCardFrame;
import org.magic.game.model.ZoneEnum;

public class ScryActions extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	DisplayableCard c;
	String k = "scry";
	String value;

	public ScryActions(DisplayableCard c) {
		this.c = c;
		putValue(NAME, "Scry " + parse() + " cards");
		putValue(SHORT_DESCRIPTION, "");
		putValue(MNEMONIC_KEY, KeyEvent.VK_C);
	}

	private String parse() {
		try {
			value = c.getMagicCard().getText()
					.substring(c.getMagicCard().getText().toLowerCase().indexOf(k) + k.length(),
							c.getMagicCard().getText().toLowerCase().indexOf(k) + k.length() + 2)
					.trim();
		} catch (Exception e) {
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
}
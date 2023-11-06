package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.regex.Pattern;

import org.magic.api.beans.game.ZoneEnum;
import org.magic.game.actions.abbstract.AbstractCardAction;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;

public class PrototypeActions extends AbstractCardAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private final String k = "Prototype";
	
	public PrototypeActions(DisplayableCard card) {
		super(card,"Prototype");
		putValue(SHORT_DESCRIPTION, "Cast creature as Prototype");
		putValue(MNEMONIC_KEY, KeyEvent.VK_P);
		parse(card.getMagicCard().getText());
	}

	private void parse(String text) {
		var cost="";
		try {
			String regex = "/*" + k + " \\{(.*?)\\ ";
			var p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
			var m = p.matcher(text);
			
			if (m.find())
				cost = m.group().replaceAll(k, "").trim();
			else
				cost = text.substring(text.indexOf(k + "\u2014") + k.length(), text.indexOf('('));
			
		} catch (Exception e) {
			logger.error(e);
			cost = "";
		}
		
		card.getMagicCard().setCost(cost);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		GamePanelGUI.getInstance().getHandPanel().remove(card);
		GamePanelGUI.getInstance().getPanelBattleField().add(card);
		
		GamePanelGUI.getInstance().getPanelBattleField().revalidate();
		GamePanelGUI.getInstance().getPanelBattleField().repaint();

	}

	@Override
	public ZoneEnum playableFrom() {
			return ZoneEnum.HAND;
	}

}

package org.magic.game.actions.player;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.magic.api.beans.MagicCard;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.model.GameManager;
import org.magic.game.model.Player;

public class LaunchSpell extends AbstractAction {

	private Player p;
	private DisplayableCard mc;


	public LaunchSpell(Player p ,DisplayableCard mc) {
		this.p=p;
		this.mc=mc;
		
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		GameManager.getInstance().getStack().put(mc);
		
	}

}

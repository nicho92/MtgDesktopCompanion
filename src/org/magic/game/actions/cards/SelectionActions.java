package org.magic.game.actions.cards;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.border.LineBorder;

import org.magic.game.gui.components.DisplayableCard;

public class SelectionActions extends AbstractAction {

	
	private DisplayableCard card;

	public SelectionActions(DisplayableCard card) {
		super("Select");
		putValue(SHORT_DESCRIPTION, "select the card");
        putValue(MNEMONIC_KEY, KeyEvent.VK_S);
        this.card = card;
}
	
	@Override
	public void actionPerformed(ActionEvent e) {
			 if(card.isSelected())
			 {	  
			//  card.setBorder(null);
		  	  card.setSelected(false);
			 }
			 else
			 {
			  //card.setBorder(new LineBorder(Color.RED));
		  	  card.setSelected(true);
			 }
		
			 card.repaint();
	}
	
}

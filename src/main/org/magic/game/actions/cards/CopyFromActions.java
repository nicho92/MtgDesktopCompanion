package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.apache.commons.beanutils.BeanUtils;
import org.magic.api.beans.MagicCard;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.dialog.CardChooseDialog;
import org.magic.game.model.AbilitySpell;

public class CopyFromActions extends AbilitySpell {

	
	private String cost;
	DisplayableCard mc;
	
	public CopyFromActions(DisplayableCard card) {
			super("Copy","Make a copy of",card);
	        putValue(MNEMONIC_KEY, KeyEvent.VK_C);
			this.mc=card;
			
			cost="";
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		CardChooseDialog diag = new CardChooseDialog();
		
		diag.setVisible(true);
		
		
		if(diag.getSelected()!=null)
			try {
				mc.setMagicCard((MagicCard)BeanUtils.cloneBean(diag.getSelected().getMagicCard()));
			} catch (Exception e1) {
				e1.printStackTrace();
			} 
		
		mc.validate();
		mc.repaint();
	}


	@Override
	public String getCost() {
		return cost;
	}


	@Override
	public boolean isStackable() {
		return true;
	}

}

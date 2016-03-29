package org.magic.gui.components;

import javax.swing.JLabel;

import org.magic.api.beans.MagicCard;

public class MagicCardLabel extends JLabel {

	
	private MagicCard mc ;
	
	public MagicCardLabel(String name) {
		setText(name);
	}


	public void setMagicCard(MagicCard mc)
	{
		this.mc=mc;
	}
	
	
	public MagicCard getMagicCard()
	{
		return mc;
	}
	
	
}

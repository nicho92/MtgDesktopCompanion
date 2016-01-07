package org.magic.gui.components;

import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
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

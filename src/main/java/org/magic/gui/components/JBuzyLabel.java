package org.magic.gui.components;

import javax.swing.JLabel;

import org.magic.services.MTGConstants;

public class JBuzyLabel extends JLabel {
	
	public JBuzyLabel() {
		super(MTGConstants.ICON_LOADING);
		buzy(false);
	}
	
	public void buzy(boolean visible)
	{
		setText("");
		setVisible(visible);
	}
	
	
	
	public void buzy(boolean visible,String text)
	{
		setText(text);
		setVisible(visible);
	}
}

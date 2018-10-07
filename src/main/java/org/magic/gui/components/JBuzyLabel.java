package org.magic.gui.components;

import javax.swing.JLabel;

import org.magic.services.MTGConstants;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public class JBuzyLabel extends JLabel implements Observer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



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

	@Override
	public void update(Observable o, Object arg) {
		if(arg!=null)
			setText(String.valueOf(arg));
		
	}
}

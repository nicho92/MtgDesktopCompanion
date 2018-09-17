package org.magic.gui;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.magic.services.MTGLogger;

public abstract class MTGUIPanel extends JPanel {

	protected static final long serialVersionUID = 1L;
	protected transient Logger logger = MTGLogger.getLogger(this.getClass());

	public abstract ImageIcon getIcon();
	public abstract String getTitle();
	
	public MTGUIPanel()
	{
		logger.info("init GUI : " + getTitle());
	}
	
	public static MTGUIPanel build(JComponent c,String name,ImageIcon ic)
	{
		MTGUIPanel pane = new MTGUIPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getTitle() {
				return name;
			}
			
			@Override
			public ImageIcon getIcon() {
				return ic;
			}
		};
		
		pane.setLayout(new BorderLayout());
		pane.add(c,BorderLayout.CENTER);
		
		return pane;
		
		
	}
	
	
}

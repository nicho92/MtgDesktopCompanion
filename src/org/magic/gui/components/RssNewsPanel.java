package org.magic.gui.components;

import java.awt.SystemColor;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

public class RssNewsPanel extends JDesktopPane {
	public RssNewsPanel() {
		setBackground(SystemColor.inactiveCaption);
		
		  JInternalFrame internalFrame = new JInternalFrame("MTG GoldFish News", true, true, true, true);
		  internalFrame.setLocation(81, 69);
		  	internalFrame.setSize(278, 225);
		  	internalFrame.setVisible(true);
		  add(internalFrame);
	}
}

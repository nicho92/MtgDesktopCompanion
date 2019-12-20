package org.magic.gui.components.dialog;

import javax.swing.ImageIcon;

import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class StockSyncDialog  extends MTGUIComponent{

	public StockSyncDialog() {
		setLayout(new BorderLayout());
	}


	@Override
	public String getTitle() {
		return "Synchronization";
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_STOCK;
	}
	
	

	
}

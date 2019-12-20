package org.magic.gui.components.dialog;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;

import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;

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

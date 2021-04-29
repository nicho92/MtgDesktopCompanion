package org.magic.gui;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.shops.TransactionsPanel;
import org.magic.gui.components.shops.WebShopConfigPanel;
import org.magic.services.MTGConstants;
import org.magic.tools.UITools;

public class ShopGUI extends MTGUIComponent {

	
	private JTabbedPane pane;
	
	
	public ShopGUI() {
		pane = new JTabbedPane();
		setLayout(new BorderLayout());
		
		
		add(pane,BorderLayout.CENTER);
		
		
		UITools.addTab(pane, new TransactionsPanel());
		pane.addTab("config", new WebShopConfigPanel());
	}

	@Override
	public String getTitle() {
		return "Shop";
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_SHOP;
	}
	
	
	
}

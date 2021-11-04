package org.magic.gui;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.shops.ContactsManagementPanel;
import org.magic.gui.components.shops.TransactionsPanel;
import org.magic.gui.components.shops.WebShopConfigPanel;
import org.magic.gui.components.shops.extshop.ConverterPanel;
import org.magic.gui.components.shops.extshop.ProductsCreatorComponent;
import org.magic.gui.components.shops.extshop.StockSynchronizerComponent;
import org.magic.gui.components.shops.extshop.TransactionCreatorComponent;
import org.magic.services.MTGConstants;
import org.magic.tools.UITools;

public class ShopGUI extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JTabbedPane pane;
	private JTabbedPane subPane;
	
	public ShopGUI() {
		setLayout(new BorderLayout());
		
		pane = new JTabbedPane();
		subPane = new JTabbedPane();
		add(pane,BorderLayout.CENTER);
		
		UITools.addTab(pane,new WebShopConfigPanel());
		UITools.addTab(pane, new TransactionsPanel());
		UITools.addTab(pane, new ContactsManagementPanel());
		
		UITools.addTab(subPane, new ProductsCreatorComponent());
		UITools.addTab(subPane, new TransactionCreatorComponent());
		UITools.addTab(subPane, new StockSynchronizerComponent());
		UITools.addTab(subPane, new ConverterPanel());
		
		
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

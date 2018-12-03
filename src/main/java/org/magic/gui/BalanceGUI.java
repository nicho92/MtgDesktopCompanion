package org.magic.gui;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.OrderEntry;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.pricers.impl.MagicCardMarketPricer2;
import org.magic.api.shopping.impl.MagicCardmarketShopper;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.ShoppingEntryTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.UITools;

public class BalanceGUI extends MTGUIComponent {
	
	private static final long serialVersionUID = 1L;
	private ShoppingEntryTableModel model;
	
	public BalanceGUI() {
		
		JPanel panneauHaut = new JPanel();
		JXTable table = new JXTable();
		model = new ShoppingEntryTableModel();
		JButton btnNewEntry = new JButton(MTGConstants.ICON_NEW);
		JButton btnImportTransaction = new JButton(MTGConstants.ICON_IMPORT);

		UITools.initTableFilter(table);
		
		setLayout(new BorderLayout(0, 0));
		table.setModel(model);
		
		panneauHaut.add(btnNewEntry);
		panneauHaut.add(btnImportTransaction);
		
		add(panneauHaut, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);
		
		
		btnNewEntry.addActionListener(ae-> model.addItem(new OrderEntry()));
		
		btnImportTransaction.addActionListener(ae->{
			MagicCardmarketShopper pricer = new MagicCardmarketShopper();
			
			try {
				model.addItems(pricer.listOrders());
			} catch (IOException e) {
				
			}
		});
		
	}

	public static void main(String[] args) {
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		MTGUIComponent.createJDialog(MTGUIComponent.build(new BalanceGUI(), "Balance",MTGConstants.ICON_SHOP ),true, false).setVisible(true);
	}

	@Override
	public String getTitle() {
		return MTGControler.getInstance().getLangService().getCapitalize("SHOPPING_MODULE");
	}
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_SHOP;
	}
	
	

}

package org.magic.gui.components;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTextField;

import org.magic.api.interfaces.MTGExternalShop;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.tools.UITools;

import javax.swing.JScrollPane;
import javax.swing.JList;

public class ProductsCreatorComponent extends JPanel {
	private JTextField txtSearchProduct;
	private JList<MTGStockItem> listItems;
	
	
	public ProductsCreatorComponent() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panelNorth = new JPanel();	
		txtSearchProduct = new JTextField(25);
		var comboBox =UITools.createCombobox(MTGExternalShop.class,true);
		listItems = new JList<>();
		
		panelNorth.add(txtSearchProduct);
		panelNorth.add(comboBox);
		add(panelNorth, BorderLayout.NORTH);
		add(new JScrollPane(listItems), BorderLayout.WEST);
		
	}

}

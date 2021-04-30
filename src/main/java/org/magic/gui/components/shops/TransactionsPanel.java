package org.magic.gui.components.shops;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.magic.api.beans.Proposition;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.tools.UITools;

public class TransactionsPanel extends MTGUIComponent {
	private JTable table;
	private GenericTableModel<Proposition> model;
	
	
	public TransactionsPanel() {
		setLayout(new BorderLayout(0, 0));
		var panneauHaut = new JPanel();
		model = new GenericTableModel<>();
		var btnValidate = new JButton(MTGConstants.ICON_CHECK);
		var btnDecline = new JButton(MTGConstants.ICON_DELETE);
		table = UITools.createNewTable(model);
	
		model.setColumns("id","contact","dateProposition","items");
		
		
		add(new JScrollPane(table));
		add(panneauHaut, BorderLayout.NORTH);
		panneauHaut.add(btnValidate);
		panneauHaut.add(btnDecline);
			
		
	}


	@Override
	public String getTitle() {
		return "Transaction";
	}
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_EURO;
	}

	
}

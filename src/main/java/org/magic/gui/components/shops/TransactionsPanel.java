package org.magic.gui.components.shops;

import static org.magic.tools.MTG.getPlugin;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import org.magic.api.beans.Proposition;
import org.magic.api.interfaces.MTGServer;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.ServerStatePanel;
import org.magic.services.MTGConstants;
import org.magic.tools.UITools;

public class TransactionsPanel extends MTGUIComponent {
	private JTable table;
	private GenericTableModel<Proposition> model;
	
	
	public TransactionsPanel() {
		setLayout(new BorderLayout(0, 0));
		JPanel panneauHaut = new JPanel();
		model = new GenericTableModel<>();
		JButton btnValidate = new JButton(MTGConstants.ICON_CHECK);
		JButton btnDecline = new JButton(MTGConstants.ICON_DELETE);
		table = UITools.createNewTable(model);
	
		model.setColumns("id","contact","dateProposition","items");
		
		
		add(new JScrollPane(table));
		add(panneauHaut, BorderLayout.NORTH);
		panneauHaut.add(btnValidate);
		panneauHaut.add(btnDecline);
		
		for(int i = 0; i <45; i++)
			model.addItem(new Proposition(i));
		
		
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

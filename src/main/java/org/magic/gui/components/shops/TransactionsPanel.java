package org.magic.gui.components.shops;

import java.awt.BorderLayout;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.TransactionsModel;
import org.magic.gui.renderer.standard.DateTableCellEditorRenderer;
import org.magic.services.MTGConstants;
import org.magic.tools.MTG;
import org.magic.tools.UITools;

public class TransactionsPanel extends MTGUIComponent {
	private JXTable table;
	private TransactionsModel model;
	
	
	public TransactionsPanel() {
		setLayout(new BorderLayout(0, 0));
		var panneauHaut = new JPanel();
		model = new TransactionsModel();
		
		
		var btnValidate = new JButton(MTGConstants.ICON_CHECK);
		var btnDecline = new JButton(MTGConstants.ICON_DELETE);
		var btnRefresh = new JButton(MTGConstants.ICON_REFRESH);
		table = UITools.createNewTable(model);
		table.setDefaultRenderer(Date.class, new DateTableCellEditorRenderer(true));
		
		table.packAll();
		
		
		add(new JScrollPane(table));
		add(panneauHaut, BorderLayout.NORTH);
		panneauHaut.add(btnRefresh);
		panneauHaut.add(btnValidate);
		panneauHaut.add(btnDecline);
		
		btnRefresh.addActionListener(al->reload());
		
	}
	
	private void reload()
	{
		try {
			model.clear();
			model.addItems(MTG.getEnabledPlugin(MTGDao.class).listTransactions());
			model.fireTableDataChanged();
		} catch (Exception e) {
			logger.error("error loading transactions",e);
		}
	}
	
	

	@Override
	public void onFirstShowing() {
		reload();
		
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

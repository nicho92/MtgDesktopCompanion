package org.magic.gui.components.shops;

import java.awt.BorderLayout;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.Transaction;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.CardStockPanel;
import org.magic.gui.components.ContactPanel;
import org.magic.gui.models.TransactionsModel;
import org.magic.gui.renderer.standard.DateTableCellEditorRenderer;
import org.magic.services.MTGConstants;
import org.magic.tools.MTG;
import org.magic.tools.UITools;

public class TransactionsPanel extends MTGUIComponent {
	private JXTable table;
	private TransactionsModel model;
	private ContactPanel contactPanel;
	private TransactionManagementPanel managementPanel;
	
	
	public TransactionsPanel() {
		setLayout(new BorderLayout(0, 0));
		var panneauHaut = new JPanel();
		var stockDetailPanel = new CardStockPanel();
		var tabbedPane = new JTabbedPane();
		contactPanel = new ContactPanel(true);
		managementPanel= new TransactionManagementPanel();
		model = new TransactionsModel();
		
		
		var btnRefresh = new JButton(MTGConstants.ICON_REFRESH);
		table = UITools.createNewTable(model);
		table.setDefaultRenderer(Date.class, new DateTableCellEditorRenderer(true));
		UITools.initTableFilter(table);

		var stockManagementPanel = new JPanel();
			   stockManagementPanel.setLayout(new BorderLayout());
			   stockManagementPanel.add(stockDetailPanel,BorderLayout.CENTER);
			   stockManagementPanel.add(managementPanel,BorderLayout.EAST);
			   
		UITools.addTab(tabbedPane, MTGUIComponent.build(stockManagementPanel, stockDetailPanel.getName(), stockDetailPanel.getIcon()));
		UITools.addTab(tabbedPane, contactPanel);
		
		
		table.packAll();
		stockDetailPanel.showAllColumns();
		
		add(new JScrollPane(table));
		add(panneauHaut, BorderLayout.NORTH);
		add(tabbedPane,BorderLayout.SOUTH);
		panneauHaut.add(btnRefresh);
		
		table.getSelectionModel().addListSelectionListener(lsl->{
			
			Transaction t = UITools.getTableSelection(table, 0);
			
			if(t==null)
				return;
			
			stockDetailPanel.initMagicCardStock(t.getItems());
			contactPanel.setContact(t.getContact());
			managementPanel.setTransaction(t);
			stockDetailPanel.disableCommands();
		});
		
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

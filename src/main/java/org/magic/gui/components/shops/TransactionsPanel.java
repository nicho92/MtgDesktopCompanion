package org.magic.gui.components.shops;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

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
import org.magic.gui.components.ObjectViewerPanel;
import org.magic.gui.models.TransactionsModel;
import org.magic.gui.renderer.standard.DateTableCellEditorRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.TransactionService;
import org.magic.tools.MTG;
import org.magic.tools.UITools;
import org.magic.tools.WooCommerceTools;

public class TransactionsPanel extends MTGUIComponent {
	private JXTable table;
	private TransactionsModel model;
	private ContactPanel contactPanel;
	private TransactionManagementPanel managementPanel;
	private ObjectViewerPanel objectPanel;
	
	public TransactionsPanel() {
		setLayout(new BorderLayout(0, 0));
		var panneauHaut = new JPanel();
		var stockDetailPanel = new CardStockPanel();
		var tabbedPane = new JTabbedPane();
		contactPanel = new ContactPanel(true);
		managementPanel= new TransactionManagementPanel();
		model = new TransactionsModel();
		objectPanel = new ObjectViewerPanel();
		
		var btnRefresh = new JButton(MTGConstants.ICON_REFRESH);
		var btnMerge = new JButton(MTGConstants.ICON_IMPORT);
		btnMerge.setEnabled(false);
		
		table = UITools.createNewTable(model);
		table.setDefaultRenderer(Date.class, new DateTableCellEditorRenderer(true));
		UITools.initTableFilter(table);

		var stockManagementPanel = new JPanel();
			   stockManagementPanel.setLayout(new BorderLayout());
			   stockManagementPanel.add(stockDetailPanel,BorderLayout.CENTER);
			   stockManagementPanel.add(managementPanel,BorderLayout.EAST);
			   
		UITools.addTab(tabbedPane, MTGUIComponent.build(stockManagementPanel, stockDetailPanel.getName(), stockDetailPanel.getIcon()));
		UITools.addTab(tabbedPane, contactPanel);
		
		if(MTGControler.getInstance().get("debug-json-panel").equals("true"))
				UITools.addTab(tabbedPane, objectPanel);
		
		table.packAll();
		stockDetailPanel.showAllColumns();
		
		add(new JScrollPane(table));
		add(panneauHaut, BorderLayout.NORTH);
		add(tabbedPane,BorderLayout.SOUTH);
		panneauHaut.add(btnRefresh);
		panneauHaut.add(btnMerge);
		
		stockDetailPanel.disableCommands();
		table.getSelectionModel().addListSelectionListener(lsl->{
			
			List<Transaction> t = UITools.getTableSelections(table, 0);

			if(t.isEmpty())
				return;
			
			
			
			btnMerge.setEnabled(t.size()>1);
			
			
			stockDetailPanel.initMagicCardStock(t.get(0).getItems());
			contactPanel.setContact(t.get(0).getContact());
			managementPanel.setTransaction(t.get(0));
			objectPanel.show(t.get(0));
			
		});
		
		btnRefresh.addActionListener(al->reload());
		
		
		btnMerge.addActionListener(al->{
			List<Transaction> t = UITools.getTableSelections(table, 0);
			try {
				TransactionService.mergeTransactions(t);
			} catch (SQLException e) {
				MTGControler.getInstance().notify(e);
			}
			
		});
		
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

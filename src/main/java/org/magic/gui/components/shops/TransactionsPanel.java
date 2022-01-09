package org.magic.gui.components.shops;

import java.awt.BorderLayout;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.shop.Transaction;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.ContactPanel;
import org.magic.gui.components.ObjectViewerPanel;
import org.magic.gui.models.TransactionsTableModel;
import org.magic.gui.renderer.standard.DateTableCellEditorRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.TransactionService;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.UITools;

import com.jogamp.newt.event.KeyEvent;

public class TransactionsPanel extends MTGUIComponent {
	
	private static final long serialVersionUID = 1L;
	private JXTable table;
	private TransactionsTableModel model;
	private ContactPanel contactPanel;
	private TransactionManagementPanel managementPanel;
	private ObjectViewerPanel viewerPanel;
	private JPanel panneauHaut;
	private AbstractBuzyIndicatorComponent buzy;
	
	public TransactionsPanel() {
		setLayout(new BorderLayout(0, 0));
		panneauHaut = new JPanel();
		var splitPanel = new JSplitPane();
		
		var stockDetailPanel = new StockItemPanel();
		var tabbedPane = new JTabbedPane();
		contactPanel = new ContactPanel(true);
		managementPanel= new TransactionManagementPanel();
		model = new TransactionsTableModel();
		viewerPanel = new ObjectViewerPanel();
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		var btnRefresh = UITools.createBindableJButton("", MTGConstants.ICON_REFRESH,KeyEvent.VK_R,"reload");
		var btnMerge = UITools.createBindableJButton("", MTGConstants.ICON_MERGE,KeyEvent.VK_M,"merge");
		var btnDelete = UITools.createBindableJButton("", MTGConstants.ICON_DELETE,KeyEvent.VK_D,"delete");
		
		btnMerge.setEnabled(false);
		btnDelete.setEnabled(false);
		splitPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPanel.setDividerLocation(.5);
		splitPanel.setResizeWeight(0.5);
		
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
			UITools.addTab(tabbedPane, viewerPanel);
		
		table.packAll();
		
		
		
		splitPanel.setLeftComponent(new JScrollPane(table));
		splitPanel.setRightComponent(tabbedPane);
		add(panneauHaut, BorderLayout.NORTH);
		add(splitPanel,BorderLayout.CENTER);
		panneauHaut.add(btnRefresh);
		panneauHaut.add(btnMerge);
		panneauHaut.add(btnDelete);
		panneauHaut.add(buzy);
		
		
		table.getSelectionModel().addListSelectionListener(lsl->{
			
			List<Transaction> t = UITools.getTableSelections(table, 0);

			if(t.isEmpty())
				return;
			
			
			
			btnMerge.setEnabled(t.size()>1);
			btnDelete.setEnabled(!t.isEmpty());
			
			stockDetailPanel.initItems(t.get(0).getItems());
			contactPanel.setContact(t.get(0).getContact());
			managementPanel.setTransaction(t.get(0));
			viewerPanel.show(t.get(0));
		});
		
		btnRefresh.addActionListener(al->reload());
		
		
		btnDelete.addActionListener(al->{
			
			
			int res = JOptionPane.showConfirmDialog(this, "Delete Transaction will NOT update stock","Sure ?",JOptionPane.YES_NO_OPTION);
			
			
			if(res == JOptionPane.YES_OPTION) {
			
				List<Transaction> t = UITools.getTableSelections(table, 0);
				try {
					TransactionService.deleteTransaction(t);
					reload();
				} catch (Exception e) {
					MTGControler.getInstance().notify(e);
				}
			}
		});
		
		btnMerge.addActionListener(al->{
			List<Transaction> t = UITools.getTableSelections(table, 0);
			try {
				TransactionService.mergeTransactions(t);
				reload();
			} catch (Exception e) {
				MTGControler.getInstance().notify(e);
			}
			
		});
		
	}
	
	public JTable getTable() {
		return table;
	}
	
	public TransactionsTableModel getModel() {
		return model;
	}

	
	public void init(List<Transaction> list)
	{
		try {
			model.clear();
			model.addItems(list);
			model.fireTableDataChanged();
		} catch (Exception e) {
			logger.error("error loading transactions",e);
		}
	}
	
	private void reload()
	{
		buzy.start();
		model.clear();
		var sw = new SwingWorker<List<Transaction>, Void>(){

			@Override
			protected List<Transaction> doInBackground() throws Exception {
				return TransactionService.listTransactions();
			}
			
			@Override
			protected void done() {
				try {
					model.addItems(get());
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					logger.error(e);
				}
				buzy.end();
				model.fireTableDataChanged();
			}
			
			
		};
		
		ThreadManager.getInstance().runInEdt(sw, "Load transactions");
		
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

	public void disableCommands() {
		managementPanel.setVisible(false);
		panneauHaut.setVisible(false);
		model.setWritable(false);
	}

	
	
}

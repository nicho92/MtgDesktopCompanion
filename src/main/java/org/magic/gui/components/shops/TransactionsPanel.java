package org.magic.gui.components.shops;

import java.awt.BorderLayout;
import java.sql.SQLException;
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
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGShopper;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.ContactPanel;
import org.magic.gui.components.ObjectViewerPanel;
import org.magic.gui.components.dialog.TransactionsImporterDialog;
import org.magic.gui.models.TransactionsTableModel;
import org.magic.gui.renderer.standard.DateTableCellEditorRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.TransactionService;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;

import com.jogamp.newt.event.KeyEvent;

public class TransactionsPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JXTable tableTransactions;
	private TransactionsTableModel model;
	private ContactPanel contactPanel;
	private TransactionManagementPanel managementPanel;
	private ObjectViewerPanel viewerPanel;
	private JPanel panneauHaut;
	private AbstractBuzyIndicatorComponent buzy;
	private TransactionTotalPanel panneauBas;

	
	
	public TransactionsPanel() {
		setLayout(new BorderLayout(0, 0));
		panneauHaut = new JPanel();
		var splitPanel = new JSplitPane();
		var stockDetailPanel = new StockItemPanel();
		var tabbedPane = new JTabbedPane();
		panneauBas = new TransactionTotalPanel();
		contactPanel = new ContactPanel(true);
		managementPanel= new TransactionManagementPanel();
		model = new TransactionsTableModel();
		viewerPanel = new ObjectViewerPanel();
		
	
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		var btnRefresh = UITools.createBindableJButton("", MTGConstants.ICON_REFRESH,KeyEvent.VK_R,"reload");
		var btnMerge = UITools.createBindableJButton("", MTGConstants.ICON_MERGE,KeyEvent.VK_M,"merge");
		var btnDelete = UITools.createBindableJButton("", MTGConstants.ICON_DELETE,KeyEvent.VK_D,"delete");
		var btnImportTransaction = UITools.createBindableJButton(null,MTGConstants.ICON_IMPORT,KeyEvent.VK_I,"transaction import");
		
		
		btnMerge.setEnabled(false);
		btnDelete.setEnabled(false);
		splitPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPanel.setDividerLocation(.5);
		splitPanel.setResizeWeight(0.5);

		
		
		tableTransactions = UITools.createNewTable(model);
		tableTransactions.setDefaultRenderer(Date.class, new DateTableCellEditorRenderer(true));
		UITools.initTableFilter(tableTransactions);
		
		

		var stockManagementPanel = new JPanel();
			   stockManagementPanel.setLayout(new BorderLayout());
			   stockManagementPanel.add(stockDetailPanel,BorderLayout.CENTER);
			   stockManagementPanel.add(managementPanel,BorderLayout.EAST);

		UITools.addTab(tabbedPane, MTGUIComponent.build(stockManagementPanel, stockDetailPanel.getName(), stockDetailPanel.getIcon()));
		UITools.addTab(tabbedPane, contactPanel);

		if(MTGControler.getInstance().get("debug-json-panel").equals("true"))
			UITools.addTab(tabbedPane, viewerPanel);

		tableTransactions.packAll();



		splitPanel.setLeftComponent(new JScrollPane(tableTransactions));
		splitPanel.setRightComponent(tabbedPane);
		add(panneauHaut, BorderLayout.NORTH);
		add(splitPanel,BorderLayout.CENTER);
		add(panneauBas,BorderLayout.SOUTH);
		
		panneauHaut.add(btnImportTransaction);
		panneauHaut.add(btnRefresh);
		panneauHaut.add(btnMerge);
		panneauHaut.add(btnDelete);
		panneauHaut.add(buzy);

		
		btnImportTransaction.addActionListener(ae->{
			var diag = new TransactionsImporterDialog();
			diag.setVisible(true);

			if(diag.getSelectedEntries()!=null) {
				
				
				
				var sw = new AbstractObservableWorker<List<Transaction>, Transaction,MTGShopper>(buzy,diag.getSelectedSniffer(),diag.getSelectedEntries().size())
						{

							@Override
							protected List<Transaction> doInBackground() throws Exception {
								return plug.listTransactions(diag.getSelectedEntries());
							}

							@Override
							protected void notifyEnd() {
								model.addItems(getResult());
								//todo add Transactionn save
							}
						};
					
						ThreadManager.getInstance().runInEdt(sw, "importing transactions");
			}
		});
		
		tableTransactions.getSelectionModel().addListSelectionListener(lsl->{

			List<Transaction> t = UITools.getTableSelections(tableTransactions, 0);

			if(t.isEmpty())
				return;

			panneauBas.calulate(t, model);

			btnMerge.setEnabled(t.size()>1);
			btnDelete.setEnabled(!t.isEmpty());

			stockDetailPanel.initItems(t.get(0).getItems());
			contactPanel.setContact(t.get(0).getContact());
			managementPanel.setTransaction(t.get(0));
			viewerPanel.init(t.get(0));
		});


		tableTransactions.getModel().addTableModelListener(tml->{
			if(tml.getFirstRow() >0 && tml.getType()==0)
			{ 
				try{ 
					
					buzy.start();
					var sw = new SwingWorker<Void, Void>()
					{

						@Override
						protected Void doInBackground() throws Exception {
							try {
								MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateTransaction(model.getItemAt(tml.getFirstRow()));
							} catch (SQLException e) {
								logger.error(e);
							}
							return null;
						}
						
						@Override
						protected void done() {
							buzy.end();
							panneauBas.refresh();
						}
						
				
					};
					
					ThreadManager.getInstance().runInEdt(sw, "Saving transaction");
				}
				catch(Exception e)
				{
					
				}
				
			}
			
		});
		
		btnRefresh.addActionListener(al->reload());


		btnDelete.addActionListener(al->{


			int res = JOptionPane.showConfirmDialog(this, "Delete Transaction will NOT update stock","Sure ?",JOptionPane.YES_NO_OPTION);


			if(res == JOptionPane.YES_OPTION) {

				List<Transaction> t = UITools.getTableSelections(tableTransactions, 0);
				try {
					TransactionService.deleteTransaction(t);
					reload();
				} catch (Exception e) {
					MTGControler.getInstance().notify(e);
				}
			}
		});

		btnMerge.addActionListener(al->{
			List<Transaction> t = UITools.getTableSelections(tableTransactions, 0);
			try {
				TransactionService.mergeTransactions(t);
				reload();
			} catch (Exception e) {
				MTGControler.getInstance().notify(e);
			}

		});

	}

	public JTable getTable() {
		return tableTransactions;
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
					panneauBas.calulate(get(), model);
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

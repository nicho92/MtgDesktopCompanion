package org.magic.gui.components.shops;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SortOrder;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.shop.Transaction;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.ContactPanel;
import org.magic.gui.components.ObjectViewerPanel;
import org.magic.gui.components.dialog.JContactChooserDialog;
import org.magic.gui.components.dialog.TransactionsImporterDialog;
import org.magic.gui.models.TransactionsTableModel;
import org.magic.gui.renderer.standard.DateTableCellEditorRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.TransactionService;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

import com.jogamp.newt.event.KeyEvent;

public class TransactionsPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JXTable tableTransactions;
	private TransactionsTableModel model;
	private ContactPanel contactPanel;
	private ObjectViewerPanel viewerPanel;
	private JPanel panneauHaut;
	private AbstractBuzyIndicatorComponent buzy;
	private TransactionTotalPanel panneauBas;
	private TransactionTrackingPanel trackPanel;
	
	public TransactionsPanel() {
		setLayout(new BorderLayout(0, 0));
		panneauHaut = new JPanel();
		var splitPanel = new JSplitPane();
		var stockDetailPanel = new StockItemPanel();
		panneauBas = new TransactionTotalPanel();
		contactPanel = new ContactPanel(true);
		model = new TransactionsTableModel();
		viewerPanel = new ObjectViewerPanel();
		trackPanel=  new TransactionTrackingPanel();
	
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		var btnRefresh = UITools.createBindableJButton("", MTGConstants.ICON_REFRESH,KeyEvent.VK_R,"reload");
		var btnMerge = UITools.createBindableJButton("", MTGConstants.ICON_MERGE,KeyEvent.VK_M,"merge");
		var btnDelete = UITools.createBindableJButton("", MTGConstants.ICON_DELETE,KeyEvent.VK_D,"delete");
		var btnContact = UITools.createBindableJButton("", MTGConstants.ICON_CONTACT,KeyEvent.VK_C,"contact");
		var btnImportTransaction = UITools.createBindableJButton(null,MTGConstants.ICON_IMPORT,KeyEvent.VK_I,"transaction import");
		
		
		btnMerge.setEnabled(false);
		btnDelete.setEnabled(false);
		btnContact.setEnabled(false);
		splitPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPanel.setDividerLocation(.5);
		splitPanel.setResizeWeight(0.5);

		
		
		tableTransactions = UITools.createNewTable(model);
		tableTransactions.setDefaultRenderer(Date.class, new DateTableCellEditorRenderer(true));
		UITools.initTableFilter(tableTransactions);
		UITools.sort(tableTransactions, 1, SortOrder.DESCENDING);
		
		
		
		UITools.addTab(getContextTabbedPane(), stockDetailPanel);
		UITools.addTab(getContextTabbedPane(), trackPanel);
		
		

		if(MTG.readPropertyAsBoolean("debug-json-panel"))
			UITools.addTab(getContextTabbedPane(), viewerPanel);

		tableTransactions.packAll();

		splitPanel.setLeftComponent(new JScrollPane(tableTransactions));
		splitPanel.setRightComponent(getContextTabbedPane());
		add(panneauHaut, BorderLayout.NORTH);
		add(splitPanel,BorderLayout.CENTER);
		add(panneauBas,BorderLayout.SOUTH);
		
		panneauHaut.add(btnImportTransaction);
		panneauHaut.add(btnRefresh);
		panneauHaut.add(btnMerge);
		panneauHaut.add(btnDelete);
		panneauHaut.add(btnContact);
		
		panneauHaut.add(buzy);

		
		btnImportTransaction.addActionListener(ae->{
			var diag = new TransactionsImporterDialog();
			diag.setVisible(true);

			if(diag.getSelectedEntries()!=null) {
			//TODO put in swingworker
				for(var t : diag.getSelectedEntries())
				{
					try {
						TransactionService.saveTransaction(t, false);
						model.fireTableDataChanged();
					} catch (IOException e) {
						logger.error(e);
					}
					model.fireTableDataChanged();
				}
					
			}
		});
		
		tableTransactions.getSelectionModel().addListSelectionListener(lsl->{

			List<Transaction> t = UITools.getTableSelections(tableTransactions, 0);

			if(t.isEmpty())
				return;

			panneauBas.calulate(t, model);

			btnMerge.setEnabled(t.size()>1);
			btnDelete.setEnabled(!t.isEmpty());
			btnContact.setEnabled(t.size()==1);
			
			stockDetailPanel.initItems(t.get(0).getItems());
			trackPanel.init(t.get(0));
			contactPanel.setContact(t.get(0).getContact());
			
			if(MTG.readPropertyAsBoolean("debug-json-panel"))
				viewerPanel.init(t.get(0));
		});
		
		stockDetailPanel.getTable().getModel().addTableModelListener(tml->{
			if(tml.getFirstRow() >0 && tml.getType()==0)
			{
				logger.info("tml");
			}
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
								TransactionService.saveTransaction(model.getItemAt(tml.getFirstRow()), false);
							} catch (IOException e) {
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
		
		
		btnContact.addActionListener(al->{
			var diag = new JContactChooserDialog();
				  diag.setVisible(true);
											   
				if(diag.getSelectedContacts()!=null)
				{
					Transaction t = UITools.getTableSelection(tableTransactions, 0);
					var c =  diag.getSelectedContacts() ;
					int res = JOptionPane.showConfirmDialog(this, "Confirm " +c+ " to transaction #"+t.getId(),"Sure ?",JOptionPane.YES_NO_OPTION);

					if(res==JOptionPane.YES_OPTION)
					{	
						t.setContact(c);
						try {
							TransactionService.saveTransaction(t, false);
						} catch (IOException e) {
								logger.error(e);
						}
					}
				}
		});
		

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
		panneauHaut.setVisible(false);
		model.setWritable(false);
		
		
	}

	

}

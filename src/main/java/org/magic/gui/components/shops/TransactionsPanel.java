package org.magic.gui.components.shops;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SortOrder;
import javax.swing.SwingWorker;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.GedPanel;
import org.magic.gui.components.dialog.JContactChooserDialog;
import org.magic.gui.components.dialog.TransactionsImporterDialog;
import org.magic.gui.components.tech.ObjectViewerPanel;
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
	private TransactionsTableModel transactionModel;
	private ContactPanel contactPanel;
	private ObjectViewerPanel viewerPanel;
	private JPanel panneauHaut;
	private AbstractBuzyIndicatorComponent buzy;
	private TransactionTotalPanel panneauBas;	
	private TransactionTrackingPanel trackPanel;
	private StockItemPanel stockDetailPanel;
	private GedPanel<Transaction> gedPanel;
	private JButton btnMerge;
	private JButton btnDelete;
	private JButton btnNew;
	private JButton btnContact;
	private JButton btnImportTransaction;
	private Contact contact;
	private JButton btnValidateTransaction;
	
	public TransactionsPanel() {
		setLayout(new BorderLayout(0, 0));
		panneauHaut = new JPanel();
		var splitPanel = new JSplitPane();
		stockDetailPanel = new StockItemPanel();
		panneauBas = new TransactionTotalPanel();
		contactPanel = new ContactPanel(true);
		transactionModel = new TransactionsTableModel();
		viewerPanel = new ObjectViewerPanel();
		trackPanel=  new TransactionTrackingPanel();
		gedPanel = new GedPanel<>();
		var chkEditingMode = new JCheckBox("Editing mode");
		
		
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		btnNew = UITools.createBindableJButton("", MTGConstants.ICON_NEW,KeyEvent.VK_N,"new");
		var btnSearch = UITools.createBindableJButton("", MTGConstants.ICON_FILTER,KeyEvent.VK_S,"search");
		var btnRefresh = UITools.createBindableJButton("", MTGConstants.ICON_REFRESH,KeyEvent.VK_R,"reload");
		btnMerge = UITools.createBindableJButton("", MTGConstants.ICON_MERGE,KeyEvent.VK_M,"merge");
		btnDelete = UITools.createBindableJButton("", MTGConstants.ICON_DELETE,KeyEvent.VK_D,"delete");
		btnContact = UITools.createBindableJButton("", MTGConstants.ICON_CONTACT,KeyEvent.VK_C,"contact");
		btnImportTransaction = UITools.createBindableJButton(null,MTGConstants.ICON_IMPORT,KeyEvent.VK_I,"transaction import");
		btnValidateTransaction = UITools.createBindableJButton(null,MTGConstants.ICON_CHECK,KeyEvent.VK_V,"transaction validate");
		
	
		splitPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPanel.setDividerLocation(.5);
		splitPanel.setResizeWeight(0.5);

		
		
		tableTransactions = UITools.createNewTable(transactionModel,true);
		
		tableTransactions.setDefaultRenderer(Date.class, new DateTableCellEditorRenderer(false));
		UITools.sort(tableTransactions, 1, SortOrder.DESCENDING);
		UITools.addTab(getContextTabbedPane(), stockDetailPanel);
		UITools.addTab(getContextTabbedPane(), trackPanel);
		UITools.addTab(getContextTabbedPane(), gedPanel);
		

		if(MTG.readPropertyAsBoolean("debug-json-panel"))
			UITools.addTab(getContextTabbedPane(), viewerPanel);

		tableTransactions.packAll();

		splitPanel.setLeftComponent(new JScrollPane(tableTransactions));
		splitPanel.setRightComponent(getContextTabbedPane());
		add(panneauHaut, BorderLayout.NORTH);
		add(splitPanel,BorderLayout.CENTER);
		add(panneauBas,BorderLayout.SOUTH);
		
		
		panneauHaut.add(btnSearch);
		panneauHaut.add(btnNew);
		panneauHaut.add(btnImportTransaction);
		panneauHaut.add(btnRefresh);
		panneauHaut.add(btnMerge);
		panneauHaut.add(btnValidateTransaction);
		panneauHaut.add(btnDelete);
		panneauHaut.add(btnContact);
		panneauHaut.add(chkEditingMode);
		panneauHaut.add(buzy);
		
		
		transactionModel.setWritable(chkEditingMode.isSelected());
		stockDetailPanel.setWritable(chkEditingMode.isSelected());
		btnMerge.setEnabled(false);
		btnDelete.setEnabled(false);
		btnContact.setEnabled(false);
		btnNew.setEnabled(false);
		btnValidateTransaction.setEnabled(false);
		chkEditingMode.addActionListener(al->enableEditing(chkEditingMode.isSelected()));
		
		
		
		
		btnNew.addActionListener(al->{
			
			var t = new Transaction();
				 t.setContact(MTGControler.getInstance().getWebConfig().getContact());
			transactionModel.addItem(t);
		});
		
		
		btnSearch.addActionListener(al->{
		var text = JOptionPane.showInputDialog("List Transaction with product : ");
			
			@SuppressWarnings("unchecked")
			TableRowSorter<TransactionsTableModel> sorter = (TableRowSorter<TransactionsTableModel>) tableTransactions.getRowSorter();
			RowFilter<TransactionsTableModel, Integer> filter = new RowFilter<>()
					{
									@Override
									public boolean include(Entry<? extends TransactionsTableModel, ? extends Integer> entry) {
										for(var t : entry.getModel().getItemAt(entry.getIdentifier()).getItems())
										{
											
											if(t.getProduct()!=null && (t.getProduct()+"").toUpperCase().contains(text.toUpperCase()))
												return true;
										}
										return false;
									}
					};
		    sorter.setRowFilter(filter);
		    tableTransactions.setRowSorter(sorter);
		    
		});
		
		
		btnImportTransaction.addActionListener(ae->{
			var diag = new TransactionsImporterDialog();
			diag.setVisible(true);

			if(diag.isSelected()) {
				for(var t : diag.getSelectedEntries())
				{
					try {
						TransactionService.saveTransaction(t, false);
						reload();
					} catch (IOException e) {
						logger.error(e);
					}
					transactionModel.fireTableDataChanged();
				}
					
			}
		});
		
		tableTransactions.getSelectionModel().addListSelectionListener(lsl->{
			if(lsl.getValueIsAdjusting())
				return;
			
			List<Transaction> t = UITools.getTableSelections(tableTransactions, 0);
		
			if(t.isEmpty())
				return;

			buzy.start();
			var sw = new SwingWorker<List<MTGStockItem>, Void>()
					{

						@Override
						protected List<MTGStockItem> doInBackground() throws Exception {
							return t.get(0).getItems();
						}

						@Override
						protected void done() {
							
							btnMerge.setEnabled(t.size()>1);
							btnDelete.setEnabled(!t.isEmpty());
							btnContact.setEnabled(t.size()==1);
							btnValidateTransaction.setEnabled(t.size()==1);
							panneauBas.calulate(t, transactionModel);
							stockDetailPanel.bind(t.get(0).getItems());
							trackPanel.init(t.get(0));
							contactPanel.setContact(t.get(0).getContact());
							gedPanel.init(Transaction.class, t.get(0));
							
							
							if(MTG.readPropertyAsBoolean("debug-json-panel"))
								viewerPanel.init(t.get(0));
							
							buzy.end();
						}
					};
					
					ThreadManager.getInstance().runInEdt(sw, "Load items for" + t.get(0));
		
		});
		
		
		trackPanel.getComboBox().addItemListener((ItemEvent event)->{
			
			Transaction transaction = trackPanel.getTransaction();
			transaction.setTransporter(trackPanel.getComboBox().getSelectedItem().toString());
			
			var sw = new SwingWorker<Void, Void>()
			{

				@Override
				protected Void doInBackground() throws Exception {
					TransactionService.saveTransaction(transaction, false);
					return null;
				}

				@Override
				protected void done() {
					buzy.end();
				}
				
				
				 
			};
			
			
			if (event.getStateChange() == ItemEvent.SELECTED && isVisible()) {
				buzy.start();
			   ThreadManager.getInstance().runInEdt(sw, "Update tracking for transaction " + transaction);
		      }
	});
	
		
		

		tableTransactions.getModel().addTableModelListener(tml->{
			if(tml.getFirstRow() >-1 && tml.getType()==0 && tml.getLastRow()<=transactionModel.getRowCount())
			{ 
				try{ 
					
					buzy.start();
					var sw = new SwingWorker<Void, Void>()
					{

						@Override
						protected Void doInBackground() throws Exception {
							try {
								TransactionService.saveTransaction(transactionModel.getItemAt(tml.getFirstRow()), false);
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
					
					ThreadManager.getInstance().runInEdt(sw, "Saving transaction ");
				}
				catch(Exception e)
				{
					MTGControler.getInstance().notify(e);
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
			List<Transaction> t = UITools.getTableSelections(tableTransactions, 0);
			int res = JOptionPane.showConfirmDialog(this, "Delete "+t.size()+ " transaction(s) will NOT update stock","Sure ?",JOptionPane.YES_NO_OPTION);
			if(res == JOptionPane.YES_OPTION) {
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
		
		
		btnValidateTransaction.addActionListener(al->{
			Transaction t = UITools.getTableSelection(tableTransactions, 0);
			
			int res = JOptionPane.showConfirmDialog(this, "Validate #"+t.getId()+ " transaction willupdate stock ("+ t.getItems().size()+" items)","Sure ?",JOptionPane.YES_NO_OPTION);
			if(res == JOptionPane.YES_OPTION) {
			try {
				TransactionService.validateTransaction(t);
				reload();
			} catch (Exception e) {
				MTGControler.getInstance().notify(e);
			}
			}
			
		});

	}

	public JTable getTable() {
		return tableTransactions;
	}

	public TransactionsTableModel getModel() {
		return transactionModel;
	}


	public void init(Contact t) {
		this.contact = t;
		reload();
	}


	
	
	

	public void init(List<Transaction> list)
	{
		try {
			contact=null;
			transactionModel.clear();
			transactionModel.addItems(list);
			transactionModel.fireTableDataChanged();
		} catch (Exception e) {
			logger.error("error loading transactions",e);
		}
	}

	private void reload()
	{
		buzy.start();
		
		var sw = new SwingWorker<List<Transaction>, Void>(){

			@Override
			protected List<Transaction> doInBackground() throws Exception {
				
				if(contact!=null)
					return TransactionService.listTransactions(contact);
				
				return TransactionService.listTransactions();
			}

			@Override
			protected void done() {
				try {
					transactionModel.clear();
					transactionModel.addItems(get());
					panneauBas.calulate(get(), transactionModel);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					logger.error(e);
				}
				finally{
					buzy.end();
				}
				
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
		enableEditing(false);
	}
	

	private void enableEditing(boolean selected) {
		transactionModel.setWritable(selected);
		stockDetailPanel.setWritable(selected);
		btnNew.setEnabled(selected);
	}


}

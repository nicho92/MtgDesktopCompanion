package org.magic.gui.components.shops.extshop;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGExternalShop;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.shops.TransactionsPanel;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.services.workers.AbstractObservableWorker;
import org.magic.tools.UITools;


public class TransactionCreatorComponent extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JComboBox<MTGExternalShop> cboInput;
	private JComboBox<MTGExternalShop> cboOutput;
	
	private JList<Transaction> listOutput;
	private DefaultListModel<Transaction> modelOutput;
	
	private AbstractBuzyIndicatorComponent buzy;
	private JButton btnSend;
	
	private TransactionsPanel panelTransactions;

	public TransactionCreatorComponent() {
		setLayout(new BorderLayout(0, 0));

		
		btnSend = UITools.createBindableJButton("Export", MTGConstants.ICON_EXPORT, KeyEvent.VK_S,"searchProduct");
		var btnSearch = UITools.createBindableJButton("", MTGConstants.ICON_SEARCH_24, KeyEvent.VK_F,"searchProduct");
		
		var panelNorth = new JPanel();
		var panelWest = new JPanel();
		panelWest.setLayout(new BorderLayout());
		var panelEast = new JPanel();
		panelEast.setLayout(new BorderLayout());
		
		cboInput = UITools.createCombobox(MTGExternalShop.class,true);
		cboOutput= UITools.createCombobox(MTGExternalShop.class,true);
		
		buzy = AbstractBuzyIndicatorComponent.createProgressComponent();
		modelOutput= new DefaultListModel<>();
		listOutput = new JList<>(modelOutput);
		
		panelTransactions = new TransactionsPanel();
		panelTransactions.disableCommands();
		panelNorth.add(btnSearch);
		panelNorth.add(btnSend);
		panelNorth.add(buzy);
		
		
		add(panelNorth, BorderLayout.NORTH);
		add(panelWest,BorderLayout.WEST);
		add(panelEast,BorderLayout.EAST);
		
		panelWest.add(cboInput, BorderLayout.NORTH);
		panelEast.add(cboOutput, BorderLayout.NORTH);
		
		panelEast.add(new JScrollPane(listOutput), BorderLayout.CENTER);
		
		add(panelTransactions, BorderLayout.CENTER);
		
		btnSearch.addActionListener(e->loadTransactions());
		btnSend.addActionListener(e->sendTransaction());
		btnSend.setEnabled(false);
		
		
		panelTransactions.getTable().getSelectionModel().addListSelectionListener(event -> {
			if (!event.getValueIsAdjusting()) {
				btnSend.setEnabled(!UITools.getTableSelections(panelTransactions.getTable(), 0).isEmpty());
				btnSend.setText("send "+ UITools.getTableSelections(panelTransactions.getTable(), 0).size() + " items");
			}
		});
	}


	private void sendTransaction() {
		
		List<Transaction> list = UITools.getTableSelections(panelTransactions.getTable(), 0);
		
		
		AbstractObservableWorker<Void,Transaction,MTGExternalShop> sw = new AbstractObservableWorker<>(buzy,(MTGExternalShop)cboOutput.getSelectedItem(),list.size())
		{
			@Override
			protected Void doInBackground() throws Exception {
					for(Transaction p : list)
						{
							plug.saveOrUpdateTransaction(p);
							publish(p);
						}
					return null;
			}
			@Override
			protected void process(List<Transaction> chunks) {
				super.process(chunks);
				modelOutput.addAll(chunks);
			}
			@Override
			protected void done() {
				super.done();
				listOutput.updateUI();
			}
		};
		
		ThreadManager.getInstance().runInEdt(sw,"search Products");
	}


	private void loadTransactions() {
		panelTransactions.getModel().clear();
		
		AbstractObservableWorker<List<Transaction>,Transaction,MTGExternalShop> sw = new AbstractObservableWorker<>(buzy,(MTGExternalShop)cboInput.getSelectedItem())
		{
			@Override
			protected List<Transaction> doInBackground() throws Exception {
					return plug.listTransaction();
			}
			
			@Override
			protected void done() {
				super.done();
				try {
					panelTransactions.init(get());
				} catch (InterruptedException | ExecutionException e) {
					Thread.currentThread().interrupt();
				} 
			}
		};
		
		ThreadManager.getInstance().runInEdt(sw,"search Transaction");
	}


	@Override
	public String getTitle() {
		return "Transactions Importer";
	}
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_PRICES;
	}

}

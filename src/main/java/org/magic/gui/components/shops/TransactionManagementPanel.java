package org.magic.gui.components.shops;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;

import javax.script.ScriptException;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.Transaction;
import org.magic.api.beans.OrderEntry.TYPE_ITEM;
import org.magic.api.beans.OrderEntry.TYPE_TRANSACTION;
import org.magic.api.beans.Transaction.STAT;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.scripts.impl.JavaScript;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.TransactionService;
import org.magic.services.threads.ThreadManager;
import org.magic.services.workers.AbstractObservableWorker;
import org.magic.tools.UITools;

import static org.magic.tools.MTG.getEnabledPlugin;

public class TransactionManagementPanel extends MTGUIComponent {
	
	private Transaction t;
	private JButton btnAcceptTransaction;
	private JButton btnSend;
	private JButton btnSave;
	private AbstractBuzyIndicatorComponent loader;
	
	public void setTransaction(Transaction t)
	{
		this.t=t;
		btnAcceptTransaction.setEnabled(t!=null);
		btnSend.setEnabled(t!=null);
		btnSave.setEnabled(t!=null);
	}
	
	
	public TransactionManagementPanel() {
		setLayout(new BorderLayout(0, 0));
		loader = AbstractBuzyIndicatorComponent.createProgressComponent();
		btnAcceptTransaction = new JButton("Accept Transaction",MTGConstants.ICON_SMALL_CHECK);
		btnSend = new JButton("Mark as Sent",MTGConstants.ICON_TAB_DELIVERY);
		btnSave = new JButton("Save",MTGConstants.ICON_SMALL_SAVE);
		
		
		btnSend.setEnabled(false);
		btnSave.setEnabled(false);
		btnAcceptTransaction.setEnabled(false);
		
		
		var panelCenter = new JPanel();
		panelCenter.setLayout(new GridLayout(3,1));
		
		panelCenter.add(btnSave);
		panelCenter.add(btnAcceptTransaction);
		panelCenter.add(btnSend);
		
		add(panelCenter, BorderLayout.NORTH);
		add(loader,BorderLayout.SOUTH);
			
		btnSave.addActionListener(e->{
			try {
				TransactionService.update(t);
			} catch (SQLException e1) {
				MTGControler.getInstance().notify(e1);
			}
		});
		
		
		btnSend.addActionListener(e->{
			
			String text = JOptionPane.showInputDialog(this, "Tracking number ?");
			t.setTransporterShippingCode(text);
			t.setStatut(STAT.SENT);
			try {
				TransactionService.update(t);
			} catch (SQLException e1) {
				MTGControler.getInstance().notify(e1);
			}
			
		});
		
		
		btnAcceptTransaction.addActionListener(e->{
			loader.start();
			var sw = new AbstractObservableWorker<Void, MagicCardStock, MTGDao>(loader,getEnabledPlugin(MTGDao.class),t.getItems().size()) 
			{

				@Override
				protected Void doInBackground() throws Exception {
					for(MagicCardStock transactionItem : t.getItems())
					{
							MagicCardStock stock = plug.getStockById(transactionItem.getIdstock());
							if(transactionItem.getQte()>stock.getQte())
							{
								   logger.debug("Not enough stock for " + transactionItem.getIdstock() +":" + transactionItem.getMagicCard() + " " + transactionItem.getQte() +" > " + stock.getQte());
								   t.setStatut(STAT.IN_PROGRESS);
							}
							else
							{
								   stock.setQte(stock.getQte()-transactionItem.getQte());
								   plug.saveOrUpdateStock(stock);
								   plug.saveOrUpdateOrderEntry(TransactionService.toOrder(t, transactionItem));
								   t.setStatut(STAT.ACCEPTED);
							}
					}
					TransactionService.update(t);
					return null;
				}
			};
			ThreadManager.getInstance().runInEdt(sw, "update stock for transactions");
	});
	
	}


	@Override
	public String getTitle() {
		return "Transaction management";
	}
}

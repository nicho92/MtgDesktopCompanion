package org.magic.gui.components.shops;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;

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
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;
import org.magic.services.workers.AbstractObservableWorker;
import org.magic.tools.UITools;

import static org.magic.tools.MTG.getEnabledPlugin;

public class TransactionManagementPanel extends MTGUIComponent {
	
	private Transaction t;
	private JButton btnAcceptTransaction;
	private JButton btnSend;
	private AbstractBuzyIndicatorComponent loader;
	
	public void setTransaction(Transaction t)
	{
		this.t=t;
		btnAcceptTransaction.setEnabled(t!=null);
		btnSend.setEnabled(t!=null);
	}
	
	
	public TransactionManagementPanel() {
		setLayout(new BorderLayout(0, 0));
		loader = AbstractBuzyIndicatorComponent.createProgressComponent();
		btnAcceptTransaction = new JButton("Accept Transaction",MTGConstants.ICON_SMALL_CHECK);
		btnSend = new JButton("Mark as Sent",MTGConstants.ICON_TAB_DELIVERY);
		
		btnSend.setEnabled(false);
		btnAcceptTransaction.setEnabled(false);
		
		
		var panelCenter = new JPanel();
		panelCenter.setLayout(new GridLayout(2,1));
		
		panelCenter.add(btnAcceptTransaction);
		panelCenter.add(btnSend);
		
		add(panelCenter, BorderLayout.NORTH);
		add(loader,BorderLayout.SOUTH);
			
		
		btnSend.addActionListener(e->{
			
			String text = JOptionPane.showInputDialog(this, "Tracking number ?");
			t.setTransporterShippingCode(text);
			t.setStatut(STAT.SENT);
			try {
				getEnabledPlugin(MTGDao.class).saveOrUpdateTransaction(t);
			} catch (SQLException e1) {
				logger.error("error updating " + t,e1);
				MTGControler.getInstance().notify(e1);
				
			}
			
			
		});
		
		
		btnAcceptTransaction.addActionListener(e->{
			loader.start();
			var sw = new AbstractObservableWorker<Void, MagicCardStock, MTGDao>(loader,getEnabledPlugin(MTGDao.class),t.getItems().size()) 
			{

				@Override
				protected Void doInBackground() throws Exception {
					for(MagicCardStock st : t.getItems())
					{
							MagicCardStock stock = plug.getStockById(st.getIdstock());
							if(st.getQte()>stock.getQte())
							{
								   logger.debug("Not enough stock for " + st.getIdstock() +":" + st.getMagicCard() + " " + st.getQte() +" > " + stock.getQte());
								   t.setStatut(STAT.IN_PROGRESS);
							}
							else
							{
								   stock.setQte(stock.getQte()-st.getQte());
								   plug.saveOrUpdateStock(stock);
								   
								   var oe = new OrderEntry();
									   oe.setCurrency(MTGControler.getInstance().getCurrencyService().getCurrentCurrency());
									   oe.setDescription(st.getMagicCard().getName());
									   oe.setEdition(st.getMagicCard().getCurrentSet());
									   oe.setIdTransation(String.valueOf(t.getId()));
									   oe.setItemPrice(UITools.roundDouble(st.getPrice()));
									   oe.setTransactionDate(t.getDateProposition());
									   oe.setShippingPrice(UITools.roundDouble(t.getShippingPrice()));
									   oe.setSource(MTGControler.getInstance().getWebConfig().getSiteTitle());
									   oe.setType(TYPE_ITEM.CARD);
									    
									   if(stock.getPrice()>0)								   
										   oe.setTypeTransaction(TYPE_TRANSACTION.SELL);
									   else
										   oe.setTypeTransaction(TYPE_TRANSACTION.BUY);
								   
								   plug.saveOrUpdateOrderEntry(oe);
								   
								   t.setStatut(STAT.ACCEPTED);
							}
					}
					plug.saveOrUpdateTransaction(t);
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

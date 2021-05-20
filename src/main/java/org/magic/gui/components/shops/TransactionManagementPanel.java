package org.magic.gui.components.shops;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.Transaction;
import org.magic.api.beans.Transaction.STAT;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.services.threads.ThreadManager;
import org.magic.services.workers.AbstractObservableWorker;
import org.magic.tools.MTG;

public class TransactionManagementPanel extends JPanel {
	
	private Transaction t;
	private JButton btnAcceptTransaction;
	private AbstractBuzyIndicatorComponent loader;
	
	public void setTransaction(Transaction t)
	{
		this.t=t;
		btnAcceptTransaction.setEnabled(t!=null);
	}
	
	
	public TransactionManagementPanel() {
		setLayout(new BorderLayout(0, 0));
		loader = AbstractBuzyIndicatorComponent.createProgressComponent();
		
		
		btnAcceptTransaction = new JButton("Accept Transaction");
		btnAcceptTransaction.setEnabled(false);
		var panelCenter = new JPanel();
		panelCenter.add(btnAcceptTransaction);
		
		
		add(panelCenter, BorderLayout.NORTH);
		add(loader,BorderLayout.SOUTH);
			
			
		
		btnAcceptTransaction.addActionListener(e->{
			var sw = new AbstractObservableWorker<Void, MagicCardStock, MTGDao>(loader,MTG.getEnabledPlugin(MTGDao.class),t.getItems().size()) 
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
}

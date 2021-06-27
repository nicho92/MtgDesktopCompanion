package org.magic.gui.components.shops;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.Transaction;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.exports.impl.WooCommerceExport;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGTrackingService;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.TransactionService;
import org.magic.services.threads.ThreadManager;
import org.magic.services.workers.AbstractObservableWorker;
import org.magic.tools.MTG;
import org.magic.tools.UITools;

public class TransactionManagementPanel extends MTGUIComponent {
	
	private static final long serialVersionUID = 1L;
	private static final String TRACKING = "Tracking";
	private Transaction t;
	private JButton btnAcceptTransaction;
	private JButton btnSend;
	private JButton btnSave;
	private AbstractBuzyIndicatorComponent loader;
	private JButton btnWooCommerce;
	private JButton btnPaid;
	private JButton btnTrack;
	private JButton btnCancel;
	
	public void setTransaction(Transaction t)
	{
		this.t=t;
		btnAcceptTransaction.setEnabled(t!=null && (t.getStatut()==TransactionStatus.NEW||t.getStatut()==TransactionStatus.IN_PROGRESS||t.getStatut()==TransactionStatus.CANCELED));
		btnSave.setEnabled(t!=null);
		btnPaid.setEnabled(t!=null);
		btnSend.setEnabled(t!=null);
		btnWooCommerce.setEnabled(t!=null && TransactionService.isWoocommerceAvailable(t));
		btnTrack.setEnabled(t!=null && t.getStatut()==TransactionStatus.SENT);
		btnCancel.setEnabled(t!=null  && (t.getStatut()==TransactionStatus.PAID || t.getStatut()==TransactionStatus.CANCELATION_ASK || t.getStatut()==TransactionStatus.IN_PROGRESS || t.getStatut()==TransactionStatus.PAYMENT_WAITING || t.getStatut()==TransactionStatus.NEW) );
	}
	
	
	public TransactionManagementPanel() {
		setLayout(new BorderLayout(0, 0));
		loader = AbstractBuzyIndicatorComponent.createProgressComponent();
		btnAcceptTransaction = new JButton("Accept Transaction",MTGConstants.ICON_SMALL_CHECK);
		btnSend = new JButton("Mark as Sent",MTGConstants.ICON_TAB_DELIVERY);
		btnSave = new JButton("Save",MTGConstants.ICON_SMALL_SAVE);
		btnWooCommerce = new JButton("Send WooCommerce", new WooCommerceExport().getIcon());
		btnPaid = new JButton("Mark as Paid", MTGConstants.ICON_TAB_PRICES);
		btnTrack = new JButton("Track", MTGConstants.ICON_TAB_DELIVERY);
		btnCancel = new JButton("Cancel", MTGConstants.ICON_SMALL_CANCEL);
		
		
		btnSend.setEnabled(false);
		btnSave.setEnabled(false);
		btnAcceptTransaction.setEnabled(false);
		btnPaid.setEnabled(false);
		btnWooCommerce.setEnabled(false);
		btnTrack.setEnabled(false);
		btnCancel.setEnabled(false);
		
		var panelCenter = new JPanel();
		panelCenter.setLayout(new GridLayout(7,1));
		
		panelCenter.add(btnSave);
		panelCenter.add(btnAcceptTransaction);
		panelCenter.add(btnPaid);
		panelCenter.add(btnCancel);
		
		panelCenter.add(btnSend);
		panelCenter.add(btnTrack);
		panelCenter.add(btnWooCommerce);
		
		
		add(panelCenter, BorderLayout.NORTH);
		add(loader,BorderLayout.SOUTH);
			
		btnWooCommerce.addActionListener(e->{
			Map ret = ((WooCommerceExport)MTG.getPlugin("WooCommerce", MTGCardsExport.class)).sendOrder(t);
			logger.debug("Order created " + ret);			
		});
		
		
		btnSave.addActionListener(e->{
			try {
				TransactionService.saveTransaction(t,true);
			} catch (SQLException e1) {
				MTGControler.getInstance().notify(e1);
			}
		});
		
		btnPaid.addActionListener(e->{
			try {
				TransactionService.payingTransaction(t, "Paypal");
			} catch (SQLException e1) {
				MTGControler.getInstance().notify(e1);
			}
		});
		
		
		btnTrack.addActionListener(e->{
			try {
				
				var ret = MTG.getPlugin(t.getTransporter(), MTGTrackingService.class).track(t.getTransporterShippingCode());
				
				
				if(ret.isFinished())
				{
					t.setStatut(TransactionStatus.CLOSED);
					TransactionService.saveTransaction(t,false);
					MTGControler.getInstance().notify(new MTGNotification(TRACKING, "Delivery OK", MESSAGE_TYPE.INFO));
				}
				else
				{
					if(ret.last()!=null)
						MTGControler.getInstance().notify(new MTGNotification(TRACKING, ret.last().toString(), MESSAGE_TYPE.INFO));
				}
			} catch (Exception e1) {
				MTGControler.getInstance().notify(e1);
			}
		});
		
		
		
		
		btnSend.addActionListener(e->{
			var pane = new JPanel();
			
			JComboBox<MTGTrackingService> cboService = UITools.createCombobox(MTGTrackingService.class, false);
			var field = new JTextField(t.getTransporterShippingCode());
			var btnV = new JButton("OK");
			var btnC = new JButton("Cancel");
				
			pane.add(cboService);
			pane.add(new JLabel("Tracking #"));
			pane.add(field);
			pane.add(btnV);
			pane.add(btnC);
			var jd = MTGUIComponent.createJDialog(MTGUIComponent.build(pane,TRACKING,MTGConstants.ICON_TAB_DELIVERY),false,true);
			
			btnV.addActionListener(al->{
				if(cboService.getSelectedItem()!=null)
					t.setTransporter(cboService.getSelectedItem().toString());
				
				t.setTransporterShippingCode(field.getText());
				
				try {
					TransactionService.sendTransaction(t);
				} catch (SQLException e1) {
					MTGControler.getInstance().notify(e1);
				}
				
				jd.dispose();
			});
			
			btnC.addActionListener(al->jd.dispose());
			
			jd.setVisible(true);
				
		});
		
		btnCancel.addActionListener(e->{
			loader.start();
			var sw = new AbstractObservableWorker<Void, MagicCardStock, MTGDao>(loader,getEnabledPlugin(MTGDao.class),t.getItems().size()) 
			{
				@Override
				protected Void doInBackground() throws Exception {
					TransactionService.cancelTransaction(t);
					return null;
				}
				@Override
				protected void notifyEnd() {
					
					if(t.getStatut()!=TransactionStatus.CANCELED)
					{
						MTGControler.getInstance().notify(new MTGNotification("Error Update", getResult().toString(),MESSAGE_TYPE.WARNING));
					}
				}
				
				
				
			};
			ThreadManager.getInstance().runInEdt(sw, "update stock for transactions");
		});

		btnAcceptTransaction.addActionListener(e->{
			loader.start();
			var sw = new AbstractObservableWorker<List<MagicCardStock>, MagicCardStock, MTGDao>(loader,getEnabledPlugin(MTGDao.class),t.getItems().size()) 
			{
				@Override
				protected List<MagicCardStock> doInBackground() throws Exception {
					return TransactionService.validateTransaction(t);
				}
				@Override
				protected void notifyEnd() {
					
					if(t.getStatut()==TransactionStatus.IN_PROGRESS)
					{
						MTGControler.getInstance().notify(new MTGNotification("Error Update", getResult().toString(),MESSAGE_TYPE.WARNING));
					}
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

package org.magic.gui.components.dialog.importer;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.listEnabledPlugins;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.RetrievableTransaction;
import org.magic.api.interfaces.MTGShopper;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.AbstractDelegatedImporterDialog;
import org.magic.gui.components.shops.TransactionsPanel;
import org.magic.gui.models.ShoppingEntryTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;
public class TransactionsImporterDialog extends AbstractDelegatedImporterDialog<Transaction> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private JComboBox<MTGShopper> cboSniffers;
	private ShoppingEntryTableModel model;
	private AbstractBuzyIndicatorComponent lblLoad = AbstractBuzyIndicatorComponent.createLabelComponent();
	private transient MTGShopper selectedSniffer;
	private JPanel panelChoose;
	private TransactionsPanel transactionPanel;
	
	
	public TransactionsImporterDialog() {

		setSize(new Dimension(500, 300));
		setTitle(capitalize("SHOP"));
		setIconImage(MTGConstants.ICON_SHOP.getImage());


		var panelHaut = new JPanel();
		var btnLoad = new JButton(MTGConstants.ICON_OPEN);
		panelChoose = new JPanel();
		
		
		cboSniffers =UITools.createComboboxPlugins(MTGShopper.class,false);
		panelHaut.setLayout(new BorderLayout(0, 0));
		getContentPane().add(panelHaut, BorderLayout.NORTH);
		panelHaut.add(panelChoose, BorderLayout.WEST);
		panelChoose.add(cboSniffers);
		panelChoose.add(btnLoad);
		panelChoose.add(lblLoad);
		selectedSniffer = listEnabledPlugins(MTGShopper.class).get(0);
		cboSniffers.addActionListener(_ -> selectedSniffer = (MTGShopper) cboSniffers.getSelectedItem());
		btnLoad.addActionListener(_->{
			AbstractObservableWorker<List<RetrievableTransaction>, RetrievableTransaction, MTGShopper> sw = new AbstractObservableWorker<>(lblLoad,selectedSniffer) {
				@Override
				protected List<RetrievableTransaction> doInBackground() throws Exception {
					return plug.listOrders();
				}
				@Override
				protected void done() {
					super.done();
					model.init(getResult());
				}
			};
			ThreadManager.getInstance().runInEdt(sw, "loading orders");
		});
	}
	
	

	@Override
	public JComponent getSelectComponent() {
		var splitPane = new JSplitPane();
		transactionPanel = new TransactionsPanel();
		
		model = new ShoppingEntryTableModel();
		model.setWritable(false);
		var table = UITools.createNewTable(model,true);
		transactionPanel.getModel().setWritable(false);
		transactionPanel.disableCommands();

		
		table.getSelectionModel().addListSelectionListener(_->{
					
					List<RetrievableTransaction> rts = UITools.getTableSelections(table, 0);
					AbstractObservableWorker<List<Transaction>, Transaction, MTGShopper> sw = new AbstractObservableWorker<>(lblLoad,selectedSniffer) {
		
						@Override
						protected List<Transaction> doInBackground() throws Exception {
							var ret = new ArrayList<Transaction>();
							
							rts.stream().forEach(rt->{
								
								try {
									ret.add(plug.getTransaction(rt));
								} catch (IOException e) {
									logger.error(e);
								}
								
							});
							return ret;
							
						}
						
						@Override
						protected void done() {
							super.done();
							try {
								transactionPanel.init(get());
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
								logger.error(e);
							} catch (ExecutionException e) {
								logger.error(e);
							}
						}
						
						
					};
					ThreadManager.getInstance().runInEdt(sw, "retrieve transaction from " + selectedSniffer.getName());
				});
		
		
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setLeftComponent(new JScrollPane(table));
		splitPane.setRightComponent(transactionPanel);
		
		
		return splitPane;
		
	}
	

	public MTGShopper getSelectedSniffer() {
		return selectedSniffer;
	}

	
	@Override
	public List<Transaction> getSelectedItems() {
		return UITools.getTableSelections(transactionPanel.getTable(),0);
	}
	
	
	
}

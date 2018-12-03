package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.log4j.Logger;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.MTGDeckSniffer;
import org.magic.api.interfaces.MTGShopper;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.models.DeckSnifferTableModel;
import org.magic.gui.models.ShoppingEntryTableModel;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;
import org.magic.tools.UITools;

public class OrderImporterDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable table;
	private JComboBox<MTGShopper> cboSniffers;
	private ShoppingEntryTableModel model;
	private AbstractBuzyIndicatorComponent lblLoad = AbstractBuzyIndicatorComponent.createLabelComponent();
	private JButton btnImport;
	private transient MTGShopper selectedSniffer;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private JPanel panelChoose;
	private transient List<OrderEntry> selectedEntries;
	
	public OrderImporterDialog() {
		
		setSize(new Dimension(500, 300));
		setTitle(MTGControler.getInstance().getLangService().getCapitalize("FINANCIAL_MODULE"));
		setIconImage(MTGConstants.ICON_SHOP.getImage());
		setModal(true);
		getContentPane().setLayout(new BorderLayout(0, 0));

		table = new JTable();
		model = new ShoppingEntryTableModel();
		panelChoose = new JPanel();
		JPanel panel = new JPanel();
		JPanel panelButton = new JPanel();
		JButton btnClose = new JButton(MTGConstants.ICON_CANCEL);
		JButton btnLoad = new JButton(MTGConstants.ICON_OPEN);
		btnImport = new JButton(MTGConstants.ICON_CHECK);
		cboSniffers =UITools.createCombobox(MTGShopper.class,false);
		
		table.setModel(model);
		panel.setLayout(new BorderLayout(0, 0));
		
		
		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);		
		getContentPane().add(panel, BorderLayout.NORTH);
		
		panel.add(panelChoose, BorderLayout.WEST);
		panelChoose.add(cboSniffers);
		panelChoose.add(btnLoad);
		panelChoose.add(lblLoad);
		getContentPane().add(panelButton, BorderLayout.SOUTH);
		panelButton.add(btnClose);
		panelButton.add(btnImport);
		
		selectedSniffer = MTGControler.getInstance().listEnabled(MTGShopper.class).get(0);
		
		
				
		cboSniffers.addActionListener(e -> selectedSniffer = (MTGShopper) cboSniffers.getSelectedItem());
	
		btnLoad.addActionListener(ae->{
			ThreadManager.getInstance().execute(()->{
					try {
						lblLoad.start();
						model.init(selectedSniffer.listOrders());
						lblLoad.end();
					} catch (IOException e) {
						logger.error(e);
						lblLoad.end();
					}
			}, "loading orders");
			
			
			
		});
		
		
		btnClose.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("CANCEL"));
		
		btnClose.addActionListener(e -> dispose());
	
		btnImport.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("IMPORT"));
		btnImport.addActionListener(e -> ThreadManager.getInstance().execute(() -> {
			try {
				btnImport.setEnabled(false);
				selectedEntries = UITools.getTableSelection(table, 0);
				btnImport.setEnabled(true);
				dispose();
			} catch (Exception e1) {
				logger.error("Error snif",e1);
				MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),e1));
				btnImport.setEnabled(true);
			}
			finally
			{
				selectedSniffer.removeObserver(lblLoad);
			}
		}, "Import Orders"));

		
		setLocationRelativeTo(null);
	}

	public List<OrderEntry> getSelectedEntries() {
		return selectedEntries;
	}

}

package org.magic.gui;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.OrderEntry.TYPE_TRANSACTION;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.dialog.OrderImporterDialog;
import org.magic.gui.models.ShoppingEntryTableModel;
import org.magic.gui.renderer.MagicEditionJLabelRenderer;
import org.magic.gui.renderer.MagicEditionsComboBoxCellEditor;
import org.magic.gui.renderer.OrderEntryRenderer;
import org.magic.services.FinancialBookService;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.tools.UITools;

import com.google.gson.reflect.TypeToken;
import org.magic.gui.components.OrderEntryPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class BalanceGUI extends MTGUIComponent {
	
	private static final long serialVersionUID = 1L;
	private ShoppingEntryTableModel model;
	private JLabel totalBuy;
	private JLabel totalSell;
	private JLabel total;
	
	
	private JLabel selectionBuy;
	private JLabel selectionSell;
	private JLabel totalSelection;
	
	
	
	private JXTable table;
	private transient FinancialBookService serv;
	private OrderEntryPanel orderEntryPanel;
	
	
	
	private void calulate(List<OrderEntry> entries)
	{
		double totalS=0;
		double totalB=0;
		for(OrderEntry e : entries)
		{
			if(e.getTypeTransaction().equals(TYPE_TRANSACTION.BUY))
				totalB=totalB+e.getItemPrice();
			else
				totalS=totalS+e.getItemPrice();
		}
	
		if(entries.size()<model.getRowCount())
		{
			selectionBuy.setText(UITools.formatDouble(totalB));
			selectionSell.setText(UITools.formatDouble(totalS));
			totalSelection.setText(": "+UITools.formatDouble(totalS-totalB)+")");
			if((totalS-totalB)>0)
				totalSelection.setIcon(MTGConstants.ICON_UP);
			else
				totalSelection.setIcon(MTGConstants.ICON_DOWN);
			
		}
		else
		{
			totalBuy.setText(UITools.formatDouble(totalB));
			totalSell.setText(UITools.formatDouble(totalS));
			total.setText(UITools.formatDouble(totalS-totalB));
			
			if((totalS-totalB)>0)
				total.setIcon(MTGConstants.ICON_UP);
			else
				total.setIcon(MTGConstants.ICON_DOWN);
		}
	}
	
	private void loadFinancialBook()
	{
			List<OrderEntry> l = serv.loadFinancialBook();
			model.addItems(l);
			calulate(l);
			table.packAll();
	}
	
	
	public BalanceGUI() {
		
		serv=new FinancialBookService();
		
		JPanel panneauBas = new JPanel();
		JPanel panneauHaut = new JPanel();
		JPanel panneauRight = new JPanel();
		table = new JXTable();
		model = new ShoppingEntryTableModel();
		JButton btnImportTransaction = new JButton(MTGConstants.ICON_IMPORT);
		JButton btnSave = new JButton(MTGConstants.ICON_SAVE);
		
		totalBuy = new JLabel(MTGConstants.ICON_DOWN);
		totalSell = new JLabel(MTGConstants.ICON_UP);
		total = new JLabel();
		
		totalSelection = new JLabel();
		selectionSell = new JLabel(MTGConstants.ICON_UP);
		selectionBuy=new JLabel(MTGConstants.ICON_DOWN);
		
		
		panneauBas.add(totalBuy);
		panneauBas.add(totalSell);
		panneauBas.add(total);
		panneauBas.add(new JLabel(" ("));
		panneauBas.add(selectionBuy);
		panneauBas.add(selectionSell);
		panneauBas.add(totalSelection);
		
		
		
		UITools.initTableFilter(table);
		model.setWritable(true);
		setLayout(new BorderLayout(0, 0));
		table.setModel(model);
		OrderEntryRenderer render = new OrderEntryRenderer();
		table.setDefaultRenderer(MagicEdition.class, new MagicEditionJLabelRenderer());
		table.setDefaultRenderer(Double.class, render);
		panneauHaut.add(btnImportTransaction);
		panneauHaut.add(btnSave);
		add(panneauHaut, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);
		add(panneauRight,BorderLayout.EAST);
		GridBagLayout gblpanneauRight = new GridBagLayout();
		gblpanneauRight.columnWidths = new int[]{105, 0};
		gblpanneauRight.rowHeights = new int[]{18, 0, 0};
		gblpanneauRight.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gblpanneauRight.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panneauRight.setLayout(gblpanneauRight);
		
		orderEntryPanel = new OrderEntryPanel();
		panneauRight.add(orderEntryPanel, UITools.createGridBagConstraints(GridBagConstraints.WEST, GridBagConstraints.VERTICAL, 0, 0));
		
		JPanel panelButton = new JPanel();
		panneauRight.add(panelButton, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 0, 1));
		
		JButton btnSaveOrder = new JButton(MTGConstants.ICON_SAVE);

		btnSaveOrder.addActionListener(ae->{
			orderEntryPanel.save();
			model.fireTableDataChanged();
		});
		panelButton.add(btnSaveOrder);
		JButton btnNewEntry = new JButton(MTGConstants.ICON_NEW);
		panelButton.add(btnNewEntry);
		btnNewEntry.addActionListener(ae->model.addItem(orderEntryPanel.newOrderEntry()));
		add(panneauBas,BorderLayout.SOUTH);
		
		
		loadFinancialBook();
		
		table.getSelectionModel().addListSelectionListener(event -> {
			if (!event.getValueIsAdjusting()) {
				try {
				OrderEntry o = (OrderEntry) UITools.getTableSelection(table, 0).get(0);
				orderEntryPanel.setOrderEntry(o);
				
				calulate(UITools.getTableSelection(table, 0));
				}
				catch(Exception e)
				{
					//do nothing
				}
			}
		});
		
		btnSave.addActionListener(ae->serv.saveBook(model.getItems()));
		
		btnImportTransaction.addActionListener(ae->{
			OrderImporterDialog diag = new OrderImporterDialog();
			diag.setVisible(true);
			model.addItems(diag.getSelectedEntries());
			calulate(model.getItems());
		});
		
	}

	public static void main(String[] args) {
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		MTGUIComponent.createJDialog(new BalanceGUI(),true,false).setVisible(true);
	}

	@Override
	public String getTitle() {
		return MTGControler.getInstance().getLangService().getCapitalize("FINANCIAL_MODULE");
	}
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_SHOP;
	}
	
	

}

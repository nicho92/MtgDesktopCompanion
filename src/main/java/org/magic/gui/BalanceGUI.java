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

import org.apache.commons.io.FileUtils;
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
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.UITools;

import com.google.gson.reflect.TypeToken;

public class BalanceGUI extends MTGUIComponent {
	
	private static final long serialVersionUID = 1L;
	private ShoppingEntryTableModel model;
	private File tamponFile = Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(), "financialBook.json").toFile();
	private JLabel lblInformation;
	
	
	private void calulate(List<OrderEntry> entries)
	{
		double totalSell=0;
		double totalBuy=0;
		
		for(OrderEntry e : entries)
		{
			if(e.getTypeTransaction().equals(TYPE_TRANSACTION.BUY))
				totalBuy=totalBuy+e.getItemPrice();
			else
				totalSell=totalSell+e.getItemPrice();
		}
		
		lblInformation.setText("-"+UITools.formatDouble(totalBuy) +" / +"+UITools.formatDouble(totalSell)+" Total: "+UITools.formatDouble(totalSell-totalBuy));
	}
	
	private void loadFinancialBook()
	{
		if(tamponFile.exists())
		{
			
			try {
				List<OrderEntry> l = new JsonExport().fromJsonList(FileUtils.readFileToString(tamponFile,MTGConstants.DEFAULT_ENCODING),OrderEntry.class);
				model.addItems(l);
				calulate(l);
			} catch (IOException e) {
				logger.error("error loading " + tamponFile,e);
			}
			
			
			
		}
		
	}
	
	
	public BalanceGUI() {
		JPanel panneauBas = new JPanel();
		JPanel panneauHaut = new JPanel();
		JPanel panneauRight = new JPanel();
		JXTable table = new JXTable();
		lblInformation= new JLabel();
		model = new ShoppingEntryTableModel();
		JButton btnNewEntry = new JButton(MTGConstants.ICON_NEW);
		JButton btnImportTransaction = new JButton(MTGConstants.ICON_IMPORT);
		JButton btnSave = new JButton(MTGConstants.ICON_SAVE);
		
		UITools.initTableFilter(table);
		model.setWritable(true);
		setLayout(new BorderLayout(0, 0));
		table.setModel(model);
		OrderEntryRenderer render = new OrderEntryRenderer();
		table.setDefaultRenderer(MagicEdition.class, new MagicEditionJLabelRenderer());
		table.setDefaultRenderer(Double.class, render);
		panneauHaut.add(btnNewEntry);
		panneauHaut.add(btnImportTransaction);
		panneauHaut.add(btnSave);
		add(panneauHaut, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);
		add(panneauRight,BorderLayout.EAST);
		add(panneauBas,BorderLayout.SOUTH);
		panneauBas.add(lblInformation);
		
		
		
		
		
		
		loadFinancialBook();
		
		
		
		table.getSelectionModel().addListSelectionListener(event -> {

			if (!event.getValueIsAdjusting()) {
				calulate(UITools.getTableSelection(table, 0));
			}
		});

		
	
		
		btnSave.addActionListener(ae->{
			
			try {
				FileUtils.write(tamponFile, new JsonExport().toJson(model.getItems()),MTGConstants.DEFAULT_ENCODING.displayName());
			} catch (IOException e) {
				logger.error("error while saving in " + tamponFile,e);
			}
			
		});
		
		
		btnNewEntry.addActionListener(ae-> model.addItem(new OrderEntry()));
		
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

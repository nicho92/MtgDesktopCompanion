package org.magic.gui;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.io.FileUtils;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.OrderEntry;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.shopping.impl.MagicCardmarketShopper;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.dialog.OrderImporterDialog;
import org.magic.gui.models.ShoppingEntryTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.UITools;

import com.google.gson.JsonSyntaxException;
import com.itextpdf.text.List;

public class BalanceGUI extends MTGUIComponent {
	
	private static final long serialVersionUID = 1L;
	private ShoppingEntryTableModel model;
	
	private File tamponFile = Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(), "financialBook.json").toFile();
	
	
	
	public BalanceGUI() {
		
		JPanel panneauHaut = new JPanel();
		JPanel panneauRight = new JPanel();
		JXTable table = new JXTable();
		model = new ShoppingEntryTableModel();
		JButton btnNewEntry = new JButton(MTGConstants.ICON_NEW);
		JButton btnImportTransaction = new JButton(MTGConstants.ICON_IMPORT);
		JButton btnSave = new JButton(MTGConstants.ICON_SAVE);
		
		UITools.initTableFilter(table);
		
		setLayout(new BorderLayout(0, 0));
		table.setModel(model);
		
		panneauHaut.add(btnNewEntry);
		panneauHaut.add(btnImportTransaction);
		panneauHaut.add(btnSave);
		add(panneauHaut, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);
		add(panneauRight,BorderLayout.EAST);
		
		
	
		
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

package org.magic.gui.components.shops;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.ConverterItem;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.ConverterItemsTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.providers.StockItemConversionManager;
import org.magic.tools.UITools;

public class ConverterPanel extends MTGUIComponent{
	
	private static final long serialVersionUID = 1L;
	private ConverterItemsTableModel model;
	private JXTable table;
	
	
	public ConverterPanel() {
		setLayout(new BorderLayout(0, 0));
		
		var panel = new JPanel();
		var btnReload = UITools.createBindableJButton("", MTGConstants.ICON_REFRESH, KeyEvent.VK_R, "Reload");
		var btnSave = UITools.createBindableJButton("", MTGConstants.ICON_SAVE, KeyEvent.VK_S, "Save");
		var btnAdd = UITools.createBindableJButton("", MTGConstants.ICON_NEW, KeyEvent.VK_N, "New");
		var btnDelete = UITools.createBindableJButton("", MTGConstants.ICON_DELETE, KeyEvent.VK_D, "Delete");
		  
		
		model = new ConverterItemsTableModel();
		table = UITools.createNewTable(model);
		
		model.init(StockItemConversionManager.inst().getConversionsItems());
		
		UITools.initTableFilter(table);
		table.packAll();
		
		
		panel.add(btnReload);
		panel.add(btnAdd);
		panel.add(btnDelete);
		panel.add(btnSave);
		
		add(panel, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);
		
		btnReload.addActionListener(el->model.init(StockItemConversionManager.inst().getConversionsItems()));
		btnDelete.addActionListener(el->model.removeRows(UITools.getSelectedRows(table)));
		btnAdd.addActionListener(el->model.addItem(new ConverterItem()));
	
		btnSave.addActionListener(el->{
			try {
				StockItemConversionManager.inst().resetFile(model.getItems());
			} catch (IOException e) {
				MTGControler.getInstance().notify(e);
			}
		});
		
	}
	

	@Override
	public String getTitle() {
		return "Conversions";
	}

}

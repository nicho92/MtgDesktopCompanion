package org.magic.gui.components.shops;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.ConverterItem;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.ConverterItemsTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.MTG;
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
		
		try {
			model.init(MTG.getEnabledPlugin(MTGDao.class).listConversionItems());
		} catch (SQLException e1) {
			MTGControler.getInstance().notify(e1);
		}
		
		UITools.initTableFilter(table);
		table.packAll();
		
		
		panel.add(btnReload);
		panel.add(btnAdd);
		panel.add(btnDelete);
		panel.add(btnSave);
		
		add(panel, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);
		
		btnReload.addActionListener(el->{
			try {
				model.init(MTG.getEnabledPlugin(MTGDao.class).listConversionItems());
			} catch (SQLException e1) {
				MTGControler.getInstance().notify(e1);
			}
		});
		btnDelete.addActionListener(el->model.removeRows(UITools.getSelectedRows(table)));
		btnAdd.addActionListener(el->{
			model.addItem(new ConverterItem());	
		});
	
		btnSave.addActionListener(el->{
				for(ConverterItem it : model.getItems())
				{
					try {
						MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateConversionItem(it);
					} catch (SQLException e) {
						MTGControler.getInstance().notify(e);
					}
				}
		});
		
	}
	

	@Override
	public String getTitle() {
		return "Conversions";
	}

}

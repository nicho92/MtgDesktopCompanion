package org.magic.gui.components;

import static org.magic.tools.MTG.getPlugin;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.exports.impl.WooCommerceExport;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.gui.models.conf.MapTableModel;
import org.magic.gui.renderer.standard.ComboBoxEditor;
import org.magic.services.MTGConstants;
import org.magic.services.PluginRegistry;
import org.magic.tools.UITools;
public class StockItemsSynchronizationPanel extends JPanel {
	 
	private static final long serialVersionUID = 1L;
	private JXTable table;
	private MapTableModel<MTGPlugin, Object> model;
	private MagicCardStock st;
	
	public StockItemsSynchronizationPanel() {
		setLayout(new BorderLayout(0, 0));
		model = new MapTableModel<>();
		table = UITools.createNewTable(model);
		
		
		add(new JScrollPane(table));
		model.setWritable(true);
		var panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		
		var btnDelete = UITools.createBindableJButton(null, MTGConstants.ICON_DELETE, KeyEvent.VK_DELETE, "Delete Sync");
		var btnAdd = UITools.createBindableJButton(null, MTGConstants.ICON_NEW, KeyEvent.VK_N, "Add Sync");
		
		panel.add(btnDelete);
		panel.add(btnAdd);
		table.getColumn(0).setCellEditor(new ComboBoxEditor<>(PluginRegistry.inst().listEnabledPlugins(MTGCardsExport.class)));
		
		btnDelete.addActionListener(al->{
			
			MTGPlugin name = UITools.getTableSelection(table,0);
			
			if(name!=null) {
				st.getTiersAppIds().remove(name.getName());
				st.setUpdate(true);
				init(st);
			}
		}); 
		
		btnAdd.addActionListener(al->{
			model.addRow(new WooCommerceExport(), "123");
			model.fireTableDataChanged();
		});
		
		
		model.setColumnNames("Plugin", "id");
		
		
	}

	//@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_SYNC;
	}
	
	
	//@Override
	public String getTitle() {
		return "Synchronization";
	}

	public void init(MagicCardStock st)
	{
		this.st=st;
		model.clear();
		for(Entry<String, String> m : st.getTiersAppIds().entrySet())
			model.addRow(getPlugin(m.getKey(),MTGCardsExport.class),m.getValue());
		
		model.fireTableDataChanged();
		
		
	}
	
	
}

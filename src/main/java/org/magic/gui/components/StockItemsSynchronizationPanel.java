package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.magic.api.beans.MagicCardStock;
import org.magic.gui.models.conf.MapTableModel;
import org.magic.services.MTGConstants;
import org.magic.tools.UITools;

public class StockItemsSynchronizationPanel extends JPanel {
	 
	private static final long serialVersionUID = 1L;
	private JTable table;
	private MapTableModel<String, Object> model;
	
	public StockItemsSynchronizationPanel() {
		setLayout(new BorderLayout(0, 0));
		model = new MapTableModel<>();
		table = new JTable(model);
		add(new JScrollPane(table));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		
		JButton btnDelete = UITools.createBindableJButton(null, MTGConstants.ICON_DELETE, KeyEvent.VK_DELETE, "Delete Sync");
		panel.add(btnDelete);
		
		
		btnDelete.addActionListener(e->{
			
			
		});
		
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
		model.clear();
		for(Entry<String, Object> m : st.getTiersAppIds().entrySet())
			model.addRow(m.getKey(),m.getValue());
	}
	
	
}

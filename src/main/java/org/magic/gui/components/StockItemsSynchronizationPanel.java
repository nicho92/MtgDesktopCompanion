package org.magic.gui.components;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.magic.api.beans.MagicCardStock;
import org.magic.gui.models.conf.MapTableModel;

import java.awt.BorderLayout;
import java.util.Map.Entry;

public class StockItemsSynchronizationPanel extends JPanel {
	 
	private static final long serialVersionUID = 1L;
	private JTable table;
	private MapTableModel<String, Object> model;
	
	public StockItemsSynchronizationPanel() {
		setLayout(new BorderLayout(0, 0));
		model = new MapTableModel<>();
		table = new JTable(model);
		add(new JScrollPane(table));
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

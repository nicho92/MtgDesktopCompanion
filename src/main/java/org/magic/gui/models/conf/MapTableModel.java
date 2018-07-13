package org.magic.gui.models.conf;

import java.util.Map;

import javax.swing.table.DefaultTableModel;

public class MapTableModel<K,V> extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	private transient Map<K,V> map;
	
	public MapTableModel() {
	}
	
	public MapTableModel(Map<K,V> map2)
	{
		init(map2);
	}
	
	
	public void init(Map<K, V> map)
	{
		this.map=map;
		fireTableDataChanged();
		fireTableStructureChanged();
	}
	
	@Override
	public int getRowCount() {
		if(map==null)
			return 0;
		
		return map.size();
	}
	
	
	@Override
	public int getColumnCount() {
		return 2;
	}
	
	@Override
	public String getColumnName(int column) {
		if(column==0)
			return "ID";
		else
			return "Value";
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		if(column==0)
			return map.keySet().toArray()[row];
		else return map.get(map.keySet().toArray()[row]);
			
	}
	
	
	
}

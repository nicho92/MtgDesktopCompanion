package org.magic.gui.models.conf;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.table.DefaultTableModel;

import org.magic.services.MTGControler;

public class MapTableModel<K,V> extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	protected transient List<Entry<K, V>> keys;
	
	protected String[] columnsName =new String[] {"ID","VALUE"};
	
	public MapTableModel() {
		this.keys = new ArrayList<>();
	}
	
	public List<Entry<K, V>> getValues()
	{
		return keys;
	}
	
	public MapTableModel(Map<K,V> map2)
	{
		init(map2);
	}
	
	public void setColumnNameAt(int index,String name)
	{
		columnsName[index]=name;
	}
	
	public void setColumnNames(String c1,String c2)
	{
		columnsName[0]=c1;
		columnsName[1]=c2;
		fireTableStructureChanged();
	}
	
	public void init(Map<K, V> map)
	{
		this.keys = new ArrayList<>(map.entrySet());
		fireTableDataChanged();
	}
	
	public void addRow(K key, V value)
	{
		
		keys.add(new AbstractMap.SimpleEntry<>(key,value));
		fireTableDataChanged();
			
	}
	

	public void clear() {
		keys.clear();
		fireTableDataChanged();
		
	}
	
	
	
	@Override
	public int getRowCount() {
		if(keys==null)
			return 0;
		
		return keys.size();
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(keys==null || keys.isEmpty())
			return super.getColumnClass(columnIndex);

		if(columnIndex==0)
		{
			return keys.get(0).getKey().getClass();
		}
		else
		{
			if(keys.get(0).getValue()!=null)
					return keys.get(0).getValue().getClass();
				else
					return super.getColumnClass(columnIndex);	
		}
	}
	
	
	@Override
	public int getColumnCount() {
		return 2;
	}
	
	@Override
	public String getColumnName(int column) {
		return MTGControler.getInstance().getLangService().getCapitalize(columnsName[column]);
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		if(column==0)
			return keys.get(row).getKey();
		else
			return keys.get(row).getValue();
	}

	public boolean removeRow(K ed) {
		
		Entry<K,V> removed =null;
		for(Entry<K, V> r : keys)
		{
			if(r.getKey()==ed)
			{
				removed=r;
				break;
			}
		}
		
		if(removed!=null)
		{
			Boolean b = keys.remove(removed);
			fireTableDataChanged();
			return b;
		}
		return false;
		
	}

	public void updateRow(K k, V v) {
		getValues().forEach(entry->{
			if(entry.getKey()==k)
			{	
				entry.setValue(v);
				fireTableDataChanged();
				return;
			}
		});
		
	}

	
	
}

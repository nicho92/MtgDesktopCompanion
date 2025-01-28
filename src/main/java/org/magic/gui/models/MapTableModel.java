package org.magic.gui.models;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.magic.gui.abstracts.GenericTableModel;
public class MapTableModel<K,V> extends GenericTableModel<Entry<K, V>> {

	private static final long serialVersionUID = 1L;

	public MapTableModel() {
		setColumns("ID","VALUE");
		setWritable(false);
	}

	public void init(Set<Entry<K, V>> entrySet) {
		this.items = new ArrayList<>(entrySet);
		fireTableDataChanged();
	}


	public void init(Map<K, V> map)
	{
		this.items = new ArrayList<>(map.entrySet());
		fireTableDataChanged();
	}


	public void addRow(K key, V value)
	{
		items.add(new AbstractMap.SimpleEntry<>(key,value));
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(items==null || items.isEmpty())
			return super.getColumnClass(columnIndex);

		if(columnIndex==0)
		{
			return items.get(0).getKey().getClass();
		}
		else
		{
			if(items.get(0).getValue()!=null)
					return items.get(0).getValue().getClass();
				else
					return super.getColumnClass(columnIndex);
		}
	}

	@Override
	public Object getValueAt(int row, int column) {
		if(column==0)
			return items.get(row).getKey();
		else
			return items.get(row).getValue();
	}

	public boolean removeRow(K ed) {

		Entry<K,V> removed =null;
		for(Entry<K, V> r : items)
		{
			if(r.getKey()==ed)
			{
				removed=r;
				break;
			}
		}

		if(removed!=null)
		{
			Boolean b = items.remove(removed);
			fireTableDataChanged();
			return b;
		}
		return false;

	}



	@Override
	public void setValueAt(Object aValue, int row, int column) {
		if(column==1)
			items.get(row).setValue((V) aValue);
	}


	public void updateRow(K k, V v) {
		getItems().forEach(entry->{
			if(entry.getKey()==k)
			{
				entry.setValue(v);
				fireTableDataChanged();
				return;
			}
		});

	}



}

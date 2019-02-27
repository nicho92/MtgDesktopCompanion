package org.magic.gui.abstracts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class GenericTableModel<T> extends DefaultTableModel {

	private static final long serialVersionUID = 1L;
	protected transient List<T> items;
	protected String[] columns;
	protected transient Logger logger = MTGLogger.getLogger(this.getClass());
	protected boolean writable=false;
	
	public GenericTableModel() {
		items = new ArrayList<>();
		columns = new String[]{"VALUE"};
	}
	
	public GenericTableModel(String...columnName) {
		items = new ArrayList<>();
		columns = columnName;
	}
	
	
	public void setWritable(boolean writable) {
		this.writable = writable;
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		T it = items.get(row);
		try {
			return BeanUtils.getProperty(it, columns[column]);
		} catch (Exception e) {
			logger.error("error",e);
		} 
		
		return it; 
		
		
	}
	
	public void setColumns(String... columns)
	{
		this.columns=columns;
		fireTableStructureChanged();
	}
	
	public void addItem(T t)
	{
		items.add(t);
		fireTableDataChanged();
	}
	
	public void init(List<T> t)
	{
		if(t==null)
			items=new ArrayList<>();
		else
			items=new ArrayList<>(t);
		
		
		fireTableDataChanged();
	}
	
	public void init(T[] t)
	{
		init(Arrays.asList(t));
	}
	
	
	public void addItems(List<T> t)
	{
		
		if(t!=null)
			t.stream().forEach(c->items.add(c));
		
		fireTableDataChanged();
	}
	
	public void removeItem(List<T> list)
	{
		for(T it : list)
			items.remove(it);

		fireTableDataChanged();
	}
	
	public void removeItem(T t)
	{
		items.remove(t);
		fireTableDataChanged();
	}
	
	@Override
	public int getRowCount() {
		if (items != null)
			return items.size();
		else
			return 0;
	}
	
	@Override
	public String getColumnName(int column) {
		return MTGControler.getInstance().getLangService().getCapitalize(columns[column]);
	}
	
	@Override
	public int getColumnCount() {
		if(columns==null)
			return 0;
		return columns.length;
	}
	

	public void clear() {
		items.clear();
		fireTableDataChanged();
	}
	
	public T getItemAt(int row)	{
		return items.get(row);
	}
	
	
	public List<T> getItems() {
		return items;
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return writable;
	}
	
}

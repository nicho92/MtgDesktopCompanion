package org.magic.gui.abstracts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.table.DefaultTableModel;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class GenericTableModel<T> extends DefaultTableModel {

	private static final long serialVersionUID = 1L;
	protected transient List<T> items;
	protected String[] columns;
	protected transient Logger logger = MTGLogger.getLogger(this.getClass());
	protected boolean writable=false;
	protected boolean changed=false;
	
	
	public GenericTableModel() {
		items = new ArrayList<>();
		columns = new String[]{"VALUE"};
	}
	
	public GenericTableModel(String...columnName) {
		items = new ArrayList<>();
		columns = columnName;
		changed=false;
	}
	
	public GenericTableModel(T classe) {
		items = new ArrayList<>();
		Set<String> s;
		changed=false;
		try {
			s = BeanUtils.describe(classe).keySet();
			columns = Arrays.copyOf(s.toArray(), s.size(),String[].class);
		} catch (Exception e) {
			logger.error("error calculate columns for " + classe,e);
		}
	}
	
	public boolean isChanged() {
		return changed;
	}
	
	
	public void setWritable(boolean writable) {
		this.writable = writable;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
	
		try {
			T it = items.get(0);
			return PropertyUtils.getProperty(it, columns[columnIndex]).getClass();
		} catch (Exception e) {
			return super.getColumnClass(columnIndex);	
		} 
		
	}
	
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		changed=true;
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		T it = items.get(row);
		try {
			return PropertyUtils.getProperty(it, columns[column]);
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
		changed=true;
		fireTableDataChanged();
	}
	
	public void init(List<T> t)
	{
		if(t==null)
			items=new ArrayList<>();
		else
			items=new ArrayList<>(t);
		
		changed=false;
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
	
	public boolean isEmpty()
	{
		if(items!=null)
			return items.isEmpty();
		
		return true;
	}
	
	public void removeItem(List<T> list)
	{
		for(T it : list)
			items.remove(it);

		changed=true;
		fireTableDataChanged();
	}
	
	public void removeItem(T t)
	{
		items.remove(t);
		changed=true;
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
		changed=true;
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

package org.magic.gui.abstracts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

import com.google.common.collect.Lists;

public abstract class GenericTableModel<T> extends DefaultTableModel {

	private static final long serialVersionUID = 1L;
	protected transient List<T> items;
	protected String[] columns;
	protected transient Logger logger = MTGLogger.getLogger(this.getClass());
	
	
	public GenericTableModel() {
		items = new ArrayList<>();
		columns = new String[]{"VALUE"};
	}
	
	public void setColumnName(String[] columnName) {
		this.columns = columnName;
		fireTableStructureChanged();
	}
	
	public void setColumns(String[] columns)
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
		items=t;
		fireTableDataChanged();
	}
	
	public void init(T[] t)
	{
		init(Arrays.asList(t));
	}
	
	
	public void addItems(List<T> t)
	{
		items.addAll(t);
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
		return columns.length;
	}
	

	public void clear() {
		items.clear();
		fireTableDataChanged();
	}
	
	public List<T> getItems() {
		return items;
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
}

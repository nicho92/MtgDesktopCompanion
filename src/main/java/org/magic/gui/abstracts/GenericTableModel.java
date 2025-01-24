package org.magic.gui.abstracts;

import static org.magic.services.tools.MTG.capitalize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Logger;
import org.magic.services.logging.MTGLogger;



public class GenericTableModel<T> extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	protected transient List<T> items;
	private String[] columns;
	protected transient Logger logger = MTGLogger.getLogger(this.getClass());
	protected boolean writable=false;
	protected boolean changed=false;
	private int[] hiddenColumns = new int[0];


	public GenericTableModel() {
		items = new ArrayList<>();
		columns = new String[]{"VALUE"};
	}

	public GenericTableModel(String...columnName) {
		items = new ArrayList<>();
		columns = columnName;
		changed=false;
	}

	public void setDefaultHiddenComlumns(int... nums)
	{
		this.hiddenColumns =nums;
	}

	public void addHiddenColumns(int i) {
		hiddenColumns= ArrayUtils.add(hiddenColumns,i);
	}


	public int[] defaultHiddenColumns()
	{
		return hiddenColumns;
	}

	public GenericTableModel(T classe) {
		items = new ArrayList<>();
		Set<String> s;
		changed=false;
		try {
			s = BeanUtils.describe(classe).keySet();
			setColumns(Arrays.copyOf(s.toArray(), s.size(),String[].class));
		} catch (Exception e) {
			logger.error("error calculate columns for {} : {}",classe,e);
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
			var it = items.get(0);
			return PropertyUtils.getNestedProperty(it, columns[columnIndex]).getClass();
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
		var it = items.get(row);
		try {
			return PropertyUtils.getNestedProperty(it, columns[column]);
		} catch (Exception e) {
			logger.error(e);
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
		fireTableRowsInserted(getRowCount()-1,getRowCount()-1);
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
			t.stream().forEach(items::add);

		fireTableDataChanged();
	}

	public void bind(List<T> items)
	{
		this.items=items;
		changed=false;
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

	public void removeRow(int row)
	{
		items.remove(row);
	    fireTableRowsDeleted(row, row);
	}


	public void removeRows(List<Integer> selectedRows) {
		for(Integer i : selectedRows)
				removeRow(i);
	}



	@Override
	public int getRowCount() {
		if (items != null)
			return items.size();
		else
			return 0;
	}

	public String getColumn(int ind) {
		return columns[ind];
	}
	
	
	@Override
	public String getColumnName(int column) {
		return capitalize(getColumn(column));
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

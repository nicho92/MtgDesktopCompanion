package org.magic.test;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.commons.beanutils.BeanUtils;

public class WantsTableModel extends DefaultTableModel{

	private final static String[] COLUMN= {"product","expension","names","wishPrice","minCondition","foil","signed","playset","altered"};
	private List<Want> wants;
	
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	
	
	public void init(List<Want> wants)
	{
		this.wants = wants;
		fireTableDataChanged();
	}
	
	
	@Override
	public int getRowCount() {
		if(wants!=null)
			return wants.size();
		
		return 0;
	}
	
	@Override
	public int getColumnCount() {
		return COLUMN.length;
	}
	
	@Override
	public String getColumnName(int column) {
		return COLUMN[column];
	}
	
	
	@Override
	public Object getValueAt(int row, int column) {
		Want w = wants.get(row);
		
		try {
			if(column==1||column==2)	
				return BeanUtils.getProperty(w.getProduct(), COLUMN[column]);
			else
				return BeanUtils.getProperty(w, COLUMN[column]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
		
		
	}
}

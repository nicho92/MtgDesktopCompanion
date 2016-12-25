package org.magic.gui.models;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;

public class MagicCardNamesTableModel extends DefaultTableModel {

	
	MagicCard mc;
	final static String COLUMN[] = new String[]{"Language","Name","Gatherer ID"};
	
	public void init(MagicCard mc)
	{
		this.mc = mc;
		fireTableDataChanged();
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return true;
	}
	
	@Override
	public int getColumnCount() {
		return 3;
	}
	
	@Override
	public int getRowCount() {
		if(mc!=null)
			return mc.getForeignNames().size();
		
		return 0;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch(columnIndex)
		{
			case 0: return String.class;
			case 1: return String.class;
			case 2: return Integer.class;
			default : return Object.class;
		}
	}
	
	
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		switch(column)
		{
			case 0: mc.getForeignNames().get(row).setLanguage(String.valueOf(aValue));break;
			case 1: mc.getForeignNames().get(row).setName(String.valueOf(aValue));break;
			case 2: mc.getForeignNames().get(row).setGathererId(Integer.parseInt(aValue.toString()));break;
			
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return COLUMN[column];
	}
	@Override
	public Object getValueAt(int row, int column) {
		switch(column)
		{
			case 0: return mc.getForeignNames().get(row).getLanguage();
			case 1: return mc.getForeignNames().get(row).getName();
			case 2: return mc.getForeignNames().get(row).getGathererId();
			default : return "";
		}
		
		
	}
	
}

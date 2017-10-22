package org.magic.gui.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.Booster;
import org.magic.api.beans.MagicCard;

public class BoostersTableModel extends DefaultTableModel
{
	
	List<Booster> boosters;
	private static final String[] COLUMNS = {"Number","Cards","Price"};
	
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0: return String.class;
		case 1: return List.class;
		case 2: return Double.class;
		default : return super.getColumnClass(columnIndex);
		}
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		
		switch (column) {
		case 0: return boosters.get(row).getBoosterNumber();
		case 1: return boosters.get(row).getCards();
		case 2: return boosters.get(row).getPrice();
		default : return "";
		}
	}
	
	public void clear() {
		boosters.clear();
		
	}

	@Override
	public String getColumnName(int column) {
		return COLUMNS[column];
	}
	
	public void addLine(Booster bl)
	{
		boosters.add(bl);
		fireTableDataChanged();
	}
	
	public BoostersTableModel() {
		boosters=new ArrayList<Booster>();
	}
	
	public void init(List<Booster> lines)
	{
		this.boosters=lines;
		fireTableDataChanged();
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}
	
	@Override
	public int getRowCount() {
		if(boosters==null)
			return 0;
		else
			return boosters.size();
	}
	
	
	
}
package org.magic.gui.models;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.api.mkm.modele.InsightElement;

public class MkmInsightModel extends DefaultTableModel {

	private String[] columnName;
	private List<InsightElement> elements;
	
	
	public void setColumnName(String[] columnName) {
		this.columnName = columnName;
		fireTableStructureChanged();
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	@Override
	public int getColumnCount() {
		if(columnName!=null)
			return columnName.length;
		return 0;
	}
	
	@Override
	public String getColumnName(int column) {
		if(columnName!=null)
			return columnName[column];
		return "";
	}
	
	@Override
	public int getRowCount() {
		if(elements!=null)
			return elements.size();
		
		return 0;
	} 
	
	@Override
	public Object getValueAt(int row, int column) {
		return elements.get(row);
	}
	
	
}

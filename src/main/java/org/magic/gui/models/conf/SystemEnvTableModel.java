package org.magic.gui.models.conf;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

public class SystemEnvTableModel extends DefaultTableModel {

	private List<String> keys;
	
	public SystemEnvTableModel() {
		keys = new ArrayList<>(System.getenv().keySet());
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	@Override
	public String getColumnName(int column) {
		if(column==0)
			return "Id";
		
		return "Value";
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		if(column==0)
			return keys.get(row);
		else
			return  System.getenv().get(keys.get(row));
		
	}
	
	
	@Override
	public int getRowCount() {
		return System.getenv().size();
	}
	
	@Override
	public int getColumnCount() {
		return 2;
	}
	
	
}

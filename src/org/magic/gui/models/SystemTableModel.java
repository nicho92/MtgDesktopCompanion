package org.magic.gui.models;

import java.util.Set;

import javax.swing.table.DefaultTableModel;

public class SystemTableModel extends DefaultTableModel {
		
		String[] columns = new String[]{"Key","Value"};
		Object[] propsSet = System.getProperties().keySet().toArray();
		
		public String getColumnName(int column) {
			return columns[column];
		}

		public int getRowCount() {
			return System.getProperties().size();
		}
		
		public int getColumnCount() {
				return 2;
		}
		
		
		public Object getValueAt(int row, int column) {
			String key = propsSet[row].toString();	
			
			if(column==0)
				return key;
			else
				return System.getProperties().getProperty(key);
				
		}
	
	

}

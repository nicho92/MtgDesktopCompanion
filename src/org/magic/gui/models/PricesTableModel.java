package org.magic.gui.models;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.table.DefaultTableModel;

import org.magic.tools.MagicFactory;

public class PricesTableModel extends DefaultTableModel {
	
	
		String columns[] = new String[]{"Provider","max result","Enable"};
	
		@Override
		public int getRowCount() {
			return MagicFactory.getInstance().getListPricers().size();
		}
		
		@Override
		public int getColumnCount() {
			return 3;
		}
		
		@Override
		public String getColumnName(int column) {
			return columns[column];
		}
		
		@Override
		public Object getValueAt(int row, int column) {
			switch(column)
			{
			case 0 :return MagicFactory.getInstance().getListPricers().get(row).getName();
			case 1 :return MagicFactory.getInstance().getListPricers().get(row).getProperties();
			case 2 : return true;
			default : return null;
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
		
			if(columnIndex==2)
				return Boolean.class;
			
		return super.getColumnClass(columnIndex);
		}
	
	
}

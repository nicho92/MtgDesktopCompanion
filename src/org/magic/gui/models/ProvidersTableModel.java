package org.magic.gui.models;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.table.DefaultTableModel;

import org.magic.tools.MagicFactory;

public class ProvidersTableModel extends DefaultTableModel {
	
	
		String columns[] = new String[]{"Provider","Version","URL","Enable"};
	
		@Override
		public int getRowCount() {
			return MagicFactory.getInstance().getListProviders().size();
		}
		
		@Override
		public int getColumnCount() {
			return 4;
		}
		
		@Override
		public String getColumnName(int column) {
			return columns[column];
		}
		
		@Override
		public Object getValueAt(int row, int column) {
			switch(column)
			{
			case 0 :return MagicFactory.getInstance().getListProviders().get(row);
			case 1 : return MagicFactory.getInstance().getListProviders().get(row).getVersion();
			case 2 : try {return MagicFactory.getInstance().getListProviders().get(row).getWebSite();} catch (MalformedURLException e) { return null;}
			case 3 : return true;
			default : return null;
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
		
			if(columnIndex==2)
				return URL.class;
			
			if(columnIndex==3)
				return Boolean.class;
			
		return super.getColumnClass(columnIndex);
		}

		
	
	
}

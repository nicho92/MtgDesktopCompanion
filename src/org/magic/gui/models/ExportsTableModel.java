package org.magic.gui.models;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.table.DefaultTableModel;

import org.magic.api.interfaces.CardExporter;
import org.magic.services.MagicFactory;

public class ExportsTableModel extends DefaultTableModel {
	
	
		String columns[] = new String[]{"Exports","Enable"};
	
		@Override
		public int getRowCount() {
			return MagicFactory.getInstance().getDeckExports().size();
		}
		
		@Override
		public int getColumnCount() {
			return 2;
		}
		
		@Override
		public String getColumnName(int column) {
			return columns[column];
		}
		
		@Override
		public Object getValueAt(int row, int column) {
			switch(column)
			{
			case 0 :return MagicFactory.getInstance().getDeckExports().get(row);
			case 1 : return MagicFactory.getInstance().getDeckExports().get(row).isEnable();
			default : return null;
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch(columnIndex)
			{
			case 0 : return CardExporter.class;
			case 1 : return Boolean.class;
			default : return Object.class;
			}
		}
		
		@Override
		public void setValueAt(Object aValue, int row, int column) {
			MagicFactory.getInstance().getDeckExports().get(row).enable(Boolean.parseBoolean(aValue.toString()));	
			MagicFactory.getInstance().setProperty(MagicFactory.getInstance().getDeckExports().get(row), aValue);
			
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			if(column==1)
				return true;
			
			else return false;
		}
	
	
}

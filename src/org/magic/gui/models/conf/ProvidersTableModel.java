package org.magic.gui.models.conf;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.table.DefaultTableModel;

import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.services.MTGDesktopCompanionControler;

public class ProvidersTableModel extends DefaultTableModel {
	
	
		static final String columns[] = new String[]{"Provider","Version","State","URL","Enable"};
	
		@Override
		public int getRowCount() {
			return MTGDesktopCompanionControler.getInstance().getListProviders().size();
		}
		
		@Override
		public int getColumnCount() {
			return columns.length;
		}
		
		@Override
		public String getColumnName(int column) {
			return columns[column];
		}
		
		@Override
		public Object getValueAt(int row, int column) {
			switch(column)
			{
			case 0 :return MTGDesktopCompanionControler.getInstance().getListProviders().get(row);
			case 1 : return MTGDesktopCompanionControler.getInstance().getListProviders().get(row).getVersion();
			case 2 : return MTGDesktopCompanionControler.getInstance().getListProviders().get(row).getStatut();
			case 3 : try {return MTGDesktopCompanionControler.getInstance().getListProviders().get(row).getWebSite();} catch (MalformedURLException e) { return null;}
			case 4 : return MTGDesktopCompanionControler.getInstance().getListProviders().get(row).isEnable();
			default : return null;
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
		
			if(columnIndex==3)
				return URL.class;
			
			if(columnIndex==4)
				return Boolean.class;
			
		return super.getColumnClass(columnIndex);
		}
		
		@Override
		public void setValueAt(Object aValue, int row, int column) {
			MTGDesktopCompanionControler.getInstance().getListProviders().get(row).enable(Boolean.parseBoolean(aValue.toString()));	
			MTGDesktopCompanionControler.getInstance().setProperty(MTGDesktopCompanionControler.getInstance().getListProviders().get(row), aValue);
			
			for(MagicCardsProvider daos : MTGDesktopCompanionControler.getInstance().getListProviders())
    		{
				if(daos!=MTGDesktopCompanionControler.getInstance().getListProviders().get(row))
    			{
    				daos.enable(false);
    				MTGDesktopCompanionControler.getInstance().setProperty(daos, daos.isEnable());
    			}
    		}
			fireTableDataChanged();
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			if(column==4)
				return true;
			
			else return false;
		}
}

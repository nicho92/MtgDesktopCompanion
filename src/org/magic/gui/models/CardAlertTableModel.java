package org.magic.gui.models;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicEdition;
import org.magic.services.MTGDesktopCompanionControler;


public class CardAlertTableModel extends DefaultTableModel {

	
	static final String columns[] = new String[]{"Card","Edition","Maximum bid","Offers"};
	
	
	
	public CardAlertTableModel() {
	}
	
	
	@Override
	public int getColumnCount() {
		return columns.length;
	}
	
	
	@Override
	public int getRowCount() {
		try{
			if(MTGDesktopCompanionControler.getInstance().getEnabledDAO().getAlerts()!=null)
			return MTGDesktopCompanionControler.getInstance().getEnabledDAO().getAlerts().size();
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
			
		return 0;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch(columnIndex)
		{
			case 0 : return MagicCardAlert.class;
			case 1 : return MagicEdition.class;
			case 2 : return Double.class;
			case 3 : return Integer.class;
			default : return super.getColumnClass(columnIndex);
		}
		
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return (column==2);
	}
	
	
	@Override
	public String getColumnName(int column) {
		return columns[column];
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		
		switch(column)
		{
			case 0 : return MTGDesktopCompanionControler.getInstance().getEnabledDAO().getAlerts().get(row);
			case 1 : return MTGDesktopCompanionControler.getInstance().getEnabledDAO().getAlerts().get(row).getCard().getEditions().get(0);
			case 2 : return MTGDesktopCompanionControler.getInstance().getEnabledDAO().getAlerts().get(row).getPrice();
			case 3 : return MTGDesktopCompanionControler.getInstance().getEnabledDAO().getAlerts().get(row).getOffers().size();
		default : return "";
		}
	}
	
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		MagicCardAlert alert = MTGDesktopCompanionControler.getInstance().getEnabledDAO().getAlerts().get(row);
		alert.setPrice(Double.parseDouble(aValue.toString()));
		try {
			MTGDesktopCompanionControler.getInstance().getEnabledDAO().updateAlert(alert);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

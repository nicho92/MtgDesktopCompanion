package org.magic.gui.models;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicEdition;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;


public class CardAlertTableModel extends DefaultTableModel {

	
	static final String[] columns = new String[]{
											MTGControler.getInstance().getLangService().getCapitalize("CARD"),
											MTGControler.getInstance().getLangService().getCapitalize("EDITION"),
											MTGControler.getInstance().getLangService().getCapitalize("MAX_BID"),
											MTGControler.getInstance().getLangService().getCapitalize("OFFERS")
									};
	
	@Override
	public int getColumnCount() {
		return columns.length;
	}
	
	
	@Override
	public int getRowCount() {
		try{
			if(MTGControler.getInstance().getEnabledDAO().getAlerts()!=null)
			return MTGControler.getInstance().getEnabledDAO().getAlerts().size();
			
		}catch(Exception e)
		{
			MTGLogger.printStackTrace(e);
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
			case 0 : return MTGControler.getInstance().getEnabledDAO().getAlerts().get(row);
			case 1 : return MTGControler.getInstance().getEnabledDAO().getAlerts().get(row).getCard().getEditions().get(0);
			case 2 : return MTGControler.getInstance().getEnabledDAO().getAlerts().get(row).getPrice();
			case 3 : return MTGControler.getInstance().getEnabledDAO().getAlerts().get(row).getOffers().size();
		default : return "";
		}
	}
	
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		MagicCardAlert alert = MTGControler.getInstance().getEnabledDAO().getAlerts().get(row);
		alert.setPrice(Double.parseDouble(aValue.toString()));
		try {
			MTGControler.getInstance().getEnabledDAO().updateAlert(alert);
			fireTableDataChanged();
		} catch (Exception e) {
			MTGLogger.printStackTrace(e);
		}
	}
	
}

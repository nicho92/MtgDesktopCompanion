package org.magic.gui.models;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.services.MagicFactory;

public class CardAlertTableModel extends DefaultTableModel {

	
	static final String columns[] = new String[]{"Card","Edition","Price"};
	
	
	
	public CardAlertTableModel() {
	}
	
	
	@Override
	public int getColumnCount() {
		return columns.length;
	}
	
	
	@Override
	public int getRowCount() {
		if(MagicFactory.getInstance().getAlerts()!=null)
			return MagicFactory.getInstance().getAlerts().size();
		return 0;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch(columnIndex)
		{
			case 0 : return MagicCard.class;
			case 1 : return MagicEdition.class;
			case 2 : return Double.class;
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
		case 0 : return MagicFactory.getInstance().getAlerts().get(row).getCard();
		case 1 : return MagicFactory.getInstance().getAlerts().get(row).getCard().getEditions().get(0);
		case 2 : return MagicFactory.getInstance().getAlerts().get(row).getPrice();
		default : return "";
		}
	}
	
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		MagicFactory.getInstance().getAlerts().get(row).setPrice((Double)aValue);
	}
	
}

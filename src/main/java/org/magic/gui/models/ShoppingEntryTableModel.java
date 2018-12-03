package org.magic.gui.models;

import org.magic.api.beans.OrderEntry;
import org.magic.gui.abstracts.GenericTableModel;

public class ShoppingEntryTableModel extends GenericTableModel<OrderEntry> {

	public ShoppingEntryTableModel() {
		columns=new String[]{"ID", "DATE","TYPE","SENS","DESCRIPTION","EDITION","COLLECTION","PRICE" };
	}
	
	
	@Override
	public Object getValueAt(int row, int column) {
		
		OrderEntry e = items.get(row);
		
		switch (column) { 
			case 0 : return e.getIdTransation();
			case 1 : return e.getTransationDate();
			case 2 : return e.getType();
			case 3 : return e.getTypeTransaction();
			case 4 : return e.getDescription();
			case 5 : return e.getEdition();
			case 6 : return e.getCollection();
			case 7 : return e.getItemPrice();
			
			default : return new Object();
			
		}
	}
}

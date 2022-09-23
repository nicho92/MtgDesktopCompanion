package org.magic.gui.models;

import java.util.Date;

import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.OrderEntry;
import org.magic.gui.abstracts.GenericTableModel;

public class ShoppingEntryTableModel extends GenericTableModel<OrderEntry> {

	private static final long serialVersionUID = 1L;


	public ShoppingEntryTableModel() {
		columns=new String[]{"ID", "SOURCE","DATE","TYPE","MOVE","DESCRIPTION","EDITION","PRICE" };
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(columns[columnIndex].equals("ID"))
			return OrderEntry.class;

		if(columns[columnIndex].equals("DATE"))
			return Date.class;

		if(columns[columnIndex].equals("PRICE"))
			return Double.class;

		if(columns[columnIndex].equals("EDITION"))
			return MagicEdition.class;

		return super.getColumnClass(columnIndex);
	}


	@Override
	public Object getValueAt(int row, int column) {
		OrderEntry e = items.get(row);
		switch (column) {
			case 0 : return e;
			case 1 : return e.getSource();
			case 2 : return e.getTransactionDate();
			case 3 : return e.getType();
			case 4 : return e.getTypeTransaction();
			case 5 : return e.getDescription();
			case 6 : return e.getEdition();
			case 7 : return e.getItemPrice();
			default : return new Object();

		}
	}
}

package org.magic.gui.models;

import java.util.Date;

import org.magic.api.beans.technical.MoneyValue;
import org.magic.api.beans.technical.RetrievableTransaction;
import org.magic.gui.abstracts.GenericTableModel;

public class ShoppingEntryTableModel extends GenericTableModel<RetrievableTransaction> {

	private static final long serialVersionUID = 1L;


	public ShoppingEntryTableModel() {
		columns=new String[]{"ID","DATE","DESCRIPTION","TOTAL" };
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		switch(columnIndex)
		{
			case 0 : return RetrievableTransaction.class;
			case 1 : return Date.class;
			case 4 : return MoneyValue.class;
			default : return super.getColumnClass(columnIndex);
		}
	}


	@Override
	public Object getValueAt(int row, int column) {
		RetrievableTransaction e = items.get(row);
		switch (column) {
			case 0 : return e;
			case 1 : return e.getDateTransaction();
			case 2 : return e.getComments();
			case 3 : return e.getTotalValue();
			default : return new Object();

		}
	}
}

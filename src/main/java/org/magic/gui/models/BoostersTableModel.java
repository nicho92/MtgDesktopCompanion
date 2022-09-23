package org.magic.gui.models;

import org.magic.api.beans.Booster;
import org.magic.gui.abstracts.GenericTableModel;

public class BoostersTableModel extends GenericTableModel<Booster> {

	private static final long serialVersionUID = 1L;

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return Booster.class;
		case 1:
			return Double.class;
		default:
			return super.getColumnClass(columnIndex);
		}
	}

	@Override
	public Object getValueAt(int row, int column) {

		switch (column) {
		case 0:
			return items.get(row);
		case 1:
			return items.get(row).getPrice();
		default:
			return "";
		}
	}



	public BoostersTableModel() {
		columns=new String[]{ "CARD_NUMBER","PRICE" };
	}



}
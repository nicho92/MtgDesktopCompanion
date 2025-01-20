package org.magic.gui.models;

import org.magic.api.beans.MTGBooster;
import org.magic.gui.abstracts.GenericTableModel;

public class BoostersTableModel extends GenericTableModel<MTGBooster> {

	private static final long serialVersionUID = 1L;

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return MTGBooster.class;
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
		setColumns("CARD_NUMBER","PRICE" );
	}



}
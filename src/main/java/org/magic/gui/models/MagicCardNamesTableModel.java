package org.magic.gui.models;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.gui.abstracts.GenericTableModel;

public class MagicCardNamesTableModel extends GenericTableModel<MagicCardNames> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private MagicCard mc;


	public MagicCardNamesTableModel() {
		columns= new String[] {
				"CARD_LANGUAGE",
				"NAME",
				"Gatherer ID" };
	}


	public void init(MagicCard mc) {
		this.mc=mc;
		items=mc.getForeignNames();
		fireTableDataChanged();
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return true;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.class;
		case 1:
			return String.class;
		case 2:
			return Integer.class;
		default:
			return Object.class;
		}
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		switch (column) {
		case 0:
			mc.getForeignNames().get(row).setLanguage(String.valueOf(aValue));
			break;
		case 1:
			mc.getForeignNames().get(row).setName(String.valueOf(aValue));
			break;
		case 2:
			mc.getForeignNames().get(row).setGathererId(Integer.parseInt(aValue.toString()));
			break;
		default:
			break;

		}
	}

	@Override
	public Object getValueAt(int row, int column) {
		switch (column) {
		case 0:
			return mc.getForeignNames().get(row).getLanguage();
		case 1:
			return mc.getForeignNames().get(row).getName();
		case 2:
			return mc.getForeignNames().get(row).getGathererId();
		default:
			return "";
		}

	}

}

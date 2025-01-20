package org.magic.gui.models;

import static org.magic.services.tools.MTG.capitalize;

import java.util.List;

import org.apache.commons.lang3.tuple.MutableTriple;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumExtra;
import org.magic.gui.abstracts.GenericTableModel;
public class SealedBoosterTableModel extends GenericTableModel<MutableTriple<MTGEdition, EnumExtra, Integer>> {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final String[] COLUMNS = { "EDITION","TYPE","QTY" };


	public void add(MTGEdition ed, EnumExtra extra, Integer qty) {
		items.add(MutableTriple.of(ed, extra,qty));
		fireTableDataChanged();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return MTGEdition.class;
		case 1:
			return EnumExtra.class;
		case 2:
			return Integer.class;
		default:
			return super.getColumnClass(columnIndex);
		}
	}

	@Override
	public Object getValueAt(int row, int column) {
		switch (column) {
		case 0:
			return items.get(row).getLeft();
		case 1:
			return items.get(row).getMiddle();
		case 2:
			return items.get(row).getRight();
		default:
			return "";
		}
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		if (column == 0)
			items.get(row).setLeft((MTGEdition) aValue);
		else if (column == 1)
			items.get(row).setMiddle(EnumExtra.valueOf(aValue.toString().toUpperCase()));
		else if (column == 2) {
			if (Integer.parseInt(aValue.toString()) > 0) {
				items.get(row).setRight(Integer.parseInt(aValue.toString()));
			} else {
				items.remove(row);
			}
		}
		
		fireTableDataChanged();
	}

	@Override
	public String getColumnName(int column) {
		return capitalize(COLUMNS[column]);
	}

	public void init(List<MutableTriple<MTGEdition, EnumExtra, Integer>> lines) {
		items = lines;
		fireTableDataChanged();
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return column > 0;
	}

	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}


}

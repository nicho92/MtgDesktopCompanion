package org.magic.gui.models;

import static org.magic.services.tools.MTG.capitalize;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumExtra;
public class SealedBoosterTableModel extends DefaultTableModel {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private List<MutableTriple<MagicEdition, EnumExtra, Integer>> pack;

	private static final String[] COLUMNS = { "EDITION","TYPE","QTY" };

	public List<MutableTriple<MagicEdition, EnumExtra, Integer>> getSealedPack() {
		return pack;
	}

	public void add(MagicEdition ed, EnumExtra extra, Integer qty) {
		pack.add(MutableTriple.of(ed, extra,qty));
		fireTableDataChanged();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return MagicEdition.class;
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
			return pack.get(row).getLeft();
		case 1:
			return pack.get(row).getMiddle();
		case 2:
			return pack.get(row).getRight();
		default:
			return "";
		}
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		if (column == 0)
			pack.get(row).setLeft((MagicEdition) aValue);
		else if (column == 1)
			pack.get(row).setMiddle(EnumExtra.valueOf(aValue.toString().toUpperCase()));
		else if (column == 2) {
			if (Integer.parseInt(aValue.toString()) > 0) {
				pack.get(row).setRight(Integer.parseInt(aValue.toString()));
			} else {
				pack.remove(row);
			}
		}
		
		fireTableDataChanged();
	}

	public void clear() {
		pack.clear();

	}

	@Override
	public String getColumnName(int column) {
		return capitalize(COLUMNS[column]);
	}

	public SealedBoosterTableModel() {
		pack = new ArrayList<>();
	}

	public void init(List<MutableTriple<MagicEdition, EnumExtra, Integer>> lines) {
		this.pack = lines;
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

	@Override
	public int getRowCount() {
		if (pack == null)
			return 0;
		else
			return pack.size();
	}

}

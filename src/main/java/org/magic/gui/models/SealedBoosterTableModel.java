package org.magic.gui.models;

import static org.magic.services.tools.MTG.capitalize;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.BoosterPackContainer;
import org.magic.api.beans.MagicEdition;
public class SealedBoosterTableModel extends DefaultTableModel {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private BoosterPackContainer pack;

	private static final String[] COLUMNS = { "EDITION",
			"QTY" };

	public BoosterPackContainer getSealedPack() {
		return pack;
	}

	public void add(MagicEdition ed, Integer qty) {
		pack.set(ed, qty);
		fireTableDataChanged();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return MagicEdition.class;
		case 1:
			return Integer.class;
		default:
			return super.getColumnClass(columnIndex);
		}
	}

	@Override
	public Object getValueAt(int row, int column) {
		MagicEdition ed = pack.listEditions().get(row);
		switch (column) {
		case 0:
			return ed;
		case 1:
			return pack.getQty(ed);
		default:
			return "";
		}
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		MagicEdition ed = pack.listEditions().get(row);

		if (column == 0)
			pack.set((MagicEdition) aValue, 0);
		else if (column == 1) {
			if (Integer.parseInt(aValue.toString()) > 0) {
				pack.set(ed, Integer.parseInt(aValue.toString()));
			} else {
				pack.remove(ed);

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
		pack = new BoosterPackContainer();
	}

	public void init(BoosterPackContainer lines) {
		this.pack = lines;
		fireTableDataChanged();
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return column == 1;
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

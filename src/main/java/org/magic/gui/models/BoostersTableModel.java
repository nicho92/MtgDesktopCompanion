package org.magic.gui.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.Booster;
import org.magic.services.MTGControler;

public class BoostersTableModel extends DefaultTableModel {

	private transient List<Booster> boosters;
	private static final String[] COLUMNS = { "CARD_NUMBER",
			"PRICE" };

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
			return boosters.get(row);
		case 1:
			return boosters.get(row).getPrice();
		default:
			return "";
		}
	}

	public void clear() {
		boosters.clear();

	}

	@Override
	public String getColumnName(int column) {
		return MTGControler.getInstance().getLangService().getCapitalize(COLUMNS[column]);
	}

	public void addLine(Booster bl) {
		boosters.add(bl);
		fireTableDataChanged();
	}

	public BoostersTableModel() {
		boosters = new ArrayList<>();
	}

	public void init(List<Booster> lines) {
		this.boosters = lines;
		fireTableDataChanged();
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}

	@Override
	public int getRowCount() {
		if (boosters == null)
			return 0;
		else
			return boosters.size();
	}

}
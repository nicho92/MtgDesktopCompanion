package org.magic.gui.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.magic.api.beans.CardShake;
import org.magic.api.interfaces.abstracts.AbstractDashBoard.FORMAT;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class CardsShakerTableModel extends DefaultTableModel {

	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private transient List<CardShake> list;

	String[] columns = new String[] { MTGControler.getInstance().getLangService().getCapitalize("CARD"),
			MTGControler.getInstance().getLangService().getCapitalize("EDITION"),
			MTGControler.getInstance().getLangService().getCapitalize("PRICE"),
			MTGControler.getInstance().getLangService().getCapitalize("DAILY"),
			MTGControler.getInstance().getLangService().getCapitalize("PC_DAILY") };

	public CardsShakerTableModel() {
		list = new ArrayList<>();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return CardShake.class;
		case 1:
			return String.class;
		case 2:
			return Double.class;
		case 4:
			return Double.class;
		default:
			return super.getColumnClass(columnIndex);
		}
	}

	public void init(List<CardShake> l) {
		this.list = l;

	}

	public void init(FORMAT f) {
		try {
			list = MTGControler.getInstance().getEnabledDashBoard().getShakerFor(f.toString());

		} catch (IOException e) {
			logger.error(e);
		}
	}

	@Override
	public String getColumnName(int column) {
		return columns[column];
	}

	@Override
	public int getRowCount() {
		if (list != null)
			return list.size();
		else
			return 0;
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public Object getValueAt(int row, int column) {
		try {

			CardShake mp = list.get(row);
			switch (column) {
			case 0:
				return mp;
			case 1:
				return mp.getEd();
			case 2:
				return mp.getPrice();
			case 3:
				return mp.getPriceDayChange();
			case 4:
				return mp.getPercentDayChange();
			case 5:
				return mp.getPriceDayChange();
			default:
				return 0;
			}
		} catch (IndexOutOfBoundsException ioob) {
			logger.error(ioob);
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

}

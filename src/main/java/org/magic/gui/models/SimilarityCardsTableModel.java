package org.magic.gui.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.MagicCard;
import org.magic.services.MTGControler;

public class SimilarityCardsTableModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient Map<MagicCard,Float> map;
	private static final String[] COLUMNS = { "CARD","CARD_EDITIONS","%" };

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return MagicCard.class;
		case 2:
			return Float.class;
		default:
			return String.class;
		}
	}

	@Override
	public Object getValueAt(int row, int column) {

		
		MagicCard r = new ArrayList<>(map.keySet()).get(row);
		switch (column) {
		case 0:return r;
		case 1:return r.getCurrentSet();
		case 2:return map.get(r);
		default:
			return "";
		}
	}


	@Override
	public String getColumnName(int column) {
		try {
			return MTGControler.getInstance().getLangService().getCapitalize(COLUMNS[column]);
		}
		catch(Exception e){
			return COLUMNS[column];
		}
	}

	public SimilarityCardsTableModel() {
		map = new HashMap<>();
	}

	public void init(Map<MagicCard,Float> lines) {
		this.map = lines;
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
		if (map == null)
			return 0;
		else
			return map.size();
	}

}
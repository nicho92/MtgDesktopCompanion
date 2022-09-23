package org.magic.gui.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.gui.abstracts.GenericTableModel;

public class SimilarityCardsTableModel extends GenericTableModel<MagicCard> {


	private static final long serialVersionUID = 1L;
	private transient Map<MagicCard,Float> map;

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

	public SimilarityCardsTableModel() {
		map = new HashMap<>();
		columns = new String[]{ "CARD","CARD_EDITIONS","%" };
	}

	public void init(Map<MagicCard,Float> lines) {
		this.map = lines;
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		if (map == null)
			return 0;
		else
			return map.size();
	}

}
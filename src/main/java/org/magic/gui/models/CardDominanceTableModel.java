package org.magic.gui.models;

import org.magic.api.beans.MTGDominance;
import org.magic.gui.abstracts.GenericTableModel;

public class CardDominanceTableModel extends GenericTableModel<MTGDominance> {

	private static final long serialVersionUID = 1L;

	public CardDominanceTableModel() {
		setColumns("CARD",
				"POSITION",
				"PC_DECKS",
				"PLAYED" );
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return MTGDominance.class;
		case 1:
			return Integer.class;
		case 2:
			return Double.class;
		case 3:
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
			return items.get(row).getPosition();
		case 2:
			return items.get(row).getDecksPercent();
		case 3:
			return items.get(row).getPlayers();
		default:
			return "";
		}
	}

}

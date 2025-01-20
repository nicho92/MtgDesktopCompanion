package org.magic.gui.models;

import org.magic.api.beans.technical.RetrievableDeck;
import org.magic.gui.abstracts.GenericTableModel;

public class DeckSnifferTableModel extends GenericTableModel<RetrievableDeck> {

	private static final long serialVersionUID = 1L;
	public DeckSnifferTableModel() {
		setColumns("NAME",
				"CARD_COLOR",
				"AUTHOR",
				"DESCRIPTION"
		);
	}

	@Override
	public Object getValueAt(int row, int column) {

		switch (column) {
		case 0:
			return items.get(row);
		case 1:
			return items.get(row).getColor();
		case 2:
			return items.get(row).getAuthor();
		case 3:
			return items.get(row).getDescription();
		default:
			return null;
		}

	}


}

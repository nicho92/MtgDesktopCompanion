package org.magic.gui.models;

import java.sql.Date;

import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGFormat;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.services.MTGDeckManager;

public class DeckSelectionTableModel extends GenericTableModel<MTGDeck> {

	private static final long serialVersionUID = 1L;

	public DeckSelectionTableModel() {
		columns=new String[] {"DECK","CARD_COLOR","STANDARD","MODERN","LEGACY","VINTAGE","ARENA","CARDS","DATE"};
	}


	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return MTGDeck.class;
		case 1:
			return String.class;
		case 2:
			return Boolean.class;
		case 3:
			return Boolean.class;
		case 4:
			return Boolean.class;
		case 5:
			return Boolean.class;
		case 6:
			return Boolean.class;
		case 7:
			return Integer.class;
		case 8:
			return Date.class;

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
			return items.get(row).getColors();
		case 2:
			return MTGDeckManager.isLegal(items.get(row), MTGFormat.FORMATS.valueOf(columns[column]));
		case 3:
			return MTGDeckManager.isLegal(items.get(row), MTGFormat.FORMATS.valueOf(columns[column]));
		case 4:
			return MTGDeckManager.isLegal(items.get(row), MTGFormat.FORMATS.valueOf(columns[column]));
		case 5:
			return MTGDeckManager.isLegal(items.get(row), MTGFormat.FORMATS.valueOf(columns[column]));
		case 6:
			return MTGDeckManager.isArenaDeck(items.get(row));
		case 7:
			return items.get(row).getMainAsList().size();
		case 8: 
			return items.get(row).getDateCreation();

		default:
			return "";
		}
	}

}

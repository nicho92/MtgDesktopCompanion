package org.magic.gui.models;

import java.io.IOException;

import org.magic.api.beans.MagicDeck;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.services.MTGDeckManager;
import org.magic.services.ThreadManager;
import org.utils.patterns.observer.Observable;

public class DeckSelectionTableModel extends GenericTableModel<MagicDeck> {

	private static final long serialVersionUID = 1L;
	private transient MTGDeckManager manager;

	public DeckSelectionTableModel() {
		columns=new String[] {"DECK","CARD_COLOR","Standard","Modern","Legacy","Vintage","CARDS"};
		
		manager = new MTGDeckManager();
		manager.addObserver((Observable o, Object obj)->{
				items.add((MagicDeck) obj);
				fireTableDataChanged();
		});
	}
	
	
	public void init()
	{
		ThreadManager.getInstance().execute(() -> manager.listDecks(), "ListDecks");
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return MagicDeck.class;
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
			return Integer.class;
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
			return manager.isLegal(items.get(row), columns[column]);
		case 3:
			return manager.isLegal(items.get(row), columns[column]);
		case 4:
			return manager.isLegal(items.get(row), columns[column]);
		case 5:
			return manager.isLegal(items.get(row), columns[column]);
		case 6:
			return items.get(row).getAsList().size();

		default:
			return "";
		}
	}

	public void removeDeck(MagicDeck selectedDeck) throws IOException {
			manager.remove(selectedDeck);
			items.remove(selectedDeck);
			fireTableDataChanged();
	}

	

}

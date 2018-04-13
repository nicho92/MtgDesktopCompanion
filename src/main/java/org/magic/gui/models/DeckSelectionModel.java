package org.magic.gui.models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicFormat;
import org.magic.api.exports.impl.MTGDesktopCompanionExport;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGDeckSniffer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGDeckManager;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public class DeckSelectionModel extends DefaultTableModel {

	private final String[] columns={MTGControler.getInstance().getLangService().getCapitalize("DECK"),
								   MTGControler.getInstance().getLangService().getCapitalize("CARD_COLOR"),
								   "Standard","Modern","Legacy","Vintage",
								   MTGControler.getInstance().getLangService().getCapitalize("CARDS")};
	private List<MagicDeck> decks;
	private MTGDeckManager manager;
	
	public DeckSelectionModel() 
	{
		decks = new ArrayList<>();
		manager = new MTGDeckManager();
		manager.addObserver(new Observer() {
			
			@Override
			public void update(Observable o, Object obj) {
				decks.add((MagicDeck)obj);
				fireTableDataChanged();
				
			}
		});
		ThreadManager.getInstance().execute(()->manager.listDecks());
		
	}
	
	@Override
	public String getColumnName(int column) {
		return columns[column];
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	@Override
	public int getRowCount() {
		if(decks !=null)
			return decks.size();
		
		return 0;
	}
	
	@Override
	public int getColumnCount() {
		return columns.length;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch(columnIndex)
		{
		case 0 : return MagicDeck.class;
		case 1 : return String.class;
		case 2 : return Boolean.class;
		case 3 : return Boolean.class;
		case 4 : return Boolean.class;
		case 5 : return Boolean.class;
		case 6 : return Integer.class;
		default: return super.getColumnClass(columnIndex);
		}
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		switch(column)
		{
		case 0 : return decks.get(row);
		case 1 : return decks.get(row).getColors();
		case 2 : return isLegal(decks.get(row),columns[column]);
		case 3 : return isLegal(decks.get(row),columns[column]);
		case 4 : return isLegal(decks.get(row),columns[column]);
		case 5 : return isLegal(decks.get(row),columns[column]);
		case 6 : return decks.get(row).getAsList().size();
		
		default: return "";
		}
	}

	private boolean isLegal(MagicDeck magicDeck,String format) {
		MagicFormat mf = new MagicFormat();
				mf.setFormat(format);
		return magicDeck.isCompatibleFormat(mf);
		
	}

	public void remove(MagicDeck selectedDeck) {
		 manager.remove(selectedDeck);
		 decks.remove(selectedDeck);
		 fireTableDataChanged();
		
	}
	
}

package org.magic.gui.models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.MagicDeck;
import org.magic.api.exports.impl.MTGDesktopCompanionExport;
import org.magic.services.MagicFactory;

public class DeckSelectionModel extends DefaultTableModel {

	final static String[] columns={"Deck","Color","Legalities"};
	List<MagicDeck> decks;
	
	public DeckSelectionModel() {
		
		decks = new ArrayList<MagicDeck>();
		
		for(File f : new File(MagicFactory.CONF_DIR,"decks").listFiles() )
		{
			try {
				MagicDeck deck = new MTGDesktopCompanionExport().importDeck(f);
				decks.add(deck);
			} catch (Exception e) {
			}
		}
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
		case 2 : return Set.class;
		default: return super.getColumnClass(columnIndex);
		}
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		switch(column)
		{
		case 0 : return decks.get(row);
		case 1 : return decks.get(row).getColors();
		case 2 : return decks.get(row).getLegality();
		default: return "";
		}
	}
	
}

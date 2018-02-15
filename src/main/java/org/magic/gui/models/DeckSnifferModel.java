package org.magic.gui.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.MTGDeckSniffer;
import org.magic.services.MTGControler;

public class DeckSnifferModel extends DefaultTableModel {

	private final String[] columns = new String[]{MTGControler.getInstance().getLangService().getCapitalize("NAME"),
										  MTGControler.getInstance().getLangService().getCapitalize("CARD_COLOR"),
										  MTGControler.getInstance().getLangService().getCapitalize("AUTHOR"),
										  MTGControler.getInstance().getLangService().getCapitalize("DESCRIPTION")
										  
										 };

	private transient List<RetrievableDeck> decks;
	
	public DeckSnifferModel() {
		decks=new ArrayList<>();
	}
	
	public void init(MTGDeckSniffer sniff) throws IOException
	{
		decks=sniff.getDeckList();
	}
	
	@Override
	public int getColumnCount() {
		return columns.length;
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	@Override
	public int getRowCount() {
		if(decks!=null)
			return decks.size();
		else
			return 0;
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		
		switch(column)
		{
		case 0 : return decks.get(row);
		case 1 : return decks.get(row).getColor();
		case 2 : return decks.get(row).getAuthor();
		case 3 : return decks.get(row).getDescription();
		default : return null;
		}
		
	}
	
	@Override
	public String getColumnName(int column) {
		return columns[column];
	}
	
}

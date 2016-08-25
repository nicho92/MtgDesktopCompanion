package org.magic.gui.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.DeckSniffer;

public class DeckSnifferModel extends DefaultTableModel {

	final String[] columns = new String[]{"name"};

	List<RetrievableDeck> decks;
	
	public DeckSnifferModel() {
		decks=new ArrayList<RetrievableDeck>();
	}
	
	public void init(DeckSniffer sniff) throws Exception
	{
		decks=sniff.getDeckList();
	}
	
	@Override
	public int getColumnCount() {
		return columns.length;
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
		return decks.get(row);
	}
	
	@Override
	public String getColumnName(int column) {
		return columns[column];
	}
	
}

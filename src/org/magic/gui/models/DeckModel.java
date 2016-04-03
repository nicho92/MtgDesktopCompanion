package org.magic.gui.models;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;

public class DeckModel extends DefaultTableModel {

	String[] columns = new String[]{"name","type","cost","color","edition","Qty"};

	private MagicDeck deck;
	public static enum TYPE { DECK,SIDE };
	
	private TYPE t;
	
	public DeckModel(TYPE t) {
		this.t=t;
		deck = new MagicDeck();
	}
	
	public void initSide(MagicDeck deck)
	{
		this.deck=deck;
	}
	
	public MagicDeck getDeck() {
		return deck;
	}
	
	
	public void load(MagicDeck deck)
	{
		this.deck=deck;
	}
	
	
	@Override
	public int getColumnCount() {
		return columns.length;
	}
	
	@Override
	public String getColumnName(int column) {
		return columns[column];
	}

	@Override
	public Object getValueAt(int row, int column) {
		MagicCard mc ;
		switch(t)
		{
			case DECK :mc = deck.getValueAt(row);break;
			case SIDE : mc = deck.getSideValueAt(row);break;
			default : mc = deck.getValueAt(row);break;
		}
		
		
 
		if(column==0)
			return mc;
		
		if(column==1)
			return mc.getFullType();
	
		
		if(column==2)
			return mc.getCost();
		
		if(column==3)
			return mc.getColors();

		if(column==4)
			return mc.getEditions();
		
		if(column==5)
		{
			switch(t)
			{
				case DECK :return deck.getMap().get(mc);
				case SIDE : return deck.getMapSideBoard().get(mc);
				default : return null;
			}
			
		}
		
		
		return null;
	}
	
	@Override
	public int getRowCount() {
		if(deck==null)
			return 0;
		
		switch(t)
		{
			case DECK : return deck.getMap().size();
			case SIDE : return deck.getMapSideBoard().size();
			default :return deck.getMap().size();
		}
		
	}
	
	public void init(MagicDeck deck)
	{
		this.deck=deck;
	}
	

	@Override
	public boolean isCellEditable(int row, int column) {
		
		if(column==columns.length-1)
			return true;
		
		return false;
	}
	
	
	
	public void setValueAt(Object aValue, int row, int column) {
		if(Integer.valueOf(aValue.toString())==0)
		{
			deck.getMap().remove(deck.getValueAt(row));
		}
		else
		{	
			deck.getMap().put(deck.getValueAt(row),Integer.valueOf(aValue.toString()));
		}
	}
	
	
}

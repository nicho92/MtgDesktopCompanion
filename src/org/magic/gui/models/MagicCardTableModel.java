package org.magic.gui.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;

public class MagicCardTableModel extends DefaultTableModel{

	List<MagicCard> cards;
	
	String columns[] = new String[] {"name","manacost","type","power","rarity","Editions","N°"};
	
	
	public MagicCardTableModel() {
		cards = new ArrayList<MagicCard>();
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}
	
	public int getRowCount() {
		if(cards==null)
			return 0;
		
		return cards.size();
	}
	
	public String getColumnName(int column) {
		return columns[column];
	}
	
	public Class<?> getColumnClass(int columnIndex) {
		return super.getColumnClass(columnIndex);
//		switch(columnIndex)
//		{
//		case 0 : return MagicCard.class;
//		case 1 : return String.class;
//		case 2 : return String.class;
//		case 3 : return String.class;
//		case 4 : return String.class;
//		case 5 : return MagicEdition.class;
//		case 6 : return String.class;
//		default : return String.class;
//		}
	}

	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	public Object getValueAt(int row, int column) {
		MagicCard mc =cards.get(row); 
		
		switch(column)
		{
			case 0: return mc;
			case 1: return mc.getCost();
			case 2 : return mc.getFullType();
			case 3:  return contains(mc.getTypes(),"creature")? mc.getPower() +"/"+mc.getToughness() : contains(mc.getTypes(),"planeswalker")? mc.getLoyalty() : "";
			case 4 : return mc.getEditions().get(0).getRarity();
			case 5 : return mc.getEditions();
			case 6 : return mc.getNumber();
			default : return mc;
		}
		
	}

	private boolean contains(List<String> types, String string) {
		for(String s : types)
			if(s.toLowerCase().equals(string.toLowerCase()))
				return true;
		
		
		return false;
						
	}

	public void init(List<MagicCard> cards2) {
		this.cards=cards2;
		
	}

	public List<MagicCard> getListCards() {
		return cards;
	};
}

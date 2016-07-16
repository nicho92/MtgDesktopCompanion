package org.magic.gui.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;

public class MagicCardTableModel extends DefaultTableModel{

	List<MagicCard> cards;
	String langage;
	String columns[] = new String[] {"name","langage","manacost","type","power","rarity","Editions","N°","Color"};
	
	
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
			case 1: return getName(mc.getForeignNames());
			case 2: return mc.getCost();
			case 3 : return mc.getFullType();
			case 4:  return contains(mc.getTypes(),"creature")? mc.getPower() +"/"+mc.getToughness() : contains(mc.getTypes(),"planeswalker")? mc.getLoyalty() : "";
			case 5 : return mc.getEditions().get(0).getRarity();
			case 6 : return mc.getEditions();
			case 7 : return mc.getNumber();
			case 8 : return mc.getColors();
			default : return mc;
		}
		
	}

	private String getName(List<MagicCardNames> foreignNames) {
		for(MagicCardNames name: foreignNames)
		{
			if(name.getLanguage().equals(langage))
				return name.getName();
		}
		return "";
	}

	private boolean contains(List<String> types, String string) {
		for(String s : types)
			if(s.toLowerCase().equals(string.toLowerCase()))
				return true;
		
		
		return false;
						
	}

	public void init(List<MagicCard> cards2,String lang) {
		this.cards=cards2;
		columns[1]=lang;
		this.langage=lang;
	}

	public List<MagicCard> getListCards() {
		return cards;
	};
}

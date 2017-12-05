package org.magic.gui.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.magic.api.beans.CardDominance;
import org.magic.api.interfaces.abstracts.AbstractDashBoard.FORMAT;
import org.magic.services.MTGControler;
import org.magic.tools.MTGLogger;


public class CardDominanceTableModel extends DefaultTableModel {

	
	static final String columns[] = new String[]{"Card","Position","% Dominance","% deck","Players"};
	Logger logger = MTGLogger.getLogger(this.getClass());
	private List<CardDominance> list;
	
	public CardDominanceTableModel() {
		list=new ArrayList<CardDominance>();
	}
	
	public void init(FORMAT f,String filter)
	{
		try {
			list=MTGControler.getInstance().getEnabledDashBoard().getBestCards(f, filter);
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	@Override
	public int getColumnCount() {
		return columns.length;
	}
	
	
	
	
	@Override
	public int getRowCount() {
			if(list!=null)
				return list.size();
		return 0;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch(columnIndex)
		{
			case 0 : return CardDominance.class;
			case 1 : return Integer.class;
			case 2 : return Double.class;
			case 3 : return Double.class;
			case 4 : return Double.class;
			default : return super.getColumnClass(columnIndex);
		}
		
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	
	@Override
	public String getColumnName(int column) {
		return columns[column];
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		
		switch(column)
		{
		case 0: return list.get(row);
		case 1: return list.get(row).getPosition();
		case 2: return list.get(row).getDominance();
		case 3: return list.get(row).getDecksPercent();
		case 4: return list.get(row).getPlayers();
		default : return "";
		}
	}
	
	
}

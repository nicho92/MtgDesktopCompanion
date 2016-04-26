package org.magic.gui.models;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.dashboard.impl.MTGoldFishDashBoard;
import org.magic.api.interfaces.AbstractDashBoard;
import org.magic.api.interfaces.AbstractDashBoard.FORMAT;
import org.magic.api.interfaces.AbstractDashBoard.ONLINE_PAPER;
import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.tools.MagicFactory;

public class CardsShakerTableModel extends DefaultTableModel {

	 static final Logger logger = LogManager.getLogger(CardsShakerTableModel.class.getName());

	String columns[] = new String[]{"Card","Edition","Price","Daily","Daily%","Check"};
			
	AbstractDashBoard provider;
	List<CardShake> list;
	
	
	public CardsShakerTableModel() {
		provider=new MTGoldFishDashBoard();
		provider.setSupportType(ONLINE_PAPER.paper);
		list=new ArrayList<CardShake>();
	}
	
	
	public void init(FORMAT f)
	{
		try {
			list=provider.getShakerFor(f.toString(), "dod");
			
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return columns[column];
	}
	
	@Override
	public int getRowCount() {
		if(list!=null)
			return list.size();
		else
			return 0;
	}
	
	@Override
	public int getColumnCount() {
		return columns.length;
	}
	
	
	public Object getValueAt(int row, int column) {
		try{
			
		CardShake mp = list.get(row);
		switch(column)
		{
			case 0: return mp;
			case 1 : return mp.getEd();
			case 2: return mp.getPrice();
			case 3 : return mp.getPriceDayChange();
			case 4 : return mp.getPercentDChange();
			case 5 : return mp.getPriceDayChange();
		default : return 0;
		}
		}catch(IndexOutOfBoundsException ioob)
		{
			logger.error(ioob);
			return null;
		}
	}

	public boolean isCellEditable(int row, int column) {
		return false;
	}

	
	
}

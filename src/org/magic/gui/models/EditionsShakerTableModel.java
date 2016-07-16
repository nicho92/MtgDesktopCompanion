package org.magic.gui.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.DashBoard;
import org.magic.services.MagicFactory;

public class EditionsShakerTableModel extends DefaultTableModel {

	 static final Logger logger = LogManager.getLogger(EditionsShakerTableModel.class.getName());

	String columns[] = new String[]{"Card","Edition","Price","Daily","Daily%","Weekly","Weekly%"};
			
	DashBoard provider;
	List<CardShake> list;
	
	
	public EditionsShakerTableModel() {
		provider=MagicFactory.getInstance().getEnabledDashBoard();
		list=new ArrayList<CardShake>();
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0: return CardShake.class;
		case 1 : return String.class;
		default : return Double.class;
		
		}
		
		
	}
	
	
	public void init(MagicEdition ed)
	{
		try {
			list=provider.getShakeForEdition(ed);
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
			case 4 : return mp.getPercentDayChange();
			case 5 : return mp.getPriceDayChange();
			case 6 : return mp.getPriceWeekChange();
			case 7 : return mp.getPercentWeekChange();

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

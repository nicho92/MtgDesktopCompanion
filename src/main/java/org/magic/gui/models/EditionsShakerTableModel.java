package org.magic.gui.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.DashBoard;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class EditionsShakerTableModel extends DefaultTableModel {

	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	String[] columns = new String[]{
	
	MTGControler.getInstance().getLangService().getCapitalize("CARD"),
	MTGControler.getInstance().getLangService().getCapitalize("EDITION"),
	MTGControler.getInstance().getLangService().getCapitalize("PRICE"),
	MTGControler.getInstance().getLangService().getCapitalize("DAILY"),
	MTGControler.getInstance().getLangService().getCapitalize("PC_DAILY"),
	MTGControler.getInstance().getLangService().getCapitalize("WEEKLY"),
	MTGControler.getInstance().getLangService().getCapitalize("PC_WEEKLY")
	};
	
	private transient DashBoard provider;
	private transient List<CardShake> list;
	
	
	public EditionsShakerTableModel() {
		provider=MTGControler.getInstance().getEnabledDashBoard();
		list=new ArrayList<>();
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
	
	
	@Override
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

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	
	
}

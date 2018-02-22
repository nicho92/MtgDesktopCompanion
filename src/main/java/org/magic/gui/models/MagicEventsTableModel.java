package org.magic.gui.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicEvent;
import org.magic.services.MTGLogger;

public class MagicEventsTableModel extends DefaultTableModel {

	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private transient List<MagicEvent> list;
	
	
	String[] columns = new String[]{
								"Event",
								"Date"
	};
	
	
	
	public MagicEventsTableModel() {
		list=new ArrayList<>();
	}
	
	
	
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0: return MagicEvent.class;
		case 1 : return Date.class;
		default:return super.getColumnClass(columnIndex);
		}
	}
	
	public void init(List<MagicEvent> l)
	{
		this.list=l;
	
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
			
		MagicEvent mp = list.get(row);
		switch(column)
		{
			case 0: return mp;
			case 1 : return mp.getStartDate();
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

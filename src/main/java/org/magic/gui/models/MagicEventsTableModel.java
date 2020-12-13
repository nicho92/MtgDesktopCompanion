package org.magic.gui.models;

import java.sql.Date;

import org.magic.api.beans.MagicEvent;
import org.magic.api.beans.MagicEvent.EVENT_FORMAT;
import org.magic.api.beans.MagicEvent.ROUNDS;
import org.magic.gui.abstracts.GenericTableModel;

public class MagicEventsTableModel extends GenericTableModel<MagicEvent> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public MagicEventsTableModel() {
		setColumns("Title","Date","Format","Round","Winning round","Round time");
	}
	
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
			case 0: return MagicEvent.class;
			case 1 : return Date.class;
			case 2 : return EVENT_FORMAT.class;
			case 3 : return ROUNDS.class;
			case 4 : return Integer.class;
			case 5 : return Integer.class;
			default: return super.getColumnClass(columnIndex);
		}
	}
	
	
	@Override
	public Object getValueAt(int row, int column) {
		switch (column)
		{
			case 0 : return items.get(row);
			case 1 : return items.get(row).getStartDate();
			case 2 : return items.get(row).getFormat();
			case 3 : return items.get(row).getRoundFormat();
			case 4 : return items.get(row).getMaxWinRound();
			case 5 : return items.get(row).getRoundTime();
			default : return items.get(row);
			
		}
	}
}

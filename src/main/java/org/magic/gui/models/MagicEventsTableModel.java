package org.magic.gui.models;

import org.magic.api.beans.MagicEvent;
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

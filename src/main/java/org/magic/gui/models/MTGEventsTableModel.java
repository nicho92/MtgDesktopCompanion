package org.magic.gui.models;

import org.magic.api.beans.MagicEvent;
import org.magic.gui.abstracts.GenericTableModel;

public class MTGEventsTableModel extends GenericTableModel<MagicEvent> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public MTGEventsTableModel() {
		setColumns("Title","Date","Format","Round","Players");
	}
	
}

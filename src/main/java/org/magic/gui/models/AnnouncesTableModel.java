package org.magic.gui.models;

import org.magic.api.beans.Announce;
import org.magic.gui.abstracts.GenericTableModel;

public class AnnouncesTableModel extends GenericTableModel<Announce> {

	private static final long serialVersionUID = 1L;

		
	public AnnouncesTableModel() {
		setColumns("id","title","startDate","endDate","expirationDate");
	}
}

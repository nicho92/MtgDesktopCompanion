package org.magic.gui.models;

import org.magic.api.beans.MTGNewsContent;
import org.magic.gui.abstracts.GenericTableModel;

public class MagicNewsTableModel extends GenericTableModel<MTGNewsContent> {

	private static final long serialVersionUID = 1L;


	public MagicNewsTableModel() {
		columns=new String[]{ "RSS_TITLE",
				"RSS_DATE",
				"RSS_AUTHOR" };
	}

	@Override
	public Object getValueAt(int row, int column) {
		switch (column) {
		case 0:
			return items.get(row);
		case 1:
			return items.get(row).getDate();
		case 2:
			return items.get(row).getAuthor();
		default:
			return "";
		}
	}

}

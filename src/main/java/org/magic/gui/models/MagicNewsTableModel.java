package org.magic.gui.models;

import java.util.Date;

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
	public Class<?> getColumnClass(int columnIndex) {
		
		switch(columnIndex)
		{
		case  0 : return MTGNewsContent.class;
		case 1 : return Date.class;
		case 2 : return String.class;
		
		default : return super.getColumnClass(columnIndex);
		}
			
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

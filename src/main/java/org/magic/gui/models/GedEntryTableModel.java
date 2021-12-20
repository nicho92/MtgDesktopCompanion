package org.magic.gui.models;

import org.magic.api.beans.GedEntry;
import org.magic.api.interfaces.MTGStorable;
import org.magic.gui.abstracts.GenericTableModel;

public class GedEntryTableModel extends GenericTableModel<GedEntry<MTGStorable>> {

	private static final long serialVersionUID = 1L;

	public GedEntryTableModel() {
		setColumns("name","id","classe","isImage","size");
	}
	
	
	@Override
	public Object getValueAt(int row, int column) {
		if(column==0)
			return items.get(row);
		
		if(column==3)
			return  items.get(row).isImage();	
		
		if(column==4)
			return  items.get(row).getLength();
			
		return super.getValueAt(row, column);
	}
	
	
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		switch (columnIndex)
		{
		case 0 : return GedEntry.class;
		case 2 : return Class.class;
		case 3 : return Boolean.class;
		case 4 : return Long.class;
		default : return super.getColumnClass(columnIndex);
		}
		
	}
	
}

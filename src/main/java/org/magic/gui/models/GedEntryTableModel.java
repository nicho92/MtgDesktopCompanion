package org.magic.gui.models;

import org.magic.api.beans.GedEntry;
import org.magic.api.interfaces.MTGStorable;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.tools.UITools;

public class GedEntryTableModel extends GenericTableModel<GedEntry<MTGStorable>> {

	private static final long serialVersionUID = 1L;

	public GedEntryTableModel() {
		setColumns("name","id","classe","isImage","size");
	}
	
	
	@Override
	public Object getValueAt(int row, int column) {
		if(column==4)
			return  UITools.humanReadableSize(items.get(row).getLength());
	
		if(column==3)
			return  items.get(row).isImage();	
			
		return super.getValueAt(row, column);
	}
	
	
}

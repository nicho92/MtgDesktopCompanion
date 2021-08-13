package org.magic.gui.models;

import org.magic.api.beans.ConverterItem;
import org.magic.gui.abstracts.GenericTableModel;

public class ConverterItemsTableModel extends GenericTableModel<ConverterItem> {

	private static final long serialVersionUID = 1L;
	
	public ConverterItemsTableModel() {
		setColumns("name","lang","source","inputId","destination","outputId");
		setWritable(true);
	}
	
	@Override
	public void setValueAt(Object val, int row, int column) {
		ConverterItem it = items.get(row);
		switch (column)
		{
			case 0:it.setName(val.toString());break;
			case 1:it.setLang(val.toString());break;
			case 2:it.setSource(val.toString());break;
			case 3:it.setInputId(Integer.parseInt(val.toString()));break;
			case 4:it.setDestination(val.toString());break;
			case 5:it.setOutputId(Integer.parseInt(val.toString()));break;
			default : return;
		}
	}
}

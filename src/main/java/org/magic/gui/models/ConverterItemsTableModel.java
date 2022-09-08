package org.magic.gui.models;

import org.magic.api.beans.technical.ConverterItem;
import org.magic.gui.abstracts.GenericTableModel;

public class ConverterItemsTableModel extends GenericTableModel<ConverterItem> {

	private static final long serialVersionUID = 1L;
	
	public ConverterItemsTableModel() {
		setColumns("id","name","source","inputId","destination","outputId");
		setWritable(true);
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		
		if(column==0)
			return items.get(row);
			
		return super.getValueAt(row, column);
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return writable && column>0;
	}
	
	@Override
	public void setValueAt(Object val, int row, int column) {
		ConverterItem it = items.get(row);
		switch (column)
		{
			case 1:it.setName(val.toString());break;
			case 2:it.setSource(val.toString());break;
			case 3:it.setInputId(Integer.parseInt(val.toString()));break;
			case 4:it.setDestination(val.toString());break;
			case 5:it.setOutputId(Integer.parseInt(val.toString()));break;
			default : return;
		}
		it.setUpdated(true);
		
	}
	
	
}

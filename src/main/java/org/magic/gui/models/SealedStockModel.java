package org.magic.gui.models;

import org.magic.api.beans.SealedStock;
import org.magic.gui.abstracts.GenericTableModel;

public class SealedStockModel extends GenericTableModel<SealedStock> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SealedStockModel() {
		setColumns("ID","Type","Edition","Quality","Qty");
	}
	
	
	
	@Override
	public Object getValueAt(int row, int column) {
		
		SealedStock it = items.get(row);
		
		switch(column)
		{
			case 0: return it;
			case 1: return it.getProduct().getType();
			case 2: return it.getProduct().getEdition();
			case 3: return it.getCondition();
			case 4 : return it.getQte();
			default : return super.getValueAt(row, column);
		}
		
	}
	
	
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		
		SealedStock it = items.get(row);
		
		switch(column)
		{
			case 4: it.setQte(Integer.parseInt(String.valueOf(aValue)));break;
		}
		
		
	}
}

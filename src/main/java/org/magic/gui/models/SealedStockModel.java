package org.magic.gui.models;

import org.magic.api.beans.EnumStock;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Packaging;
import org.magic.api.beans.SealedStock;
import org.magic.gui.abstracts.GenericTableModel;

public class SealedStockModel extends GenericTableModel<SealedStock> {

	
	private static final long serialVersionUID = 1L;

	public SealedStockModel() {
		setColumns("ID","Type","Edition","LANGUAGE","Quality","Qty");
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:return Packaging.class;
		case 1: return Packaging.TYPE.class;
		case 2: return MagicEdition.class;
		case 3: return String.class;
		case 4: return EnumStock.class;
		case 5: return Integer.class;
		default: return super.getColumnClass(columnIndex);
		}
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		
		SealedStock it = items.get(row);
		
		switch(column)
		{
			case 0: return it;
			case 1: return it.getProduct().getType();
			case 2: return it.getProduct().getEdition();
			case 3: return it.getProduct().getLang();
			case 4: return it.getCondition();
			case 5 : return it.getQte();
			default : return super.getValueAt(row, column);
		}
	}
	
	
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		
		SealedStock it = items.get(row);
		
		switch(column)
		{
			case 3: it.getProduct().setLang(String.valueOf(aValue));break;
			case 4: it.setCondition(EnumStock.valueOf(aValue.toString()));break;
			case 5: it.setQte(Integer.parseInt(String.valueOf(aValue)));break;
			default:break;
		}
		
		
	}
}

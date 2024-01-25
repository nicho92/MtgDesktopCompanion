package org.magic.gui.models;

import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGSealedStock;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExtra;
import org.magic.api.beans.enums.EnumItems;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.services.tools.UITools;

public class SealedStockTableModel extends GenericTableModel<MTGSealedStock> {


	private static final long serialVersionUID = 1L;

	public SealedStockTableModel() {
		setColumns("ID","Type","Extra","Edition","LANGUAGE","Quality","Qty","Collection","Price");
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:return MTGSealedProduct.class;
		case 1: return EnumItems.class;
		case 2: return EnumExtra.class;
		case 3: return MTGEdition.class;
		case 4: return String.class;
		case 5: return EnumCondition.class;
		case 6: return Integer.class;
		case 7: return MTGCollection.class;
		case 8: return Double.class;
		default: return super.getColumnClass(columnIndex);
		}
	}

	@Override
	public Object getValueAt(int row, int column) {
		MTGSealedStock it = items.get(row);
		
		switch(column)
		{
			case 0: return it;
			case 1: return it.getProduct().getTypeProduct();
			case 2 : return it.getProduct().getExtra();
			case 3: return it.getProduct().getEdition();
			case 4: return it.getLanguage();
			case 5: return it.getCondition();
			case 6 : return it.getQte();
			case 7 : return it.getMagicCollection();
			case 8 : return it.getPrice();
			default : return super.getValueAt(row, column);
		}
	}

	
	@Override
	public boolean isCellEditable(int row, int column) {
		return column>3;
	}
	

	@Override
	public void setValueAt(Object aValue, int row, int column) {

		MTGSealedStock it = items.get(row);

		switch(column)
		{
			case 4: it.setLanguage(String.valueOf(aValue));break;
			case 5: it.setCondition(EnumCondition.valueOf(aValue.toString()));break;
			case 6: it.setQte(Integer.parseInt(String.valueOf(aValue)));break;
			case 7: it.setMagicCollection(new MTGCollection(String.valueOf(aValue)));break;
			case 8: it.setPrice(UITools.parseDouble(aValue.toString()));break;
			default: break;
		}

		it.setUpdated(true);

	}
}

package org.magic.gui.models;

import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.MTGSealedProduct.EXTRA;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.SealedStock;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.tools.UITools;

public class SealedStockTableModel extends GenericTableModel<SealedStock> {

	
	private static final long serialVersionUID = 1L;

	public SealedStockTableModel() {
		setColumns("ID","Type","Extra","Edition","LANGUAGE","Quality","Qty","Collection","Price");
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:return MTGSealedProduct.class;
		case 1: return EnumItems.class;
		case 2: return EXTRA.class;
		case 3: return MagicEdition.class;
		case 4: return String.class;
		case 5: return EnumCondition.class;
		case 6: return Integer.class;
		case 7: return MagicCollection.class;
		case 8: return Double.class;
		default: return super.getColumnClass(columnIndex);
		}
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		SealedStock it = items.get(row);
		switch(column)
		{
			case 0: return it;
			case 1: return it.getProduct().getTypeProduct();
			case 2 : return it.getProduct().getExtra();
			case 3: return it.getProduct().getEdition();
			case 4: return it.getProduct().getLang();
			case 5: return it.getCondition();
			case 6 : return it.getQte();
			case 7 : return it.getMagicCollection();
			case 8 : return it.getPrice();
			default : return super.getValueAt(row, column);
		}
	}
	
	
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		
		SealedStock it = items.get(row);
		
		switch(column)
		{
			case 4: it.getProduct().setLang(String.valueOf(aValue));break;
			case 5: it.setCondition(EnumCondition.valueOf(aValue.toString()));break;
			case 6: it.setQte(Integer.parseInt(String.valueOf(aValue)));break;
			case 7: it.setMagicCollection(new MagicCollection(String.valueOf(aValue)));break;
			case 8: it.setPrice(UITools.parseDouble(aValue.toString()));break;
			default: break;
		}
		
		it.setUpdated(true);
		
	}
}

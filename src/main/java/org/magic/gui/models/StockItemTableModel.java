package org.magic.gui.models;

import java.util.Map;

import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.interfaces.MTGProduct;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.tools.UITools;

public class StockItemTableModel extends GenericTableModel<MTGStockItem> {
	
	private static final long serialVersionUID = 1L;

	public StockItemTableModel() {
		setWritable(true);
		columns = new String[] { "ID",
				"PRODUCT",
				"EDITION",
				"LANGUAGE",
				"COLLECTION",
				"TYPE",
				"FOIL",
				"ALTERED",
				"SIGNED",
				"QTY",
				"PRICE",
				"COMMENT",
				"IDS"
			};
	}
	
	@Override
	public void addItem(MTGStockItem t) {
		if(t.getId()==-1)
		{
			items.add(t);
		}
		else
		{
			for(var i=0;i<=items.size();i++)
			{
				if(items.get(i).getId().equals(t.getId()))
				{
					items.set(i, t);
					break;
				}
			}
		}
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return MTGStockItem.class;
		case 1:
			return MTGProduct.class;
		case 2:
			return MagicEdition.class;
		case 3:
			return String.class;
		case 4:
			return MagicCollection.class;
		case 5:
			return EnumItems.class;
		case 6:
			return Boolean.class;
		case 7:
			return Boolean.class;
		case 8:
			return Boolean.class;
		case 9:
			return Integer.class;
		case 10:
			return Double.class;
		case 11:
			return String.class;
		case 12:
			return Map.class;
			
		default:
			return super.getColumnClass(columnIndex);
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		
		if(writable)
			return (column ==9 || column==10);
		else
			return false;
	}

	@Override
	public Object getValueAt(int row, int column) {

		switch (column) {
		case 0:
			return items.get(row);
		case 1:
			return items.get(row).getProduct();
		case 2:
			return items.get(row).getProduct().getEdition();
		case 3 :
			return items.get(row).getLanguage();
		case 4:
			return items.get(row).getMagicCollection();
		case 5:
			return items.get(row).getProduct().getTypeProduct();
		case 6:
			return items.get(row).isFoil();
		case 7:
			return items.get(row).isAltered();
		case 8:
			return items.get(row).isSigned();
		case 9:
			return items.get(row).getQte();
		case 10:
			return UITools.roundDouble(items.get(row).getPrice());
		case 11:
			return items.get(row).getComment();
		case 12:
			return items.get(row).getTiersAppIds();

		default:
			return "";
		}
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		
		switch (column) {
		case 9:
			items.get(row).setQte((Integer) aValue);
			break;
		case 10:
			items.get(row).setPrice(Double.parseDouble(aValue.toString()));
			break;
			
		default:
			break;
		}
		
		items.get(row).setUpdated(true);

		
	}


}

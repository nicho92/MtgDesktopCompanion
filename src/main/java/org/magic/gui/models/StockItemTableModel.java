package org.magic.gui.models;

import java.util.Map;

import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.technical.MoneyValue;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.extra.MTGProduct;
import org.magic.gui.abstracts.GenericTableModel;

public class StockItemTableModel extends GenericTableModel<MTGStockItem> {

	private static final long serialVersionUID = 1L;

	public StockItemTableModel() {
		setWritable(true);
		setColumns("ID",
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
				"CONDITION",
				"IDS"
			);
	}

	@Override
	public void addItem(MTGStockItem t) {
		if(t.getId()==-1 || items.isEmpty())
		{
			items.add(t);
		}
		else
		{
			
			var index = items.indexOf(t);
			if(index>-1)
			{
				items.get(index).setQte(items.get(index).getQte()+t.getQte());
				items.get(index).setUpdated(true);
			}
			else
			{
				items.add(t);
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
			return MTGEdition.class;
		case 3:
			return String.class;
		case 4:
			return MTGCollection.class;
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
			return MoneyValue.class;
		case 11:
			return String.class;
		case 12:
			return EnumCondition.class;
		case 13:
			return Map.class;

		default:
			return super.getColumnClass(columnIndex);
		}
	}

	@Override
	public boolean isCellEditable(int row, int column) {

		if(isWritable())
			return column>1;
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
			return items.get(row).getProduct()!=null?items.get(row).getProduct().getEdition():null;
		case 3 :
			return items.get(row).getLanguage();
		case 4:
			return items.get(row).getMagicCollection();
		case 5:
			return items.get(row).getProduct()!=null?items.get(row).getProduct().getTypeProduct():null;
		case 6:
			return items.get(row).isFoil();
		case 7:
			return items.get(row).isAltered();
		case 8:
			return items.get(row).isSigned();
		case 9:
			return items.get(row).getQte();
		case 10:
			return items.get(row).getValue();
		case 11:
			return items.get(row).getComment();
		case 12:
			return items.get(row).getCondition();
		case 13:
			return items.get(row).getTiersAppIds();

		default:
			return "";
		}
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {

		switch (column) {
		case 2:
			items.get(row).getProduct().setEdition((MTGEdition)aValue);
			break;
		case 3:
			items.get(row).setLanguage(String.valueOf(aValue));
			break;
		case 4:
			items.get(row).setMagicCollection((MTGCollection)aValue);
			break;
		case 5:
			items.get(row).getProduct().setTypeProduct((EnumItems)aValue);
			break;
		case 6:
			items.get(row).setFoil(Boolean.parseBoolean(aValue.toString()));
			break;
		case 7:
			items.get(row).setAltered(Boolean.parseBoolean(aValue.toString()));
			break;
		case 8:
			items.get(row).setSigned(Boolean.parseBoolean(aValue.toString()));
			break;
		case 9:
			items.get(row).setQte((Integer) aValue);
			break;
		case 10:
			items.get(row).setPrice(Double.parseDouble(aValue.toString()));
			break;
		case 11:
			items.get(row).setComment(String.valueOf(aValue));
			break;
		case 12:
			items.get(row).setCondition((EnumCondition)aValue);
			break;
		default:
			break;
		}

		items.get(row).setUpdated(true);
		
		fireTableRowsUpdated(row, row);
	}


}

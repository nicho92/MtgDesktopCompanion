package org.magic.gui.models;

import java.net.URL;
import java.util.Date;

import org.magic.api.beans.ShopItem;
import org.magic.gui.abstracts.GenericTableModel;

public class ShopItemTableModel extends GenericTableModel<ShopItem> {

	private static final long serialVersionUID = 1L;

	

	public ShopItemTableModel() {
		 columns = new String[] { "WEBSITE",
					"SHOP_NAME",
					"PRICE",
					"SHOP_DATE",
					"SHOP_TYPE",
					"URL" };
	}


	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.class;
		case 1:
			return ShopItem.class;
		case 2:
			return Double.class;
		case 3:
			return Date.class;
		case 4:
			return String.class;
		case 5:
			return URL.class;
		default:
			return String.class;
		}
	}

	@Override
	public Object getValueAt(int row, int column) {
		try {

			ShopItem mp = items.get(row);

			switch (column) {
			case 0:
				return mp.getShopName();
			case 1:
				return mp;
			case 2:
				return mp.getPrice();
			case 3:
				return mp.getDate();
			case 4:
				return mp.getType();
			case 5:
				return mp.getUrl();
			default:
				return 0;
			}
		} catch (Exception ioob) {
			logger.error(ioob);
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return (column == 6);
	}

}

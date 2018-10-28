package org.magic.gui.models;

import java.net.URL;
import java.util.Currency;

import org.magic.api.beans.MagicPrice;
import org.magic.gui.abstracts.GenericTableModel;

public class CardsPriceTableModel extends GenericTableModel<MagicPrice> {

	private static final long serialVersionUID = 1L;
	public static final int COLUMUM_URL = 7;



	public CardsPriceTableModel() {
		columns=new String[] { "WEBSITE",
				"PRICE",
				"CURRENCY",
				"SELLER",
				"QUALITY",
				"CARD_LANGUAGE",
				"COUNTRY",
				"URL"};

	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.class;
		case 1:
			return Double.class;
		case 2:
			return Currency.class;
		case 3:
			return String.class;
		case 4:
			return String.class;
		case 5:
			return String.class;
		case 6:
			return String.class;
		default:
			return URL.class;
		}
	}

	@Override
	public Object getValueAt(int row, int column) {
		try {

			MagicPrice mp = items.get(row);

			switch (column) {
			case 0:
				return mp.getSite();
			case 1:
				return mp.getValue();
			case 2:
				return mp.getCurrency();
			case 3:
				return mp.getSeller();
			case 4:
				return mp.getQuality();
			case 5:
				return mp.getLanguage();
			case 6:
				return mp.getCountry();
			case 7:
				return mp.getUrl();
			default:
				return 0;
			}
		} catch (IndexOutOfBoundsException ioob) {
			return null;
		}
	}

}

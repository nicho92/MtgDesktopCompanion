package org.magic.gui.models;

import java.net.URL;
import java.util.Currency;

import org.magic.api.beans.MagicPrice;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.tools.UITools;

public class CardsPriceTableModel extends GenericTableModel<MagicPrice> {

	private static final long serialVersionUID = 1L;
	public static final int COLUMUM_URL = 8;



	public CardsPriceTableModel() {
		columns=new String[] { 
				"WEBSITE",
				"PRICE",
				"CURRENCY",
				"SELLER",
				"QUALITY",
				"FOIL",
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
			return Boolean.class;
		case 6:
			return String.class;
		case 7:
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
				return UITools.roundDouble(mp.getValue());
			case 2:
				return mp.getCurrency();
			case 3:
				return mp.getSeller();
			case 4:
				return mp.getQuality();
			case 5:
				return mp.isFoil();
			case 6:
				return mp.getLanguage();
			case 7:
				return mp.getCountry();
			case 8:
				return mp.getUrl();
			default:
				return 0;
			}
		} catch (IndexOutOfBoundsException ioob) {
			return null;
		}
	}

}

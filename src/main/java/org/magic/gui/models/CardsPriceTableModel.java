package org.magic.gui.models;

import java.net.URL;
import java.util.Currency;

import org.magic.api.beans.MagicPrice;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.tools.UITools;

public class CardsPriceTableModel extends GenericTableModel<MagicPrice> {

	private static final long serialVersionUID = 1L;


	public CardsPriceTableModel() {
		columns=new String[] { 
				"CARD",
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
	public int[] defaultHiddenColumns() {
		return new int[] {0};
	}
	

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return MagicPrice.class;
		case 1:
			return String.class;
		case 2:
			return Double.class;
		case 3:
			return Currency.class;
		case 4:
			return String.class;
		case 5:
			return String.class;
		case 6:
			return Boolean.class;
		case 7:
			return String.class;
		case 8:
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
				return mp;
			case 1:
				return mp.getSite();
			case 2:
				return UITools.roundDouble(mp.getValue());
			case 3:
				return mp.getCurrency();
			case 4:
				return mp.getSeller();
			case 5:
				return mp.getQuality();
			case 6:
				return mp.isFoil();
			case 7:
				return mp.getLanguage();
			case 8:
				return mp.getCountry();
			case 9:
				return mp.getUrl();
			default:
				return 0;
			}
		} catch (IndexOutOfBoundsException ioob) {
			return null;
		}
	}

}

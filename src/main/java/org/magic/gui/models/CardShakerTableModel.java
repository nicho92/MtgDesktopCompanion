package org.magic.gui.models;

import org.magic.api.beans.CardShake;
import org.magic.api.beans.enums.MTGCardVariation;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.services.tools.UITools;

public class CardShakerTableModel extends GenericTableModel<CardShake> {

	private static final long serialVersionUID = 1L;

	public CardShakerTableModel() {
		columns = new String[] { "CARD",
				"EDITION",
				"PRICE",
				"DAILY",
				"PC_DAILY",
				"WEEKLY",
				"PC_WEEKLY",
				"FOIL",
				"LAYOUT"};
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {

		switch (columnIndex) {
			case 0:
				return CardShake.class;
			case 1:
				return String.class;
			case 7:
				return Boolean.class;
			case 8:
				return MTGCardVariation.class;

		default:
			return Double.class;

		}

	}

	@Override
	public Object getValueAt(int row, int column) {
		try {
			CardShake mp = items.get(row);

			switch (column) {
			case 0:
				return mp;
			case 1:
				return mp.getEd();
			case 2:
				return UITools.roundDouble(mp.getPrice());
			case 3:
				return UITools.roundDouble(mp.getPriceDayChange());
			case 4:
				return UITools.roundDouble(mp.getPercentDayChange());
			case 5:
				return UITools.roundDouble(mp.getPriceWeekChange());
			case 6:
				return UITools.roundDouble(mp.getPercentWeekChange());
			case 7: return mp.isFoil();
			case 8: return mp.getCardVariation();


			default:return 0;
			}
		} catch (IndexOutOfBoundsException ioob) {
			logger.error(ioob);
			return null;
		}
	}

}

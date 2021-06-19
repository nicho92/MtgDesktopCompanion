package org.magic.gui.models;

import org.magic.api.beans.CardShake;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.tools.UITools;

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
				return String.class;
			
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
			case 8: return getLayout(mp);
				

			default:return 0;
			}
		} catch (IndexOutOfBoundsException ioob) {
			logger.error(ioob);
			return null;
		}
	}

	private String getLayout(CardShake mp) {
		
		StringBuilder temp = new StringBuilder();
		
		
		if(mp.isShowcase())
			return "showcase";
		else if(mp.isExtendedArt())
			return "extendedArt";
		else if(mp.isBorderless())
			return "borderless";
		else if(mp.isFullArt())
			return "Full Art";
		else if(mp.isTimeshifted())
			return "TimeShifted";
			else return "";
	}


}

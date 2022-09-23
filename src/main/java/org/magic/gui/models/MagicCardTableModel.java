package org.magic.gui.models;

import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.services.MTGControler;

public class MagicCardTableModel extends GenericTableModel<MagicCard> {

	private static final long serialVersionUID = 1L;

	public MagicCardTableModel() {
		columns=new String[] {
				"CARD_NAME",
				"CARD_LANGUAGE",
				"CARD_MANA",
				"CARD_TYPES",
				"CARD_POWER",
				"CARD_RARITY",
				"CARD_EDITIONS",
				"CARD_NUMBER",
				"CARD_COLOR",
				"RESERVED LIST",
				"LAYOUT",
				"SHOWCASE",
				"EXTENDED ART",
				"BORDERLESS",
				"TIMESHIFTED",
				"SIDE"};

		setDefaultHiddenComlumns(1,8,9,10,11,12,13,14,15);

	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		switch(columnIndex)
		{
			case 0: return MagicCard.class;
			case 6: return List.class;
			case 8: return List.class;
			case 9: return Boolean.class;
			case 11: return Boolean.class;
			case 12: return Boolean.class;
			case 13: return Boolean.class;
			case 14: return Boolean.class;
			default:return String.class;
		}
	}

	@Override
	public Object getValueAt(int row, int column) {
		try {
			MagicCard mc = items.get(row);
			switch (column) {
			case 0:
				return mc;
			case 1:
				return getName(mc.getForeignNames());
			case 2:
				return mc.getCost();
			case 3:
				return mc.getFullType();
			case 4:
				return powerorloyalty(mc);
			case 5:
				return (mc.getRarity() != null) ? mc.getRarity().toPrettyString() : "";
			case 6:
				return mc.getEditions();
			case 7:
				return (mc.getCurrentSet() != null) ? mc.getCurrentSet().getNumber() : "";
			case 8:
				return mc.getColors();
			case 9:
				return mc.isReserved();
			case 10:
				return mc.getLayout().toPrettyString();
			case 11:
				return mc.isShowCase();
			case 12:
				return mc.isExtendedArt();
			case 13:
				return mc.isBorderLess();
			case 14:
				return mc.isTimeshifted();
			case 15:
				return mc.getSide();
			default:
				return mc;
			}
		} catch (Exception e) {
			return null;
		}

	}

	private String powerorloyalty(MagicCard mc) {

		if(mc.isCreature())
			return mc.getPower() + "/" + mc.getToughness();
		else if(mc.isPlaneswalker())
			return String.valueOf(mc.getLoyalty());

		return "";
	}

	private String getName(List<MagicCardNames> foreignNames) {
		for (MagicCardNames name : foreignNames) {
			if (name.getLanguage().equals(MTGControler.getInstance().get("langage")))
				return name.getName();
		}
		return "";
	}


}

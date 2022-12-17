package org.magic.gui.models;

import java.util.Map;
import java.util.Objects;

import org.magic.api.beans.Grading;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.MTGRarity;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.services.tools.UITools;

public class CardStockTableModel extends GenericTableModel<MagicCardStock> {

	private static final long serialVersionUID = 1L;

	public CardStockTableModel() {
		setWritable(true);
		columns = new String[] { "ID",
				"CARD",
				"EDITION",
				"RARITY",
				"COLLECTION",
				"QUALITY",
				"QTY",
				"CARD_LANGUAGE",
				"FOIL",
				"ETCHED",
				"SIGNED",
				"ALTERED",
				"PRICE",
				"GRADED",
				"COMMENTS",
				"SYNC"};
	}

	@Override
	public void addItem(MagicCardStock t) {
		if(t.getId()==-1)
		{
			items.add(t);
		}
		else
		{
			items.removeIf(e -> t.getId().equals(e.getId()));
			items.add(t);
		}
	}


	@Override
	public int[] defaultHiddenColumns() {
		return new int[] {1,2};
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return MagicCardStock.class;
		case 1:
			return MagicCard.class;
		case 2:
			return MagicEdition.class;
		case 3:
			return MTGRarity.class;
		case 4:
			return MagicCollection.class;
		case 5:
			return EnumCondition.class;
		case 6:
			return Integer.class;
		case 7:
			return String.class;
		case 8:
			return Boolean.class;
		case 9:
			return Boolean.class;
		case 10:
			return Boolean.class;
		case 11:
			return Boolean.class;
		case 12:
			return Double.class;
		case 13:
			return Grading.class;
		case 14:
			return String.class;
		case 15:
			return Map.class;


		default:
			return super.getColumnClass(columnIndex);
		}
	}

	@Override
	public boolean isCellEditable(int row, int column) {

		if(writable)
			return !(column ==1 || column==13 || column==2|| column==3);
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
			return (Objects.isNull(items.get(row).getProduct()) ? "" :  items.get(row).getProduct().getCurrentSet());
		case 3:
			return (Objects.isNull(items.get(row).getProduct()) ? "" :  items.get(row).getProduct().getRarity());
		case 4:
			return items.get(row).getMagicCollection();
		case 5:
			return items.get(row).getCondition();
		case 6:
			return items.get(row).getQte();
		case 7:
			return items.get(row).getLanguage();
		case 8:
			return items.get(row).isFoil();
		case 9:
			return items.get(row).isEtched();
		case 10:
			return items.get(row).isSigned();
		case 11:
			return items.get(row).isAltered();
		case 12:
			return UITools.roundDouble(items.get(row).getPrice());
		case 13:
			return items.get(row).getGrade();
		case 14:
			return items.get(row).getComment();
		case 15:
			return items.get(row).getTiersAppIds();

		default:
			return "";
		}
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {

		switch (column) {
		case 4:
			items.get(row).setMagicCollection(new MagicCollection(aValue.toString()));
			break;
		case 5:
			items.get(row).setCondition((EnumCondition) aValue);
			break;
		case 6:
			items.get(row).setQte((Integer) aValue);
			break;
		case 7:
			items.get(row).setLanguage(String.valueOf(aValue));
			break;
		case 8:
			items.get(row).setFoil(Boolean.parseBoolean(aValue.toString()));
			break;
		case 9:
			items.get(row).setEtched(Boolean.parseBoolean(aValue.toString()));
			break;
		case 10:
			items.get(row).setSigned(Boolean.parseBoolean(aValue.toString()));
			break;
		case 11:
			items.get(row).setAltered(Boolean.parseBoolean(aValue.toString()));
			break;
		case 12:
			items.get(row).setPrice(Double.valueOf(String.valueOf(aValue)));
			break;
		case 13:
			items.get(row).setGrade((Grading)aValue);
			break;
		case 14:
			items.get(row).setComment(String.valueOf(aValue));
			break;

		default:
			break;
		}
		items.get(row).setUpdated(true);


	}


}

package org.magic.gui.models;

import java.util.Map;

import org.magic.api.beans.Grading;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.services.MTGControler;
import org.magic.tools.UITools;

public class CardStockTableModel extends GenericTableModel<MagicCardStock> {
	
	private static final long serialVersionUID = 1L;

	public CardStockTableModel() {
		columns = new String[] { "ID",
				"CARD",
				"EDITION",
				"COLLECTION",
				"QUALITY",
				"QTY",
				"CARD_LANGUAGE",
				"FOIL",
				"SIGNED",
				"ALTERED",
				"PRICE",
				"GRADED",
				"COMMENTS",
				"SYNC"};
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
			return MagicCollection.class;
		case 4:
			return EnumCondition.class;
		case 5:
			return Integer.class;
		case 6:
			return String.class;
		case 7:
			return Boolean.class;
		case 8:
			return Boolean.class;
		case 9:
			return Boolean.class;
		case 10:
			return Double.class;
		case 11:
			return Grading.class;
		case 12:
			return String.class;
		case 13:
			return Map.class;


		default:
			return super.getColumnClass(columnIndex);
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return !(column ==1 || column==11 || column==2);
	}

	@Override
	public Object getValueAt(int row, int column) {

		switch (column) {
		case 0:
			return items.get(row);
		case 1:
			return items.get(row).getMagicCard();
		case 2:
			return items.get(row).getMagicCard().getCurrentSet();
		case 3:
			return items.get(row).getMagicCollection();
		case 4:
			return items.get(row).getCondition();
		case 5:
			return items.get(row).getQte();
		case 6:
			return items.get(row).getLanguage();
		case 7:
			return items.get(row).isFoil();
		case 8:
			return items.get(row).isSigned();
		case 9:
			return items.get(row).isAltered();
		case 10:
			return UITools.roundDouble(items.get(row).getPrice());
		case 11:
			return items.get(row).getGrade();
		case 12:
			return items.get(row).getComment();
		case 13:
			return items.get(row).getTiersAppIds();

		default:
			return "";
		}
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		
		switch (column) {
		case 3:
			items.get(row).setMagicCollection(new MagicCollection(aValue.toString()));
			break;
		case 4:
			items.get(row).setCondition((EnumCondition) aValue);
			break;
		case 5:
			items.get(row).setQte((Integer) aValue);
			break;
		case 6:
			items.get(row).setLanguage(String.valueOf(aValue));
			break;
		case 7:
			items.get(row).setFoil(Boolean.parseBoolean(aValue.toString()));
			break;
		case 8:
			items.get(row).setSigned(Boolean.parseBoolean(aValue.toString()));
			break;
		case 9:
			items.get(row).setAltered(Boolean.parseBoolean(aValue.toString()));
			break;
		case 10:
			items.get(row).setPrice(Double.valueOf(String.valueOf(aValue)));
			break;
		case 11:
			items.get(row).setGrade((Grading)aValue);
			break;
		case 12:
			items.get(row).setComment(String.valueOf(aValue));
			break;
			
		default:
			break;
		}
		items.get(row).setUpdate(true);

		
	}

	public void updateEdition(MagicCardStock magicCardStock, MagicEdition aValue) {
		
		try {
			if(!magicCardStock.getMagicCard().getCurrentSet().equals(aValue))
			{
				MTGControler.getInstance().getEnabled(MTGDao.class).deleteStock(magicCardStock);
				magicCardStock.setMagicCard(MTGControler.getInstance().switchEditions(magicCardStock.getMagicCard(), aValue));
				magicCardStock.setIdstock(-1);
				
				MTGControler.getInstance().getEnabled(MTGDao.class).saveOrUpdateStock(magicCardStock);
				
				
			}
			}
			catch(Exception e)
			{
				logger.error("Error update edition for " + magicCardStock,e);
			}

	}


}

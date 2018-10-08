package org.magic.gui.models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGControler;
import org.utils.patterns.observer.Observer;

public class CardStockTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;
	
	private transient List<MagicCardStock> list;

	static final String[] columns = new String[] { "ID",
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
			"COMMENTS" };

	public List<MagicCardStock> getList() {
		return list;
	}

	public void removeRows(List<MagicCardStock> stocks) throws SQLException {
		list.removeAll(stocks);
		for (Iterator<MagicCardStock> iter = stocks.listIterator(); iter.hasNext();) {
			MagicCardStock a = iter.next();
			if (a.getIdstock() == -1) {
				iter.remove();
			}
		}
		if (!stocks.isEmpty())
			MTGControler.getInstance().getEnabled(MTGDao.class).deleteStock(stocks);

		fireTableDataChanged();
	}

	public void init() throws SQLException {
		list.clear();
		list.addAll(MTGControler.getInstance().getEnabled(MTGDao.class).listStocks());
		fireTableDataChanged();
	}

	public CardStockTableModel() {
		list = new ArrayList<>();

	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public int getRowCount() {
		if (list != null)
			return list.size();

		return 0;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return MagicCardStock.class;
		case 1:
			return MagicCard.class;
		case 2:
			return List.class;
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
			return String.class;

		default:
			return super.getColumnClass(columnIndex);
		}

	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return column > 1;
	}

	@Override
	public String getColumnName(int column) {
		return MTGControler.getInstance().getLangService().getCapitalize(columns[column]);
	}

	@Override
	public Object getValueAt(int row, int column) {

		switch (column) {
		case 0:
			return list.get(row);
		case 1:
			return list.get(row).getMagicCard();
		case 2:
			return list.get(row).getMagicCard().getEditions();
		case 3:
			return list.get(row).getMagicCollection();
		case 4:
			return list.get(row).getCondition();
		case 5:
			return list.get(row).getQte();
		case 6:
			return list.get(row).getLanguage();
		case 7:
			return list.get(row).isFoil();
		case 8:
			return list.get(row).isSigned();
		case 9:
			return list.get(row).isAltered();
		case 10:
			return list.get(row).getPrice();
		case 11:
			return list.get(row).getComment();

		default:
			return "";
		}
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		switch (column) {
		case 2:
			updateEdition(list.get(row), (MagicEdition) aValue);
			break;
		case 3:
			list.get(row).setMagicCollection(new MagicCollection(aValue.toString()));
			break;
		case 4:
			list.get(row).setCondition((EnumCondition) aValue);
			break;
		case 5:
			list.get(row).setQte((Integer) aValue);
			break;
		case 6:
			list.get(row).setLanguage(String.valueOf(aValue));
			break;
		case 7:
			list.get(row).setFoil(Boolean.parseBoolean(aValue.toString()));
			break;
		case 8:
			list.get(row).setSigned(Boolean.parseBoolean(aValue.toString()));
			break;
		case 9:
			list.get(row).setAltered(Boolean.parseBoolean(aValue.toString()));
			break;
		case 10:
			list.get(row).setPrice(Double.valueOf(String.valueOf(aValue)));
			break;
		case 11:
			list.get(row).setComment(String.valueOf(aValue));
			break;
		default:
			break;
		}
		list.get(row).setUpdate(true);
	}

	private void updateEdition(MagicCardStock magicCardStock, MagicEdition aValue) {
		
		if(!magicCardStock.getMagicCard().getCurrentSet().equals(aValue))
		{
			
			MagicEdition ed = aValue;
			magicCardStock.setMagicCard(MTGControler.getInstance().switchEditions(magicCardStock.getMagicCard(), ed));
			magicCardStock.getMagicCard().getEditions().remove(ed);
			magicCardStock.getMagicCard().getEditions().add(0, (MagicEdition) aValue);
			
		}

	}

	public void add(MagicCardStock selected) {
		list.add(selected);
		fireTableDataChanged();

	}

}

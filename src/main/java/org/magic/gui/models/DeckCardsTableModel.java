package org.magic.gui.models;

import static org.magic.services.tools.MTG.capitalize;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGDeck.BOARD;
import org.magic.api.beans.MTGEdition;
import org.magic.services.CardsManagerService;

public class DeckCardsTableModel extends DefaultTableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String[] columns = new String[] { "NAME",
			"CARD_TYPES",
			"CARD_MANA",
			"CARD_EDITION",
			"QTY",
			"ARENA"};

	private MTGDeck deck;

	private BOARD t;

	@Override
	public Class<?> getColumnClass(int columnIndex) {

		if (columnIndex == 3)
			return List.class;

		if (columnIndex == 5)
			return Boolean.class;

		if (columnIndex == 4)
			return Integer.class;

		return super.getColumnClass(columnIndex);
	}

	public DeckCardsTableModel(BOARD t) {
		this.t = t;
		deck = new MTGDeck();
	}

	public void initSide(MTGDeck deck) {
		this.deck = deck;
	}

	public MTGDeck getDeck() {
		return deck;
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public String getColumnName(int column) {
		return capitalize(columns[column]);
	}

	@Override
	public Object getValueAt(int row, int column) {
		MTGCard mc;
		switch (t) {
		case MAIN:
			mc = deck.getValueAt(row);
			break;
		case SIDE:
			mc = deck.getSideValueAt(row);
			break;
		default:
			mc = deck.getValueAt(row);
			break;
		}

		if (column == 0)
			return mc;

		if (column == 1)
			return mc.getFullType();

		if (column == 2)
			return mc.getCost();

		if (column == 3)
			return mc.getEditions();

		if (column == 4) {
			switch (t) {
			case MAIN:
				return deck.getMain().get(mc);
			case SIDE:
				return deck.getSideBoard().get(mc);
			default:
				return null;
			}

		}

		if (column == 5)
			return mc.isArenaCard();

		return null;
	}

	@Override
	public int getRowCount() {
		if (deck == null)
			return 0;

		switch (t) {
		case MAIN:
			return deck.getMain().size();
		case SIDE:
			return deck.getSideBoard().size();
		default:
			return deck.getMain().size();
		}

	}

	public void init(MTGDeck deck) {
		this.deck = deck;
		fireTableDataChanged();
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return (column == 3 || column == 4);
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		
		MTGCard mc = null;
		int qty = 0;
		
		switch (t)
		{
			case MAIN : mc = deck.getValueAt(row); qty = deck.getMain().get(mc); break;
			case SIDE: mc = deck.getSideValueAt(row); qty=deck.getSideBoard().get(mc);break;
			default:  mc=deck.getValueAt(row); qty=0;break;
		}

		if (column == 3) {
			MTGEdition ed = (MTGEdition) aValue;

			if(!ed.equals(mc.getEdition()))
			{
				var newC = CardsManagerService.switchEditions(mc, ed);
				if (t == BOARD.MAIN) 
				{
					deck.getMain().remove(mc);
					deck.getMain().put(newC, qty);
				} else 
				{
					deck.getSideBoard().remove(mc);
					deck.getSideBoard().put(newC, qty);
				}
			}
		}

		if (column == 4)
		{
			if (Integer.valueOf(aValue.toString()) == 0) {
				if (t == BOARD.MAIN) {
					deck.getMain().remove(deck.getValueAt(row));
				} else {
					deck.getSideBoard().remove(deck.getSideValueAt(row));
				}
			} else {
				if (t == BOARD.MAIN) {
					deck.getMain().put(deck.getValueAt(row), Integer.valueOf(aValue.toString()));
				} else {
					deck.getSideBoard().put(deck.getSideValueAt(row), Integer.valueOf(aValue.toString()));
				}
			}

		}
		fireTableDataChanged();
	}

}

package org.magic.gui.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.magic.api.beans.CardDominance;
import org.magic.api.beans.MTGFormat;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class CardDominanceTableModel extends GenericTableModel<CardDominance> {

	private static final long serialVersionUID = 1L;
	
	public CardDominanceTableModel() {
		columns=new String[] { "CARD",
				"POSITION",
				"PC_DOMINANCE",
				"PC_DECKS",
				"PLAYERS" };
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return CardDominance.class;
		case 1:
			return Integer.class;
		case 2:
			return Double.class;
		case 3:
			return Double.class;
		case 4:
			return Double.class;
		default:
			return super.getColumnClass(columnIndex);
		}

	}

	@Override
	public Object getValueAt(int row, int column) {

		switch (column) {
		case 0:
			return items.get(row);
		case 1:
			return items.get(row).getPosition();
		case 2:
			return items.get(row).getDominance();
		case 3:
			return items.get(row).getDecksPercent();
		case 4:
			return items.get(row).getPlayers();
		default:
			return "";
		}
	}

}

package org.magic.gui.models;

import static org.magic.tools.MTG.capitalize;

import java.util.Locale;

import org.magic.game.model.Player;
import org.magic.game.model.Player.STATUS;
import org.magic.gui.abstracts.GenericTableModel;

public class PlayerTableModel extends GenericTableModel<Player> {

	private static final long serialVersionUID = 1L;
	
	public PlayerTableModel() {
		columns = new String[]{ capitalize("PLAYER"),capitalize("COUNTRY"),capitalize("STATE")};
		setWritable(false);
	}
	
	
	@Override
	public Class<?> getColumnClass(int c) {
		switch (c)
		{
			case 0 : return Player.class;
			case 1 : return Locale.class;
			case 2 : return STATUS.class;
			default : return super.getColumnClass(c);
		}
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		switch (column) {
		case 0:
			return items.get(row);
		case 1:
			return items.get(row).getLocal();
		case 2:
			return items.get(row).getState();
		default:
			return null;
		}
	}

}
package org.magic.gui.models;

import org.magic.api.beans.game.Player;
import org.magic.gui.abstracts.GenericTableModel;

public class PlayerTableModel extends GenericTableModel<Player> {

	private static final long serialVersionUID = 1L;

	public PlayerTableModel() {
		setColumns("PLAYER");
		setWritable(false);
	}


	@Override
	public Class<?> getColumnClass(int c) {
		if(c==0)
			return Player.class;

		return super.getColumnClass(c);

	}

	@Override
	public Object getValueAt(int row, int column) {
		return items.get(row);

	}

}
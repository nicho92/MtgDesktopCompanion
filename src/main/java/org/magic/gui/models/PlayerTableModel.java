package org.magic.gui.models;

import org.apache.tools.ant.taskdefs.Local;
import org.magic.game.model.Player;
import org.magic.game.model.Player.STATUS;
import org.magic.gui.abstracts.GenericTableModel;

public class PlayerTableModel extends GenericTableModel<Player> {

	private static final long serialVersionUID = 1L;
	
	public PlayerTableModel() {
		columns = new String[]{"PLAYER","COUNTRY","STATE"};
		setWritable(false);
	}
	
	
	@Override
	public Class<?> getColumnClass(int c) {
		switch (c)
		{
			case 0 : return Player.class;
			case 1 : return Local.class;
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
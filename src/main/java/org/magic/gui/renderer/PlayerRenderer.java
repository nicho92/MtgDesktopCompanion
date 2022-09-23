package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.magic.game.model.Player;
import org.magic.gui.components.renderer.PlayerPanel;

public class PlayerRenderer implements TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {


		if(value==null)
			return new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);


		Player p = (Player) value;

		var pComponent = new PlayerPanel();
		pComponent.setPlayer(p);


			if(isSelected)
			{
				pComponent.setForeground(table.getSelectionForeground());
				pComponent.setBackground(table.getSelectionBackground());
			}
			else
			{
				pComponent.setForeground(table.getForeground());
				pComponent.setBackground(table.getBackground());
			}

		return pComponent;

	}

}

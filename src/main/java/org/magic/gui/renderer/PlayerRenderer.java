package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.magic.game.model.Player;
import org.magic.gui.components.renderer.PlayerPanel;

public class PlayerRenderer implements TableCellRenderer, ListCellRenderer<Player> {

	@Override
	public Component getListCellRendererComponent(JList<? extends Player> list, Player value, int index,boolean isSelected, boolean cellHasFocus) {
		return component(value);
	}
	
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {


		if(value==null)
			return new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);


		var pComponent= component((Player)value);
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


	private Component component(Player value) {
		var pComponent = new PlayerPanel();
		pComponent.setPlayer(value);
		return pComponent;
	}

	

}

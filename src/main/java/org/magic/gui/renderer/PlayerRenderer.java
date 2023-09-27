package org.magic.gui.renderer;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.magic.api.beans.game.Player;
import org.magic.gui.components.renderer.PlayerPanel;

public class PlayerRenderer implements TableCellRenderer, ListCellRenderer<Player> {

	@Override
	public Component getListCellRendererComponent(JList<? extends Player> list, Player value, int index,boolean isSelected, boolean cellHasFocus) {
		var panel = new JPanel();
		var separator = new JPanel();
		
		panel.setLayout(new BorderLayout());
		
		
		
		var comp= component(value);
		
		var color = value.getState().getColor();
	
		comp.setBorder(new LineBorder(color));
		separator.setBackground(color);
		
		panel.add(separator,BorderLayout.WEST);
		panel.add(comp,BorderLayout.CENTER);
		
		
		return panel;
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


	private JComponent component(Player value) {
		var pComponent = new PlayerPanel();
		pComponent.setPlayer(value);
		return pComponent;
	}

	

}

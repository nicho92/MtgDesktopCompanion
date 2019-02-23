package org.magic.gui.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class AlertedCardsRenderer implements TableCellRenderer {
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		Component comp = new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		comp.setForeground(Color.BLACK);
		
		
		
		if ((Integer) value > 0)
			comp.setBackground(Color.GREEN);
		else if(isSelected)
			comp.setBackground(table.getSelectionBackground());
		else
			comp.setBackground(table.getBackground());
		
		
		return comp;
	}
}

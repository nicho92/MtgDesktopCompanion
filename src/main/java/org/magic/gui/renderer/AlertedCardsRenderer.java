package org.magic.gui.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.magic.gui.renderer.standard.IntegerCellEditorRenderer;

public class AlertedCardsRenderer implements TableCellRenderer {
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		var comp = new IntegerCellEditorRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	
		if(value==null)
			return comp;
		
		
		if ((Integer) value > 0)
			comp.setBackground(Color.GREEN);
		
		return comp;
	}
}

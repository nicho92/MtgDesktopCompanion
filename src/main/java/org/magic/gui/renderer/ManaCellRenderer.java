package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.magic.gui.components.ManaPanel;

public class ManaCellRenderer implements TableCellRenderer {

	ManaPanel pane;
	
	public ManaCellRenderer() {
		pane = new ManaPanel();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {

		pane.setManaCost(String.valueOf(value));
		pane.setBackground(new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column).getBackground());
		return pane;

	}

}

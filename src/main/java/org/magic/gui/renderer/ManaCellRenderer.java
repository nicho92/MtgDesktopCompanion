package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.JTable;
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
		pane.setBackground(table.getBackground());

		if(isSelected)
			pane.setBackground(table.getSelectionBackground());

		return pane;

	}

}

package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.magic.gui.components.card.ManaPanel;

public class ManaCellRenderer extends ManaPanel implements TableCellRenderer {

	
	
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {

		setManaCost(String.valueOf(value));

		if(isSelected)
			setBackground(table.getSelectionBackground());
		else
			setBackground(table.getBackground());
		
		return this;

	}

}

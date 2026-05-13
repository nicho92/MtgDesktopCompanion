package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.magic.api.interfaces.MTGStockItem;

public class MTGStockItemRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		var obj = (MTGStockItem) value;

		setText(obj.getId() + " " + (obj.isUpdated() ? "*" : ""));
		setOpaque(true);
		setBackground(t.getBackground());

		if (isSelected) {
			setBackground(t.getSelectionBackground());
			setForeground(t.getSelectionForeground());
		}

		return this;
	}
}

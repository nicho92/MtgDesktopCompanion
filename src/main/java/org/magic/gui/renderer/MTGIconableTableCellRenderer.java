package org.magic.gui.renderer;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import org.magic.api.interfaces.extra.MTGIconable;

public class MTGIconableTableCellRenderer extends JLabel implements TableCellRenderer {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		setHorizontalAlignment(SwingConstants.LEADING);
		setOpaque(true);
		setBackground(t.getBackground());

		if (value != null) {
			setText(((MTGIconable) value).getName());
			setIcon(((MTGIconable) value).getIcon());
		}
		else
		{
			setText("");
			setIcon(null);
		}

		if (isSelected) {
			setBackground(t.getSelectionBackground());
			setForeground(t.getSelectionForeground());
		}
		return this;
	}

}

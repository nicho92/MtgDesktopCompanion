package org.magic.gui.renderer;

import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.magic.api.beans.MTGEdition;
import org.magic.services.MTGControler;
import org.magic.services.providers.IconsProvider;

public class MagicEditionCellRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	public MagicEditionCellRenderer() {
		setOpaque(true);
		setFont(MTGControler.getInstance().getFont().deriveFont(Font.PLAIN));
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		if (value == null)
			return new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
					column);

		var ed = (MTGEdition) value;
		setIcon(IconsProvider.getInstance().get16(ed.getId()));
		setText(ed.getSet());
		setToolTipText(ed.getSet());

		if (isSelected) {
			setForeground(table.getSelectionForeground());
			setBackground(table.getSelectionBackground());
		} else {
			setForeground(table.getForeground());
			setBackground(table.getBackground());
		}

		return this;

	}

}

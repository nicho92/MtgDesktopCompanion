package org.magic.gui.renderer;

import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.services.tools.ImageTools;

public class MTGPluginCellRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		if (value == null)
			return new DefaultTableCellRenderer();

		var plug = ((MTGPlugin) value);

		setIcon(ImageTools.resize(plug.getIcon(), 24, 24));
		setText(plug.getName());
		setOpaque(true);

		if (plug.isPartner())
			setFont(getFont().deriveFont(Font.BOLD));

		if (isSelected) {
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		} else {
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}
		return this;
	}

}

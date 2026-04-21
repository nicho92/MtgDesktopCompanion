package org.magic.gui.renderer;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import org.magic.api.beans.MTGGrading;
import org.magic.api.interfaces.MTGGraders;
import org.magic.services.PluginRegistry;

public class GradingCellRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object g, boolean isSelected, boolean hasFocus,
			int row, int column) {

		setOpaque(true);
		var grad = (MTGGrading) g;

		try {
			var c = PluginRegistry.inst().getPlugin(grad.getGraderName(), MTGGraders.class).getIcon();
			setText(grad.toString());
			setIcon(c);
			setHorizontalAlignment(SwingConstants.LEADING);
		} catch (Exception _) {
			setText(null);
		}

		if (isSelected)
			setBackground(table.getSelectionBackground());

		return this;

	}

}

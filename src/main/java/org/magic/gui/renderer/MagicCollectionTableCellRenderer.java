package org.magic.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.magic.gui.renderer.standard.BooleanCellEditorRenderer;
import org.magic.gui.renderer.standard.DateTableCellEditorRenderer;
import org.magic.gui.renderer.standard.DoubleCellEditorRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.tools.UITools;

public class MagicCollectionTableCellRenderer extends DefaultTableRenderer {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Color c;

	// Reuse renderers
	private DoubleCellEditorRenderer doubleRenderer;
	private BooleanCellEditorRenderer booleanRenderer;
	private DateTableCellEditorRenderer dateRenderer;
	private JLabel iconLabel;

	public MagicCollectionTableCellRenderer() {

		doubleRenderer = new DoubleCellEditorRenderer(true, false);
		booleanRenderer = new BooleanCellEditorRenderer();
		dateRenderer = new DateTableCellEditorRenderer();
		iconLabel = new JLabel();
		iconLabel.setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		Component pane;

		if (value instanceof Double) {
			pane = doubleRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		} else if (value instanceof ImageIcon ic) {
			iconLabel.setIcon(ic);
			pane = iconLabel;
		} else if (value instanceof Boolean) {
			pane = booleanRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		} else if (value instanceof Date) {
			pane = dateRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		} else {
			pane = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}

		pane.setBackground(c);
		pane.setForeground(Color.BLACK);

		double val = (double) table.getValueAt(row, 4);

		if (val >= 1.0) {
			pane.setBackground(MTGConstants.COLLECTION_100PC);

		} else if (val >= 0.9) {
			pane.setBackground(MTGConstants.COLLECTION_90PC);

		} else if (val >= 0.5) {
			pane.setBackground(MTGConstants.COLLECTION_50PC);

		} else if (val > 0.0) {
			pane.setBackground(MTGConstants.COLLECTION_1PC);

		} else {
			pane.setBackground(table.getBackground());
			pane.setForeground(table.getForeground());
		}

		if (isSelected)
			UITools.applyDefaultSelection(pane);

		return pane;
	}

}

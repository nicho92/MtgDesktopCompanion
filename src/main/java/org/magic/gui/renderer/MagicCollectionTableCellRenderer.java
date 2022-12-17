package org.magic.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.text.NumberFormat;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.magic.gui.renderer.standard.BooleanCellEditorRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.tools.UITools;

public class MagicCollectionTableCellRenderer extends DefaultTableRenderer {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Color c;


	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {

		if (column == 4)
			value = NumberFormat.getPercentInstance().format((double) value);

		Component pane;

		if(value instanceof ImageIcon ic)
		{
			pane=new JLabel(ic);
			((JLabel)pane).setOpaque(true);
		}
		else if(value instanceof Boolean)
		{
			pane=new BooleanCellEditorRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
		else
		{
			pane = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}

		pane.setBackground(c);

		double val = (double) table.getValueAt(row, 4);

		if (val>= 0.1 && val<0.5) {
			pane.setBackground(MTGConstants.COLLECTION_1PC);
			pane.setForeground(Color.BLACK);
		}

		if (val >= 0.5) {
			pane.setBackground(MTGConstants.COLLECTION_50PC);
			pane.setForeground(Color.BLACK);
		}

		if (val>=0.9) {
			pane.setBackground(MTGConstants.COLLECTION_90PC);
			pane.setForeground(Color.BLACK);
		}

		if (val >= 1) {
			pane.setBackground(MTGConstants.COLLECTION_100PC);
			pane.setForeground(Color.BLACK);
		}

		if(isSelected)
			UITools.applyDefaultSelection(pane);


		return pane;
	}

}

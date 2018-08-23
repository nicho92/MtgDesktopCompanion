package org.magic.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.magic.api.beans.MagicCardStock;

public class StockTableRenderer extends DefaultTableRenderer {

	Component pane;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		pane = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if(value instanceof Boolean)
		{
			pane=new JPanel();
			JCheckBox jcbox=new JCheckBox("",Boolean.parseBoolean(value.toString()));
					  jcbox.setOpaque(false);
					  FlowLayout flowLayout = new FlowLayout();
						flowLayout.setVgap(0);
			((JPanel)pane).setLayout(flowLayout);
			((JPanel)pane).add(jcbox);
		}
		
		
		if (((MagicCardStock) table.getValueAt(row, 0)).isUpdate()) {
			pane.setBackground(Color.GREEN);
			pane.setForeground(table.getForeground());
		} else if (isSelected) {
			pane.setBackground(table.getSelectionBackground());
			pane.setForeground(table.getSelectionForeground());
		} else {
			pane.setBackground(table.getBackground());
			pane.setForeground(table.getForeground());
		}

		return pane;
	}

}
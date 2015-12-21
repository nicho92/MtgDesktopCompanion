package org.magic.gui.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.magic.gui.components.ManaPanel;

public class MagicCollectionTableCellRenderer extends DefaultTableCellRenderer {

	
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		{
			Component pane = super.getTableCellRendererComponent(table, value, isSelected,hasFocus, row, column);
			if((int)table.getValueAt(row, 4)<50)
			{
				pane.setBackground(table.getBackground());
				pane.setForeground(Color.black);
			}
			
			if((int)table.getValueAt(row, 4)>=50)
			{
				pane.setBackground(Color.orange);
			}
			
			if((int)table.getValueAt(row, 4)==100)
			{
				pane.setBackground(Color.green);
			}
			
			return pane;
		}

	}
	
	
}

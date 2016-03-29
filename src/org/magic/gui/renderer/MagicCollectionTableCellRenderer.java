package org.magic.gui.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class MagicCollectionTableCellRenderer extends DefaultTableCellRenderer {

	
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		{
			
			if(column==4)
				value=value.toString()+"%";
			
			Component pane = super.getTableCellRendererComponent(table, value, isSelected,hasFocus, row, column);
			
			if((int)table.getValueAt(row, 4)<5)
			{
				pane.setBackground(table.getBackground());
				pane.setForeground(Color.BLACK);
			}
			
			if((int)table.getValueAt(row, 4)>=5 && (int)table.getValueAt(row, 4)<50)
			{
				pane.setBackground(Color.YELLOW);
				pane.setForeground(Color.BLACK);
			}
			
			if((int)table.getValueAt(row, 4)>=50)
			{
				pane.setBackground(Color.ORANGE);
			}
			
			if((int)table.getValueAt(row, 4)==100)
			{
				pane.setBackground(Color.GREEN);
			}
			
			return pane;
		}

	}
	
	
}

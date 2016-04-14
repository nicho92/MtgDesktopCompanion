package org.magic.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class MagicCollectionTableCellRenderer extends DefaultTableCellRenderer {

	public static final int SIZE=40;
	
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
			
			
			if(column==0)
			{
				try
				{
					ImageIcon ic = new ImageIcon(new ImageIcon(MagicCollectionTableCellRenderer.class.getResource("/res/set/icons/"+value+"_set.png")).getImage().getScaledInstance(SIZE, SIZE, Image.SCALE_DEFAULT));
					JLabel l = new JLabel(ic);
					l.setBackground(pane.getBackground());
					return l;
				}
				catch(NullPointerException e)
				{}
			}
		
			
			return pane;
		}

	}
	
	
}

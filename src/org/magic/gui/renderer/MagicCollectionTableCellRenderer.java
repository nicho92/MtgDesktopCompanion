package org.magic.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import org.jdesktop.swingx.renderer.DefaultTableRenderer;

public class MagicCollectionTableCellRenderer extends DefaultTableRenderer {

	HashMap<String, ImageIcon> cache;
	Color c ;
	
	public MagicCollectionTableCellRenderer() {
		cache=new HashMap<String,ImageIcon>();
	}
	
	Component pane;
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
	{
			if(column==4)
				value=value.toString()+"%";
			
			pane = super.getTableCellRendererComponent(table, value, isSelected,hasFocus, row, column);

			pane.setBackground(c);
			
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
				pane.setForeground(Color.BLACK);
			}
			
			if((int)table.getValueAt(row, 4)==100)
			{
				pane.setBackground(Color.GREEN);
				pane.setForeground(Color.BLACK);
			}
			return pane;
		}

	}
	
	
}

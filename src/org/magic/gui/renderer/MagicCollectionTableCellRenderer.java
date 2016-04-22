package org.magic.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
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
			
			
			if(column==0)
			{
				try
				{
					BufferedImage im = ImageIO.read(MagicCollectionTableCellRenderer.class.getResource("/res/set/icons/"+value+"_set.png"));
					//BufferedImage im = ImageIO.read(new URL("https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/src/res/set/icons/"+value+"_set.png"));
					ImageIcon ic = new ImageIcon(im.getSubimage(12, 11, 55, 42).getScaledInstance(26, 24, Image.SCALE_SMOOTH));
					JLabel l = new JLabel(ic);
					l.setOpaque(false);
					l.setBackground(pane.getBackground());
					return l;
				}
				catch(Exception e)
				{}
			}
		
			return pane;
		}

	}
	
	
}

package org.magic.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class MagicCollectionTableCellRenderer extends DefaultTableCellRenderer {

	HashMap<String, ImageIcon> cache;
	
	public MagicCollectionTableCellRenderer() {
		cache=new HashMap<>();
	}
	
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
				pane.setForeground(Color.BLACK);
			}
			
			if((int)table.getValueAt(row, 4)==100)
			{
				pane.setBackground(Color.GREEN);
				pane.setForeground(Color.BLACK);
			}
			
			
			if(column==0)
			{
				try
				{
					ImageIcon im ;
					if(cache.get(value.toString())==null)
					{
					
						if(value.toString().startsWith("p"))
							im=new ImageIcon(ImageIO.read(MagicCollectionTableCellRenderer.class.getResource("/res/set/icons/VAN_set.png")).getSubimage(12, 11, 55, 42).getScaledInstance(26, 24, Image.SCALE_SMOOTH));
						else
							im = new ImageIcon(ImageIO.read(MagicCollectionTableCellRenderer.class.getResource("/res/set/icons/"+value+"_set.png")).getSubimage(12, 11, 55, 42).getScaledInstance(26, 24, Image.SCALE_SMOOTH));
					
						
						cache.put(value.toString(),im);
					}
					else
					{
							im=cache.get(value.toString());
					}
					
					JLabel l = new JLabel(im);
					l.setOpaque(false);
					l.setBackground(pane.getBackground());
					return l;
				}
				catch(Exception e)
				{ 
				//	e.printStackTrace();
				}
			}
		
			return pane;
		}

	}
	
	
}

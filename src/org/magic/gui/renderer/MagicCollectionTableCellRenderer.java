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
	
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
	{
			if(column==4)
				value=value.toString()+"%";
			
			JLabel pane = (JLabel)super.getTableCellRendererComponent(table, value, isSelected,hasFocus, row, column);
			pane.setIcon(null);
			pane.setHorizontalAlignment(JLabel.LEFT);
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
			/*
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
						System.out.println(value.toString() + " loaded from cache");
						im=cache.get(value.toString());
					}
					pane.setIcon(im);
					pane.setText(null);
					pane.setHorizontalAlignment(JLabel.CENTER);
				} 
				catch(Exception e)
				{ 
				//	e.printStackTrace();
				}
				c=pane.getBackground();
			}*/
			return pane;
		}

	}
	
	
}

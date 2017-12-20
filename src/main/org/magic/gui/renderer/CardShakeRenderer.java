package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CardShakeRenderer extends DefaultTableCellRenderer {

	ImageIcon up;
	ImageIcon down;
	JLabel comp;
	
	public CardShakeRenderer() {
		up = new ImageIcon(CardShakeRenderer.class.getResource("/res/up.png"));
		down = new ImageIcon(CardShakeRenderer.class.getResource("/res/down.png"));
		comp=new JLabel();
		
		comp.setHorizontalAlignment(JLabel.CENTER);
		comp.setOpaque(false);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		{
			try 
			{
				
				comp.setText(value.toString());
					
				if(((Double)value).doubleValue()>0)
					comp.setIcon(up);
	
				if(((Double)value).doubleValue()<0)
					comp.setIcon(down);
				
				if(((Double)value).doubleValue()==0)
					comp.setIcon(null);
			
				return comp;
			}
			catch(Exception e)
			{
				comp.setText(e.getMessage());
				return comp;
			}
		}
	}
	
	
}

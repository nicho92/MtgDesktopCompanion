package org.magic.gui.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

public class CardShakeRenderer extends DefaultTableCellRenderer {

	ImageIcon up;
	ImageIcon down;
	JLabel comp = null;
	
	public CardShakeRenderer() {
		up = new ImageIcon(CardShakeRenderer.class.getResource("/res/up.png"));
		down = new ImageIcon(CardShakeRenderer.class.getResource("/res/down.png"));
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		{
		/*	Color c;
			if (isSelected) {
			    c=table.getSelectionBackground();
			}
			else {
			    if ( row % 2 == 0 ) {
			    	c=UIManager.getColor("Table.alternateRowColor");
			    } else { 
			    	c=table.getBackground();
			    }
			}
			
			*/
			if(((Double)value).doubleValue()>0)
				comp = new JLabel(value.toString(), up,JLabel.CENTER);
			
			if(((Double)value).doubleValue()<0)
				comp=new JLabel(value.toString(), down,JLabel.CENTER);
			
			if(((Double)value).doubleValue()==0)
				comp=new JLabel(value.toString());
			
			
		//	comp.setBackground(c);
			comp.setOpaque(false);
			return comp;
		
			//return super.getTableCellRendererComponent(table, 0, isSelected,hasFocus, row, column);
			
		}
	}
	
	
}

package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CardShakeRenderer extends DefaultTableCellRenderer {

	ImageIcon up;
	ImageIcon down;
	
	public CardShakeRenderer() {
		up = new ImageIcon(CardShakeRenderer.class.getResource("/res/up.png"));
		down = new ImageIcon(CardShakeRenderer.class.getResource("/res/down.png"));
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		{
			
			if(((Double)value).doubleValue()>0)
				return new JLabel(up);
			
			if(((Double)value).doubleValue()<0)
				return new JLabel(down);
			
			return super.getTableCellRendererComponent(table, "=", isSelected,hasFocus, row, column);
		}
	}
	
	
}

package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CardShakeRenderer extends DefaultTableCellRenderer {

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		{
			
			if(((Double)value).doubleValue()>0)
				return new JLabel(new ImageIcon(CardShakeRenderer.class.getResource("/res/up.png")));
			
			if(((Double)value).doubleValue()<0)
				return new JLabel(new ImageIcon(CardShakeRenderer.class.getResource("/res/down.png")));
			
			return super.getTableCellRendererComponent(table, value, isSelected,hasFocus, row, column);
		}
	}
	
	
}

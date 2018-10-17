package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.magic.tools.UITools;

public class CardShakeTreeCellRenderer extends DefaultTableCellRenderer{
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		
		if(value instanceof Double)
			return new JLabel(UITools.formatDouble((Double)value));
		
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}

	
}

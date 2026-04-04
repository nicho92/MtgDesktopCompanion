package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import org.magic.api.beans.technical.MoneyValue;
import org.magic.services.tools.UITools;

public class MoneyCellRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		var text="";
		
		try {
				var val = (MoneyValue)value;
				text = UITools.formatDouble(val.doubleValue()) + " " + val.getCurrency().getSymbol();
			}
		catch(Exception _)
			{
				//do nothing
			}
	
		setText(text);
		setHorizontalAlignment(SwingConstants.CENTER);
		setOpaque(true);
			if(isSelected)
			{
				setBackground(table.getSelectionBackground());
				setForeground(table.getSelectionForeground());
			}
			else
			{
				setBackground(table.getBackground());
				setForeground(table.getForeground());
			}

		return this;
	}

}

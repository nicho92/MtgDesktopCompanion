package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import org.magic.api.beans.technical.MoneyValue;
import org.magic.services.tools.UITools;

public class MoneyCellRenderer implements TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		var text="";
		
		try {
				MoneyValue val = (MoneyValue)value;
				text = UITools.formatDouble(val.doubleValue()) + " " + val.getCurrency().getSymbol();
			}
		catch(Exception e)
			{
				//do nothing
			}
	

		var l= new JLabel(text,SwingConstants.CENTER);
		   l.setOpaque(true);
			if(isSelected)
			{
				l.setBackground(table.getSelectionBackground());
				l.setForeground(table.getSelectionForeground());
			}
			else
			{
				l.setBackground(table.getBackground());
				l.setForeground(table.getForeground());
			}



		return l;
	}

}

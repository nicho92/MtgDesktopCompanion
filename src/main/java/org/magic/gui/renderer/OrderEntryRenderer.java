package org.magic.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class OrderEntryRenderer implements TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {

		var o = (OrderEntry)table.getValueAt(row, 0);

		var comp = new JLabel(String.valueOf(value));
		comp.setFont(MTGControler.getInstance().getFont().deriveFont(Font.PLAIN));
		comp.setHorizontalAlignment(SwingConstants.CENTER);
		comp.setHorizontalTextPosition(SwingConstants.RIGHT);
		comp.setOpaque(true);

		if (((OrderEntry) table.getValueAt(row, 0)).isUpdated()) {
			comp.setBackground(Color.GREEN);
			comp.setForeground(table.getForeground());
		}
		else if (isSelected) {
			comp.setBackground(table.getSelectionBackground());
			comp.setForeground(table.getSelectionForeground());
		} else {
			comp.setBackground(table.getBackground());
			comp.setForeground(table.getForeground());
		}


		try {
			if(value instanceof Double)
			{
				if (o.getTypeTransaction()==TransactionDirection.BUY)
				{
					comp.setIcon(MTGConstants.ICON_OUT);
				}
				else
				{
					comp.setIcon(MTGConstants.ICON_IN);
				}
			}
			return comp;
		} catch (Exception e) {
			comp.setText(e.getMessage());
			return comp;
		}

	}

}

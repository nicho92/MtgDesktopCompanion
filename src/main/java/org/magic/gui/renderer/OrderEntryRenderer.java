package org.magic.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.OrderEntry.TYPE_TRANSACTION;
import org.magic.services.MTGConstants;

public class OrderEntryRenderer implements TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		
		OrderEntry o = (OrderEntry)table.getValueAt(row, 0);
		
		JLabel comp = new JLabel(String.valueOf(value));
		comp.setFont(MTGConstants.FONT.deriveFont(Font.PLAIN));
		comp.setHorizontalAlignment(JLabel.CENTER);
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
				if (o.getTypeTransaction()==TYPE_TRANSACTION.BUY)
				{
					comp.setIcon(MTGConstants.ICON_DOWN);
				}
				else
				{
					comp.setIcon(MTGConstants.ICON_UP);
				}
				
			return comp;
		} catch (Exception e) {
			comp.setText(e.getMessage());
			return comp;
		}

	}

}

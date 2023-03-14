package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.magic.api.beans.shop.Contact;

public class ContactRenderer implements TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {


		if(value==null)
			return new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);


		Contact p = (Contact) value;

		var pComponent = new JLabel(p.getName() + " " + p.getLastName());
		pComponent.setOpaque(true);


			if(isSelected)
			{
				pComponent.setForeground(table.getSelectionForeground());
				pComponent.setBackground(table.getSelectionBackground());
			}
			else
			{
				pComponent.setForeground(table.getForeground());
				pComponent.setBackground(table.getBackground());
			}

		return pComponent;

	}

}

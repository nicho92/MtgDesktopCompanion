package org.magic.gui.renderer;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.magic.api.interfaces.MTGPlugin;

public class MTGPluginCellRenderer implements TableCellRenderer{

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		var lab = new JLabel();

		var plug = ((MTGPlugin)value);

		lab.setIcon( plug.getIcon());
		lab.setText(plug.getName());
		lab.setOpaque(true);


		if(plug.isPartner())
			 lab.setFont(lab.getFont().deriveFont(Font.BOLD));


		if (isSelected) {
			lab.setBackground(table.getSelectionBackground());
		} else {
			lab.setBackground(table.getBackground());
		}


		return lab;
	}

}

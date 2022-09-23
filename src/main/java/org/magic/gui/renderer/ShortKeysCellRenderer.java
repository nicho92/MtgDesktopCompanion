package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;

public class ShortKeysCellRenderer implements TableCellRenderer{

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column)
	{

		var val="";
		Icon ic = null;

		if(value instanceof JButton b)
		{
			val=b.getName();
			ic = b.getIcon();
		}

		if(value instanceof MTGUIComponent b)
		{
			val=b.getTitle();
			ic = b.getIcon();
		}



		table.setRowHeight(MTGConstants.ICON_NEW.getIconHeight());
		var l= new JLabel(val, ic, SwingConstants.LEFT);
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

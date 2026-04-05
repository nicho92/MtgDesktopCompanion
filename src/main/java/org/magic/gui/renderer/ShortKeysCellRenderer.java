package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.tools.ImageTools;

public class ShortKeysCellRenderer extends JLabel implements TableCellRenderer{

	private static final long serialVersionUID = 1L;

	private final int ICON_SIZE=32;
	
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



		table.setRowHeight(ICON_SIZE);
		
		setText(val);
		setHorizontalAlignment(SwingConstants.LEFT);
		setOpaque(true);

		
		
		if(ic!=null)
			setIcon(ImageTools.resize(ic, ICON_SIZE,ICON_SIZE));
		
		
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

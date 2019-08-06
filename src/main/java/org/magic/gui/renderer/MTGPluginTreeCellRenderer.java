package org.magic.gui.renderer;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import org.magic.api.interfaces.MTGPlugin;
import org.magic.services.MTGConstants;
import org.magic.tools.UITools;
import org.magic.tools.URLTools;

public class MTGPluginTreeCellRenderer implements TreeCellRenderer{
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,int row, boolean hasFocus) {
		JLabel lab = new JLabel();
		tree.setRowHeight(MTGConstants.TREE_ROW_HEIGHT);
		lab.setBackground(tree.getBackground());
		lab.setForeground(tree.getForeground());
		if(value instanceof MTGPlugin)
		{
		   lab.setFont(lab.getFont().deriveFont(Font.BOLD));
		   lab.setText(value.toString());
		   lab.setIcon(((MTGPlugin)value).getIcon());
			
		}else if (value instanceof Entry)
		{
			lab.setIcon(MTGConstants.ICON_MANA_INCOLOR);
			lab.setText(((Entry)value).getKey().toString());
		}
		return lab;
	}
	
}

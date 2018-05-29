package org.magic.gui.renderer;

import java.awt.Component;
import java.awt.Font;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.magic.api.interfaces.MTGPlugin;
import org.magic.services.MTGConstants;

public class MTGPluginTreeCellRenderer extends DefaultTreeCellRenderer{
	
	
	
	
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
		}
		else if (value instanceof Entry)
		{
			lab.setIcon(MTGConstants.ICON_MANA_INCOLOR);
			lab.setText(((Entry)value).getKey().toString());
		}
		return lab;
	}
	
}

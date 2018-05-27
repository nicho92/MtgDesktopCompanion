package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.magic.api.interfaces.MTGPlugin;
import org.magic.services.MTGConstants;

public class MTGPluginTreeCellRenderer extends DefaultTreeCellRenderer{

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,int row, boolean hasFocus) {
		
		tree.setRowHeight(MTGConstants.TREE_ROW_HEIGHT);
		
		if(value instanceof MTGPlugin)
		{
			JLabel lab = new JLabel(value.toString());
				   lab.setIcon(((MTGPlugin)value).getIcon());
				   return lab;
		}
		return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	}
	
}

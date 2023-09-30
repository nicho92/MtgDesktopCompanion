package org.magic.gui.renderer;

import java.awt.Component;
import java.awt.Font;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import org.magic.api.interfaces.MTGPlugin;
import org.magic.services.MTGConstants;
import org.magic.services.tools.ImageTools;

public class MTGPluginTreeCellRenderer implements TreeCellRenderer{

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,int row, boolean hasFocus) {
		var lab = new JLabel();
		tree.setRowHeight(MTGConstants.TREE_ROW_HEIGHT);
		lab.setBackground(tree.getBackground());
		lab.setForeground(tree.getForeground());
		if(value instanceof MTGPlugin p)
		{
		   lab.setFont(lab.getFont().deriveFont(Font.BOLD));
		   lab.setText(value.toString());
		   lab.setIcon( ImageTools.resize(p.getIcon(),24,24));

		}else if (value instanceof Entry<?, ?> e)
		{
			lab.setIcon(MTGConstants.ICON_MANA_INCOLOR);
			lab.setText(e.getKey().toString());
		}
		return lab;
	}

}

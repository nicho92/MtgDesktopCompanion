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

public class MTGPluginTreeCellRenderer extends JLabel  implements TreeCellRenderer{

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,int row, boolean hasFocus) {
		tree.setRowHeight(MTGConstants.TREE_ROW_HEIGHT);
		setBackground(tree.getBackground());
		setForeground(tree.getForeground());
		if(value instanceof MTGPlugin p)
		{
		   setFont(getFont().deriveFont(Font.BOLD));
		   setText(value.toString());
		   setIcon( ImageTools.resize(p.getIcon(),24,24));

		}else if (value instanceof Entry<?, ?> e)
		{
			setIcon(MTGConstants.ICON_MANA_INCOLOR);
			setText(e.getKey().toString());
		}
		return this;
	}

}

package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import org.magic.api.beans.MTGPrice;
import org.magic.services.MTGConstants;

public class MagicPriceShoppingTreeCellRenderer implements TreeCellRenderer{

	private JLabel lab;


	public MagicPriceShoppingTreeCellRenderer() {
		lab = new JLabel();
	}
	
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,int row, boolean hasFocus) {
		lab.setBackground(tree.getBackground());
		lab.setForeground(tree.getForeground());
		lab.setText(value.toString());
		tree.setRowHeight(MTGConstants.TREE_ROW_HEIGHT);

		if(value instanceof String)
		{
			lab.setIcon(MTGConstants.ICON_TAB_USER);
		}


		if(value instanceof MTGPrice)
		{

		   lab.setIcon(MTGConstants.ICON_TAB_CARD);
		}
		return lab;
	}

}

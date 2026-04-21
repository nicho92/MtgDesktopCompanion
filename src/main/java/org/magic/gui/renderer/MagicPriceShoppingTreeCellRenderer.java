package org.magic.gui.renderer;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import org.magic.api.beans.MTGPrice;
import org.magic.services.MTGConstants;

public class MagicPriceShoppingTreeCellRenderer extends JLabel implements TreeCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		setBackground(tree.getBackground());
		setForeground(tree.getForeground());
		setText(value.toString());
		tree.setRowHeight(MTGConstants.TREE_ROW_HEIGHT);

		if (value instanceof String) {
			setIcon(MTGConstants.ICON_TAB_USER);
		}

		if (value instanceof MTGPrice) {
			setIcon(MTGConstants.ICON_TAB_CARD);
		}
		return this;
	}

}

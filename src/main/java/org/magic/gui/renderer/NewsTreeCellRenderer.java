package org.magic.gui.renderer;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import org.magic.api.beans.MTGNews;
import org.magic.services.MTGConstants;

public class NewsTreeCellRenderer extends JLabel implements TreeCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean isLeaf, int row, boolean focused) {

		setText(String.valueOf(value));

		if (((DefaultMutableTreeNode) value).getUserObject() instanceof MTGNews news) {
			try {
				setIcon(news.getProvider().getIcon());
			} catch (Exception _) {
				setIcon(null);
			}

		}

		if (((DefaultMutableTreeNode) value).getUserObject() instanceof String)
			setIcon(MTGConstants.ICON_NEWS);

		return this;
	}
}

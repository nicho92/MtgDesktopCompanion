package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.magic.api.beans.MagicNews;
import org.magic.services.MTGConstants;

public class NewsTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,boolean isLeaf, int row, boolean focused) {
		Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, focused);

		if (((DefaultMutableTreeNode) value).getUserObject() instanceof MagicNews) {
			try {
			switch (((MagicNews) ((DefaultMutableTreeNode) value).getUserObject()).getProvider().getProviderType()) {
			case RSS:
				setIcon(MTGConstants.ICON_RSS);
				break;
			case TWITTER:
				setIcon(MTGConstants.ICON_TWITTER);
				break;
			case FORUM:
				setIcon(MTGConstants.ICON_FORUM);
				break;
			}
			}
			catch(Exception e)
			{
				setIcon(null);
			}

		}

		if (((DefaultMutableTreeNode) value).getUserObject() instanceof String)
			setIcon(MTGConstants.ICON_NEWS);

		return c;
	}
}

package org.magic.gui.renderer;

import java.awt.Component;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import org.magic.services.MTGConstants;
import org.magic.services.tools.ImageTools;

public class DeckTreeCellRenderer implements TreeCellRenderer {

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		final var lab = new JLabel();
		lab.setBackground(tree.getBackground());
		lab.setForeground(tree.getForeground());

		final var node = (DefaultMutableTreeNode) value;

		if (node.getUserObject() instanceof final String s) {

			lab.setText(s);
			try {
				final var ic = MTGConstants.getManaSymbol(s);
				lab.setIcon(ImageTools.resize(ic, 18, 18));
			} catch (final Exception _) {
				// do nothing
			}

		}

		if (node.getUserObject() instanceof final Map.Entry entry) {
			lab.setText(entry.getValue() + " " + entry.getKey());
		}

		return lab;
	}

}

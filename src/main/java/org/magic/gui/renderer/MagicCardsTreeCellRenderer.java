package org.magic.gui.renderer;

import java.awt.Component;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Packaging;
import org.magic.gui.components.ManaPanel;
import org.magic.services.MTGConstants;
import org.magic.services.extra.IconSetProvider;
import org.magic.tools.ColorParser;

public class MagicCardsTreeCellRenderer implements TreeCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ManaPanel pane;
	private Map<String, ImageIcon> map;

	public MagicCardsTreeCellRenderer() {
		try {
			pane = new ManaPanel();
			map = new HashMap<>();
			map.put("{W}", new ImageIcon(pane.getManaSymbol("{W}").getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
			map.put("{U}", new ImageIcon(pane.getManaSymbol("{U}").getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
			map.put("{B}", new ImageIcon(pane.getManaSymbol("{B}").getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
			map.put("{R}", new ImageIcon(pane.getManaSymbol("{R}").getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
			map.put("{G}", new ImageIcon(pane.getManaSymbol("{G}").getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
			map.put("{X}", new ImageIcon(pane.getManaSymbol("{X}").getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
		} catch (Exception e) {
			// do nothing
		}

	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean isLeaf, int row, boolean focused) {
		JLabel c = (JLabel)new DefaultTreeCellRenderer().getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, focused);
		try {
			if (((DefaultMutableTreeNode) value).getUserObject() instanceof MagicEdition) {
				MagicEdition ed = (MagicEdition) ((DefaultMutableTreeNode) value).getUserObject();
				c.setIcon(IconSetProvider.getInstance().get16(ed.getId()));
			} else if (((DefaultMutableTreeNode) value).getUserObject() instanceof MagicCard) {
				MagicCard mc = (MagicCard) ((DefaultMutableTreeNode) value).getUserObject();

				c.setOpaque(false);
				c.setIcon(MTGConstants.ICON_MANA_INCOLOR);

				if (mc.isArtifact()) {
					c.setIcon(map.get("{X}"));
				}
				if (mc.getColors().size() == 1) {
					c.setIcon(map.get(ColorParser.getCodeByName(mc.getColors().get(0),true)));
				}
				if (mc.getColors().size() > 1) {
					c.setIcon(MTGConstants.ICON_MANA_GOLD);
				}
				if (mc.isLand()) {
					c.setIcon(MTGConstants.ICON_MANA_INCOLOR);
				}
			} 
			else if (((DefaultMutableTreeNode) value).getUserObject() instanceof Packaging) {
				c.setIcon(MTGConstants.ICON_PACKAGE_SMALL);
			}
			else {
				c.setIcon(MTGConstants.ICON_BACK);
			}
			return c;
		} catch (Exception e) {
			return c;
		}
	}

}

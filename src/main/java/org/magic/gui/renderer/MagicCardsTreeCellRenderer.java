package org.magic.gui.renderer;

import java.awt.Component;
import java.awt.Image;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.MTGColor;
import org.magic.gui.components.ManaPanel;
import org.magic.services.MTGConstants;
import org.magic.services.providers.IconSetProvider;

public class MagicCardsTreeCellRenderer implements TreeCellRenderer {

	private ManaPanel pane;
	private Map<MTGColor, ImageIcon> map;

	public MagicCardsTreeCellRenderer() {
		try {
			pane = new ManaPanel();
			map = new EnumMap<>(MTGColor.class);
			map.put(MTGColor.WHITE, new ImageIcon(pane.getManaSymbol(MTGColor.WHITE.getCode()).getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
			map.put(MTGColor.BLUE, new ImageIcon(pane.getManaSymbol(MTGColor.BLUE.getCode()).getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
			map.put(MTGColor.BLACK, new ImageIcon(pane.getManaSymbol(MTGColor.BLACK.getCode()).getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
			map.put(MTGColor.RED, new ImageIcon(pane.getManaSymbol(MTGColor.RED.getCode()).getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
			map.put(MTGColor.GREEN, new ImageIcon(pane.getManaSymbol(MTGColor.GREEN.getCode()).getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
			map.put(MTGColor.UNCOLOR, new ImageIcon(pane.getManaSymbol("X").getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
		} catch (Exception e) {
			// do nothing
		}

	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean isLeaf, int row, boolean focused) {
		JLabel c = (JLabel)new DefaultTreeCellRenderer().getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, focused);
		try {
			if (((DefaultMutableTreeNode) value).getUserObject() instanceof MagicEdition ed) {
				c.setIcon(IconSetProvider.getInstance().get16(ed.getId()));
			} else if (((DefaultMutableTreeNode) value).getUserObject() instanceof MagicCard mc) {

				c.setOpaque(false);
				c.setIcon(MTGConstants.ICON_MANA_INCOLOR);

				if (mc.isArtifact()) {
					c.setIcon(map.get(MTGColor.UNCOLOR));
				}
				if (mc.getColors().size() == 1) {
					c.setIcon(map.get(mc.getColors().get(0)));
				}
				if (mc.isMultiColor()) {
					c.setIcon(MTGConstants.ICON_MANA_GOLD);
				}
				if (mc.isLand()) {
					c.setIcon(MTGConstants.ICON_MANA_INCOLOR);
				}
			}
			else if (((DefaultMutableTreeNode) value).getUserObject() instanceof MTGSealedProduct) {
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

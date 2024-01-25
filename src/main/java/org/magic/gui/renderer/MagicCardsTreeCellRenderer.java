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
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumColors;
import org.magic.gui.components.card.ManaPanel;
import org.magic.services.MTGConstants;
import org.magic.services.providers.IconSetProvider;

public class MagicCardsTreeCellRenderer implements TreeCellRenderer {

	private ManaPanel pane;
	private Map<EnumColors, ImageIcon> map;

	public MagicCardsTreeCellRenderer() {
		try {
			pane = new ManaPanel();
			map = new EnumMap<>(EnumColors.class);
			map.put(EnumColors.WHITE, new ImageIcon(pane.getManaSymbol(EnumColors.WHITE.getCode()).getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
			map.put(EnumColors.BLUE, new ImageIcon(pane.getManaSymbol(EnumColors.BLUE.getCode()).getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
			map.put(EnumColors.BLACK, new ImageIcon(pane.getManaSymbol(EnumColors.BLACK.getCode()).getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
			map.put(EnumColors.RED, new ImageIcon(pane.getManaSymbol(EnumColors.RED.getCode()).getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
			map.put(EnumColors.GREEN, new ImageIcon(pane.getManaSymbol(EnumColors.GREEN.getCode()).getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
			map.put(EnumColors.UNCOLOR, new ImageIcon(pane.getManaSymbol("X").getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
		} catch (Exception e) {
			// do nothing
		}

	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean isLeaf, int row, boolean focused) {
		JLabel c = (JLabel)new DefaultTreeCellRenderer().getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, focused);
		try {
			if (((DefaultMutableTreeNode) value).getUserObject() instanceof MTGEdition ed) {
				c.setIcon(IconSetProvider.getInstance().get16(ed.getId()));
			} else if (((DefaultMutableTreeNode) value).getUserObject() instanceof MTGCard mc) {

				c.setOpaque(false);
				c.setIcon(MTGConstants.ICON_MANA_INCOLOR);

				if (mc.isArtifact()) {
					c.setIcon(map.get(EnumColors.UNCOLOR));
				}else if (mc.isLand()) {
					c.setIcon(MTGConstants.ICON_MANA_INCOLOR);
				}else if (mc.getColors().size() == 1) {
					c.setIcon(map.get(mc.getColors().get(0)));
				}else if (mc.isMultiColor()) {
					c.setIcon(MTGConstants.ICON_MANA_GOLD);
				}
			}
			else if (((DefaultMutableTreeNode) value).getUserObject() instanceof MTGSealedProduct ) {
				c.setIcon(MTGConstants.ICON_TAB_PACKAGE);
			}
			else {
				c.setIcon(MTGConstants.ICON_TAB_BACK);
			}
			return c;
		} catch (Exception e) {
			return c;
		}
	}

}

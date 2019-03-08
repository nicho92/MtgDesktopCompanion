package org.magic.gui.renderer;

import java.awt.Component;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Packaging;
import org.magic.gui.components.ManaPanel;
import org.magic.services.MTGConstants;
import org.magic.services.extra.IconSetProvider;
import org.magic.tools.ColorParser;

public class MagicCardsTreeCellRenderer extends DefaultTreeCellRenderer {

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
		Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, focused);
		try {
			if (((DefaultMutableTreeNode) value).getUserObject() instanceof MagicEdition) {
				MagicEdition ed = (MagicEdition) ((DefaultMutableTreeNode) value).getUserObject();
				setIcon(IconSetProvider.getInstance().get16(ed.getId()));
			} else if (((DefaultMutableTreeNode) value).getUserObject() instanceof MagicCard) {
				MagicCard mc = (MagicCard) ((DefaultMutableTreeNode) value).getUserObject();

				setOpaque(false);
				setIcon(MTGConstants.ICON_MANA_INCOLOR);

				if (mc.getFullType().toLowerCase().contains("artifact")) {
					setIcon(map.get("{X}"));
				}
				if (mc.getColors().size() == 1) {
					setIcon(map.get(ColorParser.getCodeByName(mc.getColors().get(0),true)));
				}
				if (mc.getColors().size() > 1) {
					setIcon(MTGConstants.ICON_MANA_GOLD);
				}
				if (mc.isLand()) {
					setIcon(MTGConstants.ICON_MANA_INCOLOR);
				}
			} 
			else if (((DefaultMutableTreeNode) value).getUserObject() instanceof Packaging) {
				setIcon(MTGConstants.ICON_PACKAGE_SMALL);
			}
			else {
				setIcon(MTGConstants.ICON_BACK);
			}
			return c;
		} catch (Exception e) {
			return c;
		}
	}

}

package org.magic.gui.renderer;

import java.awt.Component;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.gui.components.ManaPanel;
import org.magic.services.MTGConstants;
import org.magic.services.extra.IconSetProvider;
import org.magic.tools.ColorParser;

public class MagicCardsTreeCellRenderer extends DefaultTreeCellRenderer {

	private ManaPanel pane;
	private ImageIcon gold;
	private ImageIcon uncolor;
	private ImageIcon back;
	private Map<String, ImageIcon> map;

	public MagicCardsTreeCellRenderer() {
		try {
			pane = new ManaPanel();
			map = new HashMap<>();
			gold = new ImageIcon(
					ImageIO.read(MTGConstants.URL_MANA_GOLD).getScaledInstance(15, 15, Image.SCALE_DEFAULT));
			uncolor = new ImageIcon(
					ImageIO.read(MTGConstants.URL_MANA_INCOLOR).getScaledInstance(15, 15, Image.SCALE_DEFAULT));
			back = new ImageIcon(
					ImageIO.read(MTGConstants.URL_COLLECTION).getScaledInstance(15, 15, Image.SCALE_DEFAULT));

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
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean isLeaf, int row, boolean focused) {
		Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, focused);
		try {
			if (((DefaultMutableTreeNode) value).getUserObject() instanceof MagicEdition) {
				MagicEdition ed = (MagicEdition) ((DefaultMutableTreeNode) value).getUserObject();
				setIcon(IconSetProvider.getInstance().get16(ed.getId()));
			} else if (((DefaultMutableTreeNode) value).getUserObject() instanceof MagicCard) {
				MagicCard mc = (MagicCard) ((DefaultMutableTreeNode) value).getUserObject();

				setOpaque(false);
				setIcon(uncolor);

				if (mc.getFullType().toLowerCase().contains("artifact")) {
					setIcon(map.get("{X}"));
				}
				if (mc.getColors().size() == 1) {
					setIcon(map.get(ColorParser.parse(mc.getColors().get(0))));
				}
				if (mc.getColors().size() > 1) {
					setIcon(gold);
				}
				if (mc.getFullType().toLowerCase().contains("land")) {
					setIcon(uncolor);
				}
			} else {
				setIcon(back);
			}
			return c;
		} catch (Exception e) {
			return c;
		}
	}

}

package org.magic.gui.components.deck;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Enumeration;
import java.util.Map;

import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.gui.components.card.MagicCardMainDetailPanel;
import org.magic.gui.renderer.DeckTreeCellRenderer;

public class DeckTree extends JTree {

	private static final long serialVersionUID = 1L;
	private final DefaultTreeModel model;
	private final DefaultMutableTreeNode root;
	private final DefaultMutableTreeNode creatureNode = new DefaultMutableTreeNode("Creatures");
	private final DefaultMutableTreeNode artifactsNode = new DefaultMutableTreeNode("Artifacts");
	private final DefaultMutableTreeNode planeswalkerNode = new DefaultMutableTreeNode("Planeswalkers");
	private final DefaultMutableTreeNode landsNode = new DefaultMutableTreeNode("Lands");
	private final DefaultMutableTreeNode spellsNode = new DefaultMutableTreeNode("Spells");
	private final DefaultMutableTreeNode sideNode = new DefaultMutableTreeNode("Sideboard");
	private MTGDeck selectedDeck;

	public void setDeck(MTGDeck deck) {
		this.selectedDeck = deck;
		initTree();
	}

	public DeckTree() {
		root = new DefaultMutableTreeNode("Cards");
		root.add(creatureNode);
		root.add(artifactsNode);
		root.add(planeswalkerNode);
		root.add(landsNode);
		root.add(spellsNode);
		root.add(sideNode);
		model = new DefaultTreeModel(root);

		setModel(model);

		setCellRenderer(new DeckTreeCellRenderer());
	}

	private void expandAll(TreePath parent) {
		final TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (final Enumeration<? extends TreeNode> e = node.children(); e.hasMoreElements();) {
				final var n = e.nextElement();
				final var path = parent.pathByAddingChild(n);
				expandAll(path);
			}
		}
		expandPath(parent);
	}

	private void initTree() {

		creatureNode.removeAllChildren();
		artifactsNode.removeAllChildren();
		planeswalkerNode.removeAllChildren();
		landsNode.removeAllChildren();
		spellsNode.removeAllChildren();
		sideNode.removeAllChildren();

		if (selectedDeck != null) {
			for (final var mc : selectedDeck.getMain().entrySet()) {
				if (mc.getKey().isCreature() && !mc.getKey().isArtifact()) {
				    creatureNode.add(new DefaultMutableTreeNode(mc));
				} else if (mc.getKey().isArtifact()) {
				    artifactsNode.add(new DefaultMutableTreeNode(mc));
				} else if (mc.getKey().isLand()) {
				    landsNode.add(new DefaultMutableTreeNode(mc));
				} else if (mc.getKey().isPlaneswalker()) {
				    planeswalkerNode.add(new DefaultMutableTreeNode(mc));
				} else {
				    spellsNode.add(new DefaultMutableTreeNode(mc));
				}
			}

			for (final var mc : selectedDeck.getSideBoard().entrySet()) {
			    sideNode.add(new DefaultMutableTreeNode(mc));
			}

			model.reload();

			expandAll(new TreePath(root));
		}
	}

	public void enableThumbnail() {
		final var popUp = new JPopupMenu();
		addTreeSelectionListener(tsl -> {

			final var p = ((DefaultMutableTreeNode) tsl.getPath().getLastPathComponent()).getUserObject();

			if (p instanceof final Map.Entry e) {
				final var mc = (MTGCard) e.getKey();

				final var pane = new MagicCardMainDetailPanel();
				pane.enableThumbnail(true);
				pane.init(mc);

				popUp.setBorder(new LineBorder(Color.black));
				popUp.setVisible(false);
				popUp.removeAll();
				popUp.setLayout(new BorderLayout());
				popUp.add(pane, BorderLayout.CENTER);
				popUp.show(this, getParent().getWidth(), 0);
				popUp.setVisible(true);

			}

		});
	}

}

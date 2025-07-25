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

public class DeckTree extends JTree{
	
	private static final long serialVersionUID = 1L;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;
	private DefaultMutableTreeNode creatureNode = new DefaultMutableTreeNode("Creatures");
	private DefaultMutableTreeNode artifactsNode = new DefaultMutableTreeNode("Artifacts");
	private DefaultMutableTreeNode planeswalkerNode = new DefaultMutableTreeNode("Planeswalkers");
	private DefaultMutableTreeNode landsNode = new DefaultMutableTreeNode("Lands");
	private DefaultMutableTreeNode spellsNode = new DefaultMutableTreeNode("Spells");
	private DefaultMutableTreeNode sideNode = new DefaultMutableTreeNode("Sideboard");
	private MTGDeck selectedDeck;
	
	
	public void setDeck(MTGDeck deck)
	{
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
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration<? extends TreeNode>e = node.children(); e.hasMoreElements();) {
				var n =  e.nextElement();
				var path = parent.pathByAddingChild(n);
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
			for (var mc : selectedDeck.getMain().entrySet()) {
				if (mc.getKey().isCreature() && !mc.getKey().isArtifact())
					creatureNode.add(new DefaultMutableTreeNode(mc));
				else if (mc.getKey().isArtifact())
					artifactsNode.add(new DefaultMutableTreeNode(mc));
				else if (mc.getKey().isLand())
					landsNode.add(new DefaultMutableTreeNode(mc));
				else if (mc.getKey().isPlaneswalker())
					planeswalkerNode.add(new DefaultMutableTreeNode(mc));
				else
					spellsNode.add(new DefaultMutableTreeNode(mc));
			}

			for (var mc : selectedDeck.getSideBoard().entrySet())
				sideNode.add(new DefaultMutableTreeNode(mc));

			model.reload();

			expandAll(new TreePath(root));
		}
	}

	
	public void enableThumbnail()
	{
		final var popUp = new JPopupMenu();
		addTreeSelectionListener(tsl->{
			
				var p = ((DefaultMutableTreeNode)tsl.getPath().getLastPathComponent()).getUserObject();
				
				
				if(p instanceof Map.Entry e)
				{
					var mc = (MTGCard) e.getKey();
					
					var pane = new MagicCardMainDetailPanel();
					pane.enableThumbnail(true);
					pane.init(mc);
					
					popUp.setBorder(new LineBorder(Color.black));
					popUp.setVisible(false);
					popUp.removeAll();
					popUp.setLayout(new BorderLayout());
					popUp.add(pane, BorderLayout.CENTER);
					popUp.show(this,getParent().getWidth(),0);
					popUp.setVisible(true);
					
				}
			
			
		});
	}
	
}

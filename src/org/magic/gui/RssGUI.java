package org.magic.gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JSplitPane;
import javax.swing.JEditorPane;
import javax.swing.tree.DefaultTreeModel;

import org.magic.gui.models.RssTableModel;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class RssGUI extends JPanel {
	private JTable table;
	
	public RssGUI() {
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollTable = new JScrollPane();
		
		table = new JTable();
		scrollTable.setViewportView(table);
		
		JScrollPane scrollTree = new JScrollPane();
		
		JTree tree = new JTree();
		tree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("Magic RSS") {
				{
					DefaultMutableTreeNode node_1;
					node_1 = new DefaultMutableTreeNode("Spoil");
						node_1.add(new DefaultMutableTreeNode("Magic Spoiler"));
					add(node_1);
					node_1 = new DefaultMutableTreeNode("News En");
						node_1.add(new DefaultMutableTreeNode("Channel Fireball"));
						node_1.add(new DefaultMutableTreeNode("Daily MTG"));
						node_1.add(new DefaultMutableTreeNode("MTGSalvation"));
					add(node_1);
					node_1 = new DefaultMutableTreeNode("News FR");
						node_1.add(new DefaultMutableTreeNode("MagicTrade"));
						node_1.add(new DefaultMutableTreeNode("SMFCorp"));
						node_1.add(new DefaultMutableTreeNode("MagicCorporation"));
					add(node_1);
					node_1 = new DefaultMutableTreeNode("Trade");
						node_1.add(new DefaultMutableTreeNode("MTGGoldFish"));
					add(node_1);
				}
			}
		));
		scrollTree.setViewportView(tree);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		JScrollPane scrollEditor = new JScrollPane();
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		
		scrollEditor.setViewportView(editorPane);
		
		
		splitPane.setLeftComponent(scrollTable);
		splitPane.setRightComponent(scrollEditor);
		
		JSplitPane splitPane_1 = new JSplitPane();
		add(splitPane_1, BorderLayout.CENTER);
		splitPane_1.setLeftComponent(scrollTree);
		splitPane_1.setRightComponent(splitPane);
		
		JPanel panelHaut = new JPanel();
		add(panelHaut, BorderLayout.NORTH);
		
		JButton btnNewButton = new JButton("");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				((RssTableModel)table.getModel()).init();
			}
		});
		btnNewButton.setIcon(new ImageIcon(RssGUI.class.getResource("/res/refresh.png")));
		panelHaut.add(btnNewButton);
	}

}

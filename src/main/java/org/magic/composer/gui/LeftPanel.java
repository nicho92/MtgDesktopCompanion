package org.magic.composer.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;

public class LeftPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public LeftPanel(JTree tree, LayersListPanel layerList, LayerDetailPanel inspector) {

	setLayout(new BorderLayout());
	setPreferredSize(new Dimension(350, 0));

	var treeScrollPane = new JScrollPane(tree);

	treeScrollPane.setBorder(BorderFactory.createTitledBorder("Resources"));

	var treePanel = new JPanel(new BorderLayout());

	treePanel.add(treeScrollPane, BorderLayout.CENTER);

	var layersPanel = new JPanel(new BorderLayout());
	layersPanel.setBorder(BorderFactory.createTitledBorder("Composition"));
	layersPanel.add(layerList, BorderLayout.CENTER);

	
	var tabPane = new JTabbedPane();
		tabPane.addTab("Layers", layersPanel);
		tabPane.addTab("Inspect", inspector);
	
	
	// =================================================================
	// Split
	// =================================================================

	var splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, treePanel, tabPane);

	splitPane.setResizeWeight(0.65);

	splitPane.setOneTouchExpandable(true);

	add(splitPane, BorderLayout.CENTER);
    }
}
package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.gui.components.charts.CmcChartPanel;
import org.magic.gui.components.editor.JTagsPanel;
import org.magic.gui.models.DeckSelectionModel;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGControler;

public class JDeckChooserDialog extends JDialog {
	
	JXTable table;
	CmcChartPanel cmcChartPanel;
	MagicDeck selectedDeck;
	JTagsPanel tagsPanel;
	
	JTree tree;
	  DefaultTreeModel model;
	  DefaultMutableTreeNode root ;
	  DefaultMutableTreeNode creatureNode = new DefaultMutableTreeNode("Creatures");
	  DefaultMutableTreeNode artifactsNode = new DefaultMutableTreeNode("Artifacts");
	  DefaultMutableTreeNode planeswalkerNode = new DefaultMutableTreeNode("Planeswalkers");
	  DefaultMutableTreeNode landsNode = new DefaultMutableTreeNode("Lands");
	  DefaultMutableTreeNode spellsNode = new DefaultMutableTreeNode("Spells");
	  DefaultMutableTreeNode sideNode = new DefaultMutableTreeNode("Sideboard");
	
	
	public MagicDeck getSelectedDeck() {
		return selectedDeck;
	}
	
	
	private void initTree() {
		  
		creatureNode.removeAllChildren();
		artifactsNode.removeAllChildren();
	    planeswalkerNode.removeAllChildren();
	    landsNode.removeAllChildren();
	    spellsNode.removeAllChildren();
	    sideNode.removeAllChildren();
	    
	    if(selectedDeck!=null)
	        {
    			for(MagicCard mc : selectedDeck.getMap().keySet())
	        	{
	        		if(mc.getTypes().contains("Creature") && !mc.getTypes().contains("Artifact"))
	        			creatureNode.add(new DefaultMutableTreeNode(selectedDeck.getMap().get(mc) + " " + mc));
	        		else if(mc.getTypes().contains("Artifact"))
	        			artifactsNode.add(new DefaultMutableTreeNode(selectedDeck.getMap().get(mc) + " " + mc));
	        		else if(mc.getTypes().contains("Land"))
	        			landsNode.add(new DefaultMutableTreeNode(selectedDeck.getMap().get(mc) + " " + mc));
	        		else if(mc.getTypes().contains("Planeswalker"))
	        			planeswalkerNode.add(new DefaultMutableTreeNode(selectedDeck.getMap().get(mc) + " " + mc));
	        		else
	        			spellsNode.add(new DefaultMutableTreeNode(selectedDeck.getMap().get(mc) + " " + mc));
	        	}
	        	
	        	for(MagicCard mc : selectedDeck.getMapSideBoard().keySet())
	        		sideNode.add(new DefaultMutableTreeNode(selectedDeck.getMapSideBoard().get(mc) + " " + mc));

	        	
	        	   
		        model.reload();
		        
		        expandAll(new TreePath(root));
	        }
	}
	
	private void expandAll(TreePath parent) {
	    TreeNode node = (TreeNode) parent.getLastPathComponent();
	    if (node.getChildCount() >= 0) {
	      for (Enumeration e = node.children(); e.hasMoreElements();) {
	        TreeNode n = (TreeNode) e.nextElement();
	        TreePath path = parent.pathByAddingChild(n);
	        expandAll(path);
	      }
	    }
	    tree.expandPath(parent);
	    // tree.collapsePath(parent);
	  }
	
	public JDeckChooserDialog() {
		setTitle(MTGControler.getInstance().getLangService().getCapitalize("OPEN_DECK"));
		setSize(828, 500);
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		model = new DefaultTreeModel(root);
		
		addWindowListener(new WindowAdapter() 
		{
		  public void windowClosed(WindowEvent e)
		  {
		    selectedDeck=null;
		  }

		  public void windowClosing(WindowEvent e)
		  {
			  selectedDeck=null;
		  }
		});
		
		table = new JXTable(new DeckSelectionModel());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				
				//((DefaultListModel)list.getModel()).removeAllElements();
				selectedDeck = (MagicDeck)table.getValueAt(table.getSelectedRow(), 0);
	
				initTree();
				tagsPanel.clean();
				tagsPanel.addTags(selectedDeck.getTags());
				cmcChartPanel.init(selectedDeck);
				cmcChartPanel.revalidate();
				cmcChartPanel.repaint();
				
				if (event.getClickCount() == 2) {
					dispose();
				}
				
				
			}

			
		});
		
		scrollPane.setViewportView(table);
		
		JPanel panelBas = new JPanel();
		getContentPane().add(panelBas, BorderLayout.SOUTH);
		
		JButton btnSelect = new JButton(MTGControler.getInstance().getLangService().getCapitalize("OPEN"));
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(selectedDeck==null)
					JOptionPane.showMessageDialog(null, MTGControler.getInstance().getLangService().getCapitalize("CHOOSE_DECK"));
				else	
					dispose();
			}
		});
		panelBas.add(btnSelect);
		
		JButton btnCancel = new JButton(MTGControler.getInstance().getLangService().getCapitalize("CANCEL"));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectedDeck=null;
				dispose();
			}
		});
		panelBas.add(btnCancel);
		
		JButton btnNewButton = new JButton(MTGControler.getInstance().getLangService().getCapitalize("DELETE"));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				int res = JOptionPane.showConfirmDialog(null,MTGControler.getInstance().getLangService().getCapitalize("CONFIRM_DELETE",selectedDeck.getName()),MTGControler.getInstance().getLangService().getCapitalize("CONFIRMATION")+" ?",JOptionPane.YES_NO_OPTION);
				
				if(res==JOptionPane.YES_OPTION)
				{
					((DeckSelectionModel)table.getModel()).remove(selectedDeck);
				}
				
			}
		});
		panelBas.add(btnNewButton);
		
		JSplitPane panelRight = new JSplitPane();
		panelRight.setOrientation(JSplitPane.VERTICAL_SPLIT);
		getContentPane().add(panelRight, BorderLayout.EAST);
	
		cmcChartPanel = new CmcChartPanel();
		cmcChartPanel.setPreferredSize(new Dimension(250, 150));
		panelRight.setBottomComponent(cmcChartPanel);
		root = new DefaultMutableTreeNode("Cards");
		 //add the child nodes to the root node
        root.add(creatureNode);
        root.add(artifactsNode);
        root.add(planeswalkerNode);
        root.add(landsNode);
        root.add(spellsNode);
        root.add(sideNode);
		model = new DefaultTreeModel(root);
		
		
		panelRight.setDividerLocation(300);
		
		JPanel panelTree = new JPanel();
		panelTree.setLayout(new BorderLayout(0, 0));
		panelRight.setTopComponent(panelTree);
		tree = new JTree();
		tree.setModel(model);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panelTree.add(scrollPane_1);
		scrollPane_1.setViewportView(tree);
		
		tagsPanel = new JTagsPanel();
		tagsPanel.setFontSize(11);
		tagsPanel.setColors(Color.DARK_GRAY, Color.WHITE);
		tagsPanel.setEditable(false);
		panelTree.add(tagsPanel, BorderLayout.SOUTH);
		
		table.getColumnModel().getColumn(1).setCellRenderer(new ManaCellRenderer());
		table.packAll();
		setLocationRelativeTo(null);
		setModal(true);
	}
}


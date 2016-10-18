package org.magic.gui.components;

import java.awt.BorderLayout;
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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.gui.components.charts.CmcChartPanel;
import org.magic.gui.models.DeckSelectionModel;
import org.magic.gui.renderer.ManaCellRenderer;

public class JDeckChooserDialog extends JDialog {
	
	JXTable table;
	CmcChartPanel cmcChartPanel;
	MagicDeck selectedDeck;
	
	
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
	
	public static void main(String[] args) {
		new JDeckChooserDialog().setVisible(true);;
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
		setTitle("Choose your deck");
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
		
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				
				//((DefaultListModel)list.getModel()).removeAllElements();
				selectedDeck = (MagicDeck)table.getModel().getValueAt(table.getSelectedRow(),0);
				
				//for(MagicCard mc : selectedDeck.getMap().keySet())
				//	((DefaultListModel)list.getModel()).addElement( selectedDeck.getMap().get(mc)+" "+ mc);
				
				initTree();
				
				
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
		
		JButton btnSelect = new JButton("Select");
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(selectedDeck==null)
					JOptionPane.showMessageDialog(null, "Please choose a deck");
				else	
					dispose();
			}
		});
		panelBas.add(btnSelect);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectedDeck=null;
				dispose();
			}
		});
		panelBas.add(btnCancel);
		
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
		
		tree = new JTree();
		tree.setModel(model);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panelRight.setTopComponent(scrollPane_1);
		scrollPane_1.setViewportView(tree);
		
		
		panelRight.setDividerLocation(300);
		
		table.getColumnModel().getColumn(1).setCellRenderer(new ManaCellRenderer());
		table.packAll();
		setLocationRelativeTo(null);
		setModal(true);
	}
}


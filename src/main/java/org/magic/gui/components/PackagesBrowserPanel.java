package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Packaging;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.renderer.MagicCardsTreeCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.extra.PackagesProvider;

public class PackagesBrowserPanel extends MTGUIComponent{
	
	private static final long serialVersionUID = 1L;
	private transient PackagesProvider provider;
	private DefaultTreeModel model;
	private ZoomableJPanel panelDraw;
	private JTree tree;
	
	
	public PackagesBrowserPanel() {
		provider = PackagesProvider.inst();
		initGUI();
		initTree();
	}
	
	public PackagesBrowserPanel(boolean load) {
		provider = PackagesProvider.inst();
		initGUI();
		
		if(load)
			initTree();
	}
	

	public JTree getTree() {
		return tree;
	}
	
	
	
	public void setMagicEdition(MagicEdition ed)
	{
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
		root.setUserObject(ed);
		root.removeAllChildren();
		Arrays.asList(Packaging.TYPE.values()).forEach(t->{
			List<Packaging> pks = provider.get(ed, t);
			logger.trace("loading " + ed + " " + pks);
			if(!pks.isEmpty())
			{
				DefaultMutableTreeNode dir = new DefaultMutableTreeNode(t);
				pks.forEach(p->dir.add(new DefaultMutableTreeNode(p)));
				root.add(dir);
			}
		});
		model.reload();
		for (int i = 0; i < tree.getRowCount(); i++) {
		    tree.expandRow(i);
		}		
		panelDraw.setImg(null);
		
	}
	
	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		setPreferredSize(new Dimension(1024, 768));
		model = new DefaultTreeModel(new DefaultMutableTreeNode("Packaging"));
		panelDraw = new ZoomableJPanel() ;
		add(panelDraw, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		add(panel, BorderLayout.WEST);
		tree = new JTree(model);
		tree.setCellRenderer(new MagicCardsTreeCellRenderer());

		panel.add(new JScrollPane(tree),BorderLayout.CENTER);
		
		tree.addTreeSelectionListener(e-> {
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			
			if(selectedNode!=null && (selectedNode.getUserObject() instanceof Packaging))
				load((Packaging)selectedNode.getUserObject());
		});
		
		
		
	}
	
	public void load(Packaging p)
	{
			panelDraw.setImg(provider.get(p));
			panelDraw.revalidate();
			panelDraw.repaint();
		
	}
	
	
	public void initTree()
	{
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
		root.removeAllChildren();
		
		provider.listEditions().forEach(ed->{
			
			DefaultMutableTreeNode edNode = new DefaultMutableTreeNode(ed);
			root.add(edNode);
			
			Arrays.asList(Packaging.TYPE.values()).forEach(t->{
				List<Packaging> pks = provider.get(ed, t);
				if(!pks.isEmpty())
				{
					DefaultMutableTreeNode dir = new DefaultMutableTreeNode(t);
					pks.forEach(p->dir.add(new DefaultMutableTreeNode(p)));
					edNode.add(dir);
				}
			});
		});
		model.reload();

		tree.expandRow(0);
		panelDraw.setImg(null);
		
	}


	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_PACKAGE;
	}
	

	@Override
	public String getTitle() {
		return "Package Browser";
	}
}

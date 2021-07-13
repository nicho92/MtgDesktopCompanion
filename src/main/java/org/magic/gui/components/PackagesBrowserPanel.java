package org.magic.gui.components;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jdesktop.swingx.JXTree;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Packaging;
import org.magic.api.beans.enums.EnumItems;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.renderer.MagicCardsTreeCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.providers.PackagesProvider;

public class PackagesBrowserPanel extends MTGUIComponent{
	
	private static final long serialVersionUID = 1L;
	private transient PackagesProvider provider;
	private DefaultTreeModel model;
	private ImagePanel panelDraw;
	private JXTree tree;
	private boolean view;

	
	public PackagesBrowserPanel(boolean viewThumbnail) {
		provider = PackagesProvider.inst();
		this.view = viewThumbnail;
		initGUI();

	}
	

	public JXTree getTree() {
		return tree;
	}
	
	public ImagePanel getThumbnailPanel()
	{
		return panelDraw;
	}
	
	
	public void setMagicEdition(MagicEdition ed)
	{
		var root = (DefaultMutableTreeNode)model.getRoot();
		root.setUserObject(ed);
		root.removeAllChildren();
		Arrays.asList(EnumItems.values()).forEach(t->{
			List<Packaging> pks = provider.get(ed, t);
			logger.trace("loading " + ed + " " + pks);
			if(!pks.isEmpty())
			{
				var dir = new DefaultMutableTreeNode(t);
				pks.forEach(p->dir.add(new DefaultMutableTreeNode(p)));
				root.add(dir);
			}
		});
		model.reload();
		for (var i = 0; i < tree.getRowCount(); i++) {
		    tree.expandRow(i);
		}
		
		if(view)
			panelDraw.setImg(null);
		
	}
	
	private void initGUI() {
		setLayout(new BorderLayout(0, 0));

		model = new DefaultTreeModel(new DefaultMutableTreeNode("Packaging"));
		panelDraw = new ImagePanel(true, false, true);
		panelDraw.setReflection(false);
		if(view) {
			add(panelDraw, BorderLayout.CENTER);
		}
		
		var panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		if(!view)
		{
			add(panel, BorderLayout.CENTER);
		}
		else
		{
			add(panel, BorderLayout.WEST);	
		}
		
		tree = new JXTree(model);
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
		var root = (DefaultMutableTreeNode)model.getRoot();
		root.removeAllChildren();
		
		provider.listEditions().forEach(ed->{
			
			var edNode = new DefaultMutableTreeNode(ed);
			root.add(edNode);
			
			Arrays.asList(EnumItems.values()).forEach(t->{
				List<Packaging> pks = provider.get(ed, t);
				if(!pks.isEmpty())
				{
					var dir = new DefaultMutableTreeNode(t);
					pks.forEach(p->dir.add(new DefaultMutableTreeNode(p)));
					edNode.add(dir);
				}
			});
		});
		model.reload();

		tree.expandRow(0);
		
		if(view)
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

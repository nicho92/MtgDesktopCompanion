package org.magic.gui.components;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Packaging;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.renderer.MagicCardsTreeCellRenderer;
import org.magic.gui.renderer.MagicEditionIconListRenderer.SIZE;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.extra.PackagesProvider;
import org.magic.tools.UITools;

public class PackagesBrowserPanel extends MTGUIComponent{
	
	private static final long serialVersionUID = 1L;
	private transient PackagesProvider provider;
	private DefaultTreeModel model;
	private ZoomableJPanel panelDraw;
	private JTree tree;
	
	
	public PackagesBrowserPanel() {
		provider = PackagesProvider.inst();
		initGUI();
	}

	
	public void setMagicEdition(MagicEdition ed)
	{
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
		root.setUserObject(ed);
		root.removeAllChildren();
		Arrays.asList(Packaging.TYPE.values()).forEach(t->{
			List<Packaging> pks = provider.get(ed, t);
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
		
		initTree();
		
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
		for (int i = 0; i < 2; i++) {
		    tree.expandRow(i);
		}		
		panelDraw.setImg(null);
		
	}

	public static void main(String[] args) {
		
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		
		JFrame f = new JFrame();
		PackagesBrowserPanel pane = new PackagesBrowserPanel();
		
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(pane,BorderLayout.CENTER);
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
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

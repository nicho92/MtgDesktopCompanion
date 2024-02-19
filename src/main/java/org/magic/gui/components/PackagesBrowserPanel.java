package org.magic.gui.components;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jdesktop.swingx.JXTree;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGSealedProvider;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.renderer.MagicCardsTreeCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;

public class PackagesBrowserPanel extends MTGUIComponent{

	private static final long serialVersionUID = 1L;
	private DefaultTreeModel model;
	private ImagePanel panelDraw;
	private JXTree tree;
	private boolean view;
	private DefaultMutableTreeNode firstItem;


	public PackagesBrowserPanel(boolean viewThumbnail) {
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


	public void init(MTGEdition ed)
	{
		var root = (DefaultMutableTreeNode)model.getRoot();
		root.setUserObject(ed);
		root.removeAllChildren();
		
		
		
		
		Arrays.asList(EnumItems.values()).forEach(t->{
			List<MTGSealedProduct> pks = MTG.getEnabledPlugin(MTGSealedProvider.class).get(ed, t);
			logger.trace("loading {} {}",ed,pks);
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
		firstItem =new DefaultMutableTreeNode("Loading...");
		model = new DefaultTreeModel(firstItem);
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

				if(selectedNode!=null && (selectedNode.getUserObject() instanceof MTGSealedProduct msp))
					load(msp);
			});
		


		SwingWorker<Void, Void> sw  = new SwingWorker<>(){
			@Override
			protected Void doInBackground() throws Exception {
				initTree();
				return null;
			}
			@Override
			protected void done() {
				reload();
			}
		};
		ThreadManager.getInstance().runInEdt(sw, "Loading sealed tree");

		
		
		
	}

	public void load(MTGSealedProduct p)
	{
			panelDraw.setImg(MTG.getEnabledPlugin(MTGSealedProvider.class).getPictureFor(p));
			panelDraw.revalidate();
			panelDraw.repaint();
	}



	public List<MTGSealedProduct> getSelecteds() {
		
		var ret = new ArrayList<MTGSealedProduct>();
		
		
		for(var p : tree.getSelectionPaths()) {
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)p.getLastPathComponent();
			
			if(selectedNode!=null && (selectedNode.getUserObject() instanceof MTGSealedProduct msp))
			{
				ret.add(msp);
			}
			
		}
		
		return ret;
	}
	
	
	public void initTree() throws IOException
	{
		var root = (DefaultMutableTreeNode)model.getRoot();
		root.removeAllChildren();

		MTG.getEnabledPlugin(MTGCardsProvider.class).listEditions().stream().sorted().forEach(ed->{

			var edNode = new DefaultMutableTreeNode(ed);
			root.add(edNode);

			Arrays.asList(EnumItems.values()).forEach(t->{
				List<MTGSealedProduct> pks = MTG.getEnabledPlugin(MTGSealedProvider.class).get(ed, t);
				if(!pks.isEmpty())
				{
					var dir = new DefaultMutableTreeNode(t);
					pks.forEach(p->dir.add(new DefaultMutableTreeNode(p)));
					edNode.add(dir);
				}
			});
		});



		if(view)
			panelDraw.setImg(null);
		
		
		firstItem.setUserObject("Package");
		
	}

	public void reload()
	{
		model.reload();
		tree.expandRow(0);
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

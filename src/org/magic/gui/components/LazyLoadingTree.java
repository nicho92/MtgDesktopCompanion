package org.magic.gui.components;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.api.interfaces.MagicDAO;
import org.magic.services.MagicFactory;



public class LazyLoadingTree extends JTree {

	private DefaultTreeModel model;
	private MagicDAO dao;
	private MyNode root;
	private MagicCardsProvider prov;
	
	static final Logger logger = LogManager.getLogger(LazyLoadingTree.class.getName());

	
	public LazyLoadingTree() throws ClassNotFoundException, SQLException {
		
		dao=MagicFactory.getInstance().getEnabledDAO();
		prov=MagicFactory.getInstance().getEnabledProviders();
		
		root = new MyNode("Collection");
		model = new DefaultTreeModel(root);
		
		setModel(model);
		setShowsRootHandles(true);
		
		root.loadChildren(model);
		
		addTreeWillExpandListener(new TreeWillExpandListener() {

            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                TreePath path = event.getPath();
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)path.getLastPathComponent();
                
                if (selectedNode.getUserObject() instanceof String) {
                	MyNode node = (MyNode) path.getLastPathComponent();
                	node.loadChildren(model);
                }
                else if (selectedNode.getUserObject() instanceof MagicCollection) {
                	MyNode node = (MyNode) path.getLastPathComponent();
                	node.loadChildren(model);
                    
                }
                else  if (selectedNode.getUserObject() instanceof MagicEdition) {
                	MyNode node = (MyNode) path.getLastPathComponent();
                	node.loadChildren(model);
                    
                }
                else  if (selectedNode.getUserObject() instanceof MagicCard) {
                //	MyNode node = (MyNode) path.getLastPathComponent();
                    
                }
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

            }
        });
		
		
		
	}

public class MyNode extends DefaultMutableTreeNode implements Comparable<MyNode>
{
	private boolean loaded;
	private Object userObject;
	private boolean leaf = false;
	
	public void setLoaded(boolean b)
	{
		loaded=b;
	}
	
	@Override
	public boolean isLeaf() {
		return leaf;
	}
	
	public MyNode(Object c)
	{
		userObject=c;
		setUserObject(c);
		add(new DefaultMutableTreeNode("Loading...", false));
		if(c instanceof MagicCard)
		{
			setAllowsChildren(false);
			leaf=true;
		}
			
	}
	
	private void setChildren(List<MyNode> children) 
    {
        removeAllChildren();
        setAllowsChildren(children.size() > 0);
        for (MutableTreeNode node : children) {
            add(node);
        }
        setLoaded(true);
    }
	
	public void loadChildren(final DefaultTreeModel model) 
	{
     
		if (loaded) 
		{
          //  return ;
        }
    	
		 if(userObject instanceof String)
			loadCollection();
		
        if(userObject instanceof MagicCollection)
			loadEditionFromCollection((MagicCollection)userObject);
        
		if(userObject instanceof MagicEdition)
		{
			 MagicCollection col = new MagicCollection();
	        				 col.setName(getPath()[1].toString());
	       
			loadCardsFromEdition(col,(MagicEdition)userObject);
		}
		
    }
	
	private void loadCollection() {
		SwingWorker<List<MyNode>, Void> worker = new SwingWorker<List<MyNode>, Void>() {
            @Override
            protected List<MyNode> doInBackground() throws Exception {

                List<MyNode> children = new ArrayList<LazyLoadingTree.MyNode>();
                for(MagicCollection c : dao.getCollections())
                {
                	MyNode n = new MyNode(c);
                	children.add(n);
            	}
                return children;
            }

            @Override
            protected void done() {
                try {
                    setChildren(get());
                    model.nodeStructureChanged(MyNode.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.done();
            }
        };
        worker.execute();
		
		
	}

	private void loadCardsFromEdition(final MagicCollection col, final MagicEdition ed) {
		 
        SwingWorker<List<MyNode>, Void> worker = new SwingWorker<List<MyNode>, Void>() {
            @Override
            protected List<MyNode> doInBackground() throws Exception {
            	logger.debug("loading cards from " + col+"/"+ed);
                
                List<MyNode> children = new ArrayList<LazyLoadingTree.MyNode>();
                for(MagicCard card : dao.getCardsFromCollection(col, ed))
                {
                	MyNode n = new MyNode(card);
                	children.add(n);
            	}
                return children;
            }

            @Override
            protected void done() {
                try {
                	logger.debug("loading cards from " + col+"/"+ed +" done");
                    setChildren(get());
                    model.nodeStructureChanged(MyNode.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.done();
            }
        };
        worker.execute();
		
	}

	private void loadEditionFromCollection(final MagicCollection c) {
		 
        SwingWorker<List<MyNode>, Void> worker = new SwingWorker<List<MyNode>, Void>() {
            @Override
            protected List<MyNode> doInBackground() throws Exception {
            	logger.debug("loading editions from " + c);
                List<MyNode> children = new ArrayList<LazyLoadingTree.MyNode>();
                for(String ed : dao.getEditionsIDFromCollection(c))
                {
                	MyNode n = new MyNode(prov.getSetById(ed));
                	children.add(n);
            	}
               Collections.sort(children);
                return children;
            }

            @Override
            protected void done() {
                try {
                	 logger.debug("loading editions from " + c +  " done");
                    setChildren(get());
                    model.nodeStructureChanged(MyNode.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.done();
            }
        };
        worker.execute();
		
	}
	
	@Override
	public int compareTo(MyNode o) {
		return this.toString().compareTo(o.toString());
	}
    }

public void refresh() {
	model.reload();
	
}

}

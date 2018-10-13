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

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.sorters.CardsEditionSorter;

public class LazyLoadingTree extends JTree {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DefaultTreeModel model;
	private MyNode root;

	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public LazyLoadingTree() throws ClassNotFoundException, SQLException {
		root = new MyNode("Collection");
		model = new DefaultTreeModel(root);

		setModel(model);
		setShowsRootHandles(true);
		root.loadChildren();
		addTreeWillExpandListener(new TreeWillExpandListener() {
			@Override
			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
				TreePath path = event.getPath();
				MyNode node = (MyNode) path.getLastPathComponent();
				node.loadChildren();
			}

			@Override
			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
				// do nothing
			}
		});
	}

	public class MyNode extends DefaultMutableTreeNode implements Comparable<MyNode> {
		private static final long serialVersionUID = 1L;
		private transient Object obj;
		private boolean leaf = false;

		@Override
		public boolean isLeaf() {
			return leaf;
		}

		public MyNode(Object c) {
			obj = c;
			setUserObject(c);
			add(new DefaultMutableTreeNode(MTGControler.getInstance().getLangService().getCapitalize("LOADING"),
					false));
			if (c instanceof MagicCard) {
				setAllowsChildren(false);
				leaf = true;
			}

		}

		private void setChildren(List<MyNode> children) {
			removeAllChildren();
			setAllowsChildren(!children.isEmpty());
			for (MutableTreeNode node : children) {
				add(node);
			}
		}

		public void loadChildren() {
			if (obj instanceof String)
				loadCollection();

			if (obj instanceof MagicCollection)
				loadEditionFromCollection((MagicCollection) obj);

			if (obj instanceof MagicEdition) {
				MagicCollection col = new MagicCollection();
				col.setName(getPath()[1].toString());

				loadCardsFromEdition(col, (MagicEdition) obj);
			}

		}

		private void loadCollection() {
			SwingWorker<List<MyNode>, Void> worker = new SwingWorker<List<MyNode>, Void>() {
				@Override
				protected List<MyNode> doInBackground() throws Exception {

					List<MyNode> children = new ArrayList<>();
					for (MagicCollection c : MTGControler.getInstance().getEnabled(MTGDao.class).getCollections()) {
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
						logger.error(e);
					}
					super.done();
				}
			};
			worker.execute();

		}

		private void loadCardsFromEdition(final MagicCollection col, final MagicEdition ed) {

			SwingWorker<List<MyNode>, Void> worker = new SwingWorker<List<MyNode>, Void>() {
				@Override
				protected List<MyNode> doInBackground() {
					logger.debug("loading cards from " + col + "/" + ed);

					List<MyNode> children = new ArrayList<>();
					try {
						List<MagicCard> res = MTGControler.getInstance().getEnabled(MTGDao.class).listCardsFromCollection(col,
								ed);
						Collections.sort(res, new CardsEditionSorter());

						for (MagicCard card : res) {
							MyNode n = new MyNode(card);
							children.add(n);
						}
					} catch (SQLException e) {
						logger.error("unknow edition " + ed.getId());
					}
					return children;
				}

				@Override
				protected void done() {
					try {
						logger.debug("loading cards from " + col + "/" + ed + " done");
						setChildren(get());
						model.nodeStructureChanged(MyNode.this);
					} catch (Exception e) {
						logger.error("error loading tree",e);
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
					List<MyNode> children = new ArrayList<>();
					for (String ed : MTGControler.getInstance().getEnabled(MTGDao.class).getEditionsIDFromCollection(c)) {
						MyNode n = new MyNode(MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetById(ed));
						children.add(n);
					}
					Collections.sort(children);
					return children;
				}

				@Override
				protected void done() {
					try {
						logger.debug("loading editions from " + c + " done");
						setChildren(get());
						model.nodeStructureChanged(MyNode.this);
					} catch (Exception e) {
						logger.error(e);
					}
					super.done();
				}
			};
			worker.execute();

		}

		@Override
		public int hashCode() {
			return toString().hashCode();
		}

		@Override
		public int compareTo(MyNode o) {
			return this.toString().compareTo(o.toString());
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null)
				return false;

			return obj.toString() == this.toString();
		}

	}

	public void refresh() {
		model.reload();

	}

	public void refresh(DefaultMutableTreeNode curr) {
		model.reload(curr);
		
	}

}

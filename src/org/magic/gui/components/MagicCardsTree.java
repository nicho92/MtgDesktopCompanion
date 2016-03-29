package org.magic.gui.components;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTree;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.api.interfaces.MagicDAO;

public class MagicCardsTree extends JXTree {
	
	DefaultMutableTreeNode nodeSet;
	private MagicDAO dao;
	private MagicCardsProvider provider;
	DefaultMutableTreeNode root;
	
	
	static final Logger logger = LogManager.getLogger(MagicCardsTree.class.getName());

	public DefaultMutableTreeNode getnodeSet()
	{
		return nodeSet;
	}
	
	public void refresh()
	{
		//init();
		((DefaultTreeModel)getModel()).reload();
	}
	

	public void init()
	{
		setModel(new DefaultTreeModel(
				new DefaultMutableTreeNode("Collections") {
					{
						
						new Thread(new Runnable() {
							public void run() {
								List<MagicCollection> collection = null;
								try {
									collection = dao.getCollections();
								} catch (Exception e) {
									e.printStackTrace();
								}
								
								for(MagicCollection me : collection)
								{
									logger.debug("loading cards from " + me);
									nodeSet = new DefaultMutableTreeNode(me);
									add(nodeSet);
									
									try {
										List<MagicEdition> editions = provider.searchSetByCriteria(null, null);
										for(MagicEdition ed : editions)
										{
											List<MagicCard> cards = dao.getCardsFromCollection(me,ed);
											if(cards.size()>0)
											{
												DefaultMutableTreeNode nodeEd = new DefaultMutableTreeNode(ed);
											
												nodeSet.add(nodeEd);

												for(MagicCard mc : cards)
													nodeEd.add(new DefaultMutableTreeNode(mc));
											}
										}
										
									} catch (Exception e) {
									e.printStackTrace();	
									}
								}
								expandPath(getPathForRow(0));
							}
						}).start();
					}
				}
			));
		
		
	}
	
	
	public MagicCardsTree(final MagicCardsProvider provider,final MagicDAO dao) throws Exception {
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.provider=provider;
		this.dao=dao;
		init();
	
	}


	}

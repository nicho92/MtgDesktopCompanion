package org.magic.gui;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGAlert;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.GedPanel;
import org.magic.gui.components.GroupedShoppingPanel;
import org.magic.gui.components.JLazyLoadingTree;
import org.magic.gui.components.PackagesBrowserPanel;
import org.magic.gui.components.PricesTablePanel;
import org.magic.gui.components.card.CardSearchPanel;
import org.magic.gui.components.card.CardStockPanel;
import org.magic.gui.components.card.CardsEditionTablePanel;
import org.magic.gui.components.card.MagicCardDetailPanel;
import org.magic.gui.components.card.MagicEditionDetailPanel;
import org.magic.gui.components.card.TokensTablePanel;
import org.magic.gui.components.charts.HistoryPricesPanel;
import org.magic.gui.components.charts.ManaRepartitionPanel;
import org.magic.gui.components.charts.RarityRepartitionPanel;
import org.magic.gui.components.charts.TypeRepartitionPanel;
import org.magic.gui.components.deck.CardsDeckCheckerPanel;
import org.magic.gui.components.dialog.MassCollectionImporterDialog;
import org.magic.gui.components.dialog.MassMoverDialog;
import org.magic.gui.components.dialog.WebSiteGeneratorDialog;
import org.magic.gui.components.tech.ObjectViewerPanel;
import org.magic.gui.models.MagicEditionsTableModel;
import org.magic.gui.renderer.MagicCardsTreeCellRenderer;
import org.magic.gui.renderer.MagicCollectionTableCellRenderer;
import org.magic.services.CardsManagerService;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;
import org.magic.services.workers.WebsiteExportWorker;


@SuppressWarnings("rawtypes")
public class CollectionPanelGUI extends MTGUIComponent {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JXTable tableEditions;
	private transient MTGCardsProvider provider;
	private transient MTGDao dao;
	private JLazyLoadingTree tree;
	private TreePath path;
	private MTGCollection selectedcol;
	private transient MagicEditionDetailPanel magicEditionDetailPanel;
	private HistoryPricesPanel historyPricesPanel;
	private ObjectViewerPanel jsonPanel;
	private JPopupMenu popupMenuEdition;
	private JPopupMenu popupMenuCards;
	private MagicEditionsTableModel model;
	private AbstractBuzyIndicatorComponent buzy;
	private TypeRepartitionPanel typeRepartitionPanel;
	private ManaRepartitionPanel manaRepartitionPanel;
	private RarityRepartitionPanel rarityRepartitionPanel;
	private MagicCardDetailPanel magicCardDetailPanel;
	private CardStockPanel stockPanel;
	private CardsDeckCheckerPanel deckPanel;
	private CardsEditionTablePanel cardsSetPanel;
	private TokensTablePanel tokensPanel;
	private JTabbedPane panneauTreeTable;
	private JButton btnAdd;
	private JButton btnRefresh;
	private JButton btnRemove;
	private JButton btnAddAllSet;
	private JButton btnMassCollection;
	private JButton btnGenerateWebSite;
	private JSplitPane splitListPanel;
	private JSplitPane splitPane;
	private PackagesBrowserPanel packagePanel;
	private PricesTablePanel pricePanel;
	private GroupedShoppingPanel groupShopPanel;
	private GedPanel gedPanel;

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_COLLECTION;
	}

	@Override
	public String getTitle() {
		return capitalize("COLLECTION_MODULE");
	}



	public CollectionPanelGUI() throws IOException, SQLException, ClassNotFoundException {
		this.provider = getEnabledPlugin(MTGCardsProvider.class);
		this.dao = getEnabledPlugin(MTGDao.class);
		initGUI();
	}

	@Override
	public void onFirstShowing() {
		
		try {
			initPopupCollection();
		} catch (SQLException e1) {
		logger.error(e1);
		}
		
		initActions();
		
		splitListPanel.setDividerLocation(.70);
		splitPane.setDividerLocation(.5);
		buzy.start();
		buzy.setText("Loading");
		var init = new SwingWorker<List<MTGEdition>, Void>() {
				@Override
				protected List<MTGEdition> doInBackground() throws Exception {
					return provider.listEditions();
				}
				@Override
				protected void done() {
					try {
						model.init(get());
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					} catch (Exception e) {
						logger.error(e);
					}
					buzy.end();
					tableEditions.packAll();
				}
			};

			ThreadManager.getInstance().runInEdt(init, "calculate collection");
	}

	public void initGUI() throws SQLException, ClassNotFoundException {

		JPanel panneauHaut;
		JPanel panneauGauche;
		JPanel panelTotal;
		JPanel panneauDroite;
		MagicCollectionTableCellRenderer render;
		gedPanel = new GedPanel<>();

		//////// INIT COMPONENTS
		panneauHaut = new JPanel();
		packagePanel = new PackagesBrowserPanel(true);
		btnAdd = UITools.createBindableJButton(null, MTGConstants.ICON_NEW, KeyEvent.VK_N, "new Collection");
		btnRefresh =  UITools.createBindableJButton(null, MTGConstants.ICON_REFRESH, KeyEvent.VK_R, "Collection refresh");
		btnRemove = UITools.createBindableJButton(null, MTGConstants.ICON_DELETE, KeyEvent.VK_D, "Collection delete");
		btnAddAllSet =UITools.createBindableJButton(null, MTGConstants.ICON_CHECK, KeyEvent.VK_A, "Collection addAll");
		groupShopPanel = new GroupedShoppingPanel();
		btnMassCollection = UITools.createBindableJButton(null, MTGConstants.ICON_MASS_IMPORT, KeyEvent.VK_I, "Collection massImport");
		btnGenerateWebSite = UITools.createBindableJButton(null, MTGConstants.ICON_WEBSITE_24, KeyEvent.VK_W, "Collection website");
		cardsSetPanel = new CardsEditionTablePanel();
		deckPanel = new CardsDeckCheckerPanel();
		splitListPanel = new JSplitPane();
		splitPane = new JSplitPane();
		panneauGauche = new JPanel();
		tokensPanel = new TokensTablePanel();
		panelTotal = new JPanel();
		panneauDroite = new JPanel();
		render = new MagicCollectionTableCellRenderer();
		panneauTreeTable = new JTabbedPane();
		
		buzy = AbstractBuzyIndicatorComponent.createProgressComponent();
		
		magicEditionDetailPanel = new MagicEditionDetailPanel(false);
		magicCardDetailPanel = new MagicCardDetailPanel(true);
		typeRepartitionPanel = new TypeRepartitionPanel(false);
		manaRepartitionPanel = new ManaRepartitionPanel(false);
		rarityRepartitionPanel = new RarityRepartitionPanel(false);
		stockPanel = new CardStockPanel();
		historyPricesPanel = new HistoryPricesPanel(true);
		jsonPanel = new ObjectViewerPanel();
		tree = new JLazyLoadingTree();
		pricePanel = new PricesTablePanel();

		//////// MODELS
		model = new MagicEditionsTableModel();
		tableEditions = UITools.createNewTable(model,true);


		///////// CONFIGURE COMPONENTS
		splitListPanel.setDividerLocation(0.5);
		splitListPanel.setResizeWeight(0.5);
		tree.setRootVisible(false);
		btnRemove.setEnabled(false);
		btnAddAllSet.setEnabled(false);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

		tree.setCellRenderer(new MagicCardsTreeCellRenderer());

		magicCardDetailPanel.setPreferredSize(new Dimension(0, 0));
		magicCardDetailPanel.enableThumbnail(true);


		UITools.setDefaultRenderer(tableEditions, render);
		tableEditions.setRowHeight(25);
		
		

		///////// LAYOUT
		setLayout(new BorderLayout(0, 0));
		panneauDroite.setLayout(new BorderLayout());
		panneauGauche.setLayout(new BorderLayout(0, 0));

		///////// ADD PANELS
		add(panneauHaut, BorderLayout.NORTH);
		panneauHaut.add(btnAdd);
		panneauHaut.add(btnRefresh);
		panneauHaut.add(btnRemove);
		panneauHaut.add(btnAddAllSet);
		panneauHaut.add(btnMassCollection);
		panneauHaut.add(btnGenerateWebSite);
		panneauHaut.add(buzy);
		add(splitListPanel, BorderLayout.CENTER);
		splitListPanel.setRightComponent(panneauDroite);
		panneauDroite.add(splitPane, BorderLayout.CENTER);
		splitPane.setLeftComponent(panneauTreeTable);
		panneauTreeTable.addTab(capitalize("COLLECTION"), MTGConstants.ICON_TAB_BACK,new JScrollPane(tree), null);
		panneauTreeTable.addTab(capitalize("CARDS"), MTGConstants.ICON_TAB_CARD,cardsSetPanel, null);
		UITools.addTab(panneauTreeTable, tokensPanel);


		splitPane.setRightComponent(getContextTabbedPane());
		splitListPanel.setLeftComponent(panneauGauche);
		panneauGauche.add(new JScrollPane(tableEditions));
		panneauGauche.add(panelTotal, BorderLayout.SOUTH);
			
		addContextComponent(magicCardDetailPanel);
		addContextComponent(magicEditionDetailPanel);
		addContextComponent(packagePanel);
		addContextComponent(pricePanel);
		addContextComponent(groupShopPanel);
		addContextComponent(typeRepartitionPanel);
		addContextComponent(manaRepartitionPanel);
		addContextComponent(rarityRepartitionPanel);
		addContextComponent(stockPanel);
		addContextComponent(historyPricesPanel);
		addContextComponent(deckPanel);
		addContextComponent(gedPanel);
		
		if(MTG.readPropertyAsBoolean("debug-json-panel"))
			addContextComponent(jsonPanel);

		///////// Labels
		btnAdd.setToolTipText(MTGControler.getInstance().getLangService().get("COLLECTION_ADD"));
		btnRefresh.setToolTipText(capitalize("COLLECTION_REFRESH"));
		btnRemove.setToolTipText(capitalize("ITEM_SELECTED_REMOVE"));
		btnAddAllSet.setToolTipText(capitalize("COLLECTION_SET_FULL"));
		btnMassCollection.setToolTipText(capitalize("COLLECTION_IMPORT"));
		btnGenerateWebSite.setToolTipText(capitalize("GENERATE_WEBSITE"));


		UITools.sort(tableEditions,3,SortOrder.DESCENDING);

			
	}
	
	@SuppressWarnings("unchecked")
	public void initCardSelectionGui(MTGCard mc, MTGCollection col)
	{
		magicCardDetailPanel.init(mc);
		magicEditionDetailPanel.init(mc.getEdition());
		deckPanel.init(mc);
		pricePanel.init(mc);
		packagePanel.init(mc.getEdition());
		jsonPanel.init(mc);
		gedPanel.init(MTGCard.class,mc);


		try {
			stockPanel.init(mc,col);
			stockPanel.enabledAdd(true);
		}
		catch(NullPointerException e)
		{
			//do nothing
		}
		historyPricesPanel.init(mc, null, mc.getName());


	}


	@SuppressWarnings("unchecked")
	private void initActions()
	{

		btnRefresh.addActionListener(e -> {

		buzy.start();

		SwingWorker<Void, Void> sw = new SwingWorker<>()
		{
			@Override
			protected void process(List<Void> chunks) {
				buzy.progress();
			}

			 protected Void doInBackground() {
					try {
						model.calculate();
					} catch (Exception ex) {
						logger.error(ex);
					}
				 return null;
			 }

			 @Override
			protected void done() {
				model.fireTableDataChanged();
				tree.refresh();
				buzy.end();
			}


		};

		ThreadManager.getInstance().runInEdt(sw,"calculate collection");
		});


		splitPane.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent componentEvent) {
				splitPane.setDividerLocation(.5);
				removeComponentListener(this);
			}
		});


		tree.addTreeSelectionListener(tse -> {
			path = tse.getPath();
			btnRemove.setEnabled(true);
			btnAddAllSet.setEnabled(false);

			final DefaultMutableTreeNode curr = (DefaultMutableTreeNode) path.getLastPathComponent();

			if (curr.getUserObject() instanceof String) {
	
				stockPanel.enabledAdd(false);
			}

			if (curr.getUserObject() instanceof MTGCollection col) {
				selectedcol = col;
				stockPanel.enabledAdd(false);
				gedPanel.init(MTGCollection.class,selectedcol);
				ThreadManager.getInstance().executeThread(new MTGRunnable() {

					@Override
					protected void auditedRun() {
						try {

							List<MTGCard> list = dao.listCardsFromCollection(selectedcol);
							rarityRepartitionPanel.init(list);
							typeRepartitionPanel.init(list);
							manaRepartitionPanel.init(list);
							groupShopPanel.init(list);
							jsonPanel.init(curr.getUserObject());

						} catch (Exception e) {
							logger.error("error",e);
						}

					}


				}, "Calculate Collection cards");

			}

			if (curr.getUserObject() instanceof MTGEdition ed) {

		
				magicEditionDetailPanel.init(ed);
				packagePanel.init(ed);
				stockPanel.enabledAdd(false);
				gedPanel.init(MTGEdition.class,ed);

				ThreadManager.getInstance().executeThread(new MTGRunnable() {

					@Override
					protected void auditedRun() {
						try {
							
							if(curr.getParent()==null)
								return;
							
							
							var collec = (MTGCollection) ((DefaultMutableTreeNode) curr.getParent()).getUserObject();
							var list = dao.listCardsFromCollection(collec,ed);
							rarityRepartitionPanel.init(list);
							typeRepartitionPanel.init(list);
							manaRepartitionPanel.init(list);
							groupShopPanel.init(list);
							historyPricesPanel.init(null, ed,curr.getUserObject().toString());
							jsonPanel.init(curr.getUserObject());

						} catch (Exception e) {
							logger.error("error refresh {} : {}",curr.getUserObject(),e.getLocalizedMessage());
						}

					}


				}, "Calculate Editions cards");
			}

			if (curr.getUserObject() instanceof MTGCard) {

				var card = (MTGCard) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
				try {
					
					if(curr.getParent()!=null)
							initCardSelectionGui(card,(MTGCollection) ((DefaultMutableTreeNode) curr.getParent().getParent()).getUserObject());
				}
				catch(Exception e)
				{
					logger.error("error updating {} in {}" ,card,curr.getParent(),e);
				}
			}
		});

		cardsSetPanel.getTable().getSelectionModel().addListSelectionListener(me-> {

			if(!me.getValueIsAdjusting() && cardsSetPanel.getSelectedCard()!=null) {
					cardsSetPanel.enabledImport(true);
					initCardSelectionGui(cardsSetPanel.getSelectedCard(),null);
			}
		});

		tokensPanel.getTable().getSelectionModel().addListSelectionListener(me-> {

			if(!me.getValueIsAdjusting() && tokensPanel.getSelectedCard()!=null) {
				initCardSelectionGui(tokensPanel.getSelectedCard(),null);
			}
		});



		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if (SwingUtilities.isRightMouseButton(e)) {
					int row = tree.getClosestRowForLocation(e.getX(), e.getY());
					tree.setSelectionRow(row);

					final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

					if (node.getUserObject() instanceof MTGEdition) {
						popupMenuEdition.show(e.getComponent(), e.getX(), e.getY());

					}
					if (node.getUserObject() instanceof MTGCard) {
						popupMenuCards.show(e.getComponent(), e.getX(), e.getY());
					}
					if (node.getUserObject() instanceof MTGCollection col) {
						var p = new JPopupMenu();
						var it = new JMenuItem(capitalize("MASS_MOVEMENTS"),MTGConstants.ICON_COLLECTION);
						var itSync = new JMenuItem(capitalize("IMPORT_FROM",MTGControler.getInstance().getLangService().get("STOCK_MODULE")),MTGConstants.ICON_COLLECTION);


						p.add(it);
						p.add(itSync);

						it.addActionListener(ae -> {
							var d = new MassMoverDialog(col, null);
							d.setVisible(true);
							if(d.hasChange())
								tree.refresh(node);

							logger.trace("closing mass import with change ={}",d.hasChange());
						});

						itSync.addActionListener(ae->{

								buzy.start();
								var sw = new SwingWorker<List<MTGCard>, MTGCard>(){

										@Override
										protected void done() {
											buzy.end();
											try {
												JOptionPane.showMessageDialog(null, "OK : " + get().size() + " items added in collection","Synchronized", JOptionPane.INFORMATION_MESSAGE);
											}catch(InterruptedException ex)
											{
												Thread.currentThread().interrupt();
											}
											catch (Exception e) {
												MTGControler.getInstance().notify(e);
											}
										}

										@Override
										protected void process(List<MTGCard> chunks) {
											buzy.progressSmooth(chunks.size());
										}

										@Override
										protected List<MTGCard> doInBackground() throws Exception {
											return getEnabledPlugin(MTGDao.class).synchronizeCollection((MTGCollection) node.getUserObject());
										}
									};
									ThreadManager.getInstance().runInEdt(sw,"synchronize stocks and collection");
						});


						p.show(e.getComponent(), e.getX(), e.getY());
					}

				}
			}
		});

		btnMassCollection.addActionListener(ae -> {
			var diag = new MassCollectionImporterDialog();

			if(magicEditionDetailPanel.getMagicEdition()!=null)
				diag.setDefaultEdition(magicEditionDetailPanel.getMagicEdition());

			diag.setVisible(true);
			try {
				model.calculate();
			} catch (Exception e) {
				logger.error(e);
			}
			model.fireTableDataChanged();
		});


		btnGenerateWebSite.addActionListener(ae -> ThreadManager.getInstance().invokeLater(new MTGRunnable() {

			@Override
			protected void auditedRun() {
				try {

					var diag = new WebSiteGeneratorDialog(dao.listCollections());
					diag.setVisible(true);
					if (diag.value()) {
						var max = 0;
						for (MTGCollection col : diag.getSelectedCollections())
							max += dao.getCardsCount(col, null);

						buzy.start(max);
						var sw = new WebsiteExportWorker(diag.getTemplate(), diag.getDest(), diag.getSelectedCollections(), diag.getPriceProviders(), buzy);
						ThreadManager.getInstance().runInEdt(sw,"website generation");
					}

				} catch (Exception e) {
					logger.error("error generating website", e);
					buzy.end();
					MTGControler.getInstance().notify(e);
				}
			}
		}, "Opening WebSite Export dialog"));

	    btnAddAllSet.addActionListener(ae ->{
	    	var popupMenu = new JPopupMenu("Title");
			try {
					for(MTGCollection c : getEnabledPlugin(MTGDao.class).listCollections())
					{
						var cutMenuItem = new JMenuItem(c.getName(),MTGConstants.ICON_COLLECTION);
						initAddAllSet(cutMenuItem);
						popupMenu.add(cutMenuItem);
					}
				} catch (Exception e1) {
					logger.error(e1);
			}
			btnAddAllSet.setComponentPopupMenu(popupMenu);
			var b=(Component)ae.getSource();
	    	var p=b.getLocationOnScreen();
	    	popupMenu.show(this,0,0);
	    	popupMenu.setLocation(p.x,p.y+b.getHeight());
	    });



		tableEditions.getSelectionModel().addListSelectionListener(me-> {
			if(!me.getValueIsAdjusting()) 
			{
		    	  try 
		    	  {
					MTGEdition ed = UITools.getTableSelection(tableEditions, 1);
						
					if(ed==null)
					    return;
				  
					magicEditionDetailPanel.init(ed);
					cardsSetPanel.init(ed);
					packagePanel.init(ed);
					historyPricesPanel.init(null, ed, ed.getSet());
					jsonPanel.init(ed);
					btnRemove.setEnabled(false);
					btnAddAllSet.setEnabled(true);
					
					panneauTreeTable.setTitleAt(1, ed.getSet());
					panneauTreeTable.setSelectedIndex(1);
					tokensPanel.init(ed);
					gedPanel.init(MTGEdition.class,ed);
					
					
					
					var sw = new AbstractObservableWorker<List<MTGCard>, Void, MTGCardsProvider>(buzy,getEnabledPlugin(MTGCardsProvider.class)) {

						@Override
						protected List<MTGCard> doInBackground() throws Exception {
							return plug.searchCardByEdition(ed);
						}
						
						@Override
						protected void notifyEnd() {
							manaRepartitionPanel.init(getResult());
							rarityRepartitionPanel.init(getResult());
							typeRepartitionPanel.init(getResult());
						}
					};
					ThreadManager.getInstance().runInEdt(sw, "loading cards from " +ed);

		    	  }
		    	  catch(Exception e)
		    	  {
		    		  logger.error(e);
		    		  buzy.end();
		    	  }
			}
			
			
		});
		
		
		
		btnAdd.addActionListener(e -> {
			String name = JOptionPane.showInputDialog(capitalize("NAME") + " ?");

			if(name==null||name.isEmpty())
				return;

			var collectionAdd = new MTGCollection(name);
			try {
				dao.saveCollection(collectionAdd);
				((JLazyLoadingTree.MyNode) getJTree().getModel().getRoot()).add(new DefaultMutableTreeNode(collectionAdd));
				getJTree().refresh();
				initPopupCollection();
			} catch (Exception ex) {
				logger.error(ex);
				MTGControler.getInstance().notify(ex);
			}
		});


		btnRemove.addActionListener(evt -> {
			MTGCollection col = (MTGCollection) ((DefaultMutableTreeNode) path.getPathComponent(1)).getUserObject();
			var res = 0;

			
			DefaultMutableTreeNode curr = (DefaultMutableTreeNode) path.getLastPathComponent();
			if (curr.getUserObject() instanceof MTGCard) {
				MTGCard card = (MTGCard) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();

				try {
					res = JOptionPane.showConfirmDialog(null, capitalize("CONFIRM_COLLECTION_ITEM_DELETE", card, col));
					if (res == JOptionPane.YES_OPTION) {
						dao.removeCard(card, col);
					}
				} catch (SQLException e) {
					MTGControler.getInstance().notify(e);
				}
			}
			if (curr.getUserObject() instanceof MTGEdition) {
				MTGEdition me = (MTGEdition) ((DefaultMutableTreeNode) path.getPathComponent(2)).getUserObject();

				try {
					res = JOptionPane.showConfirmDialog(null, MTGControler.getInstance().getLangService()
							.getCapitalize("CONFIRM_COLLECTION_ITEM_DELETE", me, col));
					if (res == JOptionPane.YES_OPTION) {
						dao.removeEdition(me, col);
					}
				} catch (SQLException e) {
					MTGControler.getInstance().notify(e);
				}
			}
			if (curr.getUserObject() instanceof MTGCollection) {
				try {
					res = JOptionPane.showConfirmDialog(null, capitalize("CONFIRM_COLLECTION_DELETE", col, dao.getCardsCount(col, null)));
					if (res == JOptionPane.YES_OPTION) {
						dao.removeCollection(col);
					}
				} catch (SQLException e) {
					MTGControler.getInstance().notify(e);
				}
			}

			if (res == JOptionPane.YES_OPTION) {
				try {
					tree.removeNodeFromParent(curr);
				} catch (Exception e) {
					MTGControler.getInstance().notify(e);
				}

			}
		});
	}



	public void initAddAllSet(JMenuItem it)
	{

		it.addActionListener(evt -> {
			List<MTGEdition> eds = UITools.getTableSelections(tableEditions, 1);

			int res = JOptionPane.showConfirmDialog(null, capitalize(
					"CONFIRM_COLLECTION_ITEM_ADDITION", eds, it.getText()));

			if (res == JOptionPane.YES_OPTION)
			{
				try {
					List<MTGCard> list = new ArrayList<>();

					for(MTGEdition e : eds)
						for(MTGCard mc : provider.searchCardByEdition(e))
							list.add(mc);

						buzy.start(list.size());
						logger.debug("save {} cards from {}",list.size(),eds);


						SwingWorker<Void, MTGCard> sw = new SwingWorker<>()
						{

							@Override
							protected Void doInBackground() {
								for (MTGCard mc : list) {
									var col = new MTGCollection(it.getText());
									try {
										CardsManagerService.saveCard(mc, col,null);
										publish(mc);

									} catch (SQLException e) {
										logger.error(e);
									}
								}
								return null;
							}

							@Override
							protected void done() {
								model.calculate();
								model.fireTableDataChanged();
								buzy.end();
							}

							@Override
							protected void process(List<MTGCard> chunks) {
								buzy.progressSmooth(chunks.size());
							}



						};

						ThreadManager.getInstance().runInEdt(sw, "insert sets");
					} catch (Exception e) {
						logger.error(e);
						MTGControler.getInstance().notify(e);

					}

			}

		});

	}


	public void initPopupCollection() throws SQLException {

		popupMenuEdition = new JPopupMenu();
		popupMenuCards = new JPopupMenu();

		var menuItemAdd = new JMenu(capitalize("ADD_MISSING_CARDS_IN"));
		var menuItemRemoveFrom = new JMenu(capitalize("REMOVE_CARDS_IN"));
		var menuItemMove = new JMenu(capitalize("MOVE_CARD_TO"));
		var menuItemMoveEditions = new JMenu(capitalize("MOVE_EDITION_TO"));
		
		
		menuItemAdd.setIcon(MTGConstants.ICON_COLLECTION);
		menuItemMove.setIcon(MTGConstants.ICON_COLLECTION);
		menuItemRemoveFrom.setIcon(MTGConstants.ICON_COLLECTION);
		menuItemMoveEditions.setIcon(MTGConstants.ICON_COLLECTION);
		
		
		var menuItemAlerts = new JMenuItem(capitalize("ADD_CARDS_ALERTS"),MTGConstants.ICON_ALERT);

		for (MTGCollection mc : dao.listCollections()) {
			var adds = new JMenuItem(mc.getName(),MTGConstants.ICON_COLLECTION);
			var movs = new JMenuItem(mc.getName(),MTGConstants.ICON_COLLECTION);
			var rmvs = new JMenuItem(mc.getName(),MTGConstants.ICON_COLLECTION);
			var movEd = new JMenuItem(mc.getName(),MTGConstants.ICON_COLLECTION);

			
			movEd.addActionListener(e -> {
				var nodeCol = ((DefaultMutableTreeNode) path.getPathComponent(1));
				var nodeCd = ((DefaultMutableTreeNode) path.getPathComponent(2));
				var ed = (MTGEdition) nodeCd.getUserObject();
				var oldCol = new MTGCollection(nodeCol.getUserObject().toString());
				
				final String collec = ((JMenuItem) e.getSource()).getText();
				var nmagicCol = new MTGCollection(collec);
				
				buzy.start();

				var sw = new SwingWorker<Void, MTGCard>(){

						@Override
						protected void done() {
							buzy.end();
							nodeCd.removeFromParent();
							tree.refresh(((DefaultMutableTreeNode) path.getPathComponent(1)));
						}

						@Override
						protected void process(List<MTGCard> chunks) {
							buzy.progressSmooth(chunks.size());
						}

						@Override
						protected Void doInBackground() throws Exception {
							CardsManagerService.moveCard(ed, oldCol, nmagicCol,buzy);
							return null;
						}
					};
				ThreadManager.getInstance().runInEdt(sw,"move editions");
			});

			
			movs.addActionListener(e -> {
				var nodeCol = ((DefaultMutableTreeNode) path.getPathComponent(1));
				var nodeCd = ((DefaultMutableTreeNode) path.getPathComponent(3));
				var card = (MTGCard) nodeCd.getUserObject();
				var oldCol = (MTGCollection) nodeCol.getUserObject();

				final String collec = ((JMenuItem) e.getSource()).getText();
				var nmagicCol = new MTGCollection(collec);
				try {
					CardsManagerService.moveCard(card, oldCol, nmagicCol,null);
					nodeCd.removeFromParent();
					nodeCol.add(new DefaultMutableTreeNode(card));
					tree.refresh(((DefaultMutableTreeNode) path.getPathComponent(2)));
				} catch (SQLException e1) {
					logger.error("error ",e1);
				}

			});

			adds.addActionListener(e -> {
				try {

						final String destinationCollection = ((JMenuItem) e.getSource()).getText();

						var node = ((DefaultMutableTreeNode) path.getPathComponent(2));
						var me = (MTGEdition) node.getUserObject();

						var col = new MTGCollection(destinationCollection);
						var sets = provider.searchCardByEdition(me);

						var sourceCol = new MTGCollection(node.getPath()[1].toString());
						var list = dao.listCardsFromCollection(sourceCol, me);

						logger.debug("{} items in {}/{}", list.size(), sourceCol,me);
						sets.removeAll(list);
						logger.debug("{} items to insert in {}/{}",sets.size(),col,me);

				buzy.start(sets.size());


					var sw = new SwingWorker<Void, MTGCard>(){
						@Override
						protected void done() {
							buzy.end();
							tree.refresh(node);
						}

						@Override
						protected void process(List<MTGCard> chunks) {
							buzy.progressSmooth(chunks.size());
						}

						@Override
						protected Void doInBackground() throws Exception {
							for (MTGCard m : sets)
							{
								try{
									CardsManagerService.saveCard(m, col,null);
									publish(m);
								}catch(Exception e)
								{
									logger.error(e);
								}

							}
							return null;
						}
					};
					ThreadManager.getInstance().runInEdt(sw,"move missing cards");


				}catch(Exception ex)
				{
					MTGControler.getInstance().notify(ex);
				}
			});

			rmvs.addActionListener(e -> {
				try {

						final String selectedCols = ((JMenuItem) e.getSource()).getText();
						var node = ((DefaultMutableTreeNode) path.getPathComponent(2));
						var me = (MTGEdition) node.getUserObject();
						var coldest = new MTGCollection(selectedCols);
						var colcurrent = new MTGCollection(node.getPath()[1].toString());
						List<MTGCard> listtoDelete = dao.listCardsFromCollection(colcurrent, me);
						logger.trace("{} items to remove from {}/{}",listtoDelete.size(),coldest,me);

				buzy.start(listtoDelete.size());

				SwingWorker<Void, MTGCard> sw = new SwingWorker<>(){

						@Override
						protected void done() {
							buzy.end();
							tree.refresh(node);
						}

						@Override
						protected void process(List<MTGCard> chunks) {
							buzy.progressSmooth(chunks.size());
						}

						@Override
						protected Void doInBackground() throws Exception {
							for (MTGCard m : listtoDelete)
							{
								dao.removeCard(m, coldest);
								publish(m);
							}
							return null;
						}
					};
					ThreadManager.getInstance().runInEdt(sw,"remove duplicate cards");


				}catch(Exception ex)
				{
					MTGControler.getInstance().notify(ex);
				}
			});



			menuItemMoveEditions.add(movEd);
			menuItemAdd.add(adds);
			menuItemMove.add(movs);
			menuItemRemoveFrom.add(rmvs);
			
			
		}

		var menuItemOpen = new JMenuItem(capitalize("OPEN"),MTGConstants.ICON_OPEN);
		menuItemOpen.addActionListener(e -> {
			var col = (MTGCollection) ((DefaultMutableTreeNode) path.getPathComponent(1)).getUserObject();
			var edition = (MTGEdition) ((DefaultMutableTreeNode) path.getPathComponent(2)).getUserObject();
			try {
				((MagicGUI)SwingUtilities.getRoot(this)).setSelectedTab(0);
				CardSearchPanel.getInstance().open(getEnabledPlugin(MTGDao.class).listCardsFromCollection(col, edition));
			} catch (SQLException e1) {
				logger.error(e1);
			}

		});
		popupMenuEdition.add(menuItemOpen);
	
		var it = new JMenuItem(capitalize("MASS_MOVEMENTS"),MTGConstants.ICON_COLLECTION);
		it.addActionListener(e -> {
			var col = (MTGCollection) ((DefaultMutableTreeNode) path.getPathComponent(1)).getUserObject();
			var edition = (MTGEdition) ((DefaultMutableTreeNode) path.getPathComponent(2)).getUserObject();
			var d = new MassMoverDialog(col, edition);
			d.setVisible(true);
			logger.debug("closing mass import with change ={}",d.hasChange());

			if(d.hasChange())
				tree.refresh((DefaultMutableTreeNode)path.getPathComponent(2));


		});

		menuItemAlerts.addActionListener(e ->{
			var col = (MTGCollection) ((DefaultMutableTreeNode) path.getPathComponent(1)).getUserObject();
			var edition = (MTGEdition) ((DefaultMutableTreeNode) path.getPathComponent(2)).getUserObject();

			try {
				for(MTGCard mc : getEnabledPlugin(MTGDao.class).listCardsFromCollection(col, edition))
				{
					var alert = new MTGAlert();
					alert.setCard(mc);
					alert.setPrice(0.0);
					getEnabledPlugin(MTGDao.class).saveAlert(alert);
				}
			} catch (SQLException e1) {
				logger.error(e1);
			}
		});

		popupMenuEdition.add(it);
		popupMenuEdition.add(menuItemAlerts);
		popupMenuEdition.add(menuItemAdd);
		popupMenuEdition.add(menuItemRemoveFrom);
		popupMenuEdition.add(menuItemMoveEditions);
		popupMenuCards.add(menuItemMove);
	}

	public JLazyLoadingTree getJTree() {
		return tree;
	}

}

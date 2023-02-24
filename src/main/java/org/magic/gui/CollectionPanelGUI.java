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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
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
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsExport.MODS;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.CardSearchPanel;
import org.magic.gui.components.CardStockPanel;
import org.magic.gui.components.CardsDeckCheckerPanel;
import org.magic.gui.components.CardsEditionTablePanel;
import org.magic.gui.components.GedPanel;
import org.magic.gui.components.GroupedShoppingPanel;
import org.magic.gui.components.JLazyLoadingTree;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.gui.components.MagicEditionDetailPanel;
import org.magic.gui.components.ObjectViewerPanel;
import org.magic.gui.components.PackagesBrowserPanel;
import org.magic.gui.components.PricesTablePanel;
import org.magic.gui.components.TokensTablePanel;
import org.magic.gui.components.charts.HistoryPricesPanel;
import org.magic.gui.components.charts.ManaRepartitionPanel;
import org.magic.gui.components.charts.RarityRepartitionPanel;
import org.magic.gui.components.charts.TypeRepartitionPanel;
import org.magic.gui.components.dialog.MassCollectionImporterDialog;
import org.magic.gui.components.dialog.MassMoverDialog;
import org.magic.gui.components.dialog.WebSiteGeneratorDialog;
import org.magic.gui.components.widgets.JExportButton;
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
	private MagicCollection selectedcol;
	private transient MagicEditionDetailPanel magicEditionDetailPanel;
	private HistoryPricesPanel historyPricesPanel;
	private ObjectViewerPanel jsonPanel;
	private JPopupMenu popupMenuEdition;
	private JPopupMenu popupMenuCards;
	private MagicEditionsTableModel model;
	private AbstractBuzyIndicatorComponent progressBar;
	private TypeRepartitionPanel typeRepartitionPanel;
	private ManaRepartitionPanel manaRepartitionPanel;
	private RarityRepartitionPanel rarityRepartitionPanel;
	private MagicCardDetailPanel magicCardDetailPanel;
	private CardStockPanel stockPanel;
	private JLabel lblTotal;
	private CardsDeckCheckerPanel deckPanel;
	private CardsEditionTablePanel cardsSetPanel;
	private TokensTablePanel tokensPanel;
	private JTabbedPane panneauTreeTable;
	private JButton btnAdd;
	private JButton btnRefresh;
	private JButton btnRemove;
	private JButton btnAddAllSet;
	private JExportButton btnExport;
	private JButton btnMassCollection;
	private JButton btnGenerateWebSite;
	private JSplitPane splitListPanel;
	private JSplitPane splitPane;
	private List<MagicCard> listExport;
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
		
		splitListPanel.setDividerLocation(.45);
		splitPane.setDividerLocation(.5);
		progressBar.start();
		progressBar.setText("Loading");
		SwingWorker<List<MagicEdition>, Void> init = new SwingWorker<>() {
				@Override
				protected List<MagicEdition> doInBackground() throws Exception {
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
					progressBar.end();
					tableEditions.packAll();
					initTotal();

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
		btnExport = new JExportButton(MODS.EXPORT);
		UITools.bindJButton(btnExport, KeyEvent.VK_E, "Collection export");
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
		
		progressBar = AbstractBuzyIndicatorComponent.createProgressComponent();
		lblTotal = new JLabel();
		magicEditionDetailPanel = new MagicEditionDetailPanel(false);
		magicCardDetailPanel = new MagicCardDetailPanel();
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
		tableEditions = UITools.createNewTable(model);
		UITools.initTableFilter(tableEditions);


		///////// CONFIGURE COMPONENTS
		splitListPanel.setDividerLocation(0.5);
		splitListPanel.setResizeWeight(0.5);
		tree.setRootVisible(false);
		btnRemove.setEnabled(false);
		btnAddAllSet.setEnabled(false);
		btnExport.setEnabled(false);

		splitPane.setResizeWeight(0.5);
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
		panneauHaut.add(btnExport);
		panneauHaut.add(btnGenerateWebSite);
		panneauHaut.add(progressBar);
		add(splitListPanel, BorderLayout.CENTER);
		splitListPanel.setRightComponent(panneauDroite);
		panneauDroite.add(splitPane, BorderLayout.CENTER);
		splitPane.setLeftComponent(panneauTreeTable);
		panneauTreeTable.addTab(capitalize("COLLECTION"), MTGConstants.ICON_BACK,new JScrollPane(tree), null);
		panneauTreeTable.addTab(capitalize("CARDS"), MTGConstants.ICON_TAB_CARD,cardsSetPanel, null);
		UITools.addTab(panneauTreeTable, tokensPanel);


		splitPane.setRightComponent(getContextTabbedPane());
		splitListPanel.setLeftComponent(panneauGauche);
		panneauGauche.add(new JScrollPane(tableEditions));
		panneauGauche.add(panelTotal, BorderLayout.SOUTH);
		panelTotal.add(lblTotal);


		
		
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
		btnExport.setToolTipText(capitalize("EXPORT_AS"));
		btnMassCollection.setToolTipText(capitalize("COLLECTION_IMPORT"));
		btnGenerateWebSite.setToolTipText(capitalize("GENERATE_WEBSITE"));


		UITools.sort(tableEditions,3,SortOrder.DESCENDING);

			
	}
	
	@SuppressWarnings("unchecked")
	public void initCardSelectionGui(MagicCard mc, MagicCollection col)
	{
		magicCardDetailPanel.init(mc);
		magicEditionDetailPanel.init(mc.getCurrentSet());
		deckPanel.init(mc);
		pricePanel.init(mc);
		btnExport.setEnabled(false);
		packagePanel.init(mc.getCurrentSet());
		jsonPanel.init(mc);
		gedPanel.init(MagicCard.class,mc);


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


	private void initTotal() {
			lblTotal.setText("Total : " + model.getCountDefaultLibrary() + "/" + model.getCountTotal() + " ("+ new DecimalFormat("#0.00").format(  ((double)model.getCountDefaultLibrary() / (double)model.getCountTotal())*100)  +"%)");

	}



	@SuppressWarnings("unchecked")
	private void initActions()
	{

		btnRefresh.addActionListener(e -> {

		progressBar.start();

		SwingWorker<Void, Void> sw = new SwingWorker<>()
		{
			@Override
			protected void process(List<Void> chunks) {
				progressBar.progress();
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
				initTotal();
				model.fireTableDataChanged();
				tree.refresh();
				progressBar.end();
			}


		};

		ThreadManager.getInstance().runInEdt(sw,"calculate collection");
		});



		btnExport.initCardsExport(new Callable<MagicDeck>() {
			@Override
			public MagicDeck call() throws Exception {
				DefaultMutableTreeNode curr = (DefaultMutableTreeNode) path.getLastPathComponent();
				MagicCollection mc = null;
				MagicEdition ed = null;

				if (curr.getUserObject() instanceof MagicEdition edition) {
					ed =edition;
					mc = (MagicCollection) ((DefaultMutableTreeNode) curr.getParent()).getUserObject();
				} else {
					mc = (MagicCollection) curr.getUserObject();
				}

				try {
					if (ed == null)
						listExport= dao.listCardsFromCollection(mc);
					else
						listExport= dao.listCardsFromCollection(mc, ed);
				}
				catch(Exception e)
				{
					MTGControler.getInstance().notify(e);

				}
				return MagicDeck.toDeck(listExport);
			}
		}, progressBar);



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
			btnExport.setEnabled(true);
			final DefaultMutableTreeNode curr = (DefaultMutableTreeNode) path.getLastPathComponent();

			if (curr.getUserObject() instanceof String) {
				btnExport.setEnabled(false);
				stockPanel.enabledAdd(false);
			}

			if (curr.getUserObject() instanceof MagicCollection col) {
				selectedcol = col;
				stockPanel.enabledAdd(false);
				gedPanel.init(MagicCollection.class,selectedcol);
				ThreadManager.getInstance().executeThread(new MTGRunnable() {

					@Override
					protected void auditedRun() {
						try {

							List<MagicCard> list = dao.listCardsFromCollection(selectedcol);
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

			if (curr.getUserObject() instanceof MagicEdition ed) {

		
				magicEditionDetailPanel.init(ed);
				packagePanel.init(ed);
				stockPanel.enabledAdd(false);
				gedPanel.init(MagicEdition.class,ed);

				ThreadManager.getInstance().executeThread(new MTGRunnable() {

					@Override
					protected void auditedRun() {
						try {

							MagicCollection collec = (MagicCollection) ((DefaultMutableTreeNode) curr.getParent()).getUserObject();
							List<MagicCard> list = dao.listCardsFromCollection(collec,ed);
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

			if (curr.getUserObject() instanceof MagicCard) {

				var card = (MagicCard) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
				try {
					initCardSelectionGui(card,(MagicCollection) ((DefaultMutableTreeNode) curr.getParent().getParent()).getUserObject());
				}catch(Exception e)
				{
					logger.error("error updating {} in {}" ,card,curr.getParent());
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

					if (node.getUserObject() instanceof MagicEdition) {
						popupMenuEdition.show(e.getComponent(), e.getX(), e.getY());

					}
					if (node.getUserObject() instanceof MagicCard) {
						popupMenuCards.show(e.getComponent(), e.getX(), e.getY());
					}
					if (node.getUserObject() instanceof MagicCollection col) {
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

								progressBar.start();
								SwingWorker<List<MagicCard>, MagicCard> sw = new SwingWorker<>(){

										@Override
										protected void done() {
											progressBar.end();
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
										protected void process(List<MagicCard> chunks) {
											progressBar.progressSmooth(chunks.size());
										}

										@Override
										protected List<MagicCard> doInBackground() throws Exception {
											return getEnabledPlugin(MTGDao.class).synchronizeCollection((MagicCollection) node.getUserObject());
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
						for (MagicCollection col : diag.getSelectedCollections())
							max += dao.getCardsCount(col, null);

						progressBar.start(max);
						var sw = new WebsiteExportWorker(diag.getTemplate(), diag.getDest(), diag.getSelectedCollections(), diag.getPriceProviders(), progressBar);
						ThreadManager.getInstance().runInEdt(sw,"website generation");
					}

				} catch (Exception e) {
					logger.error("error generating website", e);
					progressBar.end();
					MTGControler.getInstance().notify(e);
				}
			}
		}, "Opening WebSite Export dialog"));

	    btnAddAllSet.addActionListener(ae ->{
	    	var popupMenu = new JPopupMenu("Title");
			try {
					for(MagicCollection c : getEnabledPlugin(MTGDao.class).listCollections())
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
			if(!me.getValueIsAdjusting()) {
		    	  try {
		    		 int row = tableEditions.getSelectedRow();
					MagicEdition ed = (MagicEdition) tableEditions.getValueAt(row, 1);
					magicEditionDetailPanel.init(ed);
					packagePanel.init(ed);
					historyPricesPanel.init(null, ed, ed.getSet());
					jsonPanel.init(ed);
					btnRemove.setEnabled(false);
					btnAddAllSet.setEnabled(true);
					btnExport.setEnabled(false);
					cardsSetPanel.init(ed);
					panneauTreeTable.setTitleAt(1, ed.getSet());
					panneauTreeTable.setSelectedIndex(1);
					tokensPanel.init(ed);


		    	  }
		    	  catch(Exception e)
		    	  {
		    		  progressBar.end();
		    	  }
			}
		});

		btnAdd.addActionListener(e -> {
			String name = JOptionPane.showInputDialog(capitalize("NAME") + " ?");

			if(name==null||name.isEmpty())
				return;

			var collectionAdd = new MagicCollection(name);
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
			MagicCollection col = (MagicCollection) ((DefaultMutableTreeNode) path.getPathComponent(1)).getUserObject();
			var res = 0;

			
			DefaultMutableTreeNode curr = (DefaultMutableTreeNode) path.getLastPathComponent();
			if (curr.getUserObject() instanceof MagicCard) {
				MagicCard card = (MagicCard) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();

				try {
					res = JOptionPane.showConfirmDialog(null, capitalize("CONFIRM_COLLECTION_ITEM_DELETE", card, col));
					if (res == JOptionPane.YES_OPTION) {
						CardsManagerService.removeCard(card, col);
					}
				} catch (SQLException e) {
					MTGControler.getInstance().notify(e);
				}
			}
			if (curr.getUserObject() instanceof MagicEdition) {
				MagicEdition me = (MagicEdition) ((DefaultMutableTreeNode) path.getPathComponent(2)).getUserObject();

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
			if (curr.getUserObject() instanceof MagicCollection) {
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
			List<MagicEdition> eds = UITools.getTableSelections(tableEditions, 1);

			int res = JOptionPane.showConfirmDialog(null, capitalize(
					"CONFIRM_COLLECTION_ITEM_ADDITION", eds, it.getText()));

			if (res == JOptionPane.YES_OPTION)
			{
				try {
					List<MagicCard> list = new ArrayList<>();

					for(MagicEdition e : eds)
						for(MagicCard mc : provider.searchCardByEdition(e))
							list.add(mc);

						progressBar.start(list.size());
						logger.debug("save {} cards from {}",list.size(),eds);


						SwingWorker<Void, MagicCard> sw = new SwingWorker<>()
						{

							@Override
							protected Void doInBackground() {
								for (MagicCard mc : list) {
									var col = new MagicCollection(it.getText());
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
								progressBar.end();
							}

							@Override
							protected void process(List<MagicCard> chunks) {
								progressBar.progressSmooth(chunks.size());
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
		var menuItemStocks = new JMenuItem(capitalize("ADD_CARDS_STOCKS"),MTGConstants.ICON_STOCK);

		for (MagicCollection mc : dao.listCollections()) {
			var adds = new JMenuItem(mc.getName(),MTGConstants.ICON_COLLECTION);
			var movs = new JMenuItem(mc.getName(),MTGConstants.ICON_COLLECTION);
			var rmvs = new JMenuItem(mc.getName(),MTGConstants.ICON_COLLECTION);
			var movEd = new JMenuItem(mc.getName(),MTGConstants.ICON_COLLECTION);

			
			movEd.addActionListener(e -> {
				var nodeCol = ((DefaultMutableTreeNode) path.getPathComponent(1));
				var nodeCd = ((DefaultMutableTreeNode) path.getPathComponent(2));
				var ed = (MagicEdition) nodeCd.getUserObject();
				var oldCol = new MagicCollection(nodeCol.getUserObject().toString());
				
				final String collec = ((JMenuItem) e.getSource()).getText();
				var nmagicCol = new MagicCollection(collec);
				
				progressBar.start();

				SwingWorker<Void, MagicCard> sw = new SwingWorker<>(){

						@Override
						protected void done() {
							progressBar.end();
							nodeCd.removeFromParent();
							tree.refresh(((DefaultMutableTreeNode) path.getPathComponent(1)));
						}

						@Override
						protected void process(List<MagicCard> chunks) {
							progressBar.progressSmooth(chunks.size());
						}

						@Override
						protected Void doInBackground() throws Exception {
							CardsManagerService.moveCard(ed, oldCol, nmagicCol,progressBar);
							return null;
						}
					};
				ThreadManager.getInstance().runInEdt(sw,"move editions");
			});

			
			movs.addActionListener(e -> {
				var nodeCol = ((DefaultMutableTreeNode) path.getPathComponent(1));
				var nodeCd = ((DefaultMutableTreeNode) path.getPathComponent(3));
				var card = (MagicCard) nodeCd.getUserObject();
				var oldCol = (MagicCollection) nodeCol.getUserObject();

				final String collec = ((JMenuItem) e.getSource()).getText();
				var nmagicCol = new MagicCollection(collec);
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

						DefaultMutableTreeNode node = ((DefaultMutableTreeNode) path.getPathComponent(2));
						MagicEdition me = (MagicEdition) node.getUserObject();

						var col = new MagicCollection(destinationCollection);
						List<MagicCard> sets = provider.searchCardByEdition(me);

						var sourceCol = new MagicCollection(node.getPath()[1].toString());
						List<MagicCard> list = dao.listCardsFromCollection(sourceCol, me);

						logger.debug("{} items in {}/{}", list.size(), sourceCol,me);
						sets.removeAll(list);
						logger.debug("{} items to insert in {}/{}",sets.size(),col,me);

				progressBar.start(sets.size());


				SwingWorker<Void, MagicCard> sw = new SwingWorker<>(){

						@Override
						protected void done() {
							progressBar.end();
							tree.refresh(node);
						}

						@Override
						protected void process(List<MagicCard> chunks) {
							progressBar.progressSmooth(chunks.size());
						}

						@Override
						protected Void doInBackground() throws Exception {
							for (MagicCard m : sets)
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
						var me = (MagicEdition) node.getUserObject();
						var coldest = new MagicCollection(selectedCols);
						var colcurrent = new MagicCollection(node.getPath()[1].toString());
						List<MagicCard> listtoDelete = dao.listCardsFromCollection(colcurrent, me);
						logger.trace("{} items to remove from {}/{}",listtoDelete.size(),coldest,me);

				progressBar.start(listtoDelete.size());

				SwingWorker<Void, MagicCard> sw = new SwingWorker<>(){

						@Override
						protected void done() {
							progressBar.end();
							tree.refresh(node);
						}

						@Override
						protected void process(List<MagicCard> chunks) {
							progressBar.progressSmooth(chunks.size());
						}

						@Override
						protected Void doInBackground() throws Exception {
							for (MagicCard m : listtoDelete)
							{
								CardsManagerService.removeCard(m, coldest);
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
			var col = (MagicCollection) ((DefaultMutableTreeNode) path.getPathComponent(1)).getUserObject();
			var edition = (MagicEdition) ((DefaultMutableTreeNode) path.getPathComponent(2)).getUserObject();
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
			var col = (MagicCollection) ((DefaultMutableTreeNode) path.getPathComponent(1)).getUserObject();
			var edition = (MagicEdition) ((DefaultMutableTreeNode) path.getPathComponent(2)).getUserObject();
			var d = new MassMoverDialog(col, edition);
			d.setVisible(true);
			logger.debug("closing mass import with change ={}",d.hasChange());

			if(d.hasChange())
				tree.refresh((DefaultMutableTreeNode)path.getPathComponent(2));


		});

		menuItemAlerts.addActionListener(e ->{
			var col = (MagicCollection) ((DefaultMutableTreeNode) path.getPathComponent(1)).getUserObject();
			var edition = (MagicEdition) ((DefaultMutableTreeNode) path.getPathComponent(2)).getUserObject();

			try {
				for(MagicCard mc : getEnabledPlugin(MTGDao.class).listCardsFromCollection(col, edition))
				{
					var alert = new MagicCardAlert();
					alert.setCard(mc);
					alert.setPrice(0.0);
					getEnabledPlugin(MTGDao.class).saveAlert(alert);
				}
			} catch (SQLException e1) {
				logger.error(e1);
			}
		});

		menuItemStocks.addActionListener(e ->{
			var col = (MagicCollection) ((DefaultMutableTreeNode) path.getPathComponent(1)).getUserObject();
			var edition = (MagicEdition) ((DefaultMutableTreeNode) path.getPathComponent(2)).getUserObject();

			try {
				var cards = getEnabledPlugin(MTGDao.class).listCardsFromCollection(col, edition);
				AbstractObservableWorker<Void, MagicCardStock, MTGDao> sw = new AbstractObservableWorker<>(progressBar,getEnabledPlugin(MTGDao.class),cards.size()) {

					@Override
					protected Void doInBackground() throws Exception {
							for(MagicCard mc : cards)
							{
								MagicCardStock st = MTGControler.getInstance().getDefaultStock();
								st.setProduct(mc);
								st.setMagicCollection(col);
								plug.saveOrUpdateCardStock(st);
							}
							return null;
					}
				};

				ThreadManager.getInstance().runInEdt(sw, "Stock updating");

			} catch (SQLException e1) {
				logger.error(e1);
			}
		});

		popupMenuEdition.add(it);
		popupMenuEdition.add(menuItemAlerts);
		popupMenuEdition.add(menuItemStocks);
		popupMenuEdition.add(menuItemAdd);
		popupMenuEdition.add(menuItemRemoveFrom);
		popupMenuEdition.add(menuItemMoveEditions);
		popupMenuCards.add(menuItemMove);
	}

	public JLazyLoadingTree getJTree() {
		return tree;
	}

}

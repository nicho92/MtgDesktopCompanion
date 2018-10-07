package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultRowSorter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.abstracts.AbstractCardExport.MODS;
import org.magic.gui.abstracts.MTGUIPanel;
import org.magic.gui.components.CardSearchPanel;
import org.magic.gui.components.CardStockPanel;
import org.magic.gui.components.CardsDeckCheckerPanel;
import org.magic.gui.components.JBuzyProgress;
import org.magic.gui.components.JSONPanel;
import org.magic.gui.components.LazyLoadingTree;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.gui.components.MagicEditionDetailPanel;
import org.magic.gui.components.PricesTablePanel;
import org.magic.gui.components.charts.HistoryPricesPanel;
import org.magic.gui.components.charts.ManaRepartitionPanel;
import org.magic.gui.components.charts.RarityRepartitionPanel;
import org.magic.gui.components.charts.TypeRepartitionPanel;
import org.magic.gui.components.dialog.MassCollectionImporterDialog;
import org.magic.gui.components.dialog.MassMoverDialog;
import org.magic.gui.components.dialog.WebSiteGeneratorDialog;
import org.magic.gui.models.MagicEditionsTableModel;
import org.magic.gui.renderer.MagicCardsTreeCellRenderer;
import org.magic.gui.renderer.MagicCollectionTableCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MagicWebSiteGenerator;
import org.magic.services.ThreadManager;
import org.magic.tools.UITools;
import org.utils.patterns.observer.Observable;

public class CollectionPanelGUI extends MTGUIPanel {
	
	private JXTable tableEditions;
	private transient MTGCardsProvider provider;
	private transient MTGDao dao;
	private LazyLoadingTree tree;
	private TreePath path;
	private MagicCollection selectedcol;
	private transient MagicEditionDetailPanel magicEditionDetailPanel;
	private HistoryPricesPanel historyPricesPanel;
	private JSONPanel jsonPanel;
	private JPopupMenu popupMenuEdition;
	private JPopupMenu popupMenuCards;
	private MagicEditionsTableModel model;
	private JBuzyProgress progressBar;
	private TypeRepartitionPanel typeRepartitionPanel;
	private ManaRepartitionPanel manaRepartitionPanel;
	private RarityRepartitionPanel rarityRepartitionPanel;
	private MagicCardDetailPanel magicCardDetailPanel;
	private CardStockPanel statsPanel;
	private JLabel lblTotal;
	private CardsDeckCheckerPanel deckPanel;
	
	private JButton btnRefresh;
	private JButton btnRemove;
	private JButton btnAddAllSet;
	private JButton btnExport;
	private JButton btnMassCollection;
	private JButton btnGenerateWebSite;
	private JSplitPane splitListPanel;
	private JSplitPane splitPane;
	
	
	private PricesTablePanel pricePanel;
	
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_COLLECTION;
	}
	
	@Override
	public String getTitle() {
		return MTGControler.getInstance().getLangService().getCapitalize("COLLECTION_MODULE");
	}
	
	
	
	public CollectionPanelGUI() throws IOException, SQLException, ClassNotFoundException {
		this.provider = MTGControler.getInstance().getEnabled(MTGCardsProvider.class);
		this.dao = MTGControler.getInstance().getEnabled(MTGDao.class);
		initGUI();
	}

	public void initGUI() throws IOException, SQLException, ClassNotFoundException {
		
		JTabbedPane tabbedPane;
		JPanel panneauHaut;
		JButton btnAdd;
		
		JPanel panneauGauche;
		JScrollPane scrollPane;
		JPanel panelTotal;
		JPanel panneauDroite;
		MagicCollectionTableCellRenderer render;


		//////// INIT COMPONENTS
		panneauHaut = new JPanel();
		btnAdd = new JButton(MTGConstants.ICON_NEW);
		btnRefresh = new JButton(MTGConstants.ICON_REFRESH);
		btnRemove = new JButton(MTGConstants.ICON_DELETE);
		btnAddAllSet = new JButton(MTGConstants.ICON_CHECK);
		btnExport = new JButton(MTGConstants.ICON_EXPORT);
		btnMassCollection = new JButton(MTGConstants.ICON_MASS_IMPORT);
		btnGenerateWebSite = new JButton(MTGConstants.ICON_WEBSITE);
		
		deckPanel = new CardsDeckCheckerPanel();
		splitListPanel = new JSplitPane();
		splitPane = new JSplitPane();
		panneauGauche = new JPanel();
		scrollPane = new JScrollPane();
		panelTotal = new JPanel();
		panneauDroite = new JPanel();
		render = new MagicCollectionTableCellRenderer();

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		progressBar = new JBuzyProgress();
		lblTotal = new JLabel();
		magicEditionDetailPanel = new MagicEditionDetailPanel(true, false);
		magicCardDetailPanel = new MagicCardDetailPanel();
		typeRepartitionPanel = new TypeRepartitionPanel();
		manaRepartitionPanel = new ManaRepartitionPanel();
		rarityRepartitionPanel = new RarityRepartitionPanel();
		statsPanel = new CardStockPanel();
		historyPricesPanel = new HistoryPricesPanel();
		jsonPanel = new JSONPanel();
		tree = new LazyLoadingTree();
		tableEditions = new JXTable();
		pricePanel = new PricesTablePanel();
		
		//////// MODELS
		model = new MagicEditionsTableModel();
		DefaultRowSorter<DefaultTableModel, Integer> sorterEditions = new TableRowSorter<>(model);
		model.init(provider.loadEditions());
		tableEditions.setModel(model);
		
		UITools.initTableFilter(tableEditions);
		

		///////// CONFIGURE COMPONENTS
		splitListPanel.setDividerLocation(0.5);
		splitListPanel.setResizeWeight(0.5);

		progressBar.setVisible(false);
		btnRemove.setEnabled(true);
		btnExport.setEnabled(false);

		splitPane.setResizeWeight(0.5);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		tree.setCellRenderer(new MagicCardsTreeCellRenderer());

		magicCardDetailPanel.setPreferredSize(new Dimension(0, 0));
		magicCardDetailPanel.enableThumbnail(true);

		tableEditions.setDefaultRenderer(Object.class, render);
		tableEditions.setDefaultRenderer(String.class, render);
		tableEditions.setDefaultRenderer(Integer.class, render);
		tableEditions.setDefaultRenderer(double.class, render);
		tableEditions.setDefaultRenderer(Boolean.class, render);
		tableEditions.setDefaultRenderer(ImageIcon.class, render);
		
		tableEditions.setRowHeight(25);
		tableEditions.setRowSorter(sorterEditions);

		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent componentEvent) {
				splitListPanel.setDividerLocation(.45);
				splitPane.setDividerLocation(.5);
				removeComponentListener(this);
			}

		});
		
		
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
		splitPane.setLeftComponent(new JScrollPane(tree));
		splitPane.setRightComponent(tabbedPane);
		splitListPanel.setLeftComponent(panneauGauche);
		panneauGauche.add(scrollPane);
		scrollPane.setViewportView(tableEditions);
		panneauGauche.add(panelTotal, BorderLayout.SOUTH);
		panelTotal.add(lblTotal);

		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("DETAILS"), MTGConstants.ICON_TAB_DETAILS,magicCardDetailPanel, null);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("CARD_EDITIONS"),  MTGConstants.ICON_BACK,magicEditionDetailPanel, null);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("PRICES"), MTGConstants.ICON_TAB_PRICES, pricePanel,null);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("CARD_TYPES"), MTGConstants.ICON_TAB_TYPE,typeRepartitionPanel, null);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("CARD_MANA"), MTGConstants.ICON_TAB_MANA,manaRepartitionPanel, null);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("CARD_RARITY"), MTGConstants.ICON_TAB_RARITY,rarityRepartitionPanel, null);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("STOCK_MODULE"), MTGConstants.ICON_TAB_STOCK, statsPanel,null);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("PRICE_VARIATIONS"), MTGConstants.ICON_TAB_VARIATIONS,historyPricesPanel, null);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("DECK_MODULE"), MTGConstants.ICON_TAB_DECK,deckPanel, null);
		
		
		if (MTGControler.getInstance().get("debug-json-panel").equalsIgnoreCase("true"))
			tabbedPane.addTab("Json", MTGConstants.ICON_TAB_JSON, jsonPanel, null);

		///////// Labels
		lblTotal.setText("Total : " + model.getCountDefaultLibrary() + "/" + model.getCountTotal());
		btnAdd.setToolTipText(MTGControler.getInstance().getLangService().get("COLLECTION_ADD"));
		btnRefresh.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("COLLECTION_REFRESH"));
		btnRemove.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("ITEM_SELECTED_REMOVE"));
		btnAddAllSet.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("COLLECTION_SET_FULL"));
		btnExport.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("EXPORT_AS"));
		btnMassCollection.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("COLLECTION_IMPORT"));
		btnGenerateWebSite.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("GENERATE_WEBSITE"));

		List<SortKey> keys = new ArrayList<>();
		SortKey sortKey = new SortKey(3, SortOrder.DESCENDING);// column index 2
		keys.add(sortKey);
		sorterEditions.setSortKeys(keys);

		tableEditions.packAll();

		initPopupCollection();

		tableEditions.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int row = tableEditions.getSelectedRow();
				MagicEdition ed = (MagicEdition) tableEditions.getValueAt(row, 1);
				magicEditionDetailPanel.setMagicEdition(ed);
				historyPricesPanel.init(null, ed, ed.getSet());
				jsonPanel.show(ed);
			}
		});

		btnAdd.addActionListener(e -> {
			String name = JOptionPane
					.showInputDialog(MTGControler.getInstance().getLangService().getCapitalize("NAME") + " ?");
			MagicCollection collectionAdd = new MagicCollection();
			collectionAdd.setName(name);
			try {
				dao.saveCollection(collectionAdd);
				((LazyLoadingTree.MyNode) getJTree().getModel().getRoot()).add(new DefaultMutableTreeNode(collectionAdd));
				getJTree().refresh();
				initPopupCollection();
			} catch (Exception ex) {
				logger.error(ex);
				MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),ex));
			}
		});

		

		initActions();

	}
	
	
	private void initActions()
	{
		
		btnRefresh.addActionListener(e ->

		ThreadManager.getInstance().execute(() -> {
			progressBar.setVisible(true);
			tree.refresh();
			try {
				model.calculate();
				lblTotal.setText("Total : " + model.getCountDefaultLibrary() + "/" + model.getCountTotal());
			} catch (Exception ex) {
				logger.error(ex);
			}
			model.fireTableDataChanged();
			progressBar.setVisible(false);
		}, "update Tree"));
		
		
		btnExport.addActionListener(ae -> {
			JPopupMenu menu = new JPopupMenu();

			for (final MTGCardsExport exp : MTGControler.getInstance().listEnabled(MTGCardsExport.class)) 
			{
				if (exp.getMods() == MODS.BOTH || exp.getMods() == MODS.EXPORT) {
					JMenuItem it = new JMenuItem();
					it.setIcon(exp.getIcon());
					it.setText(exp.getName());
					it.addActionListener(al -> ThreadManager.getInstance().execute(() -> {
						try {

							DefaultMutableTreeNode curr = (DefaultMutableTreeNode) path.getLastPathComponent();
							JFileChooser jf = new JFileChooser();

							MagicCollection mc = null;
							MagicEdition ed = null;

							if (curr.getUserObject() instanceof MagicEdition) {
								ed = (MagicEdition) curr.getUserObject();
								mc = (MagicCollection) ((DefaultMutableTreeNode) curr.getParent()).getUserObject();
							} else {
								mc = (MagicCollection) curr.getUserObject();
							}

							jf.setSelectedFile(new File(mc.getName() + exp.getFileExtension()));
							int result = jf.showSaveDialog(null);
							File f = jf.getSelectedFile();

							progressBar.start();
							exp.addObserver(progressBar);
							if (result == JFileChooser.APPROVE_OPTION) {
								if (ed == null)
									exp.export(dao.listCardsFromCollection(mc), f);
								else
									exp.export(dao.listCardsFromCollection(mc, ed), f);

								progressBar.end();
								
								MTGControler.getInstance().notify(new MTGNotification(
										MTGControler.getInstance().getLangService().getCapitalize("FINISHED"),
										MTGControler.getInstance().getLangService().combine("EXPORT", "FINISHED"),
										MESSAGE_TYPE.INFO
										));
							}

						} catch (Exception e) {
							logger.error("error in export",e);
							progressBar.end();
							MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),e));
						}
						finally
						{
							exp.removeObserver(progressBar);
						}
					}, "export collection with " + exp));

					menu.add(it);
				}
				
			}

			Component b = (Component) ae.getSource();
			Point p = b.getLocationOnScreen();
			menu.show(b, 0, 0);
			menu.setLocation(p.x, p.y + b.getHeight());

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

			final DefaultMutableTreeNode curr = (DefaultMutableTreeNode) path.getLastPathComponent();

			if (curr.getUserObject() instanceof String) {
				btnExport.setEnabled(false);
				statsPanel.enabledAdd(false);
			}

			if (curr.getUserObject() instanceof MagicCollection) {
				btnExport.setEnabled(true);
				selectedcol = (MagicCollection) curr.getUserObject();
				statsPanel.enabledAdd(false);
				btnExport.setEnabled(true);
				ThreadManager.getInstance().execute(() -> {
					try {

						List<MagicCard> list = dao.listCardsFromCollection(selectedcol);
						rarityRepartitionPanel.init(list);
						typeRepartitionPanel.init(list);
						manaRepartitionPanel.init(list);
						jsonPanel.show(curr.getUserObject());

					} catch (Exception e) {
						logger.error("error",e);
					}
				}, "Calculate Collection cards");
				
			}

			if (curr.getUserObject() instanceof MagicEdition) {
				magicEditionDetailPanel.setMagicEdition((MagicEdition) curr.getUserObject());
				btnExport.setEnabled(true);
				statsPanel.enabledAdd(false);
				ThreadManager.getInstance().execute(() -> {
					try {

						MagicCollection collec = (MagicCollection) ((DefaultMutableTreeNode) curr.getParent()).getUserObject();
						List<MagicCard> list = dao.listCardsFromCollection(collec, (MagicEdition) curr.getUserObject());
						rarityRepartitionPanel.init(list);
						typeRepartitionPanel.init(list);
						manaRepartitionPanel.init(list);
						historyPricesPanel.init(null, (MagicEdition) curr.getUserObject(),curr.getUserObject().toString());
						jsonPanel.show(curr.getUserObject());

					} catch (Exception e) {
						logger.error(e);
					}
				}, "Calculate Editions cards");
			}

			if (curr.getUserObject() instanceof MagicCard) {
				final MagicCard card = (MagicCard) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
				btnExport.setEnabled(false);
				magicCardDetailPanel.setMagicCard((MagicCard) curr.getUserObject());
				magicEditionDetailPanel.setMagicEdition(card.getCurrentSet());
				deckPanel.init((MagicCard) curr.getUserObject());
				magicCardDetailPanel.enableThumbnail(true);
				jsonPanel.show(curr.getUserObject());

				ThreadManager.getInstance().execute(() -> {
					
					try {
					statsPanel.initMagicCardStock(card,(MagicCollection) ((DefaultMutableTreeNode) curr.getParent().getParent()).getUserObject());
					statsPanel.enabledAdd(true);
					}
					catch(NullPointerException e)
					{
						//do nothing
					}
					
				
				}, "Update Collection");

				pricePanel.init(card,null);

				ThreadManager.getInstance().execute(() -> {
					try {
						historyPricesPanel.init(card, null, card.getName());
					} catch (Exception e) {
						logger.error("error history",e);
					}
				}, "update history");
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
					if (node.getUserObject() instanceof MagicCollection) {
						JPopupMenu p = new JPopupMenu();
						JMenuItem it = new JMenuItem("Mass movement");
						p.add(it);

						it.addActionListener(ae -> {
							MassMoverDialog d = new MassMoverDialog((MagicCollection) node.getUserObject(), null);
							d.setVisible(true);
							if(d.hasChange())
								tree.refresh(node);
							
							logger.trace("closing mass import with change =" + d.hasChange());
						});
						p.show(e.getComponent(), e.getX(), e.getY());
					}

				}
			}
		});

		btnMassCollection.addActionListener(ae -> {
			MassCollectionImporterDialog diag = new MassCollectionImporterDialog();
			diag.setVisible(true);
			try {
				model.calculate();
			} catch (Exception e) {
				logger.error(e);
			}
			model.fireTableDataChanged();
		});

		
		btnGenerateWebSite.addActionListener(ae -> ThreadManager.getInstance().execute(() -> {
			try {

				WebSiteGeneratorDialog diag = new WebSiteGeneratorDialog(dao.getCollections());
				diag.setVisible(true);
				if (diag.value()) {
				
					int max = 0;
					for (MagicCollection col : diag.getSelectedCollections())
						max += dao.getCardsCount(col, null);

					progressBar.start(max);
					MagicWebSiteGenerator gen = new MagicWebSiteGenerator(diag.getTemplate(),diag.getDest().getAbsolutePath());

					gen.addObserver(progressBar);
					gen.generate(diag.getSelectedCollections(), diag.getPriceProviders());

					int res = JOptionPane.showConfirmDialog(null,MTGControler.getInstance().getLangService().getCapitalize("WEBSITE_CONFIRMATION_VIEW"));

					if (res == JOptionPane.YES_OPTION) {
						Path p = Paths.get(diag.getDest().getAbsolutePath() + "/index.htm");
						Desktop.getDesktop().browse(p.toUri());
					}
					progressBar.end();
				}

			} catch (Exception e) {
				logger.error("error generating website", e);
				progressBar.end();
				MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),e));
			}
		}, "btnGenerateWebSite generate website"));

	    btnAddAllSet.addActionListener(ae ->{
			JPopupMenu popupMenu = new JPopupMenu("Title");
			try {
					for(MagicCollection c : MTGControler.getInstance().getEnabled(MTGDao.class).getCollections())
					{
						JMenuItem cutMenuItem = new JMenuItem(c.getName());
						initAddAllSet(cutMenuItem);
						popupMenu.add(cutMenuItem);
					}
				} catch (Exception e1) {
					logger.error(e1);
			}
			btnAddAllSet.setComponentPopupMenu(popupMenu);
	    	Component b=(Component)ae.getSource();
	    	Point p=b.getLocationOnScreen();
	    	popupMenu.show(this,0,0);
	    	popupMenu.setLocation(p.x,p.y+b.getHeight());
	    });
	    
	    
	    
	    
	    
	   
		btnRemove.addActionListener(evt -> {

			MagicCollection col = (MagicCollection) ((DefaultMutableTreeNode) path.getPathComponent(1)).getUserObject();
			int res = 0;

			DefaultMutableTreeNode curr = (DefaultMutableTreeNode) path.getLastPathComponent();
			if (curr.getUserObject() instanceof MagicCard) {
				MagicCard card = (MagicCard) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();

				try {
					res = JOptionPane.showConfirmDialog(null, MTGControler.getInstance().getLangService().getCapitalize("CONFIRM_COLLECTION_ITEM_DELETE", card, col));
					if (res == JOptionPane.YES_OPTION) {
						MTGControler.getInstance().removeCard(card, col);
						curr.removeFromParent();
					}
				} catch (SQLException e) {
					MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),e));
				}
			}
			if (curr.getUserObject() instanceof MagicEdition) {
				MagicEdition me = (MagicEdition) ((DefaultMutableTreeNode) path.getPathComponent(2)).getUserObject();

				try {
					res = JOptionPane.showConfirmDialog(null, MTGControler.getInstance().getLangService()
							.getCapitalize("CONFIRM_COLLECTION_ITEM_DELETE", me, col));
					if (res == JOptionPane.YES_OPTION) {
						dao.removeEdition(me, col);
						curr.removeFromParent();
					}
				} catch (SQLException e) {
					MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),e));
				}
			}
			if (curr.getUserObject() instanceof MagicCollection) {
				try {
					res = JOptionPane.showConfirmDialog(null, MTGControler.getInstance().getLangService()
							.getCapitalize("CONFIRM_COLLECTION_DELETE", col, dao.getCardsCount(col, null)));
					if (res == JOptionPane.YES_OPTION) {
						dao.removeCollection(col);
						curr.removeFromParent();
					}
				} catch (SQLException e) {
					MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),e));
				}
			}

			if (res == JOptionPane.YES_OPTION) {
				try {
					model.calculate();
				} catch (Exception e) {
					MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),e));
				}
				tree.refresh(curr);

			}
		});
	}
	
	
	
	public void initAddAllSet(JMenuItem it)
	{
		 
		it.addActionListener(evt -> {
			MagicEdition ed = (MagicEdition) tableEditions.getValueAt(tableEditions.getSelectedRow(), 1);

			int res = JOptionPane.showConfirmDialog(null, MTGControler.getInstance().getLangService().getCapitalize(
					"CONFIRM_COLLECTION_ITEM_ADDITION", ed, it.getText()));

			if (res == JOptionPane.YES_OPTION)
			{	
				try {
						
						List<MagicCard> list = provider.searchCardByEdition(ed);
						progressBar.start(list.size());
						logger.debug("save " + list.size() + " cards from " + ed.getId());
						
						ThreadManager.getInstance().execute(()->{
								for (MagicCard mc : list) {
									MagicCollection col = new MagicCollection(it.getText());
									try {
										MTGControler.getInstance().saveCard(mc, col);
										progressBar.progress();
									} catch (SQLException e) {
										logger.error(e);
									}
								}
								model.calculate();
								model.fireTableDataChanged();
								progressBar.end();
							
						}, "insert sets");
					} catch (Exception e) {
						MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),e));
						logger.error(e);
						progressBar.end();
					}
					
			}		
			
		});

	}
	

	public void initPopupCollection() throws SQLException {

		popupMenuEdition = new JPopupMenu();
		popupMenuCards = new JPopupMenu();

		JMenu menuItemAdd = new JMenu(MTGControler.getInstance().getLangService().getCapitalize("ADD_MISSING_CARDS_IN"));
		JMenu menuItemMove = new JMenu(MTGControler.getInstance().getLangService().getCapitalize("MOVE_CARD_TO"));
		JMenuItem menuItemAlerts = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("ADD_CARDS_ALERTS"));
		JMenuItem menuItemStocks = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("ADD_CARDS_STOCKS"));
		
		for (MagicCollection mc : dao.getCollections()) {
			JMenuItem adds = new JMenuItem(mc.getName());
			JMenuItem movs = new JMenuItem(mc.getName());

			movs.addActionListener(e -> {
				DefaultMutableTreeNode nodeCol = ((DefaultMutableTreeNode) path.getPathComponent(1));
				DefaultMutableTreeNode nodeCd = ((DefaultMutableTreeNode) path.getPathComponent(3));
				MagicCard card = (MagicCard) nodeCd.getUserObject();
				MagicCollection oldCol = (MagicCollection) nodeCol.getUserObject();

				final String collec = ((JMenuItem) e.getSource()).getText();
				MagicCollection nmagicCol = new MagicCollection();
				nmagicCol.setName(collec);

				try {
					dao.moveCard(card, oldCol, nmagicCol);
					nodeCd.removeFromParent();
					nodeCol.add(new DefaultMutableTreeNode(card));
					tree.refresh(((DefaultMutableTreeNode) path.getPathComponent(2)));
				} catch (SQLException e1) {
					logger.error(e1);
				}

			});

			adds.addActionListener(e -> {

				final String destinationCollection = ((JMenuItem) e.getSource()).getText();
				progressBar.setValue(0);
				progressBar.setVisible(true);
				ThreadManager.getInstance().execute(() -> {
					try {
						DefaultMutableTreeNode node = ((DefaultMutableTreeNode) path.getPathComponent(2));
						MagicEdition me = (MagicEdition) node.getUserObject();

						MagicCollection col = new MagicCollection();
						col.setName(destinationCollection);
						List<MagicCard> sets = provider.searchCardByEdition(me);

						MagicCollection sourceCol = new MagicCollection(node.getPath()[1].toString());
						List<MagicCard> list = dao.listCardsFromCollection(sourceCol, me);
						sets.removeAll(list);
						progressBar.start(sets.size());
						

						for (MagicCard m : sets)
						{
							MTGControler.getInstance().saveCard(m, col);
							progressBar.progress();
						}

						tree.refresh(node);
					} catch (Exception e1) {
						logger.error(e1);
						progressBar.end();
					}
				}, "btnAdds addCardsCollection");
			});

			menuItemAdd.add(adds);
			menuItemMove.add(movs);
		}

		JMenuItem menuItemOpen = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("OPEN"));
		menuItemOpen.addActionListener(e -> {
			MagicCollection col = (MagicCollection) ((DefaultMutableTreeNode) path.getPathComponent(1)).getUserObject();
			MagicEdition edition = (MagicEdition) ((DefaultMutableTreeNode) path.getPathComponent(2)).getUserObject();
			try {
				CardSearchPanel.getInstance()
						.open(MTGControler.getInstance().getEnabled(MTGDao.class).listCardsFromCollection(col, edition));
			} catch (SQLException e1) {
				logger.error(e1);
			}

		});
		popupMenuEdition.add(menuItemOpen);

		JMenuItem it = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("MASS_MOVEMENTS"));
		it.addActionListener(e -> {
			MagicCollection col = (MagicCollection) ((DefaultMutableTreeNode) path.getPathComponent(1)).getUserObject();
			MagicEdition edition = (MagicEdition) ((DefaultMutableTreeNode) path.getPathComponent(2)).getUserObject();
			MassMoverDialog d = new MassMoverDialog(col, edition);
			d.setVisible(true);
			logger.debug("closing mass import with change =" + d.hasChange());
			
			if(d.hasChange())
				tree.refresh((DefaultMutableTreeNode)path.getPathComponent(2));
			
			
		});
		
		menuItemAlerts.addActionListener(e ->{
			MagicCollection col = (MagicCollection) ((DefaultMutableTreeNode) path.getPathComponent(1)).getUserObject();
			MagicEdition edition = (MagicEdition) ((DefaultMutableTreeNode) path.getPathComponent(2)).getUserObject();
			
			try {
				for(MagicCard mc : MTGControler.getInstance().getEnabled(MTGDao.class).listCardsFromCollection(col, edition))
				{
					MagicCardAlert alert = new MagicCardAlert();
					alert.setCard(mc);
					alert.setPrice(0.0);
					MTGControler.getInstance().getEnabled(MTGDao.class).saveAlert(alert);
				}
			} catch (SQLException e1) {
				logger.error(e1);
			}
		});
		
		menuItemStocks.addActionListener(e ->{
			MagicCollection col = (MagicCollection) ((DefaultMutableTreeNode) path.getPathComponent(1)).getUserObject();
			MagicEdition edition = (MagicEdition) ((DefaultMutableTreeNode) path.getPathComponent(2)).getUserObject();
			
			try {
				for(MagicCard mc : MTGControler.getInstance().getEnabled(MTGDao.class).listCardsFromCollection(col, edition))
				{
					MagicCardStock st = MTGControler.getInstance().getDefaultStock();
					st.setMagicCard(mc);
					st.setMagicCollection(col);
					
					MTGControler.getInstance().getEnabled(MTGDao.class).saveOrUpdateStock(st);
				}
			} catch (SQLException e1) {
				logger.error(e1);
			}
		});
		
		popupMenuEdition.add(it);
		popupMenuEdition.add(menuItemAlerts);
		popupMenuEdition.add(menuItemStocks);
		popupMenuEdition.add(menuItemAdd);
		popupMenuCards.add(menuItemMove);
	}

	public LazyLoadingTree getJTree() {
		return tree;
	}

}

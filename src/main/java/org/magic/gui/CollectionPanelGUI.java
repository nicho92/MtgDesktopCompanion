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
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
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

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.exports.impl.CSVExport;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.components.CardSearchPanel;
import org.magic.gui.components.CardStockPanel;
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
import org.magic.gui.components.dialog.PriceCatalogExportDialog;
import org.magic.gui.components.dialog.WebSiteGeneratorDialog;
import org.magic.gui.models.MagicEditionsTableModel;
import org.magic.gui.renderer.MagicCardsTreeCellRenderer;
import org.magic.gui.renderer.MagicCollectionTableCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.MagicWebSiteGenerator;
import org.magic.services.ThreadManager;
import org.utils.patterns.observer.Observable;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

public class CollectionPanelGUI extends JPanel {
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	private JXTable tableEditions;
	private transient MTGCardsProvider provider;
	private transient MTGDao dao;
	private LazyLoadingTree tree;
	private TreePath path;
	private MagicCollection selectedcol;
	private transient MagicEditionDetailPanel magicEditionDetailPanel;
	private HistoryPricesPanel historyPricesPanel;
	private JSONPanel jsonPanel;
	private JPopupMenu popupMenuEdition = new JPopupMenu();
	private JPopupMenu popupMenuCards = new JPopupMenu();

	public CollectionPanelGUI() throws IOException, SQLException, ClassNotFoundException {
		this.provider = MTGControler.getInstance().getEnabledCardsProviders();
		this.dao = MTGControler.getInstance().getEnabledDAO();
		initGUI();
	}

	public void initGUI() throws IOException, SQLException, ClassNotFoundException {
		logger.info("init collection GUI");

		MagicEditionsTableModel model;
		JProgressBar progressBar;
		JTabbedPane tabbedPane;
		TypeRepartitionPanel typeRepartitionPanel;
		ManaRepartitionPanel manaRepartitionPanel;
		RarityRepartitionPanel rarityRepartitionPanel;
		MagicCardDetailPanel magicCardDetailPanel;
		CardStockPanel statsPanel;
		JLabel lblTotal;

		//////// INIT COMPONENTS
		JPanel panneauHaut = new JPanel();
		JButton btnAdd = new JButton(MTGConstants.ICON_NEW);
		JButton btnRefresh = new JButton(MTGConstants.ICON_REFRESH);
		JButton btnRemove = new JButton(MTGConstants.ICON_DELETE);
		JButton btnAddAllSet = new JButton(MTGConstants.ICON_CHECK);
		JButton btnExport = new JButton(MTGConstants.ICON_EXPORT);
		JButton btnMassCollection = new JButton(MTGConstants.ICON_IMPORT);
		JButton btnExportPriceCatalog = new JButton(MTGConstants.ICON_EURO);
		JButton btnGenerateWebSite = new JButton(MTGConstants.ICON_WEBSITE);
		JScrollPane scrollPaneCollections = new JScrollPane();
		JSplitPane splitListPanel = new JSplitPane();
		JSplitPane splitPane = new JSplitPane();
		JPanel panneauGauche = new JPanel();
		JScrollPane scrollPane = new JScrollPane();
		JPanel panelTotal = new JPanel();
		JPanel panneauDroite = new JPanel();
		MagicCollectionTableCellRenderer render = new MagicCollectionTableCellRenderer();

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		progressBar = new JProgressBar();
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
		PricesTablePanel pricePanel = new PricesTablePanel();
		
		//////// MODELS
		model = new MagicEditionsTableModel();
		DefaultRowSorter sorterEditions = new TableRowSorter<DefaultTableModel>(model);
		model.init(provider.loadEditions());
		tableEditions.setModel(model);
		new TableFilterHeader(tableEditions, AutoChoices.ENABLED);

		///////// CONFIGURE COMPONENTS
		splitListPanel.setDividerLocation(0.5);
		splitListPanel.setResizeWeight(0.5);

		progressBar.setVisible(false);
		btnRemove.setEnabled(true);
		btnExport.setEnabled(false);
		btnExportPriceCatalog.setEnabled(false);

		splitPane.setResizeWeight(0.5);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		scrollPaneCollections.setMinimumSize(new Dimension(0, 0));
		tree.setCellRenderer(new MagicCardsTreeCellRenderer());

		magicCardDetailPanel.setPreferredSize(new Dimension(0, 0));
		magicCardDetailPanel.enableThumbnail(true);

		tableEditions.setDefaultRenderer(Object.class, render);
		tableEditions.setDefaultRenderer(String.class, render);
		tableEditions.setDefaultRenderer(Integer.class, render);
		tableEditions.setDefaultRenderer(double.class, render);
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
		panneauHaut.add(btnExportPriceCatalog);
		panneauHaut.add(btnGenerateWebSite);
		panneauHaut.add(progressBar);
		add(splitListPanel, BorderLayout.CENTER);
		splitListPanel.setRightComponent(panneauDroite);
		panneauDroite.add(splitPane, BorderLayout.CENTER);
		splitPane.setLeftComponent(scrollPaneCollections);
		scrollPaneCollections.setViewportView(tree);
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

		if (MTGControler.getInstance().get("debug-json-panel").equalsIgnoreCase("true"))
			tabbedPane.addTab("Json", MTGConstants.ICON_TAB_JSON, jsonPanel, null);

		///////// Labels
		lblTotal.setText("Total : " + model.getCountDefaultLibrary() + "/" + model.getCountTotal());
		btnAdd.setToolTipText(MTGControler.getInstance().getLangService().get("COLLECTION_ADD"));
		btnRefresh.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("COLLECTION_REFRESH"));
		btnRemove.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("ITEM_SELECTED_REMOVE"));
		btnAddAllSet.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("COLLECTION_SET_FULL"));
		btnExport.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("EXPORT_AS"));
		btnMassCollection
				.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("COLLECTION_IMPORT"));
		btnExportPriceCatalog
				.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("COLLECTION_EXPORT_PRICES"));
		btnGenerateWebSite
				.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("GENERATE_WEBSITE"));

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
				((LazyLoadingTree.MyNode) getJTree().getModel().getRoot())
						.add(new DefaultMutableTreeNode(collectionAdd));
				getJTree().refresh();
				initPopupCollection();
			} catch (Exception ex) {
				logger.error(ex);
				JOptionPane.showMessageDialog(null, ex, MTGControler.getInstance().getLangService().getError(),
						JOptionPane.ERROR_MESSAGE);
			}
		});

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

			for (final MTGCardsExport exp : MTGControler.getInstance().getEnabledDeckExports()) {
				JMenuItem it = new JMenuItem();
				it.setIcon(exp.getIcon());
				it.setText(exp.getName());
				it.addActionListener(arg0 -> ThreadManager.getInstance().execute(() -> {
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

						exp.addObserver((Observable o, Object arg) -> progressBar.setValue((int) arg));

						if (result == JFileChooser.APPROVE_OPTION) {
							progressBar.setVisible(true);
							if (ed == null)
								exp.export(dao.listCardsFromCollection(mc), f);
							else
								exp.export(dao.listCardsFromCollection(mc, ed), f);

							progressBar.setVisible(false);
							JOptionPane.showMessageDialog(null,
									MTGControler.getInstance().getLangService().combine("EXPORT", "FINISHED"),
									MTGControler.getInstance().getLangService().getCapitalize("FINISHED"),
									JOptionPane.INFORMATION_MESSAGE);

						}

					} catch (Exception e) {
						logger.error(e);
						progressBar.setVisible(false);
						JOptionPane.showMessageDialog(null, e, MTGControler.getInstance().getLangService().getError(),
								JOptionPane.ERROR_MESSAGE);
					}
				}, "export collection with " + exp));

				menu.add(it);
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
				btnExportPriceCatalog.setEnabled(false);
				statsPanel.enabledAdd(false);
			}

			if (curr.getUserObject() instanceof MagicCollection) {
				btnExport.setEnabled(true);
				btnExportPriceCatalog.setEnabled(true);
				selectedcol = (MagicCollection) curr.getUserObject();
				statsPanel.enabledAdd(false);
				btnExport.setEnabled(true);
				btnExportPriceCatalog.setEnabled(true);
			}

			if (curr.getUserObject() instanceof MagicEdition) {
				magicEditionDetailPanel.setMagicEdition((MagicEdition) curr.getUserObject());
				btnExport.setEnabled(true);
				btnExportPriceCatalog.setEnabled(false);
				statsPanel.enabledAdd(false);
				ThreadManager.getInstance().execute(() -> {
					try {

						MagicCollection collec = (MagicCollection) ((DefaultMutableTreeNode) curr.getParent())
								.getUserObject();
						List<MagicCard> list = dao.listCardsFromCollection(collec, (MagicEdition) curr.getUserObject());
						rarityRepartitionPanel.init(list);
						typeRepartitionPanel.init(list);
						manaRepartitionPanel.init(list);
						historyPricesPanel.init(null, (MagicEdition) curr.getUserObject(),
								curr.getUserObject().toString());
						jsonPanel.show(curr.getUserObject());

					} catch (Exception e) {
						logger.error(e);
					}
				}, "Refresh Collection");
			}

			if (curr.getUserObject() instanceof MagicCard) {
				final MagicCard card = (MagicCard) ((DefaultMutableTreeNode) path.getLastPathComponent())
						.getUserObject();
				btnExport.setEnabled(false);
				btnExportPriceCatalog.setEnabled(false);

				magicCardDetailPanel.setMagicCard((MagicCard) curr.getUserObject());
				magicEditionDetailPanel.setMagicEdition(card.getEditions().get(0));
				magicCardDetailPanel.enableThumbnail(true);
				jsonPanel.show(curr.getUserObject());

				ThreadManager.getInstance().execute(() -> {
					statsPanel.initMagicCardStock(card,
							(MagicCollection) ((DefaultMutableTreeNode) curr.getParent().getParent()).getUserObject());
					statsPanel.enabledAdd(true);
				}, "Update Collection");

				if (tabbedPane.getSelectedIndex() == 2) {
					pricePanel.init(card,null);
				}

				ThreadManager.getInstance().execute(() -> {
					try {
						historyPricesPanel.init(card, null, card.getName());
					} catch (Exception e) {
						logger.error(e);
					}
				}, "update history");
			}

		});

		tabbedPane.addChangeListener(e -> {
			if ((tabbedPane.getSelectedIndex() == 2) && ((DefaultMutableTreeNode) tree.getLastSelectedPathComponent()).getUserObject() instanceof MagicCard)
				pricePanel.init(((MagicCard) ((DefaultMutableTreeNode) tree.getLastSelectedPathComponent()).getUserObject()),null);
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
							logger.debug("closing mass import with change =" + d.hasChange());
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

		btnExportPriceCatalog.addActionListener(ae ->

		ThreadManager.getInstance().execute(() -> {
			try {
				PriceCatalogExportDialog diag = new PriceCatalogExportDialog(selectedcol);
				diag.setVisible(true);
				if (diag.value()) {
					progressBar.setVisible(true);
					progressBar.setStringPainted(true);
					progressBar.setMinimum(0);
					progressBar.setMaximum(dao.getCardsCount(selectedcol, null));
					CSVExport exp = new CSVExport();
					exp.addObserver((Observable o, Object arg) -> progressBar.setValue((int) arg));
					exp.exportPriceCatalog(dao.listCardsFromCollection(selectedcol), diag.getDest(),
							diag.getPriceProviders());

					JOptionPane.showMessageDialog(null,
							MTGControler.getInstance().getLangService().combine("EXPORT", "FINISHED"));
					progressBar.setVisible(false);
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}, "btnExportPriceCatalog export Prices"));

		btnGenerateWebSite.addActionListener(ae -> ThreadManager.getInstance().execute(() -> {
			try {

				WebSiteGeneratorDialog diag = new WebSiteGeneratorDialog(dao.getCollections());
				diag.setVisible(true);
				if (diag.value()) {
					progressBar.setVisible(true);
					progressBar.setStringPainted(true);
					progressBar.setMinimum(0);

					int max = 0;
					for (MagicCollection col : diag.getSelectedCollections())
						max += dao.getCardsCount(col, null);

					progressBar.setMaximum(max);
					progressBar.setValue(0);

					MagicWebSiteGenerator gen = new MagicWebSiteGenerator(diag.getTemplate(),
							diag.getDest().getAbsolutePath());

					gen.addObserver((Observable o, Object arg) -> progressBar.setValue((int) arg));
					gen.generate(diag.getSelectedCollections(), diag.getPriceProviders());

					int res = JOptionPane.showConfirmDialog(null,
							MTGControler.getInstance().getLangService().getCapitalize("WEBSITE_CONFIRMATION_VIEW"));

					if (res == JOptionPane.YES_OPTION) {
						Path p = Paths.get(diag.getDest().getAbsolutePath() + "/index.htm");
						Desktop.getDesktop().browse(p.toUri());
					}
					progressBar.setVisible(false);
				}

			} catch (Exception e) {
				logger.error("error generating website", e);
				progressBar.setVisible(false);
				JOptionPane.showMessageDialog(null, e, MTGControler.getInstance().getLangService().getError(),
						JOptionPane.ERROR_MESSAGE);
			}
		}, "btnGenerateWebSite generate website"));

		btnAddAllSet.addActionListener(evt -> {
			MagicEdition ed = (MagicEdition) tableEditions.getValueAt(tableEditions.getSelectedRow(), 1);

			int res = JOptionPane.showConfirmDialog(null, MTGControler.getInstance().getLangService().getCapitalize(
					"CONFIRM_COLLECTION_ITEM_ADDITION", ed, MTGControler.getInstance().get("default-library")));

			if (res == JOptionPane.YES_OPTION)
				try {
					List<MagicCard> list = provider.searchCardByCriteria("set", ed.getId(), null, false);
					logger.debug("save " + list.size() + " cards from " + ed.getId());
					for (MagicCard mc : list) {
						MagicCollection col = new MagicCollection();
						col.setName(MTGControler.getInstance().get("default-library"));
						dao.saveCard(mc, col);
					}
					model.calculate();
					model.fireTableDataChanged();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage(),
							MTGControler.getInstance().getLangService().getError(), JOptionPane.ERROR_MESSAGE);
					logger.error(e);
				}
		});

		btnRemove.addActionListener(evt -> {

			MagicCollection col = (MagicCollection) ((DefaultMutableTreeNode) path.getPathComponent(1)).getUserObject();
			int res = 0;

			DefaultMutableTreeNode curr = (DefaultMutableTreeNode) path.getLastPathComponent();
			if (curr.getUserObject() instanceof MagicCard) {
				MagicCard card = (MagicCard) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();

				try {
					res = JOptionPane.showConfirmDialog(null, MTGControler.getInstance().getLangService()
							.getCapitalize("CONFIRM_COLLECTION_ITEM_DELETE", card, col));
					if (res == JOptionPane.YES_OPTION) {
						dao.removeCard(card, col);
						curr.removeFromParent();
					}
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(),
							MTGControler.getInstance().getLangService().getError(), JOptionPane.ERROR_MESSAGE);
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
					JOptionPane.showMessageDialog(null, e.getMessage(),
							MTGControler.getInstance().getLangService().getError(), JOptionPane.ERROR_MESSAGE);
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
					JOptionPane.showMessageDialog(null, e.getMessage(),
							MTGControler.getInstance().getLangService().getError(), JOptionPane.ERROR_MESSAGE);
				}
			}

			if (res == JOptionPane.YES_OPTION) {
				try {
					model.calculate();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage(),
							MTGControler.getInstance().getLangService().getError(), JOptionPane.ERROR_MESSAGE);
				}
				tree.refresh();

			}
		});

	}

	public void initPopupCollection() throws SQLException {

		popupMenuEdition = new JPopupMenu();
		popupMenuCards = new JPopupMenu();

		JMenu menuItemAdd = new JMenu(
				MTGControler.getInstance().getLangService().getCapitalize("ADD_MISSING_CARDS_IN"));
		JMenu menuItemMove = new JMenu(MTGControler.getInstance().getLangService().getCapitalize("MOVE_CARD_TO"));

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
					tree.refresh();
				} catch (SQLException e1) {
					logger.error(e1);
				}

			});

			adds.addActionListener(e -> {

				final String destinationCollection = ((JMenuItem) e.getSource()).getText();
				ThreadManager.getInstance().execute(() -> {
					try {
						DefaultMutableTreeNode node = ((DefaultMutableTreeNode) path.getPathComponent(2));
						MagicEdition me = (MagicEdition) node.getUserObject();

						MagicCollection col = new MagicCollection();
						col.setName(destinationCollection);
						List<MagicCard> sets = provider.searchCardByCriteria("set", me.getId(), null, false);

						MagicCollection sourceCol = new MagicCollection();
						sourceCol.setName(node.getPath()[1].toString());

						List<MagicCard> list = dao.listCardsFromCollection(sourceCol, me);
						sets.removeAll(list);

						for (MagicCard m : sets)
							dao.saveCard(m, col);

						tree.refresh();
					} catch (Exception e1) {
						logger.error(e1);
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
						.open(MTGControler.getInstance().getEnabledDAO().listCardsFromCollection(col, edition));
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
		});
		popupMenuEdition.add(it);

		popupMenuEdition.add(menuItemAdd);
		popupMenuCards.add(menuItemMove);
	}

	public LazyLoadingTree getJTree() {
		return tree;
	}

}

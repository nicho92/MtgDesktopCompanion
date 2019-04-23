package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
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
import javax.swing.SwingWorker;
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
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.CardSearchPanel;
import org.magic.gui.components.CardStockPanel;
import org.magic.gui.components.CardsDeckCheckerPanel;
import org.magic.gui.components.CardsEditionTablePanel;
import org.magic.gui.components.JSONPanel;
import org.magic.gui.components.LazyLoadingTree;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.gui.components.MagicEditionDetailPanel;
import org.magic.gui.components.PackagesBrowserPanel;
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
import org.magic.services.ThreadManager;
import org.magic.services.workers.AbstractObservableWorker;
import org.magic.services.workers.WebsiteExportWorker;
import org.magic.tools.UITools;

public class CollectionPanelGUI extends MTGUIComponent {
	
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
	private AbstractBuzyIndicatorComponent progressBar;
	private TypeRepartitionPanel typeRepartitionPanel;
	private ManaRepartitionPanel manaRepartitionPanel;
	private RarityRepartitionPanel rarityRepartitionPanel;
	private MagicCardDetailPanel magicCardDetailPanel;
	private CardStockPanel statsPanel;
	private JLabel lblTotal;
	private CardsDeckCheckerPanel deckPanel;
	private CardsEditionTablePanel cardsSetPanel;
	private JTabbedPane panneauTreeTable;
	private JButton btnAdd;
	private JButton btnRefresh;
	private JButton btnRemove;
	private JButton btnAddAllSet;
	private JButton btnExport;
	private JButton btnMassCollection;
	private JButton btnGenerateWebSite;
	private JSplitPane splitListPanel;
	private JSplitPane splitPane;
	private List<MagicCard> listExport;
	private PackagesBrowserPanel packagePanel;
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
		
		JPanel panneauGauche;
		JScrollPane scrollPane;
		JPanel panelTotal;
		JPanel panneauDroite;
		MagicCollectionTableCellRenderer render;


		//////// INIT COMPONENTS
		panneauHaut = new JPanel();
		packagePanel = new PackagesBrowserPanel(false);
		btnAdd = new JButton(MTGConstants.ICON_NEW);
		btnRefresh = new JButton(MTGConstants.ICON_REFRESH);
		btnRemove = new JButton(MTGConstants.ICON_DELETE);
		btnAddAllSet = new JButton(MTGConstants.ICON_CHECK);
		btnExport = new JButton(MTGConstants.ICON_EXPORT);
		btnMassCollection = new JButton(MTGConstants.ICON_MASS_IMPORT);
		btnGenerateWebSite = new JButton(MTGConstants.ICON_WEBSITE);
		cardsSetPanel = new CardsEditionTablePanel();
		deckPanel = new CardsDeckCheckerPanel();
		splitListPanel = new JSplitPane();
		splitPane = new JSplitPane();
		panneauGauche = new JPanel();
		scrollPane = new JScrollPane();
		panelTotal = new JPanel();
		panneauDroite = new JPanel();
		render = new MagicCollectionTableCellRenderer();
		panneauTreeTable = new JTabbedPane();
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		progressBar = AbstractBuzyIndicatorComponent.createProgressComponent();
		lblTotal = new JLabel();
		magicEditionDetailPanel = new MagicEditionDetailPanel(false);
		magicCardDetailPanel = new MagicCardDetailPanel();
		typeRepartitionPanel = new TypeRepartitionPanel();
		manaRepartitionPanel = new ManaRepartitionPanel();
		rarityRepartitionPanel = new RarityRepartitionPanel();
		statsPanel = new CardStockPanel();
		historyPricesPanel = new HistoryPricesPanel(true);
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

		btnRemove.setEnabled(false);
		btnAddAllSet.setEnabled(false);
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
		splitPane.setLeftComponent(panneauTreeTable);
		panneauTreeTable.addTab(MTGControler.getInstance().getLangService().getCapitalize("COLLECTION"), MTGConstants.ICON_BACK,new JScrollPane(tree), null);
		panneauTreeTable.addTab(MTGControler.getInstance().getLangService().getCapitalize("CARDS"), MTGConstants.ICON_TAB_CARD,cardsSetPanel, null);
		panneauTreeTable.addTab(MTGControler.getInstance().getLangService().getCapitalize("PACKAGES"),MTGConstants.ICON_PACKAGE_SMALL,packagePanel,null);
		splitPane.setRightComponent(tabbedPane);
		splitListPanel.setLeftComponent(panneauGauche);
		panneauGauche.add(scrollPane);
		scrollPane.setViewportView(tableEditions);
		panneauGauche.add(panelTotal, BorderLayout.SOUTH);
		panelTotal.add(lblTotal);

		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("DETAILS"), MTGConstants.ICON_TAB_DETAILS,magicCardDetailPanel, null);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("CARD_EDITIONS"),  MTGConstants.ICON_BACK,magicEditionDetailPanel, null);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("PACKAGES"),MTGConstants.ICON_PACKAGE_SMALL,packagePanel,null);
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
		try {
		sorterEditions.setSortKeys(keys);
		}
		catch(NullPointerException e)
		{
			logger.error(e);
		}
		tableEditions.packAll();

		initPopupCollection();

		
		

		initActions();

	}
	
	
	public void initCardSelectionGui(MagicCard mc, MagicCollection col)
	{
		magicCardDetailPanel.setMagicCard(mc);
		deckPanel.init(mc);
		pricePanel.init(mc, mc.getCurrentSet());
		jsonPanel.show(mc);
		pricePanel.init(mc,mc.getCurrentSet());
		btnExport.setEnabled(false);
		magicEditionDetailPanel.setMagicEdition(mc.getCurrentSet());
		packagePanel.setMagicEdition(mc.getCurrentSet());
		
		try {
			if(col==null)
			{
				statsPanel.initMagicCardStock(mc,new MagicCollection(MTGControler.getInstance().get("default-library")));
			}
			else
			{
				statsPanel.initMagicCardStock(mc,col);
			}
		
			statsPanel.enabledAdd(true);
		}
		catch(NullPointerException e)
		{
			//do nothing
		}

		ThreadManager.getInstance().executeThread(() -> {
			try {
				historyPricesPanel.init(mc, null, mc.getName());
			} catch (Exception e) {
				logger.error("error history",e);
			}
		}, "update history");
		
	}
	
	private void initActions()
	{
		
		btnRefresh.addActionListener(e -> {

		progressBar.start();
		
		SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>()
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
				lblTotal.setText("Total : " + model.getCountDefaultLibrary() + "/" + model.getCountTotal());
				model.fireTableDataChanged();
				tree.refresh();
				progressBar.end();
			}
		};
		
		ThreadManager.getInstance().runInEdt(sw);
		});
		
		btnExport.addActionListener(ae -> {
			JPopupMenu menu = new JPopupMenu();

			for (final MTGCardsExport exp : MTGControler.getInstance().listEnabled(MTGCardsExport.class)) 
			{
				if (exp.getMods() == MODS.BOTH || exp.getMods() == MODS.EXPORT) {
					JMenuItem it = new JMenuItem();
					it.setIcon(exp.getIcon());
					it.setText(exp.getName());
					it.addActionListener(al -> {

						
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
						
								if (result == JFileChooser.APPROVE_OPTION) {
									try {
										if (ed == null)
											listExport= dao.listCardsFromCollection(mc);
										else
											listExport= dao.listCardsFromCollection(mc, ed);
										
									
									AbstractObservableWorker<Void, MagicCard, MTGCardsExport> swExp = new AbstractObservableWorker<Void, MagicCard, MTGCardsExport>(progressBar,exp,listExport.size()){

										@Override
										protected Void doInBackground() throws Exception {
											plug.export(listExport, f);
											return null;
										}

										@Override
										protected void notifyEnd() {
											MTGControler.getInstance().notify(new MTGNotification(
													MTGControler.getInstance().getLangService().getCapitalize("FINISHED"),
													MTGControler.getInstance().getLangService().combine("EXPORT", "FINISHED"),
													MESSAGE_TYPE.INFO
													));
										}

										@Override
										protected void error(Exception e) {
											MTGControler.getInstance().notify(e);
										}
									};
									ThreadManager.getInstance().runInEdt(swExp);
									
									}
									catch(Exception e)
									{
										MTGControler.getInstance().notify(e);
									}
									
									
								}
						
													
					
					});
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
			btnRemove.setEnabled(true);
			btnAddAllSet.setEnabled(false);
			btnExport.setEnabled(true);
			final DefaultMutableTreeNode curr = (DefaultMutableTreeNode) path.getLastPathComponent();

			if (curr.getUserObject() instanceof String) {
				btnExport.setEnabled(false);
				statsPanel.enabledAdd(false);
			}

			if (curr.getUserObject() instanceof MagicCollection) {
				selectedcol = (MagicCollection) curr.getUserObject();
				statsPanel.enabledAdd(false);
				ThreadManager.getInstance().executeThread(() -> {
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
				packagePanel.setMagicEdition((MagicEdition) curr.getUserObject());
				statsPanel.enabledAdd(false);
				ThreadManager.getInstance().executeThread(() -> {
					try {

						MagicCollection collec = (MagicCollection) ((DefaultMutableTreeNode) curr.getParent()).getUserObject();
						List<MagicCard> list = dao.listCardsFromCollection(collec, (MagicEdition) curr.getUserObject());
						rarityRepartitionPanel.init(list);
						typeRepartitionPanel.init(list);
						manaRepartitionPanel.init(list);
						historyPricesPanel.init(null, (MagicEdition) curr.getUserObject(),curr.getUserObject().toString());
						jsonPanel.show(curr.getUserObject());

					} catch (Exception e) {
						logger.error("error refresh " + curr.getUserObject() +":"+e.getLocalizedMessage());
					}
				}, "Calculate Editions cards");
			}

			if (curr.getUserObject() instanceof MagicCard) {
				final MagicCard card = (MagicCard) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
				try {
					initCardSelectionGui(card,(MagicCollection) ((DefaultMutableTreeNode) curr.getParent().getParent()).getUserObject());
				}catch(Exception e)
				{
					logger.error("error updating " + card + " in " + curr.getParent() );
				}
			}
		});
		
		cardsSetPanel.getTable().getSelectionModel().addListSelectionListener(me-> {
			
			if(!me.getValueIsAdjusting() && cardsSetPanel.getSelectedCard()!=null) {
					cardsSetPanel.enabledImport(true);
					initCardSelectionGui(cardsSetPanel.getSelectedCard(),null);
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
						JMenuItem it = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("MASS_MOVEMENTS"),MTGConstants.ICON_COLLECTION);
						
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
	
			if(magicEditionDetailPanel.getMagicEdition().getId()!=null)
				diag.setDefaultEdition(magicEditionDetailPanel.getMagicEdition());
			
			diag.setVisible(true);
			try {
				model.calculate();
			} catch (Exception e) {
				logger.error(e);
			}
			model.fireTableDataChanged();
		});

		
		btnGenerateWebSite.addActionListener(ae -> ThreadManager.getInstance().invokeLater(() -> {
			try {

				WebSiteGeneratorDialog diag = new WebSiteGeneratorDialog(dao.listCollections());
				diag.setVisible(true);
				if (diag.value()) {
					int max = 0;
					for (MagicCollection col : diag.getSelectedCollections())
						max += dao.getCardsCount(col, null);

					progressBar.start(max);
					WebsiteExportWorker sw = new WebsiteExportWorker(diag.getTemplate(), diag.getDest(), diag.getSelectedCollections(), diag.getPriceProviders(), progressBar);
					ThreadManager.getInstance().runInEdt(sw);
				}

			} catch (Exception e) {
				logger.error("error generating website", e);
				progressBar.end();
				MTGControler.getInstance().notify(e);
			}
		}));

	    btnAddAllSet.addActionListener(ae ->{
			JPopupMenu popupMenu = new JPopupMenu("Title");
			try {
					for(MagicCollection c : MTGControler.getInstance().getEnabled(MTGDao.class).listCollections())
					{
						JMenuItem cutMenuItem = new JMenuItem(c.getName(),MTGConstants.ICON_COLLECTION);
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
	    
	    
	    
		tableEditions.getSelectionModel().addListSelectionListener(me-> {
			if(!me.getValueIsAdjusting()) {
		    	  try {  
		    		 int row = tableEditions.getSelectedRow();
					MagicEdition ed = (MagicEdition) tableEditions.getValueAt(row, 1);
					magicEditionDetailPanel.setMagicEdition(ed);
					packagePanel.setMagicEdition(ed);
					historyPricesPanel.init(null, ed, ed.getSet());
					jsonPanel.show(ed);
					btnRemove.setEnabled(false);
					btnAddAllSet.setEnabled(true);
					btnExport.setEnabled(false);
					cardsSetPanel.init(ed);
					panneauTreeTable.setTitleAt(1, ed.getSet());
					panneauTreeTable.setSelectedIndex(1);
		    	  }
		    	  catch(Exception e)
		    	  {
		    		  progressBar.end();
		    	  }
			}
		});
	    
		btnAdd.addActionListener(e -> {
			String name = JOptionPane
					.showInputDialog(MTGControler.getInstance().getLangService().getCapitalize("NAME") + " ?");
			MagicCollection collectionAdd = new MagicCollection(name);
			try {
				dao.saveCollection(collectionAdd);
				((LazyLoadingTree.MyNode) getJTree().getModel().getRoot()).add(new DefaultMutableTreeNode(collectionAdd));
				getJTree().refresh();
				initPopupCollection();
			} catch (Exception ex) {
				logger.error(ex);
				MTGControler.getInstance().notify(ex);
			}
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
					res = JOptionPane.showConfirmDialog(null, MTGControler.getInstance().getLangService().getCapitalize("CONFIRM_COLLECTION_DELETE", col, dao.getCardsCount(col, null)));
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
			List<MagicEdition> eds = UITools.getTableSelection(tableEditions, 1);

			int res = JOptionPane.showConfirmDialog(null, MTGControler.getInstance().getLangService().getCapitalize(
					"CONFIRM_COLLECTION_ITEM_ADDITION", eds, it.getText()));

			if (res == JOptionPane.YES_OPTION)
			{	
				try {
					List<MagicCard> list = new ArrayList<>();
					
					for(MagicEdition e : eds)
						for(MagicCard mc : provider.searchCardByEdition(e))
							list.add(mc);
						
						progressBar.start(list.size());
						logger.debug("save " + list.size() + " cards from " + eds);
						
						
						SwingWorker<Void, MagicCard> sw = new SwingWorker<Void, MagicCard>()
						{

							@Override
							protected Void doInBackground() {
								for (MagicCard mc : list) {
									MagicCollection col = new MagicCollection(it.getText());
									try {
										MTGControler.getInstance().saveCard(mc, col,null);
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

		JMenu menuItemAdd = new JMenu(MTGControler.getInstance().getLangService().getCapitalize("ADD_MISSING_CARDS_IN"));
		JMenu menuItemMove = new JMenu(MTGControler.getInstance().getLangService().getCapitalize("MOVE_CARD_TO"));
		menuItemAdd.setIcon(MTGConstants.ICON_COLLECTION);
		menuItemMove.setIcon(MTGConstants.ICON_COLLECTION);
		JMenuItem menuItemAlerts = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("ADD_CARDS_ALERTS"),MTGConstants.ICON_ALERT);
		JMenuItem menuItemStocks = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("ADD_CARDS_STOCKS"),MTGConstants.ICON_STOCK);
		
		for (MagicCollection mc : dao.listCollections()) {
			JMenuItem adds = new JMenuItem(mc.getName(),MTGConstants.ICON_COLLECTION);
			JMenuItem movs = new JMenuItem(mc.getName(),MTGConstants.ICON_COLLECTION);

			movs.addActionListener(e -> {
				DefaultMutableTreeNode nodeCol = ((DefaultMutableTreeNode) path.getPathComponent(1));
				DefaultMutableTreeNode nodeCd = ((DefaultMutableTreeNode) path.getPathComponent(3));
				MagicCard card = (MagicCard) nodeCd.getUserObject();
				MagicCollection oldCol = (MagicCollection) nodeCol.getUserObject();

				final String collec = ((JMenuItem) e.getSource()).getText();
				MagicCollection nmagicCol = new MagicCollection(collec);
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
				ThreadManager.getInstance().invokeLater(() -> {
					try {
						DefaultMutableTreeNode node = ((DefaultMutableTreeNode) path.getPathComponent(2));
						MagicEdition me = (MagicEdition) node.getUserObject();

						MagicCollection col = new MagicCollection(destinationCollection);
						List<MagicCard> sets = provider.searchCardByEdition(me);

						MagicCollection sourceCol = new MagicCollection(node.getPath()[1].toString());
						List<MagicCard> list = dao.listCardsFromCollection(sourceCol, me);
						
						logger.trace(list.size() + " items in " + sourceCol +"/"+me);
						sets.removeAll(list);
						logger.trace(sets.size() + " items to insert int " + col +"/"+me);
						
						
						
						progressBar.start(sets.size());
						

						for (MagicCard m : sets)
						{
							MTGControler.getInstance().saveCard(m, col,null);
							progressBar.progress();
						}

						tree.refresh(node);
						progressBar.end();
					} catch (Exception e1) {
						logger.error(e1);
						progressBar.end();
					}
				});
			});

			menuItemAdd.add(adds);
			menuItemMove.add(movs);
		}

		JMenuItem menuItemOpen = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("OPEN"));
		menuItemOpen.addActionListener(e -> {
			MagicCollection col = (MagicCollection) ((DefaultMutableTreeNode) path.getPathComponent(1)).getUserObject();
			MagicEdition edition = (MagicEdition) ((DefaultMutableTreeNode) path.getPathComponent(2)).getUserObject();
			try {
				((MagicGUI)SwingUtilities.getRoot(this)).setSelectedTab(0);
				CardSearchPanel.getInstance().open(MTGControler.getInstance().getEnabled(MTGDao.class).listCardsFromCollection(col, edition));
			} catch (SQLException e1) {
				logger.error(e1);
			}

		});
		popupMenuEdition.add(menuItemOpen);

		JMenuItem it = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("MASS_MOVEMENTS"),MTGConstants.ICON_COLLECTION);
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

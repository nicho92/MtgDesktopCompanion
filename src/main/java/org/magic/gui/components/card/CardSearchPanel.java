package org.magic.gui.components.card;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.DefaultListModel;
import javax.swing.DefaultRowSorter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGEdition;
import org.magic.api.interfaces.MTGCardsExport.MODS;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.sorters.CardsEditionSorter;
import org.magic.api.sorters.NumberSorter;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.HandPanel;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.GedPanel;
import org.magic.gui.components.ImagePanel2;
import org.magic.gui.components.charts.CmcChartPanel;
import org.magic.gui.components.charts.HistoryPricesPanel;
import org.magic.gui.components.charts.ManaRepartitionPanel;
import org.magic.gui.components.charts.RarityRepartitionPanel;
import org.magic.gui.components.charts.TypeRepartitionPanel;
import org.magic.gui.components.deck.CardsDeckCheckerPanel;
import org.magic.gui.components.dialog.AdvancedSearchQueryDialog;
import org.magic.gui.components.prices.PricesTablePanel;
import org.magic.gui.components.tech.CriteriaComponent;
import org.magic.gui.components.tech.ObjectViewerPanel;
import org.magic.gui.components.widgets.JExportButton;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.gui.renderer.MagicEditionIconListRenderer;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.CardsManagerService;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.providers.IconsProvider;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;

public class CardSearchPanel extends MTGUIComponent {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final String SEARCH_MODULE = "SEARCH_MODULE";

	private MTGCard selectedCard;
	private MTGEdition selectedEdition;
	private MagicCardTableModel cardsModeltable;
	private JTabbedPane tabbedCardsView;
	private HandPanel thumbnailPanel;
	private ManaRepartitionPanel manaRepartitionPanel;
	private TypeRepartitionPanel typeRepartitionPanel;
	private RarityRepartitionPanel rarityRepartitionPanel;
	private SimilarityCardPanel similarityPanel;
	private CmcChartPanel cmcChart;
	private ImagePanel2 cardsPicPanel;
	private HistoryPricesPanel historyChartPanel;
	private MagicEditionDetailPanel magicEditionDetailPanel;
	private MagicCardDetailPanel detailCardPanel;
	private PricesTablePanel priceTablePanel;
	private RulesPanel txtRulesArea;
	private CardStockPanel stockPanel;
	private ObjectViewerPanel panelJson;
	private CriteriaComponent searchComponent;
	private JPopupMenu popupMenu = new JPopupMenu();
	private JXTable tableCards;
	private JExportButton btnExport;
	private JList<MTGEdition> listEdition;
	private CardsDeckCheckerPanel deckPanel;
	private AbstractBuzyIndicatorComponent lblLoading;
	private CardAbilitiesPanel abilitiesPanel;
	private JButton defaultEnterButton;
	private GedPanel<MTGCard> gedPanel;
		
	public AbstractBuzyIndicatorComponent getLblLoading() {
		return lblLoading;
	}
	

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_SEARCH_24;
	}

	@Override
	public String getTitle() {
		return capitalize(SEARCH_MODULE);
	}

	public List<MTGCard> getMultiSelection() {
		return UITools.getTableSelections(tableCards,0);
	}

	public MTGCard getSelected() {
		return selectedCard;
	}

	private void initPopupCollection() throws SQLException {
		var menuItemAdd = new JMenu(capitalize("ADD"));
		menuItemAdd.setIcon(MTGConstants.ICON_NEW);
		for (var mc : getEnabledPlugin(MTGDao.class).listCollections()) {

			var adds = new JMenuItem(mc.getName());
			adds.setIcon(MTGConstants.ICON_COLLECTION);
			adds.addActionListener(addEvent -> {

				String collec = ((JMenuItem) addEvent.getSource()).getText();
				lblLoading.start(tableCards.getSelectedRowCount());
				lblLoading.setText(capitalize("ADD_CARDS_TO") + " " + collec);

				for (var i = 0; i < tableCards.getSelectedRowCount(); i++) {
					MTGCard mcCard = UITools.getTableSelection(tableCards, 0);
					try {
						CardsManagerService.saveCard(mcCard, getEnabledPlugin(MTGDao.class).getCollection(collec),null);
					} catch (SQLException e1) {
						logger.error(e1);
						MTGControler.getInstance().notify(e1);
					}

				}
				lblLoading.end();
			});
			menuItemAdd.add(adds);
		}

		popupMenu.add(menuItemAdd);
	}


	public void initGUI() {
		cardsModeltable = new MagicCardTableModel();
		JPanel panelResultsCards;
		JPanel panelFilters;
		JPanel panelmana;
		JPanel editionDetailPanel;
		JPanel panneauHaut;
		JPanel panneauCard;
		JPanel panneauStat;
		JTextField txtFilter;
		JButton btnClear;
		JButton btnFilter;

		DefaultRowSorter<TableModel, Integer> sorterCards;
		sorterCards = new TableRowSorter<>(cardsModeltable);
		sorterCards.setComparator(6, new NumberSorter());


		List<MTGEdition> li = new ArrayList<>();
		try {
			li = getEnabledPlugin(MTGCardsProvider.class).listEditions();
			Collections.sort(li);
		} catch (Exception e2) {
			logger.error("error no edition loaded", e2);
		}

		/// INIT COMPONENTS

		var scrollThumbnails = new JScrollPane();
		var panneauCentral = new JSplitPane();
		panneauStat = new JPanel();
		panneauHaut = new JPanel();
		panneauCard = new JPanel();
		editionDetailPanel = new JPanel();
		panelResultsCards = new JPanel();
		abilitiesPanel = new CardAbilitiesPanel();
		cmcChart = new CmcChartPanel();
		gedPanel = new GedPanel<>();
		manaRepartitionPanel = new ManaRepartitionPanel(false);
		typeRepartitionPanel = new TypeRepartitionPanel(false);
		stockPanel = new CardStockPanel();
		historyChartPanel = new HistoryPricesPanel(true);
		cardsPicPanel = new ImagePanel2();
		priceTablePanel = new PricesTablePanel();
		rarityRepartitionPanel = new RarityRepartitionPanel(false);
		detailCardPanel = new MagicCardDetailPanel(true);
		panelmana = new JPanel();
		panelFilters = new JPanel();
		panelJson = new ObjectViewerPanel();
		tabbedCardsView = new JTabbedPane(SwingConstants.TOP);
		thumbnailPanel = new HandPanel();
		thumbnailPanel.setBackground(MTGConstants.THUMBNAIL_BACKGROUND_COLOR);
		btnExport = new JExportButton(MODS.EXPORT);
		UITools.bindJButton(btnExport, KeyEvent.VK_E, "Search Export");
		btnFilter = UITools.createBindableJButton(null, MTGConstants.ICON_FILTER, KeyEvent.VK_F, "searchfilter");
		btnClear = UITools.createBindableJButton(null, MTGConstants.ICON_CLEAR, KeyEvent.VK_C, "clear");
		similarityPanel = new SimilarityCardPanel();
		tableCards = UITools.createNewTable(cardsModeltable,true);
		lblLoading = AbstractBuzyIndicatorComponent.createProgressComponent();
		var lblFilter = new JLabel();
		listEdition = new JList<>();
		var advancedSearch = UITools.createBindableJButton(null, MTGConstants.ICON_SEARCH_ADVANCED, KeyEvent.VK_A, "AdvancedSearch");
		searchComponent = new CriteriaComponent(false);
		defaultEnterButton = new JButton(MTGConstants.ICON_SEARCH);
		txtRulesArea = new RulesPanel();

		txtFilter = new JTextField();

		deckPanel = new CardsDeckCheckerPanel();

		/// MODELS
		listEdition.setModel(new DefaultListModel<>());

		/// RENDERER
		tableCards.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());
		listEdition.setCellRenderer(new MagicEditionIconListRenderer());

		/// CONFIGURE COMPONENTS

		btnFilter.setToolTipText(capitalize("FILTER"));
		btnExport.setToolTipText(capitalize("EXPORT_RESULTS"));
		btnExport.setEnabled(false);
		listEdition.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		thumbnailPanel.enableDragging(false);
		panneauCentral.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panneauCentral.setRightComponent(getContextTabbedPane());
		panneauCentral.setLeftComponent(tabbedCardsView);
	
		tableCards.setRowSorter(sorterCards);

		UITools.initTableVisibility(tableCards, cardsModeltable);

		panneauCentral.setDividerLocation(0.5);
		panneauCentral.setResizeWeight(0.5);

		/////// LAYOUT
		setLayout(new BorderLayout());
		panneauStat.setLayout(new GridLayout(2, 2, 0, 0));
		panneauCard.setLayout(new BorderLayout());
		editionDetailPanel.setLayout(new BorderLayout());
		panelResultsCards.setLayout(new BorderLayout(0, 0));
		panelmana.setLayout(new GridLayout(1, 0, 2, 2));

		var flpanelFilters = (FlowLayout) panelFilters.getLayout();
		flpanelFilters.setAlignment(FlowLayout.LEFT);

		var flowLayout = (FlowLayout) panneauHaut.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);

		/// DIMENSION
		cardsPicPanel.setPreferredSize(new Dimension(500, 10));

		scrollThumbnails.getVerticalScrollBar().setUnitIncrement(10);
		txtFilter.setColumns(25);

		/// VISIBILITY
		tableCards.setColumnControlVisible(true);
		panelFilters.setVisible(false);
		lblLoading.setVisible(false);
		tableCards.setShowVerticalLines(false);

		/// ADD PANELS
		for (String s : new String[] { "W", "U", "B", "R", "G", "C", "1" }) {
			final var btnG = new JButton();
			btnG.setToolTipText(s);
			if (s.equals("1"))
				btnG.setToolTipText("[0-9]*");

			btnG.setIcon(new ImageIcon(IconsProvider.getInstance().getManaSymbol(s).getScaledInstance(15, 15, Image.SCALE_SMOOTH)));
			btnG.setForeground(btnG.getBackground());
			btnG.addActionListener(_ -> {
				txtFilter.setText("\\{" + btnG.getToolTipText() + "}");
				sorterCards.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
			});
			panelmana.add(btnG);

		}
		scrollThumbnails.setViewportView(thumbnailPanel);
		thumbnailPanel.setEnclosingScrollPane(scrollThumbnails);


		panneauHaut.add(searchComponent);
		panneauHaut.add(advancedSearch);
		panneauHaut.add(btnFilter);
		panneauHaut.add(btnExport);
		panneauHaut.add(lblLoading);
		panneauCard.add(new JScrollPane(listEdition), BorderLayout.SOUTH);
		panneauCard.add(cardsPicPanel, BorderLayout.CENTER);

		panelResultsCards.add(panelFilters, BorderLayout.NORTH);
		panelResultsCards.add(new JScrollPane(tableCards),BorderLayout.CENTER);
		magicEditionDetailPanel = new MagicEditionDetailPanel();

		editionDetailPanel.add(magicEditionDetailPanel, BorderLayout.CENTER);

		panelFilters.add(lblFilter);
		panelFilters.add(txtFilter);
		panelFilters.add(btnClear);
		panelFilters.add(panelmana);

		
		addContextComponent(detailCardPanel);
		addContextComponent(new JScrollPane(editionDetailPanel),"EDITION",MTGConstants.ICON_TAB_BACK);
		addContextComponent(priceTablePanel);
		addContextComponent(txtRulesArea);
		addContextComponent(historyChartPanel);
		addContextComponent(similarityPanel);
		addContextComponent(deckPanel);
		addContextComponent(stockPanel);
		addContextComponent(abilitiesPanel);
		addContextComponent(gedPanel);
		if(MTG.readPropertyAsBoolean("debug-json-panel"))
			addContextComponent(panelJson);

		
		
		panneauStat.add(cmcChart);
		panneauStat.add(manaRepartitionPanel);
		panneauStat.add(typeRepartitionPanel);
		panneauStat.add(rarityRepartitionPanel);

		tabbedCardsView.addTab(capitalize("RESULTS"),  MTGConstants.ICON_TAB_RESULTS,panelResultsCards, null);
		tabbedCardsView.addTab(capitalize("THUMBNAIL"), MTGConstants.ICON_TAB_THUMBNAIL,scrollThumbnails, null);
		tabbedCardsView.addTab(capitalize("STATS"), MTGConstants.ICON_TAB_ANALYSE, panneauStat,null);

		add(panneauHaut, BorderLayout.NORTH);
		add(panneauCard, BorderLayout.EAST);
		add(panneauCentral, BorderLayout.CENTER);

		/// Right click
		try {
			initPopupCollection();
		} catch (Exception e2) {
			logger.error("error init popup",e2);
		}

		/// Action listners
		addComponentListener(new ComponentAdapter() {
			@Override
		    public void componentShown(ComponentEvent componentEvent){
		        searchComponent.requestFocus();
				panneauCentral.setDividerLocation(.45);
				removeComponentListener(this);
		    }
		});


		similarityPanel.getTableSimilarity().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {

				if(similarityPanel.getTableSimilarity().getSelectedRow()==-1)
					return;

				MTGCard mc = UITools.getTableSelection(similarityPanel.getTableSimilarity(), 0);
				cardsPicPanel.init(mc);
			}
		});


		advancedSearch.addActionListener(_->{
			var diag = new AdvancedSearchQueryDialog();
									  diag.setVisible(true);

			if(diag.getCrits().isEmpty())
				return;


			lblLoading.start();
			lblLoading.setText(capitalize("SEARCHING"));
			cardsModeltable.clear();

			var wk = new AbstractObservableWorker<List<MTGCard>, MTGCard, MTGCardsProvider>(lblLoading,getEnabledPlugin(MTGCardsProvider.class)) {

						@Override
						protected List<MTGCard> doInBackground() throws Exception {


							if(diag.getCollection()==null)
							{
								return plug.searchByCriteria(diag.getCrits());
							}
							else
							{
								return plug.searchByCriteria(diag.getCrits()).stream().filter(mc->{
									try {
										return MTG.getEnabledPlugin(MTGDao.class).listCollectionFromCards(mc).contains(diag.getCollection());
									} catch (SQLException e) {
										logger.error("error sql for {} : {}",mc,e);
										return false;
									}
								}).toList();
							}
						}

						@Override
						protected void process(List<MTGCard> chunks) {
							super.process(chunks);
							cardsModeltable.addItems(chunks);
						}

						@Override
						protected void done() {
							super.done();
							open(getResult());
							btnExport.setEnabled(tableCards.getRowCount() > 0);
						}


			};


			ThreadManager.getInstance().runInEdt(wk,"searching "+diag.getCrits());


		});

		btnClear.addActionListener(_ -> {
			txtFilter.setText("");
			sorterCards.setRowFilter(null);
		});

		btnFilter.addActionListener(_ -> panelFilters.setVisible(!panelFilters.isVisible()));

		searchComponent.addButton(defaultEnterButton,true);

		defaultEnterButton.addActionListener(_->{
					selectedEdition = null;
					lblLoading.start();
					lblLoading.setText(capitalize("SEARCHING"));

					MTGPlugin plug = searchComponent.isCollectionSearch() ? getEnabledPlugin(MTGDao.class):getEnabledPlugin(MTGCardsProvider.class);
					cardsModeltable.clear();

					AbstractObservableWorker<List<MTGCard>, MTGCard, MTGPlugin> wk = new AbstractObservableWorker<>(lblLoading,plug) {
						@Override
						protected List<MTGCard> doInBackground() throws Exception {
							List<MTGCard> cards;

							if (searchComponent.isCollectionSearch()) {
								cards=((MTGDao)plug).listCardsFromCollection((MTGCollection) searchComponent.getMTGCriteria().getFirst());
							}
							else if(searchComponent.isSetSearch()) {
								cards=((MTGCardsProvider)plug).searchCardByEdition((MTGEdition)searchComponent.getMTGCriteria().getFirst());
							}
							else if (searchComponent.isAllCardsSearch()) {
								cards=((MTGCardsProvider)plug).listAllCards();

							}
							else {
								cards=((MTGCardsProvider)plug).searchCardByCriteria(searchComponent.getMTGCriteria().getAtt(), ((MTGCardsProvider)plug).getMTGQueryManager().getValueFor(searchComponent.getMTGCriteria().getFirst()).toString(), null, false);
							}

							try {
								Collections.sort(cards, new CardsEditionSorter());
							}
							catch(Exception _)
							{
								//do nothing
							}

							return cards;
						}

						@Override
						protected void process(List<MTGCard> chunks) {
							super.process(chunks);
							cardsModeltable.addItems(chunks);
						}

						@Override
						protected void done() {
							super.done();
							open(getResult());
							btnExport.setEnabled(tableCards.getRowCount() > 0);
						}
					};
					ThreadManager.getInstance().runInEdt(wk,"searching "+searchComponent.getMTGCriteria());
		});

		tableCards.getSelectionModel().addListSelectionListener(event -> {
			if (!event.getValueIsAdjusting()) {
				try {
					selectedCard = UITools.getTableSelection(tableCards, 0);

					if(selectedCard==null)
						return;

					selectedEdition = selectedCard.getEdition();
					updateCards();
				} catch (Exception e) {
					logger.error("error selecting line",e);
				}
			}
		});




		tableCards.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if(tableCards.getSelectedRow()==-1)
					return;

				if (SwingUtilities.isRightMouseButton(evt)) {
					var point = evt.getPoint();
					popupMenu.show(tableCards, (int) point.getX(), (int) point.getY());
				} 
			}
		});

		listEdition.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mev) {
				selectedEdition = listEdition.getSelectedValue();

				if(selectedEdition==null)
					return;

				var sw = new SwingWorker<MTGCard, MTGCard>()
						{

							@Override
							protected void process(List<MTGCard> chunks) {
								detailCardPanel.init(chunks.get(0));
								magicEditionDetailPanel.init(selectedEdition);
							}

							@Override
							protected MTGCard doInBackground() throws Exception {
								try {
									var mc = CardsManagerService.switchEditions(selectedCard, selectedEdition);
									publish(mc);
									return mc;
								} catch (Exception e) {
									logger.error(e);
									return null;
								}


							}
							@Override
							protected void done() {
								try {
									selectedCard = get();
									cardsPicPanel.init(selectedCard); // backcard
									historyChartPanel.init(selectedCard, selectedEdition, selectedCard.getName());
									priceTablePanel.init(selectedCard);
								}catch(InterruptedException _)
								{
									Thread.currentThread().interrupt();
								}
								catch (Exception e) {
									logger.error(e);
								}
							}

						};


				ThreadManager.getInstance().runInEdt(sw,"loading " + selectedCard);
			}
		});

	

		btnExport.initCardsExport(new Callable<MTGDeck>() {
			@Override
			public MTGDeck call() throws Exception {
				List<MTGCard> export = UITools.getTableSelections(tableCards,0);

				if(export.isEmpty())
					export =  ((MagicCardTableModel) tableCards.getRowSorter().getModel()).getItems();

				return MTGDeck.toDeck(export);
			}
		}, lblLoading);

		txtFilter.addActionListener(_ -> {
			String text = txtFilter.getText();
			if (text.isEmpty()) {
				sorterCards.setRowFilter(null);
			} else {
				sorterCards.setRowFilter(RowFilter.regexFilter(text));
			}
		});

		thumbnailPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				var lab = (DisplayableCard) thumbnailPanel.getComponentAt(new Point(e.getX(), e.getY()));
				selectedCard = lab.getMagicCard();
				selectedEdition = lab.getMagicCard().getEdition();
				cardsPicPanel.init(selectedCard);
				updateCards();
			}

		});
	}

	public void setSelectedCard(MTGCard mc) {
		this.selectedCard = mc;
		updateCards();
	}

	@Override
	public void onFirstShowing() {
		SwingUtilities.getRootPane(this).setDefaultButton(defaultEnterButton);
	}


	public CardSearchPanel() {

		try {

			initGUI();
		} catch (Exception e) {
			logger.error("Error init", e);
			MTGControler.getInstance().notify(e);
		}

		logger.debug("construction of SearchGUI : done");
	}

	public HandPanel getThumbnailPanel() {
		return thumbnailPanel;
	}

	private void updateCards() {
		try {
			cardsPicPanel.init(selectedCard);
			detailCardPanel.init(selectedCard);
			magicEditionDetailPanel.init(selectedCard.getEdition());
			txtRulesArea.init(selectedCard);
			priceTablePanel.init(selectedCard);
			similarityPanel.init(selectedCard);
			panelJson.init(selectedCard);
			deckPanel.init(selectedCard);
			stockPanel.init(selectedCard,null);
			abilitiesPanel.init(selectedCard);
			historyChartPanel.init(selectedCard, selectedEdition, selectedCard.getName());
			gedPanel.init(MTGCard.class, selectedCard);


			((DefaultListModel<MTGEdition>) listEdition.getModel()).removeAllElements();
			for (MTGEdition me : selectedCard.getEditions())
				((DefaultListModel<MTGEdition>) listEdition.getModel()).addElement(me);



		} catch (Exception e1) {
			logger.error("error ",e1);
		}

	}

	public void open(List<MTGCard> cards) {

		if(cards==null)
			return;


		logger.debug("results {} cards",cards.size());

		if (!cards.isEmpty()) {
			cardsModeltable.init(cards);
			thumbnailPanel.initThumbnails(cards, false, false);
			cmcChart.init(cards);
			typeRepartitionPanel.init(cards);
			manaRepartitionPanel.init(cards);
			rarityRepartitionPanel.init(cards);
			tabbedCardsView.setTitleAt(0, capitalize("RESULTS") + " ("+ cardsModeltable.getRowCount() + ")");
			btnExport.setEnabled(true);
		}
	}

}

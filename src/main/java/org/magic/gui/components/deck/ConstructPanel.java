package org.magic.gui.components.deck;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getEnabledPlugin;
import static org.magic.services.tools.MTG.listEnabledPlugins;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGDeck.BOARD;
import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsExport.MODS;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGIA;
import org.magic.game.gui.components.HandPanel;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.card.CardStockPanel;
import org.magic.gui.components.card.ComboFinderPanel;
import org.magic.gui.components.card.MagicCardDetailPanel;
import org.magic.gui.components.card.RulesPanel;
import org.magic.gui.components.charts.CmcChartPanel;
import org.magic.gui.components.charts.DrawProbabilityPanel;
import org.magic.gui.components.charts.ManaRepartitionPanel;
import org.magic.gui.components.charts.RarityRepartitionPanel;
import org.magic.gui.components.charts.TypeRepartitionPanel;
import org.magic.gui.components.dialog.PromptDialog;
import org.magic.gui.components.editor.MagicEditionsComboBoxCellEditor;
import org.magic.gui.components.prices.DeckPricePanel;
import org.magic.gui.components.tech.CriteriaComponent;
import org.magic.gui.components.tech.LoggerViewPanel;
import org.magic.gui.components.tech.ObjectViewerPanel;
import org.magic.gui.components.widgets.JExportButton;
import org.magic.gui.models.DeckCardsTableModel;
import org.magic.gui.renderer.MagicCardListRenderer;
import org.magic.gui.renderer.MagicEditionsComboBoxCellRenderer;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.gui.renderer.standard.NumberCellEditorRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGDeckManager;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;
import org.magic.services.workers.DeckImportWorker;
public class ConstructPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private static final String UPDATED_DECK = "UPDATED_DECK";
	private static final String FINISHED = "FINISHED";

	private DeckDetailsPanel deckDetailsPanel;
	private CmcChartPanel cmcChartPanel;
	private ManaRepartitionPanel manaRepartitionPanel;
	private TypeRepartitionPanel typeRepartitionPanel;
	private RarityRepartitionPanel rarityRepartitionPanel;
	private MagicCardDetailPanel magicCardDetailPanel;
	private DrawProbabilityPanel drawProbabilityPanel;
	private DeckPricePanel deckPricePanel;
	private DeckCardsTableModel deckSidemodel;
	private DeckCardsTableModel deckmodel;
	private ComboFinderPanel comboPanel;
	private CardStockPanel stockDetailPanel;
	private MTGDeck deck;
	private JExportButton btnExports;
	private transient MTGDeckManager deckManager;
	private final DefaultListModel<MTGCard> resultListModel = new DefaultListModel<>();
	private JList<MTGCard> listResult;
	private DrawProbabilityPanel cardDrawProbaPanel;
	private ObjectViewerPanel objectviewerPanel;
	private DeckIAAssitantPanel assistantPanel;
	private int selectedIndex = 0;
	private File f;
	private JLabel lblCards;
	private DeckStockComparatorPanel stockPanel;
	private JXTable tableDeck;
	private JXTable tableSide;
	private JButton defaultEnterButton;
	private RulesPanel rulesPanel;
	private CriteriaComponent searchComponent;
	private JPanel panneauGauche;
	private JButton btnNewDeck;
	private JButton btnOpen;
	private JButton btnImport;
	private JButton btnPin;

	public ConstructPanel() {
		deck = new MTGDeck();
		deckManager = new MTGDeckManager();
		initGUI();
		setDeck(deck);
	}

	public void hideSearchComponent(boolean hidecomponent) {
		searchComponent.setVisible(!hidecomponent);
		panneauGauche.setVisible(!hidecomponent);
		btnNewDeck.setVisible(!hidecomponent);
		btnOpen.setVisible(!hidecomponent);
		btnImport.setVisible(!hidecomponent);
		btnPin.setVisible(!hidecomponent);
	}

	protected void updatePanels() {

		if (deck == null) {
		    return;
		}

		deckDetailsPanel.init(deck);
		cmcChartPanel.init(deck.getMainAsList());
		typeRepartitionPanel.init(deck.getMainAsList());
		manaRepartitionPanel.init(deck.getMainAsList());
		rarityRepartitionPanel.init(deck.getMainAsList());
		deckPricePanel.init(deck);
		drawProbabilityPanel.init(deck);
		btnExports.setEnabled(!deck.getMainAsList().isEmpty());
		objectviewerPanel.init(deck);

	}

	public void setDeck(MTGDeck deck) {
		if (deck == null) {
		    deck = new MTGDeck();
		}

		this.deck = deck;
		deckDetailsPanel.init(deck);
		deckDetailsPanel.updatePicture();
		deckmodel.init(deck);
		deckSidemodel.init(deck);
		stockPanel.setCurrentDeck(deck);
		objectviewerPanel.init(deck);
		assistantPanel.init(deck);
		tableDeck.packAll();
		tableSide.packAll();

	}

	@Override
	public void onFirstShowing() {
		SwingUtilities.getRootPane(this).setDefaultButton(defaultEnterButton);
	}

	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		final var p = MTGControler.getInstance().getProfilPlayer();
		rulesPanel = new RulesPanel();
		final var panneauHaut = new JPanel();
		JButton btnUpdate;
		final var btnRandom = UITools.createBindableJButton("", MTGConstants.ICON_RANDOM, KeyEvent.VK_R, "Random");
		final var btnIA = UITools.createBindableJButton("", MTGConstants.ICON_IA, KeyEvent.VK_I, "IA");
		HandPanel thumbnail;
		searchComponent = new CriteriaComponent(false);
		JTabbedPane tabbedPaneHaut;
		ButtonGroup groupsFilterResult;
		final var buzyLabel = AbstractBuzyIndicatorComponent.createProgressComponent();
		deckmodel = new DeckCardsTableModel(BOARD.MAIN);
		deckSidemodel = new DeckCardsTableModel(BOARD.SIDE);

		deckDetailsPanel = new DeckDetailsPanel();
		thumbnail = new HandPanel();
		final var flowLayout = (FlowLayout) panneauHaut.getLayout();
		comboPanel = new ComboFinderPanel();
		final var importLogPanel = new LoggerViewPanel();


		assistantPanel = new DeckIAAssitantPanel();

		lblCards = new JLabel();
		btnNewDeck = UITools.createBindableJButton("", MTGConstants.ICON_NEW, KeyEvent.VK_N, "New");
		btnOpen = UITools.createBindableJButton("", MTGConstants.ICON_OPEN, KeyEvent.VK_O, "Open");
		btnUpdate = UITools.createBindableJButton("", MTGConstants.ICON_REFRESH, KeyEvent.VK_R, "Refresh");
		final var btnSave = UITools.createBindableJButton("", MTGConstants.ICON_SAVE, KeyEvent.VK_S, "Save");
		btnImport = UITools.createBindableJButton("", MTGConstants.ICON_IMPORT, KeyEvent.VK_I, "Import");
		btnPin = UITools.createBindableJButton("", MTGConstants.ICON_PIN, KeyEvent.VK_P, "Pin");

		btnExports = new JExportButton(MODS.EXPORT);
		stockPanel = new DeckStockComparatorPanel();
		objectviewerPanel = new ObjectViewerPanel();
		final var buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		final var panneauBas = new JPanel();
		tabbedPaneHaut = new JTabbedPane(SwingConstants.TOP);
		final var panneauDeck = new JSplitPane();
		magicCardDetailPanel = new MagicCardDetailPanel(false);
		cardDrawProbaPanel = new DrawProbabilityPanel();
		tableDeck = UITools.createNewTable(deckmodel, false);
		tableSide = UITools.createNewTable(deckSidemodel, false);

		final var tabbedDeckSide = new JTabbedPane(SwingConstants.RIGHT);
		final var panelInfoDeck = new JPanel();
		cmcChartPanel = new CmcChartPanel();
		manaRepartitionPanel = new ManaRepartitionPanel(false);
		typeRepartitionPanel = new TypeRepartitionPanel(false);
		rarityRepartitionPanel = new RarityRepartitionPanel(false);
		drawProbabilityPanel = new DrawProbabilityPanel();
		final var randomHandPanel = new JPanel();
		final var statPanel = new JPanel();
		deckPricePanel = new DeckPricePanel();
		final var panel = new JPanel();
		final var btnDrawAHand = new JButton(capitalize("DRAW_HAND"));
		final var panneauResultFilter = new JPanel();
		final var tglbtnStd = new JToggleButton("STD");
		final var tglbtnMdn = new JToggleButton("MDN");
		final var tglbtnLeg = new JToggleButton("LEG");
		final var tglbtnVin = new JToggleButton("VIN");
		final var tglbtnCmd = new JToggleButton("CMD");
		defaultEnterButton = UITools.createBindableJButton(null, MTGConstants.ICON_SEARCH, KeyEvent.VK_S, "search");
		panneauGauche = new JPanel();
		listResult = new JList<>(new DefaultListModel<>());
		stockDetailPanel = new CardStockPanel();

		groupsFilterResult = new ButtonGroup() {
			private static final long serialVersionUID = 1L;

			@Override
			public void setSelected(ButtonModel model, boolean selected) {
				if (selected) {
					super.setSelected(model, selected);
				} else {
					clearSelection();
				}
			}
		};

		searchComponent.addButton(defaultEnterButton, true);
		thumbnail.setThumbnailSize(new Dimension(223, 311));
		thumbnail.enableDragging(false);
		thumbnail.setMaxCardsRow(4);
		flowLayout.setAlignment(FlowLayout.LEFT);
		searchComponent.setBackground(Color.WHITE);
		btnNewDeck.setToolTipText(capitalize("CREATE_NEW_DECK"));
		btnOpen.setToolTipText(capitalize("OPEN_DECK"));
		btnUpdate.setToolTipText(capitalize("UPDATE_DECK"));
		btnSave.setToolTipText(capitalize("SAVE_DECK"));
		btnExports.setEnabled(false);
		btnExports.setToolTipText(capitalize("EXPORT_AS"));
		importLogPanel.enabledAutoLoad();
		importLogPanel.setLevel(org.apache.logging.log4j.Level.ERROR);
		panneauDeck.setDividerLocation(0.5);
		panneauDeck.setResizeWeight(0.5);
		panneauDeck.setOrientation(JSplitPane.VERTICAL_SPLIT);
		magicCardDetailPanel.enableThumbnail(true);

		panelInfoDeck.setLayout(new BorderLayout(0, 0));
		randomHandPanel.setLayout(new BorderLayout(0, 0));
		statPanel.setLayout(new GridLayout(3, 2, 0, 0));
		btnImport.setToolTipText(capitalize("DECK_IMPORT_AS"));
		panneauGauche.setLayout(new BorderLayout(0, 0));
		listResult.setCellRenderer(new MagicCardListRenderer());
		listResult.setMinimumSize(new Dimension(100, 0));
		listResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tglbtnStd.setActionCommand("Standard");
		tglbtnMdn.setActionCommand("Modern");
		tglbtnLeg.setActionCommand("Legacy");
		tglbtnVin.setActionCommand("Vintage");
		tglbtnCmd.setActionCommand("Commander");

		add(panneauHaut, BorderLayout.NORTH);
		panneauHaut.add(searchComponent);
		panneauHaut.add(buzy);
		panneauHaut.add(lblCards);
		panneauHaut.add(btnNewDeck);
		panneauHaut.add(btnOpen);
		panneauHaut.add(btnUpdate);
		panneauHaut.add(btnSave);
		panneauHaut.add(btnImport);
		panneauHaut.add(btnExports);
		panneauHaut.add(btnRandom);
		panneauHaut.add(btnIA);
		panneauHaut.add(btnPin);

		panneauHaut.add(buzyLabel);
		panneauDeck.setRightComponent(getContextTabbedPane());
		add(panneauBas, BorderLayout.SOUTH);
		add(tabbedPaneHaut, BorderLayout.CENTER);

		addContextComponent(magicCardDetailPanel);
		addContextComponent(comboPanel);
		addContextComponent(cardDrawProbaPanel);
		addContextComponent(rulesPanel);
		addContextComponent(stockDetailPanel);
		addContextComponent(assistantPanel);
		addContextComponent(importLogPanel);

		if (MTG.readPropertyAsBoolean("debug-json-panel")) {
		    addContextComponent(objectviewerPanel);
		}

		panneauDeck.setLeftComponent(tabbedDeckSide);
		tabbedDeckSide.addTab("Main", MTGConstants.ICON_TAB_DECK, new JScrollPane(tableDeck), null);
		tabbedDeckSide.addTab("SideBoard", MTGConstants.ICON_TAB_DECK, new JScrollPane(tableSide), null);

		tabbedPaneHaut.addTab(capitalize("DECK"), MTGConstants.ICON_TAB_DECK, panneauDeck, null);
		tabbedPaneHaut.addTab(capitalize("INFORMATIONS"), MTGConstants.ICON_TAB_DETAILS, panelInfoDeck, null);
		tabbedPaneHaut.addTab(capitalize("STATS"), MTGConstants.ICON_TAB_ANALYSE, statPanel, null);
		tabbedPaneHaut.addTab(capitalize("SAMPLE_HAND"), MTGConstants.ICON_TAB_THUMBNAIL, randomHandPanel, null);
		UITools.addTab(tabbedPaneHaut, stockPanel);

		panelInfoDeck.add(deckDetailsPanel, BorderLayout.NORTH);
		randomHandPanel.add(thumbnail, BorderLayout.CENTER);
		statPanel.add(manaRepartitionPanel);
		statPanel.add(typeRepartitionPanel);
		statPanel.add(rarityRepartitionPanel);
		statPanel.add(cmcChartPanel);
		statPanel.add(drawProbabilityPanel);
		statPanel.add(deckPricePanel);
		randomHandPanel.add(panel, BorderLayout.NORTH);
		panel.add(btnDrawAHand);
		add(panneauGauche, BorderLayout.WEST);
		panneauResultFilter.add(tglbtnStd);
		panneauResultFilter.add(tglbtnMdn);
		panneauResultFilter.add(tglbtnLeg);
		panneauResultFilter.add(tglbtnVin);
		panneauResultFilter.add(tglbtnCmd);
		groupsFilterResult.add(tglbtnStd);
		groupsFilterResult.add(tglbtnMdn);
		groupsFilterResult.add(tglbtnLeg);
		groupsFilterResult.add(tglbtnVin);
		groupsFilterResult.add(tglbtnCmd);
		panneauGauche.add(new JScrollPane(listResult));
		panneauGauche.add(panneauResultFilter, BorderLayout.NORTH);

		listResult.setModel(resultListModel);

		initTables(tableDeck, BOARD.MAIN, deckmodel);
		initTables(tableSide, BOARD.SIDE, deckSidemodel);

		deckDetailsPanel.init(deck);

		///ACTIONS
		btnNewDeck.addActionListener(_ -> {
			final var newDeck = new MTGDeck();
			setDeck(newDeck);
		});

		tabbedDeckSide.addChangeListener(_ -> selectedIndex = tabbedDeckSide.getSelectedIndex());

		btnPin.addActionListener(_ -> {

			final var tree = new DeckTree();
			tree.enableThumbnail();
			tree.setDeck(deck);

			MTGUIComponent.createJDialog(MTGUIComponent.build(new JScrollPane(tree), "Deck", MTGConstants.ICON_DECK),
					true, false).setVisible(true);

		});

		btnIA.addActionListener(_ -> {
			final var diag = new PromptDialog();
			diag.setVisible(true);

			final var description = diag.getPrompt();

			if (description == null || description.isEmpty()) {
			    return;
			}

			buzyLabel.start();
			final SwingWorker<MTGDeck, Void> sw = new SwingWorker<>() {

				@Override
				protected MTGDeck doInBackground() throws Exception {
					return MTG.getEnabledPlugin(MTGIA.class).generateDeck(description);
				}

				@Override
				protected void done() {
					try {
						deck = get();
						deckDetailsPanel.init(deck);
						deckmodel.init(deck);
						deckSidemodel.init(deck);
						setDeck(deck);
						updatePanels();
					} catch (final InterruptedException e) {
						Thread.currentThread().interrupt();
						MTGControler.getInstance().notify(e);
					} catch (final Exception e) {
						MTGControler.getInstance().notify(e);
					}
					buzyLabel.end();

				}

			};
			ThreadManager.getInstance().runInEdt(sw, "generate deck with ia");
		});

		btnRandom.addActionListener(_ -> {
			buzyLabel.start();
			final SwingWorker<MTGDeck, Void> sw = new SwingWorker<>() {

				@Override
				protected MTGDeck doInBackground() throws Exception {
					return deckManager.generateRandomDeck();
				}

				@Override
				protected void done() {
					try {
						deck = get();
						deckDetailsPanel.init(deck);
						deckmodel.init(deck);
						deckSidemodel.init(deck);
						setDeck(deck);
						updatePanels();
					} catch (final InterruptedException e) {
						Thread.currentThread().interrupt();
						MTGControler.getInstance().notify(e);
					} catch (final Exception e) {
						MTGControler.getInstance().notify(e);
					}
					buzyLabel.end();

				}

			};
			ThreadManager.getInstance().runInEdt(sw, "random deck");
		});

		btnOpen.addActionListener(_ -> {
			try {
				final var choose = new JDeckChooserDialog();
				choose.setVisible(true);
				deck = choose.getSelectedDeck();
				if (deck != null) {
					setDeck(deck);
					updatePanels();
				}
			} catch (final Exception ex) {
				logger.error(ex);
				MTGControler.getInstance().notify(ex);
			}

		});

		btnUpdate.addActionListener(_ -> {
			if (deck == null) {
			    return;
			}

			final Map<MTGCard, Integer> updateM = new HashMap<>();
			final Map<MTGCard, Integer> updateS = new HashMap<>();
			btnUpdate.setEnabled(false);

			buzyLabel.start(deck.getMain().size() + deck.getSideBoard().size());
			final SwingWorker<Void, MTGCard> sw = new SwingWorker<>() {
				@Override
				protected void done() {
					buzyLabel.end();
					btnUpdate.setEnabled(true);

					deck.getMain().clear();
					deck.setMain(updateM);

					deck.getSideBoard().clear();
					deck.setSideBoard(updateS);

					updatePanels();

					btnUpdate.setEnabled(true);
					buzyLabel.end();
					MTGControler.getInstance().notify(
							new MTGNotification(capitalize(FINISHED), capitalize(UPDATED_DECK), MESSAGE_TYPE.INFO));
				}

				@Override
				protected void process(List<MTGCard> chunks) {
					buzyLabel.progressSmooth(chunks.size());
				}

				@Override
				protected Void doInBackground() throws Exception {
					for (final MTGCard mc : deck.getMain().keySet()) {
						try {

							var newMc = getEnabledPlugin(MTGCardsProvider.class)
									.getCardByScryfallId(mc.getScryfallId());
							if (newMc == null) {
							    newMc = mc;
							}
							updateM.put(newMc, deck.getMain().get(mc));
							publish(mc);
						} catch (final Exception e) {
							logger.error("error update {}", mc, e);
						}
					}
					for (final MTGCard mc : deck.getSideBoard().keySet()) {
						try {
							var newMc = getEnabledPlugin(MTGCardsProvider.class)
									.getCardByScryfallId(mc.getScryfallId());
							if (newMc == null) {
							    newMc = mc;
							}
							updateS.put(newMc, deck.getSideBoard().get(mc));
							publish(mc);
						} catch (final Exception e) {
							logger.error("error update {}", mc, e);

						}
					}

					return null;
				}

			};
			ThreadManager.getInstance().runInEdt(sw, "updating " + deck);
		});

		btnSave.addActionListener(_ -> {
			if (deck == null) {
				MTGControler.getInstance().notify(new NullPointerException("Deck is Null"));
				return;
			}

			buzyLabel.start();

			final String dname = deck.getName();
			if (deck.getId() < 0 && dname.isEmpty()) {
				final String name = JOptionPane.showInputDialog(capitalize("DECK_NAME") + " ?", dname);
				if (name != null && !name.isEmpty()) {
				    deck.setName(name);
				}

			}

			final var sw = new SwingWorker<Void, Void>() {

				@Override
				protected Void doInBackground() throws Exception {
					logger.debug("saving {}", deck);
					deckManager.saveDeck(deck);
					p.setDeck(deck);
					return null;
				}

				@Override
				protected void done() {
					buzyLabel.end();

					try {
						get();
					} catch (final InterruptedException _) {
						Thread.currentThread().interrupt();
					} catch (final Exception ex) {

						logger.error("error saving", ex);
						MTGControler.getInstance().notify(ex);
					}

				}

			};

			ThreadManager.getInstance().runInEdt(sw, "saving deck");

		});

		btnDrawAHand.addActionListener(_ -> {

			if (deck == null || deck.isEmpty()) {
			    return;
			}

			thumbnail.removeAll();
			p.setDeck(deck);
			p.mixHandAndLibrary();
			p.shuffleLibrary();
			p.drawCard(7);
			thumbnail.initThumbnails(p.getHand().getCards(), false, false);

		});

		listResult.getSelectionModel().addListSelectionListener(lsl -> {

			if (!lsl.getValueIsAdjusting()) {
				final var mc = listResult.getSelectedValue();

				if (mc == null) {
				    return;
				}

				magicCardDetailPanel.init(mc);
				stockDetailPanel.init(mc, null);
				comboPanel.init(mc);
			}

		});

		listResult.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent ev) {

				if (ev.getClickCount() == 2 && !ev.isConsumed()) {
					ev.consume();

					final MTGCard mc = listResult.getSelectedValue();

					if (mc == null) {
					    return;
					}

					if (getSelectedMap().get(mc) != null) {
						getSelectedMap().put(mc, deck.getMain().get(mc) + 1);
					} else {
						getSelectedMap().put(mc, 1);
					}
					deckmodel.init(deck);
					deckSidemodel.init(deck);

				}
			}
		});

		btnImport.addActionListener(ae -> {
			final var menu = new JPopupMenu();
			for (final MTGCardsExport exp : listEnabledPlugins(MTGCardsExport.class)) {
				if (exp.getMods() == MODS.BOTH || exp.getMods() == MODS.IMPORT) {

					final var it = new JMenuItem(exp.getName(), exp.getIcon());
					it.addActionListener(_ -> {

						final var jf = new JFileChooser(MTGConstants.DATA_DIR);
						jf.setFileHidingEnabled(false);
						jf.setFileFilter(new FileFilter() {
							@Override
							public String getDescription() {
								return exp.getName() + " (" + exp.getDeckFileExtension() + ")";
							}

							@Override
							public boolean accept(File f) {
								return (f.isDirectory() || f.getName().endsWith(exp.getDeckFileExtension()));
							}
						});

						int res = -1;
						f = new File("");

						if (!exp.needDialogForDeck(MODS.IMPORT) && exp.needFile()) {
							res = jf.showOpenDialog(this);
							f = jf.getSelectedFile();
						} else if (!exp.needFile() && !exp.needDialogForDeck(MODS.IMPORT)) {
							logger.trace("{} need no file. Skip", exp);
							res = JFileChooser.APPROVE_OPTION;
						} else {
							try {
								setDeck(exp.importDeckFromFile(null));
								updatePanels();
							} catch (final IOException e1) {
								logger.error(e1);
							}

						}

						if (res == JFileChooser.APPROVE_OPTION) {
							buzyLabel.start();
							final var importWork = new DeckImportWorker(exp, buzyLabel, f) {
								@Override
								protected void done() {
									super.done();
									try {
										setDeck(get());
										updatePanels();
									} catch (final InterruptedException e) {
										Thread.currentThread().interrupt();
										MTGControler.getInstance().notify(e);
									} catch (final Exception e) {
										logger.error(e);
									}
								}
							};
							ThreadManager.getInstance().runInEdt(importWork, "Deck imports from " + f);
						}
					});

					UITools.buildCategorizedMenu(menu, it, exp);

				}
			}

			final var b = (Component) ae.getSource();
			final var point = b.getLocationOnScreen();
			menu.show(b, 0, 0);
			menu.setLocation(point.x, point.y + b.getHeight());

		});

		btnExports.initCardsExport(new Callable<MTGDeck>() {
			@Override
			public MTGDeck call() throws Exception {
				return deck;
			}
		}, buzyLabel);

		defaultEnterButton.addActionListener(_ -> {

			resultListModel.clear();

			final var sw = new AbstractObservableWorker<List<MTGCard>, MTGCard, MTGCardsProvider>(buzy,
					getEnabledPlugin(MTGCardsProvider.class)) {
				@Override
				protected List<MTGCard> doInBackground() throws Exception {
					return plug
							.searchCardByCriteria(
									searchComponent.getMTGCriteria().getAtt(), plug.getMTGQueryManager()
											.getValueFor(searchComponent.getMTGCriteria().getFirst()).toString(),
									null, false);
				}

				@Override
				protected void process(List<MTGCard> chunks) {
					super.process(chunks);
					final var form = new MTGFormat();
					for (final MTGCard m : chunks) {
						if (groupsFilterResult.getSelection() != null) {
							form.setFormat(groupsFilterResult.getSelection().getActionCommand());
							if (m.getLegalities().contains(form)) {
							    resultListModel.addElement(m);
							}

						} else {
							resultListModel.addElement(m);
						}
					}
				}

				@Override
				protected void notifyEnd() {
					lblCards.setText(
							resultListModel.size() + " " + MTGControler.getInstance().getLangService().get("RESULTS"));
					listResult.updateUI();
				}
			};

			ThreadManager.getInstance().runInEdt(sw, "search cards for deck");

		});
	}

	private void initTables(JXTable table, BOARD f, DeckCardsTableModel model) {

		table.setRowSorter(new TableRowSorter<>(model));
		table.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());
		table.getColumnModel().getColumn(3).setCellRenderer(new MagicEditionsComboBoxCellRenderer());
		table.getColumnModel().getColumn(3).setCellEditor(new MagicEditionsComboBoxCellEditor());
		table.getColumnModel().getColumn(4).setCellEditor(new NumberCellEditorRenderer());
		table.getColumnModel().getColumn(0).setCellRenderer(
				(JTable table2, Object value, boolean isSelected, boolean hasFocus, int row, int column) -> {

					if (value == null) {
					    return new JLabel();
					}

					final var comp = (JLabel) new DefaultTableCellRenderer().getTableCellRendererComponent(table2, value,
							isSelected, hasFocus, row, column);
					comp.setText(((MTGCard) value).getName());

					try {
						if (deck != null && value != null && deck.getCommander() != null
								&& ((MTGCard) value).getName().equals(deck.getCommander().getName())) {
						    comp.setFont(MTGControler.getInstance().getFont().deriveFont(Font.BOLD));
						}
					} catch (final Exception e) {
						logger.error("error applying font {}  {}", value, deck, e);
					}

					if (((MTGCard) value).isCompanion()) {
					    comp.setFont(comp.getFont().deriveFont(Font.ITALIC));
					}

					return comp;
				});

		table.getSelectionModel().addListSelectionListener(event -> {
			if (!event.getValueIsAdjusting()) {
				try {
					final MTGCard mc = UITools.getTableSelection(table, 0);
					magicCardDetailPanel.init(mc);
					comboPanel.init(mc);
					stockDetailPanel.init(mc, null);
					rulesPanel.init(mc);
					assistantPanel.init(deck);
					if (f == BOARD.MAIN) {
					    cardDrawProbaPanel.init(deck, mc);
					}

				} catch (final Exception e) {
					logger.error(e);
				}
			}
		});

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent ev) {

				if (UITools.getTableSelections(table, 0).isEmpty()) {
				    return;
				}

				final MTGCard mc = UITools.getTableSelection(table, 0);

				if (f == BOARD.MAIN) {
				    cardDrawProbaPanel.init(deck, mc);
				}

				if (SwingUtilities.isRightMouseButton(ev)) {
					final var menu = new JPopupMenu();

					final var itemDel = new JMenuItem(capitalize("DELETE"));
					menu.add(itemDel);
					itemDel.addActionListener(_ -> {

						deck.delete(mc, f);
						model.fireTableDataChanged();

					});

					if (mc.isLegendary() && (mc.isCreature() || mc.isPlaneswalker())) {

						if ((deck.getCommander() != null) && mc.getName().equals(deck.getCommander().getName())) {
							final var itemRemoveCommander = new JMenuItem(capitalize("REMOVE_COMMANDER"));
							menu.add(itemRemoveCommander);
							itemRemoveCommander.addActionListener(_ -> deck.setCommander(null));
						} else {
							final var itemSelCommander = new JMenuItem(capitalize("SELECT_COMMANDER"));
							menu.add(itemSelCommander);
							itemSelCommander.addActionListener(_ -> deck.setCommander(mc));
						}
					}

					final var itemMove = new JMenuItem(capitalize("MOVE_CARD_TO") + ((f == BOARD.MAIN) ? " Side" : " Main"));
					menu.add(itemMove);

					itemMove.addActionListener(_ -> {
						if (f == BOARD.MAIN) {
							final List<MTGCard> list = UITools.getTableSelections(tableDeck, 0);

							for (final MTGCard card : list) {
								final int qty = deck.getMain().get(card);
								deck.getSideBoard().put(card, qty);
								deck.getMain().remove(card);
							}
						} else {

							final List<MTGCard> list = UITools.getTableSelections(tableSide, 0);

							for (final MTGCard card : list) {
								final int qty = deck.getSideBoard().get(card);
								deck.getMain().put(card, qty);
								deck.getSideBoard().remove(card);
							}
						}
						model.fireTableDataChanged();

					});

					final var item = new JMenuItem(capitalize("MORE_LIKE_THIS"));
					menu.add(item);
					item.addActionListener(_ -> {

						resultListModel.removeAllElements();
						try {
							for (final MTGCard card : getEnabledPlugin(MTGCardsIndexer.class).similarity(mc).keySet()) {
							    resultListModel.addElement(card);
							}

							lblCards.setText(resultListModel.size() + " "
									+ MTGControler.getInstance().getLangService().get("RESULTS"));
							listResult.setModel(resultListModel);
							listResult.updateUI();

						} catch (final IOException e) {
							logger.error(e);
						}
					});
					final var point = ev.getPoint();
					menu.show(table, (int) point.getX(), (int) point.getY());

				}

			}
		});

		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				final MTGCard mc = UITools.getTableSelection(table, 0);
				final int sel = table.getSelectedRow();
				if (e.getKeyChar() == '+') {
					if (f == BOARD.MAIN) {
					    deck.add(mc);
					} else {
					    deck.addSide(mc);
					}

					deckmodel.init(deck);
				}

				if (e.getKeyChar() == '-') {
					if (f == BOARD.MAIN) {
					    deck.remove(mc);
					} else {
					    deck.removeSide(mc);
					}

					deckmodel.init(deck);
				}

				if (sel > -1) {
				    table.setRowSelectionInterval(sel, sel);
				}

			}
		});

		table.getModel().addTableModelListener(_ -> updatePanels());

		table.getDefaultEditor(String.class).addCellEditorListener(new CellEditorListener() {

			@Override
			public void editingStopped(ChangeEvent e) {
				updatePanels();

			}

			@Override
			public void editingCanceled(ChangeEvent e) {
				updatePanels();

			}
		});
	}

	public Map<MTGCard, Integer> getSelectedMap() {

		if (selectedIndex == 1) {
		    return deck.getSideBoard();
		}

		return deck.getMain();

	}

	@Override
	public String getTitle() {
		return "Construct";
	}
}

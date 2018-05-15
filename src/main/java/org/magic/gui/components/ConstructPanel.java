package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultRowSorter;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXSearchField;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicFormat;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.abstracts.AbstractCardExport.MODS;
import org.magic.game.gui.components.HandPanel;
import org.magic.game.model.Player;
import org.magic.gui.components.charts.CmcChartPanel;
import org.magic.gui.components.charts.DrawProbabilityPanel;
import org.magic.gui.components.charts.ManaRepartitionPanel;
import org.magic.gui.components.charts.RarityRepartitionPanel;
import org.magic.gui.components.charts.TypeRepartitionPanel;
import org.magic.gui.components.dialog.JDeckChooserDialog;
import org.magic.gui.models.DeckModel;
import org.magic.gui.renderer.IntegerCellEditor;
import org.magic.gui.renderer.MagicCardListRenderer;
import org.magic.gui.renderer.MagicEditionsComboBoxEditor;
import org.magic.gui.renderer.MagicEditionsComboBoxRenderer;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGDeckManager;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;

public class ConstructPanel extends JPanel {

	private DeckDetailsPanel deckDetailsPanel;
	private CmcChartPanel cmcChartPanel;
	private ManaRepartitionPanel manaRepartitionPanel;
	private TypeRepartitionPanel typeRepartitionPanel;
	private RarityRepartitionPanel rarityRepartitionPanel;
	private MagicCardDetailPanel magicCardDetailPanel;
	private DrawProbabilityPanel drawProbabilityPanel;
	private DeckPricePanel deckPricePanel;
	private DeckModel deckSidemodel;
	private DeckModel deckmodel;
	private MagicDeck deck;
	private JButton btnExports;
	private transient MTGDeckManager deckManager;
	private DefaultListModel<MagicCard> resultListModel = new DefaultListModel<>();

	private JTable tableDeck;
	private JTable tableSide;
	private JList<MagicCard> listResult;
	private JBuzyLabel lblExport;
	private DrawProbabilityPanel cardDrawProbaPanel;

	public static final int MAIN = 0;
	public static final int SIDE = 1;

	protected int selectedIndex = 0;

	private File exportedFile;

	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	private File f;
	private Player p;

	public void loading(boolean show, String text) {
		lblExport.setText(text);
		lblExport.setVisible(show);
	}

	public ConstructPanel() {
		logger.info("init DeckBuilder GUI");
		deck = new MagicDeck();
		deckManager = new MTGDeckManager();
		initGUI();
		setDeck(deck);
	}

	public void setDeck(MagicDeck deck) {
		this.deck = deck;
		deckDetailsPanel.setMagicDeck(deck);
		deckDetailsPanel.updatePicture();
		deckmodel.init(deck);
		p = new Player(deck);
	}

	private void initGUI() {

		JPanel panneauHaut = new JPanel();
		JButton btnUpdate;
		HandPanel thumbnail;
		JPanel panelBottom;
		JXSearchField txtSearch;
		JComboBox<String> cboAttributs;
		JScrollPane scrollResult;
		JTabbedPane tabbedPane;
		ButtonGroup groupsFilterResult;
		lblExport = new JBuzyLabel();

		setLayout(new BorderLayout(0, 0));
		deckmodel = new DeckModel(DeckModel.TYPE.DECK);
		deckSidemodel = new DeckModel(DeckModel.TYPE.SIDE);
		deckDetailsPanel = new DeckDetailsPanel();
		panelBottom = new JPanel();

		thumbnail = new HandPanel();
		thumbnail.setThumbnailSize(new Dimension(223, 311));
		thumbnail.enableDragging(false);
		thumbnail.setMaxCardsRow(4);

		FlowLayout flowLayout = (FlowLayout) panneauHaut.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(panneauHaut, BorderLayout.NORTH);

		cboAttributs = new JComboBox<>(new DefaultComboBoxModel<String>(
				MTGControler.getInstance().getEnabledCardsProviders().getQueryableAttributs()));
		panneauHaut.add(cboAttributs);

		txtSearch = new JXSearchField(MTGControler.getInstance().getLangService().getCapitalize("SEARCH_MODULE"));
		txtSearch.setSearchMode(MTGConstants.SEARCH_MODE);
		txtSearch.setBackground(Color.WHITE);

		panneauHaut.add(txtSearch);
		txtSearch.setColumns(25);

		JLabel lblCards = new JLabel();
		panneauHaut.add(lblCards);

		JButton btnNewDeck = new JButton(MTGConstants.ICON_NEW);
		btnNewDeck.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("CREATE_NEW_DECK"));

		panneauHaut.add(btnNewDeck);

		btnNewDeck.addActionListener(newDeckEvent -> {

			MagicDeck newDeck = new MagicDeck();
			setDeck(newDeck);
			deckmodel.init(newDeck);
			deckSidemodel.init(newDeck);
		});

		JButton btnOpen = new JButton(MTGConstants.ICON_OPEN);
		btnOpen.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("OPEN_DECK"));
		panneauHaut.add(btnOpen);

		btnOpen.addActionListener(openEvent -> {
			try {
				JDeckChooserDialog choose = new JDeckChooserDialog();
				choose.setVisible(true);
				deck = choose.getSelectedDeck();
				if (deck != null) {
					deckDetailsPanel.setMagicDeck(deck);
					deckmodel.init(deck);
					deckSidemodel.init(deck);
					setDeck(deck);
					updatePanels();

				}
			} catch (Exception ex) {
				logger.error(ex);
				JOptionPane.showMessageDialog(null, ex, MTGControler.getInstance().getLangService().getError(),
						JOptionPane.ERROR_MESSAGE);
			}

		});

		btnUpdate = new JButton();
		btnUpdate.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("UPDATE_DECK"));
		btnUpdate.addActionListener(updateEvent -> ThreadManager.getInstance().execute(() -> {

			Map<MagicCard, Integer> updateM = new HashMap<>();
			Map<MagicCard, Integer> updateS = new HashMap<>();

			btnUpdate.setEnabled(false);
			loading(true, "");
			for (MagicCard mc : deck.getMap().keySet()) {
				try {
					updateM.put(MTGControler.getInstance().getEnabledCardsProviders().getCardById(mc.getId()),
							deck.getMap().get(mc));
				} catch (Exception e) {
					logger.error(e);
					btnUpdate.setEnabled(true);
					loading(false, "");
				}
			}
			for (MagicCard mc : deck.getMapSideBoard().keySet()) {
				try {
					updateS.put(MTGControler.getInstance().getEnabledCardsProviders().getCardById(mc.getId()),
							deck.getMapSideBoard().get(mc));
				} catch (Exception e) {
					btnUpdate.setEnabled(true);
					loading(false, "");
				}
			}

			deck.getMap().clear();
			deck.setMapDeck(updateM);

			deck.getMapSideBoard().clear();
			deck.setMapSideBoard(updateS);

			updatePanels();

			btnUpdate.setEnabled(true);
			loading(false, "");
			JOptionPane.showMessageDialog(null,
					MTGControler.getInstance().getLangService().getCapitalize("UPDATED_DECK"),
					MTGControler.getInstance().getLangService().getCapitalize("FINISHED"),
					JOptionPane.INFORMATION_MESSAGE);
		}, "Update Deck"));
		btnUpdate.setIcon(MTGConstants.ICON_REFRESH);

		panneauHaut.add(btnUpdate);

		JButton btnSave = new JButton(MTGConstants.ICON_SAVE);
		btnSave.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("SAVE_DECK"));
		panneauHaut.add(btnSave);

		btnSave.addActionListener(e -> {
			try {
				String name = JOptionPane.showInputDialog(
						MTGControler.getInstance().getLangService().getCapitalize("DECK_NAME") + " ?", deck.getName());
				deck.setName(name);
				deckManager.saveDeck(deck);
			} catch (Exception ex) {
				logger.error("error saving", ex);
				JOptionPane.showMessageDialog(null, ex, MTGControler.getInstance().getLangService().getError(),
						JOptionPane.ERROR_MESSAGE);
			}

		});

		JButton btnImport = new JButton(MTGConstants.ICON_IMPORT);
		btnImport.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("DECK_IMPORT_AS"));

		btnImport.addActionListener(ae -> {
			JPopupMenu menu = new JPopupMenu();
			for (final MTGCardsExport exp : MTGControler.getInstance().getEnabledDeckExports()) {
				if (exp.getMods() == MODS.BOTH || exp.getMods() == MODS.IMPORT) {

					JMenuItem it = new JMenuItem();
					it.setIcon(exp.getIcon());
					it.setText(exp.getName());
					it.addActionListener(itEvent -> {
						JFileChooser jf = new JFileChooser(".");
						jf.setFileFilter(new FileFilter() {
							@Override
							public String getDescription() {
								return exp.getName();
							}
							@Override
							public boolean accept(File f) {
								return (f.isDirectory() || f.getName().endsWith(exp.getFileExtension()));
							}
						});

						int res = -1;
						f = new File("");

						if (!exp.needDialogGUI()) {
							res = jf.showOpenDialog(null);
							f = jf.getSelectedFile();
						} else {
							res = JFileChooser.APPROVE_OPTION;

						}

						if (res == JFileChooser.APPROVE_OPTION)
							ThreadManager.getInstance().execute(() -> {
								try {
									loading(true, MTGControler.getInstance().getLangService().get("LOADING_FILE",f.getName(), exp));
									deck = exp.importDeck(f);
									JOptionPane.showMessageDialog(null,
											MTGControler.getInstance().getLangService().getCapitalize("FINISHED"),
											exp.getName() + " "
													+ MTGControler.getInstance().getLangService().get("FINISHED"),
											JOptionPane.INFORMATION_MESSAGE);
									setDeck(deck);
									loading(false, "");
									deckmodel.init(deck);
									deckSidemodel.init(deck);
									setDeck(deck);
									updatePanels();

								} catch (Exception e) {
									logger.error("error import", e);
									loading(false, "");
									JOptionPane.showMessageDialog(null, e,
											MTGControler.getInstance().getLangService().getError(),
											JOptionPane.ERROR_MESSAGE);
								}

							}, "import " + exp);

					});

					menu.add(it);

				}
			}

			Component b = (Component) ae.getSource();
			Point point = b.getLocationOnScreen();
			menu.show(b, 0, 0);
			menu.setLocation(point.x, point.y + b.getHeight());

		});

		panneauHaut.add(btnImport);

		btnExports = new JButton();
		btnExports.setEnabled(false);
		btnExports.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("EXPORT_AS"));
		btnExports.setIcon(MTGConstants.ICON_EXPORT);

		btnExports.addActionListener(exportsAction -> {
			JPopupMenu menu = new JPopupMenu();

			for (final MTGCardsExport exp : MTGControler.getInstance().getEnabledDeckExports()) {
				if (exp.getMods() == MODS.BOTH || exp.getMods() == MODS.EXPORT) {
					JMenuItem it = new JMenuItem();
					it.setIcon(exp.getIcon());
					it.setText(exp.getName());
					it.addActionListener(pluginExportEvent -> {
						JFileChooser jf = new JFileChooser(".");
						jf.setSelectedFile(new File(deck.getName() + exp.getFileExtension()));
						jf.showSaveDialog(null);
						exportedFile = jf.getSelectedFile();
						ThreadManager.getInstance().execute(() -> {
							try {
								loading(true, MTGControler.getInstance().getLangService().get("EXPORT_TO", deck, exp));
								exp.export(deck, exportedFile);
								JOptionPane.showMessageDialog(null,
										MTGControler.getInstance().getLangService().combine("EXPORT", "FINISHED"),
										exp.getName() + " "
												+ MTGControler.getInstance().getLangService().getCapitalize("FINISHED"),
										JOptionPane.INFORMATION_MESSAGE);
								loading(false, "");
							} catch (Exception e) {
								logger.error(e);
								loading(false, "");
								JOptionPane.showMessageDialog(null, e,
										MTGControler.getInstance().getLangService().getError(),
										JOptionPane.ERROR_MESSAGE);
							}
						}, "Export " + deck + " to " + exp.getName());
					});
					menu.add(it);
				}
			}

			Component b = (Component) exportsAction.getSource();
			Point point = b.getLocationOnScreen();
			menu.show(b, 0, 0);
			menu.setLocation(point.x, point.y + b.getHeight());

		});
		panneauHaut.add(btnExports);

		panneauHaut.add(lblExport);

		JPanel panneauBas = new JPanel();
		add(panneauBas, BorderLayout.SOUTH);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		add(tabbedPane, BorderLayout.CENTER);

		JSplitPane panneauDeck = new JSplitPane();
		panneauDeck.setOrientation(JSplitPane.VERTICAL_SPLIT);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("DECK"), MTGConstants.ICON_TAB_DECK, panneauDeck, null);
		DefaultRowSorter sorterCards = new TableRowSorter<DefaultTableModel>(deckmodel);

		magicCardDetailPanel = new MagicCardDetailPanel();
		magicCardDetailPanel.setPreferredSize(new Dimension(0, 0));
		magicCardDetailPanel.enableThumbnail(true);
		panelBottom.setLayout(new BorderLayout(0, 0));
		panelBottom.add(magicCardDetailPanel);
		panneauDeck.setRightComponent(panelBottom);

		cardDrawProbaPanel = new DrawProbabilityPanel();
		panelBottom.add(cardDrawProbaPanel, BorderLayout.EAST);

		final JTabbedPane tabbedDeckSide = new JTabbedPane(JTabbedPane.BOTTOM);

		panneauDeck.setLeftComponent(tabbedDeckSide);

		JScrollPane scrollDeck = new JScrollPane();
		tabbedDeckSide.addTab("Main", MTGConstants.ICON_TAB_DECK, scrollDeck, null);

		tableDeck = new JTable();
		scrollDeck.setViewportView(tableDeck);

		tableDeck.setModel(deckmodel);
		tableDeck.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());
		tableDeck.setRowHeight(MTGConstants.TABLE_ROW_HEIGHT);
		tableDeck.setRowSorter(sorterCards);
		tableDeck.getColumnModel().getColumn(4).setCellEditor(new IntegerCellEditor());
		tableDeck.getColumnModel().getColumn(3).setCellRenderer(new MagicEditionsComboBoxRenderer());
		tableDeck.getColumnModel().getColumn(3).setCellEditor(new MagicEditionsComboBoxEditor());

		JScrollPane scrollSideboard = new JScrollPane();
		tabbedDeckSide.addTab("SideBoard", MTGConstants.ICON_TAB_DECK, scrollSideboard, null);

		tableSide = new JTable();
		tableSide.setModel(deckSidemodel);
		tableSide.setRowHeight(MTGConstants.TABLE_ROW_HEIGHT);
		tableSide.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());
		tableSide.getColumnModel().getColumn(4).setCellEditor(new IntegerCellEditor());
		tableSide.getColumnModel().getColumn(3).setCellRenderer(new MagicEditionsComboBoxRenderer());
		tableSide.getColumnModel().getColumn(3).setCellEditor(new MagicEditionsComboBoxEditor());
		tableSide.getColumnModel().getColumn(4).setCellEditor(new IntegerCellEditor());

		scrollSideboard.setViewportView(tableSide);

		tableDeck.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent ev) {

				MagicCard mc = (MagicCard) tableDeck.getValueAt(tableDeck.getSelectedRow(), 0);
				magicCardDetailPanel.setMagicCard(mc);
				cardDrawProbaPanel.init(deck, mc);
			}
		});

		tableSide.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent ev) {

				MagicCard mc = (MagicCard) tableSide.getValueAt(tableSide.getSelectedRow(), 0);
				magicCardDetailPanel.setMagicCard(mc);

			}
		});

		tableDeck.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				MagicCard mc = (MagicCard) tableDeck.getValueAt(tableDeck.getSelectedRow(), 0);
				if (e.getKeyCode() == 0) {
					deck.getMap().remove(mc);
					deckmodel.init(deck);
				}

			}
		});

		tableSide.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				MagicCard mc = (MagicCard) tableSide.getValueAt(tableSide.getSelectedRow(), 0);
				if (e.getKeyCode() == 0) {
					deck.getMapSideBoard().remove(mc);
					deckmodel.init(deck);
				}

			}
		});

		tableDeck.getModel().addTableModelListener(e -> updatePanels());

		tableSide.getModel().addTableModelListener(e -> updatePanels());

		tableDeck.getDefaultEditor(String.class).addCellEditorListener(new CellEditorListener() {

			@Override
			public void editingStopped(ChangeEvent e) {
				updatePanels();

			}

			@Override
			public void editingCanceled(ChangeEvent e) {
				updatePanels();

			}
		});

		tableSide.getDefaultEditor(String.class).addCellEditorListener(new CellEditorListener() {

			@Override
			public void editingStopped(ChangeEvent e) {
				updatePanels();

			}

			@Override
			public void editingCanceled(ChangeEvent e) {
				updatePanels();

			}
		});

		JPanel panelInfoDeck = new JPanel();
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("INFORMATIONS"), MTGConstants.ICON_TAB_DETAILS,
				panelInfoDeck, null);
		panelInfoDeck.setLayout(new BorderLayout(0, 0));

		panelInfoDeck.add(deckDetailsPanel, BorderLayout.NORTH);
		deckDetailsPanel.setMagicDeck(deck);

		cmcChartPanel = new CmcChartPanel();
		manaRepartitionPanel = new ManaRepartitionPanel();
		typeRepartitionPanel = new TypeRepartitionPanel();
		rarityRepartitionPanel = new RarityRepartitionPanel();
		drawProbabilityPanel = new DrawProbabilityPanel();

		JPanel randomHandPanel = new JPanel();
		JPanel statPanel = new JPanel();

		randomHandPanel.setLayout(new BorderLayout(0, 0));
		randomHandPanel.add(thumbnail, BorderLayout.CENTER);

		statPanel.setLayout(new GridLayout(3, 2, 0, 0));
		statPanel.add(manaRepartitionPanel);
		statPanel.add(typeRepartitionPanel);
		statPanel.add(rarityRepartitionPanel);
		statPanel.add(cmcChartPanel);
		statPanel.add(drawProbabilityPanel);

		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("STATS"), MTGConstants.ICON_TAB_ANALYSE, statPanel, null);

		deckPricePanel = new DeckPricePanel();
		statPanel.add(deckPricePanel);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("SAMPLE_HAND"), MTGConstants.ICON_TAB_THUMBNAIL,
				randomHandPanel, null);

		JPanel panel = new JPanel();
		randomHandPanel.add(panel, BorderLayout.NORTH);

		JButton btnDrawAHand = new JButton(MTGControler.getInstance().getLangService().getCapitalize("DRAW_HAND"));
		btnDrawAHand.addActionListener(ae -> {
			thumbnail.removeAll();
			p.mixHandAndLibrary();
			p.shuffleLibrary();
			p.drawCard(7);
			thumbnail.initThumbnails(p.getHand(), false, false);

		});
		panel.add(btnDrawAHand);

		JPanel panneauGauche = new JPanel();
		add(panneauGauche, BorderLayout.WEST);
		panneauGauche.setLayout(new BorderLayout(0, 0));

		scrollResult = new JScrollPane();
		panneauGauche.add(scrollResult);

		listResult = new JList<>(new DefaultListModel<MagicCard>());
		listResult.setCellRenderer(new MagicCardListRenderer());
		listResult.setMinimumSize(new Dimension(100, 0));
		listResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollResult.setViewportView(listResult);

		JPanel panneauResultFilter = new JPanel();
		panneauGauche.add(panneauResultFilter, BorderLayout.NORTH);

		groupsFilterResult = new ButtonGroup() {
			@Override
			public void setSelected(ButtonModel model, boolean selected) {
				if (selected) {
					super.setSelected(model, selected);
				} else {
					clearSelection();
				}
			}
		};

		JToggleButton tglbtnStd = new JToggleButton("STD");
		tglbtnStd.setActionCommand("Standard");
		panneauResultFilter.add(tglbtnStd);

		JToggleButton tglbtnMdn = new JToggleButton("MDN");
		tglbtnMdn.setActionCommand("Modern");
		panneauResultFilter.add(tglbtnMdn);

		JToggleButton tglbtnLeg = new JToggleButton("LEG");
		tglbtnLeg.setActionCommand("Legacy");
		panneauResultFilter.add(tglbtnLeg);

		JToggleButton tglbtnVin = new JToggleButton("VIN");
		tglbtnVin.setActionCommand("Vintage");
		panneauResultFilter.add(tglbtnVin);

		groupsFilterResult.add(tglbtnStd);
		groupsFilterResult.add(tglbtnMdn);
		groupsFilterResult.add(tglbtnLeg);
		groupsFilterResult.add(tglbtnVin);

		listResult.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent ev) {

				if (ev.getClickCount() == 1 && !ev.isConsumed()) {
					ev.consume();
					MagicCard mc = listResult.getSelectedValue();
					magicCardDetailPanel.setMagicCard(mc);

				}

				if (ev.getClickCount() == 2 && !ev.isConsumed()) {
					ev.consume();

					MagicCard mc = listResult.getSelectedValue();

					if (getSelectedMap().get(mc) != null) {
						getSelectedMap().put(mc, deck.getMap().get(mc) + 1);
					} else {
						getSelectedMap().put(mc, 1);
					}
					deckmodel.init(deck);
					deckSidemodel.init(deck);
				}
			}
		});

		tabbedDeckSide.addChangeListener(e -> selectedIndex = tabbedDeckSide.getSelectedIndex());

		txtSearch.addActionListener(aeSearch -> {

			if (txtSearch.getText().equals(""))
				return;

			resultListModel.removeAllElements();

			ThreadManager.getInstance().execute(() -> {
				try {
					String searchName = txtSearch.getText();
					List<MagicCard> cards = MTGControler.getInstance().getEnabledCardsProviders().searchCardByCriteria(cboAttributs.getSelectedItem().toString(), searchName, null, false);
					MagicFormat form = new MagicFormat();

					for (MagicCard m : cards) {
						if (groupsFilterResult.getSelection() != null) {
							form.setFormat(groupsFilterResult.getSelection().getActionCommand());
							if (m.getLegalities().contains(form))
								resultListModel.addElement(m);
						} else {
							resultListModel.addElement(m);
						}
					}
					lblCards.setText(
							resultListModel.size() + " " + MTGControler.getInstance().getLangService().get("RESULTS"));
					listResult.setModel(resultListModel);
					listResult.updateUI();

				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage(),
							MTGControler.getInstance().getLangService().getError(), JOptionPane.ERROR_MESSAGE);
				}

			}, "search deck");

		});
	}

	public Map<MagicCard, Integer> getSelectedMap() {
		if (selectedIndex > 0)
			return deck.getMapSideBoard();
		else
			return deck.getMap();

	}

	protected void updatePanels() {

		deckDetailsPanel.setMagicDeck(deck);
		cmcChartPanel.init(deck);
		typeRepartitionPanel.init(deck.getAsList());
		manaRepartitionPanel.init(deck);
		rarityRepartitionPanel.init(deck);
		deckPricePanel.initDeck(deck);
		drawProbabilityPanel.init(deck);
		btnExports.setEnabled(!deck.getAsList().isEmpty());

	}
}

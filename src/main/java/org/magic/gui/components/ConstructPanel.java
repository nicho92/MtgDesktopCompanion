package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicFormat;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsExport.MODS;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.game.gui.components.HandPanel;
import org.magic.game.model.Player;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.components.charts.CmcChartPanel;
import org.magic.gui.components.charts.DrawProbabilityPanel;
import org.magic.gui.components.charts.ManaRepartitionPanel;
import org.magic.gui.components.charts.RarityRepartitionPanel;
import org.magic.gui.components.charts.TypeRepartitionPanel;
import org.magic.gui.components.dialog.JDeckChooserDialog;
import org.magic.gui.editor.IntegerCellEditor;
import org.magic.gui.editor.MagicEditionsComboBoxCellEditor;
import org.magic.gui.models.DeckCardsTableModel;
import org.magic.gui.renderer.MagicCardListRenderer;
import org.magic.gui.renderer.MagicEditionsComboBoxCellRenderer;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGDeckManager;
import org.magic.services.MTGLogger;
import org.magic.services.threads.ThreadManager;
import org.magic.services.workers.AbstractObservableWorker;
import org.magic.services.workers.DeckImportWorker;
import org.magic.tools.UITools;

public class ConstructPanel extends JPanel {

	
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
	private MagicDeck deck;
	private JExportButton btnExports;
	private transient MTGDeckManager deckManager;
	private DefaultListModel<MagicCard> resultListModel = new DefaultListModel<>();
	private JList<MagicCard> listResult;
	private DrawProbabilityPanel cardDrawProbaPanel;
	public static final int MAIN = 0;
	public static final int SIDE = 1;
	protected int selectedIndex = 0;

	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private File f;
	private JLabel lblCards;
	private DeckStockComparatorPanel stockPanel;
	private JXTable tableDeck;
	private JXTable tableSide;
	
	
	public ConstructPanel() {
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
		deckSidemodel.init(deck);
		stockPanel.setCurrentDeck(deck);
		tableDeck.packAll();
		tableSide.packAll();
	}

	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		Player p = new Player();
		
		
		JPanel panneauHaut = new JPanel();
		JButton btnUpdate;
		HandPanel thumbnail;
		JTabbedPane panelBottom;
		JTextField txtSearch;
		JComboBox<String> cboAttributs;
		JTabbedPane tabbedPane;
		ButtonGroup groupsFilterResult;
		AbstractBuzyIndicatorComponent buzyLabel = AbstractBuzyIndicatorComponent.createProgressComponent();
		deckmodel = new DeckCardsTableModel(DeckCardsTableModel.TYPE.DECK);
		deckSidemodel = new DeckCardsTableModel(DeckCardsTableModel.TYPE.SIDE);
		deckDetailsPanel = new DeckDetailsPanel();
		panelBottom = new JTabbedPane();
		thumbnail = new HandPanel();
		FlowLayout flowLayout = (FlowLayout) panneauHaut.getLayout();
		cboAttributs = UITools.createCombobox(MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getQueryableAttributs());	
		txtSearch = UITools.createSearchField();
		comboPanel = new ComboFinderPanel();
		
		
		lblCards = new JLabel();
		JButton btnNewDeck = new JButton(MTGConstants.ICON_NEW);
		JButton btnOpen = new JButton(MTGConstants.ICON_OPEN);
		btnUpdate = new JButton();
		JButton btnSave = new JButton(MTGConstants.ICON_SAVE);
		JButton btnImport = new JButton(MTGConstants.ICON_IMPORT);
		btnExports = new JExportButton(MODS.EXPORT);
		stockPanel = new DeckStockComparatorPanel();
		AbstractBuzyIndicatorComponent buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		JPanel panneauBas = new JPanel();
		tabbedPane = new JTabbedPane(SwingConstants.TOP);
		JSplitPane panneauDeck = new JSplitPane();
		magicCardDetailPanel = new MagicCardDetailPanel();
		cardDrawProbaPanel = new DrawProbabilityPanel();
		tableDeck = new JXTable();
		tableSide = new JXTable();
		final JTabbedPane tabbedDeckSide = new JTabbedPane(SwingConstants.RIGHT);
		JPanel panelInfoDeck = new JPanel();
		cmcChartPanel = new CmcChartPanel();
		manaRepartitionPanel = new ManaRepartitionPanel();
		typeRepartitionPanel = new TypeRepartitionPanel();
		rarityRepartitionPanel = new RarityRepartitionPanel();
		drawProbabilityPanel = new DrawProbabilityPanel();
		JPanel randomHandPanel = new JPanel();
		JPanel statPanel = new JPanel();
		deckPricePanel = new DeckPricePanel();
		JPanel panel = new JPanel();
		JButton btnDrawAHand = new JButton(MTGControler.getInstance().getLangService().getCapitalize("DRAW_HAND"));
		JPanel panneauResultFilter = new JPanel();
		JToggleButton tglbtnStd = new JToggleButton("STD");
		JToggleButton tglbtnMdn = new JToggleButton("MDN");
		JToggleButton tglbtnLeg = new JToggleButton("LEG");
		JToggleButton tglbtnVin = new JToggleButton("VIN");
		JToggleButton tglbtnCmd = new JToggleButton("CMD");
		
		JPanel panneauGauche = new JPanel();
		listResult = new JList<>(new DefaultListModel<MagicCard>());
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

		
		thumbnail.setThumbnailSize(new Dimension(223, 311));
		thumbnail.enableDragging(false);
		thumbnail.setMaxCardsRow(4);
		flowLayout.setAlignment(FlowLayout.LEFT);
		txtSearch.setBackground(Color.WHITE);
		txtSearch.setColumns(25);
		btnNewDeck.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("CREATE_NEW_DECK"));
		btnOpen.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("OPEN_DECK"));
		btnUpdate.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("UPDATE_DECK"));
		btnUpdate.setIcon(MTGConstants.ICON_REFRESH);
		btnSave.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("SAVE_DECK"));
		btnExports.setEnabled(false);
		btnExports.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("EXPORT_AS"));
		
		panneauDeck.setDividerLocation(0.5);
		panneauDeck.setResizeWeight(0.5);
		panneauDeck.setOrientation(JSplitPane.VERTICAL_SPLIT);
		magicCardDetailPanel.enableThumbnail(true);
		panelBottom.setLayout(new BorderLayout(0, 0));
		panelInfoDeck.setLayout(new BorderLayout(0, 0));
		randomHandPanel.setLayout(new BorderLayout(0, 0));
		statPanel.setLayout(new GridLayout(3, 2, 0, 0));
		btnImport.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("DECK_IMPORT_AS"));
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
		panneauHaut.add(cboAttributs);
		panneauHaut.add(txtSearch);
		panneauHaut.add(buzy);
		panneauHaut.add(lblCards);
		panneauHaut.add(btnNewDeck);
		panneauHaut.add(btnOpen);
		panneauHaut.add(btnUpdate);
		panneauHaut.add(btnSave);
		panneauHaut.add(btnImport);
		panneauHaut.add(btnExports);
		panneauHaut.add(buzyLabel);
		add(panneauBas, BorderLayout.SOUTH);
		add(tabbedPane, BorderLayout.CENTER);
		panelBottom.addTab(MTGControler.getInstance().getLangService().getCapitalize("DETAIL"),MTGConstants.ICON_TAB_CARD,magicCardDetailPanel);
		panelBottom.addTab("Combos",comboPanel.getIcon(),comboPanel);
		panneauDeck.setRightComponent(panelBottom);
		panelBottom.addTab("Drawing",MTGConstants.ICON_TAB_DECK,cardDrawProbaPanel);
		panneauDeck.setLeftComponent(tabbedDeckSide);
		tabbedDeckSide.addTab("Main", MTGConstants.ICON_TAB_DECK, new JScrollPane(tableDeck), null);
		tabbedDeckSide.addTab("SideBoard", MTGConstants.ICON_TAB_DECK, new JScrollPane(tableSide), null);
		
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("DECK"), MTGConstants.ICON_TAB_DECK,panneauDeck, null);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("INFORMATIONS"),MTGConstants.ICON_TAB_DETAILS, panelInfoDeck, null);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("STATS"),MTGConstants.ICON_TAB_ANALYSE, statPanel, null);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("SAMPLE_HAND"),MTGConstants.ICON_TAB_THUMBNAIL, randomHandPanel, null);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("STOCK_MODULE"),MTGConstants.ICON_TAB_STOCK, stockPanel, null);
	
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

			
		
		
		initTables(tableDeck,MAIN,deckmodel);
		initTables(tableSide,SIDE,deckSidemodel);
		deckDetailsPanel.setMagicDeck(deck);
		
//////////////////////////////////////////////////////////////////ACTIONS		
		btnNewDeck.addActionListener(newDeckEvent -> {
			MagicDeck newDeck = new MagicDeck();
			setDeck(newDeck);
		});

		
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
				MTGControler.getInstance().notify(ex);
			}

		});

		btnUpdate.addActionListener(updateEvent ->  {
			Map<MagicCard, Integer> updateM = new HashMap<>();
			Map<MagicCard, Integer> updateS = new HashMap<>();
			btnUpdate.setEnabled(false);
			buzyLabel.start(deck.getMain().size() + deck.getSideBoard().size());
			SwingWorker<Void, MagicCard> sw = new SwingWorker<>()
					{
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
							MTGControler.getInstance()
									.notify(new MTGNotification(MTGControler.getInstance().getLangService().getCapitalize(FINISHED),
											MTGControler.getInstance().getLangService().getCapitalize(UPDATED_DECK),
											MESSAGE_TYPE.INFO));
						}

						@Override
						protected void process(List<MagicCard> chunks) {
							buzyLabel.progressSmooth(chunks.size());
						}

						@Override
						protected Void doInBackground() throws Exception {
							for (MagicCard mc : deck.getMain().keySet()) {
								try {
									updateM.put(MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName(mc.getName(), mc.getCurrentSet(), true).get(0),deck.getMain().get(mc));
									publish(mc);
								} catch (Exception e) {
									logger.error("error update " + mc,e);
								}
							}
							for (MagicCard mc : deck.getSideBoard().keySet()) {
								try {
									updateS.put(MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName(mc.getName(), mc.getCurrentSet(), true).get(0),deck.getSideBoard().get(mc));
									publish(mc);
								} catch (Exception e) {
									logger.error("error update " + mc,e);
									
								}
							}
							return null;
						}
				
					};
			ThreadManager.getInstance().runInEdt(sw,"updating "+deck);
		});
		
		btnSave.addActionListener(e -> {
			try {
				logger.debug("saving " + deck);
				
				String dname = deck.getName();
				String name = JOptionPane.showInputDialog(MTGControler.getInstance().getLangService().getCapitalize("DECK_NAME") + " ?", dname);
				
				if(name!=null && !name.isEmpty())
				{
					deck.setName(name);
					deckManager.saveDeck(deck);
					p.setDeck(deck);
				}
			} catch (Exception ex) {
				logger.error("error saving", ex);
				MTGControler.getInstance().notify(ex);
			}

		});

	

		
	btnDrawAHand.addActionListener(ae -> {
			thumbnail.removeAll();
			p.setDeck(deck);
			p.mixHandAndLibrary();
			p.shuffleLibrary();
			p.drawCard(7);
			thumbnail.initThumbnails(p.getHand().getCards(), false, false);

		});
	
		listResult.getSelectionModel().addListSelectionListener(lsl->{
			
			if(!lsl.getValueIsAdjusting())
			{
				MagicCard mc = listResult.getSelectedValue();
				magicCardDetailPanel.setMagicCard(mc);
			}
			
		});
	
	

		listResult.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent ev) {
				if (ev.getClickCount() == 2 && !ev.isConsumed()) {
					ev.consume();

					MagicCard mc = listResult.getSelectedValue();

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

		tabbedDeckSide.addChangeListener(e -> selectedIndex = tabbedDeckSide.getSelectedIndex());

		btnImport.addActionListener(ae -> {
			JPopupMenu menu = new JPopupMenu();
			for (final MTGCardsExport exp : MTGControler.getInstance().listEnabled(MTGCardsExport.class)) {
				if (exp.getMods() == MODS.BOTH || exp.getMods() == MODS.IMPORT) {

					JMenuItem it = new JMenuItem(exp.getName(),exp.getIcon());
					it.addActionListener(itEvent -> {
						JFileChooser jf = new JFileChooser(MTGConstants.DATA_DIR);
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
						
						if (!exp.needDialogForDeck(MODS.IMPORT) && exp.needFile()) {
							res = jf.showOpenDialog(null);
							f = jf.getSelectedFile();
						} 
						else if(!exp.needFile() && !exp.needDialogForDeck(MODS.IMPORT))
						{
							logger.trace(exp + " need no file. Skip");
							res = JFileChooser.APPROVE_OPTION;
						}
						else 
						{
							try {
								setDeck(exp.importDeckFromFile(null));
								updatePanels();
							} catch (IOException e1) {
								logger.error(e1);
							}
							
						}

						if (res == JFileChooser.APPROVE_OPTION) 
						{
							buzyLabel.start();
							DeckImportWorker importWork = new DeckImportWorker(exp, buzyLabel, f) {
								@Override
								protected void done() {
									super.done();
									try {
										setDeck(get());
										updatePanels();
									} catch (Exception e) {
										logger.error(e);
									} 
								}
							};
							ThreadManager.getInstance().runInEdt(importWork,"Deck imports from "+f);
						}
					});
				
					menu.add(it);

				}
			}

			Component b = (Component) ae.getSource();
			Point point = b.getLocationOnScreen();
			menu.show(b, 0, 0);
			menu.setLocation(point.x, point.y + b.getHeight());

		});
		
		
		btnExports.initCardsExport(new Callable<MagicDeck>() {
			@Override
			public MagicDeck call() throws Exception {
				return deck;
			}
		}, buzyLabel);
				
		txtSearch.addActionListener(aeSearch -> {
			
			resultListModel.clear();
			
			if (txtSearch.getText().isEmpty())
				return;

			AbstractObservableWorker<List<MagicCard>,MagicCard,MTGCardsProvider> sw = new AbstractObservableWorker<>(buzy,MTGControler.getInstance().getEnabled(MTGCardsProvider.class))
			{
				@Override
				protected List<MagicCard> doInBackground() throws Exception {
					return plug.searchCardByCriteria(cboAttributs.getSelectedItem().toString(), txtSearch.getText(), null, false);
				}

				@Override
				protected void process(List<MagicCard> chunks) {
					super.process(chunks);
					MagicFormat form = new MagicFormat();
					for (MagicCard m : chunks) {
						if (groupsFilterResult.getSelection() != null) {
							form.setFormat(groupsFilterResult.getSelection().getActionCommand());
							if (m.getLegalities().contains(form))
								resultListModel.addElement(m);
						} else {
							resultListModel.addElement(m);
						}
					}
				}
				@Override
				protected void done() {
					super.done();
					lblCards.setText(resultListModel.size() + " " + MTGControler.getInstance().getLangService().get("RESULTS"));
					listResult.setModel(resultListModel);
					listResult.updateUI();
				}
			};
			
			ThreadManager.getInstance().runInEdt(sw,"search cards for deck");

		});
	}

	private void initTables(JXTable table, int f, DeckCardsTableModel model) {
		table.setModel(model);
		table.setRowSorter(new TableRowSorter<DefaultTableModel>(model));
		table.setRowHeight(MTGConstants.TABLE_ROW_HEIGHT);
		table.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());
		table.getColumnModel().getColumn(3).setCellRenderer(new MagicEditionsComboBoxCellRenderer());
		table.getColumnModel().getColumn(3).setCellEditor(new MagicEditionsComboBoxCellEditor());
		table.getColumnModel().getColumn(4).setCellEditor(new IntegerCellEditor());
	
		
		table.getColumnModel().getColumn(0).setCellRenderer((JTable table2, Object value, boolean isSelected, boolean hasFocus,int row, int column)-> {

			JLabel comp = (JLabel)new DefaultTableCellRenderer().getTableCellRendererComponent(table2, value, isSelected, hasFocus, row, column);
			comp.setText(((MagicCard)value).getName());

			try {
				if(value!=null && deck.getCommander()!=null && ((MagicCard)value).getName().equals(deck.getCommander().getName()))
					comp.setFont(MTGControler.getInstance().getFont().deriveFont(Font.BOLD));
				}
				catch(Exception e){
					logger.error("error applying font " + value + " "+ deck +":"+e);
				}
			return comp;
			
			
		});
		
		
		
		table.getSelectionModel().addListSelectionListener(event -> {
			if (!event.getValueIsAdjusting()) {
				try {
					MagicCard mc = UITools.getTableSelection(table, 0);
					magicCardDetailPanel.setMagicCard(mc);
					comboPanel.init(mc);
					
					if(f==MAIN)
						cardDrawProbaPanel.init(deck, mc);
					
					
				} catch (Exception e) {
					logger.error(e);
				}
			}
		});
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent ev) {
				
				if(UITools.getTableSelections(table, 0).isEmpty())
					return;
				
				MagicCard mc = UITools.getTableSelection(table, 0);
				magicCardDetailPanel.setMagicCard(mc);
				
				if(f==MAIN)
					cardDrawProbaPanel.init(deck, mc);
				
				if(SwingUtilities.isRightMouseButton(ev))
				{
					JPopupMenu menu = new JPopupMenu();
	
					JMenuItem itemDel = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("DELETE"));
					menu.add(itemDel);
					itemDel.addActionListener(ae->{
						
						deck.delete(mc);
						model.fireTableDataChanged();
						
					});
					
					
					if(mc.isLegendary() && (mc.isCreature()||mc.isPlaneswalker())) {
					
							if((deck.getCommander()!=null) && mc.getName().equals(deck.getCommander().getName()))
							{
								JMenuItem itemRemoveCommander = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("REMOVE_COMMANDER"));
								menu.add(itemRemoveCommander);
								itemRemoveCommander.addActionListener(ae->deck.setCommander(null));	
							}
							else
							{
								JMenuItem itemSelCommander = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("SELECT_COMMANDER"));
								menu.add(itemSelCommander);
								itemSelCommander.addActionListener(ae->deck.setCommander(mc));
							
							}
					}
					
					
					
					
					JMenuItem item = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("MORE_LIKE_THIS"));
					menu.add(item);
					item.addActionListener(ae->{
						
						resultListModel.removeAllElements();
						try {
							for(MagicCard card : MTGControler.getInstance().getEnabled(MTGCardsIndexer.class).similarity(mc).keySet())
								resultListModel.addElement(card);
						
							lblCards.setText(resultListModel.size() + " " + MTGControler.getInstance().getLangService().get("RESULTS"));
							listResult.setModel(resultListModel);
							listResult.updateUI();
							
						} catch (IOException e) {
							logger.error(e);
						}
					});
					Point point = ev.getPoint();
					menu.show(table, (int) point.getX(), (int) point.getY());
					
				}
				
				
			}
		});
		
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				MagicCard mc = UITools.getTableSelection(table, 0);
				
				//tableCards.convertRowIndexToModel(i)
				int sel = table.getSelectedRow();
				if (e.getKeyChar() == '+') {
					if(f==MAIN)
						deck.add(mc);
					else
						deck.addSide(mc);
					
					deckmodel.init(deck);
				}
				
				if (e.getKeyChar() == '-') {
					if(f==MAIN)
						deck.remove(mc);
					else
						deck.removeSide(mc);
					
					deckmodel.init(deck);
				}
				
				table.setRowSelectionInterval(sel, sel);
				
			}
		});
		
		table.getModel().addTableModelListener(e -> updatePanels());

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

	public Map<MagicCard, Integer> getSelectedMap() {
		
		if (selectedIndex > 0)
			return deck.getSideBoard();
		else
			return deck.getMain();

	}

	protected void updatePanels() {

		if(deck==null)
			return;

		deckDetailsPanel.setMagicDeck(deck);
		cmcChartPanel.init(deck.getAsList());
		typeRepartitionPanel.init(deck.getAsList());
		manaRepartitionPanel.init(deck.getAsList());
		rarityRepartitionPanel.init(deck.getAsList());
		deckPricePanel.initDeck(deck);
		drawProbabilityPanel.init(deck);
		btnExports.setEnabled(!deck.getAsList().isEmpty());
		
	}
}

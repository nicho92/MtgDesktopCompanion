package org.magic.gui.components;

import static org.magic.tools.MTG.capitalize;
import static org.magic.tools.MTG.getEnabledPlugin;
import static org.magic.tools.MTG.listEnabledPlugins;

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

import org.apache.log4j.Level;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicDeck.BOARD;
import org.magic.api.beans.MagicFormat;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsExport.MODS;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.game.gui.components.HandPanel;
import org.magic.game.model.Player;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.charts.CmcChartPanel;
import org.magic.gui.components.charts.DrawProbabilityPanel;
import org.magic.gui.components.charts.ManaRepartitionPanel;
import org.magic.gui.components.charts.RarityRepartitionPanel;
import org.magic.gui.components.charts.TypeRepartitionPanel;
import org.magic.gui.components.dialog.JDeckChooserDialog;
import org.magic.gui.editor.MagicEditionsComboBoxCellEditor;
import org.magic.gui.models.DeckCardsTableModel;
import org.magic.gui.renderer.MagicCardListRenderer;
import org.magic.gui.renderer.MagicEditionsComboBoxCellRenderer;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.gui.renderer.standard.NumberCellEditorRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGDeckManager;
import org.magic.services.threads.ThreadManager;
import org.magic.services.workers.AbstractObservableWorker;
import org.magic.services.workers.DeckImportWorker;
import org.magic.tools.UITools;
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
	private MagicDeck deck;
	private JExportButton btnExports;
	private transient MTGDeckManager deckManager;
	private DefaultListModel<MagicCard> resultListModel = new DefaultListModel<>();
	private JList<MagicCard> listResult;
	private DrawProbabilityPanel cardDrawProbaPanel;
	protected int selectedIndex = 0;
	private File f;
	private JLabel lblCards;
	private DeckStockComparatorPanel stockPanel;
	private JXTable tableDeck;
	private JXTable tableSide;
	private JButton defaultEnterButton;
	private RulesPanel rulesPanel;
	
	
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

	@Override
	public void onFirstShowing() {
		SwingUtilities.getRootPane(this).setDefaultButton(defaultEnterButton);
	}
	
	
	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		var p = new Player();
		rulesPanel = new RulesPanel();
		var panneauHaut = new JPanel();
		JButton btnUpdate;
		var btnRandom= UITools.createBindableJButton("", MTGConstants.ICON_RANDOM, KeyEvent.VK_R, "Random");
		HandPanel thumbnail;
		JTabbedPane panelBottom;
		var searchComponent = new CriteriaComponent(false);
		JTabbedPane tabbedPane;
		ButtonGroup groupsFilterResult;
		var buzyLabel = AbstractBuzyIndicatorComponent.createProgressComponent();
		deckmodel = new DeckCardsTableModel(DeckCardsTableModel.TYPE.DECK);
		deckSidemodel = new DeckCardsTableModel(DeckCardsTableModel.TYPE.SIDE);
		deckDetailsPanel = new DeckDetailsPanel();
		panelBottom = new JTabbedPane();
		thumbnail = new HandPanel();
		var flowLayout = (FlowLayout) panneauHaut.getLayout();
		comboPanel = new ComboFinderPanel();
		var importLogPanel = new LoggerViewPanel();
		
		lblCards = new JLabel();
		var btnNewDeck = UITools.createBindableJButton("", MTGConstants.ICON_NEW, KeyEvent.VK_N, "New");
		var btnOpen = UITools.createBindableJButton("", MTGConstants.ICON_OPEN, KeyEvent.VK_O, "Open");
		btnUpdate = UITools.createBindableJButton("", MTGConstants.ICON_REFRESH, KeyEvent.VK_R, "Refresh");
		var btnSave = UITools.createBindableJButton("", MTGConstants.ICON_SAVE, KeyEvent.VK_S, "Save");
		var btnImport = UITools.createBindableJButton("", MTGConstants.ICON_IMPORT, KeyEvent.VK_I, "Import");
		btnExports = new JExportButton(MODS.EXPORT);
		stockPanel = new DeckStockComparatorPanel();
		var buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		var panneauBas = new JPanel();
		tabbedPane = new JTabbedPane(SwingConstants.TOP);
		var panneauDeck = new JSplitPane();
		magicCardDetailPanel = new MagicCardDetailPanel();
		cardDrawProbaPanel = new DrawProbabilityPanel();
		tableDeck = UITools.createNewTable(null);
		tableSide = UITools.createNewTable(null);
		final var tabbedDeckSide = new JTabbedPane(SwingConstants.RIGHT);
		var panelInfoDeck = new JPanel();
		cmcChartPanel = new CmcChartPanel();
		manaRepartitionPanel = new ManaRepartitionPanel(false);
		typeRepartitionPanel = new TypeRepartitionPanel(false);
		rarityRepartitionPanel = new RarityRepartitionPanel(false);
		drawProbabilityPanel = new DrawProbabilityPanel();
		var randomHandPanel = new JPanel();
		var statPanel = new JPanel();
		deckPricePanel = new DeckPricePanel();
		var panel = new JPanel();
		var btnDrawAHand = new JButton(capitalize("DRAW_HAND"));
		var panneauResultFilter = new JPanel();
		var tglbtnStd = new JToggleButton("STD");
		var tglbtnMdn = new JToggleButton("MDN");
		var tglbtnLeg = new JToggleButton("LEG");
		var tglbtnVin = new JToggleButton("VIN");
		var tglbtnCmd = new JToggleButton("CMD");
		defaultEnterButton = UITools.createBindableJButton(null, MTGConstants.ICON_SEARCH, KeyEvent.VK_S, "search");
		var panneauGauche = new JPanel();
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
		importLogPanel.setLevel(Level.ERROR);
		panneauDeck.setDividerLocation(0.5);
		panneauDeck.setResizeWeight(0.5);
		panneauDeck.setOrientation(JSplitPane.VERTICAL_SPLIT);
		magicCardDetailPanel.enableThumbnail(true);
		panelBottom.setLayout(new BorderLayout(0, 0));
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
		panneauHaut.add(buzyLabel);
		add(panneauBas, BorderLayout.SOUTH);
		add(tabbedPane, BorderLayout.CENTER);
		panelBottom.addTab(capitalize("DETAIL"),MTGConstants.ICON_TAB_CARD,magicCardDetailPanel);
		panelBottom.addTab("Combos",comboPanel.getIcon(),comboPanel);
		panneauDeck.setRightComponent(panelBottom);
		panelBottom.addTab("Drawing",MTGConstants.ICON_TAB_DECK,cardDrawProbaPanel);
		UITools.addTab(panelBottom, rulesPanel);
		UITools.addTab(panelBottom, stockDetailPanel);
		
		panneauDeck.setLeftComponent(tabbedDeckSide);
		tabbedDeckSide.addTab("Main", MTGConstants.ICON_TAB_DECK, new JScrollPane(tableDeck), null);
		tabbedDeckSide.addTab("SideBoard", MTGConstants.ICON_TAB_DECK, new JScrollPane(tableSide), null);
		
		tabbedPane.addTab(capitalize("DECK"), MTGConstants.ICON_TAB_DECK,panneauDeck, null);
		tabbedPane.addTab(capitalize("INFORMATIONS"),MTGConstants.ICON_TAB_DETAILS, panelInfoDeck, null);
		tabbedPane.addTab(capitalize("STATS"),MTGConstants.ICON_TAB_ANALYSE, statPanel, null);
		tabbedPane.addTab(capitalize("SAMPLE_HAND"),MTGConstants.ICON_TAB_THUMBNAIL, randomHandPanel, null);
		UITools.addTab(tabbedPane, stockPanel);
		
		
	
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
		panelBottom.addTab(capitalize("LOG"),importLogPanel.getIcon(), importLogPanel, null);
			
		
		
		initTables(tableDeck,BOARD.MAIN,deckmodel);
		initTables(tableSide,BOARD.SIDE,deckSidemodel);
		deckDetailsPanel.setMagicDeck(deck);
		
//////////////////////////////////////////////////////////////////ACTIONS		
		btnNewDeck.addActionListener(newDeckEvent -> {
			var newDeck = new MagicDeck();
			setDeck(newDeck);
		});
		
		
		btnRandom.addActionListener(al->{
			
			
				buzyLabel.start();
				SwingWorker<MagicDeck, Void> sw = new SwingWorker<>()
				{

					@Override
					protected MagicDeck doInBackground() throws Exception {
						return deckManager.generateRandomDeck();
					}

					@Override
					protected void done() {
						try {
							deck = get();
							deckDetailsPanel.setMagicDeck(deck);
							deckmodel.init(deck);
							deckSidemodel.init(deck);
							setDeck(deck);
							updatePanels();
						}catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							MTGControler.getInstance().notify(e);
						}
						catch (Exception e) {
							MTGControler.getInstance().notify(e);
						}
						buzyLabel.end();
					
					}
						
					
				};
				
				ThreadManager.getInstance().runInEdt(sw, "random deck");

			
			
		});

		
		btnOpen.addActionListener(openEvent -> {
			try {
				var choose = new JDeckChooserDialog();
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
									.notify(new MTGNotification(capitalize(FINISHED),
											capitalize(UPDATED_DECK),
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
									updateM.put(getEnabledPlugin(MTGCardsProvider.class).searchCardByName(mc.getName(), mc.getCurrentSet(), true).get(0),deck.getMain().get(mc));
									publish(mc);
								} catch (Exception e) {
									logger.error("error update " + mc,e);
								}
							}
							for (MagicCard mc : deck.getSideBoard().keySet()) {
								try {
									updateS.put(getEnabledPlugin(MTGCardsProvider.class).searchCardByName(mc.getName(), mc.getCurrentSet(), true).get(0),deck.getSideBoard().get(mc));
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
			
			
			if(deck==null)
			{
				MTGControler.getInstance().notify(new NullPointerException("Deck is Null"));
				return;
			}
			
			
			buzyLabel.start();
			
			String dname = deck.getName();
			if(deck.getId()<0 && dname.isEmpty())
			{
				String name = JOptionPane.showInputDialog(capitalize("DECK_NAME") + " ?", dname);
				if(name!=null && !name.isEmpty())
					deck.setName(name);
			
			}
			
			
			var sw = new SwingWorker<Void, Void>(){

				@Override
				protected Void doInBackground() throws Exception {
					logger.debug("saving " + deck);
					deckManager.saveDeck(deck);
					p.setDeck(deck);
					return null;
				}

				@Override
				protected void done() {
					buzyLabel.end();
					
					try {
						get();
					}
					catch(InterruptedException ex)
					{
						Thread.currentThread().interrupt();
					}catch(Exception ex) {
						
						logger.error("error saving", ex);
						MTGControler.getInstance().notify(ex);
					}
					
				}
				
				
				
				
			};
		
			ThreadManager.getInstance().runInEdt(sw, "saving deck");

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
				stockDetailPanel.initMagicCardStock(mc,null);
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
			var menu = new JPopupMenu();
			for (final MTGCardsExport exp : listEnabledPlugins(MTGCardsExport.class)) 
			{
				if (exp.getMods() == MODS.BOTH || exp.getMods() == MODS.IMPORT) {
					
					var it = new JMenuItem(exp.getName(),exp.getIcon());
					it.addActionListener(itEvent -> {
						
						var jf = new JFileChooser(MTGConstants.DATA_DIR);
						jf.setFileFilter(new FileFilter() {
							@Override
							public String getDescription() {
								return exp.getName() +" ("+exp.getFileExtension()+")" ;
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
							var importWork = new DeckImportWorker(exp, buzyLabel, f) {
								@Override
								protected void done() {
									super.done();
									try {
										setDeck(get());
										updatePanels();
									} catch (InterruptedException e) {
										Thread.currentThread().interrupt();
										MTGControler.getInstance().notify(e);
									}catch (Exception e) {
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
			var point = b.getLocationOnScreen();
			menu.show(b, 0, 0);
			menu.setLocation(point.x, point.y + b.getHeight());

		});
		
		
		btnExports.initCardsExport(new Callable<MagicDeck>() {
			@Override
			public MagicDeck call() throws Exception {
				return deck;
			}
		}, buzyLabel);
				
		defaultEnterButton.addActionListener(aeSearch -> {
			
			resultListModel.clear();
			
			AbstractObservableWorker<List<MagicCard>,MagicCard,MTGCardsProvider> sw = new AbstractObservableWorker<>(buzy,getEnabledPlugin(MTGCardsProvider.class))
			{
				@Override
				protected List<MagicCard> doInBackground() throws Exception {
						return plug.searchCardByCriteria(searchComponent.getMTGCriteria().getAtt(), plug.getMTGQueryManager().getValueFor(searchComponent.getMTGCriteria().getFirst()).toString(), null, false);
				}

				@Override
				protected void process(List<MagicCard> chunks) {
					super.process(chunks);
					var form = new MagicFormat();
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

	private void initTables(JXTable table, BOARD f, DeckCardsTableModel model) {
		table.setModel(model);
		table.setRowSorter(new TableRowSorter<>(model));
		table.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());
		table.getColumnModel().getColumn(3).setCellRenderer(new MagicEditionsComboBoxCellRenderer());
		table.getColumnModel().getColumn(3).setCellEditor(new MagicEditionsComboBoxCellEditor());
		table.getColumnModel().getColumn(4).setCellEditor(new NumberCellEditorRenderer());

		
		table.getColumnModel().getColumn(0).setCellRenderer((JTable table2, Object value, boolean isSelected, boolean hasFocus,int row, int column)-> {

			JLabel comp = (JLabel)new DefaultTableCellRenderer().getTableCellRendererComponent(table2, value, isSelected, hasFocus, row, column);
			comp.setText(((MagicCard)value).getName());

			try {
				if(deck!=null && value!=null && deck.getCommander()!=null && ((MagicCard)value).getName().equals(deck.getCommander().getName()))
					comp.setFont(MTGControler.getInstance().getFont().deriveFont(Font.BOLD));
				}
				catch(Exception e){
					logger.error("error applying font " + value + " "+ deck +":"+e);
				}
			
			
			if(((MagicCard)value).isCompanion())
				comp.setFont(comp.getFont().deriveFont(Font.ITALIC));
			
			
			
			return comp;
			
			
		});
		
		
		
		table.getSelectionModel().addListSelectionListener(event -> {
			if (!event.getValueIsAdjusting()) {
				try {
					MagicCard mc = UITools.getTableSelection(table, 0);
					magicCardDetailPanel.setMagicCard(mc);
					comboPanel.init(mc);
					stockDetailPanel.initMagicCardStock(mc,null);
					rulesPanel.init(mc);
					if(f==BOARD.MAIN)
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
				
				if(f==BOARD.MAIN)
					cardDrawProbaPanel.init(deck, mc);
				
				if(SwingUtilities.isRightMouseButton(ev))
				{
					var menu = new JPopupMenu();
	
					var itemDel = new JMenuItem(capitalize("DELETE"));
					menu.add(itemDel);
					itemDel.addActionListener(ae->{
						
						deck.delete(mc);
						model.fireTableDataChanged();
						
					});
					
					
					if(mc.isLegendary() && (mc.isCreature()||mc.isPlaneswalker())) {
					
							if((deck.getCommander()!=null) && mc.getName().equals(deck.getCommander().getName()))
							{
								var itemRemoveCommander = new JMenuItem(capitalize("REMOVE_COMMANDER"));
								menu.add(itemRemoveCommander);
								itemRemoveCommander.addActionListener(ae->deck.setCommander(null));	
							}
							else
							{
								var itemSelCommander = new JMenuItem(capitalize("SELECT_COMMANDER"));
								menu.add(itemSelCommander);
								itemSelCommander.addActionListener(ae->deck.setCommander(mc));
							}
					}
					
					var itemMove = new JMenuItem(capitalize("MOVE_CARD_TO") + ((f==BOARD.MAIN)? " Side":" Main"));
					menu.add(itemMove);
					
					
					itemMove.addActionListener(ae->{
						if(f==BOARD.MAIN)
						{
							List<MagicCard> list = UITools.getTableSelections(tableDeck, 0);
							
							for(MagicCard card : list) {
								int qty = deck.getMain().get(card);
								deck.getSideBoard().put(card, qty);
								deck.getMain().remove(card);
							}
						}
						else
						{
							
							List<MagicCard> list = UITools.getTableSelections(tableSide, 0);
							
							for(MagicCard card : list) {
								int qty = deck.getSideBoard().get(card);
								deck.getMain().put(card, qty);
								deck.getSideBoard().remove(card);
							}
						}
						model.fireTableDataChanged();
						
					});
					
					
					var item = new JMenuItem(capitalize("MORE_LIKE_THIS"));
					menu.add(item);
					item.addActionListener(ae->{
						
						resultListModel.removeAllElements();
						try {
							for(MagicCard card : getEnabledPlugin(MTGCardsIndexer.class).similarity(mc).keySet())
								resultListModel.addElement(card);
						
							lblCards.setText(resultListModel.size() + " " + MTGControler.getInstance().getLangService().get("RESULTS"));
							listResult.setModel(resultListModel);
							listResult.updateUI();
							
						} catch (IOException e) {
							logger.error(e);
						}
					});
					var point = ev.getPoint();
					menu.show(table, (int) point.getX(), (int) point.getY());
					
				}
				
				
			}
		});
		
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				MagicCard mc = UITools.getTableSelection(table, 0);
				int sel = table.getSelectedRow();
				if (e.getKeyChar() == '+') {
					if(f==BOARD.MAIN)
						deck.add(mc);
					else
						deck.addSide(mc);
					
					deckmodel.init(deck);
				}
				
				if (e.getKeyChar() == '-') {
					if(f==BOARD.MAIN)
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
		cmcChartPanel.init(deck.getMainAsList());
		typeRepartitionPanel.init(deck.getMainAsList());
		manaRepartitionPanel.init(deck.getMainAsList());
		rarityRepartitionPanel.init(deck.getMainAsList());
		deckPricePanel.initDeck(deck);
		drawProbabilityPanel.init(deck);
		btnExports.setEnabled(!deck.getMainAsList().isEmpty());
		
		
	}

	@Override
	public String getTitle() {
		return "Construct";
	}
}

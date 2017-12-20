package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.DefaultListModel;
import javax.swing.DefaultRowSorter;
import javax.swing.ImageIcon;
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
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.exports.impl.MTGDesktopCompanionExport;
import org.magic.api.interfaces.CardExporter;
import org.magic.game.gui.components.HandPanel;
import org.magic.game.model.Player;
import org.magic.gui.components.DeckDetailsPanel;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.gui.components.ManaPanel;
import org.magic.gui.components.charts.CmcChartPanel;
import org.magic.gui.components.charts.ManaRepartitionPanel;
import org.magic.gui.components.charts.RarityRepartitionPanel;
import org.magic.gui.components.charts.TypeRepartitionPanel;
import org.magic.gui.components.dialog.DeckSnifferDialog;
import org.magic.gui.components.dialog.JDeckChooserDialog;
import org.magic.gui.components.dialog.ManualImportFrame;
import org.magic.gui.models.DeckModel;
import org.magic.gui.renderer.MagicCardListRenderer;
import org.magic.gui.renderer.MagicDeckQtyEditor;
import org.magic.gui.renderer.MagicEditionEditor;
import org.magic.gui.renderer.MagicEditionRenderer;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;

public class DeckBuilderGUI extends JPanel {

	private DeckDetailsPanel deckDetailsPanel;
	private CmcChartPanel cmcChartPanel;
	private ManaRepartitionPanel manaRepartitionPanel;
	private TypeRepartitionPanel typeRepartitionPanel;
	private RarityRepartitionPanel rarityRepartitionPanel;
	private MagicCardDetailPanel magicCardDetailPanel;
	private HandPanel thumbnail;

	private JTextField txtSearch;

	private JComboBox<String> cboAttributs;

	private JScrollPane scrollResult;

	private DeckModel deckSidemodel;
	private DeckModel deckmodel;
	private JButton btnSearch;
	private JButton btnExports;
	private JButton btnUpdate;
	private MagicDeck deck;

	private DefaultListModel<MagicCard> resultListModel = new DefaultListModel<MagicCard>();

	private JTable tableDeck;
	private JTable tableSide;
	private JList<MagicCard> listResult;
	private JTabbedPane tabbedPane;
	private JLabel lblExport = new JLabel("");
	private ButtonGroup groupsFilterResult;

	public static final int MAIN = 0;
	public static final int SIDE = 1;

	protected int selectedIndex = 0;

	private File exportedFile;

	Logger logger = MTGLogger.getLogger(this.getClass());

	File deckDirectory = new File(MTGControler.CONF_DIR, "decks");
	private Player p;

	public void loading(boolean show, String text) {
		lblExport.setText(text);
		lblExport.setVisible(show);
	}

	public DeckBuilderGUI() {
		logger.info("init DeckBuilder GUI");
		deck = new MagicDeck();
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

		lblExport.setIcon(new ImageIcon(MagicGUI.class.getResource("/res/load.gif")));
		lblExport.setVisible(false);

		setLayout(new BorderLayout(0, 0));
		deckmodel = new DeckModel(DeckModel.TYPE.DECK);
		deckSidemodel = new DeckModel(DeckModel.TYPE.SIDE);
		deckDetailsPanel = new DeckDetailsPanel();

		thumbnail = new HandPanel();
		thumbnail.setThumbnailSize(new Dimension(223, 311));
		thumbnail.enableDragging(false);
		thumbnail.setMaxCardsRow(4);

		FlowLayout flowLayout = (FlowLayout) panneauHaut.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(panneauHaut, BorderLayout.NORTH);

		cboAttributs = new JComboBox(MTGControler.getInstance().getEnabledProviders().getQueryableAttributs());
		panneauHaut.add(cboAttributs);

		txtSearch = new JTextField();
		panneauHaut.add(txtSearch);
		txtSearch.setColumns(25);

		btnSearch = new JButton(new ImageIcon(DeckBuilderGUI.class.getResource("/res/search.png")));
		panneauHaut.add(btnSearch);

		final JLabel lblCards = new JLabel("");
		panneauHaut.add(lblCards);

		JButton btnNewDeck = new JButton(new ImageIcon(DeckBuilderGUI.class.getResource("/res/new.png")));
		btnNewDeck.setToolTipText("Create New Deck");

		panneauHaut.add(btnNewDeck);

		btnNewDeck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				MagicDeck newDeck = new MagicDeck();
				setDeck(newDeck);
				deckmodel.init(newDeck);
				deckSidemodel.init(newDeck);
			}
		});

		JButton btnOpen = new JButton(new ImageIcon(DeckBuilderGUI.class.getResource("/res/open.png")));
		btnOpen.setToolTipText("Open deck");
		panneauHaut.add(btnOpen);

		btnOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
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
					JOptionPane.showMessageDialog(null, ex, "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		btnUpdate = new JButton("");
		btnUpdate.setToolTipText("Update the deck");
		btnUpdate.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				ThreadManager.getInstance().execute(new Runnable() {
					public void run() {
						
						Map<MagicCard,Integer> updateM = new HashMap<MagicCard,Integer>();
						Map<MagicCard,Integer> updateS = new HashMap<MagicCard,Integer>();
						
						btnUpdate.setEnabled(false);
						lblExport.setVisible(true);
						for (MagicCard mc : deck.getMap().keySet()) {
							try {
								updateM.put(MTGControler.getInstance().getEnabledProviders().getCardById(mc.getId()),deck.getMap().get(mc));
							} catch (Exception e) {
								logger.error(e);
								btnUpdate.setEnabled(true);
								lblExport.setVisible(false);
							}
						}
						for (MagicCard mc : deck.getMapSideBoard().keySet()) {
							try {
								updateS.put(
										MTGControler.getInstance().getEnabledProviders().getCardById(mc.getId()),
										deck.getMapSideBoard().get(mc));
							} catch (Exception e) {
								btnUpdate.setEnabled(true);
								lblExport.setVisible(false);
							}
						}
						
						deck.getMap().clear();
						deck.setMapDeck(updateM);
						
						deck.getMapSideBoard().clear();
						deck.setMapSideBoard(updateS);
						
						updatePanels();
						
						btnUpdate.setEnabled(true);
						lblExport.setVisible(false);
						JOptionPane.showMessageDialog(null, "Deck Updated", "Update", JOptionPane.INFORMATION_MESSAGE);
						
						
						
					}
				}, "Update Deck");

			}
		});
		btnUpdate.setIcon(new ImageIcon(DeckBuilderGUI.class.getResource("/res/refresh.png")));

		panneauHaut.add(btnUpdate);

		JButton btnSave = new JButton(new ImageIcon(DeckBuilderGUI.class.getResource("/res/save.png")));
		btnSave.setToolTipText("Save deck");
		panneauHaut.add(btnSave);

		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String name = JOptionPane.showInputDialog("Deck Name ?", deck.getName());
					deck.setName(name);

					if (!deckDirectory.exists())
						deckDirectory.mkdir();

					MTGDesktopCompanionExport serialis = new MTGDesktopCompanionExport();
					serialis.export(deck, new File(deckDirectory + "/" + name + serialis.getFileExtension()));

				} catch (Exception ex) {
					ex.printStackTrace();
					logger.error(ex);
					JOptionPane.showMessageDialog(null, ex, "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		JButton btnImport = new JButton(new ImageIcon(DeckBuilderGUI.class.getResource("/res/import.png")));
		btnImport.setToolTipText("Import deck as ");

		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JPopupMenu menu = new JPopupMenu();

				JMenuItem manuel = new JMenuItem("Manual Import");
				manuel.setIcon(new ImageIcon(DeckBuilderGUI.class.getResource("/res/manual.png")));
				manuel.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						ManualImportFrame fimport = new ManualImportFrame();
						fimport.setVisible(true);

						if (!fimport.getStringDeck().equals(""))
							importDeckFromString(fimport.getStringDeck());
					}
				});
				menu.add(manuel);

				JMenuItem webSite = new JMenuItem("Import from website");
				webSite.setIcon(new ImageIcon(DeckBuilderGUI.class.getResource("/res/website.png")));
				webSite.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						DeckSnifferDialog diag = new DeckSnifferDialog();
						diag.setModal(true);
						diag.setVisible(true);

						if (diag.getSelectedDeck() != null) {
							deckmodel.init(diag.getSelectedDeck());
							deckSidemodel.init(diag.getSelectedDeck());
							setDeck(diag.getSelectedDeck());
							updatePanels();
							deckmodel.fireTableDataChanged();
							deckSidemodel.fireTableDataChanged();
						}
					}
				});
				menu.add(webSite);

				for (final CardExporter exp : MTGControler.getInstance().getEnabledDeckExports()) {
					JMenuItem it = new JMenuItem();
					it.setIcon(exp.getIcon());
					it.setText(exp.getName());
					it.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							JFileChooser jf = new JFileChooser(".");
							jf.setFileFilter(new FileFilter() {

								@Override
								public String getDescription() {
									return exp.getName();
								}

								@Override
								public boolean accept(File f) {
									if (f.isDirectory())
										return true;

									if (f.getName().endsWith(exp.getFileExtension()))
										return true;

									return false;
								}
							});
							int res = jf.showOpenDialog(null);
							final File f = jf.getSelectedFile();

							if (res == JFileChooser.APPROVE_OPTION)
								ThreadManager.getInstance().execute(new Runnable() {

									@Override
									public void run() {
										try {
											loading(true, "loading from " + exp);
											deck = exp.importDeck(f);
											JOptionPane.showMessageDialog(null, "Import Finished",
													exp.getName() + " Finished", JOptionPane.INFORMATION_MESSAGE);
											setDeck(deck);
											loading(false, "");
											deckmodel.init(deck);
											deckSidemodel.init(deck);
											setDeck(deck);
											updatePanels();

										} catch (Exception e) {
											logger.error(e);
											e.printStackTrace();
											loading(false, "");
											JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
										}

									}
								}, "import " + exp);
						}
					});

					menu.add(it);
				}

				Component b = (Component) ae.getSource();
				Point p = b.getLocationOnScreen();
				menu.show(b, 0, 0);
				menu.setLocation(p.x, p.y + b.getHeight());
			}
		});

		panneauHaut.add(btnImport);

		btnExports = new JButton();
		btnExports.setEnabled(false);
		btnExports.setToolTipText("Export as");
		btnExports.setIcon(new ImageIcon(DeckBuilderGUI.class.getResource("/res/export.png")));
		btnExports.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JPopupMenu menu = new JPopupMenu();

				for (final CardExporter exp : MTGControler.getInstance().getEnabledDeckExports()) {
					JMenuItem it = new JMenuItem();
					it.setIcon(exp.getIcon());
					it.setText(exp.getName());
					it.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							JFileChooser jf = new JFileChooser(".");
							jf.setSelectedFile(new File(deck.getName() + exp.getFileExtension()));
							jf.showSaveDialog(null);
							exportedFile = jf.getSelectedFile();
							ThreadManager.getInstance().execute(new Runnable() {

								@Override
								public void run() {
									try {
										loading(true, "Export " + deck + " to " + exp);
										exp.export(deck, exportedFile);
										JOptionPane.showMessageDialog(null, "Export Finished",
												exp.getName() + " Finished", JOptionPane.INFORMATION_MESSAGE);
										loading(false, "");
									} catch (Exception e) {
										logger.error(e);
										loading(false, "");
										JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
									}
								}
							}, "Export " + deck + " to " + exp.getName());

						}
					});

					menu.add(it);
				}

				Component b = (Component) ae.getSource();
				Point p = b.getLocationOnScreen();
				menu.show(b, 0, 0);
				menu.setLocation(p.x, p.y + b.getHeight());
			}
		});
		panneauHaut.add(btnExports);

		panneauHaut.add(lblExport);

		JPanel panneauBas = new JPanel();
		add(panneauBas, BorderLayout.SOUTH);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		add(tabbedPane, BorderLayout.CENTER);

		JSplitPane panneauDeck = new JSplitPane();
		panneauDeck.setOrientation(JSplitPane.VERTICAL_SPLIT);
		tabbedPane.addTab("Deck", null, panneauDeck, null);
		DefaultRowSorter sorterCards = new TableRowSorter<DefaultTableModel>(deckmodel);

		magicCardDetailPanel = new MagicCardDetailPanel();
		magicCardDetailPanel.setPreferredSize(new Dimension(0, 0));
		magicCardDetailPanel.enableThumbnail(true);
		panneauDeck.setRightComponent(magicCardDetailPanel);

		final JTabbedPane tabbedDeck_side = new JTabbedPane(JTabbedPane.BOTTOM);

		panneauDeck.setLeftComponent(tabbedDeck_side);

		JScrollPane scrollDeck = new JScrollPane();
		tabbedDeck_side.addTab("Main", null, scrollDeck, null);

		tableDeck = new JTable();
		scrollDeck.setViewportView(tableDeck);

		tableDeck.setModel(deckmodel);
		tableDeck.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());
		tableDeck.setRowHeight(ManaPanel.row_height);
		tableDeck.setRowSorter(sorterCards);

		JScrollPane scrollSideboard = new JScrollPane();
		tabbedDeck_side.addTab("SideBoard", null, scrollSideboard, null);

		tableSide = new JTable();
		tableSide.setModel(deckSidemodel);
		tableSide.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());

		scrollSideboard.setViewportView(tableSide);

		tableDeck.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent ev) {

				MagicCard mc = (MagicCard) tableDeck.getValueAt(tableDeck.getSelectedRow(), 0);
				magicCardDetailPanel.setMagicCard(mc);

			}
		});

		tableSide.addMouseListener(new MouseAdapter() {

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

		tableDeck.getModel().addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				updatePanels();
			}
		});

		tableSide.getModel().addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				updatePanels();
			}
		});

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

		tableDeck.getColumnModel().getColumn(3).setCellRenderer(new MagicEditionRenderer());
		tableDeck.getColumnModel().getColumn(3).setCellEditor(new MagicEditionEditor());

		tableSide.getColumnModel().getColumn(3).setCellRenderer(new MagicEditionRenderer());
		tableSide.getColumnModel().getColumn(3).setCellEditor(new MagicEditionEditor());

		tableDeck.getColumnModel().getColumn(4).setCellEditor(new MagicDeckQtyEditor());
		tableSide.getColumnModel().getColumn(4).setCellEditor(new MagicDeckQtyEditor());

		JPanel panelInfoDeck = new JPanel();
		tabbedPane.addTab("Info", null, panelInfoDeck, null);
		panelInfoDeck.setLayout(new BorderLayout(0, 0));

		panelInfoDeck.add(deckDetailsPanel, BorderLayout.NORTH);
		deckDetailsPanel.setMagicDeck(deck);

		cmcChartPanel = new CmcChartPanel();
		manaRepartitionPanel = new ManaRepartitionPanel();
		typeRepartitionPanel = new TypeRepartitionPanel();
		rarityRepartitionPanel = new RarityRepartitionPanel();

		JPanel randomHandPanel = new JPanel();
		JPanel statPanel = new JPanel();

		randomHandPanel.setLayout(new BorderLayout(0, 0));
		randomHandPanel.add(thumbnail, BorderLayout.CENTER);

		statPanel.setLayout(new GridLayout(2, 2, 0, 0));
		statPanel.add(manaRepartitionPanel);
		statPanel.add(typeRepartitionPanel);
		statPanel.add(rarityRepartitionPanel);
		statPanel.add(cmcChartPanel);

		tabbedPane.addTab("Stats", null, statPanel, null);
		tabbedPane.addTab("Sample Hand", null, randomHandPanel, null);

		JPanel panel = new JPanel();
		randomHandPanel.add(panel, BorderLayout.NORTH);

		JButton btnDrawAHand = new JButton("Draw a hand");
		btnDrawAHand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				thumbnail.removeAll();
				p.mixHandAndLibrary();
				p.shuffleLibrary();
				p.drawCard(7);
				thumbnail.initThumbnails(p.getHand(), false);
			}
		});
		panel.add(btnDrawAHand);

		JPanel panneauGauche = new JPanel();
		add(panneauGauche, BorderLayout.WEST);
		panneauGauche.setLayout(new BorderLayout(0, 0));

		scrollResult = new JScrollPane();
		panneauGauche.add(scrollResult);

		listResult = new JList(new DefaultListModel<MagicCard>());
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

			public void mouseClicked(MouseEvent ev) {

				if (ev.getClickCount() == 1 && !ev.isConsumed()) {
					ev.consume();
					MagicCard mc = (MagicCard) listResult.getSelectedValue();
					magicCardDetailPanel.setMagicCard(mc);

				}

				if (ev.getClickCount() == 2 && !ev.isConsumed()) {
					ev.consume();

					MagicCard mc = (MagicCard) listResult.getSelectedValue();

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

		txtSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSearch.doClick();

			}
		});

		tabbedDeck_side.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				selectedIndex = tabbedDeck_side.getSelectedIndex();
			}
		});

		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (txtSearch.getText().equals(""))
					return;

				resultListModel.removeAllElements();

				ThreadManager.getInstance().execute(new Runnable() {
					public void run() {
						try {
							//String searchName = URLEncoder.encode(txtSearch.getText(), "UTF-8");
							String searchName = txtSearch.getText();
							List<MagicCard> cards = MTGControler.getInstance().getEnabledProviders()
									.searchCardByCriteria(cboAttributs.getSelectedItem().toString(), searchName, null);
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
							lblCards.setText(resultListModel.size() + " results");
							listResult.setModel(resultListModel);
							listResult.updateUI();

						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, e.getMessage(), "ERREUR", JOptionPane.ERROR_MESSAGE);
						}
					}
				},"search deck");
			}
		});
	}

	protected void importDeckFromString(final String stringDeck) {
		ThreadManager.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				String[] line = stringDeck.split("\n");
				for (String l : line) {
					int nb = Integer.parseInt(l.substring(0, l.indexOf(" ")));
					String name = l.substring(l.indexOf(" "), l.length());
					// Scanner s = new Scanner(input).useDelimiter("\\s*\\s*");
					try {
						MagicCard mc;
						if (name.trim().equalsIgnoreCase("Plains") || name.trim().equalsIgnoreCase("Island")
								|| name.trim().equalsIgnoreCase("Swamp") || name.trim().equalsIgnoreCase("Mountain")
								|| name.trim().equalsIgnoreCase("Forest")) {
							MagicEdition ed = new MagicEdition();
							ed.setId(MTGControler.getInstance().get("default-land-deck"));
							mc = MTGControler.getInstance().getEnabledProviders()
									.searchCardByCriteria("name", name.trim(), ed).get(0);
						} else {
							mc = MTGControler.getInstance().getEnabledProviders()
									.searchCardByCriteria("name", name.trim(), null).get(0);
						}

						if (mc != null) {
							getSelectedMap().put(mc, nb);
							setDeck(deck);
							updatePanels();
							deckmodel.fireTableDataChanged();
							deckSidemodel.fireTableDataChanged();
						}
					} catch (Exception e) {
						logger.error(e);
					}

				}

			}
		}, "importDeckFromString");

	}

	//
	// private void filterResult(String format)
	// {
	// while(resultListModel.elements().hasMoreElements())
	// {
	// MagicCard mc = resultListModel.elements().nextElement();
	// MagicFormat form = new MagicFormat();
	// form.setFormat(groupsFilterResult.getSelection().getActionCommand());
	// if(mc.getLegalities().contains(form))
	// resultListModel.removeElement(mc);
	//
	// }
	// }

	public Map<MagicCard, Integer> getSelectedMap() {
		if (selectedIndex == 0)
			return deck.getMap();
		else
			return deck.getMapSideBoard();

	}

	protected void updatePanels() {

		deckDetailsPanel.setMagicDeck(deck);
		cmcChartPanel.init(deck);
		typeRepartitionPanel.init(deck);
		manaRepartitionPanel.init(deck);
		rarityRepartitionPanel.init(deck);
		btnExports.setEnabled(deck.getAsList().size() > 0);

	}
}

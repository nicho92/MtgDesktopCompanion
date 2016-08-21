package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

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
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.exports.impl.CSVExport;
import org.magic.api.exports.impl.MTGDesktopCompanionExport;
import org.magic.api.interfaces.CardExporter;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.api.interfaces.MagicDAO;
import org.magic.game.Player;
import org.magic.gui.components.DeckDetailsPanel;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.gui.components.ManaPanel;
import org.magic.gui.components.ManualImportFrame;
import org.magic.gui.components.charts.CmcChartPanel;
import org.magic.gui.components.charts.ManaRepartitionPanel;
import org.magic.gui.components.charts.RarityRepartitionPanel;
import org.magic.gui.components.charts.TypeRepartitionPanel;
import org.magic.gui.game.ThumbnailPanel;
import org.magic.gui.models.DeckModel;
import org.magic.gui.renderer.MagicCardListRenderer;
import org.magic.gui.renderer.MagicEditionEditor;
import org.magic.gui.renderer.MagicEditionRenderer;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MagicFactory;
import org.magic.services.ThreadManager;

public class DeckBuilderGUI extends JPanel{
	
	private DeckDetailsPanel deckDetailsPanel;
	private CmcChartPanel cmcChartPanel;
	private ManaRepartitionPanel manaRepartitionPanel;
	private TypeRepartitionPanel typeRepartitionPanel;
	private RarityRepartitionPanel rarityRepartitionPanel; 
	private MagicCardDetailPanel magicCardDetailPanel;
	private JTextField txtSearch;
	private JComboBox<String> cboAttributs;
	private JScrollPane scrollResult;
	protected int selectedIndex=0;
	private ThumbnailPanel thumbnail;
	private DeckModel deckSidemodel;
	private DeckModel deckmodel ;
	
	private JButton btnExports;
	
	private MagicCardsProvider provider;
	private MagicDeck deck;
	
	private DefaultListModel<MagicCard> resultListModel = new DefaultListModel<MagicCard>();

	private JTable tableDeck;
	private JTable tableSide;
	private JList<MagicCard> listResult;
	private JTabbedPane tabbedPane;
	
	public static final int MAIN=0;
	public static final int SIDE=1;
	
	static final Logger logger = LogManager.getLogger(DeckBuilderGUI.class.getName());

	File deckDirectory = new File(System.getProperty("user.home")+"/magicDeskCompanion/decks");
	private MagicDAO dao;
	private Player p;
	
	
	public MagicDeck getDeck() {
		return deck;
	}
	
	public DeckBuilderGUI(MagicCardsProvider provider,MagicDAO dao) {
		logger.debug("init Deck panel");
		
		deck = new MagicDeck();
		this.provider=provider;
		this.dao=dao;
		initGUI();
		setDeck(deck);
	}

	
	
	public void setDeck(MagicDeck deck)
	{
		this.deck=deck;
		deckDetailsPanel.setMagicDeck(deck);
		deckmodel.init(deck);
		p=new Player(deck);
	}
	
	private void initGUI() {
		
		setLayout(new BorderLayout(0, 0));
		deckmodel = new DeckModel(DeckModel.TYPE.DECK);
		deckSidemodel = new DeckModel(DeckModel.TYPE.SIDE);
		deckDetailsPanel = new DeckDetailsPanel();
		
		thumbnail = new ThumbnailPanel();
		thumbnail.setThumbnailSize(223, 311);
		thumbnail.enableDragging(false);
		JPanel panneauHaut = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panneauHaut.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(panneauHaut, BorderLayout.NORTH);
		
		cboAttributs = new JComboBox(provider.getQueryableAttributs());
		panneauHaut.add(cboAttributs);
		
		txtSearch = new JTextField();
		panneauHaut.add(txtSearch);
		txtSearch.setColumns(25);
		
		final JButton btnSearch = new JButton(new ImageIcon(DeckBuilderGUI.class.getResource("/res/search.png")));
		panneauHaut.add(btnSearch);
		
		final JLabel lblCards = new JLabel("");
		panneauHaut.add(lblCards);
		
		JButton btnNewDeck = new JButton(new ImageIcon(DeckBuilderGUI.class.getResource("/res/new.png")));
				btnNewDeck.setToolTipText("Create New Deck");
		
		panneauHaut.add(btnNewDeck);
		
		btnNewDeck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				MagicDeck newDeck  = new MagicDeck();
				setDeck(newDeck);
				deckmodel.load(newDeck);
				deckmodel.fireTableDataChanged();
				deckSidemodel.load(newDeck);
				deckSidemodel.fireTableDataChanged();
				
				//updatePanels();
			}
		});
		
		JButton btnOpen = new JButton(new ImageIcon(DeckBuilderGUI.class.getResource("/res/open.png")));
				btnOpen.setToolTipText("Open deck");
		panneauHaut.add(btnOpen);
		
		btnOpen.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					//TO DO use DAO for loading
					JFileChooser choose = new JFileChooser(deckDirectory);
					choose.showOpenDialog(null);
					
					File f = choose.getSelectedFile();
					
					deck = new MTGDesktopCompanionExport().importDeck(f);
					deckDetailsPanel.setMagicDeck(deck);
					deckmodel.load(deck);
					deckSidemodel.load(deck);
					deckmodel.fireTableDataChanged();
					deckSidemodel.fireTableDataChanged();
					setDeck(deck);
					updatePanels();
	
					
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, ex,"ERROR",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		JButton btnSave = new JButton(new ImageIcon(DeckBuilderGUI.class.getResource("/res/save.png")));
				btnSave.setToolTipText("Save deck");
		panneauHaut.add(btnSave);
		
		btnSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					String name = JOptionPane.showInputDialog("Deck Name ?",deck.getName());
					deck.setName(name);
					
					if(!deckDirectory.exists())
						deckDirectory.mkdir();
					
					MTGDesktopCompanionExport serialis = new MTGDesktopCompanionExport();
					
					serialis.export(deck, new File(deckDirectory+"/"+name+serialis.getFileExtension()));
					dao.saveDeck(deck);
					
				}
				catch(Exception ex)
				{
					JOptionPane.showMessageDialog(null, ex,"ERROR",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		JButton btnManualImport = new JButton(new ImageIcon(DeckBuilderGUI.class.getResource("/res/import.png")));
				btnManualImport.setToolTipText("Import deck manualy");
				
		btnManualImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ManualImportFrame fimport = new ManualImportFrame();
				fimport.setVisible(true);
				
				if(!fimport.getStringDeck().equals(""))
					importDeckFromString(fimport.getStringDeck());
			}
		});
		panneauHaut.add(btnManualImport);
		
		btnExports = new JButton();
		btnExports.setEnabled(false);
		btnExports.setToolTipText("Export as");
		btnExports.setIcon(new ImageIcon(DeckBuilderGUI.class.getResource("/res/export.png")));
		btnExports.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JPopupMenu menu = new JPopupMenu();
				
				for(final CardExporter exp : MagicFactory.getInstance().getEnabledDeckExports())
				{
					JMenuItem it = new JMenuItem();
					it.setIcon(exp.getIcon());
					it.setText(exp.getName());
					it.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							JFileChooser jf =new JFileChooser(".");
							jf.setSelectedFile(new File(getDeck().getName()+exp.getFileExtension()));
							jf.showSaveDialog(null);
							File f=jf.getSelectedFile();
							
							try {
								exp.export(getDeck(), f);
								JOptionPane.showMessageDialog(null, "Export Finished",exp.getName() + " Finished",JOptionPane.INFORMATION_MESSAGE);
							} catch (Exception e) {
								logger.error(e);
								JOptionPane.showMessageDialog(null, e,"Error",JOptionPane.ERROR_MESSAGE);
							}
						}
					});
					
					menu.add(it);
				}
				
				Component b=(Component)ae.getSource();
		        Point p=b.getLocationOnScreen();
		        menu.show(b,0,0);
		        menu.setLocation(p.x,p.y+b.getHeight());
			}
		});
		panneauHaut.add(btnExports);
		
		scrollResult = new JScrollPane();
		add(scrollResult, BorderLayout.WEST);
		
		listResult = new JList(new DefaultListModel<MagicCard>());
		listResult.setCellRenderer(new MagicCardListRenderer());  
		listResult.setMinimumSize(new Dimension(100, 0));
		listResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollResult.setViewportView(listResult);
		
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
				
				MagicCard mc = (MagicCard)tableDeck.getValueAt(tableDeck.getSelectedRow(),0);
				magicCardDetailPanel.setMagicCard(mc);
				
			}
		});
		
		tableSide.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent ev) {
				
				MagicCard mc = (MagicCard)tableSide.getValueAt(tableSide.getSelectedRow(),0);
				magicCardDetailPanel.setMagicCard(mc);
				
			}
		});
		
		tableDeck.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				MagicCard mc = (MagicCard)tableDeck.getValueAt(tableDeck.getSelectedRow(),0);
				if(e.getKeyCode() == 0)
				{
					deck.getMap().remove(mc);
					deckmodel.init(deck);
					deckmodel.fireTableDataChanged();
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
	
		randomHandPanel.setLayout(new BorderLayout(0, 0));

		
		randomHandPanel.add(thumbnail, BorderLayout.CENTER);
		
		tabbedPane.addTab("Cmc", null, cmcChartPanel, null);
		tabbedPane.addTab("Mana", null, manaRepartitionPanel, null);
		tabbedPane.addTab("Types", null, typeRepartitionPanel, null);
		tabbedPane.addTab("Rarity", null, rarityRepartitionPanel, null);
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
				thumbnail.initThumbnails(p.getHand());
			}
		});
		panel.add(btnDrawAHand);
		
		listResult.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent ev) {
				
				if(ev.getClickCount()==1 && !ev.isConsumed())
				{
					ev.consume();
					MagicCard mc = (MagicCard)listResult.getSelectedValue();
					magicCardDetailPanel.setMagicCard(mc);
					
				}
				
				
				if(ev.getClickCount()==2 && !ev.isConsumed())
				{
					ev.consume();
					
					MagicCard mc = (MagicCard)listResult.getSelectedValue();
					
					if(getSelectedMap().get(mc)!=null)
					{
						getSelectedMap().put(mc, deck.getMap().get(mc)+1);
					}
					else
					{	
						getSelectedMap().put(mc, 1);
					}
					deckmodel.init(deck);
					deckSidemodel.init(deck);
					deckSidemodel.fireTableDataChanged();
					deckmodel.fireTableDataChanged();
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
		
			if(txtSearch.getText().equals(""))
				return;
			
			resultListModel.removeAllElements();
			
			ThreadManager.getInstance().execute(new Runnable() {
				public void run() {
					try {
						String searchName=URLEncoder.encode(txtSearch.getText(),"UTF-8");
						List<MagicCard> cards = provider.searchCardByCriteria(cboAttributs.getSelectedItem().toString(),searchName,null);
						
						
						for(MagicCard m : cards)
							resultListModel.addElement(m);
					
						listResult.setModel(resultListModel);
						
						lblCards.setText(cards.size() +" results");
						
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, e.getMessage(),"ERREUR",JOptionPane.ERROR_MESSAGE);
					}
				}
			},"DeckSearchCards");
		
		}
	});
	}
	
	

	protected void importDeckFromString(final String stringDeck) {
		ThreadManager.getInstance().execute(new Runnable() {
			
			@Override
			public void run() {
				String[] line = stringDeck.split("\n");
				for(String l : line)
				{
					int nb = Integer.parseInt(l.substring(0,l.indexOf(" ")));
					String name = l.substring(l.indexOf(" "),l.length()); 
					//Scanner s = new Scanner(input).useDelimiter("\\s*\\s*");
					try {
						MagicCard mc = provider.searchCardByCriteria("name", name.trim(),null).get(0);
						
						if(mc!=null)
							{
							getSelectedMap().put(mc, nb);
							setDeck(deck);
							updatePanels();
							deckmodel.fireTableDataChanged();
							deckSidemodel.fireTableDataChanged();
							}
					} catch (Exception e) {
						
					}
					
					
				}
				
				
			}
		},"importDeckFromString");
		
		
	}

	
	public Map<MagicCard,Integer> getSelectedMap()
	{
		if(selectedIndex==0)
			return deck.getMap();
		else
			return deck.getMapSideBoard();
		
	}

	protected void updatePanels() {

		deckDetailsPanel.setMagicDeck(deck);
		cmcChartPanel.init(deck);
		typeRepartitionPanel.init(deck);
		manaRepartitionPanel.init(deck);;
		rarityRepartitionPanel.init(deck);
		btnExports.setEnabled(true);
		
	}	
}


package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.DefaultRowSorter;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.gui.components.charts.CmcChartPanel;
import org.magic.gui.components.charts.ManaRepartitionPanel;
import org.magic.gui.components.charts.RarityRepartitionPanel;
import org.magic.gui.components.charts.TypeRepartitionPanel;
import org.magic.gui.models.DeckModel;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.tools.MagicSerializer;

public class DeckBuilderGUI extends JPanel{
	private JTable tableDeck;
	private JTextField txtSearch;

	MagicCardsProvider provider;
	MagicDeck deck;
	DeckModel deckmodel ;
	DeckModel sideboardmodel ;
	JComboBox cboAttributs;
	DefaultListModel<MagicCard> resultListModel = new DefaultListModel<MagicCard>();
	ThumbnailPanel thumbnailPanel;
	
	
	static final Logger logger = LogManager.getLogger(DeckBuilderGUI.class.getName());

	File deckDirectory = new File(System.getProperty("user.home")+"/magicDeskCompanion/decks");
	
	CmcChartPanel cmcChartPanel;
	ManaRepartitionPanel manaRepartitionPanel;
	TypeRepartitionPanel typeRepartitionPanel;
	RarityRepartitionPanel rarityRepartitionPanel; 
	
	
	private DeckDetailsPanel deckDetailsPanel;
	protected Object String;
	private JTable tableSide;
	
	
	
	public DeckBuilderGUI(MagicCardsProvider provider) {
		
		deck = new MagicDeck();
		this.provider=provider;
		
		initGUI();

	}

	
	public void setDeck(MagicDeck deck)
	{
		this.deck=deck;
		deckDetailsPanel.setMagicDeck(deck);
		deckmodel.init(deck);
		
		
	}
	
	private void initGUI() {
		
		setLayout(new BorderLayout(0, 0));
		deckmodel = new DeckModel();
		sideboardmodel= new DeckModel();
		deckDetailsPanel = new DeckDetailsPanel();
		
		
		JPanel panneauHaut = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panneauHaut.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(panneauHaut, BorderLayout.NORTH);
		
		cboAttributs = new JComboBox(provider.getQueryableAttributs());
		panneauHaut.add(cboAttributs);
		
		txtSearch = new JTextField();
		panneauHaut.add(txtSearch);
		txtSearch.setColumns(25);
		
		final JButton btnSearch = new JButton("Search");
		panneauHaut.add(btnSearch);
		
		final JLabel lblCards = new JLabel("");
		panneauHaut.add(lblCards);
		
		JButton btnNewDeck = new JButton("New Deck");
		panneauHaut.add(btnNewDeck);
		
		btnNewDeck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				MagicDeck newDeck  = new MagicDeck();
				setDeck(newDeck);
				deckmodel.load(newDeck);
				deckmodel.fireTableDataChanged();
				//updatePanels();
			}
		});
		
		JButton btnOpen = new JButton("Open");
		panneauHaut.add(btnOpen);
		
		btnOpen.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					
					JFileChooser choose = new JFileChooser(deckDirectory);
					choose.showOpenDialog(null);
					
					File f = choose.getSelectedFile();
					
					deck = MagicSerializer.read(f,MagicDeck.class);
					deckDetailsPanel.setMagicDeck(deck);
					deckmodel.load(deck);
					deckmodel.fireTableDataChanged();
					updatePanels();
	
					
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
		
		JButton btnSave = new JButton("Save");
		panneauHaut.add(btnSave);
		
		btnSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					String name = JOptionPane.showInputDialog("Deck Name ?",deck.getName());
					deck.setName(name);
					
					if(!deckDirectory.exists())
						deckDirectory.mkdir();
					
					
					MagicSerializer.serialize(deck, deckDirectory+"/"+name+".deck");
					
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
		
		JSeparator separator = new JSeparator();
		panneauHaut.add(separator);
		
		JButton btnWebImport = new JButton("Web Import");
		
		panneauHaut.add(btnWebImport);
		
		JButton btnManualImport = new JButton("Manual Import");
		btnManualImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ManualImportFrame fimport = new ManualImportFrame();
				fimport.setVisible(true);
				
				if(!fimport.getStringDeck().equals(""))
					importDeckFromString(fimport.getStringDeck());
			}
		});
		panneauHaut.add(btnManualImport);
		
		JScrollPane scrollResult = new JScrollPane();
		add(scrollResult, BorderLayout.WEST);
		
		final JList listResult = new JList(new DefaultListModel<MagicCard>());
		listResult.setMinimumSize(new Dimension(100, 0));
		listResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollResult.setViewportView(listResult);
		
		JPanel panneauBas = new JPanel();
		add(panneauBas, BorderLayout.SOUTH);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.RIGHT);
		add(tabbedPane, BorderLayout.CENTER);
		
		JSplitPane panneauDeck = new JSplitPane();
		panneauDeck.setOrientation(JSplitPane.VERTICAL_SPLIT);
		tabbedPane.addTab("Deck", null, panneauDeck, null);
		DefaultRowSorter sorterCards = new TableRowSorter<DefaultTableModel>(deckmodel);
		
		
		final MagicCardDetailPanel magicCardDetailPanel = new MagicCardDetailPanel();
		magicCardDetailPanel.enableThumbnail(true);
		panneauDeck.setRightComponent(magicCardDetailPanel);
		
		JTabbedPane tabbedDeck_side = new JTabbedPane(JTabbedPane.BOTTOM);
		panneauDeck.setLeftComponent(tabbedDeck_side);
		
		JScrollPane scrollDeck = new JScrollPane();
		tabbedDeck_side.addTab("Main", null, scrollDeck, null);
		
		tableDeck = new JTable();
		scrollDeck.setViewportView(tableDeck);
		tableDeck.setModel(deckmodel);
		tableDeck.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());
		tableDeck.setRowHeight(ManaPanel.pix_resize);
		tableDeck.setRowSorter(sorterCards);
		
		JScrollPane scrollSideboard = new JScrollPane();
		tabbedDeck_side.addTab("SideBoard", null, scrollSideboard, null);
		
		tableSide = new JTable();
		tableSide.setModel(sideboardmodel);
		scrollSideboard.setViewportView(tableSide);
		
		tableDeck.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent ev) {
				
				MagicCard mc = (MagicCard)tableDeck.getValueAt(tableDeck.getSelectedRow(),0);
				magicCardDetailPanel.setMagicCard(mc);
				
			}
		});
		
		
		tableDeck.getModel().addTableModelListener(new TableModelListener() {
		      public void tableChanged(TableModelEvent e) {
		    	  updatePanels();
		      }
		    });
		
		tableDeck.getDefaultEditor(Object.class).addCellEditorListener(new CellEditorListener() {
			
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
		
		thumbnailPanel = new ThumbnailPanel();
		thumbnailPanel.setThumbnailSize(224, 300);
		thumbnailPanel.setRupture(4);
		randomHandPanel.add(thumbnailPanel, BorderLayout.CENTER);
		
		JPanel panneauDraw = new JPanel();
		randomHandPanel.add(panneauDraw, BorderLayout.NORTH);
		
		JButton btnDrawHand = new JButton("Draw a hand");
		panneauDraw.add(btnDrawHand);
		
		JButton btnDrawCard = new JButton("Draw a card");
		
		panneauDraw.add(btnDrawCard);
		
		tabbedPane.addTab("Cmc", null, cmcChartPanel, null);
		tabbedPane.addTab("Mana", null, manaRepartitionPanel, null);
		tabbedPane.addTab("Types", null, typeRepartitionPanel, null);
		tabbedPane.addTab("Rarity", null, rarityRepartitionPanel, null);
		tabbedPane.addTab("Random Hand", null, randomHandPanel, null);
	
		
		
		btnDrawHand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				List<MagicCard> d = deck.getAsList();
				Collections.shuffle(d);
			    thumbnailPanel.initThumbnails(d.subList(0, 7));
			}
		});
		
		btnDrawCard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<MagicCard> d = deck.getAsList();
				Collections.shuffle(d);
			    thumbnailPanel.initThumbnails(d.subList(0, 1));
			}
		});
		
		
		btnWebImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		
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
					
					if(deck.getMap().get(mc)!=null)
					{
						deck.getMap().put(mc, deck.getMap().get(mc)+1);
					}
					else
					{	
						deck.getMap().put(mc, 1);
					}
					deckmodel.init(deck);
					deckmodel.fireTableDataChanged();
				}
			}
		});
		
		txtSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSearch.doClick();
				
			}
		});
		
		
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
		
			if(txtSearch.getText().equals(""))
				return;
			
			resultListModel.removeAllElements();
			
			Thread tsearch = new Thread(new Runnable() {
				public void run() {
					try {
						String searchName=URLEncoder.encode(txtSearch.getText(),"UTF-8");
						List<MagicCard> cards = provider.searchCardByCriteria(cboAttributs.getSelectedItem().toString(),searchName);
						
						
						for(MagicCard m : cards)
							resultListModel.addElement(m);
					
						listResult.setModel(resultListModel);
						
						lblCards.setText(cards.size() +" results");
						
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, e.getMessage(),"ERREUR",JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		
			tsearch.start();
		}
	});
	}
	
	

	protected void importDeckFromString(final String stringDeck) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String[] line = stringDeck.split("\n");
				for(String l : line)
				{
					int nb = Integer.parseInt(l.substring(0,l.indexOf(" ")));
					String name = l.substring(l.indexOf(" "),l.length()); 
					//Scanner s = new Scanner(input).useDelimiter("\\s*\\s*");
					try {
						MagicCard mc = provider.searchCardByCriteria("name", name.trim()).get(0);
						
						if(mc!=null)
							{
							deck.getMap().put(mc, nb);
							setDeck(deck);
							updatePanels();
							deckmodel.fireTableDataChanged();
							}
					} catch (Exception e) {
						
					}
					
					
				}
				
				
			}
		}).start();;
		
		
	}


	protected void updatePanels() {

		deckDetailsPanel.setMagicDeck(deck);
		cmcChartPanel.init(deck);
		typeRepartitionPanel.init(deck);
		manaRepartitionPanel.init(deck);;
		rarityRepartitionPanel.init(deck);
		
	}	
}

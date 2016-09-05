package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultRowSorter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicRuling;
import org.magic.api.interfaces.CardExporter;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.api.interfaces.MagicDAO;
import org.magic.api.pictures.impl.BoosterPicturesProvider;
import org.magic.gui.components.CardsPicPanel;
import org.magic.gui.components.JSONPanel;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.gui.components.MagicEditionDetailPanel;
import org.magic.gui.components.ManaPanel;
import org.magic.gui.components.charts.CmcChartPanel;
import org.magic.gui.components.charts.HistoryPricesPanel;
import org.magic.gui.components.charts.ManaRepartitionPanel;
import org.magic.gui.components.charts.RarityRepartitionPanel;
import org.magic.gui.components.charts.TypeRepartitionPanel;
import org.magic.gui.game.DisplayableCard;
import org.magic.gui.game.GamePanelGUI;
import org.magic.gui.game.ThumbnailPanel;
import org.magic.gui.models.CardsPriceTableModel;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MagicFactory;
import org.magic.services.ThreadManager;

import de.javasoft.plaf.synthetica.SyntheticaPlainLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaStandardLookAndFeel;
import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

public class MagicGUI extends JFrame {

	static final Logger logger = LogManager.getLogger(MagicGUI.class.getName());

	private static final int INDEX_PRICES = 2;

	TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(MagicGUI.class.getResource("/res/logo.gif")).getScaledInstance(16, 16, BufferedImage.SCALE_SMOOTH));
	public final SystemTray tray = SystemTray.getSystemTray();
	private MagicCard selected;
	private MagicEdition selectedEdition;
	private List<MagicCard> cards;
	private MagicDAO dao;
	private String defaultLanguage;
	private MagicCardsProvider provider;

	
	
	private CardsPriceTableModel priceModel;
	private MagicCardTableModel cardsModeltable;
	
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenu mnView;
	private JMenu mnuAbout;
	private JMenu mnuLang;
	private JMenuItem mntmExit;
	private JMenuItem mntmAboutMagicDesktop;
	private JMenuItem mntmReportBug;
	private JMenuItem mntmShowhideFilters;
    
	private JTabbedPane tabbedCardsView;
	private JTabbedPane tabbedCardsInfo ;
	private JTabbedPane tabbedPane;
	
	private JScrollPane scrollThumbnails;
	
	private ThumbnailPanel thumbnailPanel;
	private ManaRepartitionPanel manaRepartitionPanel;
	private TypeRepartitionPanel typeRepartitionPanel;
	private RarityRepartitionPanel rarityRepartitionPanel;
	private CmcChartPanel cmcChart;
	private CardsPicPanel cardsPicPanel;
	private HistoryPricesPanel historyChartPanel;
	private MagicEditionDetailPanel magicEditionDetailPanel;
	private MagicCardDetailPanel detailCardPanel;
	private JSONPanel jsonCardPanel;
	private JPanel boosterPanel;
	private JPanel panelResultsCards;
	private JPanel panelFilters;
    private JPanel panelmana;
	private JPanel globalPanel;
	private JPanel editionDetailPanel;
	private JPanel panneauHaut;
	private JPanel panneauCard = new JPanel();
  
	private JTextArea txtRulesArea;
	private JTextField txtFilter;
	private JTextField txtMagicSearch;
	
	private JPopupMenu popupMenu = new JPopupMenu();
    
	private JComboBox<MagicEdition> cboEdition;
    private JComboBox<MagicCardNames> cboLanguages;
	private JComboBox<String> cboQuereableItems;
	private JComboBox<MagicCollection> cboCollections;
	
    private DeckBuilderGUI deckBuilderGUI;
	private CardBuilderPanelGUI panneauBuilder;
	private CollectionPanelGUI collectionPanelGUI;
	private JXTable tableCards;
	private JXTable tablePrice;
    private DefaultRowSorter sorterCards ;
    private TableFilterHeader filterHeader;

    private JButton btnClear;
	private JButton btnGenerateBooster;
	private JButton btnSearch;
	private JButton btnExport;

	private JLabel lblLoading = new JLabel("");
	
	private JList<MagicEdition> listEdition;
	private JLabel lblBoosterPic;

	BoosterPicturesProvider boosterProvider;
	

	public void setDefaultLanguage(String language) {
		defaultLanguage=language;
		MagicFactory.getInstance().setProperty("langage", language);
		

	}

	public void loading(boolean show,String text)
	{
		lblLoading.setText(text);
		lblLoading.setVisible(show);
	}

	
	public void setSelectedTab(int id)
	{
		tabbedPane.setSelectedIndex(id);
	}

	public void setLookAndFeel(String lookAndFeel)
	{
		try {
			UIManager.put("Table.alternateRowColor", Color.decode("#E1E4F2"));
			UIManager.setLookAndFeel(lookAndFeel);
			MagicFactory.getInstance().setProperty("lookAndFeel", lookAndFeel);
			SwingUtilities.updateComponentTreeUI(this);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
	}

	public void initPopupCollection() throws Exception
	{
		JMenu menuItemAdd = new JMenu("Add");

		for(MagicCollection mc : dao.getCollections())
		{
			JMenuItem adds = new JMenuItem(mc.getName());
			adds.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					String collec = ((JMenuItem)e.getSource()).getText();
					loading(true, "add cards to " + collec); 

					for (int i = 0; i < tableCards.getSelectedRowCount(); i++) { 
						
						
						int viewRow = tableCards.getSelectedRows()[i];
						int modelRow = tableCards.convertRowIndexToModel(viewRow);
						
						
						MagicCard mc = (MagicCard)tableCards.getModel().getValueAt(modelRow, 0);
						try {
							dao.saveCard(mc, dao.getCollection(collec));
						} catch (SQLException e1) {
							logger.error(e1);
							JOptionPane.showMessageDialog(null, e1,"ERROR",JOptionPane.ERROR_MESSAGE);
						}

					}
					loading(false, "");
				}
			});
			menuItemAdd.add(adds);
		}

		popupMenu.add(menuItemAdd);
	}

	public void initGUI() throws Exception
	{
		logger.debug("construction of GUI");
		menuBar = new JMenuBar();
		mnFile = new JMenu("File");
		mntmExit = new JMenuItem("Exit");

		setSize(new Dimension(1420, 900));
		setTitle("Magic Desktop Companion ( v" + MagicFactory.getInstance().get("version")+")");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MagicGUI.class.getResource("/res/logo.gif")));
		setJMenuBar(menuBar);

		menuBar.add(mnFile);
		mnFile.add(mntmExit);

		panneauBuilder = new CardBuilderPanelGUI();
		
		mnView = new JMenu("View");
		menuBar.add(mnView);
		
		mntmShowhideFilters = new JMenuItem("Show/Hide Filters");
		
		mnView.add(mntmShowhideFilters);

		JMenu jmnuLook = new JMenu("Look");
		menuBar.add(jmnuLook);
		
		
		//mnuProviders = new JMenu("Providers");
		//menuBar.add(mnuProviders);
		
		mnuLang= new JMenu("Langage");
		menuBar.add(mnuLang);
		
		
		mnuAbout = new JMenu("?");
		menuBar.add(mnuAbout);
		
		
		JMenuItem mntmThreadItem = new JMenuItem("Threads");
		mntmThreadItem.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable(){
					@Override
					public void run() {
						new ThreadMonitorFrame().setVisible(true);
					}
				});
				
				
			}
		});
		
		mnuAbout.add(mntmThreadItem);
		
		JMenuItem mntmHelp = new JMenuItem("Read the f***g manual");
		mntmHelp.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String url ="https://github.com/nicho92/MtgDesktopCompanion/wiki";
				try {
					Desktop.getDesktop().browse(new URI(url));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		mnuAbout.add(mntmHelp);
		
		
		mntmAboutMagicDesktop = new JMenuItem("About Magic Desktop Companion");
		mntmAboutMagicDesktop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new AboutDialog().setVisible(true);
				
			}
		});
		
		mnuAbout.add(mntmAboutMagicDesktop);
		
		mntmReportBug = new JMenuItem("Report Bug");
		mntmReportBug.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String url = "https://github.com/nicho92/MtgDesktopCompanion/issues";
					Desktop.getDesktop().browse(new URI(url));
				} catch (Exception e) {
					logger.error(e);
				}
			}
		});
		
		mnuAbout.add(mntmReportBug);
		
		List<String> looks = new ArrayList<String>();
		for(LookAndFeelInfo i : UIManager.getInstalledLookAndFeels())
			looks.add(i.getClassName());
		
	//	looks.add(new SyntheticaStandardLookAndFeel().getClass().getName());
	//	looks.add(new SyntheticaPlainLookAndFeel().getClass().getName());
		
		
		
		
		for(String ui : looks)
		{
			final JMenuItem it = new JMenuItem(ui);
			it.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setLookAndFeel(it.getText());
				}
			});
			jmnuLook.add(it);
		}
		
		for(String l : provider.getLanguages())
		{
			final JMenuItem it = new JMenuItem(l);
			it.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setDefaultLanguage(it.getText());
				}
			});
			mnuLang.add(it);
		}
		
		/*
		ButtonGroup group = new ButtonGroup();
		for(final MagicCardsProvider provider : MagicFactory.getInstance().getEnabledProviders())
		{
		   JRadioButtonMenuItem it = new JRadioButtonMenuItem(provider.toString());
			group.add(it);
			it.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						setProvider(provider);
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage(),"ERREUR",JOptionPane.ERROR_MESSAGE);
					}					
				}
			});
			
			mnuProviders.add(it);
		}
		*/
		boosterProvider = new BoosterPicturesProvider();

		DefaultRowSorter sorterPrice = new TableRowSorter<DefaultTableModel>(priceModel);
		sorterCards = new TableRowSorter<DefaultTableModel>(cardsModeltable);
		sorterCards.setComparator(7, new Comparator<String>() {
		   public int compare(String num1, String num2) {
		        	try{
		        		num1=num1.replaceAll("a","").replaceAll("b", "").trim();
		        		num2=num2.replaceAll("a","").replaceAll("b", "").trim();
			   			if(Integer.parseInt(num1)>Integer.parseInt(num2))
			   				return 1;
			   			else
			   				return -1;
					}
					catch(NumberFormatException e)
					{
						return -1;
					}
		    }
		});
		loading(false,null);
		getContentPane().setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		globalPanel = new JPanel();
		globalPanel.setLayout(new BorderLayout());


		btnSearch = new JButton(new ImageIcon(MagicGUI.class.getResource("/res/search.png")));
		btnExport = new JButton(new ImageIcon(MagicGUI.class.getResource("/res/export.png")));
		btnExport.setToolTipText("Export Result");
		btnExport.setEnabled(false);
		
		cboQuereableItems = new JComboBox(provider.getQueryableAttributs());
		cboQuereableItems.addItem("collections");
		cboCollections= new JComboBox<MagicCollection>(dao.getCollections().toArray(new MagicCollection[dao.getCollections().size()]));
		cboCollections.setVisible(false);
		
		cboQuereableItems.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(cboQuereableItems.getSelectedItem().toString().equalsIgnoreCase("set"))
				{
					txtMagicSearch.setVisible(false);
					cboEdition.setVisible(true);
					cboCollections.setVisible(false);
				}
				else if(cboQuereableItems.getSelectedItem().toString().equalsIgnoreCase("collections"))
				{
					txtMagicSearch.setVisible(false);
					cboEdition.setVisible(false);
					cboCollections.setVisible(true);
				}
				else
				{
					txtMagicSearch.setVisible(true);
					cboEdition.setVisible(false);
					cboCollections.setVisible(false);
				}
			}
		});
		txtMagicSearch = new JTextField();
		panneauHaut = new JPanel();
		globalPanel.add(panneauHaut, BorderLayout.NORTH);

		FlowLayout flowLayout = (FlowLayout) panneauHaut.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);

		txtMagicSearch.setColumns(35);
		lblLoading.setIcon(new ImageIcon(MagicGUI.class.getResource("/res/load.gif")));

		panneauHaut.add(cboQuereableItems);
		panneauHaut.add(cboCollections);
		panneauHaut.add(txtMagicSearch);
		
		List li = provider.searchSetByCriteria(null, null);
		Collections.sort(li);
		cboEdition = new JComboBox(li.toArray());
		cboEdition.setVisible(false);
		cboEdition.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				txtMagicSearch.setText(((MagicEdition)cboEdition.getSelectedItem()).getId());
			}
		});
		panneauHaut.add(cboEdition);
		panneauHaut.add(btnSearch);
		panneauHaut.add(btnExport);
		
		panneauHaut.add(lblLoading);
		panneauCard = new JPanel();
		globalPanel.add(panneauCard, BorderLayout.EAST);
		cboLanguages = new JComboBox<MagicCardNames>();
		JScrollPane scrollEditions = new JScrollPane();
		panneauCard.setLayout(new BorderLayout(0, 0));


		cmcChart = new CmcChartPanel();
		manaRepartitionPanel = new ManaRepartitionPanel();
		typeRepartitionPanel = new TypeRepartitionPanel();

		panneauCard.add(scrollEditions, BorderLayout.SOUTH);
		listEdition = new JList<MagicEdition>();
		scrollEditions.setViewportView(listEdition);							

		listEdition.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listEdition.setModel(new DefaultListModel<MagicEdition>());
		panneauCard.add(cboLanguages, BorderLayout.NORTH);
		
		historyChartPanel = new HistoryPricesPanel();
		historyChartPanel.setPreferredSize(new Dimension(400, 10));
	
		jsonCardPanel=new JSONPanel();
		
		cardsPicPanel = new CardsPicPanel();
		cardsPicPanel.setPreferredSize(new Dimension(400, 10));
		panneauCard.add(cardsPicPanel, BorderLayout.CENTER);
		tablePrice = new JXTable();
		detailCardPanel = new MagicCardDetailPanel(new MagicCard());
		tabbedCardsView = new JTabbedPane(JTabbedPane.TOP);
		tabbedCardsInfo = new JTabbedPane(JTabbedPane.TOP);
		scrollThumbnails = new JScrollPane();
		thumbnailPanel = new ThumbnailPanel();
		thumbnailPanel.setThumbnailSize(179, 240);
		thumbnailPanel.enableDragging(false);
		

		rarityRepartitionPanel = new RarityRepartitionPanel();
		JScrollPane scrollPaneRules = new JScrollPane();
		JScrollPane scrollPanePrices = new JScrollPane();
		JSplitPane panneauCentral = new JSplitPane();
		globalPanel.add(panneauCentral, BorderLayout.CENTER);
		

		tablePrice.setModel(priceModel);
		tablePrice.setRowSorter(sorterPrice);

		scrollPanePrices.setViewportView(tablePrice);

		panneauCentral.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panneauCentral.setRightComponent(tabbedCardsInfo);
		panneauCentral.setLeftComponent(tabbedCardsView);

		tabbedCardsInfo.setPreferredSize(new Dimension(0, 350));

		tabbedCardsInfo.addTab("Details", null, detailCardPanel, null);

		editionDetailPanel = new JPanel();
		tabbedCardsInfo.addTab("Edition", null, editionDetailPanel, null);
		editionDetailPanel.setLayout(new BorderLayout(0, 0));

		magicEditionDetailPanel = new MagicEditionDetailPanel();
		editionDetailPanel.add(magicEditionDetailPanel, BorderLayout.CENTER);

		boosterPanel = new JPanel();
		editionDetailPanel.add(boosterPanel, BorderLayout.EAST);
		boosterPanel.setLayout(new BorderLayout(0, 0));

		btnGenerateBooster = new JButton("Open a Booster");

		boosterPanel.add(btnGenerateBooster, BorderLayout.NORTH);
		
		lblBoosterPic = new JLabel("");
		boosterPanel.add(lblBoosterPic);
		tabbedCardsInfo.addTab("Prices", null, scrollPanePrices, null);
		tabbedCardsInfo.addTab("Rules", null, scrollPaneRules, null);
		tabbedCardsInfo.addTab("Variation", null, historyChartPanel, null);
		tabbedCardsInfo.addTab("Json", null,jsonCardPanel,null);
		
		panelResultsCards = new JPanel();
		tabbedCardsView.addTab("Results", null, panelResultsCards, null);
				panelResultsCards.setLayout(new BorderLayout(0, 0));
		
				tableCards = new JXTable();
				
				tableCards.setColumnControlVisible(true);
				JScrollPane scrollCards = new JScrollPane();
				panelResultsCards.add(scrollCards);
				scrollCards.setViewportView(tableCards);
				scrollCards.setMinimumSize(new Dimension(0, 0));
				
				
						tableCards.setRowHeight(ManaPanel.row_height);
						tableCards.setModel(cardsModeltable);
						tableCards.setRowSorter(sorterCards);
						tableCards.setShowVerticalLines(false);
						
						//IFilterEditor editor = filterHeader.getFilterEditor(1);
						
						
						
						panelFilters = new JPanel();
						panelFilters.setVisible(false);
						FlowLayout fl_panelFilters = (FlowLayout) panelFilters.getLayout();
						fl_panelFilters.setAlignment(FlowLayout.LEFT);
						panelResultsCards.add(panelFilters, BorderLayout.NORTH);
						
						JLabel lblFilter = new JLabel("Filter :");
						panelFilters.add(lblFilter);
						
						txtFilter = new JTextField();
						
						panelFilters.add(txtFilter);
						txtFilter.setColumns(25);
						
						btnClear = new JButton("");
						btnClear.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								txtFilter.setText("");
								sorterCards.setRowFilter(null);
							}
						});
						btnClear.setIcon(new ImageIcon(MagicGUI.class.getResource("/res/09_clear_location.png")));
						panelFilters.add(btnClear);
						
						panelmana = new JPanel();
						panelFilters.add(panelmana);
						panelmana.setLayout(new GridLayout(1, 0, 2, 2));
						
						String[] symbolcs = new String[]{"W","U","B","R","G","C","1"};
						ManaPanel pan = new ManaPanel();
						
						for(String s : symbolcs)
						{
							final JButton btnG = new JButton();
							btnG.setToolTipText(s);
							if(s.equals("1"))
								btnG.setToolTipText("[0-9]*");

							btnG.setIcon(new ImageIcon(pan.getManaSymbol(s).getScaledInstance(15, 15, Image.SCALE_SMOOTH)));
							btnG.setForeground(btnG.getBackground());
							
							btnG.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									txtFilter.setText("\\{" + btnG.getToolTipText()+"}");
									sorterCards.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
									
								}
							});
							panelmana.add(btnG);
								
						}
						
						
		tabbedCardsView.addTab("Thumbnail", null, scrollThumbnails, null);

		txtRulesArea = new JTextArea();
		txtRulesArea.setLineWrap(true);
		txtRulesArea.setWrapStyleWord(true);
		txtRulesArea.setEditable(false);
		scrollPaneRules.setViewportView(txtRulesArea);
		scrollThumbnails.setViewportView(thumbnailPanel);
		scrollThumbnails.getVerticalScrollBar().setUnitIncrement(10);
		tabbedCardsInfo.setMinimumSize(new Dimension(23,200));



		tabbedCardsView.addTab("Mana Curve", null, cmcChart, null);

		tabbedCardsView.addTab("Colors", null, manaRepartitionPanel, null);


		tabbedCardsView.addTab("Types", null, typeRepartitionPanel, null);
		tabbedCardsView.addTab("Rarity", null, rarityRepartitionPanel, null);
		deckBuilderGUI = new DeckBuilderGUI(provider,dao);

		collectionPanelGUI = new CollectionPanelGUI();

		tabbedPane.addTab("Search", new ImageIcon(MagicGUI.class.getResource("/res/search.gif")), globalPanel, null);
		tabbedPane.addTab("Deck", new ImageIcon(MagicGUI.class.getResource("/res/book_icon.jpg")), deckBuilderGUI, null);
		tabbedPane.addTab("Game", new ImageIcon(MagicGUI.class.getResource("/res/bottom.png")), GamePanelGUI.getInstance(), null);
		tabbedPane.addTab("Collection", new ImageIcon(MagicGUI.class.getResource("/res/collection.png")), collectionPanelGUI, null);
		tabbedPane.addTab("DashBoard", new ImageIcon(MagicGUI.class.getResource("/res/dashboard.png")), new DashBoardGUI(), null);
		tabbedPane.addTab("Shopping", new ImageIcon(MagicGUI.class.getResource("/res/shop.gif")), new ShopperGUI(), null);
		tabbedPane.addTab("Builder", new ImageIcon(MagicGUI.class.getResource("/res/create.png")), panneauBuilder, null);
		tabbedPane.addTab("RSS", new ImageIcon(MagicGUI.class.getResource("/res/rss.png")), new RssGUI(), null);
		tabbedPane.addTab("Configuration", new ImageIcon(MagicGUI.class.getResource("/res/build.png")), new ConfigurationPanelGUI (), null);
		
		
		
		initPopupCollection();


	}


	protected void setProvider(MagicCardsProvider provider2) throws Exception {
		
		logger.debug("set provider '" + provider + "' by '" + provider2 +"'" ) ;
		this.provider=provider2;
		cboQuereableItems.removeAll();
		cboQuereableItems.setModel(new DefaultComboBoxModel<>(provider.getQueryableAttributs()));
		//cboQuereableItems.updateUI();
		cboQuereableItems.addItem("collections");
		
		List li = provider.searchSetByCriteria(null, null);
		Collections.sort(li);
		cboEdition.removeAll();
		cboEdition.setModel(new DefaultComboBoxModel(li.toArray()));
	}

	public void setSelectedCard(MagicCard mc)
	{
		this.selected=mc;
		updateCards();
	}

	public MagicGUI() {

		try {
			priceModel=new CardsPriceTableModel();
			cardsModeltable = new MagicCardTableModel();
			
			provider = MagicFactory.getInstance().getEnabledProviders();
			logger.info("set provider : " + provider);
			
			dao=MagicFactory.getInstance().getEnabledDAO();
			dao.init();
			
			
			initGUI();


			btnSearch.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					selectedEdition=null;
					if(txtMagicSearch.getText().equals("") && !cboCollections.isVisible())
						return;
					
					Runnable r = new Runnable() {
						public void run() {
							loading(true,"searching");
							try {
								String searchName=txtMagicSearch.getText();
								
								if(cboCollections.isVisible())
									cards = dao.getCardsFromCollection((MagicCollection)cboCollections.getSelectedItem());
								else
									cards = provider.searchCardByCriteria(cboQuereableItems.getSelectedItem().toString(),searchName,null);
								
								
								
								cardsModeltable.init(cards,defaultLanguage);
								//cardsModeltable.fireTableStructureChanged();
								tableCards.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());
								
								cardsModeltable.fireTableDataChanged();
								
								thumbnailPanel.initThumbnails(cards,false);
								
								
								cmcChart.init(cards);
								typeRepartitionPanel.init(cards);
								manaRepartitionPanel.init(cards);
								rarityRepartitionPanel.init(cards);
								tabbedCardsView.setTitleAt(0, "Results ("+cardsModeltable.getRowCount()+")");
								
								btnExport.setEnabled(tableCards.getRowCount()>0);

							} catch (Exception e) {
								e.printStackTrace();
								JOptionPane.showMessageDialog(null, e.getMessage(),"ERREUR",JOptionPane.ERROR_MESSAGE);
							}
							loading(false,"");
						}
					};
					
					ThreadManager.getInstance().execute(r,"SearchCards");
				}
			});

			btnGenerateBooster.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					if(selectedEdition==null)
						selectedEdition = selected.getEditions().get(0);

					try {
						tabbedCardsView.setSelectedIndex(1);
						thumbnailPanel.initThumbnails( provider.openBooster(selectedEdition),false);

					} catch (Exception e) {
						logger.error(e);
					}
				}
			});

			tableCards.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent evt) {

					if(SwingUtilities.isRightMouseButton(evt))
					{
						Point point = evt.getPoint();
						popupMenu.show(tableCards, (int)point.getX(), (int)point.getY());
					}
					else
					{
						selected = (MagicCard)tableCards.getValueAt(tableCards.getSelectedRow(), 0);
						selectedEdition = selected.getEditions().get(0);
						updateCards();

					}
				}
			});

			listEdition.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent mev) {
						selectedEdition = listEdition.getSelectedValue();
						detailCardPanel.setMagicLogo(selectedEdition.getId(),""+selectedEdition.getRarity());
						magicEditionDetailPanel.setMagicEdition(selectedEdition);
						
						try {
							logger.debug("LOADING ED " + BeanUtils.describe(selectedEdition));
						} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
							logger.error(e1);
						}
						
						ThreadManager.getInstance().execute(new Runnable() {
							public void run() {
								try {
									loading(true,"loading edition");
									
										cardsPicPanel.showPhoto(selected,selectedEdition);//backcard
										
										magicEditionDetailPanel.setMagicEdition(selectedEdition);
										historyChartPanel.init(MagicFactory.getInstance().getEnabledDashBoard().getPriceVariation(selected, selectedEdition),selected.getName());
										if(tabbedCardsInfo.getSelectedIndex()==INDEX_PRICES)
											updatePrices();
									loading(false,"");
								} catch (IOException e) {
									logger.error(e);
								}
							}
						},"changeEdition");
				}
			});

			tablePrice.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent ev) {
					if(ev.getClickCount()==2 && !ev.isConsumed())
					{
						ev.consume();
						try {
							String url = tablePrice.getValueAt(tablePrice.getSelectedRow(), 6).toString();
							Desktop.getDesktop().browse(new URI(url));
						} catch (Exception e) {
							logger.error(e);
						}

					}

				}
			});

			/*cboLanguages.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					MagicCardNames selLang = (MagicCardNames)cboLanguages.getSelectedItem();
					if(selLang!=null)
					{
						defaultLanguage=selLang.getLanguage();
					}
				}
			});*/


			cboLanguages.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					MagicCardNames selLang = (MagicCardNames)cboLanguages.getSelectedItem();
					try {
						if(selLang!=null)
						{
							MagicEdition ed = new MagicEdition();
								ed.setMultiverse_id(""+selLang.getGathererId());
							cardsPicPanel.showPhoto(selected,ed);
						}
					} catch (Exception e1) {}
				}

			});


			mntmExit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);

				}
			});

			
			btnExport.addActionListener(new ActionListener() {
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
								jf.setSelectedFile(new File("search"+exp.getFileExtension()));
								int result = jf.showSaveDialog(null);
								final File f=jf.getSelectedFile();
								
								if(result==JFileChooser.APPROVE_OPTION)
									ThreadManager.getInstance().execute(new Runnable() {
										
										@Override
										public void run() {
											try {
											loading(true, "export " + exp);
											
											List<MagicCard> export = ((MagicCardTableModel)tableCards.getRowSorter().getModel()).getListCards();
											exp.export(export, f);
											loading(false, "");
											JOptionPane.showMessageDialog(null, "Export Finished",exp.getName() + " Finished",JOptionPane.INFORMATION_MESSAGE);
											} catch (Exception e) {
												logger.error(e);
												loading(false, "");
												JOptionPane.showMessageDialog(null, e,"Error",JOptionPane.ERROR_MESSAGE);
											}	
										
										}
										}, "export search " + exp);
									
								
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
			
			tabbedCardsInfo.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {

						if(tabbedCardsInfo.getSelectedIndex()==INDEX_PRICES)
							updatePrices();
					
				}
			});

			txtFilter.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					String text = txtFilter.getText();
			          if (text.length() == 0) {
			        	  sorterCards.setRowFilter(null);
			          } else {
			        	  sorterCards.setRowFilter(RowFilter.regexFilter(text));
			          }
				}
			});
			
			mntmShowhideFilters.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					filterHeader = new TableFilterHeader(null, AutoChoices.ENABLED);
					filterHeader.setSelectionBackground(Color.LIGHT_GRAY);
					if(panelFilters.isVisible())
					{
						panelFilters.setVisible(false);
						filterHeader.setTable(null);
					}
					else
					{
						panelFilters.setVisible(true);
						filterHeader.setTable(tableCards);
					}
					
				}
			});
			
			thumbnailPanel.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					DisplayableCard lab = (DisplayableCard)thumbnailPanel.getComponentAt(new Point(e.getX(), e.getY()));
					selected = lab.getMagicCard();
					updateCards();
				}
				
			});
			
			
			if (SystemTray.isSupported()) {
				tray.add(trayIcon);

				trayIcon.displayMessage(getTitle(),"Application started\n",TrayIcon.MessageType.INFO);
				trayIcon.addActionListener(new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						if(!isVisible())
							setVisible(true);
						else
							setVisible(false);
					}
				});
				
				PopupMenu menuTray = new PopupMenu();
				
				
				for(int index_tab = 0;index_tab<tabbedPane.getTabCount();index_tab++)
				{
					final int index = index_tab;
					MenuItem it = new MenuItem(tabbedPane.getTitleAt(index_tab));
					it.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
								setVisible(true);
								setSelectedTab(index);
								
						}
					});
					menuTray.add(it);
				}
				
				trayIcon.setPopupMenu(menuTray);
				trayIcon.setToolTip("MTG Desktop Companion");
			}		
			
			
		} 
		catch(Exception e)
		{
			logger.error(e);
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e,"ERROR",JOptionPane.ERROR_MESSAGE);
		}

		txtMagicSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSearch.doClick();

			}
		});

		logger.debug("construction of GUI : done");
	}

	public void updatePrices() {
		ThreadManager.getInstance().execute(new Runnable() {

			@Override
			public void run() {

				loading(true,"loading prices");
				priceModel.init(selected, selectedEdition);
				priceModel.fireTableDataChanged();
				loading(false,"");

			}
		},"updatePrices");
		
	}
	
	public void updateCards() {
		try {
			cboLanguages.removeAllItems();
			txtRulesArea.setText("");
			
			((DefaultListModel<MagicEdition>)listEdition.getModel()).removeAllElements();

			for(MagicCardNames mcn : selected.getForeignNames())
				cboLanguages.addItem(mcn);
			
			for(MagicEdition me : selected.getEditions())
				((DefaultListModel<MagicEdition>)listEdition.getModel()).addElement(me);

			detailCardPanel.setMagicCard(selected,true);
			magicEditionDetailPanel.setMagicEdition(selected.getEditions().get(0));
			
			
			ThreadManager.getInstance().execute(new Runnable() {
				public void run() {
					lblBoosterPic.setIcon(boosterProvider.getBoosterFor(selectedEdition));
				}
			}, "load booster pic for " + selectedEdition);
			
			
			
			
			for(MagicRuling mr : selected.getRulings())
			{
				txtRulesArea.append(mr.toString());
				txtRulesArea.append("\n");
			}
		
			
			if(tabbedCardsInfo.getSelectedIndex()==INDEX_PRICES)
				updatePrices();
			
			
			historyChartPanel.init(MagicFactory.getInstance().getEnabledDashBoard().getPriceVariation(selected, selectedEdition),selected.getName());
			
			jsonCardPanel.showCard(selected);
			
		} catch (Exception e1) {
			e1.printStackTrace();
			logger.error(e1);
		}

	}


	

}

package org.magic.gui.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.components.dialog.DefaultStockEditorDialog;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;
import org.magic.services.extra.IconSetProvider;
import org.magic.tools.ImageTools;
import org.magic.tools.InstallCert;
import org.magic.tools.UITools;

public class ConfigurationPanel extends JPanel {

	private static final String LANGAGE = "langage";
	private static final String DEFAULT_LIBRARY = "default-library";
	private static final String CURRENCY = "currency";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JComboBox<MTGDao> cboTargetDAO;
	private JComboBox<MagicCollection> cboCollections;
	private JComboBox<Level> cboLogLevels;
	private JTextField txtdirWebsite;
	private JComboBox<MagicEdition> cboEditionLands;
	private JTextField txtMinPrice;
	private JCheckBox cbojsonView;
	private JTextField txtWebSiteCertificate;
	private JCheckBox chkToolTip;
	private AbstractBuzyIndicatorComponent lblLoading;
	private JTextField txtName;
	private JLabel lblIconAvatar;
	private JCheckBox chckbxIconset;
	private JCheckBox chckbxIconcards;
	private JCheckBox chckbxSearch;
	private JCheckBox chckbxCollection;
	private JCheckBox chckbxDashboard;
	private JCheckBox chckbxGame;
	private JCheckBox chckbxDeckBuilder;
	private JCheckBox chckbxShopper;
	private JCheckBox chckbxAlert;
	private JCheckBox chckbxRss;
	private JCheckBox chckbxCardBuilder;
	private JCheckBox chckbxStock;
	private JResizerPanel resizerPanel;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private JCheckBox chckbxWallpaper;
	private JCheckBox chckbxHistory;

	public void loading(boolean show, String text) {
		if (show) {
			lblLoading.start();
			lblLoading.setText(text);
		} else {
			lblLoading.end();
		}
	}
	
	private JPanel createBoxPanel(String keyName,GridBagLayout layout)
	{
		JPanel pane = new JPanel();
		pane.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true),
				MTGControler.getInstance().getLangService().getCapitalize(keyName), TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(64, 64, 64)));
		pane.setLayout(layout);
		
		return pane;
	}
	
	
	
	public ConfigurationPanel() {
		lblLoading = AbstractBuzyIndicatorComponent.createLabelComponent();

		
/////////////CONFIG PANEL BOX		
		
		GridBagLayout mainPanelLayout = new GridBagLayout();
					mainPanelLayout.columnWidths = new int[] { 300, 212, 0 };
					mainPanelLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
					mainPanelLayout.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
					mainPanelLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
					
		GridBagLayout daoPanelLayout = new GridBagLayout();
					daoPanelLayout.columnWidths = new int[] { 172, 130, 0, 0 };
					daoPanelLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
					daoPanelLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
					daoPanelLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };			
		
		GridBagLayout configPanelLayout = new GridBagLayout();
					configPanelLayout.columnWidths = new int[] { 0, 0, 0, 0 };
					configPanelLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
					configPanelLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
					configPanelLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
	
		GridBagLayout websitePanelLayout = new GridBagLayout();
					websitePanelLayout.columnWidths = new int[] { 0, 0, 0, 0, 0 };
					websitePanelLayout.rowHeights = new int[] { 0, 0, 0 };
					websitePanelLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
					websitePanelLayout.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		
		GridBagLayout gameProfilPanelLayout = new GridBagLayout();
					gameProfilPanelLayout.columnWidths = new int[] { 0, 71, 0, 0 };
					gameProfilPanelLayout.rowHeights = new int[] { 0, 0, 29, 0, 0 };
					gameProfilPanelLayout.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
					gameProfilPanelLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		
		GridBagLayout modulesPanelLayout = new GridBagLayout();
					modulesPanelLayout.columnWidths = new int[] { 0, 0, 0, 103, 0, 121, 0, 0 };
					modulesPanelLayout.rowHeights = new int[] { 0, 0, 0, 0,  };
					modulesPanelLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
					modulesPanelLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		
		GridBagLayout currencyPanelLayout = new GridBagLayout();
					currencyPanelLayout.columnWidths = new int[] { 106, 67, 0, 0 };
					currencyPanelLayout.rowHeights = new int[] { 23, 0, 0, 0 };
					currencyPanelLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
					currencyPanelLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		
		GridBagLayout guiPanelLayout = new GridBagLayout();
					guiPanelLayout.columnWidths = new int[] { 188, 38, 0, 0 };
					guiPanelLayout.rowHeights = new int[] { 23, 0, 0, 0, 0, 0, 0, 0 };
					guiPanelLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0 };
					guiPanelLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
					
					
		setLayout(mainPanelLayout);
		

		JPanel panelDAO = createBoxPanel("DATABASES",daoPanelLayout);
		JPanel panelConfig = createBoxPanel("CONFIGURATION",configPanelLayout);
		JPanel panelWebSite = createBoxPanel("WEBSITE",websitePanelLayout);
		JPanel panelGameProfil = createBoxPanel("GAME",gameProfilPanelLayout);
		JPanel panelModule = createBoxPanel("Modules",modulesPanelLayout);
		JPanel panelCurrency = createBoxPanel("CURRENCY",currencyPanelLayout);
		JPanel panelGUI = createBoxPanel("GUI",guiPanelLayout);
		
		
		add(panelDAO, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 0, 0));
		add(panelConfig, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH,  1, 0));
		add(panelWebSite, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH,  0, 1));
		add(panelGameProfil, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH,  0, 2));
		add(panelModule, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH,  1, 1));
		add(panelCurrency, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH,  0, 3));
		add(panelGUI, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH,  1, 2));

		
		GridBagConstraints gbclblLoading = UITools.createGridBagConstraints(null, GridBagConstraints.BOTH,  0, 4);
		gbclblLoading.gridwidth = 2;
		add(lblLoading,gbclblLoading);
		
		
/////////////DAO BOX		
		JLabel lblBackupDao = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("DAO_BACKUP") + " : ");
		textField = new JTextField(10);
		JButton btnBackup = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SAVE"));
		JLabel lblDuplicateDb = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("DUPLICATE_TO",MTGControler.getInstance().getEnabled(MTGDao.class)));
		JButton btnDuplicate = new JButton((MTGControler.getInstance().getLangService().getCapitalize("SAVE")));
		JLabel lblLocation = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("LOCATION") + " : ");
		JLabel lbldbLocationValue = new JLabel(MTGControler.getInstance().getEnabled(MTGDao.class).getDBLocation());
		JLabel lblSize = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("SIZE") + " : ");
		JLabel lblSizeValue  = new JLabel(String.valueOf(MTGControler.getInstance().getEnabled(MTGDao.class).getDBSize() / 1024 / 1024) + "MB");
		JLabel lblIndexation = new JLabel("Indexation : ");
		JLabel lblIndexSize = new JLabel(UITools.formatDate(MTGControler.getInstance().getEnabled(MTGCardsIndexer.class).getIndexDate()));
		JButton btnIndexation = new JButton("Reindexation");
		cboTargetDAO = UITools.createCombobox(MTGDao.class, true);
		cboTargetDAO.removeItem(MTGControler.getInstance().getEnabled(MTGDao.class));

		
		
		panelDAO.add(lblBackupDao, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 0));
		panelDAO.add(textField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 0));
		panelDAO.add(btnBackup, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 2, 0));
		panelDAO.add(lblDuplicateDb, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 1));
		panelDAO.add(cboTargetDAO, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 1));
		panelDAO.add(btnDuplicate, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 2, 1));
		panelDAO.add(lblLocation, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 2));
		panelDAO.add(lbldbLocationValue, UITools.createGridBagConstraints(null, null,  1, 2));
		panelDAO.add(lblSize, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 3));
		panelDAO.add(lblSizeValue, UITools.createGridBagConstraints(null, null,  1, 3));
		panelDAO.add(lblIndexation, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, 0, 4));
		panelDAO.add(lblIndexSize, UITools.createGridBagConstraints(null, null, 1, 4));
		panelDAO.add(btnIndexation, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 2, 4));
		
		
/////////////CONFIG BOX
		JLabel lblMainCol = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("MAIN_COLLECTION") + " :");
		JButton btnSaveDefaultLib = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SAVE"));
		cboCollections = UITools.createComboboxCollection();
		JLabel lblDefaultLandManuel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("DEFAULT_LAND_IMPORT") + " :");
		JButton btnSaveDefaultLandDeck = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SAVE"));
		JLabel lblLogLevel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("LOG_LEVEL") + " :");
		cboLogLevels = UITools.createCombobox(MTGLogger.getLevels());
		JButton btnSaveLoglevel = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SAVE"));
		JLabel lblDontTakeAlert = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("SHOW_LOW_PRICES") + " :");
		txtMinPrice = new JTextField(MTGControler.getInstance().get("min-price-alert"),25);
		JButton btnSavePrice = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SAVE"));
		JLabel lblCleancache = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CLEAN_CACHE") + " :");
		JButton btnClean = new JButton(MTGControler.getInstance().getLangService().getCapitalize("CLEAN"));
		JPanel panelCheckCache = new JPanel();
		JLabel lblAutoStock = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("AUTO_STOCK") + ": ");
		JCheckBox chkboxAutoAdd = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("AUTO_STOCK_ADD"));
		chckbxIconset = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("IMG_SET"));
		chckbxIconcards = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("IMG_CARD"));
		JCheckBox chkboxAutoDelete = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("AUTO_STOCK_DELETE"));
		JButton btnDefaultStock = new JButton("Default Stock");
		JPanel panelAutoStock = new JPanel();
		cboEditionLands = UITools.createComboboxEditions();

		((FlowLayout) panelAutoStock.getLayout()).setAlignment(FlowLayout.LEFT);
		
		
		chckbxIconset.setSelected(true);
		chckbxIconcards.setSelected(true);
		chkboxAutoAdd.setSelected(MTGControler.getInstance().get("collections/stockAutoAdd").equals("true"));
		chkboxAutoDelete.setSelected(MTGControler.getInstance().get("collections/stockAutoDelete").equals("true"));
		
		
		panelCheckCache.add(chckbxIconset);
		panelCheckCache.add(chckbxIconcards);
		panelAutoStock.add(chkboxAutoAdd);
		panelAutoStock.add(chkboxAutoDelete);

		
		panelConfig.add(lblMainCol, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 0));
		panelConfig.add(cboCollections, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 0));
		panelConfig.add(btnSaveDefaultLib, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 2, 0));
		panelConfig.add(lblDefaultLandManuel, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 1));
		panelConfig.add(cboEditionLands, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 1));
		panelConfig.add(btnSaveDefaultLandDeck, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 2, 1));
		panelConfig.add(lblLogLevel, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 2));
		panelConfig.add(cboLogLevels, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 2));
		panelConfig.add(btnSaveLoglevel, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 2, 2));
		panelConfig.add(lblDontTakeAlert, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 3));
		panelConfig.add(txtMinPrice, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 3));
		panelConfig.add(btnSavePrice, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 2, 3));
		panelConfig.add(lblCleancache, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 4));
		panelConfig.add(panelCheckCache, UITools.createGridBagConstraints(GridBagConstraints.WEST, GridBagConstraints.VERTICAL,  1, 4));
		panelConfig.add(btnClean, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 2, 4));
		panelConfig.add(lblAutoStock, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, 0, 5));
		panelConfig.add(panelAutoStock, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 5));
		panelConfig.add(btnDefaultStock, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 2, 5));

		

		

/////////////WEBSITE BOX		
	
		JLabel lblWebsiteDir = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("DIRECTORY") + " :");
		txtdirWebsite = new JTextField(MTGControler.getInstance().get("default-website-dir"),10);
		JButton btnWebsiteSave = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SAVE"));
		JLabel lblAddWebsiteCertificate = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("ADD_CERTIFICATE") + " :");
		txtWebSiteCertificate = new JTextField("www.",10);
		JButton btnAdd = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SAVE"));
		
		
		panelWebSite.add(lblWebsiteDir, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  0, 0));
		panelWebSite.add(txtdirWebsite, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 0));
		panelWebSite.add(btnWebsiteSave, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  3, 0));
		panelWebSite.add(lblAddWebsiteCertificate, UITools.createGridBagConstraints(null, null,  0, 1));
		panelWebSite.add(txtWebSiteCertificate, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 1));
		panelWebSite.add(btnAdd, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  3, 1));

		
		
/////////////PROFIL BOX		
		
		JLabel lblName = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("NAME") + " :");
		txtName = new JTextField(MTGControler.getInstance().get("/game/player-profil/name"),10);
		JLabel lblAvatar = new JLabel("Avatar :");
		lblIconAvatar = new JLabel();
		lblIconAvatar.setBorder(new LineBorder(Color.RED, 1, true));
		JResizerPanel gamePicsResizerPanel = new JResizerPanel(MTGControler.getInstance().getCardsGameDimension());
		JButton btnSaveProfilGame = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SAVE"));
	
		panelGameProfil.add(lblName, UITools.createGridBagConstraints(GridBagConstraints.EAST, null,  0, 0));
		panelGameProfil.add(txtName, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 0));
		panelGameProfil.add(lblAvatar, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  0, 1));
		panelGameProfil.add(lblIconAvatar, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH,  1, 1));
		panelGameProfil.add(gamePicsResizerPanel, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH,  2, 0));
		panelGameProfil.add(btnSaveProfilGame,UITools.createGridBagConstraints(null, GridBagConstraints.BOTH,  3, 0));
		
		loadIcon();

/////////////MODULES BOX				

		chckbxSearch = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("SEARCH_MODULE"));
		chckbxCollection = new JCheckBox("Collection");
		chckbxDashboard = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("DASHBOARD_MODULE"));
		chckbxShopper = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("SHOPPING_MODULE"));
		chckbxCardBuilder = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("BUILDER_MODULE"));
		chckbxHistory = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("HISTORY_MODULE"));
		chckbxDashboard.setSelected(MTGControler.getInstance().get("modules/dashboard").equals("true"));
		chckbxSearch.setSelected(MTGControler.getInstance().get("modules/search").equals("true"));
		chckbxCollection.setSelected(MTGControler.getInstance().get("modules/collection").equals("true"));
		chckbxStock = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("STOCK_MODULE"));
		chckbxAlert = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("ALERT_MODULE"));
		chckbxGame = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("GAME_MODULE"));
		chckbxDeckBuilder = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("DECK_MODULE"));
		chckbxRss = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("RSS_MODULE"));
		chckbxWallpaper = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("WALLPAPER"));
	
		chckbxDashboard.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/dashboard", chckbxDashboard.isSelected()));
		chckbxStock.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/stock", chckbxStock.isSelected()));
		chckbxAlert.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/alarm", chckbxAlert.isSelected()));
		chckbxGame.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/game", chckbxGame.isSelected()));
		chckbxDeckBuilder.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/deckbuilder", chckbxDeckBuilder.isSelected()));
		chckbxRss.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/rss", chckbxRss.isSelected()));
		chckbxWallpaper.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/wallpaper", chckbxWallpaper.isSelected()));
		chckbxShopper.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/shopper", chckbxShopper.isSelected()));
		chckbxHistory.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/history", chckbxHistory.isSelected()));
		chckbxCardBuilder.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/cardbuilder", chckbxCardBuilder.isSelected()));
		chckbxCollection.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/collection", chckbxCollection.isSelected()));
		chckbxSearch.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/search", chckbxSearch.isSelected()));
		
		chckbxStock.setSelected(MTGControler.getInstance().get("modules/stock").equals("true"));
		chckbxAlert.setSelected(MTGControler.getInstance().get("modules/alarm").equals("true"));
		chckbxGame.setSelected(MTGControler.getInstance().get("modules/game").equals("true"));
		chckbxDeckBuilder.setSelected(MTGControler.getInstance().get("modules/deckbuilder").equals("true"));
		chckbxRss.setSelected(MTGControler.getInstance().get("modules/rss").equals("true"));
		chckbxWallpaper.setSelected(MTGControler.getInstance().get("modules/wallpaper").equals("true"));
		chckbxShopper.setSelected(MTGControler.getInstance().get("modules/shopper").equals("true"));
		chckbxHistory.setSelected(MTGControler.getInstance().get("modules/history").equals("true"));
		chckbxCardBuilder.setSelected(MTGControler.getInstance().get("modules/cardbuilder").equals("true"));
		
		
		panelModule.add(chckbxSearch, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  1, 0));
		panelModule.add(chckbxCollection, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  3, 0));
		panelModule.add(chckbxStock, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  5, 0));
		panelModule.add(chckbxAlert, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  6, 0));
		panelModule.add(chckbxDashboard, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  1, 1));
		panelModule.add(chckbxGame, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  3, 1));
		panelModule.add(chckbxRss, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  5, 1));
		panelModule.add(chckbxWallpaper, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  6, 1));
		panelModule.add(chckbxDeckBuilder, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  1, 2));
		panelModule.add(chckbxShopper, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  3, 2));
		panelModule.add(chckbxHistory, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  5, 2));
		panelModule.add(chckbxCardBuilder, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  6, 2));


	
/////////////CURRENCY BOX				

		JLabel lblCurrency = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CURRENCY") + " :");
		JComboBox<Currency> cboCurrency = UITools.createCombobox(new ArrayList<>(Currency.getAvailableCurrencies()));
		JButton btnSavecurrency = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SAVE"));
		JLabel lclCodeCurrency = new JLabel("CurrencyLayer API code :");
		JTextField txtCurrencyFieldApiCode = new JTextField(MTGControler.getInstance().get("/currencylayer-access-api"),10);
		JButton btnSaveCode = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SAVE"));
		JButton btnUpdateCurrency = new JButton("Update Currency");
		
		
		if (MTGControler.getInstance().get(CURRENCY).isEmpty())
			cboCurrency.setSelectedItem(Currency.getInstance(Locale.getDefault()));
		else
			cboCurrency.setSelectedItem(Currency.getInstance(MTGControler.getInstance().get(CURRENCY)));
		
		
		panelCurrency.add(lblCurrency, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 0));
		panelCurrency.add(cboCurrency, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 0));
		panelCurrency.add(btnSavecurrency, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  2, 0));
		panelCurrency.add(lclCodeCurrency, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 1));
		panelCurrency.add(txtCurrencyFieldApiCode, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 1));
		panelCurrency.add(btnSaveCode, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  2, 1));
		panelCurrency.add(btnUpdateCurrency, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  2, 2));

/////////////GUI BOX			
		
		JLabel lblGuiLocal = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("LOCALISATION") + " :");
		JComboBox<Locale> cboLocales = UITools.createCombobox(MTGControler.getInstance().getLangService().getAvailableLocale());
		JButton btnSaveLocal = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SAVE"));
		JLabel lblCardsLanguage = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARDS_LANGUAGE") + " :");
		JComboBox<String> cboLanguages = UITools.createCombobox(MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getLanguages());
		JButton btnSavelang = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SAVE"));
		JLabel lblLook = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("LOOK") + " :");
		JComboBox<LookAndFeelInfo> cboLook = UITools.createCombobox(MTGControler.getInstance().getLafService().getAllLookAndFeel());
		JLabel lblPicsSize = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("THUMBNAIL_SIZE") + ": ");
		JButton btnSavePicSize = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SAVE"));
		resizerPanel = new JResizerPanel(MTGControler.getInstance().getPictureProviderDimension());
		JLabel lblShowJsonPanel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("SHOW_JSON_PANEL") + " :");
		cbojsonView = new JCheckBox();
		JLabel lblShowTooltip = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("SHOW_TOOLTIP") + " :");
		chkToolTip = new JCheckBox("");
		JLabel lblToolPosition = new JLabel("Position :");
		JComboBox<String> cboToolPosition = UITools.createCombobox(new String[] { "TOP", "LEFT", "RIGHT", "BOTTOM" });
		
		cboLocales.setSelectedItem(MTGControler.getInstance().getLocale());
		cbojsonView.setSelected(MTGControler.getInstance().get("debug-json-panel").equals("true"));
		chkToolTip.setSelected(MTGControler.getInstance().get("tooltip").equals("true"));
		cboToolPosition.setSelectedItem(MTGControler.getInstance().get("ui/moduleTabPosition", "LEFT"));
		
		
		panelGUI.add(lblGuiLocal, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 0));
		panelGUI.add(cboLocales, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 0));
		panelGUI.add(btnSaveLocal, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  2, 0));
		panelGUI.add(lblCardsLanguage, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 1));
		panelGUI.add(cboLanguages, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 1));
		panelGUI.add(btnSavelang, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  2, 1));
		panelGUI.add(lblLook,  UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 2));
		panelGUI.add(cboLook,  UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 2));
		panelGUI.add(lblPicsSize, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 3));
		panelGUI.add(resizerPanel, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 3));
		panelGUI.add(btnSavePicSize, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  2, 3));
		panelGUI.add(lblShowJsonPanel, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 4));
		panelGUI.add(cbojsonView, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 4));
		panelGUI.add(lblShowTooltip, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 5));
		panelGUI.add(chkToolTip, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 5));
		panelGUI.add(lblToolPosition, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 6));
		panelGUI.add(cboToolPosition, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 6));
		
		
		
		
/////////////EVENTS	
				
		cboToolPosition.addItemListener(ie -> {
			if (ie.getStateChange() == ItemEvent.SELECTED)
				MTGControler.getInstance().setProperty("ui/moduleTabPosition",
						cboToolPosition.getSelectedItem().toString());

		});
		
		chkToolTip.addItemListener(ie -> MTGControler.getInstance().setProperty("tooltip", chkToolTip.isSelected()));
		btnSaveLocal.addActionListener(ae -> MTGControler.getInstance().setProperty("locale", cboLocales.getSelectedItem()));
		btnSavelang.addActionListener(ae -> MTGControler.getInstance().setProperty(LANGAGE, cboLanguages.getSelectedItem().toString()));
		cboLook.addActionListener(ae -> MTGControler.getInstance().getLafService().setLookAndFeel(SwingUtilities.getAncestorOfClass(JFrame.class, this), (LookAndFeelInfo) cboLook.getSelectedItem(),true));
		btnSavePicSize.addActionListener(ae -> {
			MTGControler.getInstance().setProperty("/card-pictures-dimension/width",(int) resizerPanel.getDimension().getWidth());
			MTGControler.getInstance().setProperty("/card-pictures-dimension/height",(int) resizerPanel.getDimension().getHeight());
			resizerPanel.setValue(0);
			MTGControler.getInstance().getEnabled(MTGPictureProvider.class).setSize(resizerPanel.getDimension());
		});
		
		cbojsonView.addItemListener(ae -> MTGControler.getInstance().setProperty("debug-json-panel", cbojsonView.isSelected()));
		
		btnSavecurrency.addActionListener(ae -> MTGControler.getInstance().setProperty(CURRENCY, cboCurrency.getSelectedItem()));

		btnSaveCode.addActionListener(e -> MTGControler.getInstance().setProperty("currencylayer-access-api",txtCurrencyFieldApiCode.getText()));
		
		btnUpdateCurrency.addActionListener(ae -> {
			try {
				MTGControler.getInstance().getCurrencyService().clean();
				MTGControler.getInstance().getCurrencyService().init();
			} catch (IOException e) {
				logger.error(e);
			}

		});
		
		btnAdd.addActionListener(ae -> {
			try {
				InstallCert.installCert(txtWebSiteCertificate.getText());
			} catch (Exception e) {
				MTGControler.getInstance()
						.notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(), e));
			}
		});

		btnWebsiteSave.addActionListener(ae -> MTGControler.getInstance().setProperty("default-website-dir", txtdirWebsite.getText()));

		btnSavePrice.addActionListener(ae -> MTGControler.getInstance().setProperty("min-price-alert", txtMinPrice.getText()));

		
		
		
		
		btnSaveProfilGame.addActionListener(ae -> {
			MTGControler.getInstance().setProperty("/game/player-profil/name", txtName.getText());
			MTGControler.getInstance().setProperty("/game/cards/card-width",
					(int) gamePicsResizerPanel.getDimension().getWidth());
			MTGControler.getInstance().setProperty("/game/cards/card-height",
					(int) gamePicsResizerPanel.getDimension().getHeight());
			resizerPanel.setValue(0);
			GamePanelGUI.getInstance().getHandPanel().setSize(gamePicsResizerPanel.getDimension());
		});	
		
		btnClean.addActionListener(ae -> {

			try {
				loading(true, MTGControler.getInstance().getLangService().getCapitalize("CLEAN"));
				IconSetProvider.getInstance().clean();
				MTGControler.getInstance().getEnabled(MTGPicturesCache.class).clear();
				loading(false, "");
			} catch (Exception e) {
				logger.error(e);
				loading(false, "");
			}
		});
		
		btnIndexation.addActionListener(ae ->
			ThreadManager.getInstance().execute(() -> {
				try {
					loading(true, "Indexation");
					btnIndexation.setEnabled(false);
					MTGControler.getInstance().getEnabled(MTGCardsIndexer.class).initIndex();
					lblIndexSize.setText(UITools.formatDate(MTGControler.getInstance().getEnabled(MTGCardsIndexer.class).getIndexDate()));
				} catch (Exception e) {
					logger.error("error indexation", e);
					MTGControler.getInstance()
							.notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(), e));
				} finally {
					loading(false, "");
					btnIndexation.setEnabled(true);
				}
			}, "Indexation")
		);
		
		btnDuplicate.addActionListener(ae -> ThreadManager.getInstance().execute(() -> {
			try {
				MTGDao dao = (MTGDao) cboTargetDAO.getSelectedItem();
				dao.init();

				loading(true, MTGControler.getInstance().getLangService().getCapitalize("DUPLICATE_TO",
						MTGControler.getInstance().getEnabled(MTGDao.class)) + " " + dao);

				MTGControler.getInstance().getEnabled(MTGDao.class).duplicateTo(dao);

				loading(false, "");
			} catch (Exception e) {
				loading(false, "");
				logger.error(e);
			}
		}, "duplicate " + MTGControler.getInstance().getEnabled(MTGDao.class) + " to " + cboTargetDAO.getSelectedItem())

		);
		
		btnSaveLoglevel.addActionListener(ae -> {
			if (chckbxIconset.isSelected())
				MTGControler.getInstance().setProperty("loglevel", (Level) cboLogLevels.getSelectedItem());
			MTGLogger.changeLevel((Level) cboLogLevels.getSelectedItem());
		});

		cboLogLevels.addActionListener(ae -> MTGLogger.changeLevel((Level) cboLogLevels.getSelectedItem()));

		btnSaveDefaultLandDeck.addActionListener(ae -> MTGControler.getInstance().setProperty("default-land-deck",
				((MagicEdition) cboEditionLands.getSelectedItem()).getId()));

		btnSaveDefaultLib.addActionListener(ae -> {
			try {

				MTGControler.getInstance().setProperty(DEFAULT_LIBRARY,
						(MagicCollection) cboCollections.getSelectedItem());
			} catch (Exception e) {
				logger.error(e);
			}
		});
		
		chkboxAutoAdd.addActionListener(e -> MTGControler.getInstance().setProperty("collections/stockAutoAdd",String.valueOf(chkboxAutoAdd.isSelected())));
		chkboxAutoDelete.addActionListener(e -> MTGControler.getInstance().setProperty("collections/stockAutoDelete",String.valueOf(chkboxAutoDelete.isSelected())));
		
		
		btnDefaultStock.addActionListener(ae -> {
			DefaultStockEditorDialog diag = new DefaultStockEditorDialog();
			diag.setMagicCardStock(MTGControler.getInstance().getDefaultStock());
			diag.setVisible(true);

		});

		lblIconAvatar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent paramMouseEvent) {
				JFileChooser jf = new JFileChooser();
				jf.setFileFilter(new FileNameExtensionFilter("Images", "bmp", "gif", "jpg", "jpeg", "png"));
				int result = jf.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					MTGControler.getInstance().setProperty("/game/player-profil/avatar",
							jf.getSelectedFile().getAbsolutePath());
					loadIcon();
				}
			}
		});
		
		
		btnBackup.addActionListener(ae ->

		ThreadManager.getInstance().execute(() -> {
			try {
				loading(true, "backup db " + MTGControler.getInstance().getEnabled(MTGDao.class) + " database");
				MTGControler.getInstance().getEnabled(MTGDao.class).backup(new File(textField.getText()));
				loading(false, "");

			} catch (Exception e1) {
				logger.error(e1);
			}
		}, "backup " + MTGControler.getInstance().getEnabled(MTGDao.class) + " database"));
		
		lclCodeCurrency.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI(MTGConstants.CURRENCY_API));
				} catch (Exception e1) {
					logger.error(e1);
				} 
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				lclCodeCurrency.setCursor(new Cursor(Cursor.HAND_CURSOR));
				
			}
			@Override
			public void mouseExited(MouseEvent e) {
				lclCodeCurrency.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			
		});

		if (MTGControler.getInstance().get(LANGAGE) != null) {
			cboLanguages.setSelectedItem(MTGControler.getInstance().get(LANGAGE));
		}
		if (MTGControler.getInstance().get(DEFAULT_LIBRARY) != null) {
			cboCollections.setSelectedItem(new MagicCollection(MTGControler.getInstance().get(DEFAULT_LIBRARY)));
		}
		
		
		for (int i = 0; i < cboLogLevels.getItemCount(); i++) {
			if (cboLogLevels.getItemAt(i).toString().equals(MTGControler.getInstance().get("loglevel")))
				cboLogLevels.setSelectedIndex(i);

		}
		try {
			for (MagicEdition col : MTGControler.getInstance().getEnabled(MTGCardsProvider.class).loadEditions()) {
				if (col.getId().equalsIgnoreCase(MTGControler.getInstance().get("default-land-deck"))) {
					cboEditionLands.setSelectedItem(col);
				}
			}

		} catch (Exception e1) {
			logger.error(e1);
		}
	}
	
	

	private void loadIcon() {
		try {
			lblIconAvatar.setIcon(new ImageIcon(ImageTools.resize(
					ImageIO.read(new File(MTGControler.getInstance().get("/game/player-profil/avatar"))), 100, 100)));

		} catch (Exception e) {
			lblIconAvatar.setIcon(null);
		}

	}

}

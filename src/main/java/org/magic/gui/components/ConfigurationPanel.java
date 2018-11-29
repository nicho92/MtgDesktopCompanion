package org.magic.gui.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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
	private JTextField txtCurrencyFieldApiCode;

	public void loading(boolean show, String text) {
		if (show) {
			lblLoading.start();
			lblLoading.setText(text);
		} else {
			lblLoading.end();
		}
	}
	
	
	public ConfigurationPanel() {

		cboTargetDAO = UITools.createCombobox(MTGDao.class, true);
		cboTargetDAO.removeItem(MTGControler.getInstance().getEnabled(MTGDao.class));

		
		cboEditionLands = UITools.createComboboxEditions();
		lblLoading = AbstractBuzyIndicatorComponent.createLabelComponent();

		JPanel panelDAO = new JPanel();
		panelDAO.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true),
				MTGControler.getInstance().getLangService().getCapitalize("DATABASES"), TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(64, 64, 64)));
		
		
		JPanel panelConfig = new JPanel();
		panelConfig.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true),
				MTGControler.getInstance().getLangService().getCapitalize("CONFIGURATION"), TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(64, 64, 64)));
		
		JPanel panelWebSite = new JPanel();
		panelWebSite.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 1, true),
				MTGControler.getInstance().getLangService().getCapitalize("WEBSITE"), TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(64, 64, 64)));
		

/////////////CONFIG PANEL BOX		
		
		GridBagLayout mainPanelLayout = new GridBagLayout();
					mainPanelLayout.columnWidths = new int[] { 396, 212, 0 };
					mainPanelLayout.rowHeights = new int[] { 179, 0, 0, 0, 0 };
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
					
					
		setLayout(mainPanelLayout);
		panelDAO.setLayout(daoPanelLayout);
		panelConfig.setLayout(configPanelLayout);
		panelWebSite.setLayout(websitePanelLayout);
		
		
		add(panelDAO, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
		add(panelConfig, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 1, 0));
		add(panelWebSite, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 1));
		
		
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

		
		
		panelDAO.add(lblBackupDao, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, new Insets(0, 0, 5, 5), 0, 0));
		panelDAO.add(textField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 1, 0));
		panelDAO.add(btnBackup, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 2, 0));
		panelDAO.add(lblDuplicateDb, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, new Insets(0, 0, 5, 5), 0, 1));
		panelDAO.add(cboTargetDAO, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 1, 1));
		panelDAO.add(btnDuplicate, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 2, 1));
		panelDAO.add(lblLocation, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, new Insets(0, 0, 5, 5), 0, 2));
		panelDAO.add(lbldbLocationValue, UITools.createGridBagConstraints(null, null, new Insets(0, 0, 5, 5), 1, 2));
		panelDAO.add(lblSize, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, new Insets(0, 0, 5, 5), 0, 3));
		panelDAO.add(lblSizeValue, UITools.createGridBagConstraints(null, null, new Insets(0, 0, 5, 5), 1, 3));
		panelDAO.add(lblIndexation, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, new Insets(0, 0, 0, 5), 0, 4));
		panelDAO.add(lblIndexSize, UITools.createGridBagConstraints(null, null, new Insets(0, 0, 0, 5), 1, 4));
		panelDAO.add(btnIndexation, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 2, 4));
		
		
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
		
		((FlowLayout) panelAutoStock.getLayout()).setAlignment(FlowLayout.LEFT);
		
		
		chckbxIconset.setSelected(true);
		chckbxIconcards.setSelected(true);
		chkboxAutoAdd.setSelected(MTGControler.getInstance().get("collections/stockAutoAdd").equals("true"));
		chkboxAutoDelete.setSelected(MTGControler.getInstance().get("collections/stockAutoDelete").equals("true"));
		
		
		panelCheckCache.add(chckbxIconset);
		panelCheckCache.add(chckbxIconcards);
		panelAutoStock.add(chkboxAutoAdd);
		panelAutoStock.add(chkboxAutoDelete);

		
		panelConfig.add(lblMainCol, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, new Insets(0, 0, 5, 5), 0, 0));
		panelConfig.add(cboCollections, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 1, 0));
		panelConfig.add(btnSaveDefaultLib, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 2, 0));
		panelConfig.add(lblDefaultLandManuel, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, new Insets(0, 0, 5, 5), 0, 1));
		panelConfig.add(cboEditionLands, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 1, 1));
		panelConfig.add(btnSaveDefaultLandDeck, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 2, 1));
		panelConfig.add(lblLogLevel, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, new Insets(0, 0, 5, 5), 0, 2));
		panelConfig.add(cboLogLevels, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 1, 2));
		panelConfig.add(btnSaveLoglevel, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 2, 2));
		panelConfig.add(lblDontTakeAlert, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, new Insets(0, 0, 5, 5), 0, 3));
		panelConfig.add(txtMinPrice, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 1, 3));
		panelConfig.add(btnSavePrice, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 2, 3));
		panelConfig.add(lblCleancache, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, new Insets(0, 0, 5, 5), 0, 4));
		panelConfig.add(panelCheckCache, UITools.createGridBagConstraints(GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0, 0, 5, 5), 1, 4));
		panelConfig.add(btnClean, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 2, 4));
		panelConfig.add(lblAutoStock, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, new Insets(0, 0, 0, 5), 0, 5));
		panelConfig.add(panelAutoStock, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 1, 5));
		panelConfig.add(btnDefaultStock, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 2, 5));

		

		

/////////////WEBSITE BOX		
	
		JLabel lblWebsiteDir = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("DIRECTORY") + " :");
		txtdirWebsite = new JTextField(MTGControler.getInstance().get("default-website-dir"),10);
		JButton btnWebsiteSave = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SAVE"));
		JLabel lblAddWebsiteCertificate = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("ADD_CERTIFICATE") + " :");
		txtWebSiteCertificate = new JTextField("www.",10);
		JButton btnAdd = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SAVE"));
		
		
		panelWebSite.add(lblWebsiteDir, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));
		panelWebSite.add(txtdirWebsite, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 1, 0));
		panelWebSite.add(btnWebsiteSave, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 3, 0));
		panelWebSite.add(lblAddWebsiteCertificate, UITools.createGridBagConstraints(null, null, new Insets(0, 0, 5, 5), 0, 1));
		panelWebSite.add(txtWebSiteCertificate, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 1, 1));
		panelWebSite.add(btnAdd, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 3, 1));

		
		
/////////////PROFIL BOX		
		
		JPanel panelProfil = new JPanel();
		panelProfil.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true),
				MTGControler.getInstance().getLangService().getCapitalize("GAME"), TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(64, 64, 64)));
		GridBagConstraints gbcpanelProfil = new GridBagConstraints();
		gbcpanelProfil.insets = new Insets(0, 0, 5, 5);
		gbcpanelProfil.fill = GridBagConstraints.BOTH;
		gbcpanelProfil.gridx = 0;
		gbcpanelProfil.gridy = 2;
		add(panelProfil, gbcpanelProfil);
		GridBagLayout gblpanelProfil = new GridBagLayout();
		gblpanelProfil.columnWidths = new int[] { 0, 71, 0, 0 };
		gblpanelProfil.rowHeights = new int[] { 0, 0, 29, 0, 0 };
		gblpanelProfil.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gblpanelProfil.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelProfil.setLayout(gblpanelProfil);

		JLabel lblName = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("NAME") + " :");
		GridBagConstraints gbclblName = new GridBagConstraints();
		gbclblName.anchor = GridBagConstraints.EAST;
		gbclblName.insets = new Insets(0, 0, 5, 5);
		gbclblName.gridx = 0;
		gbclblName.gridy = 0;
		panelProfil.add(lblName, gbclblName);

		txtName = new JTextField(MTGControler.getInstance().get("/game/player-profil/name"));
		GridBagConstraints gbctxtName = new GridBagConstraints();
		gbctxtName.gridwidth = 2;
		gbctxtName.insets = new Insets(0, 0, 5, 0);
		gbctxtName.fill = GridBagConstraints.HORIZONTAL;
		gbctxtName.gridx = 1;
		gbctxtName.gridy = 0;
		panelProfil.add(txtName, gbctxtName);
		txtName.setColumns(10);

		JLabel lblAvatar = new JLabel("Avatar :");
		lblIconAvatar = new JLabel();
		GridBagConstraints gbclblAvatar = new GridBagConstraints();
		gbclblAvatar.insets = new Insets(0, 0, 5, 5);
		gbclblAvatar.gridx = 0;
		gbclblAvatar.gridy = 1;
		panelProfil.add(lblAvatar, gbclblAvatar);

		loadIcon();

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

		lblIconAvatar.setBorder(new LineBorder(Color.RED, 1, true));
		GridBagConstraints gbclblIconAvatar = new GridBagConstraints();
		gbclblIconAvatar.fill = GridBagConstraints.BOTH;
		gbclblIconAvatar.gridheight = 3;
		gbclblIconAvatar.insets = new Insets(0, 0, 0, 5);
		gbclblIconAvatar.gridx = 1;
		gbclblIconAvatar.gridy = 1;
		panelProfil.add(lblIconAvatar, gbclblIconAvatar);

		JPanel panelSubGame = new JPanel();
		GridBagConstraints gbcpanelSubGame = new GridBagConstraints();
		gbcpanelSubGame.gridheight = 2;
		gbcpanelSubGame.insets = new Insets(0, 0, 5, 0);
		gbcpanelSubGame.fill = GridBagConstraints.BOTH;
		gbcpanelSubGame.gridx = 2;
		gbcpanelSubGame.gridy = 1;
		panelProfil.add(panelSubGame, gbcpanelSubGame);
		panelSubGame.setLayout(new GridLayout(3, 2, 0, 0));

		JResizerPanel gamePicsResizerPanel = new JResizerPanel(MTGControler.getInstance().getCardsGameDimension());
		panelSubGame.add(gamePicsResizerPanel);

		JButton btnSave2 = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SAVE"));
		btnSave2.addActionListener(ae -> {
			MTGControler.getInstance().setProperty("/game/player-profil/name", txtName.getText());
			MTGControler.getInstance().setProperty("/game/cards/card-width",
					(int) gamePicsResizerPanel.getDimension().getWidth());
			MTGControler.getInstance().setProperty("/game/cards/card-height",
					(int) gamePicsResizerPanel.getDimension().getHeight());
			resizerPanel.setValue(0);
			GamePanelGUI.getInstance().getHandPanel().setSize(gamePicsResizerPanel.getDimension());
		});

		GridBagConstraints gbcbtnSave2 = new GridBagConstraints();
		gbcbtnSave2.gridx = 2;
		gbcbtnSave2.gridy = 3;
		panelProfil.add(btnSave2, gbcbtnSave2);

		JPanel panelModule = new JPanel();
		panelModule.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "Modules",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(64, 64, 64)));
		GridBagConstraints gbcpanelModule = new GridBagConstraints();
		gbcpanelModule.insets = new Insets(0, 0, 5, 0);
		gbcpanelModule.fill = GridBagConstraints.BOTH;
		gbcpanelModule.gridx = 1;
		gbcpanelModule.gridy = 2;
		add(panelModule, gbcpanelModule);
		GridBagLayout gblPanelModule = new GridBagLayout();
		gblPanelModule.columnWidths = new int[] { 0, 0, 0, 103, 0, 121, 0, 0 };
		gblPanelModule.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gblPanelModule.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gblPanelModule.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelModule.setLayout(gblPanelModule);

		chckbxSearch = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("SEARCH_MODULE"));
		chckbxSearch.setSelected(MTGControler.getInstance().get("modules/search").equals("true"));
		chckbxSearch.addItemListener(
				ie -> MTGControler.getInstance().setProperty("modules/search", chckbxSearch.isSelected()));
		GridBagConstraints gbcchckbxSearch = new GridBagConstraints();
		gbcchckbxSearch.anchor = GridBagConstraints.WEST;
		gbcchckbxSearch.insets = new Insets(0, 0, 5, 5);
		gbcchckbxSearch.gridx = 1;
		gbcchckbxSearch.gridy = 0;
		panelModule.add(chckbxSearch, gbcchckbxSearch);

		chckbxCollection = new JCheckBox("Collection");
		chckbxCollection.setSelected(MTGControler.getInstance().get("modules/collection").equals("true"));
		chckbxCollection.addItemListener(
				ie -> MTGControler.getInstance().setProperty("modules/collection", chckbxCollection.isSelected()));

		GridBagConstraints gbcchckbxCollection = new GridBagConstraints();
		gbcchckbxCollection.anchor = GridBagConstraints.WEST;
		gbcchckbxCollection.insets = new Insets(0, 0, 5, 5);
		gbcchckbxCollection.gridx = 3;
		gbcchckbxCollection.gridy = 0;
		panelModule.add(chckbxCollection, gbcchckbxCollection);

		chckbxDashboard = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("DASHBOARD_MODULE"));
		chckbxDashboard.setSelected(MTGControler.getInstance().get("modules/dashboard").equals("true"));
		chckbxDashboard.addItemListener(
				ie -> MTGControler.getInstance().setProperty("modules/dashboard", chckbxDashboard.isSelected()));

		chckbxStock = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("STOCK_MODULE"));
		chckbxStock.setSelected(MTGControler.getInstance().get("modules/stock").equals("true"));

		chckbxStock.addItemListener(
				ie -> MTGControler.getInstance().setProperty("modules/stock", chckbxStock.isSelected()));
		GridBagConstraints gbcchckbxStock = new GridBagConstraints();
		gbcchckbxStock.insets = new Insets(0, 0, 5, 5);
		gbcchckbxStock.anchor = GridBagConstraints.WEST;
		gbcchckbxStock.gridx = 5;
		gbcchckbxStock.gridy = 0;
		panelModule.add(chckbxStock, gbcchckbxStock);

		chckbxAlert = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("ALERT_MODULE"));
		chckbxAlert.setSelected(MTGControler.getInstance().get("modules/alarm").equals("true"));
		chckbxAlert.addItemListener(
				ie -> MTGControler.getInstance().setProperty("modules/alarm", chckbxAlert.isSelected()));

		GridBagConstraints gbcchckbxAlert = new GridBagConstraints();
		gbcchckbxAlert.anchor = GridBagConstraints.WEST;
		gbcchckbxAlert.insets = new Insets(0, 0, 5, 0);
		gbcchckbxAlert.gridx = 6;
		gbcchckbxAlert.gridy = 0;
		panelModule.add(chckbxAlert, gbcchckbxAlert);
		GridBagConstraints gbcchckbxDashboard = new GridBagConstraints();
		gbcchckbxDashboard.anchor = GridBagConstraints.WEST;
		gbcchckbxDashboard.insets = new Insets(0, 0, 5, 5);
		gbcchckbxDashboard.gridx = 1;
		gbcchckbxDashboard.gridy = 1;
		panelModule.add(chckbxDashboard, gbcchckbxDashboard);

		chckbxGame = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("GAME_MODULE"));
		chckbxGame.setSelected(MTGControler.getInstance().get("modules/game").equals("true"));
		chckbxGame
				.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/game", chckbxGame.isSelected()));
		GridBagConstraints gbcchckbxGame = new GridBagConstraints();
		gbcchckbxGame.anchor = GridBagConstraints.WEST;
		gbcchckbxGame.insets = new Insets(0, 0, 5, 5);
		gbcchckbxGame.gridx = 3;
		gbcchckbxGame.gridy = 1;
		panelModule.add(chckbxGame, gbcchckbxGame);

		chckbxDeckBuilder = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("DECK_MODULE"));
		chckbxDeckBuilder.setSelected(MTGControler.getInstance().get("modules/deckbuilder").equals("true"));
		chckbxDeckBuilder.addItemListener(
				ie -> MTGControler.getInstance().setProperty("modules/deckbuilder", chckbxDeckBuilder.isSelected()));

		chckbxRss = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("RSS_MODULE"));
		chckbxRss.setSelected(MTGControler.getInstance().get("modules/rss").equals("true"));
		chckbxRss.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/rss", chckbxRss.isSelected()));

		GridBagConstraints gbcchckbxRss = new GridBagConstraints();
		gbcchckbxRss.anchor = GridBagConstraints.WEST;
		gbcchckbxRss.insets = new Insets(0, 0, 5, 5);
		gbcchckbxRss.gridx = 5;
		gbcchckbxRss.gridy = 1;
		panelModule.add(chckbxRss, gbcchckbxRss);

		JCheckBox chckbxWallpaper = new JCheckBox(
				MTGControler.getInstance().getLangService().getCapitalize("WALLPAPER"));
		chckbxWallpaper.setSelected(MTGControler.getInstance().get("modules/wallpaper").equals("true"));
		chckbxWallpaper.addItemListener(
				ie -> MTGControler.getInstance().setProperty("modules/wallpaper", chckbxWallpaper.isSelected()));

		GridBagConstraints gbcchckbxWallpaper = new GridBagConstraints();
		gbcchckbxWallpaper.insets = new Insets(0, 0, 5, 0);
		gbcchckbxWallpaper.anchor = GridBagConstraints.WEST;
		gbcchckbxWallpaper.gridx = 6;
		gbcchckbxWallpaper.gridy = 1;
		panelModule.add(chckbxWallpaper, gbcchckbxWallpaper);
		GridBagConstraints gbcchckbxDeckBuilder = new GridBagConstraints();
		gbcchckbxDeckBuilder.anchor = GridBagConstraints.WEST;
		gbcchckbxDeckBuilder.insets = new Insets(0, 0, 5, 5);
		gbcchckbxDeckBuilder.gridx = 1;
		gbcchckbxDeckBuilder.gridy = 2;
		panelModule.add(chckbxDeckBuilder, gbcchckbxDeckBuilder);

		chckbxShopper = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("SHOPPING_MODULE"));
		chckbxShopper.setSelected(MTGControler.getInstance().get("modules/shopper").equals("true"));
		chckbxShopper.addItemListener(
				ie -> MTGControler.getInstance().setProperty("modules/shopper", chckbxShopper.isSelected()));
		GridBagConstraints gbcchckbxShopper = new GridBagConstraints();
		gbcchckbxShopper.anchor = GridBagConstraints.WEST;
		gbcchckbxShopper.insets = new Insets(0, 0, 5, 5);
		gbcchckbxShopper.gridx = 3;
		gbcchckbxShopper.gridy = 2;
		panelModule.add(chckbxShopper, gbcchckbxShopper);

		JCheckBox chckbxHistory = new JCheckBox(
				MTGControler.getInstance().getLangService().getCapitalize("HISTORY_MODULE"));
		chckbxHistory.setSelected(MTGControler.getInstance().get("modules/history").equals("true"));
		chckbxHistory.addItemListener(
				ie -> MTGControler.getInstance().setProperty("modules/history", chckbxHistory.isSelected()));
		GridBagConstraints gbcchckbxHistory = new GridBagConstraints();
		gbcchckbxHistory.anchor = GridBagConstraints.WEST;
		gbcchckbxHistory.insets = new Insets(0, 0, 5, 5);
		gbcchckbxHistory.gridx = 5;
		gbcchckbxHistory.gridy = 2;
		panelModule.add(chckbxHistory, gbcchckbxHistory);

		chckbxCardBuilder = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("BUILDER_MODULE"));
		chckbxCardBuilder.setSelected(MTGControler.getInstance().get("modules/cardbuilder").equals("true"));
		chckbxCardBuilder.addItemListener(
				ie -> MTGControler.getInstance().setProperty("modules/cardbuilder", chckbxCardBuilder.isSelected()));
		GridBagConstraints gbcchckbxCardBuilder = new GridBagConstraints();
		gbcchckbxCardBuilder.insets = new Insets(0, 0, 5, 0);
		gbcchckbxCardBuilder.anchor = GridBagConstraints.WEST;
		gbcchckbxCardBuilder.gridx = 6;
		gbcchckbxCardBuilder.gridy = 2;
		panelModule.add(chckbxCardBuilder, gbcchckbxCardBuilder);

		GridBagConstraints gbclblLoading = new GridBagConstraints();
		gbclblLoading.gridwidth = 2;
		gbclblLoading.gridx = 0;
		gbclblLoading.gridy = 4;
		add(lblLoading, gbclblLoading);

		JPanel panelCurrency = new JPanel();
		GridBagLayout gblpanelCurrency = new GridBagLayout();
		gblpanelCurrency.columnWidths = new int[] { 106, 67, 0, 0 };
		gblpanelCurrency.rowHeights = new int[] { 23, 0, 0, 0 };
		gblpanelCurrency.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gblpanelCurrency.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelCurrency.setLayout(gblpanelCurrency);
		panelCurrency.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true),
				MTGControler.getInstance().getLangService().getCapitalize("CURRENCY"), TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(64, 64, 64)));
		GridBagConstraints gbcpanelCurrency = new GridBagConstraints();
		gbcpanelCurrency.insets = new Insets(0, 0, 0, 5);
		gbcpanelCurrency.fill = GridBagConstraints.BOTH;
		gbcpanelCurrency.gridx = 0;
		gbcpanelCurrency.gridy = 3;
		add(panelCurrency, gbcpanelCurrency);

		JLabel lblCurrency = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CURRENCY") + " :");
		GridBagConstraints gbclblCurrency = new GridBagConstraints();
		gbclblCurrency.anchor = GridBagConstraints.WEST;
		gbclblCurrency.insets = new Insets(0, 0, 5, 5);
		gbclblCurrency.gridx = 0;
		gbclblCurrency.gridy = 0;
		panelCurrency.add(lblCurrency, gbclblCurrency);

		JComboBox<Currency> cboCurrency = UITools.createCombobox(new ArrayList<>(Currency.getAvailableCurrencies()));
		GridBagConstraints gbccboCurrency = new GridBagConstraints();
		gbccboCurrency.fill = GridBagConstraints.HORIZONTAL;
		gbccboCurrency.insets = new Insets(0, 0, 5, 5);
		gbccboCurrency.gridx = 1;
		gbccboCurrency.gridy = 0;
		panelCurrency.add(cboCurrency, gbccboCurrency);

		if (MTGControler.getInstance().get(CURRENCY).isEmpty())
			cboCurrency.setSelectedItem(Currency.getInstance(Locale.getDefault()));
		else
			cboCurrency.setSelectedItem(Currency.getInstance(MTGControler.getInstance().get(CURRENCY)));

		JButton btnSavecurrency = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SAVE"));
		GridBagConstraints gbcbtnSavecurrency = new GridBagConstraints();
		gbcbtnSavecurrency.fill = GridBagConstraints.HORIZONTAL;
		gbcbtnSavecurrency.insets = new Insets(0, 0, 5, 0);
		gbcbtnSavecurrency.gridx = 2;
		gbcbtnSavecurrency.gridy = 0;
		panelCurrency.add(btnSavecurrency, gbcbtnSavecurrency);

		JLabel lclCodeCurrency = new JLabel("CurrencyLayer API code :");
		
		GridBagConstraints gbclclCodeCurrency = new GridBagConstraints();
		gbclclCodeCurrency.insets = new Insets(0, 0, 5, 5);
		gbclclCodeCurrency.anchor = GridBagConstraints.WEST;
		gbclclCodeCurrency.gridx = 0;
		gbclclCodeCurrency.gridy = 1;
		panelCurrency.add(lclCodeCurrency, gbclclCodeCurrency);

		txtCurrencyFieldApiCode = new JTextField(MTGControler.getInstance().get("/currencylayer-access-api"));
		GridBagConstraints gbctextField1 = new GridBagConstraints();
		gbctextField1.insets = new Insets(0, 0, 5, 5);
		gbctextField1.fill = GridBagConstraints.HORIZONTAL;
		gbctextField1.gridx = 1;
		gbctextField1.gridy = 1;
		panelCurrency.add(txtCurrencyFieldApiCode, gbctextField1);
		txtCurrencyFieldApiCode.setColumns(10);

		JButton btnSaveCode = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SAVE"));
		btnSaveCode.addActionListener(e -> MTGControler.getInstance().setProperty("currencylayer-access-api",
				txtCurrencyFieldApiCode.getText()));
		GridBagConstraints gbcbtnSaveCode = new GridBagConstraints();
		gbcbtnSaveCode.insets = new Insets(0, 0, 5, 0);
		gbcbtnSaveCode.gridx = 2;
		gbcbtnSaveCode.gridy = 1;
		panelCurrency.add(btnSaveCode, gbcbtnSaveCode);

		JButton btnUpdateCurrency = new JButton("Update Currency");
		btnUpdateCurrency.addActionListener(ae -> {
			try {
				MTGControler.getInstance().getCurrencyService().clean();
				MTGControler.getInstance().getCurrencyService().init();
			} catch (IOException e) {
				logger.error(e);
			}

		});
		GridBagConstraints gbcbtnUpdateCurrency = new GridBagConstraints();
		gbcbtnUpdateCurrency.fill = GridBagConstraints.HORIZONTAL;
		gbcbtnUpdateCurrency.insets = new Insets(0, 0, 0, 5);
		gbcbtnUpdateCurrency.gridx = 1;
		gbcbtnUpdateCurrency.gridy = 2;
		panelCurrency.add(btnUpdateCurrency, gbcbtnUpdateCurrency);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "GUI", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(64, 64, 64)));
		GridBagConstraints gbcpanel = new GridBagConstraints();
		gbcpanel.fill = GridBagConstraints.BOTH;
		gbcpanel.gridx = 1;
		gbcpanel.gridy = 3;
		add(panel, gbcpanel);
		GridBagLayout gblpanel = new GridBagLayout();
		gblpanel.columnWidths = new int[] { 188, 38, 0, 0 };
		gblpanel.rowHeights = new int[] { 23, 0, 0, 0, 0, 0, 0, 0 };
		gblpanel.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0 };
		gblpanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gblpanel);

		JLabel lblGuiLocal = new JLabel(
				MTGControler.getInstance().getLangService().getCapitalize("LOCALISATION") + " :");
		GridBagConstraints gbclblGuiLocal = new GridBagConstraints();
		gbclblGuiLocal.anchor = GridBagConstraints.WEST;
		gbclblGuiLocal.insets = new Insets(0, 0, 5, 5);
		gbclblGuiLocal.gridx = 0;
		gbclblGuiLocal.gridy = 0;
		panel.add(lblGuiLocal, gbclblGuiLocal);

		JComboBox<Locale> cboLocales = UITools.createCombobox(MTGControler.getInstance().getLangService().getAvailableLocale());
		GridBagConstraints gbccboLocales = new GridBagConstraints();
		gbccboLocales.fill = GridBagConstraints.HORIZONTAL;
		gbccboLocales.insets = new Insets(0, 0, 5, 5);
		gbccboLocales.gridx = 1;
		gbccboLocales.gridy = 0;
		panel.add(cboLocales, gbccboLocales);
		cboLocales.setSelectedItem(MTGControler.getInstance().getLocale());
		JButton btnSave = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SAVE"));
		GridBagConstraints gbcbtnSave3 = new GridBagConstraints();
		gbcbtnSave3.fill = GridBagConstraints.HORIZONTAL;
		gbcbtnSave3.insets = new Insets(0, 0, 5, 5);
		gbcbtnSave3.gridx = 2;
		gbcbtnSave3.gridy = 0;
		panel.add(btnSave, gbcbtnSave3);
		btnSave.addActionListener(ae -> MTGControler.getInstance().setProperty("locale", cboLocales.getSelectedItem()));
		
				JLabel lblCardsLanguage = new JLabel(
						MTGControler.getInstance().getLangService().getCapitalize("CARDS_LANGUAGE") + " :");
				GridBagConstraints gbclblCardsLanguage = new GridBagConstraints();
				gbclblCardsLanguage.anchor = GridBagConstraints.WEST;
				gbclblCardsLanguage.insets = new Insets(0, 0, 5, 5);
				gbclblCardsLanguage.gridx = 0;
				gbclblCardsLanguage.gridy = 1;
				panel.add(lblCardsLanguage, gbclblCardsLanguage);
				
						JComboBox<String> cboLanguages = UITools.createCombobox(MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getLanguages());
						GridBagConstraints gbccboLanguages = new GridBagConstraints();
						gbccboLanguages.fill = GridBagConstraints.HORIZONTAL;
						gbccboLanguages.insets = new Insets(0, 0, 5, 5);
						gbccboLanguages.gridx = 1;
						gbccboLanguages.gridy = 1;
						panel.add(cboLanguages, gbccboLanguages);
		
				JButton btnSavelang = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SAVE"));
				GridBagConstraints gbcbtnSavelang = new GridBagConstraints();
				gbcbtnSavelang.insets = new Insets(0, 0, 5, 5);
				gbcbtnSavelang.gridx = 2;
				gbcbtnSavelang.gridy = 1;
				panel.add(btnSavelang, gbcbtnSavelang);
				btnSavelang.addActionListener(
						ae -> MTGControler.getInstance().setProperty(LANGAGE, cboLanguages.getSelectedItem().toString()));

		JLabel lblLook = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("LOOK") + " :");
		GridBagConstraints gbclblLook = new GridBagConstraints();
		gbclblLook.anchor = GridBagConstraints.WEST;
		gbclblLook.insets = new Insets(0, 0, 5, 5);
		gbclblLook.gridx = 0;
		gbclblLook.gridy = 2;
		panel.add(lblLook, gbclblLook);
		
				JComboBox<LookAndFeelInfo> cboLook = UITools.createCombobox(MTGControler.getInstance().getLafService().getAllLookAndFeel());
				GridBagConstraints gbccboLook = new GridBagConstraints();
				gbccboLook.insets = new Insets(0, 0, 5, 5);
				gbccboLook.fill= GridBagConstraints.HORIZONTAL;
				gbccboLook.gridx = 1;
				gbccboLook.gridy = 2;
				panel.add(cboLook, gbccboLook);
				cboLook.addActionListener(ae -> MTGControler.getInstance().getLafService().setLookAndFeel(
						SwingUtilities.getAncestorOfClass(JFrame.class, this), (LookAndFeelInfo) cboLook.getSelectedItem(),
						true));

		JLabel lblPicsSize = new JLabel(
				MTGControler.getInstance().getLangService().getCapitalize("THUMBNAIL_SIZE") + ": ");
		GridBagConstraints gbclblPicsSize = new GridBagConstraints();
		gbclblPicsSize.anchor = GridBagConstraints.WEST;
		gbclblPicsSize.insets = new Insets(0, 0, 5, 5);
		gbclblPicsSize.gridx = 0;
		gbclblPicsSize.gridy = 3;
		panel.add(lblPicsSize, gbclblPicsSize);
		
				resizerPanel = new JResizerPanel(MTGControler.getInstance().getPictureProviderDimension());
				GridBagConstraints gbcresizerPanel = new GridBagConstraints();
				gbcresizerPanel.fill = GridBagConstraints.HORIZONTAL;
				gbcresizerPanel.insets = new Insets(0, 0, 5, 5);
				gbcresizerPanel.gridx = 1;
				gbcresizerPanel.gridy = 3;
				panel.add(resizerPanel, gbcresizerPanel);

		JButton btnSavePicSize = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SAVE"));
		GridBagConstraints gbcbtnSavePicSize = new GridBagConstraints();
		gbcbtnSavePicSize.fill = GridBagConstraints.HORIZONTAL;
		gbcbtnSavePicSize.insets = new Insets(0, 0, 5, 5);
		gbcbtnSavePicSize.gridx = 2;
		gbcbtnSavePicSize.gridy = 3;
		panel.add(btnSavePicSize, gbcbtnSavePicSize);
		btnSavePicSize.addActionListener(ae -> {
			MTGControler.getInstance().setProperty("/card-pictures-dimension/width",
					(int) resizerPanel.getDimension().getWidth());
			MTGControler.getInstance().setProperty("/card-pictures-dimension/height",
					(int) resizerPanel.getDimension().getHeight());
			resizerPanel.setValue(0);
			MTGControler.getInstance().getEnabled(MTGPictureProvider.class).setSize(resizerPanel.getDimension());
		});

		JLabel lblShowJsonPanel = new JLabel(
				MTGControler.getInstance().getLangService().getCapitalize("SHOW_JSON_PANEL") + " :");
		GridBagConstraints gbclblShowJsonPanel = new GridBagConstraints();
		gbclblShowJsonPanel.anchor = GridBagConstraints.WEST;
		gbclblShowJsonPanel.insets = new Insets(0, 0, 5, 5);
		gbclblShowJsonPanel.gridx = 0;
		gbclblShowJsonPanel.gridy = 4;
		panel.add(lblShowJsonPanel, gbclblShowJsonPanel);
		
				cbojsonView = new JCheckBox();
				GridBagConstraints gbccbojsonView = new GridBagConstraints();
				gbccbojsonView.fill = GridBagConstraints.HORIZONTAL;
				gbccbojsonView.insets = new Insets(0, 0, 5, 5);
				gbccbojsonView.gridx = 1;
				gbccbojsonView.gridy = 4;
				panel.add(cbojsonView, gbccbojsonView);
				cbojsonView.setSelected(MTGControler.getInstance().get("debug-json-panel").equals("true"));
		
				JLabel lblShowTooltip = new JLabel(
						MTGControler.getInstance().getLangService().getCapitalize("SHOW_TOOLTIP") + " :");
				GridBagConstraints gbclblShowTooltip = new GridBagConstraints();
				gbclblShowTooltip.anchor = GridBagConstraints.WEST;
				gbclblShowTooltip.insets = new Insets(0, 0, 5, 5);
				gbclblShowTooltip.gridx = 0;
				gbclblShowTooltip.gridy = 5;
				panel.add(lblShowTooltip, gbclblShowTooltip);
		
				chkToolTip = new JCheckBox("");
				GridBagConstraints gbcchkToolTip = new GridBagConstraints();
				gbcchkToolTip.fill = GridBagConstraints.HORIZONTAL;
				gbcchkToolTip.insets = new Insets(0, 0, 5, 5);
				gbcchkToolTip.gridx = 1;
				gbcchkToolTip.gridy = 5;
				panel.add(chkToolTip, gbcchkToolTip);
				chkToolTip.setSelected(MTGControler.getInstance().get("tooltip").equals("true"));
				chkToolTip.addItemListener(ie -> MTGControler.getInstance().setProperty("tooltip", chkToolTip.isSelected()));

		JLabel lblToolPosition = new JLabel("Position :");
		GridBagConstraints gbclblToolPosition = new GridBagConstraints();
		gbclblToolPosition.anchor = GridBagConstraints.WEST;
		gbclblToolPosition.insets = new Insets(0, 0, 0, 5);
		gbclblToolPosition.gridx = 0;
		gbclblToolPosition.gridy = 6;
		panel.add(lblToolPosition, gbclblToolPosition);
		
				JComboBox<String> cboToolPosition = UITools.createCombobox(new String[] { "TOP", "LEFT", "RIGHT", "BOTTOM" });
				GridBagConstraints gbccboToolPosition = new GridBagConstraints();
				gbccboToolPosition.fill = GridBagConstraints.HORIZONTAL;
				gbccboToolPosition.insets = new Insets(0, 0, 0, 5);
				gbccboToolPosition.gridx = 1;
				gbccboToolPosition.gridy = 6;
				panel.add(cboToolPosition, gbccboToolPosition);
				cboToolPosition.setSelectedItem(MTGControler.getInstance().get("ui/moduleTabPosition", "LEFT"));
	
				
		cboToolPosition.addItemListener(ie -> {
			if (ie.getStateChange() == ItemEvent.SELECTED)
				MTGControler.getInstance().setProperty("ui/moduleTabPosition",
						cboToolPosition.getSelectedItem().toString());

		});
		
		cbojsonView.addItemListener(ae -> MTGControler.getInstance().setProperty("debug-json-panel", cbojsonView.isSelected()));
		
		btnSavecurrency.addActionListener(ae -> MTGControler.getInstance().setProperty(CURRENCY, cboCurrency.getSelectedItem()));

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

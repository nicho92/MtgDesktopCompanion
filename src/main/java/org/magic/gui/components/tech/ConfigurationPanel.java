package org.magic.gui.components.tech;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager2;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.painter.MattePainter;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.beans.MagicCollection;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGPictureCache;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGServer;
import org.magic.api.interfaces.abstracts.extra.AbstractWebServer;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.dialog.DefaultStockEditorDialog;
import org.magic.gui.components.widgets.JFontChooser;
import org.magic.gui.components.widgets.JLangLabel;
import org.magic.gui.components.widgets.JResizerPanel;
import org.magic.gui.components.widgets.JTextFieldFileChooser;
import org.magic.gui.models.MapTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.magic.services.providers.IconSetProvider;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class ConfigurationPanel extends JXTaskPaneContainer {

	private static final String EXPORT = "EXPORT";
	private static final String LANGAGE = "langage";
	private static final String DEFAULT_LIBRARY = "default-library";
	private static final String CURRENCY = "currency";
	private static final long serialVersionUID = 1L;
	private JComboBox<MTGDao> cboTargetDAO;
	private JComboBox<MagicCollection> cboCollections;
	private JComboBox<Level> cboLogLevels;
	private JTextField txtMinPrice;
	private JCheckBox cbojsonView;
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
	private JCheckBox chckbxAlert;
	private JCheckBox chckbxRss;
	private JCheckBox chckbxCardBuilder;
	private JCheckBox chckbxStock;
	private JLabel dateCurrencyCache;
	private JResizerPanel resizerPanel;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private JCheckBox chckbxWallpaper;
	private JCheckBox chckbxSealed;
	private JComboBox<MTGServer> cboServers;
	private JCheckBox chckbxShopping;
	private JCheckBox chckbxAnnounce;
	private JCheckBox chckbxNetwork;

	public void loading(boolean show, String text) {
		if (show) 
		{
			lblLoading.start();
			lblLoading.setText(text);
		} else {
			lblLoading.end();
		}
	}

	private JPanel createBoxPanel(String keyName, Icon ic, LayoutManager2 layout,boolean collapsed)
	{
		var pane = new JXTaskPane();
		pane.setTitle(capitalize(keyName));
		pane.setIcon(ic);
		pane.setCollapsed(collapsed);
		pane.setLayout(layout);
		return pane;
	}



	public ConfigurationPanel() {
		lblLoading = AbstractBuzyIndicatorComponent.createLabelComponent();

		setBackgroundPainter(new MattePainter(MTGConstants.PICTURE_PAINTER, true));

/////////////CONFIG PANEL BOX


					var daoPanelLayout = new GridBagLayout();
					daoPanelLayout.columnWidths = new int[] { 172, 130, 0, 0 };
					daoPanelLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
					daoPanelLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
					daoPanelLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };

					var configPanelLayout = new GridBagLayout();
					configPanelLayout.columnWidths = new int[] { 0, 0, 0, 0 };
					configPanelLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
					configPanelLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
					configPanelLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };

					var websitePanelLayout = new GridBagLayout();
					websitePanelLayout.columnWidths = new int[] { 0, 0, 0, 0, 0 };
					websitePanelLayout.rowHeights = new int[] { 0, 0, 0 };
					websitePanelLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
					websitePanelLayout.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };

					var gameProfilPanelLayout = new GridBagLayout();
					gameProfilPanelLayout.columnWidths = new int[] { 0, 71, 0, 0 };
					gameProfilPanelLayout.rowHeights = new int[] { 0, 0, 29, 0, 0 };
					gameProfilPanelLayout.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
					gameProfilPanelLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };

					var modulesPanelLayout = new GridBagLayout();
					modulesPanelLayout.columnWidths = new int[] { 0, 0, 0, 103, 0, 121, 0, 0 };
					modulesPanelLayout.rowHeights = new int[] { 0, 0, 0, 0,  };
					modulesPanelLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
					modulesPanelLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };

					var currencyPanelLayout = new GridBagLayout();
					currencyPanelLayout.columnWidths = new int[] { 106, 67, 0, 0 };
					currencyPanelLayout.rowHeights = new int[] { 23, 0, 0, 0 };
					currencyPanelLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
					currencyPanelLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };

					var guiPanelLayout = new GridBagLayout();
					guiPanelLayout.columnWidths = new int[] { 188, 38, 0, 0 };
					guiPanelLayout.rowHeights = new int[] { 23, 0, 0, 0, 0, 0, 0, 0 };
					guiPanelLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0 };
					guiPanelLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
					
					var networkPanelLayout = new GridBagLayout();
					networkPanelLayout.columnWidths = new int[] { 188, 38, 0, 0 };
					networkPanelLayout.rowHeights = new int[] { 23, 0, 0, 0, 0, 0, 0, 0 };
					networkPanelLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0 };
					networkPanelLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };

		JPanel panelDAO = createBoxPanel("DATABASES",MTGConstants.ICON_TAB_DAO,daoPanelLayout,true);
		JPanel panelConfig = createBoxPanel("CONFIGURATION",MTGConstants.ICON_TAB_ADMIN,configPanelLayout,false);
		JPanel panelLogs = createBoxPanel("LOG",MTGConstants.ICON_TAB_RULES,new BorderLayout(),true);
		JPanel panelWebSite = createBoxPanel("WEBSITE",MTGConstants.ICON_WEBSITE_24,websitePanelLayout,true);
		JPanel panelGameProfil = createBoxPanel("GAME",MTGConstants.ICON_TAB_GAME,gameProfilPanelLayout,true);
		JPanel panelModule = createBoxPanel("Modules",MTGConstants.ICON_TAB_PLUGIN,modulesPanelLayout,true);
		JPanel panelCurrency = createBoxPanel("CURRENCY",MTGConstants.ICON_TAB_PRICES,currencyPanelLayout,true);
		JPanel panelGUI = createBoxPanel("GUI",MTGConstants.ICON_TAB_PICTURE,guiPanelLayout,true);
		JPanel panelAccounts = createBoxPanel("ACCOUNTS",MTGConstants.ICON_TAB_LOCK,new BorderLayout(),true);
		JPanel panelNetworks = createBoxPanel("NETWORKS",MTGConstants.ICON_TAB_NETWORK,networkPanelLayout,true);


		add(panelConfig);
		add(panelGUI);
		add(panelModule);
		add(panelAccounts);
		add(panelDAO);
		add(panelNetworks);
		add(panelWebSite);
		add(panelGameProfil);
		add(panelCurrency);
		add(panelLogs);


		var gbclblLoading = UITools.createGridBagConstraints(null, GridBagConstraints.BOTH,  0, 4);
		gbclblLoading.gridwidth = 2;
		add(lblLoading,gbclblLoading);



////////////ACCOUNT BOX
		panelAccounts.add(new MTGAuthenticatorEditor(),BorderLayout.CENTER);



////////////LOG BOX
		cboLogLevels = UITools.createCombobox(MTGLogger.getLevels());

		var panelMainLogger = new JPanel();
			  panelMainLogger.add(new JLangLabel("LOG_LEVEL"));
			  panelMainLogger.add(cboLogLevels);

			var model = new MapTableModel<String,Level>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void setValueAt(Object aValue, int row, int column) {
				logger.info("{} change to {}",keys.get(row).getKey(),aValue);
				MTGLogger.changeLevel(MTGLogger.getLogger(keys.get(row).getKey()),aValue.toString());
				keys.get(row).setValue(Level.toLevel(aValue.toString()));
			}
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if(columnIndex==1)
					return Level.class;

				return super.getColumnClass(columnIndex);
			}

		};

		model.setWritable(true);

		for( var g : MTGLogger.getLoggers().stream().filter(l->!l.getName().startsWith("org.magic.")).toList())
			model.addRow(g.getName(), g.getLevel());

		var tableLoggers = UITools.createNewTable(model);
		UITools.initTableFilter(tableLoggers);
		panelLogs.add(panelMainLogger,BorderLayout.NORTH);
		panelLogs.add(new JScrollPane(tableLoggers),BorderLayout.CENTER);


/////////////DAO BOX
		var lblDuplicateDb = new JLabel(capitalize("DUPLICATE_TO",getEnabledPlugin(MTGDao.class)));
		var btnDuplicate = new JButton((capitalize(EXPORT)));
		var lblLocation = new JLangLabel("LOCATION",true);
		var lbldbLocationValue = new JLabel(getEnabledPlugin(MTGDao.class).getDBLocation());
		var lblSize = new JLangLabel("SIZE",true);
		var lblSizeValue  = new JLabel();
		var lblIndexation = new JLabel("Indexation : ");
		var lblIndexSize = new JLabel(UITools.formatDate(getEnabledPlugin(MTGCardsIndexer.class).getIndexDate()));
		var btnIndexation = new JButton("Reindexation");
		cboTargetDAO = UITools.createComboboxPlugins(MTGDao.class, true);
		cboTargetDAO.removeItem(getEnabledPlugin(MTGDao.class));

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


		var sw = new SwingWorker<String, Void>()
				{
						@Override
						protected String doInBackground() throws Exception {
							return UITools.humanReadableSize(getEnabledPlugin(MTGDao.class).getDBSize().values().stream().mapToLong(Long::longValue).sum());
						}

						@Override
						protected void done() {
							try {
								lblSizeValue.setText(get());
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
							} catch (ExecutionException e) {
								logger.error(e);
							}

						}
				};
		ThreadManager.getInstance().runInEdt(sw, "getting DB size");

/////////////CONFIG BOX
		cboCollections = UITools.createComboboxCollection();

		txtMinPrice = new JTextField(MTGControler.getInstance().get("min-price-alert"),25);
		var btnSavePrice = new JButton(capitalize("SAVE"));
		var lblCleancache = new JLabel(capitalize("CLEAN_CACHE") + " :");
		var btnClean = new JButton(capitalize("CLEAN"));
		var panelCheckCache = new JPanel();
		var lblAutoStock = new JLabel(capitalize("AUTO_STOCK") + ": ");
		var chkboxAutoAdd = new JCheckBox(capitalize("AUTO_STOCK_ADD"));
		chckbxIconset = new JCheckBox(capitalize("IMG_SET"));
		chckbxIconcards = new JCheckBox(capitalize("IMG_CARD"));
		
		var chkboxAutoDelete = new JCheckBox(capitalize("AUTO_STOCK_DELETE"));
		var btnDefaultStock = new JButton("Default Stock");
		var chkboxPrerelease = new JCheckBox();
		var chkTechnicalLog = new JCheckBox();
		
		var panelAutoStock = new JPanel();
		var panelBtnConfigBackup = new JPanel();
		var btnExportConfig = new JButton(capitalize(EXPORT));
		var btnImportConfig = new JButton(capitalize("IMPORT"));


		((FlowLayout) panelAutoStock.getLayout()).setAlignment(FlowLayout.LEFT);


		chckbxIconset.setSelected(true);
		chckbxIconcards.setSelected(true);
		

		chkboxPrerelease.getModel().setSelected( MTG.readPropertyAsBoolean("notifyPrerelease"));
		chkboxAutoAdd.setSelected(MTG.readPropertyAsBoolean("collections/stockAutoAdd"));
		chkboxAutoDelete.setSelected(MTG.readPropertyAsBoolean("collections/stockAutoDelete"));
		chkTechnicalLog.setSelected(MTG.readPropertyAsBoolean("technical-log"));
		
		
		panelCheckCache.add(chckbxIconset);
		panelCheckCache.add(chckbxIconcards);

		panelAutoStock.add(chkboxAutoAdd);
		panelAutoStock.add(chkboxAutoDelete);

		panelBtnConfigBackup.add(btnExportConfig);
		panelBtnConfigBackup.add(btnImportConfig);

		panelConfig.add(new JLangLabel("MAIN_COLLECTION",true), UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 0));
		panelConfig.add(cboCollections, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 0));
		panelConfig.add(new JLangLabel("SHOW_LOW_PRICES",true), UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 3));
		panelConfig.add(txtMinPrice, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 3));
		panelConfig.add(btnSavePrice, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 2, 3));
		panelConfig.add(lblCleancache, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 4));
		panelConfig.add(panelCheckCache, UITools.createGridBagConstraints(GridBagConstraints.WEST, GridBagConstraints.VERTICAL,  1, 4));
		panelConfig.add(btnClean, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 2, 4));
		panelConfig.add(lblAutoStock, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, 0, 5));
		panelConfig.add(panelAutoStock, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 5));
		panelConfig.add(btnDefaultStock, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 2, 5));
		panelConfig.add(new JLangLabel("CONFIG_BACKUP",true), UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 6));
		panelConfig.add(panelBtnConfigBackup, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 6));

		panelConfig.add(new JLangLabel("UPDATE_PRERELEASE",true), UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 7));
		panelConfig.add(chkboxPrerelease, UITools.createGridBagConstraints(null, GridBagConstraints.WEST, 1, 7));

		panelConfig.add(new JLangLabel("TECHNICAL_SERVICE_LOG",true), UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 8));
		panelConfig.add(chkTechnicalLog, UITools.createGridBagConstraints(null, GridBagConstraints.WEST, 1, 8));

	



/////////////WEBSITE BOX

		
		var txtdirWebsserver = new JTextFieldFileChooser(10,JFileChooser.DIRECTORIES_ONLY);
		
		var btnWebServerExport = new JButton(capitalize(EXPORT));
		cboServers = UITools.createCombobox(MTG.listEnabledPlugins(MTGServer.class).stream().filter(AbstractWebServer.class::isInstance).toList());

		panelWebSite.add(new JLangLabel("WEB_SERVER_UI",true), UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  0, 2));
		panelWebSite.add(cboServers, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  2, 2));
		panelWebSite.add(txtdirWebsserver, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 2));
		panelWebSite.add(btnWebServerExport, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  3, 2));


/////////////NETWORK BOX
		var chkOnlineValidation = new JCheckBox();
		chkOnlineValidation.setSelected(MTG.readPropertyAsBoolean("network-config/online-query"));
		panelNetworks.add(new JLangLabel("NETWORK_VALIDATION",true), UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 1));
		panelNetworks.add(chkOnlineValidation, UITools.createGridBagConstraints(null, GridBagConstraints.WEST, 1, 1));
		
		var chkOnlineAutoConnect = new JCheckBox();
		chkOnlineAutoConnect.setSelected(MTG.readPropertyAsBoolean("network-config/online-autoconnect"));
		panelNetworks.add(new JLangLabel("NETWORK_AUTOCONNECT",true), UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 2));
		panelNetworks.add(chkOnlineAutoConnect, UITools.createGridBagConstraints(null, GridBagConstraints.WEST, 1, 2));

/////////////PROFIL BOX

		var lblName = new JLangLabel("NAME",true);
		txtName = new JTextField(MTGControler.getInstance().get("/game/player-profil/name"),10);
		var lblAvatar = new JLabel("Avatar :");
		lblIconAvatar = new JLabel();
		lblIconAvatar.setBorder(new LineBorder(Color.RED, 1, true));
		var gamePicsResizerPanel = new JResizerPanel(MTGControler.getInstance().getCardsGameDimension());
		var btnSaveProfilGame = new JButton(capitalize("SAVE"));

		panelGameProfil.add(lblName, UITools.createGridBagConstraints(GridBagConstraints.EAST, null,  0, 0));
		panelGameProfil.add(txtName, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 0));
		panelGameProfil.add(lblAvatar, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  0, 1));
		panelGameProfil.add(lblIconAvatar, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH,  1, 1));
		panelGameProfil.add(gamePicsResizerPanel, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH,  2, 0));
		panelGameProfil.add(btnSaveProfilGame,UITools.createGridBagConstraints(null, GridBagConstraints.BOTH,  3, 0));

		loadIcon();

/////////////MODULES BOX

		chckbxSearch = new JCheckBox(capitalize("SEARCH_MODULE"));
		chckbxCollection = new JCheckBox("Collection");
		chckbxDashboard = new JCheckBox(capitalize("DASHBOARD_MODULE"));
		chckbxCardBuilder = new JCheckBox(capitalize("BUILDER_MODULE"));
		chckbxDashboard.setSelected(MTGControler.getInstance().get("modules/dashboard").equals("true"));
		chckbxSearch.setSelected(MTGControler.getInstance().get("modules/search").equals("true"));
		chckbxCollection.setSelected(MTGControler.getInstance().get("modules/collection").equals("true"));
		chckbxStock = new JCheckBox(capitalize("STOCK_MODULE"));
		chckbxAlert = new JCheckBox(capitalize("ALERT_MODULE"));
		chckbxGame = new JCheckBox(capitalize("GAME_MODULE"));
		chckbxDeckBuilder = new JCheckBox(capitalize("DECK_MODULE"));
		chckbxRss = new JCheckBox(capitalize("RSS_MODULE"));
		chckbxWallpaper = new JCheckBox(capitalize("WALLPAPER"));
		chckbxSealed = new JCheckBox(capitalize("PACKAGES"));
		chckbxShopping = new JCheckBox(capitalize("SHOP"));
		chckbxAnnounce=  new JCheckBox(capitalize("ANNOUNCE"));
		chckbxNetwork=  new JCheckBox(capitalize("NETWORK"));



		chckbxStock.setSelected(MTG.readPropertyAsBoolean("modules/stock"));
		chckbxAlert.setSelected(MTG.readPropertyAsBoolean("modules/alarm"));
		chckbxGame.setSelected(MTG.readPropertyAsBoolean("modules/game"));
		chckbxDeckBuilder.setSelected(MTG.readPropertyAsBoolean("modules/deckbuilder"));
		chckbxRss.setSelected(MTG.readPropertyAsBoolean("modules/rss"));
		chckbxWallpaper.setSelected(MTG.readPropertyAsBoolean("modules/wallpaper"));
		chckbxCardBuilder.setSelected(MTG.readPropertyAsBoolean("modules/cardbuilder"));
		chckbxSealed.setSelected(MTG.readPropertyAsBoolean("modules/sealed"));
		chckbxShopping.setSelected(MTG.readPropertyAsBoolean("modules/webshop"));
		chckbxAnnounce.setSelected(MTG.readPropertyAsBoolean("modules/announce"));
		chckbxNetwork.setSelected(MTG.readPropertyAsBoolean("modules/network"));

		chckbxDashboard.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/dashboard", chckbxDashboard.isSelected()));
		chckbxStock.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/stock", chckbxStock.isSelected()));
		chckbxAlert.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/alarm", chckbxAlert.isSelected()));
		chckbxGame.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/game", chckbxGame.isSelected()));
		chckbxDeckBuilder.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/deckbuilder", chckbxDeckBuilder.isSelected()));
		chckbxRss.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/rss", chckbxRss.isSelected()));
		chckbxWallpaper.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/wallpaper", chckbxWallpaper.isSelected()));
		chckbxCardBuilder.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/cardbuilder", chckbxCardBuilder.isSelected()));
		chckbxCollection.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/collection", chckbxCollection.isSelected()));
		chckbxSearch.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/search", chckbxSearch.isSelected()));
		chckbxSealed.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/sealed",chckbxSealed.isSelected()));
		chckbxShopping.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/webshop",chckbxShopping.isSelected()));
		chckbxAnnounce.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/announce",chckbxAnnounce.isSelected()));
		chckbxNetwork.addItemListener(ie -> MTGControler.getInstance().setProperty("modules/network",chckbxNetwork.isSelected()));

		panelModule.add(chckbxSearch, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  1, 0));
		panelModule.add(chckbxCollection, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  3, 0));
		panelModule.add(chckbxStock, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  5, 0));
		panelModule.add(chckbxAlert, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  6, 0));
		panelModule.add(chckbxDashboard, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  1, 1));
		panelModule.add(chckbxGame, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  3, 1));
		panelModule.add(chckbxRss, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  5, 1));
		panelModule.add(chckbxNetwork, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  5, 2));
		panelModule.add(chckbxWallpaper, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  6, 1));
		panelModule.add(chckbxDeckBuilder, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  1, 2));
		panelModule.add(chckbxCardBuilder, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  3, 2));
		panelModule.add(chckbxSealed, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  1, 3));
		panelModule.add(chckbxShopping, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  3, 3));
		panelModule.add(chckbxAnnounce, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  5, 3));



/////////////CURRENCY BOX

		var lblCurrency = new JLangLabel("CURRENCY",true);
		JComboBox<Currency> cboCurrency = UITools.createCombobox(new ArrayList<>(Currency.getAvailableCurrencies()));
		var lclCodeCurrency = new JLabel("CurrencyLayer API code :");
		var txtCurrencyFieldApiCode = new JTextField(MTGControler.getInstance().get("/currencylayer-access-api"),10);
		var btnSaveCode = new JButton(capitalize("SAVE"));
		var btnUpdateCurrency = new JButton("Update Currency");
		var chkEnablePriceConversion = new JCheckBox(capitalize("ENABLED"));
		dateCurrencyCache = new JLabel(UITools.formatDate(MTGControler.getInstance().getCurrencyService().getCurrencyDateCache()));

		if (MTGControler.getInstance().get(CURRENCY).isEmpty())
			cboCurrency.setSelectedItem(Currency.getInstance(Locale.getDefault()));
		else
			cboCurrency.setSelectedItem(Currency.getInstance(MTGControler.getInstance().get(CURRENCY)));

		panelCurrency.add(chkEnablePriceConversion, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 0));
		panelCurrency.add(lblCurrency, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  1, 0));
		panelCurrency.add(cboCurrency, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  2, 0));
		panelCurrency.add(lclCodeCurrency, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 1));
		panelCurrency.add(txtCurrencyFieldApiCode, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 1));
		panelCurrency.add(btnSaveCode, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  2, 1));
		panelCurrency.add(btnUpdateCurrency, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  2, 2));
		panelCurrency.add(dateCurrencyCache, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 2));



/////////////GUI BOX

		var lblGuiLocal = new JLabel(capitalize("LOCALISATION") + " :");
		JComboBox<Locale> cboLocales = UITools.createCombobox(MTGControler.getInstance().getLangService().getAvailableLocale());
		
		cboLocales.setRenderer((JList<? extends Locale> list, Locale value, int index, boolean isSelected, boolean cellHasFocus)->
			new JLabel(StringUtils.capitalize(value.getDisplayLanguage(MTGControler.getInstance().getLocale())))
		);


		var lblCardsLanguage = new JLangLabel("CARD_LANGUAGE",true);
		JComboBox<String> cboLanguages = UITools.createCombobox(getEnabledPlugin(MTGCardsProvider.class).getLanguages());
		var lblLook = new JLangLabel("LOOK",true);
		JComboBox<LookAndFeelInfo> cboLook = UITools.createCombobox(MTGControler.getInstance().getLafService().getAllLookAndFeel());
		var lblPicsSize = new JLangLabel("THUMBNAIL_SIZE",true);
		var btnSavePicSize = new JButton(capitalize("SAVE"));
		resizerPanel = new JResizerPanel(MTGControler.getInstance().getPictureProviderDimension());
		var lblShowJsonPanel = new JLangLabel("SHOW_OBJECT_PANEL",true);
		cbojsonView = new JCheckBox();
		var lblShowTooltip = new JLangLabel("SHOW_TOOLTIP",true);
		chkToolTip = new JCheckBox("");
		var lblToolPosition = new JLangLabel("TAB_POSITION",true);
		JComboBox<String> cboToolPosition = UITools.createCombobox(new String[] { "TOP", "LEFT", "RIGHT", "BOTTOM" });
		var lblFont = new JLangLabel("FONT",true);
		var chooseFontPanel = new JFontChooser();
		chooseFontPanel.initFont(MTGControler.getInstance().getFont());
		var btnSaveFont = new JButton(capitalize("SAVE"));
		var chkEnabledAutocomplete = new JCheckBox();
		var chkEnabledChrome = new JCheckBox();
		var btnShortKeys = new JButton(capitalize("SHORTKEYS"));

		cbojsonView.getModel().setSelected(MTG.readPropertyAsBoolean("debug-json-panel"));
		chkToolTip.getModel().setSelected(MTG.readPropertyAsBoolean("tooltip"));
		cboLocales.getModel().setSelectedItem(MTGControler.getInstance().getLocale());
		cboToolPosition.getModel().setSelectedItem(MTGControler.getInstance().get("ui/moduleTabPosition", "LEFT"));
		chkEnabledAutocomplete.getModel().setSelected( MTG.readPropertyAsBoolean("autocompletion"));
		chkEnabledChrome.getModel().setSelected( MTG.readPropertyAsBoolean("ui/chromedisabled"));
		chkEnablePriceConversion.getModel().setSelected(MTG.readPropertyAsBoolean("/currencylayer-converter-enable"));

		panelGUI.add(lblGuiLocal, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 0));
		panelGUI.add(cboLocales, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 0));
		panelGUI.add(lblCardsLanguage, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 1));
		panelGUI.add(cboLanguages, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 1));
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
		panelGUI.add(lblFont, UITools.createGridBagConstraints(GridBagConstraints.WEST, null,  0, 7));
		panelGUI.add(chooseFontPanel, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 7));
		panelGUI.add(btnSaveFont, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  2, 7));
		panelGUI.add(new JLabel(capitalize("ENABLE_AUTOCOMPLETION") + " :"), UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  0, 8));
		panelGUI.add(chkEnabledAutocomplete, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 8));
		panelGUI.add(new JLabel(capitalize("DISABLE_CHROME_RENDERING") + " :"), UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  0, 9));
		panelGUI.add(chkEnabledChrome, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  1, 9));

		panelGUI.add(new JLabel(capitalize("SHORTKEYS_CONFIGURATION") + " :"), UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  0, 10));
		panelGUI.add(btnShortKeys, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL,  2, 10));




/////////////EVENTS
		btnShortKeys.addActionListener(l->MTGUIComponent.createJDialog(new ShortKeyManagerUI(),true,false).setVisible(true));


		cboToolPosition.addItemListener(ie -> {
			if (ie.getStateChange() == ItemEvent.SELECTED)
				MTGControler.getInstance().setProperty("ui/moduleTabPosition",
						cboToolPosition.getSelectedItem().toString());
		});

		cboCurrency.addItemListener(ie -> {
			if (ie.getStateChange() == ItemEvent.SELECTED)
				MTGControler.getInstance().setProperty(CURRENCY, cboCurrency.getSelectedItem());
		});


		cboLocales.addItemListener(ie -> {
			if (ie.getStateChange() == ItemEvent.SELECTED)
				MTGControler.getInstance().setProperty("locale", cboLocales.getSelectedItem());
		});

		cboLanguages.addItemListener(ie -> {
			if (ie.getStateChange() == ItemEvent.SELECTED)
				MTGControler.getInstance().setProperty(LANGAGE, cboLanguages.getSelectedItem().toString());
		});

		cboLook.addItemListener(ie ->{
			if (ie.getStateChange() == ItemEvent.SELECTED)
				MTGControler.getInstance().getLafService().setLookAndFeel(SwingUtilities.getAncestorOfClass(JFrame.class, this), (LookAndFeelInfo) cboLook.getSelectedItem(),true);
		});

		cboLogLevels.addItemListener(ie -> {
			if (ie.getStateChange() == ItemEvent.SELECTED) {
				MTGControler.getInstance().setProperty("loglevel", cboLogLevels.getSelectedItem());
				MTGLogger.changeLevel((Level) cboLogLevels.getSelectedItem());
			}
		});

		cbojsonView.addItemListener(ae -> MTGControler.getInstance().setProperty("debug-json-panel", cbojsonView.isSelected()));


		btnExportConfig.addActionListener(ae->{
			try {
				var f =  new File(MTGConstants.DATA_DIR,"config.backup.zip");

				FileTools.extractConfig(f);

				MTGControler.getInstance().notify(new MTGNotification(EXPORT, "Export "+f, MESSAGE_TYPE.INFO));
			} catch (IOException e2) {
				MTGControler.getInstance().notify(e2);
			}
		});

		btnImportConfig.addActionListener(ae->{

			var chooser = new JFileChooser(MTGConstants.DATA_DIR);
				int res = chooser.showOpenDialog(null);
				chooser.setFileFilter(new FileFilter() {

					@Override
					public String getDescription() {
						return "Zip File";
					}

					@Override
					public boolean accept(File f) {
						return FilenameUtils.isExtension(f.getName(), "zip");
					}
				});

				if(res == JFileChooser.APPROVE_OPTION)
				{
					try {
						FileTools.unzip(chooser.getSelectedFile(),MTGConstants.CONF_DIR);
						MTGControler.getInstance().notify(new MTGNotification("IMPORT", "Import config done", MESSAGE_TYPE.INFO));
					} catch (IOException e1) {
						MTGControler.getInstance().notify(e1);
					}
				}
		});

		cboCollections.addItemListener(ie -> {
			if (ie.getStateChange() == ItemEvent.SELECTED)
				MTGControler.getInstance().setProperty(DEFAULT_LIBRARY,cboCollections.getSelectedItem());
		});


		btnSavePrice.addActionListener(ae -> MTGControler.getInstance().setProperty("min-price-alert", txtMinPrice.getText()));

		btnSavePicSize.addActionListener(ae -> {
			MTGControler.getInstance().setProperty("/card-pictures-dimension/width",(int) resizerPanel.getDimension().getWidth());
			MTGControler.getInstance().setProperty("/card-pictures-dimension/height",(int) resizerPanel.getDimension().getHeight());
			resizerPanel.setValue(0);
			getEnabledPlugin(MTGPictureProvider.class).setSize(resizerPanel.getDimension());
		});


		chkEnablePriceConversion.addItemListener(ie -> MTGControler.getInstance().setProperty("currencylayer-converter-enable", chkEnablePriceConversion.isSelected()));

		chkToolTip.addItemListener(ie -> MTGControler.getInstance().setProperty("tooltip", chkToolTip.isSelected()));
		chkEnabledAutocomplete.addItemListener(ie -> MTGControler.getInstance().setProperty("autocompletion", chkEnabledAutocomplete.isSelected()));
		chkEnabledChrome.addItemListener(ie -> MTGControler.getInstance().setProperty("ui/chromedisabled", chkEnabledChrome.isSelected()));
		chkboxPrerelease.addItemListener(ie -> MTGControler.getInstance().setProperty("notifyPrerelease", chkboxPrerelease.isSelected()));
		chkTechnicalLog.addItemListener(ie -> MTGControler.getInstance().setProperty("technical-log", chkTechnicalLog.isSelected()));
		chkOnlineValidation.addItemListener(ie -> MTGControler.getInstance().setProperty("network-config/online-query", chkOnlineValidation.isSelected()));
		chkOnlineAutoConnect.addItemListener(ie -> MTGControler.getInstance().setProperty("network-config/online-autoconnect", chkOnlineAutoConnect.isSelected()));

		btnSaveCode.addActionListener(e -> MTGControler.getInstance().setProperty("currencylayer-access-api",txtCurrencyFieldApiCode.getText()));
		btnUpdateCurrency.addActionListener(ae -> {
			try {
				MTGControler.getInstance().getCurrencyService().clean();
				MTGControler.getInstance().getCurrencyService().init();
				dateCurrencyCache.setText(UITools.formatDate(MTGControler.getInstance().getCurrencyService().getCurrencyDateCache()));
			} catch (IOException e) {
				logger.error(e);
			}

		});

		btnSaveProfilGame.addActionListener(ae -> {
			MTGControler.getInstance().setProperty("/game/player-profil/name", txtName.getText());
			MTGControler.getInstance().setProperty("/game/cards/card-width",
					(int) gamePicsResizerPanel.getDimension().getWidth());
			MTGControler.getInstance().setProperty("/game/cards/card-height",
					(int) gamePicsResizerPanel.getDimension().getHeight());
			resizerPanel.setValue(0);
			GamePanelGUI.getInstance().getHandPanel().setSize(gamePicsResizerPanel.getDimension());
		});


		btnSaveFont.addActionListener(ae -> {
			MTGControler.getInstance().setProperty("/ui/font/family", chooseFontPanel.getFont().getFamily());
			MTGControler.getInstance().setProperty("/ui/font/style",chooseFontPanel.getFont().getStyle());
			MTGControler.getInstance().setProperty("/ui/font/size",chooseFontPanel.getFont().getSize());
		});




		btnClean.addActionListener(ae -> {

			try {
				loading(true, capitalize("CLEAN"));

				if(chckbxIconset.isSelected())
					IconSetProvider.getInstance().clean();

				if(chckbxIconcards.isSelected())
					getEnabledPlugin(MTGPictureCache.class).clear();

				loading(false, "");
			} catch (Exception e) {
				logger.error(e);
				loading(false, "");
			}
		});

		btnIndexation.addActionListener(ae ->{

				loading(true, "Indexation");
				btnIndexation.setEnabled(false);
				var swIndex = new SwingWorker<Void, Void>()
				{
					@Override
					protected Void doInBackground() throws Exception {
						getEnabledPlugin(MTGCardsIndexer.class).initIndex(true);
						return null;
					}

					@Override
					protected void done()
					{
						try
						{
							get();
							lblIndexSize.setText(UITools.formatDate(getEnabledPlugin(MTGCardsIndexer.class).getIndexDate()));
						}
						catch(InterruptedException ex) {
							Thread.currentThread().interrupt();
						}
						catch (Exception e) {
							logger.error("error indexation", e);
							MTGControler.getInstance().notify(e);
						}
						finally {
							loading(false, "");
							btnIndexation.setEnabled(true);
						}
					}
				};

				ThreadManager.getInstance().runInEdt(swIndex,"Indexation");


		});

		btnDuplicate.addActionListener(ae ->{
			btnDuplicate.setEnabled(false);
			MTGDao dao = (MTGDao) cboTargetDAO.getSelectedItem();
			loading(true, capitalize("DUPLICATE_TO",getEnabledPlugin(MTGDao.class)) + " " + dao);
				var swDuplicate = new SwingWorker<Void, Void>()
						{
							@Override
							protected Void doInBackground() throws Exception {
								getEnabledPlugin(MTGDao.class).duplicateTo(dao);
								return null;
							}

							@Override
							protected void done()
							{
								try {
									get();
								}catch (InterruptedException e) {
									Thread.currentThread().interrupt();
								} catch (Exception e) {
									logger.error(e);
								}
								finally {
									loading(false, "");
									btnDuplicate.setEnabled(true);
								}
							}
						};
				ThreadManager.getInstance().runInEdt(swDuplicate, "duplicate " + getEnabledPlugin(MTGDao.class) + " to " + cboTargetDAO.getSelectedItem());
		}

		);


		chkboxAutoAdd.addActionListener(e -> MTGControler.getInstance().setProperty("collections/stockAutoAdd",String.valueOf(chkboxAutoAdd.isSelected())));
		chkboxAutoDelete.addActionListener(e -> MTGControler.getInstance().setProperty("collections/stockAutoDelete",String.valueOf(chkboxAutoDelete.isSelected())));


		btnDefaultStock.addActionListener(ae -> {
			var diag = new DefaultStockEditorDialog();
			diag.setMagicCardStock(MTGControler.getInstance().getDefaultStock());
			diag.setVisible(true);

		});

		lblIconAvatar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent paramMouseEvent) {
				var jf = new JFileChooser();
				jf.setFileFilter(new FileNameExtensionFilter("Images", "bmp", "gif", "jpg", "jpeg", "png"));
				int result = jf.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					MTGControler.getInstance().setProperty("/game/player-profil/avatar",
							jf.getSelectedFile().getAbsolutePath());
					loadIcon();
				}
			}
		});


		btnWebServerExport.addActionListener(ae->{
			try {
				((AbstractWebServer)cboServers.getSelectedItem()).exportWeb(txtdirWebsserver.getFile());
				MTGControler.getInstance().notify(new MTGNotification(EXPORT, "Export ok : " + txtdirWebsserver.getFile(), MESSAGE_TYPE.INFO));
			} catch (Exception e1) {

				logger.error("error copy ",e1);
				MTGControler.getInstance().notify(e1);
			}
		});


		lclCodeCurrency.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
					UITools.browse(MTGConstants.CURRENCY_API);
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


		for (var i = 0; i < cboLogLevels.getItemCount(); i++) {
			if (cboLogLevels.getItemAt(i).toString().equals(MTGControler.getInstance().get("loglevel")))
				cboLogLevels.setSelectedIndex(i);

		}

	}



	private void loadIcon() {
		try {
			lblIconAvatar.setIcon(new ImageIcon(MTGControler.getInstance().getProfilPlayer().getAvatar()));

		} catch (Exception e) {
			lblIconAvatar.setIcon(null);
		}

	}

}

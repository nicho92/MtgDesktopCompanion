package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;
import org.api.mkm.tools.MkmAPIConfig;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.notifiers.impl.OSTrayNotifier;
import org.magic.api.pricers.impl.MagicCardMarketPricer2;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.BinderTagsEditorComponent;
import org.magic.gui.components.CardSearchPanel;
import org.magic.gui.components.LoggerViewPanel;
import org.magic.gui.components.PackagesBrowserPanel;
import org.magic.gui.components.ScriptPanel;
import org.magic.gui.components.ThreadMonitor;
import org.magic.gui.components.dialog.AboutDialog;
import org.magic.gui.components.dialog.ChromeDownloader;
import org.magic.gui.components.dialog.TipsOfTheDayDialog;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.VersionChecker;
import org.magic.services.extra.GithubUtils;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.ShortKeyManager;
import org.mkm.gui.MkmPanel;

public class MagicGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private JTabbedPane tabbedPane;
	private transient VersionChecker serviceUpdate;
	private transient OSTrayNotifier osNotifier;

	public MagicGUI() {

		
		try {
			osNotifier = (OSTrayNotifier)MTGControler.getInstance().getPlugin("Tray",MTGNotifier.class);
		}
		catch(Exception e)
		{
			logger.error("error loading osNotifier",e);
			osNotifier=new OSTrayNotifier();
		}
		
		
		try {
			serviceUpdate = new VersionChecker();
			initGUI();
		} catch (Exception e) {
			logger.error("Error init GUI", e);
			MTGControler.getInstance().notify(e);
		}
		
		addTab(new ConfigurationPanelGUI());

		logger.debug("construction of GUI : done");
	}

	public void setSelectedTab(int id) {
		tabbedPane.setSelectedIndex(id);
	}

	private void initGUI() throws ClassNotFoundException, IOException, SQLException {
		JMenuBar mtgMnuBar;
		JMenu mnFile;
		JMenu mnuAbout;
		JMenuItem mntmExit;

		logger.info("init Main GUI");
		setSize(new Dimension(1420, 900));
		setTitle(MTGConstants.MTG_APP_NAME + " ( v" + serviceUpdate.getVersion() + ")");
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				MTGControler.getInstance().closeApp();
				
			}
		});
			
		
		
		setIconImage(MTGConstants.IMAGE_LOGO);
		getContentPane().setLayout(new BorderLayout());
	
		mtgMnuBar = new JMenuBar();
		setJMenuBar(mtgMnuBar);

		mnFile = new JMenu(MTGControler.getInstance().getLangService().getCapitalize("FILE"));
		mnuAbout = new JMenu("?");
		mntmExit = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("EXIT"),MTGConstants.ICON_EXIT);

		JMenuItem mntmHelp = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("READ_MANUAL"),MTGConstants.ICON_HELP);
		JMenuItem mntmThreadItem = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("THREADS"),MTGConstants.ICON_CONFIG);
		JMenuItem mntmLogsItem = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("LOGS"),MTGConstants.ICON_CONFIG);
		JMenuItem mntmAboutMagicDesktop = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("ABOUT"),new ImageIcon(MTGConstants.IMAGE_LOGO));
		JMenuItem mntmReportBug = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("REPORT_BUG"),MTGConstants.ICON_BUG);
		JMenuItem mntmFileTagEditor = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("BINDER_TAG_EDITOR"),MTGConstants.ICON_BINDERS);
		JMenuItem mntmFileChromePlugin = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("CHROME_PLUGIN"),MTGConstants.ICON_CHROME);
		JMenuItem mntmFilePackageExplorer = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("PACKAGES"),MTGConstants.ICON_PACKAGE);
		JMenuItem mntmFileScript = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("SCRIPT"),MTGConstants.ICON_SCRIPT);
		
		
		mtgMnuBar.add(mnFile);
		mnFile.add(mntmFileTagEditor);
		mnFile.add(mntmFileChromePlugin);
		mnFile.add(mntmFilePackageExplorer);
		mnFile.add(mntmFileScript);
		mnFile.add(mntmExit);
		mtgMnuBar.add(mnuAbout);
		mnuAbout.add(mntmThreadItem);
		mnuAbout.add(mntmLogsItem);
		mnuAbout.add(mntmHelp);
		mnuAbout.add(mntmAboutMagicDesktop);
		mnuAbout.add(mntmReportBug);
	
		
		mntmFileChromePlugin.addActionListener(ae->{
			ChromeDownloader dow = new ChromeDownloader();
			dow.setVisible(true);
		});
		
		mntmFilePackageExplorer.addActionListener(ae -> ThreadManager.getInstance().invokeLater(() -> {
			
			PackagesBrowserPanel pane = new PackagesBrowserPanel(true);
			pane.initTree();
			JDialog j = MTGUIComponent.createJDialog(pane, true, false);
			pane.setPreferredSize(new Dimension(1024, 768));
			j.setVisible(true);
			
		}));
		
		mntmFileScript.addActionListener(ae -> ThreadManager.getInstance().invokeLater(() -> MTGUIComponent.createJDialog(new ScriptPanel(), true, false).setVisible(true)));
		
		mntmFileTagEditor.addActionListener(ae -> ThreadManager.getInstance().invokeLater(() -> MTGUIComponent.createJDialog(new BinderTagsEditorComponent(), true, false).setVisible(true)));

		mntmLogsItem.addActionListener(ae -> ThreadManager.getInstance().invokeLater(() -> MTGUIComponent.createJDialog(new LoggerViewPanel(), true, false).setVisible(true)));

		mntmThreadItem.addActionListener(e ->

		ThreadManager.getInstance().invokeLater(() -> MTGUIComponent.createJDialog(new ThreadMonitor(), true, false).setVisible(true)));

		mntmExit.addActionListener(e -> MTGControler.getInstance().closeApp());

		mntmHelp.addActionListener(e -> {
			String url = MTGConstants.MTG_DESKTOP_WIKI_URL;
			try {
				Desktop.getDesktop().browse(new URI(url));
			} catch (Exception e1) {
				logger.error(e1);
			}
		});

		mntmAboutMagicDesktop.addActionListener(ae -> MTGUIComponent.createJDialog(new AboutDialog(), false,true).setVisible(true));

		mntmReportBug.addActionListener(ae -> {
			try {
				String url = MTGConstants.MTG_DESKTOP_ISSUES_URL;
				Desktop.getDesktop().browse(new URI(url));
			} catch (Exception e) {
				logger.error(e);
			}
		});
		
		boolean update=serviceUpdate.hasNewVersion();

		if (update) 
		{
			JMenuItem newversion = new JMenuItem(
					MTGControler.getInstance().getLangService().getCapitalize("DOWNLOAD_LAST_VERSION") + " : "
							+ serviceUpdate.getOnlineVersion());
			newversion.addActionListener(e -> {
				try {
					Desktop.getDesktop().browse(new URI(GithubUtils.inst().getReleaseURL()));
				} catch (Exception e1) {
					logger.error(e1.getMessage());
				}
			});
			mnuAbout.add(newversion);
		}
		
		
		String pos = MTGControler.getInstance().get("ui/moduleTabPosition");
		int position=0;
		switch(pos)
		{
		case "TOP": position = SwingConstants.TOP;break;
		case "LEFT": position = SwingConstants.LEFT;break;
		case "RIGHT": position = SwingConstants.RIGHT;break;
		case "BOTTOM": position = SwingConstants.BOTTOM;break;
		default : position=SwingConstants.LEFT;break;
		}

		tabbedPane = new JTabbedPane(position);

		
		
		
		
		if (MTGControler.getInstance().get("modules/search").equals("true"))
			addTab(CardSearchPanel.getInstance()); 

		if (MTGControler.getInstance().get("modules/collection").equals("true"))
			addTab(new CollectionPanelGUI()); 
		
		if (MTGControler.getInstance().get("modules/sealed").equals("true"))
			addTab(new SealedStockGUI());
		
		if (MTGControler.getInstance().get("modules/deckbuilder").equals("true"))
			addTab(new DeckBuilderGUI()); 

		if (MTGControler.getInstance().get("modules/game").equals("true"))
			addTab(new GameGUI()); 
	
		if (MTGControler.getInstance().get("modules/stock").equals("true"))
			addTab(new StockPanelGUI()); 

		if (MTGControler.getInstance().get("modules/dashboard").equals("true"))
			addTab(new DashBoardGUI2());

		if (MTGControler.getInstance().get("modules/shopper").equals("true"))
			addTab(new OrdersGUI());

		if (MTGControler.getInstance().get("modules/alarm").equals("true"))
			addTab(new AlarmGUI());

		if (MTGControler.getInstance().get("modules/cardbuilder").equals("true"))
			addTab(new CardBuilder2GUI());

		if (MTGControler.getInstance().get("modules/rss").equals("true"))
			addTab(new RssGUI());

		if (MTGControler.getInstance().get("modules/history").equals("true"))
			addTab(new StoriesGUI());

		if (MTGControler.getInstance().get("modules/wallpaper").equals("true"))
			addTab(new WallpaperGUI());

		if (MTGControler.getInstance().get("modules/mkm").equals("true"))
		{
			try {
			MkmAPIConfig.getInstance().init(new MagicCardMarketPricer2().getConfFile());
			}
			catch(Exception e)
			{
				logger.error(e);
			}
			addTab(MTGUIComponent.build(new MkmPanel(), "MKM", MTGConstants.ICON_SHOP));
		}

		
		
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		
			if(osNotifier!=null)
			{	
				osNotifier.getTrayNotifier().addActionListener(e -> setVisible(!isVisible()));
			
				PopupMenu menuTray = new PopupMenu();
				for (int index_tab = 0; index_tab < tabbedPane.getTabCount(); index_tab++) {
					final int index = index_tab;
					MenuItem it = new MenuItem(tabbedPane.getTitleAt(index_tab));
					it.addActionListener(e -> {
						setVisible(true);
						setSelectedTab(index);
					});
					menuTray.add(it);
				}
	
				osNotifier.getTrayNotifier().setPopupMenu(menuTray);
				osNotifier.getTrayNotifier().setToolTip(MTGConstants.MTG_APP_NAME);
		
				if (update)
				{
					String msg=(MTGControler.getInstance().getLangService().getCapitalize("NEW_VERSION") + " "
									+ serviceUpdate.getOnlineVersion() + " "
									+ MTGControler.getInstance().getLangService().get("AVAILABLE"));
					MTGNotification notif = new MTGNotification(getTitle(),msg,MESSAGE_TYPE.INFO);
					osNotifier.send(notif);
				}
			}
			
			ShortKeyManager.inst().load();

			
			ThreadManager.getInstance().invokeLater(() -> {
				try {
					new TipsOfTheDayDialog().shows();
				} catch (IOException e) {
					logger.error(e);
				}

			});
		}

	private void addTab(MTGUIComponent instance) {
		tabbedPane.addTab(instance.getTitle(),instance.getIcon(), instance, null);
		
	}
	

}

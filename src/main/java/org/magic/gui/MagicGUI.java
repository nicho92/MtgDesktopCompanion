package org.magic.gui;

import static org.magic.tools.MTG.capitalize;
import static org.magic.tools.MTG.getPlugin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.ImageIcon;
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
import org.magic.api.interfaces.abstracts.AbstractMTGPlugin;
import org.magic.api.notifiers.impl.OSTrayNotifier;
import org.magic.api.pricers.impl.MagicCardMarketPricer2;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.BinderTagsEditorComponent;
import org.magic.gui.components.CardSearchPanel;
import org.magic.gui.components.LoggerViewPanel;
import org.magic.gui.components.ScriptPanel;
import org.magic.gui.components.ThreadMonitor;
import org.magic.gui.components.dialog.AboutDialog;
import org.magic.gui.components.dialog.ChromeDownloader;
import org.magic.gui.components.dialog.TipsOfTheDayDialog;
import org.magic.services.AccountsManager;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ShortKeyManager;
import org.magic.services.VersionChecker;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.GithubUtils;
import org.magic.tools.UITools;
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
			osNotifier = (OSTrayNotifier)getPlugin("Tray",MTGNotifier.class);
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

		mnFile = new JMenu(capitalize("FILE"));
		mnuAbout = new JMenu("?");
		mntmExit = new JMenuItem(capitalize("EXIT"),MTGConstants.ICON_EXIT);

		var mntmHelp = new JMenuItem(capitalize("READ_MANUAL"),MTGConstants.ICON_HELP);
		var mntmDonate = new JMenuItem(capitalize("DONATE"),MTGConstants.ICON_EURO);
		var mntmThreadItem = new JMenuItem(capitalize("THREADS"),MTGConstants.ICON_CONFIG);
		var mntmLogsItem = new JMenuItem(capitalize("LOGS"),MTGConstants.ICON_TAB_RULES);
		var mntmAboutMagicDesktop = new JMenuItem(capitalize("ABOUT"),new ImageIcon(MTGConstants.IMAGE_LOGO));
		var mntmReportBug = new JMenuItem(capitalize("REPORT_BUG"),MTGConstants.ICON_BUG);
		var mntmFileTagEditor = new JMenuItem(capitalize("BINDER_TAG_EDITOR"),MTGConstants.ICON_BINDERS);
		var mntmFileChromePlugin = new JMenuItem(capitalize("CHROME_PLUGIN"),MTGConstants.ICON_CHROME);
		var mntmFileScript = new JMenuItem(capitalize("SCRIPT"),MTGConstants.ICON_SCRIPT);
		
		
		mtgMnuBar.add(mnFile);
		mnFile.add(mntmFileTagEditor);
		mnFile.add(mntmFileChromePlugin);
		mnFile.add(mntmFileScript);
		mnFile.add(mntmExit);
		mtgMnuBar.add(mnuAbout);
		mnuAbout.add(mntmThreadItem);
		mnuAbout.add(mntmLogsItem);
		mnuAbout.add(mntmHelp);
		mnuAbout.add(mntmDonate);
		mnuAbout.add(mntmAboutMagicDesktop);
		mnuAbout.add(mntmReportBug);
	
		
		mntmFileChromePlugin.addActionListener(ae->{
			var dow = new ChromeDownloader();
			dow.setVisible(true);
		});
		
		mntmFileScript.addActionListener(ae -> ThreadManager.getInstance().invokeLater(new MTGRunnable() {
			
			@Override
			protected void auditedRun() {
				MTGUIComponent.createJDialog(new ScriptPanel(), true, false).setVisible(true);
				
			}
		}, "loading Script dialog"));
		
		mntmFileTagEditor.addActionListener(ae -> ThreadManager.getInstance().invokeLater(new MTGRunnable() {
			
			@Override
			protected void auditedRun() {
				MTGUIComponent.createJDialog(new BinderTagsEditorComponent(), true, false).setVisible(true);
				
			}
		}, "loading Tags dialog"));

		mntmLogsItem.addActionListener(ae -> ThreadManager.getInstance().invokeLater(new MTGRunnable() {
			
			@Override
			protected void auditedRun() {
				MTGUIComponent.createJDialog(new LoggerViewPanel(), true, false).setVisible(true);
				
			}
		}, "loading Logs dialog"));

		mntmThreadItem.addActionListener(e ->ThreadManager.getInstance().invokeLater(new MTGRunnable() {
			
			@Override
			protected void auditedRun() {
				MTGUIComponent.createJDialog(new ThreadMonitor(), true, false).setVisible(true);
				
			}
		}, "loading Thread dialog"));

		mntmExit.addActionListener(e -> MTGControler.getInstance().closeApp());

		mntmHelp.addActionListener(e -> UITools.browse(MTGConstants.MTG_DESKTOP_WIKI_URL));
		
		mntmDonate.addActionListener(e -> UITools.browse(MTGConstants.MTG_DESKTOP_DONATION_URL));
		
		mntmAboutMagicDesktop.addActionListener(ae -> MTGUIComponent.createJDialog(new AboutDialog(), false,true).setVisible(true));

		mntmReportBug.addActionListener(ae -> UITools.browse(MTGConstants.MTG_DESKTOP_ISSUES_URL));
		
		boolean update=serviceUpdate.hasNewVersion();

		if (update) 
		{
			var newversion = new JMenuItem(
					capitalize("DOWNLOAD_LAST_VERSION") + " : "
							+ serviceUpdate.getOnlineVersion());
			newversion.addActionListener(e -> {
				try {
					UITools.browse(GithubUtils.inst().getReleaseURL());
				} catch (IOException e1) {
					logger.error(e1);
				}
			});
			mnuAbout.add(newversion);
		}
		
		
		String pos = MTGControler.getInstance().get("ui/moduleTabPosition");
		var position=0;
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
		
		if (MTGControler.getInstance().get("modules/deckbuilder").equals("true"))
			addTab(new DeckBuilderGUI()); 

		if (MTGControler.getInstance().get("modules/game").equals("true"))
			addTab(new GameGUI()); 
	
		if (MTGControler.getInstance().get("modules/stock").equals("true"))
			addTab(new StockPanelGUI()); 
		
		if (MTGControler.getInstance().get("modules/sealed").equals("true"))
			addTab(new SealedStockGUI());
		
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
		
		if (MTGControler.getInstance().get("modules/event").equals("true"))
			addTab(new EventManagerGUI());
		
		if (MTGControler.getInstance().get("modules/webshop").equals("true"))
			addTab(new ShopGUI());
		

		if (MTGControler.getInstance().get("modules/mkm").equals("true"))
		{
			try {
			
			MkmAPIConfig.getInstance().init(AccountsManager.inst().getAuthenticator(new MagicCardMarketPricer2()).getTokensAsProperties());
			}
			catch(Exception e)
			{
				logger.error(e);
			}
			addTab(MTGUIComponent.build(new MkmPanel(), "MKM", new ImageIcon(new ImageIcon(AbstractMTGPlugin.class.getResource("/icons/plugins/magicCardMarket.png")).getImage().getScaledInstance(MTGConstants.MENU_ICON_SIZE, MTGConstants.MENU_ICON_SIZE, Image.SCALE_SMOOTH))));
		}

		
		
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		
			if(osNotifier!=null)
			{	
				osNotifier.getTrayNotifier().addActionListener(e -> setVisible(!isVisible()));
			
				var menuTray = new PopupMenu();
				for (var index_tab = 0; index_tab < tabbedPane.getTabCount(); index_tab++) {
					final int index = index_tab;
					var it = new MenuItem(tabbedPane.getTitleAt(index_tab));
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
					String msg=(capitalize("NEW_VERSION") + " "
									+ serviceUpdate.getOnlineVersion() + " "
									+ MTGControler.getInstance().getLangService().get("AVAILABLE"));
					var notif = new MTGNotification(getTitle(),msg,MESSAGE_TYPE.INFO);
					osNotifier.send(notif);
				}
			}
			
			ShortKeyManager.inst().load();

			
			ThreadManager.getInstance().invokeLater(new MTGRunnable() {
				
				@Override
				protected void auditedRun() {
					try {
						new TipsOfTheDayDialog().shows();
					} catch (IOException e) {
						logger.error(e);
					}
					
				}
			}, "Loading TipsOfTheDay dialog");
		}

	private void addTab(MTGUIComponent instance) {
		tabbedPane.addTab(instance.getTitle(),instance.getIcon(), instance, null);
		
	}
	

}

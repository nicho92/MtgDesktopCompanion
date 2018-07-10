package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.notifiers.impl.OSTrayNotifier;
import org.magic.gui.components.CardSearchPanel;
import org.magic.gui.components.LoggerViewPanel;
import org.magic.gui.components.ThreadMonitorPanel;
import org.magic.gui.components.dialog.AboutDialog;
import org.magic.gui.components.dialog.TipsOfTheDayDialog;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;
import org.magic.services.VersionChecker;
import org.mkm.gui.MkmPanel;

public class MagicGUI extends JFrame {

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
			MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),e));
		}
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("CONFIGURATION"),
				MTGConstants.ICON_CONFIG, new ConfigurationPanelGUI(), null);

		logger.info("construction of GUI : done");
	}

	public void setSelectedTab(int id) {
		tabbedPane.setSelectedIndex(id);
	}

	public void initGUI() throws ClassNotFoundException, IOException, SQLException {
		JMenuBar mtgMnuBar;
		JMenu mnFile;
		JMenu mnuAbout;
		JMenuItem mntmExit;

		logger.info("init Main GUI");
		setSize(new Dimension(1420, 900));
		setTitle(MTGConstants.MTG_APP_NAME + " ( v" + serviceUpdate.getVersion() + ")");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(MTGConstants.IMAGE_LOGO);
		getContentPane().setLayout(new BorderLayout());
	
		mtgMnuBar = new JMenuBar();
		setJMenuBar(mtgMnuBar);

		mnFile = new JMenu(MTGControler.getInstance().getLangService().getCapitalize("FILE"));
		mnuAbout = new JMenu("?");

		mntmExit = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("EXIT"));

		JMenuItem mntmHelp = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("READ_MANUAL"));
		JMenuItem mntmThreadItem = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("THREADS"));
		JMenuItem mntmLogsItem = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("LOGS"));
		JMenuItem mntmAboutMagicDesktop = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("ABOUT"));
		JMenuItem mntmReportBug = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("REPORT_BUG"));
		JMenuItem mntmFileOpen = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("OPEN"));

		mtgMnuBar.add(mnFile);
		mnFile.add(mntmFileOpen);
		mnFile.add(mntmExit);
		mtgMnuBar.add(mnuAbout);
		mnuAbout.add(mntmThreadItem);
		mnuAbout.add(mntmLogsItem);
		mnuAbout.add(mntmHelp);
		mnuAbout.add(mntmAboutMagicDesktop);
		mnuAbout.add(mntmReportBug);

		mntmLogsItem.addActionListener(ae -> ThreadManager.getInstance().runInEdt(() -> {
			JFrame f = new JFrame(MTGControler.getInstance().getLangService().getCapitalize("LOGS"));
			f.getContentPane().add(new LoggerViewPanel());
			f.setLocationRelativeTo(null);
			f.setVisible(true);
			f.pack();
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}));

		mntmThreadItem.addActionListener(e ->

		ThreadManager.getInstance().runInEdt(() -> {
			JFrame f = new JFrame(MTGControler.getInstance().getLangService().getCapitalize("THREADS"));
			f.getContentPane().add(new ThreadMonitorPanel());
			f.setLocationRelativeTo(null);
			f.setVisible(true);
			f.pack();
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}));

		mntmExit.addActionListener(e -> System.exit(0));

		mntmHelp.addActionListener(e -> {
			String url = MTGConstants.MTG_DESKTOP_WIKI_URL;
			try {
				Desktop.getDesktop().browse(new URI(url));
			} catch (Exception e1) {
				logger.error(e1);
			}
		});

		mntmAboutMagicDesktop.addActionListener(ae -> new AboutDialog().setVisible(true));

		mntmReportBug.addActionListener(ae -> {
			try {
				String url = MTGConstants.MTG_DESKTOP_ISSUES_URL;
				Desktop.getDesktop().browse(new URI(url));
			} catch (Exception e) {
				logger.error(e);
			}
		});

		mntmFileOpen.addActionListener(ae -> {
			JFileChooser choose = new JFileChooser();
			int returnVal = choose.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File f = choose.getSelectedFile();
				MTGCardsExport exp = MTGControler.getInstance().getAbstractExporterFromExt(f);

				if (exp != null) {
					ThreadManager.getInstance().execute(() -> {
						try {
							if (CardSearchPanel.getInstance() == null)
								throw new NullPointerException(
										MTGControler.getInstance().getLangService().getCapitalize("MUST_BE_LOADED",
												MTGControler.getInstance().getLangService().get("SEARCH_MODULE")));

							CardSearchPanel.getInstance().loading(true, MTGControler.getInstance().getLangService()
									.getCapitalize("LOADING_FILE", f.getName(), exp));
							MagicDeck d = exp.importDeck(f);
							CardSearchPanel.getInstance().open(d.getAsList());
							CardSearchPanel.getInstance().loading(false, "");
							tabbedPane.setSelectedIndex(0);
						} catch (Exception e) {
							logger.error(e);
						}
					}, "open " + f);

				} else {
					MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),"NO EXPORT FOUND",MESSAGE_TYPE.ERROR));
				}
			}
		});

		if (serviceUpdate.hasNewVersion()) 
		{
			JMenuItem newversion = new JMenuItem(
					MTGControler.getInstance().getLangService().getCapitalize("DOWNLOAD_LAST_VERSION") + " : "
							+ serviceUpdate.getOnlineVersion());
			newversion.addActionListener(e -> {
				try {
					Desktop.getDesktop().browse(new URI(MTGConstants.MTG_DESKTOP_APP_ZIP));
				} catch (Exception e1) {
					logger.error(e1.getMessage());
				}
			});
			mnuAbout.add(newversion);
		}

		tabbedPane = new JTabbedPane(MTGConstants.MTG_DESKTOP_TABBED_POSITION);

		if (MTGControler.getInstance().get("modules/search").equals("true"))
			tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("SEARCH_MODULE"),
					MTGConstants.ICON_SEARCH_24, CardSearchPanel.getInstance(), null);

		if (MTGControler.getInstance().get("modules/deckbuilder").equals("true"))
			tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("DECK_MODULE"),
					MTGConstants.ICON_DECK, new DeckBuilderGUI(), null);

		if (MTGControler.getInstance().get("modules/game").equals("true"))
			tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("GAME_MODULE"),
					MTGConstants.ICON_GAME, new GameGUI(), null);

		if (MTGControler.getInstance().get("modules/collection").equals("true"))
			tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("COLLECTION_MODULE"),
					MTGConstants.ICON_COLLECTION, new CollectionPanelGUI(), null);

		if (MTGControler.getInstance().get("modules/stock").equals("true"))
			tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("STOCK_MODULE"),
					MTGConstants.ICON_STOCK, new StockPanelGUI(), null);

		if (MTGControler.getInstance().get("modules/dashboard").equals("true"))
			tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("DASHBOARD_MODULE"),
					MTGConstants.ICON_DASHBOARD, new DashBoardGUI2(), null);

		if (MTGControler.getInstance().get("modules/shopper").equals("true"))
			tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("SHOPPING_MODULE"),
					MTGConstants.ICON_SHOP, new ShopperGUI(), null);

		if (MTGControler.getInstance().get("modules/alarm").equals("true"))
			tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("ALERT_MODULE"),
					MTGConstants.ICON_ALERT, new AlarmGUI(), null);

		if (MTGControler.getInstance().get("modules/cardbuilder").equals("true"))
			tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("BUILDER_MODULE"),
					MTGConstants.ICON_BUILDER, new CardBuilder2GUI(), null);

		if (MTGControler.getInstance().get("modules/rss").equals("true"))
			tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("RSS_MODULE"),
					MTGConstants.ICON_NEWS, new RssGUI(), null);

		if (MTGControler.getInstance().get("modules/history").equals("true"))
			tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("HISTORY_MODULE"),
					MTGConstants.ICON_STORY, new StoriesGUI(), null);

		if (MTGControler.getInstance().get("modules/wallpaper").equals("true"))
			tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("WALLPAPER"),
					MTGConstants.ICON_WALLPAPER, new WallpaperGUI(), null);

		if (MTGControler.getInstance().get("modules/mkm").equals("true"))
			tabbedPane.addTab("MKM", MTGConstants.ICON_SHOP, new MkmPanel(), null);

		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		
			if(osNotifier!=null)
			{	
				osNotifier.getTrayNotifier().addActionListener(e -> {
					if (!isVisible())
						setVisible(true);
					else
						setVisible(false);
				});
			
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
				osNotifier.getTrayNotifier().setToolTip("MTG Desktop Companion");
		
				if (serviceUpdate.hasNewVersion())
				{
					String msg=(MTGControler.getInstance().getLangService().getCapitalize("NEW_VERSION") + " "
									+ serviceUpdate.getOnlineVersion() + " "
									+ MTGControler.getInstance().getLangService().get("AVAILABLE"));
					MTGNotification notif = new MTGNotification(getTitle(),msg,MESSAGE_TYPE.INFO);
					osNotifier.send(notif);
				}
			}
			ThreadManager.getInstance().execute(() -> {
				try {
					new TipsOfTheDayDialog().show();
				} catch (IOException e) {
					logger.error(e);
				}

			}, "launch tooltip");
		}
	

}

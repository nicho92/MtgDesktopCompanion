package org.magic.gui;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getPlugin;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.apache.logging.log4j.Logger;
import org.api.mkm.tools.MkmAPIConfig;
import org.jdesktop.swingx.JXStatusBar;
import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.notifiers.impl.OSTrayNotifier;
import org.magic.api.pricers.impl.MagicCardMarketPricer2;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.BinderTagsEditorComponent;
import org.magic.gui.components.ScriptPanel;
import org.magic.gui.components.card.CardSearchPanel;
import org.magic.gui.components.dialog.AboutDialog;
import org.magic.gui.components.dialog.ChromeDownloader;
import org.magic.gui.components.dialog.TipsOfTheDayDialog;
import org.magic.gui.components.tech.TechnicalMonitorPanel;
import org.magic.services.AccountsManager;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.ShortKeyManager;
import org.magic.services.logging.MTGLogger;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.GithubUtils;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;
import org.mkm.gui.MkmPanel;

public class MagicGUI extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private JTabbedPane tabbedPane;
	private transient OSTrayNotifier osNotifier;
	private CardSearchPanel searchPanel;
	
	
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


	public CardSearchPanel getSearchPanel() {
		return searchPanel;
	}


	
	
	private void initGUI() throws ClassNotFoundException, IOException, SQLException {
		JMenuBar mtgMnuBar;
		JMenu mnFile;
		JMenu mnuAbout;
		JMenuItem mntmExit;

		var barStatus = new JXStatusBar();
		
		setSize(new Dimension(1620, 1024));
		setTitle(MTGConstants.MTG_APP_NAME + " ( v" + MTGControler.getInstance().getVersionChecker().getVersion() + ")");

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
		
		searchPanel = new CardSearchPanel();
		
		var mntmHelp = new JMenuItem(capitalize("READ_MANUAL"),MTGConstants.ICON_HELP);
		var mntmDonate = new JMenuItem(capitalize("DONATE"),MTGConstants.ICON_EURO);
		var mntmThreadItem = new JMenuItem(capitalize("TECHNICAL"),MTGConstants.ICON_CONFIG);
		var mntmAboutMagicDesktop = new JMenuItem(capitalize("ABOUT"),new ImageIcon(MTGConstants.IMAGE_LOGO));
		var mntmReportBug = new JMenuItem(capitalize("REPORT_BUG"),MTGConstants.ICON_BUG);
		var mntmFileTagEditor = new JMenuItem(capitalize("BINDER_TAG_EDITOR"),MTGConstants.ICON_BINDERS);
		var mntmFileChromePlugin = new JMenuItem(capitalize("CHROME_PLUGIN"),MTGConstants.ICON_CHROME);
	
		mtgMnuBar.add(mnFile);
		mnFile.add(mntmFileTagEditor);
		mnFile.add(mntmFileChromePlugin);
		mnFile.add(mntmExit);

		mtgMnuBar.add(mnuAbout);
		mnuAbout.add(mntmThreadItem);
		mnuAbout.add(mntmHelp);
		mnuAbout.add(mntmDonate);
		mnuAbout.add(mntmAboutMagicDesktop);
		mnuAbout.add(mntmReportBug);


		mntmFileChromePlugin.addActionListener(_->{
			var dow = new ChromeDownloader();
			dow.setVisible(true);
		});

		mntmFileTagEditor.addActionListener(_ -> ThreadManager.getInstance().invokeLater(new MTGRunnable() {

			@Override
			protected void auditedRun() {
				MTGUIComponent.createJDialog(new BinderTagsEditorComponent(), true, false).setVisible(true);

			}
		}, "loading Tags dialog"));

		mntmThreadItem.addActionListener(_ ->ThreadManager.getInstance().invokeLater(new MTGRunnable() {

			@Override
			protected void auditedRun() {
				MTGUIComponent.createJDialog(new TechnicalMonitorPanel(), true, false).setVisible(true);

			}
		}, "loading Thread dialog"));

		mntmExit.addActionListener(_ -> MTGControler.getInstance().closeApp());

		mntmHelp.addActionListener(_ -> UITools.browse(MTGConstants.MTG_DESKTOP_WIKI_URL));

		mntmDonate.addActionListener(_ -> UITools.browse(MTGConstants.MTG_DESKTOP_DONATE_URL_PAYPAL));

		mntmAboutMagicDesktop.addActionListener(_ -> MTGUIComponent.createJDialog(new AboutDialog(), false,true).setVisible(true));

		mntmReportBug.addActionListener(_ -> UITools.browse(MTGConstants.MTG_DESKTOP_ISSUES_URL));

		boolean update=MTGControler.getInstance().getVersionChecker().hasNewVersion();

		if (update)
		{
			var newversion = new JMenuItem(capitalize("DOWNLOAD_LAST_VERSION") + " : "+ MTGControler.getInstance().getVersionChecker().getOnlineVersion());
			newversion.addActionListener(_ -> UITools.browse(GithubUtils.inst().getReleaseURL()));
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





		if (MTG.readPropertyAsBoolean("modules/search"))
			addTab(searchPanel);

		if (MTG.readPropertyAsBoolean("modules/collection"))
			addTab(new CollectionPanelGUI());

		if (MTG.readPropertyAsBoolean("modules/deckbuilder"))
			addTab(new DeckBuilderGUI());

		if (MTG.readPropertyAsBoolean("modules/game"))
			addTab(new GameGUI());

		if (MTG.readPropertyAsBoolean("modules/stock"))
			addTab(new StockPanelGUI());

		if (MTG.readPropertyAsBoolean("modules/sealed"))
			addTab(new SealedStockGUI());

		if (MTG.readPropertyAsBoolean("modules/dashboard"))
			addTab(new DashBoardGUI2());

		if (MTG.readPropertyAsBoolean("modules/alarm"))
			addTab(new AlarmGUI());

		if (MTG.readPropertyAsBoolean("modules/cardbuilder"))
			addTab(new CardBuilder2GUI());

		if (MTG.readPropertyAsBoolean("modules/rss"))
			addTab(new RssGUI());

		if (MTG.readPropertyAsBoolean("modules/wallpaper"))
			addTab(new WallpaperGUI());

		if (MTG.readPropertyAsBoolean("modules/webshop"))
			addTab(new ShopGUI());

		if (MTG.readPropertyAsBoolean("modules/announce"))
			addTab(new AnnouncesGUI());

		if (MTG.readPropertyAsBoolean("modules/network"))
			addTab(new NetworkGUI());

		if (MTG.readPropertyAsBoolean("modules/scripts"))
			addTab(new ScriptPanel());


		if (MTG.readPropertyAsBoolean("modules/mkm"))
		{
			try {

			MkmAPIConfig.getInstance().init(AccountsManager.inst().getAuthenticator(new MagicCardMarketPricer2()).getTokensAsProperties());
			}
			catch(Exception e)
			{
				logger.error(e);
			}
			addTab(MTGUIComponent.build(new MkmPanel(), "MKM", new ImageIcon(new ImageIcon(MagicGUI.class.getResource("/icons/plugins/magiccardmarket.png")).getImage().getScaledInstance(MTGConstants.MENU_ICON_SIZE, MTGConstants.MENU_ICON_SIZE, Image.SCALE_SMOOTH))));
		}



		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		getContentPane().add(barStatus,BorderLayout.SOUTH);
		
		
		
		
		var lblSupport = new JLabel("Help me with a tip :) ");
		lblSupport.setFont(MTGControler.getInstance().getFont().deriveFont(Font.BOLD));
		lblSupport.addMouseListener(new MouseAdapter() {
		    @Override
            public void mouseClicked(MouseEvent e) {
		    	UITools.browse(MTGConstants.MTG_DESKTOP_DONATE_URL_PAYPAL);
            }
		    @Override
		    public void mouseEntered(MouseEvent e) {
		    	lblSupport.setCursor(new Cursor(Cursor.HAND_CURSOR));
		    }
		    @Override
		    public void mouseExited(MouseEvent e) {
		    	lblSupport.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));	
		    }

        });
		
		barStatus.add(lblSupport);
		

			if(osNotifier!=null)
			{
				osNotifier.getTrayNotifier().addActionListener(_ -> setVisible(!isVisible()));

				var menuTray = new PopupMenu();
				for (var index_tab = 0; index_tab < tabbedPane.getTabCount(); index_tab++) {
					final int index = index_tab;
					var it = new MenuItem(tabbedPane.getTitleAt(index_tab));
					it.addActionListener(_ -> {
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
									+ MTGControler.getInstance().getVersionChecker().getOnlineVersion() + " "
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
		tabbedPane.addTab(instance.getTitle(),ImageTools.resize(instance.getIcon(), 24,24), instance, null);

	}

}

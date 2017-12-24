package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.Logger;
import org.magic.gui.components.CardSearchPanel;
import org.magic.gui.components.dialog.LoggerViewFrame;
import org.magic.gui.components.dialog.ThreadMonitorFrame;
import org.magic.gui.components.dialog.TipsOfTheDayDialog;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;
import org.magic.services.VersionChecker;

public class MagicGUI extends JFrame {

	Logger logger = MTGLogger.getLogger(this.getClass());

	private SystemTray tray;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenu mnuAbout;
	private JMenu jmnuLook;
	private JMenuItem mntmExit;

 	private JTabbedPane  tabbedPane;
	private VersionChecker serviceUpdate;
	public static TrayIcon trayNotifier;
	private Map<String,String> looks;
	private Map<String,String> looksMore;
	
	public MagicGUI() {

		try {
			serviceUpdate = new VersionChecker();
			initGUI();
		} 
		catch(Exception e)
		{
			logger.error(e);
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e,"ERROR",JOptionPane.ERROR_MESSAGE);
		}

		logger.info("construction of GUI : done");
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
			MTGControler.getInstance().setProperty("lookAndFeel", lookAndFeel);
			SwingUtilities.updateComponentTreeUI(this);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
	}

	
	public void initGUI() throws Exception
	{
		logger.info("init Main GUI");
		setSize(new Dimension(1420, 900));
		setTitle("Magic Desktop Companion ( v" + MTGControler.getInstance().getVersion()+")");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(MTGConstants.IMAGE_LOGO);
		getContentPane().setLayout(new BorderLayout());
		
		try{
			tray=SystemTray.getSystemTray();
			trayNotifier = new TrayIcon(MTGConstants.IMAGE_LOGO.getScaledInstance(16, 16, BufferedImage.SCALE_SMOOTH));
		}
		catch (Exception e) {
			logger.error(e);
		}
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		
		mnFile = new JMenu("File");
		mnuAbout = new JMenu("?");
		jmnuLook = new JMenu("Look");
			
		mntmExit = new JMenuItem("Exit");
		
		JMenuItem mntmHelp = new JMenuItem("Read the f***g manual");
		JMenuItem mntmThreadItem = new JMenuItem("Threads");
		JMenuItem mntmLogsItem = new JMenuItem("Logs");
		JMenuItem mntmAboutMagicDesktop = new JMenuItem("About Magic Desktop Companion");
		JMenuItem mntmReportBug = new JMenuItem("Report Bug");
		
		
		menuBar.add(mnFile);
		mnFile.add(mntmExit);
		menuBar.add(jmnuLook);
		menuBar.add(mnuAbout);
		mnuAbout.add(mntmThreadItem);
		mnuAbout.add(mntmLogsItem);
		mnuAbout.add(mntmHelp);
		mnuAbout.add(mntmAboutMagicDesktop);
		mnuAbout.add(mntmReportBug);
		
		
		mntmLogsItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable(){
					@Override
					public void run() {
						new LoggerViewFrame().setVisible(true);
					}
				});
				
			}
		});
		
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
		
		mntmExit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
				
			}
		});
			
		mntmHelp.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String url =MTGConstants.MTG_DESKTOP_WIKI_URL;
				try {
					Desktop.getDesktop().browse(new URI(url));
				} catch (Exception e1) {
					logger.error(e1);
				}
			}
		});
	
		mntmAboutMagicDesktop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new AboutDialog().setVisible(true);
				
			}
		});
		
		mntmReportBug.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String url = MTGConstants.MTG_DESKTOP_ISSUES_URL;
					Desktop.getDesktop().browse(new URI(url));
				} catch (Exception e) {
					logger.error(e);
				}
			}
		});
		
		if(serviceUpdate.hasNewVersion())
		{
			JMenuItem newversion = new JMenuItem("Download latest version : " + serviceUpdate.getOnlineVersion() );
			newversion.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String url =MTGConstants.MTG_DESKTOP_APP_ZIP;
					try {
						Desktop.getDesktop().browse(new URI(url));
					} catch (Exception e1) {
						logger.error(e1.getMessage());
					}
				}
			});
			mnuAbout.add(newversion);
		}
		
		
		//INIT AVAILABLE LOOK AND FEELS
		looks = new HashMap<String,String>();
		looksMore = new HashMap<String,String>();
		
		for(LookAndFeelInfo i : UIManager.getInstalledLookAndFeels())
			looks.put(i.getName(),i.getClassName());
		
		looksMore.put("Cerulan","org.pushingpixels.substance.api.skin.SubstanceCeruleanLookAndFeel");
		looksMore.put("Business Blue","org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel");
		looksMore.put("Gemini","org.pushingpixels.substance.api.skin.SubstanceGeminiLookAndFeel");
		looksMore.put("Nebula","org.pushingpixels.substance.api.skin.SubstanceNebulaLookAndFeel");		
		looksMore.put("Graphite","org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel");
		looksMore.put("Magellan","org.pushingpixels.substance.api.skin.SubstanceMagellanLookAndFeel");
		
		
		
		JMenu itMore = new JMenu("More");
		for(final String ui : looksMore.keySet())
		{
			final JMenuItem it = new JMenuItem(ui);
			it.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setLookAndFeel(looksMore.get(ui));
				}
			});
			itMore.add(it);
		}
		
		for(final String ui : looks.keySet())
		{
			final JMenuItem it = new JMenuItem(ui);
			it.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setLookAndFeel(looks.get(ui));
				}
			});
			jmnuLook.add(it);
		}
		
		
		jmnuLook.add(itMore);
	
		MTGControler.getInstance().getActivatedGUI();
		
		tabbedPane = new JTabbedPane(MTGConstants.MTG_DESKTOP_TABBED_POSITION);
		
		if(MTGControler.getInstance().get("modules/search").equals("true"))
			tabbedPane.addTab("Search", MTGConstants.ICON_SEARCH_2, new CardSearchPanel(), null);
		
		if(MTGControler.getInstance().get("modules/deckbuilder").equals("true"))
			tabbedPane.addTab("Deck", MTGConstants.ICON_DECK, new DeckBuilderGUI(), null);
		
		if(MTGControler.getInstance().get("modules/game").equals("true"))
			tabbedPane.addTab("Game", MTGConstants.ICON_COLLECTION_SMALL, new GameGUI(), null);
		
		if(MTGControler.getInstance().get("modules/collection").equals("true"))
			tabbedPane.addTab("Collection", MTGConstants.ICON_COLLECTION, new CollectionPanelGUI(), null);

		if(MTGControler.getInstance().get("modules/stock").equals("true"))
			tabbedPane.addTab("Stock", MTGConstants.ICON_STOCK, new StockPanelGUI(), null);
		
		if(MTGControler.getInstance().get("modules/dashboard").equals("true"))
			tabbedPane.addTab("DashBoard",MTGConstants.ICON_DASHBOARD, new DashBoardGUI2(), null);
		
		if(MTGControler.getInstance().get("modules/shopper").equals("true"))
			tabbedPane.addTab("Shopping",MTGConstants.ICON_SHOP, new ShopperGUI(), null);
		
		if(MTGControler.getInstance().get("modules/alarm").equals("true"))
			tabbedPane.addTab("Alert", MTGConstants.ICON_ALERT, new AlarmGUI(), null);
		
		if(MTGControler.getInstance().get("modules/cardbuilder").equals("true"))
			tabbedPane.addTab("Builder", MTGConstants.ICON_BUILDER, new CardBuilder2GUI(), null);
		
		if(MTGControler.getInstance().get("modules/rss").equals("true"))
			tabbedPane.addTab("RSS", MTGConstants.ICON_RSS, new RssGUI(), null);
		

		
		
		tabbedPane.addTab("Configuration", MTGConstants.ICON_CONFIG, new ConfigurationPanelGUI(), null);
		
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		if (SystemTray.isSupported()) {
			tray.add(trayNotifier);
			trayNotifier.addActionListener(new ActionListener() {
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
			
			trayNotifier.setPopupMenu(menuTray);
			trayNotifier.setToolTip("MTG Desktop Companion");
			if(serviceUpdate.hasNewVersion())
				trayNotifier.displayMessage(getTitle(),"New version " + serviceUpdate.getOnlineVersion() + " available",TrayIcon.MessageType.INFO);
		
			
			ThreadManager.getInstance().execute(new Runnable() {
				public void run() {
					try {
						new TipsOfTheDayDialog().show();
					} catch (IOException e) {
						logger.error(e);
					}
					
				}
			}, "launch tooltip");
		}		
	}

	

}

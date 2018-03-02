package org.magic.gui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFileChooser;
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
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsExport;
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


public class MagicGUI extends JFrame {

	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	private transient SystemTray tray;

 	private JTabbedPane  tabbedPane;
	private transient VersionChecker serviceUpdate;
	private static transient TrayIcon trayNotifier = new TrayIcon(MTGConstants.IMAGE_LOGO.getScaledInstance(16, 16, BufferedImage.SCALE_SMOOTH));
	private CardSearchPanel cardSearchPanel;
	
	public static TrayIcon getTrayNotifier() {
		return trayNotifier;
	}
	
	public MagicGUI() {

		try {
			serviceUpdate = new VersionChecker();
			initGUI();
		} 
		catch(Exception e)
		{
			logger.error("Error init GUI",e);
			JOptionPane.showMessageDialog(null, e,MTGControler.getInstance().getLangService().getError(),JOptionPane.ERROR_MESSAGE);
		}
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("CONFIGURATION"), MTGConstants.ICON_CONFIG, new ConfigurationPanelGUI(), null);
		
		logger.info("construction of GUI : done");
	}
	
	

	public void setSelectedTab(int id)
	{
		tabbedPane.setSelectedIndex(id);
	}

	public void setLookAndFeel(String lookAndFeel)
	{
		try {
			UIManager.setLookAndFeel(lookAndFeel);
			MTGControler.getInstance().setProperty("lookAndFeel", lookAndFeel);
			SwingUtilities.updateComponentTreeUI(this);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
	}

	
	public void initGUI() throws ClassNotFoundException, IOException, SQLException, AWTException
	{
		JMenuBar mtgMnuBar;
		JMenu mnFile;
		JMenu mnuAbout;
		JMenu jmnuLook;
		JMenuItem mntmExit;
		Map<String,String> looks;
		Map<String,String> looksMore;
		
		logger.info("init Main GUI");
		setSize(new Dimension(1420, 900));
		setTitle("Magic Desktop Companion ( v" + MTGControler.getInstance().getVersion()+")");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(MTGConstants.IMAGE_LOGO);
		getContentPane().setLayout(new BorderLayout());
		
		try{
			tray=SystemTray.getSystemTray();
		}
		catch (Exception e) {
			logger.error(e);
		}
		
		mtgMnuBar = new JMenuBar();
		setJMenuBar(mtgMnuBar);

		
		mnFile = new JMenu(MTGControler.getInstance().getLangService().getCapitalize("FILE"));
		mnuAbout = new JMenu("?");
		jmnuLook = new JMenu(MTGControler.getInstance().getLangService().getCapitalize("LOOK"));
			
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
		mtgMnuBar.add(jmnuLook);
		mtgMnuBar.add(mnuAbout);
		mnuAbout.add(mntmThreadItem);
		mnuAbout.add(mntmLogsItem);
		mnuAbout.add(mntmHelp);
		mnuAbout.add(mntmAboutMagicDesktop);
		mnuAbout.add(mntmReportBug);
		
		
		
		mntmLogsItem.addActionListener(ae->
				SwingUtilities.invokeLater(()->{
						JFrame f = new JFrame(MTGControler.getInstance().getLangService().getCapitalize("LOGS"));
						f.getContentPane().add(new LoggerViewPanel());
						f.setLocationRelativeTo(null);
						f.setVisible(true);
						f.pack();
						f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				})
		);
		
		mntmThreadItem.addActionListener(e->

				SwingUtilities.invokeLater(()->{
						JFrame f = new JFrame(MTGControler.getInstance().getLangService().getCapitalize("THREADS"));
						f.getContentPane().add(new ThreadMonitorPanel());
						f.setLocationRelativeTo(null);
						f.setVisible(true);
						f.pack();
						f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				})
		);
		
		mntmExit.addActionListener(e->System.exit(0));
			
		mntmHelp.addActionListener(e->{
				String url =MTGConstants.MTG_DESKTOP_WIKI_URL;
				try {
					Desktop.getDesktop().browse(new URI(url));
				} catch (Exception e1) {
					logger.error(e1);
				}
		});
	
		mntmAboutMagicDesktop.addActionListener(ae->
				new AboutDialog().setVisible(true)
		);
		
		mntmReportBug.addActionListener(ae->{
				try {
					String url = MTGConstants.MTG_DESKTOP_ISSUES_URL;
					Desktop.getDesktop().browse(new URI(url));
				} catch (Exception e) {
					logger.error(e);
				}
		});
		
		
		mntmFileOpen.addActionListener(ae->{
					JFileChooser choose = new JFileChooser();
					int returnVal = choose.showOpenDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION)
					{
						File f =choose.getSelectedFile();
						MTGCardsExport exp = MTGControler.getInstance().getAbstractExporterFromExt(f);
						
						if(exp!=null)
						{
							ThreadManager.getInstance().execute(()->{
									try 
									{
										if(cardSearchPanel==null)
											throw new NullPointerException(MTGControler.getInstance().getLangService().getCapitalize("MUST_BE_LOADED",MTGControler.getInstance().getLangService().get("SEARCH_MODULE")));
										
									cardSearchPanel.loading(true, MTGControler.getInstance().getLangService().getCapitalize("LOADING_FILE",f.getName(),exp));
									MagicDeck d=exp.importDeck(f);	
									cardSearchPanel.open(d.getAsList());
									cardSearchPanel.loading(false,"");
									tabbedPane.setSelectedIndex(0);
									} catch (Exception e) {
										logger.error(e);
									}
								},"open " + f);
							
						}
						else
						{
							JOptionPane.showMessageDialog(null, "NO EXPORTER AVAILABLE",MTGControler.getInstance().getLangService().getError(),JOptionPane.ERROR_MESSAGE);
						}
					}
		});
		
		
		if(serviceUpdate.hasNewVersion())
		{
			JMenuItem newversion = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("DOWNLOAD_LAST_VERSION")+" : " + serviceUpdate.getOnlineVersion() );
			newversion.addActionListener(e->{
					String url =MTGConstants.MTG_DESKTOP_APP_ZIP;
					try {
						Desktop.getDesktop().browse(new URI(url));
					} catch (Exception e1) {
						logger.error(e1.getMessage());
					}
			});
			mnuAbout.add(newversion);
		}
		
		
		//INIT AVAILABLE LOOK AND FEELS
		looks = new HashMap<>();
		looksMore = new HashMap<>();
		
		for(LookAndFeelInfo i : UIManager.getInstalledLookAndFeels())
			looks.put(i.getName(),i.getClassName());
		
		looksMore.put("Cerulan","org.pushingpixels.substance.api.skin.SubstanceCeruleanLookAndFeel");
		looksMore.put("Business Blue","org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel");
		looksMore.put("Gemini","org.pushingpixels.substance.api.skin.SubstanceGeminiLookAndFeel");
		looksMore.put("Nebula","org.pushingpixels.substance.api.skin.SubstanceNebulaLookAndFeel");		
		looksMore.put("Graphite","org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel");
		looksMore.put("Magellan","org.pushingpixels.substance.api.skin.SubstanceMagellanLookAndFeel");
		looksMore.put("Coffe","org.pushingpixels.substance.api.skin.SubstanceCremeCoffeeLookAndFeel");
		
		
		JMenu itMore = new JMenu(MTGControler.getInstance().getLangService().getCapitalize("MORE"));
		for(final Entry<String, String> ui : looksMore.entrySet())
		{
			final JMenuItem it = new JMenuItem(ui.getKey());
			it.addActionListener(e->setLookAndFeel(ui.getValue()));
			itMore.add(it);
		}
		
		for(final Entry<String, String> ui : looks.entrySet())
		{
			final JMenuItem it = new JMenuItem(ui.getKey());
			it.addActionListener(e->
					setLookAndFeel(ui.getValue())
			);
			jmnuLook.add(it);
		}
		
		
		jmnuLook.add(itMore);
		
		tabbedPane = new JTabbedPane(MTGConstants.MTG_DESKTOP_TABBED_POSITION);
	
		if(MTGControler.getInstance().get("modules/search").equals("true"))
			tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("SEARCH_MODULE"), MTGConstants.ICON_SEARCH_2, CardSearchPanel.getInstance(), null);
		
		if(MTGControler.getInstance().get("modules/deckbuilder").equals("true"))
			tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("DECK_MODULE"), MTGConstants.ICON_DECK, new DeckBuilderGUI(), null);
		
		if(MTGControler.getInstance().get("modules/game").equals("true"))
			tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("GAME_MODULE"), MTGConstants.ICON_COLLECTION_SMALL, new GameGUI(), null);
		
		if(MTGControler.getInstance().get("modules/collection").equals("true"))
			tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("COLLECTION_MODULE"), MTGConstants.ICON_COLLECTION, new CollectionPanelGUI(), null);

		if(MTGControler.getInstance().get("modules/stock").equals("true"))
			tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("STOCK_MODULE"), MTGConstants.ICON_STOCK, new StockPanelGUI(), null);
		
		if(MTGControler.getInstance().get("modules/dashboard").equals("true"))
			tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("DASHBOARD_MODULE"),MTGConstants.ICON_DASHBOARD, new DashBoardGUI2(), null);
		
		if(MTGControler.getInstance().get("modules/shopper").equals("true"))
			tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("SHOPPING_MODULE"),MTGConstants.ICON_SHOP, new ShopperGUI(), null);
		
		if(MTGControler.getInstance().get("modules/alarm").equals("true"))
			tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("ALERT_MODULE"), MTGConstants.ICON_ALERT, new AlarmGUI(), null);
		
		if(MTGControler.getInstance().get("modules/cardbuilder").equals("true"))
			tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("BUILDER_MODULE"), MTGConstants.ICON_BUILDER, new CardBuilder2GUI(), null);
		
		if(MTGControler.getInstance().get("modules/rss").equals("true"))
			tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("RSS_MODULE"), MTGConstants.ICON_RSS, new RssGUI(), null);
		
		if(MTGControler.getInstance().get("modules/history").equals("true"))
			tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("HISTORY_MODULE"), MTGConstants.ICON_STORY, new StoriesGUI(), null);

		//tabbedPane.addTab("MKM", MTGConstants.ICON_SHOP, new MkmPanel(), null);
		
		
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
	
		if (SystemTray.isSupported()) {
			tray.add(trayNotifier);
			trayNotifier.addActionListener(e->{
					if(!isVisible())
						setVisible(true);
					else
						setVisible(false);
			});
			PopupMenu menuTray = new PopupMenu();
			for(int index_tab = 0;index_tab<tabbedPane.getTabCount();index_tab++)
			{
				final int index = index_tab;
				MenuItem it = new MenuItem(tabbedPane.getTitleAt(index_tab));
				it.addActionListener(e->{
							setVisible(true);
							setSelectedTab(index);
				});
				menuTray.add(it);
			}
			
			trayNotifier.setPopupMenu(menuTray);
			trayNotifier.setToolTip("MTG Desktop Companion");
			if(serviceUpdate.hasNewVersion())
				trayNotifier.displayMessage(getTitle(),MTGControler.getInstance().getLangService().getCapitalize("NEW_VERSION")+" " + serviceUpdate.getOnlineVersion() + " "+MTGControler.getInstance().getLangService().get("AVAILABLE"),TrayIcon.MessageType.INFO);
		
			
			ThreadManager.getInstance().execute(()->{
					try {
						new TipsOfTheDayDialog().show();
					} catch (IOException e) {
						logger.error(e);
					}
					
			}, "launch tooltip");
		}		
	}

	

}

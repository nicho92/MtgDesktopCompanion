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
import java.util.ArrayList;
import java.util.List;

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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.gui.components.dialog.ThreadMonitorFrame;
import org.magic.gui.game.GamePanelGUI;
import org.magic.services.MTGDesktopCompanionControler;
import org.magic.services.ThreadManager;
import org.magic.services.VersionChecker;

public class MagicGUI extends JFrame {

	static final Logger logger = LogManager.getLogger(MagicGUI.class.getName());

	private final SystemTray tray = SystemTray.getSystemTray();
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenu mnuAbout;
	private JMenu mnuLang;
	private JMenu jmnuLook;
	private JMenuItem mntmExit;

 	private JTabbedPane  tabbedPane;
	private VersionChecker serviceUpdate;
	public static TrayIcon trayNotifier = new TrayIcon(Toolkit.getDefaultToolkit().getImage(MagicGUI.class.getResource("/res/logo.gif")).getScaledInstance(16, 16, BufferedImage.SCALE_SMOOTH));

	
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
			MTGDesktopCompanionControler.getInstance().setProperty("lookAndFeel", lookAndFeel);
			SwingUtilities.updateComponentTreeUI(this);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
	}

	
	public void initGUI() throws Exception
	{
		logger.info("init GUI");
		setSize(new Dimension(1420, 900));
		setTitle("Magic Desktop Companion ( v" + MTGDesktopCompanionControler.getInstance().getVersion()+")");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MagicGUI.class.getResource("/res/logo.gif")));
		getContentPane().setLayout(new BorderLayout());
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		
		mnFile = new JMenu("File");
		mnuLang= new JMenu("Langage");
		mnuAbout = new JMenu("?");
		jmnuLook = new JMenu("Look");
			
		mntmExit = new JMenuItem("Exit");
		
		JMenuItem mntmHelp = new JMenuItem("Read the f***g manual");
		JMenuItem mntmThreadItem = new JMenuItem("Threads");
		JMenuItem mntmAboutMagicDesktop = new JMenuItem("About Magic Desktop Companion");
		JMenuItem mntmReportBug = new JMenuItem("Report Bug");
		
		
		menuBar.add(mnFile);
		mnFile.add(mntmExit);
		menuBar.add(jmnuLook);
		menuBar.add(mnuLang);
		menuBar.add(mnuAbout);
		mnuAbout.add(mntmThreadItem);
		mnuAbout.add(mntmHelp);
		mnuAbout.add(mntmAboutMagicDesktop);
		mnuAbout.add(mntmReportBug);
		
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
				String url ="https://github.com/nicho92/MtgDesktopCompanion/wiki";
				try {
					Desktop.getDesktop().browse(new URI(url));
				} catch (Exception e1) {
					e1.printStackTrace();
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
					String url = "https://github.com/nicho92/MtgDesktopCompanion/issues";
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
					String url ="https://github.com/nicho92/MtgDesktopCompanion/blob/master/executable/mtgcompanion.zip?raw=true";
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
		List<String> looks = new ArrayList<String>();
		for(LookAndFeelInfo i : UIManager.getInstalledLookAndFeels())
			looks.add(i.getClassName());
		
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
		
		//INIT AVAILABLE LANGAGES
		for(String l : MTGDesktopCompanionControler.getInstance().getEnabledProviders().getLanguages())
		{
			final JMenuItem it = new JMenuItem(l);
			it.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					MTGDesktopCompanionControler.getInstance().setProperty("langage", it.getText());
				}
			});
			mnuLang.add(it);
		}
		
	
		tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		tabbedPane.addTab("Search", new ImageIcon(MagicGUI.class.getResource("/res/search.gif")), new CardSearchGUI(), null);
		tabbedPane.addTab("Deck", new ImageIcon(MagicGUI.class.getResource("/res/book_icon.jpg")), new DeckBuilderGUI(), null);
		tabbedPane.addTab("Game", new ImageIcon(MagicGUI.class.getResource("/res/bottom.png")), GamePanelGUI.getInstance(), null);
		tabbedPane.addTab("Collection", new ImageIcon(MagicGUI.class.getResource("/res/collection.png")), new CollectionPanelGUI(), null);
		tabbedPane.addTab("DashBoard", new ImageIcon(MagicGUI.class.getResource("/res/dashboard.png")), new DashBoardGUI(), null);
		tabbedPane.addTab("Shopping", new ImageIcon(MagicGUI.class.getResource("/res/shop.png")), new ShopperGUI(), null);
		tabbedPane.addTab("Alert", new ImageIcon(MagicGUI.class.getResource("/res/bell.png")), new AlarmGUI(), null);
		tabbedPane.addTab("Builder", new ImageIcon(MagicGUI.class.getResource("/res/create.png")), new CardBuilder2GUI(), null);
		tabbedPane.addTab("RSS", new ImageIcon(MagicGUI.class.getResource("/res/rss.png")), new RssGUI(), null);
		tabbedPane.addTab("Configuration", new ImageIcon(MagicGUI.class.getResource("/res/build.png")), new ConfigurationPanelGUI(), null);
		
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
				
				@Override
				public void run() {
					try {
						new TipsOfTheDayDialog().show();
					} catch (IOException e) {
					//	e.printStackTrace();
					}
					
				}
			}, "launch tooltip");
			
			
		}		
	}

	

}

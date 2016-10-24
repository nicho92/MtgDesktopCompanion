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
import org.magic.gui.game.GamePanelGUI;
import org.magic.services.MagicFactory;
import org.magic.services.VersionChecker;
import com.seaglasslookandfeel.SeaGlassLookAndFeel;

public class MagicGUI extends JFrame {

	static final Logger logger = LogManager.getLogger(MagicGUI.class.getName());

	TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(MagicGUI.class.getResource("/res/logo.gif")).getScaledInstance(16, 16, BufferedImage.SCALE_SMOOTH));
	public final SystemTray tray = SystemTray.getSystemTray();

	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenu mnuAbout;
	private JMenu mnuLang;
	private JMenuItem mntmExit;
	private JMenuItem mntmAboutMagicDesktop;
	private JMenuItem mntmReportBug;
 	private JTabbedPane  tabbedPane;
	private VersionChecker serviceUpdate;
	
	
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

		logger.debug("construction of GUI : done");
	}
	
	
	public void setDefaultLanguage(String language) {
		MagicFactory.getInstance().setProperty("langage", language);
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
			MagicFactory.getInstance().setProperty("lookAndFeel", lookAndFeel);
			SwingUtilities.updateComponentTreeUI(this);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
	}

	
	public void initGUI() throws Exception
	{
		logger.debug("init main GUI");
		menuBar = new JMenuBar();
		mnFile = new JMenu("File");
		mntmExit = new JMenuItem("Exit");

		setSize(new Dimension(1420, 900));
		setTitle("Magic Desktop Companion ( v" + MagicFactory.getInstance().getVersion()+")");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MagicGUI.class.getResource("/res/logo.gif")));
		setJMenuBar(menuBar);

		menuBar.add(mnFile);
		mnFile.add(mntmExit);
		
		
		
		JMenu jmnuLook = new JMenu("Look");
		menuBar.add(jmnuLook);
		
		mnuLang= new JMenu("Langage");
		menuBar.add(mnuLang);
		
		
		mnuAbout = new JMenu("?");
		menuBar.add(mnuAbout);
		
		
		
		JMenuItem mntmThreadItem = new JMenuItem("Threads");
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
		
		mnuAbout.add(mntmThreadItem);
		
		JMenuItem mntmHelp = new JMenuItem("Read the f***g manual");
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
		mnuAbout.add(mntmHelp);
		
		
		mntmAboutMagicDesktop = new JMenuItem("About Magic Desktop Companion");
		mntmAboutMagicDesktop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new AboutDialog().setVisible(true);
				
			}
		});
		
		mnuAbout.add(mntmAboutMagicDesktop);
		
		mntmReportBug = new JMenuItem("Report Bug");
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
		
		mnuAbout.add(mntmReportBug);
		
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
		
		List<String> looks = new ArrayList<String>();
		for(LookAndFeelInfo i : UIManager.getInstalledLookAndFeels())
			looks.add(i.getClassName());
		
		looks.add(new SeaGlassLookAndFeel().getClass().getName());
	//	looks.add(new SyntheticaPlainLookAndFeel().getClass().getName());
		
		
		
		
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
		
		for(String l : MagicFactory.getInstance().getEnabledProviders().getLanguages())
		{
			final JMenuItem it = new JMenuItem(l);
			it.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setDefaultLanguage(it.getText());
				}
			});
			mnuLang.add(it);
		}
		

		getContentPane().setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		tabbedPane.addTab("Search", new ImageIcon(MagicGUI.class.getResource("/res/search.gif")), new CardSearchGUI(), null);
		tabbedPane.addTab("Deck", new ImageIcon(MagicGUI.class.getResource("/res/book_icon.jpg")), new DeckBuilderGUI(), null);
		tabbedPane.addTab("Game", new ImageIcon(MagicGUI.class.getResource("/res/bottom.png")), GamePanelGUI.getInstance(), null);
		tabbedPane.addTab("Collection", new ImageIcon(MagicGUI.class.getResource("/res/collection.png")), new CollectionPanelGUI(), null);
		tabbedPane.addTab("DashBoard", new ImageIcon(MagicGUI.class.getResource("/res/dashboard.png")), new DashBoardGUI(), null);
		tabbedPane.addTab("Shopping", new ImageIcon(MagicGUI.class.getResource("/res/shop.gif")), new ShopperGUI(), null);
		tabbedPane.addTab("Alert", new ImageIcon(MagicGUI.class.getResource("/res/bell.png")), new AlarmGUI(), null);
		tabbedPane.addTab("Builder", new ImageIcon(MagicGUI.class.getResource("/res/create.png")), new CardBuilderPanelGUI(), null);
		tabbedPane.addTab("RSS", new ImageIcon(MagicGUI.class.getResource("/res/rss.png")), new RssGUI(), null);
		tabbedPane.addTab("Configuration", new ImageIcon(MagicGUI.class.getResource("/res/build.png")), new ConfigurationPanelGUI(), null);
		

		if (SystemTray.isSupported()) {
			tray.add(trayIcon);
			trayIcon.addActionListener(new ActionListener() {
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
			
			trayIcon.setPopupMenu(menuTray);
			trayIcon.setToolTip("MTG Desktop Companion");
			if(serviceUpdate.hasNewVersion())
				trayIcon.displayMessage(getTitle(),"New version " + serviceUpdate.getOnlineVersion() + " available",TrayIcon.MessageType.INFO);
		
		}		
	}

	

}

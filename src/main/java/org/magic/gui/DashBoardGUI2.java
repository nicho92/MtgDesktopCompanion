package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.SystemColor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;
import org.magic.services.threads.ThreadManager;

public class DashBoardGUI2 extends MTGUIComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JMenuItem mntmSaveDisplay;
	private JDesktopPane desktop;
	private JMenu mnNewMenu;
	
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_DASHBOARD;
	}
	
	@Override
	public String getTitle() {
		return MTGControler.getInstance().getLangService().getCapitalize("DASHBOARD_MODULE");
	}
	
	public DashBoardGUI2() {
		desktop = new JDesktopPane();
		JMenuBar menuBar = new JMenuBar();
		mnNewMenu = new JMenu(MTGControler.getInstance().getLangService().getCapitalize("ADD"));
		JMenu mnWindow = new JMenu(MTGControler.getInstance().getLangService().getCapitalize("WINDOW"));
		mntmSaveDisplay = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("SAVE_DISPLAY"));
		desktop.setBackground(SystemColor.activeCaption);
		menuBar.setBounds(0, 0, 120, 21);
		menuBar.add(mnNewMenu);
		menuBar.add(mnWindow);
		mnWindow.add(mntmSaveDisplay);
		desktop.add(menuBar);
		setLayout(new BorderLayout());
		
		add(desktop,BorderLayout.CENTER);
		
		try {
			for (AbstractJDashlet dash : MTGControler.getInstance().getPlugins(AbstractJDashlet.class)) {
				JMenuItem mntmNewMenuItem = new JMenuItem(dash.getName(),dash.getIcon());
				mntmNewMenuItem.addActionListener(e -> {
					try {
						addDash(PluginRegistry.inst().newInstance(dash.getClass()));
					} catch (Exception ex) {
						logger.error("Error Loading " + dash, ex);
					}
				});
				mnNewMenu.add(mntmNewMenuItem);
			}

		} catch (Exception ex) {
			logger.error("Error", ex);
		}
		
		initActions();
	}
	
	
	@Override
	public void onFirstShowing() {
		
		
		SwingWorker<Void, File> sw = new SwingWorker<>()
				{
					protected Void doInBackground() throws Exception {
						publish(AbstractJDashlet.confdir.listFiles());
						return null;
					}
					
					@Override
					protected void process(List<File> chunks) {
						for (File f : chunks) {
							try (FileInputStream fis = new FileInputStream(f)){
								Properties p = new Properties();
								p.load(fis);
								AbstractJDashlet dash = PluginRegistry.inst().newInstance(p.get("class").toString());
								dash.setProperties(p);
								addDash(dash);

							} catch (Exception e) {
								logger.error("Could not add " + f, e);
							}
						}
					}
				};
				
		ThreadManager.getInstance().runInEdt(sw, "Loading dashlets");
	}

	private void initActions() {
		mntmSaveDisplay.addActionListener(ae -> {
			int i = 0;

			try {
				FileUtils.cleanDirectory(AbstractJDashlet.confdir);
			} catch (IOException e1) {
				logger.error(e1);
			}

			
			for (JInternalFrame jif : desktop.getAllFrames()) {
				i++;
				AbstractJDashlet dash = (AbstractJDashlet) jif;
				dash.setProperty("x", String.valueOf(dash.getBounds().getX()));
				dash.setProperty("y", String.valueOf(dash.getBounds().getY()));
				dash.setProperty("w", String.valueOf(dash.getBounds().getWidth()));
				dash.setProperty("h", String.valueOf(dash.getBounds().getHeight()));
				dash.setProperty("class", dash.getClass().getName());
				dash.setProperty("id", String.valueOf(i));
				File f = new File(AbstractJDashlet.confdir, i + ".conf");

				try (FileOutputStream fos = new FileOutputStream(f)) {
					dash.getProperties().store(fos, "");
					logger.trace("saving " + f + " :" + dash.getProperties());
					
				} catch (IOException e) {
					logger.error(e);
				}
			}	
			
		
			
		});
		
	}

	public void addDash(AbstractJDashlet dash) {
				try {
					logger.debug("loading " + dash.getName());
					dash.initGUI();
					desktop.add(dash);
					dash.init();
					dash.setVisible(true);
					
				} catch (Exception e) {
					logger.error("error adding " + dash,e);
					MTGControler.getInstance().notify(e);
				} 
	}

}

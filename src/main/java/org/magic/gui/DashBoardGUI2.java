package org.magic.gui;

import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.magic.gui.abstracts.AbstractJDashlet;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class DashBoardGUI2 extends JDesktopPane {
	
	ClassLoader classLoader = DashBoardGUI2.class.getClassLoader();
	Logger logger = MTGLogger.getLogger(this.getClass());

	
	public DashBoardGUI2() {
		setBackground(SystemColor.activeCaption);
		
		logger.info("init Dashboard GUI");
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 84, 21);
		
		JMenu mnNewMenu = new JMenu(MTGControler.getInstance().getLangService().getCapitalize("ADD"));
		menuBar.add(mnNewMenu);
		
		JMenu mnWindow = new JMenu(MTGControler.getInstance().getLangService().getCapitalize("WINDOW"));
		menuBar.add(mnWindow);
//		
//		JMenuItem mntmPackAll = new JMenuItem("Pack All");
//		mntmPackAll.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				logger.info("PACK ALL TODO");
//			}
//		});
//		setLayout(null);
//		mnWindow.add(mntmPackAll);
		
		JMenuItem mntmSaveDisplay = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("SAVE_DISPLAY"));
		mntmSaveDisplay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int i=0;
				
				try {
					FileUtils.cleanDirectory(AbstractJDashlet.confdir);
				} catch (IOException e1) {
					logger.error(e1);
				}
				
				for(JInternalFrame jif : getAllFrames())
				{
					i++;
					AbstractJDashlet dash = (AbstractJDashlet)jif;
									dash.save("x", String.valueOf(dash.getBounds().getX()));
									dash.save("y", String.valueOf(dash.getBounds().getY()));
									dash.save("w", String.valueOf(dash.getBounds().getWidth()));
									dash.save("h", String.valueOf(dash.getBounds().getHeight()));
									dash.save("class", dash.getClass().getName());
									dash.save("id", String.valueOf(i));
									try {
										File f = new File(AbstractJDashlet.confdir, i+".conf");
										FileOutputStream fos = new FileOutputStream(f);
										dash.getProperties().store(fos,"");
										fos.close();
									} catch (Exception e) {
										MTGLogger.printStackTrace(e);
									} 				

				}
				
			}
		});
		
		mnWindow.add(mntmSaveDisplay);
		add(menuBar);
		
		
		try {
			for(AbstractJDashlet dash : MTGControler.getInstance().getDashlets())
			{
						JMenuItem mntmNewMenuItem = new JMenuItem(dash.getName());
						mntmNewMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								try {
									//dash = (AbstractJDashlet)classLoader.loadClass(c.getName()).newInstance();
									addDash(dash);
								} 
								catch(Exception ex)
								{
									logger.error("Error Loading " + dash,ex);
								}
							}
						});
						mnNewMenu.add(mntmNewMenuItem);
			}
					
			
		} catch (Exception e) {
			logger.error("Error",e);
		}
				
		
		
		if(!AbstractJDashlet.confdir.exists())
			AbstractJDashlet.confdir.mkdir();
		
	
		
		
		for(File f : AbstractJDashlet.confdir.listFiles())
		{
			try {
				Properties p = new Properties();
				FileInputStream fis = new FileInputStream(f);
				p.load(fis);
				AbstractJDashlet dash = (AbstractJDashlet)classLoader.loadClass(p.get("class").toString()).newInstance();
				dash.setProperties(p);
				fis.close();
				addDash(dash);
				
			} catch (Exception e) {
				logger.error("Could not add " + f,e);
			}
		}
		
		
	
	}
	
	
	
	public void addDash(AbstractJDashlet dash)
	{
		dash.initGUI();
		dash.init();
		add(dash);
	}
	
	
}

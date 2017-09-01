package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.gui.abstracts.AbstractJDashlet;
import org.magic.services.MTGControler;
import org.magic.services.ModuleInstaller;

public class DashBoardGUI2 extends JDesktopPane {
	
	ClassLoader classLoader = DashBoardGUI2.class.getClassLoader();
	static final Logger logger = LogManager.getLogger(DashBoardGUI2.class.getName());

	
	public DashBoardGUI2() {
		setBackground(SystemColor.activeCaption);
		
		logger.info("init Dashboard GUI");
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 84, 21);
		
		JMenu mnNewMenu = new JMenu("Add");
		menuBar.add(mnNewMenu);
		
		JMenu mnWindow = new JMenu("Window");
		menuBar.add(mnWindow);
		
		JMenuItem mntmPackAll = new JMenuItem("Pack All");
		mntmPackAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				logger.info("PACK ALL TODO");
			}
		});
		setLayout(null);
		mnWindow.add(mntmPackAll);
		
		JMenuItem mntmSaveDisplay = new JMenuItem("Save display");
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
										e.printStackTrace();
									} 				

				}
				
			}
		});
		
		mnWindow.add(mntmSaveDisplay);
		add(menuBar);
		
		
		ModuleInstaller mods = new ModuleInstaller();
		List<Class> cls;
		try {
			cls = mods.getClasses("org.magic.gui.dashlet");
			
			for(final Class c : cls)
			{
				
				if(!c.isAnonymousClass())
				{
					if(!c.getName().contains("$"))
					{	
						JMenuItem mntmNewMenuItem = new JMenuItem(c.newInstance().toString());
						mntmNewMenuItem.addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								AbstractJDashlet dash = null;
								try {
									dash = (AbstractJDashlet)classLoader.loadClass(c.getName()).newInstance();
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
					
				}
			}
		} catch (Exception e) {
			logger.error("Error",e);
		}
				
		
		
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
	
	
	
	public static void main(String[] args) {
		JFrame f = new JFrame();
		MTGControler.getInstance().getEnabledProviders().init();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		f.setSize(1024, 768);
		f.getContentPane().add(new DashBoardGUI2(),BorderLayout.CENTER);
		f.setVisible(true);
		
	}
}

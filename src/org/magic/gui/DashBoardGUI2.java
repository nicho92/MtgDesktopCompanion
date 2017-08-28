package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.gui.abstracts.AbstractJDashlet;
import org.magic.services.MTGControler;
import org.magic.services.ModuleInstaller;

public class DashBoardGUI2 extends JDesktopPane {
	
	ClassLoader classLoader = DashBoardGUI2.class.getClassLoader();
	static final Logger logger = LogManager.getLogger(DashBoardGUI2.class.getName());

	
	public DashBoardGUI2() {
		
		logger.info("init dashboard GUI 2");
		
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 97, 21);
		add(menuBar);
		
		JMenu mnNewMenu = new JMenu("Add");
		menuBar.add(mnNewMenu);
		
		
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
									add(dash);
									dash.init();
									
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

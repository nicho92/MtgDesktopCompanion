package org.magic.api.main;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.interfaces.MTGServer;
import org.magic.gui.LaunchWindows;
import org.magic.gui.MagicGUI;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;

public class MtgDesktopCompanion {

	static final Logger logger = LogManager.getLogger(MtgDesktopCompanion.class.getName());
	public static LaunchWindows launch;
	
	public static void main(String[] args) {
		launch= new LaunchWindows();
		launch.start();
	
		try {
			if(MTGControler.getInstance().updateConfigMods())
				JOptionPane.showMessageDialog(null, "New modules has been installed.Please restart MTG Desktop Companion after loading");
		
				LogManager.getRootLogger().setLevel(Level.toLevel(MTGControler.getInstance().get("loglevel")));
				
				MTGControler.getInstance().getEnabledProviders().init();
				MTGControler.getInstance().getEnabledDAO().init();
		
				logger.info("Init MTG Desktop Companion GUI");
		}catch (Exception e) {
			logger.error("Error initialisation",e);
			JOptionPane.showMessageDialog(null, e,"ERROR",JOptionPane.ERROR_MESSAGE);
		}

		ThreadManager.getInstance().runInEdt(new Runnable() {
			public void run() {
				
				MagicGUI gui = new MagicGUI();
						 gui.setLookAndFeel(MTGControler.getInstance().get("lookAndFeel"));
						 gui.setExtendedState(JFrame.MAXIMIZED_BOTH); 
						 gui.setVisible(true);
				
				for(MTGServer serv : MTGControler.getInstance().getEnabledServers())
					if(serv.isAutostart())
						try {
								serv.start();
							} catch (Exception e) 
							{
								logger.error(e);
							}
						
				launch.stop();				
				
			}
		});
		
		

	}


}

package org.magic.api.main;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.interfaces.MTGServer;
import org.magic.gui.MagicGUI;
import org.magic.services.MTGDesktopCompanionControler;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.cache.Cache;
import com.jayway.jsonpath.spi.cache.CacheProvider;

public class MtgDesktopCompanion {

	static final Logger logger = LogManager.getLogger(MtgDesktopCompanion.class.getName());
	
	
	public static void main(String[] args) {

		CacheProvider.setCache(new Cache() {
			//Not thread safe simple cache
			private Map<String, JsonPath> map = new HashMap<String, JsonPath>();

			@Override
			public JsonPath get(String key) {
				return map.get(key);
			}

			@Override
			public void put(String key, JsonPath jsonPath) {
				map.put(key, jsonPath);
			}
		});

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					if(MTGDesktopCompanionControler.getInstance().updateConfigMods())
						MTGDesktopCompanionControler.getInstance().reload();
					
					LogManager.getRootLogger().setLevel(Level.toLevel(MTGDesktopCompanionControler.getInstance().get("loglevel")));
					
					MTGDesktopCompanionControler.getInstance().getEnabledProviders().init();
					MTGDesktopCompanionControler.getInstance().getEnabledDAO().init();
			
					
				}catch (Exception e) {
					JOptionPane.showMessageDialog(null, e,"ERROR",JOptionPane.ERROR_MESSAGE);
				}
			
				
				MagicGUI gui = new MagicGUI();
						 gui.setLookAndFeel(MTGDesktopCompanionControler.getInstance().get("lookAndFeel"));
						 gui.setExtendedState(JFrame.MAXIMIZED_BOTH); 
						 gui.setVisible(true);
				
				for(MTGServer serv : MTGDesktopCompanionControler.getInstance().getEnabledServers())
					if(serv.isAutostart())
						try {
								serv.start();
							} catch (Exception e) 
							{
								logger.error(e);
							}
						
								
				
			}
		});
		
		

	}


}

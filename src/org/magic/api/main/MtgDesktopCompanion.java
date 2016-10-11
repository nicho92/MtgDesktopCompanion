package org.magic.api.main;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.magic.api.interfaces.MTGServer;
import org.magic.gui.MagicGUI;
import org.magic.services.MagicFactory;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.cache.Cache;
import com.jayway.jsonpath.spi.cache.CacheProvider;

public class MtgDesktopCompanion {

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
					if(MagicFactory.getInstance().updateConfigMods())
						MagicFactory.getInstance().reload();
					
				}catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				MagicGUI gui = new MagicGUI();
						gui.setLookAndFeel(MagicFactory.getInstance().get("lookAndFeel"));
						gui.setDefaultLanguage(MagicFactory.getInstance().get("langage"));
						gui.setExtendedState(JFrame.MAXIMIZED_BOTH); 
						gui.setVisible(true);
						
						for(MTGServer serv : MagicFactory.getInstance().getEnabledServers())
							if(serv.isAutostart())
								try {
									serv.start();
								} catch (Exception e) {
									e.printStackTrace();
								}
						
								
				
			}
		});
		
		

	}


}

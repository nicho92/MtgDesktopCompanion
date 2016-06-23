package org.magic.api.main;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.magic.gui.MagicGUI;
import org.magic.tools.MagicFactory;
import org.magic.tools.ThreadManager;
import org.magic.tools.ThreadMonitor;

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
				new Thread(new ThreadMonitor(ThreadManager.getInstance().getExecutor(), 2)).start();	
				MagicGUI gui = new MagicGUI();
						gui.setLookAndFeel(MagicFactory.getInstance().get("lookAndFeel"));
						gui.setDefaultLanguage(MagicFactory.getInstance().get("langage"));
						gui.setExtendedState(JFrame.MAXIMIZED_BOTH); 
						gui.setVisible(true);
						
				
			}
		});

	}


}

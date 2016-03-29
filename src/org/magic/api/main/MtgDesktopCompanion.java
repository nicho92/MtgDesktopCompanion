package org.magic.api.main;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.magic.gui.MagicGUI;

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
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
					UIManager.put("Table.alternateRowColor", Color.decode("#E1E4F2"));
				} catch (Exception e) {
					e.printStackTrace();
				}

				MagicGUI gui = new MagicGUI();
				gui.setDefaultLanguage("English");
				gui.setVisible(true);

			}
		});

	}


}

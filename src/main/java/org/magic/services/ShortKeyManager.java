package org.magic.services;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JButton;

import org.apache.logging.log4j.Logger;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.IDGenerator;

public class ShortKeyManager {
	protected static Logger logger = MTGLogger.getLogger(ShortKeyManager.class);
	private static ShortKeyManager inst;
	private Map<String,JButton> mapping;
	private File configFile = new File(MTGConstants.DATA_DIR,"shortcuts.config");

	public ShortKeyManager() {
		mapping=new HashMap<>();
	}


	public static ShortKeyManager inst() {
		if(inst ==null)
			inst = new ShortKeyManager();

		return inst;
	}

	public void removeMnemonic(JButton b)
	{
		if(b==null)
			return;

		b.setMnemonic(0);
	}

	public void setShortCutTo(int key, JButton b) {

		var tt= b.getToolTipText();
		b.setMnemonic(key);

		if(tt==null)
			b.setToolTipText(b.getName() + "( Alt+" + KeyEvent.getKeyText(key)+" )");
		else
			b.setToolTipText(tt + " ( Alt+" + KeyEvent.getKeyText(key)+" )");

		mapping.put(keyfor(b),b);
	}

	private String keyfor(JButton b) {
		return IDGenerator.generateMD5(b.getName()+b.getText()+b.getIcon());
	}


	public List<JButton> getMapping() {
		return new ArrayList<>(mapping.values());
	}




	public void store() {
		var p = new Properties();
		mapping.entrySet().forEach(e->p.put(e.getKey(), String.valueOf(e.getValue().getMnemonic())));

		try {
			FileTools.saveProperties(configFile, p);
		} catch (IOException e1) {
			MTGControler.getInstance().notify(e1);
		}
	}

	public void load()
	{
		var p = new Properties();
		try {
			FileTools.loadProperties(configFile, p);
		} catch (IOException e1) {
			logger.warn(e1.getMessage());
			return;
		}

		p.entrySet().forEach(e->{
			try {
				var b = mapping.get(e.getKey());
				if(b!=null)
					b.setMnemonic(Integer.parseInt(e.getValue().toString()));

			}catch(Exception ex)
			{
				logger.error("error loading {}",e.getKey(),ex);
			}
		});

	}


}

package org.magic.tools;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JButton;

public class ShortKeyManager {

	private static ShortKeyManager inst;
	private List<JButton> mapping;
	
	public ShortKeyManager() {
		mapping=new ArrayList<>();
	}
	
	
	public static ShortKeyManager inst() {
		if(inst ==null)
			inst = new ShortKeyManager();
		
		return inst;
	}
	
	public void removeMnemonic(JButton b)
	{
		b.setMnemonic(0);
		mapping.remove(b);
	}
	
	public void setShortCutTo(int key, JButton b) {
		b.setMnemonic(key);
		b.setToolTipText(b.getToolTipText()==null?"":b.getToolTipText() + " ( Alt+" + KeyEvent.getKeyText(key)+" )");
		mapping.add(b);
	}
	
	public List<JButton> getMapping() {
		return mapping;
	}
	
	
	

}

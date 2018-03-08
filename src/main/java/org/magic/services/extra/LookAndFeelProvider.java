package org.magic.services.extra;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.magic.gui.MagicGUI;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.reflections.Reflections;

public class LookAndFeelProvider {

	private Logger logger = MTGLogger.getLogger(this.getClass());
	private List<LookAndFeelInfo> list;
	
	
	public LookAndFeelProvider() {
		list=new ArrayList<>();
	}

	public void setComponentLookAndFeel(Component ui, LookAndFeelInfo lookAndFeel) {
		setLookAndFeel(ui, lookAndFeel.getClassName());
	}

	public void setLookAndFeel(Container container, LookAndFeelInfo lookAndFeel) {
		setLookAndFeel(container, lookAndFeel.getClassName());
	}

	public void cleanExtra()
	{
		list.clear();
	}
	
	public void setLookAndFeel(Component ui, String lookAndFeel) {
		try {
			UIManager.setLookAndFeel(lookAndFeel);
			MTGControler.getInstance().setProperty("lookAndFeel", lookAndFeel);
			SwingUtilities.updateComponentTreeUI(ui);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public LookAndFeelInfo[] getStandardLookAndFeel()
	{
		return UIManager.getInstalledLookAndFeels();
		
	}
	
	public LookAndFeelInfo[] getAllLookAndFeel()
	{
		
		return ArrayUtils.addAll(UIManager.getInstalledLookAndFeels(), getExtraLookAndFeel());
	}
	
	
	public LookAndFeelInfo[] getExtraLookAndFeel()
	{
		 
		if(!list.isEmpty())
			return list.toArray(new LookAndFeelInfo[list.size()]);
		
		
		 Reflections classReflections = new Reflections("org.pushingpixels.substance.api.skin");
		 list = new ArrayList<>();
		 for(Class<? extends SubstanceLookAndFeel> c :classReflections.getSubTypesOf(SubstanceLookAndFeel.class) )
		 {
			try {
				SubstanceLookAndFeel look = c.getConstructor(null).newInstance();
				list.add(new LookAndFeelInfo(look.getID(), c.getName()));
			} catch (Exception e) {
				logger.error("Loading " + c,e);
			} 
		}
		return list.toArray(new LookAndFeelInfo[list.size()]);
	}


}

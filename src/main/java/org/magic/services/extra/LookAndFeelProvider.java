package org.magic.services.extra;

import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.Logger;
import org.magic.services.MTGLogger;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.reflections.Reflections;

public class LookAndFeelProvider {

	Logger logger = MTGLogger.getLogger(this.getClass());

	
	public LookAndFeelInfo[] getStandardLookAndFeel()
	{
		return UIManager.getInstalledLookAndFeels();
		
	}
	
	
	public LookAndFeelInfo[] getExtraLookAndFeel()
	{
		 Reflections classReflections = new Reflections("org.pushingpixels.substance.api.skin");
		 
		 List<LookAndFeelInfo> list = new ArrayList<>();
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
	
	
	public static void main(String[] args) {
		new LookAndFeelProvider().getExtraLookAndFeel();
	}
	
	
}

package org.magic.gui;

import java.io.IOException;
import java.util.Properties;

import org.jdesktop.swingx.JXTipOfTheDay;
import org.jdesktop.swingx.tips.TipLoader;
import org.jdesktop.swingx.tips.TipOfTheDayModel;
import org.magic.services.MTGDesktopCompanionControler;

public class TipsOfTheDayDialog extends JXTipOfTheDay{

	private static final long serialVersionUID = 1L;

	public TipsOfTheDayDialog() throws IOException {
		Properties tips = new Properties();
		tips.load(TipsOfTheDayDialog.class.getResource("/res/tips.properties").openStream());
		TipOfTheDayModel model = TipLoader.load(tips);
		setModel(model);
		
	}
	
	public void show()
	{
		showDialog (null, new JXTipOfTheDay.ShowOnStartupChoice ()
	      {
	       public boolean isShowingOnStartup ()
	       {
	        return MTGDesktopCompanionControler.getInstance().get("tooltip","true").equalsIgnoreCase("true");
	       }

	       public void setShowingOnStartup (boolean x)
	       {
	        if (x)
	        	MTGDesktopCompanionControler.getInstance().setProperty("tooltip", "true");
	        else
	        	MTGDesktopCompanionControler.getInstance().setProperty("tooltip", "false");
	       }
	      });
	}
	
	
}

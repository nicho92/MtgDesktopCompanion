package org.magic.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.jdesktop.swingx.JXTipOfTheDay;
import org.jdesktop.swingx.tips.TipLoader;
import org.jdesktop.swingx.tips.TipOfTheDayModel;
import org.magic.services.MTGDesktopCompanionControler;

public class TestToolTip {

	public static void main(String[] args) throws FileNotFoundException, IOException {

		Properties tips = new Properties();
		tips.load(TestToolTip.class.getResource("/res/tips.properties").openStream());
		
		TipOfTheDayModel model = TipLoader.load(tips);
		
		JXTipOfTheDay totd = new JXTipOfTheDay (model);
		
		totd.showDialog (null, new JXTipOfTheDay.ShowOnStartupChoice ()
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

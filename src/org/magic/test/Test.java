package org.magic.test;

import java.util.Arrays;
import java.util.List;

import org.jdesktop.swingx.JXTipOfTheDay;
import org.jdesktop.swingx.tips.DefaultTip;
import org.jdesktop.swingx.tips.DefaultTipOfTheDayModel;
import org.jdesktop.swingx.tips.TipOfTheDayModel;
import org.magic.services.MTGDesktopCompanionControler;

public class Test {

	public static void main(String[] args) {

		
		
		TipOfTheDayModel.Tip tip1;
		tip1 = new DefaultTip ("Tip 1",
		      "<html>You can <strong>embed</strong> various " +
		      "<em>HTML</em> tags in tips.");

		TipOfTheDayModel.Tip tip2;
		tip2 = new DefaultTip ("Tip 2",
		      "JXTipOfTheDay provides methods that let you " +
		      "programmatically navigate tips when you use " +
		      "JXTipOfTheDay as a pane.");

		List<TipOfTheDayModel.Tip> tips = Arrays.asList (tip1, tip2);
		DefaultTipOfTheDayModel model = new DefaultTipOfTheDayModel (tips);

		
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

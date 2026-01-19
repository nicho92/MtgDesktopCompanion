package org.magic.gui.components.dialog;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jdesktop.swingx.JXTipOfTheDay;
import org.jdesktop.swingx.tips.TipLoader;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class TipsOfTheDayDialog extends JXTipOfTheDay {

	private static final long serialVersionUID = 1L;

	public TipsOfTheDayDialog() throws IOException {
		var tips = new Properties();

		try(InputStream st = MTGConstants.TOOLTIPS_FILE.openStream())
		{
			tips.load(st);
		}
		setModel(TipLoader.load(tips));

	}

	public void shows() {
		var key = "tooltip";
		showDialog(null, new JXTipOfTheDay.ShowOnStartupChoice() {
			@Override
			public boolean isShowingOnStartup() {
				return MTGControler.getInstance().get(key, "true").equalsIgnoreCase("true");
			}

			@Override
			public void setShowingOnStartup(boolean x) {
				if (x)
					MTGControler.getInstance().setProperty(key, "true");
				else
					MTGControler.getInstance().setProperty(key, "false");
			}
		});
	}


}

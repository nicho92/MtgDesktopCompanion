package org.magic.gui.abstracts;

import java.net.URL;

import javax.swing.ImageIcon;

import org.magic.gui.components.browser.ChromiumBrowserComponent;
import org.magic.gui.components.browser.JEditorPaneBrowser;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public abstract class MTGUIBrowserComponent extends MTGUIComponent {

	
	private static final long serialVersionUID = 1L;


	public abstract void loadURL(String url);
	
	
	public void loadURL(URL url)
	{
		loadURL(url.toString());
	}
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_CHROME;
	}

	@Override
	public String getTitle() {
		return "Browser";
	}


	public static MTGUIBrowserComponent createBrowser()
	{
		
		if(MTGControler.getInstance().get("ui/chromedisabled").equals("true"))
			return new JEditorPaneBrowser();
		
		try {
			return new ChromiumBrowserComponent();
		}
		catch(Exception e)
		{
			logger.error("error loading chromium. Loading default",e);
			return new JEditorPaneBrowser();
		}
	}


}

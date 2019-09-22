package org.magic.gui.abstracts;

import java.net.URL;

import javax.swing.ImageIcon;

import org.magic.services.MTGConstants;

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


}

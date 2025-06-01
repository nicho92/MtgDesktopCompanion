package org.magic.gui.abstracts;

import javax.swing.ImageIcon;

import org.magic.gui.components.browser.ChromiumBrowserComponent;
import org.magic.gui.components.browser.JEditorPaneBrowser;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public abstract class MTGUIBrowserComponent extends MTGUIComponent {


	private static final long serialVersionUID = 1L;


	public abstract void loadURL(String url);
	public abstract String getCurrentURL();
	protected transient Observable observable;

	protected MTGUIBrowserComponent() {
		observable = new Observable();
	}

	public void addObserver(Observer o)
	{
		observable.addObserver(o);
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
		catch(Exception _)
		{
			return new JEditorPaneBrowser();
		}
	}





}

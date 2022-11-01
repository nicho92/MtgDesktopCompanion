package org.magic.gui.components.browser;

import java.awt.BorderLayout;
import java.io.IOException;

import org.cef.browser.CefBrowser;
import org.cef.handler.CefLoadHandlerAdapter;
import org.magic.gui.abstracts.MTGUIBrowserComponent;
import org.magic.services.MTGConstants;
import org.magic.services.tools.UITools;
import org.panda_lang.pandomium.wrapper.PandomiumClient;

public class ChromiumBrowserComponent extends MTGUIBrowserComponent {

	private static final long serialVersionUID = 1L;
	private transient PandomiumClient client;
	private transient CefBrowser browser;
	private String currentUrl;


	public ChromiumBrowserComponent() throws IOException {
		setLayout(new BorderLayout());


		try {
			client = UITools.getPandomiumInstance().createClient();
			browser = client.loadURL("about:blank");
			add(browser.getUIComponent(),BorderLayout.CENTER);

			client.getCefClient().addLoadHandler(new CefLoadHandlerAdapter() {

				@Override
				public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {
					if(!isLoading)
					{
						observable.setChanged();
						observable.notifyObservers(browser.getURL());
					}
				}

			});


		} catch (UnsatisfiedLinkError e) {
			logger.error("maybe add : -Djava.library.path=\"{}\" at jvm startup args",MTGConstants.NATIVE_DIR);
			throw new IOException(e);
		}

	}




	@Override
	public String getCurrentURL() {
		return currentUrl;

	}

	@Override
	public void loadURL(String url) {
		logger.debug("browse to {}",url);
		browser.loadURL(url);
	}



}

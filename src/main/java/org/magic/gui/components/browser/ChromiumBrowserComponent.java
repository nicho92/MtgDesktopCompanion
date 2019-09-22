package org.magic.gui.components.browser;

import java.awt.BorderLayout;
import java.io.IOException;
import java.lang.reflect.Field;

import org.magic.gui.abstracts.MTGUIBrowserComponent;
import org.magic.services.MTGConstants;
import org.panda_lang.pandomium.Pandomium;
import org.panda_lang.pandomium.settings.PandomiumSettings;
import org.panda_lang.pandomium.wrapper.PandomiumBrowser;
import org.panda_lang.pandomium.wrapper.PandomiumClient;

public class ChromiumBrowserComponent extends MTGUIBrowserComponent {

	private static final long serialVersionUID = 1L;
	private transient PandomiumClient client;
	private transient PandomiumBrowser browser;
	
	public ChromiumBrowserComponent() throws IOException {
		setLayout(new BorderLayout());
		PandomiumSettings.getDefaultSettings();
		PandomiumSettings setts = PandomiumSettings.getDefaultSettingsBuilder()
										.nativeDirectory(MTGConstants.NATIVE_DIR.getAbsolutePath())
										.loadAsync(false)
										.build();
		
		System.setProperty("java.library.path", MTGConstants.NATIVE_DIR.getAbsolutePath() );

		try {
			
			Pandomium pandomium = new Pandomium(setts);
			logger.debug("loading pandomium with " + pandomium.getLoader());
			
			pandomium.initialize();
			
			client = pandomium.createClient();
			browser = client.loadURL("about:blank");
			add(browser.toAWTComponent(),BorderLayout.CENTER);
		} catch (Exception e) {
			throw new IOException(e);
		} 
			
	}


	@Override
	public void loadURL(String url) {
		logger.debug("browse to " + url);
		browser.getCefBrowser().loadURL(url);
	}

	

}

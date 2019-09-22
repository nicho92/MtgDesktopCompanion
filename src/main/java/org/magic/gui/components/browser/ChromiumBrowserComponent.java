package org.magic.gui.components.browser;

import java.awt.BorderLayout;
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
	
	public ChromiumBrowserComponent() {
		setLayout(new BorderLayout());
		PandomiumSettings.getDefaultSettings();
		PandomiumSettings setts = PandomiumSettings.getDefaultSettingsBuilder()
										.nativeDirectory(MTGConstants.NATIVE_DIR.getAbsolutePath())
										.loadAsync(false)
										.build();
		
		System.setProperty("java.library.path", MTGConstants.NATIVE_DIR.getAbsolutePath() );
		try {
			Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
			fieldSysPath.setAccessible( true );
			fieldSysPath.set( null, null );

		} catch (Exception e) {
			logger.error("error settings natives " + e + ": " + System.getProperty("java.library.path"));
		} 
		
		try {
		
		Pandomium pandomium = new Pandomium(setts);
		pandomium.initialize();
		
		client = pandomium.createClient();
		browser = client.loadURL("about:blank");
		add(browser.toAWTComponent(),BorderLayout.CENTER);
		} catch (Exception e) {
			logger.error("error init chromium ", e);
		} 
			
	}


	@Override
	public void loadURL(String url) {
		logger.debug("browse to " + url);
		browser.getCefBrowser().loadURL(url);
	}

	

}

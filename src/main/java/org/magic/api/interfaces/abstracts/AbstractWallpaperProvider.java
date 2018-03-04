package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.util.Properties;

import org.magic.api.interfaces.MTGWallpaperProvider;
import org.magic.services.MTGConstants;

public abstract class AbstractWallpaperProvider extends AbstractMTGPlugin implements MTGWallpaperProvider {
	
	public AbstractWallpaperProvider() {
		confdir = new File(MTGConstants.CONF_DIR, "wallpapers");
		props=new Properties();
		if(!confdir.exists())
			confdir.mkdir();
		load();
	}
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.WALLPAPER;
	}

}

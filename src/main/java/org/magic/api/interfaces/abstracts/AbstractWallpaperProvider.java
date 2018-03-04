package org.magic.api.interfaces.abstracts;

import java.io.File;

import org.magic.api.interfaces.MTGWallpaperProvider;
import org.magic.services.MTGConstants;

public abstract class AbstractWallpaperProvider extends AbstractMTGPlugin implements MTGWallpaperProvider {
	
	public AbstractWallpaperProvider() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "wallpapers");
		if(!confdir.exists())
			confdir.mkdir();
		load();
	}
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.WALLPAPER;
	}

}

package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Wallpaper;
import org.magic.api.interfaces.MTGWallpaperProvider;
import org.magic.services.MTGConstants;

public abstract class AbstractWallpaperProvider extends AbstractMTGPlugin implements MTGWallpaperProvider {

	public AbstractWallpaperProvider() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "wallpapers");
		if (!confdir.exists())
			confdir.mkdir();
		load();

		if (!new File(confdir, getName() + ".conf").exists()) {
			initDefault();
			save();
		}
	}
	
	

	@Override
	public List<Wallpaper> search(MagicEdition ed) {
		return search(ed.getSet());
	}

	@Override
	public List<Wallpaper> search(MagicCard card) {
		return search(card.getName());
	}

	@Override
	public PLUGINS getType() {
		return PLUGINS.WALLPAPER;
	}

}

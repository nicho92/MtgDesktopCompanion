package org.magic.api.interfaces.abstracts;

import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGWallpaper;
import org.magic.api.interfaces.MTGWallpaperProvider;

public abstract class AbstractWallpaperProvider extends AbstractMTGPlugin implements MTGWallpaperProvider {
	@Override
	public List<MTGWallpaper> search(MTGEdition ed) {
		return search(ed.getSet());
	}

	@Override
	public List<MTGWallpaper> search(MTGCard card) {
		return search(card.getName());
	}

	@Override
	public PLUGINS getType() {
		return PLUGINS.WALLPAPER;
	}

}

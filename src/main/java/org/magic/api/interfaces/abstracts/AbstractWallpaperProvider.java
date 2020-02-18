package org.magic.api.interfaces.abstracts;

import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Wallpaper;
import org.magic.api.interfaces.MTGWallpaperProvider;

public abstract class AbstractWallpaperProvider extends AbstractMTGPlugin implements MTGWallpaperProvider {
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

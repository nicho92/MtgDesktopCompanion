package org.magic.api.interfaces;

import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGWallpaper;

public interface MTGWallpaperProvider extends MTGPlugin {

	public List<MTGWallpaper> search(String search);

	public List<MTGWallpaper> search(MTGEdition ed);

	public List<MTGWallpaper> search(MTGCard card);

}

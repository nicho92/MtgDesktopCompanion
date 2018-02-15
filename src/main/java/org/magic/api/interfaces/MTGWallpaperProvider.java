package org.magic.api.interfaces;

import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Wallpaper;

public interface MTGWallpaperProvider extends MTGPlugin {

	
	public List<Wallpaper> search(String search);
	public List<Wallpaper> search(MagicEdition ed);
	public List<Wallpaper> search(MagicCard card);
	
	
}

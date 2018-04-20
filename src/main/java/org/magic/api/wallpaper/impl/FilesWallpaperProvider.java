package org.magic.api.wallpaper.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Wallpaper;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;

public class FilesWallpaperProvider extends AbstractWallpaperProvider {

	@Override
	public List<Wallpaper> search(String search) {
		List<Wallpaper> list = new ArrayList<>();
		try {

			Collection<File> res = FileUtils.listFiles(new File(getString("DIRECTORY")), new String[] { "png", "jpg" },
					true);

			for (File f : res) {
				Wallpaper w = new Wallpaper();
				w.setName(f.getName());
				w.setUrl(Paths.get(f.toURI()).toUri().toURL());
				w.setFormat(FilenameUtils.getExtension(w.getUrl().toString()));
				list.add(w);
			}
			return list;
		} catch (IOException e) {
			logger.error(e);
			return list;
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
	public String getName() {
		return "Filer";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public void initDefault() {
		setProperty("DIRECTORY", ".");
		setProperty("PREF_SIZE", "");
	}

	@Override
	public String getVersion() {
		return "0.1";
	}

}

package org.magic.api.wallpaper.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Wallpaper;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.services.tools.FileTools;

public class FilesWallpaperProvider extends AbstractWallpaperProvider {

	@Override
	public List<Wallpaper> search(String search) {
		List<Wallpaper> list = new ArrayList<>();
		try {
			
			Collection<File> res = FileTools.listFiles(getFile("DIRECTORY"),WildcardFileFilter.builder().setWildcards("*"+search+"*").setIoCase(IOCase.INSENSITIVE).get(),TrueFileFilter.INSTANCE);

			for (File f : res) {
				var w = new Wallpaper();
				w.setName(f.getName());
				w.setUrl(f.toURI());
				w.setFormat(FilenameUtils.getExtension(w.getUrl().toString()));
				list.add(w);
				notify(w);
			}
			return list;
		} catch (Exception e) {
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
		return "File";
	}


	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("DIRECTORY", ".",
								"PREF_SIZE", "");
	}

	@Override
	public String getVersion() {
		return "0.1";
	}

}

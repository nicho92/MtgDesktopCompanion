package org.magic.api.wallpaper.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGWallpaper;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.services.MTGConstants;
import org.magic.services.tools.FileTools;

public class FilesWallpaperProvider extends AbstractWallpaperProvider {

	@Override
	public List<MTGWallpaper> search(String search) {
		List<MTGWallpaper> list = new ArrayList<>();
		try {
			
			Collection<File> res = FileTools.listFiles(getFile("DIRECTORY"),WildcardFileFilter.builder().setWildcards("*"+search+"*").setIoCase(IOCase.INSENSITIVE).get(),TrueFileFilter.INSTANCE);

			for (File f : res) {
				var w = new MTGWallpaper();
				w.setName(f.getName());
				w.setUrl(f.toURI());
				w.setUrlThumb(f.toURI());
				w.setFormat(FilenameUtils.getExtension(w.getUrl().toString()));
				w.setPublishDate(new Date(f.lastModified()));
				w.setProvider(getName());
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
	public List<MTGWallpaper> search(MTGEdition ed) {
		return search(ed.getSet());
	}

	@Override
	public List<MTGWallpaper> search(MTGCard card) {
		return search(card.getName());
	}

	@Override
	public String getName() {
		return "File";
	}


	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("DIRECTORY", MTGProperty.newDirectoryProperty(MTGConstants.MTG_WALLPAPER_DIRECTORY));
	}

	@Override
	public String getVersion() {
		return "0.1";
	}

}

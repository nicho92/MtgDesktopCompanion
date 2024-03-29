package org.magic.api.wallpaper.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGWallpaper;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.services.network.URLTools;

public class ArtOfMtgWallpaperProvider extends AbstractWallpaperProvider {


	@Override
	public List<MTGWallpaper> search(String search) {
		List<MTGWallpaper> list = new ArrayList<>();
		try {

			Document d = URLTools.extractAsHtml(getString("URL") + "/?s=" + URLTools.encode(search));

			for (Element e : d.select("article.result")) {
				var w = new MTGWallpaper();
				w.setName(e.select("a img").attr("title"));
				w.setUrl(new URI(e.select("a img").attr("data-src")));
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
	public List<MTGWallpaper> search(MTGEdition ed) {
		List<MTGWallpaper> list = new ArrayList<>();
		try {
			var d = URLTools.extractAsHtml(getString("URL") + "/set/" + ed.getSet().toLowerCase().replace(" ", "-"));
			for (Element e : d.select("div.elastic-portfolio-item img")) {
				var w = new MTGWallpaper();
				w.setName(e.attr("title"));
				w.setUrl(new URI(e.attr("src")));
				w.setFormat(FilenameUtils.getExtension(w.getUrl().toString()));
				list.add(w);
			}
			return list;
		} catch (Exception e) {
			logger.error(e);
			return list;
		}

	}

	@Override
	public List<MTGWallpaper> search(MTGCard card) {
		return search(card.getName());
	}

	@Override
	public String getName() {
		return "Art of Mtg";
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("URL", "http://www.artofmtg.com");
	}

	@Override
	public String getVersion() {
		return "0.5";
	}

}

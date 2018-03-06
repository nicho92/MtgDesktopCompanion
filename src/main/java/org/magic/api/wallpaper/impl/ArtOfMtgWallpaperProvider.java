package org.magic.api.wallpaper.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Wallpaper;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.services.MTGConstants;

public class ArtOfMtgWallpaperProvider extends  AbstractWallpaperProvider {

	@Override
	public List<Wallpaper> search(String search) {
		List<Wallpaper> list = new ArrayList<>();
		try {
			
			Document d = Jsoup.connect(getProperty("URL")+"/?s="+search)
					  .userAgent(MTGConstants.USER_AGENT)
					  .get();
			
			for(Element e : d.select("article.result"))
			{
				Wallpaper w = new Wallpaper();
				w.setName(e.select("h2 a").text());
				w.setUrl(new URL(e.select("a img").attr("src")));
				w.setFormat(FilenameUtils.getExtension(w.getUrl().toString()));
				list.add(w);
			}
			return list;
		}
		catch (IOException e) {
			logger.error(e);
			return list;
		}
		
		
	}
	
	@Override
	public List<Wallpaper> search(MagicEdition ed) {
		List<Wallpaper> list = new ArrayList<>();
		try {
			
			Document d = Jsoup.connect(getProperty("URL")+"/set/"+ed.getSet().toLowerCase().replaceAll(" ", "-"))
					  .userAgent(getProperty("USER_AGENT", MTGConstants.USER_AGENT))
					  .get();
			
			for(Element e : d.select("div.elastic-portfolio-item img"))
			{
				Wallpaper w = new Wallpaper();
				w.setName(e.attr("title"));
				w.setUrl(new URL(e.attr("src")));
				w.setFormat(FilenameUtils.getExtension(w.getUrl().toString()));
				list.add(w);
			}
			return list;
		}
		catch (IOException e) {
			logger.error(e);
			return list;
		}
		
	}

	@Override
	public List<Wallpaper> search(MagicCard card) {
		return search(card.getName());
	}

	@Override
	public String getName() {
		return "Art of Mtg";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public void initDefault() {
		setProperty("URL", "http://www.artofmtg.com");
		setProperty("USER_AGENT", MTGConstants.USER_AGENT);
		
	}
	
	

}

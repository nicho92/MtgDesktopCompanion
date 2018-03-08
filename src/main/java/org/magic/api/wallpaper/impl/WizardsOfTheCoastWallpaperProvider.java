package org.magic.api.wallpaper.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Wallpaper;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.services.MTGConstants;

import com.google.gson.JsonParser;

public class WizardsOfTheCoastWallpaperProvider extends AbstractWallpaperProvider {

	
	private String read(String url) throws IOException
	{
		logger.debug("retrieve from " + url);
		HttpClient httpClient = HttpClients.custom()
				   .setUserAgent(getString("USER_AGENT"))
				   .setRedirectStrategy(new LaxRedirectStrategy())
				   .build();
		HttpGet req = new HttpGet(url);
				req.addHeader("content-type", "application/json");
				HttpResponse resp = httpClient.execute(req);
		return EntityUtils.toString(resp.getEntity());
	}
	
	@Override
	public List<Wallpaper> search(String search) {
		return construct(getString("URL")+"?page=1&filter_by=DESC&artist=-1&expansion=&title="+search+"&is_search=1");
	}
	
	private List<Wallpaper> construct(String url) {
		ArrayList<Wallpaper> list = new ArrayList<>();
		try {
			String json = read(url);
			
			String doc = new JsonParser().parse(json).getAsJsonObject().get("data").getAsString();
			
			for(Element e : Jsoup.parse(doc).select("div.wrap"))
			{
				Wallpaper w = new Wallpaper();
				w.setName(e.getElementsByTag("h3").text());
				w.setUrl(new URL(e.select("a").first().attr("download")));
				w.setFormat(FilenameUtils.getExtension(String.valueOf(w.getUrl())));
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
		return construct(getString("URL")+"?page=1&filter_by=DESC&artist=-1&expansion="+ed.getSet()+"&title=&is_search=1");
	}

	@Override
	public List<Wallpaper> search(MagicCard card) {
		return search(card.getName());
	}

	@Override
	public String getName() {
		return "WizardsOfTheCoast";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public void initDefault() {
		setProperty("URL", "https://magic.wizards.com/en/see-more-wallpaper");
		setProperty("USER_AGENT", MTGConstants.USER_AGENT);
	}

	@Override
	public String getVersion() {
		return "0.1";
	}

}

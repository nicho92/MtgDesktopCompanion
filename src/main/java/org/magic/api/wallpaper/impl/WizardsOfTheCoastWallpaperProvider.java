package org.magic.api.wallpaper.impl;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGWallpaper;
import org.magic.api.interfaces.abstracts.AbstractMTGPlugin;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;

public class WizardsOfTheCoastWallpaperProvider extends AbstractWallpaperProvider {

	private String read(String url) throws IOException {
		logger.debug("retrieve from {}",url);
		HttpClient httpClient = HttpClients.custom().setUserAgent(MTGConstants.USER_AGENT)
				.setRedirectStrategy(new LaxRedirectStrategy()).build();
		var req = new HttpGet(url);
		req.addHeader("content-type", URLTools.HEADER_JSON);
		var resp = httpClient.execute(req);
		return EntityUtils.toString(resp.getEntity());
	}

	@Override
	public List<MTGWallpaper> search(String search) {
		return construct(
				getString("URL") + "?page=1&filter_by=DESC&artist=-1&expansion=&title=" + search + "&is_search=1");
	}

	private List<MTGWallpaper> construct(String url) {
		ArrayList<MTGWallpaper> list = new ArrayList<>();
		try {
			var json = read(url);

			var doc = URLTools.toJson(json).getAsJsonObject().get("data").getAsString();

			for (Element e : Jsoup.parse(doc).select("div.wrap")) {
				var w = new MTGWallpaper();
				w.setName(e.getElementsByTag("h3").text());
				w.setUrl(new URI(e.select("a").first().attr("download")));
				w.setFormat(FilenameUtils.getExtension(String.valueOf(w.getUrl())));
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
		return construct(
				getString("URL") + "?page=1&filter_by=DESC&artist=-1&expansion=" + ed.getSet() + "&title=&is_search=1");
	}

	@Override
	public String getName() {
		return "Wizard Of The Coast";
	}


	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("URL", "https://magic.wizards.com/en/see-more-wallpaper");

	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(AbstractMTGPlugin.class.getResource("/icons/plugins/gatherer.png"));
	}

	@Override
	public String getVersion() {
		return "0.1";
	}

}

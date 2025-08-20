package org.magic.api.wallpaper.impl;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.magic.api.beans.MTGWallpaper;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.URLTools;

public class ImgUrWallPaperProvider extends AbstractWallpaperProvider {

	private static final String IMAGES_TAG = "images";
	private static final String TITLE_TAG = "title";
	private static final String CLIENTID="CLIENTID";

	@Override
	public List<MTGWallpaper> search(String search) {


		List<MTGWallpaper> ret = new ArrayList<>();
		MTGHttpClient c = URLTools.newClient();
		Map<String,String> h = new HashMap<>();
		Map<String,String> e = new HashMap<>();

		if(getAuthenticator().get(CLIENTID)==null)
		{
			logger.error("please fill CLIENTID attribute in config panel");
			return ret;
		}

		try {

			String query=search.trim().replace(" ", " AND ");

			e.put("q", query);
			e.put("mature", getString("MATURE"));
			h.put(URLTools.AUTHORIZATION,"Client-ID "+getAuthenticator().get(CLIENTID));


			String s= c.toString(c.doGet("https://api.imgur.com/3/gallery/search/"+getString("SORT").toLowerCase()+"/"+getString("WINDOW"), h,e));
			
			logger.debug("return : {}",s);
			
			URLTools.toJson(s).getAsJsonObject().get("data").getAsJsonArray().forEach(je->{

				var defaultTitle =je.getAsJsonObject().get(TITLE_TAG).getAsString();

				if(je.getAsJsonObject().get(IMAGES_TAG)!=null)
				{
					je.getAsJsonObject().get(IMAGES_TAG).getAsJsonArray().forEach(im->{
						var w = new MTGWallpaper();

						if(!im.getAsJsonObject().get(TITLE_TAG).isJsonNull())
							w.setName(im.getAsJsonObject().get(TITLE_TAG).getAsString());
						else
							w.setName(defaultTitle);
						
						if(je.getAsJsonObject().get("account_url")!=null)
							w.setAuthor(je.getAsJsonObject().get("account_url").getAsString());
						
						
						w.setMature(je.getAsJsonObject().get("nsfw").getAsBoolean());
						w.setUrl(URI.create(im.getAsJsonObject().get("link").getAsString()));
						w.setUrlThumb(URI.create(im.getAsJsonObject().get("link").getAsString()));
						w.setFormat(FilenameUtils.getExtension(String.valueOf(w.getUrl())));
						w.setPublishDate(new Date(im.getAsJsonObject().get("datetime").getAsLong()*1000));
						w.setProvider(getName());
					
						for( var el : je.getAsJsonObject().get("tags").getAsJsonArray())
							w.getTags().add(el.getAsJsonObject().get("name").getAsString());
							
						ret.add(w);
						notify(w);
					});
				}
				else
				{
					var w = new MTGWallpaper();
							w.setName(defaultTitle);
							w.setUrl(URI.create(je.getAsJsonObject().get("link").getAsString()));
							w.setFormat(FilenameUtils.getExtension(String.valueOf(w.getUrl())));

					ret.add(w);
					notify(w);
				}




			});
		} catch (IOException ex) {
			logger.error(ex);
		}

		logger.info("{} return {} results", getName(), ret.size());
		return ret;

	}


	@Override
	public String getName() {
		return "Imgur";
	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of(CLIENTID);
	}


	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("SORT", new MTGProperty("time", "The default sorter of the results", "time","viral","top"),
							 "WINDOW", new MTGProperty("all", "Change the date range of the request if the sort is top","day"," week","month","year","all"),
							"MATURE", MTGProperty.newBooleanProperty("false", "set to true if you want to return mature content") 
							);
	}
}

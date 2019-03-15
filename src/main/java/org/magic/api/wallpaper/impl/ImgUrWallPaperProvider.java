package org.magic.api.wallpaper.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RegExUtils;
import org.magic.api.beans.Wallpaper;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.services.MTGConstants;
import org.magic.tools.URLTools;
import org.magic.tools.URLToolsClient;

import com.google.gson.JsonArray;

public class ImgUrWallPaperProvider extends AbstractWallpaperProvider {

	private static final String IMAGES_TAG = "images";
	private static final String TITLE_TAG = "title";

	public static void main(String[] args){
		
		new ImgUrWallPaperProvider().search("liliana of the veil");
	}

	@Override
	public List<Wallpaper> search(String search) {
		
		
		List<Wallpaper> ret = new ArrayList<>();
		URLToolsClient c = URLTools.newClient();
		Map<String,String> m = new HashMap<>();
		m.put("Authorization","Client-ID "+getString("CLIENTID"));
		
		try {
			
			String query=search.trim().replaceAll(" ", " AND ");
			String uri = "https://api.imgur.com/3/gallery/search/"+getString("SORT").toLowerCase()+"/"+getString("WINDOW").toLowerCase()+"/?q="+URLEncoder.encode(query,MTGConstants.DEFAULT_ENCODING)+"&mature=true";
			String s= c.doGet(uri, m);
			logger.debug(uri);
			logger.trace(s);
			
			URLTools.toJson(s).getAsJsonObject().get("data").getAsJsonArray().forEach(je->{
				
				String defaultTitle =je.getAsJsonObject().get(TITLE_TAG).getAsString();
				
				if(je.getAsJsonObject().get(IMAGES_TAG)!=null)
				{
					je.getAsJsonObject().get(IMAGES_TAG).getAsJsonArray().forEach(im->{
						Wallpaper w = new Wallpaper();
						
						if(!im.getAsJsonObject().get(TITLE_TAG).isJsonNull())
							w.setName(im.getAsJsonObject().get(TITLE_TAG).getAsString());
						else
							w.setName(defaultTitle);
						
						w.setUrl(URI.create(im.getAsJsonObject().get("link").getAsString()));
						w.setFormat(FilenameUtils.getExtension(String.valueOf(w.getUrl())));
						ret.add(w);
						notify(w);
					});
				}
				else
				{
					Wallpaper w = new Wallpaper();
							w.setName(defaultTitle);
							w.setUrl(URI.create(je.getAsJsonObject().get("link").getAsString()));
							w.setFormat(FilenameUtils.getExtension(String.valueOf(w.getUrl())));
					
					ret.add(w);
					notify(w);
				}
				
				
				
				
			});
		} catch (IOException e) {
			logger.error(e);
		}
		
		return ret;

	}


	@Override
	public String getName() {
		return "Imgur";
	}

	@Override
	public void initDefault() {
		setProperty("CLIENTID", "");
		setProperty("SORT", "time");
		setProperty("WINDOW", "all");
	}
}

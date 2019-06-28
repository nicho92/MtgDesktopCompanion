package org.magic.api.wallpaper.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.Wallpaper;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;

import com.google.gson.JsonObject;

public class DeviantArtWallpaperProvider extends AbstractWallpaperProvider {

	
	RequestBuilder build;
	
	@Override
	public List<Wallpaper> search(String search) {
		
		List<Wallpaper> list = new ArrayList<>();
		
		
		try {
			build = RequestBuilder.build();
		    String bToken = build.setClient(URLTools.newClient())
								   .method(METHOD.GET)
								   .url("https://www.deviantart.com/oauth2/token")
								   .addContent("grant_type", "client_credentials")
								   .addContent("client_id", getString("CLIENT_ID"))
								   .addContent("client_secret", getString("CLIENT_SECRET"))
								   .toJson().getAsJsonObject().get("access_token").getAsString();
			
		    
		    
		    
		    JsonObject ret = build.clean()
		    				  .method(METHOD.GET)
		    				  .url("https://www.deviantart.com/api/v1/oauth2/browse/newest")
		    				  .addContent("q", search)
		    				  .addContent("limit", "120")
		    				  .addContent("access_token", bToken)
		    				  .toJson().getAsJsonObject();
		    
		    
		    
		    ret.get("results").getAsJsonArray().forEach(el->{
		    	
		    	logger.trace(el);
		    	Wallpaper p = new Wallpaper();
		    	p.setFormat("png");
		    	p.setName(el.getAsJsonObject().get("title").getAsString());
		    	try {
					p.setUrl(new URI(el.getAsJsonObject().get("content").getAsJsonObject().get("src").getAsString()));
				} catch (URISyntaxException e) {
					logger.error(e);
				}
		    	
		    	
		    	list.add(p);
		    	
		    });
			
		} catch (Exception e) {
			logger.error(e);
		}
		
		
		return list;
	}

	@Override
	public String getName() {
		return "DeviantArt";
	}

	
	@Override
	public void initDefault() {
		setProperty("CLIENT_ID", "");
		setProperty("CLIENT_SECRET", "");
	}

}

package org.magic.api.wallpaper.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGWallpaper;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;

import com.google.gson.JsonObject;

public class DeviantArtWallpaperProvider extends AbstractWallpaperProvider {


	private static final String LIMIT = "LIMIT";
	private static final String CLIENT_ID="CLIENT_ID";
	private static final String BASE_URL = "https://www.deviantart.com";
	private RequestBuilder build;
	private String bToken;

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public List<MTGWallpaper> search(String search) {

		List<MTGWallpaper> list = new ArrayList<>();
		try {

			if(getString(CLIENT_ID).isEmpty())
				{
					logger.error("please fill CLIENT_ID && CLIENT_SECRET attributs in config panel");
					return list;
				}



			build = RequestBuilder.build();
		    bToken = build.setClient(URLTools.newClient())
								   .get()
								   .url(BASE_URL+"/oauth2/token")
								   .addContent("grant_type", "client_credentials")
								   .addContent("client_id", getString(CLIENT_ID))
								   .addContent("client_secret", getString("CLIENT_SECRET"))
								   .toJson().getAsJsonObject().get("access_token").getAsString();

		    var offset = 0;
		    var ret= readOffset(offset,search);
				    while(ret.get("has_more").getAsBoolean())
				    {
					    ret.get("results").getAsJsonArray().forEach(el->{
					    	try {
					    		var p = new MTGWallpaper();
					    		p.setFormat("png");
					    		p.setName(el.getAsJsonObject().get("title").getAsString());
					    		p.setUrl(new URI(el.getAsJsonObject().get("content").getAsJsonObject().get("src").getAsString()));
					    		list.add(p);
							} catch (Exception e) {
								logger.error("Error for {}",el.getAsJsonObject().get("title"),e);
							}
					    });

					    if(list.size()>=getInt(LIMIT))
			    			break;

					    ret = readOffset(ret.get("next_offset").getAsInt(), search);
				    }

			} catch (Exception e) {
				logger.error("error",e);
			}

		return list;
	}

	private JsonObject readOffset(int offset,String search) {
		return  build.clean()
				  .get()
				  .url(BASE_URL+"/api/v1/oauth2/browse/newest")
				  .addContent("q", search)
				  .addContent("limit", getString(LIMIT))
				  .addContent("offset", String.valueOf(offset))
				  .addContent("mature_content", getString("MATURE"))
				  .addContent("access_token", bToken)
				  .toJson().getAsJsonObject();
	}

	@Override
	public String getName() {
		return "DeviantArt";
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of(CLIENT_ID, "",
								"CLIENT_SECRET", "",
								"MATURE","false",
								LIMIT,"50");
	}

}

package org.magic.api.wallpaper.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGWallpaper;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;

import com.google.gson.JsonObject;

public class DeviantArtWallpaperProvider extends AbstractWallpaperProvider {


	private static final String LIMIT = "LIMIT";
	private static final String BASE_URL = "https://www.deviantart.com";
	private RequestBuilder build;
	private String bToken;

	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("CLIENT_ID","CLIENT_SECRET");
	}
	
	@Override
	public List<MTGWallpaper> search(String search) {

		List<MTGWallpaper> list = new ArrayList<>();
		try {

			if(getAuthenticator()==null)
				{
					logger.error("please fill CLIENT_ID && CLIENT_SECRET attributs in config panel");
					return list;
				}

			build = RequestBuilder.build();
		    bToken = build.setClient(URLTools.newClient())
								   .get()
								   .url(BASE_URL+"/oauth2/token")
								   .addContent("grant_type", "client_credentials")
								   .addContent("client_id", getAuthenticator().get("CLIENT_ID"))
								   .addContent("client_secret", getAuthenticator().get("CLIENT_SECRET"))
								   .toJson().getAsJsonObject().get("access_token").getAsString();

		  logger.debug("Auth with {}", bToken);
		    
		    var offset = 0;
		    var ret= readOffset(offset,search);
				    while(!ret.get("results").getAsJsonArray().isEmpty())
				    {
					    ret.get("results").getAsJsonArray().forEach(el->{
					    	try {
					    		
					    		if(el.getAsJsonObject().get("content")!=null)
					    		{
					    		
					    		var p = new MTGWallpaper();
					    		p.setFormat("png");
					    		p.setName(el.getAsJsonObject().get("title").getAsString());
					    		p.setUrl(new URI(el.getAsJsonObject().get("content").getAsJsonObject().get("src").getAsString()));
					    		p.setUrlThumb(new URI(el.getAsJsonObject().get("thumbs").getAsJsonArray().get(0).getAsJsonObject().get("src").getAsString()));
					    		p.setPublishDate(new Date(el.getAsJsonObject().get("published_time").getAsLong()*1000));
					    		p.setProvider(getName());
					    		p.setMature(el.getAsJsonObject().get("is_mature").getAsBoolean());
					    		
					    		if(el.getAsJsonObject().get("author").getAsJsonObject().get("username")!=null)
					    			p.setAuthor(el.getAsJsonObject().get("author").getAsJsonObject().get("username").getAsString());
					    		
					    		  if(list.size()<getInt(LIMIT))
					    			  list.add(p);
					    		}
							} catch (Exception e) {
								logger.error("Error for {} . error : {}",el,e.getMessage());
							}
					    });

					    if(list.size()>=getInt(LIMIT) || ret.get("next_offset").isJsonNull())
					    	break;
					    
					    ret = readOffset(ret.get("next_offset").getAsInt(), search);
				    }
				   
			} catch (Exception e) {
				logger.error("error",e);
			}
		
		if(getBoolean("DATE_UPDATE_ORDER"))
			Collections.sort(list,Collections.reverseOrder());
		
		
		logger.info("{} return {} results", getName(), list.size());
		
		return list;
	}

	private JsonObject readOffset(int offset,String search) {
		var obj=  build.clean()
				  .get()
				  .url(BASE_URL+"/api/v1/oauth2/browse/home")
				  .addContent("q", search)
				  .addContent("limit", "50")
				  .addContent("offset", String.valueOf(offset))
				  .addContent("mature_content", getString("MATURE"))
				  .addContent("access_token", bToken)
				  .toJson().getAsJsonObject();
		  logger.debug("ret = {} ",obj);
		
		return obj;
		
		
	}

	@Override
	public String getName() {
		return "DeviantArt";
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("MATURE",MTGProperty.newBooleanProperty(FALSE, "set to true if you want to return mature content"),
								LIMIT,MTGProperty.newIntegerProperty("25", "Max results to return", 1, -1),
								"DATE_UPDATE_ORDER",MTGProperty.newBooleanProperty("true", "ordering results by published date (desc)"));
	}

}

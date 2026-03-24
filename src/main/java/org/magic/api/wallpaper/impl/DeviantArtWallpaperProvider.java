package org.magic.api.wallpaper.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.select.Elements;
import org.magic.api.beans.MTGWallpaper;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.services.MTGConstants;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

import com.google.gson.JsonObject;

public class DeviantArtWallpaperProvider extends AbstractWallpaperProvider {


	private static final String LIMIT = "LIMIT";
	private static final String BASE_URL = "https://www.deviantart.com";
	
	private static final String BROWSE_ENDPOINT=BASE_URL+"/api/v1/oauth2/browse/home";
	private static final String TOKEN_ENDPOINT=BASE_URL+"/oauth2/token";

	private String bToken;
	private MTGHttpClient client;
	private HashMap<String, String> maps;

	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("CLIENT_ID","CLIENT_SECRET","LOGIN","PASSWORD");
	}
	

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("MATURE",MTGProperty.newBooleanProperty(FALSE, "set to true if you want to return mature content"),
						      LIMIT,MTGProperty.newIntegerProperty("25", "Max results to return", 1, -1),
							  "DATE_UPDATE_ORDER",MTGProperty.newBooleanProperty("true", "ordering results by published date (desc)"),
							  "GRANT_TYPE", new MTGProperty("CLIENT", "Grant type for API Access. CODE need user and password, CLIENT needs client_id and client_secret ", "CODE","CLIENT"));
	}

	
	
	public DeviantArtWallpaperProvider() {
		client = URLTools.newClient();
		maps = new HashMap<>();
	};
	
	
	@Override
	public List<MTGWallpaper> search(String search) {
		
		if(getString("GRANT_TYPE").equals("CLIENT"))
			return clientSearch(search);
		else
			return codeSearch(search);
			
		
	}
	
public List<MTGWallpaper> clientSearch(String search) {
		
		var list = new ArrayList<MTGWallpaper>();
		try 
		{

			if(getAuthenticator()==null){
					logger.error("please fill CLIENT_ID && CLIENT_SECRET attributs in config panel");
					return list;
			}
		    initToken();
	    
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
		
		return returnList(list);
	}
	
	
	public List<MTGWallpaper> codeSearch(String s) 
	{
		var list = new ArrayList<MTGWallpaper>();
		
		
		if(getAuthenticator()==null){
			logger.error("please fill LOGIN && PASSWORD attributs in config panel");
			return list;
	}
		
		if(maps.isEmpty())
			authenticatedClient();
		
		
		if(maps.get("csrf_token")==null)
		{
			maps.clear();
			return returnList(list);
		}
		
		while(list.size()<getInt(LIMIT))
		{
		
		
		var jobj = RequestBuilder.build().get().setClient(client)
								.url(BASE_URL+"/_puppy/dabrowse/search/deviations")
								.addContent("q", s)
								.addContent("cursor", maps.get("cursor"))
								.addContent("da_minor_version", "20230710")
								.addContent("csrf_token",maps.get("csrf_token")).toJson().getAsJsonObject();
		
		logger.debug("ret {}",jobj);
		
		if(jobj.get("error")!=null)
		{
			maps.clear();
			logger.error(jobj.get("errorDescription").getAsString());
			return returnList(list);
		}
		
		
		maps.put("cursor",jobj.get("nextCursor").getAsString());
		
		for(var e : jobj.get("deviations").getAsJsonArray())
		{
			
			if(e.getAsJsonObject().get("type").getAsString().equals("image")) { 
			
			var wall = new MTGWallpaper();
				 wall.setName(e.getAsJsonObject().get("title").getAsString());
				 wall.setMature(e.getAsJsonObject().get("isMature").getAsBoolean());
				 wall.setPublishDate(UITools.parseDate(e.getAsJsonObject().get("publishedTime").getAsString(),"yyyy-MM-dd'T'HH:mm:ssZ"));
				 wall.setProvider(getName());
				 wall.setFormat(e.getAsJsonObject().get("filetype").getAsString());
				 wall.setAuthor(e.getAsJsonObject().get("author").getAsJsonObject().get("username").getAsString());

				 var objMedia = e.getAsJsonObject().get("media").getAsJsonObject();
				 wall.setUrl(createMedia(objMedia,false));
				 wall.setUrlThumb(createMedia(objMedia,true));
				 list.add(wall);
			}
		}
		}
		
		maps.put("cursor", null);
		
		return returnList(list);
	}
	
	
	private List<MTGWallpaper> returnList(ArrayList<MTGWallpaper> list) {
		if(getBoolean("DATE_UPDATE_ORDER") && !list.isEmpty())
			Collections.sort(list,Collections.reverseOrder());
		
		logger.info("{} return {} results", getName(), list.size());
		
		return list;
	}

	private URI createMedia(JsonObject objMedia, boolean b) {
		
		var baseUri = objMedia.get("baseUri").getAsString();
		var prettyName = objMedia.get("prettyName").getAsString();
		var token = "";
		try {
			token = objMedia.get("token").getAsJsonArray().get(0).getAsString();
		}
		catch(Exception _)
		{
			logger.warn("no token for {}",prettyName);
		}
		
		
		var types = objMedia.get("types").getAsJsonArray();
		
		var c = "";
		
		if(b)
		{
			c= types.get(0).getAsJsonObject().get("c").getAsString().replace("<prettyName>", prettyName);
		}
		else
		{
			for(var t : types)
			{
				if(t.getAsJsonObject().get("t").getAsString().equals("fullview") && !b)
				{
						if(t.getAsJsonObject().get("c")!=null)
							c = t.getAsJsonObject().get("c").getAsString().replace("<prettyName>", prettyName);
				
						break;
				}
			}
		}
		return URI.create(baseUri+c+(!token.isEmpty() ? "?token="+token:""));
	}

	private String extractCsrfToken(Elements el)
	{
		var m = Pattern.compile("window.__CSRF_TOKEN__ = '(.*?)';").matcher(el.html());
		
		if(m.find())
			return m.group(1);

		logger.warn("no CSRF found ! : {}", el.select("div.content p").text());
		return null;
	}
	
	private void authenticatedClient()  
	{
		try {
			RequestBuilder.build().get().setClient(client).url(BASE_URL+"/users/login").addContent("client_id", getAuthenticator().get("CLIENT_ID"))
															 .addContent("redirect_uri", MTGConstants.MTG_DESKTOP_WEBSITE)
															 .addContent("referer", BASE_URL)
															 .addContent("response_type", "code").toHtml().select("input[type=hidden]").forEach(el->maps.put(el.attr("name"), el.attr("value")));
		
		logger.debug("Step 0 done.  init data maps {}",maps);
		
		if(maps.containsKey("challenge") && !maps.get("challenge").equals("0"))
			logger.error("Login requires solving a CAPTCHA");
			
		var bstep1 = RequestBuilder.build().post().setClient(client).url(BASE_URL+"/_sisu/do/step2");
					maps.entrySet().forEach(e->bstep1.addContent(e.getKey(),e.getValue()));
					bstep1.addContent("username", getAuthenticator().getLogin());
					bstep1.addContent("remember", "on");
					bstep1.toHtml().select("input[type=hidden]").forEach(el->maps.put(el.attr("name"), el.attr("value")));
		
		logger.debug("Step 1 done.  completing data maps {}. Waiting 2 sec",maps);
		}
		catch(Exception ex)
		{
			logger.error("error at step 1 : {}",ex.getMessage());
		}
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException _) {
			Thread.currentThread().interrupt();
		}
		
		try 
		{
		var bstep2 = RequestBuilder.build().post().setClient(client).url(BASE_URL+"/_sisu/do/signin")
				.addContent("remember", "on")
				.addContent("password", getAuthenticator().getPassword());
		
		maps.entrySet().forEach(e->bstep2.addContent(e.getKey(),e.getValue()));
		
		var b = bstep2.toHtml();
		maps.put("csrf_token", extractCsrfToken(b.getAllElements()));
		
		logger.debug("Step 2 done. with csrf {}",maps.get("csrf_token"));
		}
		catch(Exception ex)
		{
			logger.error("error at step 2 : {}",ex.getMessage());
		}
	}
		
	
	

	private void initToken() {
		 var obj = RequestBuilder.build()
				   .setClient(client)
				   .get()
				   .url(TOKEN_ENDPOINT)
				   .addContent("grant_type", "client_credentials")
				   .addContent("client_id", getAuthenticator().get("CLIENT_ID"))
				   .addContent("client_secret", getAuthenticator().get("CLIENT_SECRET"))
				   .toJson().getAsJsonObject();
		 		
		 		logger.debug("ret = {} ",obj);
		 		bToken = obj.get("access_token").getAsString();
	}

	private JsonObject readOffset(int offset,String search) {
		var obj= RequestBuilder.build()
				  .setClient(client)
				  .get()
				  .url(BROWSE_ENDPOINT)
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

}

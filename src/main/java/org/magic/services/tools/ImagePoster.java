package org.magic.services.tools;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.logging.log4j.Logger;
import org.magic.services.logging.MTGLogger;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;

import com.google.gson.JsonObject;

public class ImagePoster {

	private Logger logger = MTGLogger.getLogger(ImagePoster.class);
	private int expirationDay = 0;
	private static final String BASE_URL = "https://postimages.org/";
	MTGHttpClient client = URLTools.newClient();
	
	
	public void setExpirationDay(int expirationDay) {
		this.expirationDay = expirationDay;
	}
	
	public boolean isProxified(String url)
	{
		return url.contains("postimg");
	}
	
	public String upload(File f) throws IOException {
	       
	    var header = Map.of(URLTools.ORIGIN, BASE_URL,
		    		           URLTools.REFERER, BASE_URL + "/web",
					   URLTools.ACCEPT, URLTools.HEADER_JSON,
					   URLTools.ACCEPT_ENCODING, "gzip, deflate, br, zstd",
					   URLTools.ACCEPT_LANGUAGE,"fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7\r\n",
					   "priority", "u=1, i",
					   "Cache-Control", "no-cache",
					   "x-requested-with", "XMLHttpRequest");
	    
	    var ret = client.doPost(BASE_URL + "/json", MultipartEntityBuilder.create().addPart("file", new FileBody(f))
		    												       .addTextBody("gallery","")
		    												       .addTextBody("expire",String.valueOf(expirationDay))
		    												       .addTextBody("numfiles","1")
		    												       .addTextBody("upload_session",generateSessionId())
		    												       .build(), header);
	    
	    
	    return directLink(URLTools.toJson(ret.getEntity().getContent()).getAsJsonObject());
	}
	
	
	private String generateSessionId() {
	   return new Date().getTime() + Double.toString(CryptoUtils.randomDouble(Double.MAX_VALUE)).substring(1);
	}

	public String upload(String url) throws IOException {
		
		

		var jsonRet = RequestBuilder.build().setClient(client).post().url(BASE_URL + "/json")

				.addHeader(URLTools.ORIGIN, BASE_URL).addHeader(URLTools.REFERER, BASE_URL + "/web")
				.addHeader(URLTools.ACCEPT, URLTools.HEADER_JSON)
				.addHeader(URLTools.ACCEPT_ENCODING, "gzip, deflate, br, zstd").addHeader("priority", "u=1, i")
				.addHeader("Cache-Control", "no-cache").addHeaders(URLTools.createSecHeaders())
				.addHeader("x-requested-with", "XMLHttpRequest")
				.addContent("gallery", "").addContent("optsize", "0")
				.addContent("expire", String.valueOf(expirationDay))
				.addContent("url", url)
				.addContent("numfiles", "1")
				.addContent("upload_session",generateSessionId())
				.toJson().getAsJsonObject();

		logger.debug("upload result : {}", jsonRet);

		return directLink(jsonRet);
	}

	private String directLink(JsonObject jsonRet) throws IOException {
	    
	    var url = jsonRet.get("url").getAsString();
	    try {
		return RequestBuilder.build().setClient(client).get().url(url).toHtml().getElementById("direct").attr("value");
	} catch (Exception _) {
		logger.error("error to upload {} : {}", url, jsonRet);
		throw new IOException("Error at upload");
	}

	}

}

package org.magic.services.tools;

import java.io.IOException;
import java.util.Date;

import org.apache.logging.log4j.Logger;
import org.magic.services.logging.MTGLogger;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;

public class ImagePoster {
	
	private static Logger logger = MTGLogger.getLogger(ImagePoster.class);
	
	
	public static String upload(String wallUrl) throws IOException {
		var baseUrl = "https://postimages.org/";
		var client = URLTools.newClient();
		var jsonRet = RequestBuilder.build().setClient(client).post().url(baseUrl+"/json/rr")
					.addHeader("x-requested-with","XMLHttpRequest")
					.addHeader(URLTools.ORIGIN, baseUrl)
					.addHeader(URLTools.REFERER, baseUrl+"/web")
					.addHeader(URLTools.ACCEPT, URLTools.HEADER_JSON)
					.addHeader("Cache-Control", "no-cache")
					.addContent("upload_session", new Date().getTime() + Double.toString(Math.random()).substring(1))
					.addContent("optsize", "0")
					.addContent("expire", "1")
					.addContent("url", wallUrl)
					.addContent("numfiles", "1")
					.addContent("gallery", "").toJson().getAsJsonObject();
		
		logger.debug("upload result : {}",  jsonRet);
		
		if(jsonRet.get("status").getAsString().equals("OK"))
		{
			return RequestBuilder.build().setClient(client).post().url(jsonRet.get("url").getAsString()).toHtml().getElementById("code_direct").attr("value");
		}
		else
		{
			logger.error("error {}", jsonRet);
			throw new IOException("Error at upload");
		}
	}

}

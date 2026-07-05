package org.magic.services.tools;

import java.io.IOException;
import java.util.Date;
import org.apache.logging.log4j.Logger;
import org.magic.services.logging.MTGLogger;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;

public class ImagePoster {

	private Logger logger = MTGLogger.getLogger(ImagePoster.class);
	private int expirationDay = 0;

	public void setExpirationDay(int expirationDay) {
		this.expirationDay = expirationDay;
	}
	
	public boolean isProxified(String url)
	{
		return url.contains("postimg");
	}
	
	
	public String upload(String url) throws IOException {
		var baseUrl = "https://postimages.org/";
		var client = URLTools.newClient();

		var jsonRet = RequestBuilder.build().setClient(client).post().url(baseUrl + "/json")

				.addHeader(URLTools.ORIGIN, baseUrl).addHeader(URLTools.REFERER, baseUrl + "/web")
				.addHeader(URLTools.ACCEPT, URLTools.HEADER_JSON)
				.addHeader(URLTools.ACCEPT_ENCODING, "gzip, deflate, br, zstd").addHeader("priority", "u=1, i")
				.addHeader("Cache-Control", "no-cache").addHeaders(URLTools.createSecHeaders())
				.addHeader("x-requested-with", "XMLHttpRequest")
				.addContent("gallery", "").addContent("optsize", "0")
				.addContent("expire", String.valueOf(expirationDay)).addContent("url", url).addContent("numfiles", "1")
				.addContent("upload_session",
						new Date().getTime() + Double.toString(CryptoUtils.randomDouble(Double.MAX_VALUE)).substring(1))
				.toJson().getAsJsonObject();

		logger.debug("upload result : {}", jsonRet);

		try {
			return RequestBuilder.build().setClient(client).post().url(jsonRet.get("url").getAsString()).toHtml()
					.getElementById("direct").attr("value");
		} catch (Exception _) {
			logger.error("error to upload {} : {}", url, jsonRet);
			throw new IOException("Error at upload");
		}

	}

}

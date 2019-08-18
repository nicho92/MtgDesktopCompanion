package org.magic.api.beans;

import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;

public class MTGDocumentation {

	private FORMAT_NOTIFICATION contentType;
	private URL url;
	
	
	public MTGDocumentation(URL url,FORMAT_NOTIFICATION contentType)
	{
		this.url=url;
		this.contentType=contentType;
	}
	
	public FORMAT_NOTIFICATION getContentType() {
		return contentType;
	}
	
	public void setContentType(FORMAT_NOTIFICATION contentType) {
		this.contentType = contentType;
	}
	
	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}

	
		
	
	
}

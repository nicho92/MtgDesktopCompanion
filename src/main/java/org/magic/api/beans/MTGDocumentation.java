package org.magic.api.beans;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.magic.api.beans.technical.MTGNotification.FORMAT_NOTIFICATION;

public class MTGDocumentation {

	private FORMAT_NOTIFICATION contentType;
	private URL url;


	public MTGDocumentation(URL url,FORMAT_NOTIFICATION contentType)
	{
		this.url=url;
		this.contentType=contentType;
	}

	public MTGDocumentation(String uri, FORMAT_NOTIFICATION fn) {
		try {
			this.url=URI.create(uri).toURL();
		} catch (MalformedURLException e) {
			//do nothing
		}
		this.contentType=fn;
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

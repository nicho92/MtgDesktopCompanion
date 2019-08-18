package org.magic.api.beans;

import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;

public class MTGDocumentation {

	private FORMAT_NOTIFICATION contentType;
	private String content;
	private URL url;
	private boolean markdownFormat;
	
	public MTGDocumentation(String content,FORMAT_NOTIFICATION contentType)
	{
		this.content=content;
		this.contentType=contentType;
	}
	
	public MTGDocumentation(URL url,FORMAT_NOTIFICATION contentType)
	{
		this.url=url;
		this.contentType=contentType;
	}
	
	public boolean isNeedDownload() {
		return StringUtils.isEmpty(content);
	}
	
	public FORMAT_NOTIFICATION getContentType() {
		return contentType;
	}
	public void setContentType(FORMAT_NOTIFICATION contentType) {
		this.contentType = contentType;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}

	public boolean isMarkdownFormat() {
		return markdownFormat;
	}

	public void setMarkdownFormat(boolean markdownFormat) {
		this.markdownFormat = markdownFormat;
	}
	
		
	
	
}

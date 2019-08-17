package org.magic.api.beans;

import java.net.URL;

public class Documentation {

	private String contentType;
	private String content;
	private URL url;
	private boolean needDownload;
	
	public Documentation(String content,String contentType)
	{
		this.content=content;
		this.contentType=contentType;
		
		needDownload=false;
	}
	
	public Documentation(URL url,String contentType)
	{
		this.url=url;
		needDownload = true;
		this.contentType=contentType;
	}
	
	public boolean isNeedDownload() {
		return needDownload;
	}
	
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
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
	
	
	
}

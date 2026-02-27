package org.magic.api.beans;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MTGWallpaper implements Comparable<MTGWallpaper> {

	private transient BufferedImage picture;
	private URI url;
	private URI urlThumb;
	private String name;
	private String format;	
	private String author;
	private Date publishDate;
	private String provider;
	private boolean mature;
	private List<String> tags;
	private transient Map<String,String> headers;
	
	public MTGWallpaper() {
		tags = new ArrayList<>();
		headers = new HashMap<>();
	}
	
	public void addHeader(String k, String v)
	{
		headers.put(k, v);
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	public List<String> getTags() {
		return tags;
	}
	
	public boolean isMature() {
		return mature;
	}

	public void setMature(boolean mature) {
		this.mature = mature;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	public String getProvider() {
		return provider;
	}
	
	
	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}
	
	public Date getPublishDate() {
		return publishDate;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public BufferedImage getPicture() {
		return picture;
	}


	public void setPicture(BufferedImage picture) {
		this.picture = picture;
	}

	public URI getUrlThumb() {
		return urlThumb;
	}
	
	public void setUrlThumb(URI urlThumb) {
		this.urlThumb = urlThumb;
	}
	
	
	public URI getUrl() {
		return url;
	}

	public void setUrl(URI url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String extension) {
		this.format = extension;

	}

	@Override
	public int compareTo(MTGWallpaper o) {
		if(getPublishDate()!=null && o!=null && o.getPublishDate()!=null)
			return getPublishDate().compareTo(o.getPublishDate());
		
		return -1;
	}

}

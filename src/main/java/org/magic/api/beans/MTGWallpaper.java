package org.magic.api.beans;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.net.URI;

public class MTGWallpaper {

	private transient BufferedImage picture;
	private URI url;
	private URI urlThumb;
	private String name;
	private String format;	
	private String author;
	
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
		this.name = name;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String extension) {
		this.format = extension;

	}

	public Dimension getDimension() {
		try {
			return new Dimension(getPicture().getWidth(), getPicture().getHeight());
		} catch (Exception _) {
			return new Dimension(0, 0);
		}
	}

}

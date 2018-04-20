package org.magic.api.beans;

import java.awt.Image;
import java.net.URL;

public class MTGStory {

	private URL url;
	private String title;
	private Image image;
	private String date;
	private String description;
	private String author;

	@Override
	public String toString() {
		return getTitle();
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Image getIcon() {
		return image;
	}

	public void setIcon(Image icone) {
		this.image = icone;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}

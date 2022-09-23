package org.magic.api.beans;

import java.net.URL;
import java.util.Date;

public class MagicNewsContent {

	private String title;
	private String content;
	private String author;
	private Date date;
	private URL link;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public URL getLink() {
		return link;
	}

	public void setLink(URL link) {
		this.link = link;
	}

	@Override
	public String toString() {
		return getTitle();
	}

}
